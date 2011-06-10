package info.pppc.pcom.system.container;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseListener;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.io.ObjectStreamTranslator;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.AssemblerProxy;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.internal.AbstractCreatorContext;
import info.pppc.pcom.system.container.internal.capability.AllocatorContext;
import info.pppc.pcom.system.container.internal.capability.ResourceBinding;
import info.pppc.pcom.system.container.internal.capability.ResourceContext;
import info.pppc.pcom.system.container.internal.component.FactoryContext;
import info.pppc.pcom.system.container.internal.component.InstanceContext;
import info.pppc.pcom.system.container.internal.component.InstanceHistory;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IAllocator;
import info.pppc.pcom.system.model.component.IFactory;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class implements the PCOM container. The container holds references to 
 * all factories, instances, allocators, resources and applications that are
 * currently installed/running on the container. It uses assemblers to configure
 * applications.
 * 
 * @author Mac
 */
public class Container implements IContainer {

	/**
	 * Initialize the abbreviations for classes used by the container and the
	 * configuration algorithms.
	 */
	static {
		ObjectStreamTranslator.register(Assembly.class.getName(), Assembly.ABBREVIATION);
		ObjectStreamTranslator.register(AssemblyPointer.class.getName(), AssemblyPointer.ABBREVIATION);
		ObjectStreamTranslator.register(AssemblyState.class.getName(), AssemblyState.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceSetup.class.getName(), InstanceSetup.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceState.class.getName(), InstanceState.ABBREVIATION);
		ObjectStreamTranslator.register(Contract.class.getName(), Contract.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceHistory.class.getName(), InstanceHistory.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceCheckpoint.class.getName(), InstanceCheckpoint.ABBREVIATION);
	}
	
	/**
	 * The single instance of the container in this java virtual machine.
	 */
	private static Container instance;
	
	/**
	 * The context objects of factories hashed by identifier.
	 */
	private Hashtable factories = new Hashtable();
	
	/**
	 * The context objects of instances hashed by identifier.
	 */
	private Hashtable instances = new Hashtable();
	
	/**
	 * The context objects of allocators hashed by identifier.
	 */
	private Hashtable allocators = new Hashtable();
	
	/**
	 * The context objects of resources hashed by identifier.
	 */
	private Hashtable resources = new Hashtable();

	/**
	 * The assembler strategy that is used to retrieve the assembler
	 * used to configure the resources for factories and instances.
	 */
	private IContainerStrategy strategy = null;
	
	/**
	 * The lease registry used to create and manage leases.
	 */
	private LeaseRegistry registry = LeaseRegistry.getInstance();
	
	/**
	 * A flag that indicates whether the container is currently recomputing
	 * the resource distribution among factories and allocators.
	 */
	private boolean recomputing = false;
	
	/**
	 * A flag that indicates whether the container received an update resources
	 * call during the last recomputation of resources rounds.  
	 */
	private boolean reiterating = false;
	
	/**
	 * Creates a new container and registers it as remote well known service
	 * at the invocation broker passed to this constructor.
	 * 
	 * @param broker The invocation broker used by the new container.
	 */
	protected Container(InvocationBroker broker) {
		ContainerSkeleton skeleton = new ContainerSkeleton();
		skeleton.setImplementation(this);
		final ObjectRegistry registry = broker.getObjectRegistry();
		registry.registerObject(IContainer.CONTAINER_ID, skeleton, this);
		broker.addBrokerListener(InvocationBroker.EVENT_BROKER_SHUTDOWN, new IListener() {
			public void handleEvent(Event event) {
				Logging.debug(getClass(), "Removing container due to broker shutdown.");
				registry.removeObject(IContainer.CONTAINER_ID);
				Vector ctxs = new Vector();
				synchronized (factories) {
					Enumeration e = factories.elements();
					while (e.hasMoreElements()) {
						ctxs.addElement(e.nextElement());
					}
				}
				while (! ctxs.isEmpty()) {
					FactoryContext ctx = (FactoryContext)ctxs.elementAt(0);
					Logging.debug(getClass(), "Removing factory " + ctx.getIdentifier() 
							+ " due to broker shutdown.");
					ctxs.removeElementAt(0);
					removeFactory(ctx.getIdentifier());
				}
				synchronized (allocators) {
					Enumeration e = allocators.elements();
					while (e.hasMoreElements()) {
						ctxs.addElement(e.nextElement());
					}
				}
				while (! ctxs.isEmpty()) {
					AllocatorContext ctx = (AllocatorContext)ctxs.elementAt(0);
					Logging.debug(getClass(), "Removing allocator " + ctx.getIdentifier() 
							+ " due to broker shutdown.");	
					ctxs.removeElementAt(0);
					removeAllocator(ctx.getIdentifier());
				}
				instance = null;
			}
		});
		
	}
	
	/**
	 * Creates and returns the single instance of the container in this virtual
	 * machine.
	 * 
	 * @return The single instance of the container in this virtual machine.
	 */
	public static Container getInstance() {
		if (instance == null) {
			instance = new Container(InvocationBroker.getInstance());
		}
		return instance;
	}
	
	/**
	 * Installs a new factory by creating a context object that is 
	 * registered under a certain id. The id under which the factory
	 * can be accessed will be returned. If the factory is already 
	 * installed, this method will simply return the object id of the
	 * factory.
	 * 
	 * @param factory The factory that needs to be installed.
	 * @return The object id under which the factory is accessible.
	 * @throws NullPointerException Thrown if the factory is null.
	 */
	public ObjectID addFactory(IFactory factory) {
		if (factory == null) throw new NullPointerException("Factory is null.");
		Logging.debug(getClass(), "Add factory called with " + factory.getName() + ".");
		synchronized (factories) {
			Enumeration e = factories.elements();
			while (e.hasMoreElements()) {
				FactoryContext context = (FactoryContext)e.nextElement();
				if (context.getFactory() == factory) {
					return context.getIdentifier();
				}
			}
			FactoryContext context = null;
			try {
				context = new FactoryContext(this, factory);
				context.startFactory();
				factories.put(context.getIdentifier(), context);
				Logging.debug(getClass(), "Factory " + context.getIdentifier() + " added.");
			} catch (Throwable t) {
				Logging.error(getClass(), "Could not install " + factory.getName() + ".", t);
				if (context != null) {
					context.stopFactory();	
				}
				return null;
			}
			updateResources();
			return context.getIdentifier();				
		}
	}
	
	/**
	 * Uninstalls the factory with the specified identifier and returns it.
	 * A call to this method will stop the factory and release all resources and
	 * instances that are currently bound.
	 * 
	 * @param factoryID The identifier of the factory to uninstall.
	 * @return The factory that has been removed by a call to this method or
	 * 	null if no factory has been removed.
	 * @throws NullPointerException Thrown if the factory id is null.
	 */
	public IFactory removeFactory(ObjectID factoryID) {
		if (factoryID == null) throw new NullPointerException("FactoryID is null.");
		Logging.debug(getClass(), "Remove factory called for " + factoryID + ".");
		// find factory and determine whether there are any existing removal threads
		FactoryContext factoryContext = null;
		synchronized (factories) {
			FactoryContext context = (FactoryContext)factories.get(factoryID);
			if (context == null) return null;
			if (context.getLock().stop()) {
				factoryContext = context;
			}
		}
		// if this thread has been assigned to remove the factory
		if (factoryContext != null) {
			// release instances
			Vector icontexts = new Vector();
			synchronized (instances) {
				Enumeration e = instances.elements();
				while (e.hasMoreElements()) {
					InstanceContext context = (InstanceContext)e.nextElement();
					if (context.getFactory() == factoryContext) {
						icontexts.addElement(context);
					}
				}
			}
			while (! icontexts.isEmpty()) {
				InstanceContext context = (InstanceContext)icontexts.elementAt(0);
				icontexts.removeElementAt(0);
				context.removeInstance();
			}
			// stop factory and release resources held by it
			factoryContext.stopFactory();			
			// remove factory context
			synchronized (factories) {
				factories.remove(factoryContext.getIdentifier());
			}
			Logging.debug(getClass(), "Factory " + factoryID + " removed.");
			return factoryContext.getFactory();
		} else {
			return null;
		}
	}
	
	/**
	 * Installs the specified allocator into this container and returns the
	 * object identifier under which the allocator will be accessible. If
	 * the allocator has been already installed, this method will simply 
	 * return its id.
	 * 
	 * @param allocator The allocator that should be installed.
	 * @return The object id under which the allocator has been installed.
	 * @throws NullPointerException Thrown if the allocator is null.
	 */
	public ObjectID addAllocator(IAllocator allocator) {
		if (allocator == null) throw new NullPointerException("Allocator is null.");
		Logging.debug(getClass(), "Add allocator called with " + allocator.getName() + ".");
		synchronized (allocators) {
				Enumeration e = allocators.elements();
				while (e.hasMoreElements()) {
					AllocatorContext context = (AllocatorContext)e.nextElement();
					if (context.getAllocator() == allocator) {
						return context.getIdentifier();
					}
				}
				AllocatorContext context = null;
				try {
					context = new AllocatorContext(this, allocator);
					context.startAllocator();
					allocators.put(context.getIdentifier(), context);
					Logging.debug(getClass(), "Allocator " + context.getIdentifier() + " added.");
				} catch (Throwable t) {
					Logging.error(getClass(), "Could not install " + allocator.getName() + ".", t);
					if (context != null) {
						context.stopAllocator();	
					}
					return null;
				}
				updateResources();
				return context.getIdentifier();				
		}
	}
	
	/**
	 * Removes the allocator that has been previously installed into this container
	 * under the specified identifier. A call to this method will release all
	 * resource assignments made by the allocator and for the allocator and it will
	 * stop the allocator.
	 * 
	 * @param allocatorID The identifier of the allocator that should be uninstalled.
	 * @return The allocator that has been removed or null if no such allocator exists.
	 * @throws NullPointerException Thrown if the allocator id is null.
	 */
	public IAllocator removeAllocator(ObjectID allocatorID) {
		if (allocatorID == null) throw new NullPointerException("AllocatorID is null.");
		Logging.debug(getClass(), "Remove allocator called for " + allocatorID + ".");
		// find allocator and determine whether there are any existing removal threads
		AllocatorContext allocatorContext = null;
		synchronized (allocators) {
			AllocatorContext context = (AllocatorContext)allocators.get(allocatorID);
			if (context == null) return null;
			if (context.getLock().stop()) {
				allocatorContext = context;
			}
		}
		// if this thread has been assigned to remove the allocator
		if (allocatorContext != null) {
			// release instances
			Vector rcontexts = new Vector();
			synchronized (resources) {
				Enumeration e = resources.elements();
				while (e.hasMoreElements()) {
					ResourceContext context = (ResourceContext)e.nextElement();
					if (context.getAllocator() == allocatorContext) {
						rcontexts.addElement(context);
					}
				}
			}
			while (! rcontexts.isEmpty()) {
				ResourceContext context = (ResourceContext)rcontexts.elementAt(0);
				rcontexts.removeElementAt(0);
				context.removeResource();
			}
			// stop allocator and release resources held by it
			allocatorContext.stopAllocator();			
			// remove allocator context
			synchronized (allocators) {
				allocators.remove(allocatorContext.getIdentifier());
			}
			Logging.debug(getClass(), "Allocator " + allocatorID + " removed.");
			return allocatorContext.getAllocator();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the strategy that the container will use to retrieve the assembler
	 * used to configure resource assignments for factories and allocators.
	 * 
	 * @param strategy The new strategy that the container will use from now
	 * 	on. If the strategy is null, the container will not configure any
	 * 	resources for allocators or factories.
	 */
	public synchronized void setStrategy(IContainerStrategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * Returns the lease registry that is globally used by the container to
	 * manage leases.
	 * 
	 * @return The lease registry that the container uses to manage leases.
	 */
	public LeaseRegistry getLeaseRegistry() {
		return registry;
	}
	
	
	/**
	 * This method is part of the predecessor interface. It is called to signal that an instance
	 * bound to an instance of this container has changed its provision. If the message is not
	 * outdated, this should change the contract of the bound instance and signal the change to
	 * the component implementation.
	 * 
	 * @param predecessorID The id of the instance that uses the instance whose contract has 
	 * 	changed.
	 * @param name The name of the successor that has caused the event.
	 * @param phase The phase used to detect outdated messages.
	 * @param provision The provision of the instance that changed its contract.
	 */
	public void changeInstance(ObjectID predecessorID, String name, int phase, Contract provision) {
		Logging.debug(getClass(), "Change instance has been called with predecessor " + predecessorID
				+ " phase " + phase + " provision " + provision + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(predecessorID);
		}
		if (icontext != null) {
			icontext.changeInstance(name, phase, provision);				
		} else {
			Logging.debug(getClass(), "Ignoring change due to missing instance " + predecessorID + ".");
		}
	}

	/**
	 * This method is part of the predecessor interface. It is called to signal that an instance
	 * bound to an instance of this container has performed a forceful shutdown. The instance
	 * needs to be replaced through adaptation. Note that if this method has been called, the
	 * instance has been removed already, thus if it was a stateful instance, all state is lost.
	 * 
	 * @param predecessorID The id of the instance that used the broken instance.
	 * @param name The name of the successor that has caused the event.
	 * @param phase The phase used to detect outdated messages.
	 */
	public void removeInstance(ObjectID predecessorID, String name, int phase) {
		Logging.debug(getClass(), "Remove instance has been called with predecessor " + predecessorID
				+ " phase " + phase + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(predecessorID);
		}
		if (icontext != null) {
			icontext.removeInstance(name, phase);				
		} else {
			Logging.debug(getClass(), "Ignoring change due to missing instance " + predecessorID + ".");
		}		
	}

	/**
	 * This method is part of the predecessor inteface. It is called to signal that an instance
	 * bound to an instance of this container wants the application to be stopped. 
	 * 
	 * @param predecessorID The id of the instance that is using the instance that called the
	 * 	method.
	 * @param name The name of the successor that has caused the event.
	 * @param phase The phase used to detect outdated messages.
	 */
	public void exitApplication(ObjectID predecessorID, String name, int phase) {
		Logging.debug(getClass(), "Exit application called with predecessor " + predecessorID + " phase " + phase + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(predecessorID);
		}
		if (icontext != null) {
			icontext.exitApplication(name, phase);				
		} else {
			Logging.debug(getClass(), "Ignoring change due to missing instance " + predecessorID + ".");
		}		
	}
	
	/**
	 * This method is part of the predecessor interface. It is called to signal that an instance
	 * bound to an instance of this container wants to save the application state.
	 * 
	 * @param predecessorID The id of the instance that is hosted on the container.
	 * @param name The name of the requesting instance.
	 * @param phase The phase to detect outdated messages.
	 */
	public void saveApplication(ObjectID predecessorID, String name, int phase) {
		Logging.debug(getClass(), "Save application called with predecessor " + predecessorID + " phase " + phase + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(predecessorID);
		}
		if (icontext != null) {
			icontext.saveApplication(name, phase);				
		} else {
			Logging.debug(getClass(), "Ignoring change due to missing instance " + predecessorID + ".");
		}		
		
	}

	/**
	 * This method is part of the successor interface. It called to start a new instance or 
	 * to adapt an existing instance to some new contract.  
	 * 
	 * @param setup The setup that describes the new instance or the adaptation of the 
	 * 	existing instance.
	 * @param phase The phase that is initiated through this call.
	 * @return The state of the instance after it has been started.
	 */
	public InstanceState startInstance(InstanceSetup setup, int phase) {
		Logging.debug(getClass(), "Start instance called (" + (setup.getAssembly().getElementID() == null?"new":"reuse") + ").");
		Assembly assemblerResult = setup.getAssembly();
		ObjectID creatorID = assemblerResult.getCreatorID();
		ObjectID instanceID = assemblerResult.getElementID();
		InstanceContext icontext = null;
		FactoryContext fcontext = null;
		synchronized (factories) {
			if (instanceID != null) {
				synchronized (instances) {
					InstanceContext context = (InstanceContext)instances.get(instanceID);
					if (context == null) {
						Logging.debug(getClass(), "Cannot find instance " + instanceID + ".");
						return new InstanceState();
					} 
					icontext = context;					
				}
			}
			FactoryContext context = (FactoryContext)factories.get(creatorID);
			if (context == null) {
				Logging.debug(getClass(), "Cannot find factory " + creatorID + ".");
				return new InstanceState();
			}
			if (! context.getLock().aquire()) {
				Logging.debug(getClass(), "Cannot aquire lock for factory " + creatorID + ".");
				return new InstanceState();
			}
			fcontext = context;
			if (icontext == null) {
				Logging.debug(getClass(), "Creating new instance for " + creatorID + ".");
				icontext = new InstanceContext(this, fcontext, setup);
				synchronized (instances) {
					instances.put(icontext.getIdentifier(), icontext);
				}
			}
		}			
		InstanceState state;
		try {
			state = icontext.startInstance(setup.getContract(), setup.getAssembly(), phase);	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not start instance for " + creatorID + ".", t);
			icontext.stopInstance();
			synchronized (instances) {
				instances.remove(icontext.getIdentifier());
			}
			state = new InstanceState();
		}
		fcontext.getLock().release();
		return state;
	}

	/**
	 * This method is part of the successor interface. It is called to pause an instance and
	 * to transfer the data of the instance into a specified assembler. 
	 * 
	 * @param successorID The id of the instance that needs to be paused.
	 * @param context The assembler context that can be used to store the data of the instance.
	 * @param phase The phase that is initiated through this call.
	 */
	public void pauseInstance(ObjectID successorID, AssemblyPointer context, int phase) {
		Logging.debug(getClass(), "Pause instance called.");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(successorID);
		}
		if (icontext != null) {
			icontext.pauseInstance(context, phase);
		} else {
			Logging.debug(getClass(), "Ignoring pause call due to missing instance " + successorID + ".");
		}
	}

	/**
	 * This method is part of the successor interface. It is called to stop an instance.
	 * 
	 * @param successorID The id of the instance that should be stopped.
	 */
	public void stopInstance(ObjectID successorID) {
		Logging.debug(getClass(), "Stop instance called with successor " + successorID + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(successorID);
		}
		if (icontext != null) {
			icontext.stopInstance();
			synchronized (instances) {
				instances.remove(icontext.getIdentifier());
			}
		} else {
			Logging.debug(getClass(), "Ignoring stop call due to missing instance " + successorID + ".");
		}
	}
	
	/**
	 * This method is part of the successor interface. It is called to load the state
	 * of an instance from a checkpoint.
	 * 
	 * @param successorID The id of the instance that should be loaded.
	 * @param checkpoint The checkpoint that contains the instance state.
	 */
	public void loadInstance(ObjectID successorID, InstanceCheckpoint checkpoint) {
		Logging.debug(getClass(), "Load instance called with successor " + successorID + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(successorID);
		}
		if (icontext != null) {
			icontext.loadInstance(checkpoint);
		}
	}
	
	/**
	 * This method is part of the successor interface. It is called to store the
	 * state of an instance into a checkpoint.
	 * 
	 * @param successorID The id of the instance that should be stopped.
	 * @return The instance checkpoint if successful, otherwise null.
	 */
	public InstanceCheckpoint storeInstance(ObjectID successorID) {
		Logging.debug(getClass(), "Store instance called with successor " + successorID + ".");
		InstanceContext icontext = null;
		synchronized (instances) {
			icontext = (InstanceContext)instances.get(successorID);
		}
		if (icontext != null) {
			return icontext.storeInstance();
		} else {
			return null;	
		}
	}
	
	/**
	 * Recomputes the assignments for factories and allocators using
	 * the assembler provided by the strategy.
	 */
	public void updateResources() {
		// determine whether we are currently recomputing the resources,
		// if so, tell the container to recompute them again, later on
		// if not, start recomputation.
		synchronized (this) {
			if (recomputing) {
				reiterating = true;
				return;
			} else {
				recomputing = true;
				reiterating = false;
			}
		}
		// begin the resource computation
		InvocationBroker.getInstance().performOperation(new IOperation() {
			public void perform(IMonitor monitor) throws Exception {
				compute: while (true) {
					Logging.debug(getClass(), "Updating resource assignments.");
					boolean abort = false; // a flag that indicate whether the process should be aborted
					if (strategy == null) return;
					ReferenceID assemberID = strategy.getAssemblerID();
					if (assemberID == null) return;
					AssemblerProxy assembler = new AssemblerProxy();
					assembler.setTargetID(assemberID);
					assembler.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
					// create a copy of the allocators that are present
					Vector creators = new Vector();
					synchronized (allocators) {
						Enumeration e = allocators.elements();
						while (e.hasMoreElements()) {
							creators.addElement(e.nextElement());
						}
					}
					synchronized (factories) {
						Enumeration e = factories.elements();
						while (e.hasMoreElements()) {
							creators.addElement(e.nextElement());
						}
					}
					// resolve the allocators open references
					for (int i = 0; i < creators.size(); i++) {
						AbstractCreatorContext creator = (AbstractCreatorContext)creators.elementAt(i);
						// omit resource allocators that are about to be removed
						if (! creator.getLock().aquire()) continue;
						ReferenceID applicationID = new ReferenceID(SystemID.SYSTEM, creator.getIdentifier());
						Vector demands = creator.getCreatorResources();
						for (int j = 0; j < demands.size(); j++) {
							Contract template = new Contract
								(Contract.TYPE_INSTANCE_TEMPLATE, creator.getName());
							template.addContract((Contract)demands.elementAt(j));
							try {
								Lease lease = assembler.prepare(applicationID);
								LeaseListener leaser = new LeaseListener();
								registry.hook(lease, leaser);
								try {
									// setup the assembler
									Vector preferences = new Vector();
									preferences.addElement(template);
									AssemblyState state = new AssemblyState();
									state.setContracts(preferences);
									state.setCreatorID(creator.getIdentifier());
									state.setElementID(creator.getIdentifier());
									state.setSystemID(SystemID.SYSTEM);
									state.setName(template.getName());
									assembler.setup(applicationID, state);
									Assembly assembly = assembler.configure(applicationID);
									if (assembly != null) {
										Assembly[] assemblies = assembly.getResources();
										if (assemblies != null && assemblies.length == 1) {
											synchronized (creator) {
												synchronized (Container.this) {
													creator.startCreatorResource(assemblies[0]);	
												}
											}	
										}
									}
								} catch (InvocationException e) {
									Logging.error(getClass(), "Could not configure allocator resources.", e);
									abort = true;
								}
								try {
									registry.unhook(lease, leaser, false);
									assembler.remove(applicationID);	
								} catch (InvocationException e) {
									Logging.error(getClass(), "Could not clean up assembler properly.", e);
									abort = true;
								}
							} catch (InvocationException e) {
								Logging.error(getClass(), "Could not initialize assembler.", e);
								abort = true;
							}
							if (abort) {
								Logging.debug(getClass(), "Aborting recomputation of assignments");
								creator.getLock().release();
								synchronized (Container.this) {
									if (reiterating) {
										reiterating = false;
										continue compute;
									} else {
										recomputing = false;
										return;
									}
								}
							}
						}
						creator.getLock().release();
					}
					synchronized (Container.this) {
						if (reiterating) {
							reiterating = false;
							continue compute;
						} else {
							recomputing = false;
							return;
						}
					}
				}
			}
			
		});		
	}
	
	
	
	/**
	 * Tries to start a resource assignment using the specified assembly for the
	 * specified binding.
	 * 
	 * @param assembly The assembly that contains the description of what to start.
	 * @param binding The binding that is used by the resource user.
	 * @return The resource context with a provision if successful, the resource
	 * 	context without a provision if the resource could be found but not allocated
	 * 	and null if the resource's factory cannot be locked or the reused element
	 * 	does not exist. 
	 */
	public ResourceContext startResource(Assembly assembly, ResourceBinding binding) {
		Logging.debug(getClass(), "Start resource called (" + (assembly.getElementID() == null?"new":"reusing") + ").");
		ObjectID creator = assembly.getCreatorID();
		ObjectID element = assembly.getElementID();
		AllocatorContext allocator = null;
		synchronized (allocators) {
			allocator = (AllocatorContext)allocators.get(creator);	
		}
		if (allocator == null) return null;
		if (allocator.getLock().aquire()) {
			ResourceContext context = null;
			if (element != null) {
				context = (ResourceContext)resources.get(element);
				if (context == null) {
					allocator.getLock().release();
					Logging.debug(getClass(), "Cannot find reused resource " + element + ".");
					return null;
				}
			} else {
				context = new ResourceContext(this, allocator, binding);
			}
			context.startResource(assembly);
			synchronized (resources) {
				resources.put(context.getIdentifier(), context);
			}
			allocator.getLock().release();
			return context;
		} else {
			Logging.debug(getClass(), "Allocator has been removed.");
			return null;
		}
	}
	
	/**
	 * Stops the resource assignment with the specified identifier.
	 * 
	 * @param identifier The identifier of the resource assignment to
	 * 	stop.
	 */
	public void stopResource(ObjectID identifier) {
		Logging.debug(getClass(), "Stop resource called with successor " + identifier + ".");
		ResourceContext rcontext = null;
		synchronized (resources) {
			rcontext = (ResourceContext)resources.get(identifier);
		}
		if (rcontext != null) {
			rcontext.stopResource();
			synchronized (resources) {
				resources.remove(identifier);
			}
		} else {
			Logging.debug(getClass(), "Ignoring stop call due to missing resource " + identifier + ".");
		}
	}

// container interface to support assembler queries 
	
	/**
	 * This method is part of the container interface. It is used to retrieve the
	 * contracts of resources and instances that can be used to satisfy a certain
	 * dependency.
	 * 
	 * @param demands The demand contracts for instances and resources.
	 * @return A hashtable of hashtable of vector that hashes input contracts to
	 * 	a hashtable of factoryid to vector of contracts.
	 */
	public Hashtable getTemplates(Vector demands) {
		Logging.debug(getClass(), "Received contract request.");
		if (demands == null) throw new NullPointerException("Demand must not be null.");
		Hashtable result = new Hashtable();
		for (int i = 0; i < demands.size(); i++) {
			Contract demand = (Contract)demands.elementAt(i);
			Hashtable demandResult = new Hashtable();
			if (demand.getType() == Contract.TYPE_RESOURCE_DEMAND) {
				synchronized (allocators) {
					Enumeration e = allocators.elements();
					while (e.hasMoreElements()) {
						AllocatorContext ctx = (AllocatorContext)e.nextElement();
						if (ctx.getLock().aquire()) {
							Vector cs = ctx.deriveContracts(demand);
							if (cs != null && cs.size() > 0) {
								demandResult.put(ctx.getIdentifier(), cs);
							}
							ctx.getLock().release();
						} else {
							Logging.debug(getClass(), "Omitting allocator due to removal " + ctx.getIdentifier() + ".");
						}
					}
				}
			} else if (demand.getType() == Contract.TYPE_INSTANCE_DEMAND) {
				synchronized (factories) {
					Enumeration e = factories.elements();
					while (e.hasMoreElements()) {
						FactoryContext ctx = (FactoryContext)e.nextElement();
						if (ctx.getLock().aquire()) {
							Vector cs = ctx.deriveContracts(demand);
							if (cs != null && cs.size() > 0) {
								demandResult.put(ctx.getIdentifier(), cs);
							}
							ctx.getLock().release();
						} else {
							Logging.debug(getClass(), "Omitting factory due to removal " + ctx.getIdentifier() + ".");
						}
					}
					
				}
			} else {
				Logging.debug(getClass(), "Retrieved malformed contract type.");
			}
			if (demandResult.size() > 0) {
				result.put(demand, demandResult);
			}
		}
		return result;
	}
	
	/**
	 * Resources the free resources that are available on the container as hashtable
	 * of integer arrays that are hashed by resource allocator identifier.
	 * 
	 * @return A hashtable of integer arrays hashed by resource allocator identifier
	 * 	that denote the available resources for each resource allocator installed
	 * 	in the container at the moment.
	 */
	public Hashtable getResources() {
		Hashtable result = new Hashtable();
		synchronized (allocators) {
			Enumeration e = allocators.elements();
			while (e.hasMoreElements()) {
				AllocatorContext context = (AllocatorContext)e.nextElement();
				if (context.getLock().aquire()) {
					result.put(context.getIdentifier(), context.freeResources());
					context.getLock().release();
				} else {
					Logging.debug(getClass(), "Omitting removed allocator " + context.getIdentifier() + ".");
				}
			}
		}
		return result;
	}
	
// ui interface this interface is not intended to be used by anyone except the ui (!)
	
	/**
	 * Retrieves the factories of the container. Returns a vector of
	 * object arrays [objectID, name, template, status].
	 * 
	 * @return The factories of the container as a vector of object arrays.
	 */
	public Vector getFactoriesUI() {
		Logging.debug(getClass(), "Get factories called.");
		Vector result = new Vector();
		synchronized (factories) {
			Enumeration e = factories.elements();
			while (e.hasMoreElements()) {
				FactoryContext factory = (FactoryContext)e.nextElement();
				if (factory.getLock().aquire()) {
					Object[] description = factory.getDescription();
					result.addElement(description);
					factory.getLock().release();
				}
			}
		}
		return result;
	}
	
	/**
	 * Retrieves the allocators of the container. Returns a vector of
	 * object arrays [objectID, name, total, free, template, status].
	 * 
	 * @return The allocators of the container as a vector of object arrays.
	 */
	public Vector getAllocatorsUI() {
		Logging.debug(getClass(), "Get allocators called.");
		Vector result = new Vector();
		synchronized (allocators) {
			Enumeration e = allocators.elements();
			while (e.hasMoreElements()) {
				AllocatorContext allocator = (AllocatorContext)e.nextElement();
				if (allocator.getLock().aquire()) {
					Object[] description = allocator.getDescription();
					result.addElement(description);
					allocator.getLock().release();
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the instances of a certain factory. Returns a vector of
	 * object arrays [objectID, name, template, status].
	 * 
	 * @param id The id of the factory whose instances need to be retrieved.
	 * @return The instances of the factory as object arrays.
	 */
	public Vector getInstancesUI(ObjectID id) {
		Logging.debug(getClass(), "Get instances called with " + id + ".");
		Vector temp = new Vector();
		synchronized (instances) {
			Enumeration e = instances.elements();
			while (e.hasMoreElements()) {
				InstanceContext c = (InstanceContext)e.nextElement();
				if (c.getFactory().getIdentifier().equals(id)) {
					temp.addElement(c);
				}
			}
		}
		Vector result = new Vector();
		for (int i = 0; i < temp.size(); i++) {
			InstanceContext c = (InstanceContext)temp.elementAt(i);
			synchronized (c) {
				result.addElement(c.getDescription());
			}
		}
		return result;
	}
	
	/**
	 * Returns the resources of a certain allocator. Returns a vector of
	 * object arrays [objectID, name, estimate, template, status].
	 * 
	 * @param id The id of the allocator whose resources needs to be retrieved.
	 * @return The resources issued by the allocator as object arrays.
	 */
	public Vector getResourcesUI(ObjectID id) {
		Logging.debug(getClass(), "Get resources called with " + id + ".");
		Vector temp = new Vector();
		synchronized (resources) {
			Enumeration e = resources.elements();
			while (e.hasMoreElements()) {
				ResourceContext c = (ResourceContext)e.nextElement();
				if (c.getAllocator().getIdentifier().equals(id)) {
					temp.addElement(c);
				}
			}
		}
		Vector result = new Vector();
		for (int i = 0; i < temp.size(); i++) {
			ResourceContext c = (ResourceContext)temp.elementAt(i);
			synchronized (c) {
				result.addElement(c.getDescription());
			}
		}
		return result;
	}
	
	/**
	 * Signals a change instance request that comes from the instance
	 * with the specified id. 
	 * 
	 * @param id The id of the instance that should signal the request.
	 */
	public void changeInstanceUI(ObjectID id) {
		InstanceContext context = null;
		synchronized (instances) {
			context = (InstanceContext)instances.get(id);
		}
		if (context != null) {
			context.changeInstance();	
		}
	}

	/**
	 * Signals a commit template request that comes from the instance
	 * with the specified id.
	 * 
	 * @param id The id of the instance that should signal the request.
	 */
	public void commitInstanceUI(ObjectID id) {
		InstanceContext context = null;
		synchronized (instances) {
			context = (InstanceContext)instances.get(id);
		}
		if (context != null) {
			try {
				context.getTemplate().commitTemplate();	
			} catch (IllegalStateException e) {
				Logging.error(getClass(), "Could not commit instance.", e);
			}
		}
	}
	
	/**
	 * Signals a stop instance request to the instance with the specified id.
	 * 
	 * @param id The id of the instance that should be stopped.
	 */
	public void stopInstanceUI(ObjectID id) {
		InstanceContext context = null;
		synchronized (instances) {
			context = (InstanceContext)instances.get(id);
		}
		if (context != null) {
			context.removeInstance();	
		}
	}
	
	/**
	 * Signals a change resource request that comes from the resource
	 * with the specified id. 
	 * 
	 * @param id The id of the resource that should signal the request.
	 */
	public void changeResourceUI(ObjectID id) {
		ResourceContext context = null;
		synchronized (resources) {
			context = (ResourceContext)resources.get(id);
		}
		if (context != null) {
			context.changeResource();	
		}
	}

	/**
	 * Signals a commit template request that comes from the resource
	 * with the specified id.
	 * 
	 * @param id The id of the resource that should signal the request.
	 */
	public void commitResourceUI(ObjectID id) {
		ResourceContext context = null;
		synchronized (resources) {
			context = (ResourceContext)resources.get(id);
		}
		if (context != null) {
			try {
				context.getTemplate().commitTemplate();	
			} catch (IllegalStateException e) {
				Logging.error(getClass(), "Could not commit resource.", e);
			}
		}
	}
	
	/**
	 * Signals a stop resource request to the resource with the specified id.
	 * 
	 * @param id The id of the resource that should be stopped.
	 */
	public void stopResourceUI(ObjectID id) {
		ResourceContext context = null;
		synchronized (resources) {
			context = (ResourceContext)resources.get(id);
		}
		if (context != null) {
			context.removeResource();	
		}
	}
}
