package info.pppc.pcom.system.container.internal.component;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.MultiOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.AssemblerProxy;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.container.ContainerProxy;
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.container.InstanceSetup;
import info.pppc.pcom.system.container.InstanceState;
import info.pppc.pcom.system.container.internal.AbstractBinding;
import info.pppc.pcom.system.container.internal.AbstractInstanceContext;
import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.AbstractTemplate;
import info.pppc.pcom.system.container.internal.capability.ResourceBinding;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IEventCollector;
import info.pppc.pcom.system.model.component.IEventEmitter;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceRestorer;
import info.pppc.pcom.system.model.component.IInstanceStatus;
import info.pppc.pcom.system.model.component.IInstanceTemplate;

/**
 * The instance context manages an instance. It provides methods that enable
 * an instance to retrieve its contract and description. Furthermore, it allows
 * the instance to perform operations in a safe manner. If an operation crashes,
 * the instance will be stopped.
 * 
 * @author Mac
 */
public class InstanceContext extends AbstractInstanceContext implements IInstanceContext {
	
	/**
	 * This state constant is used to signal that the instance bound to this context
	 * is paused at the moment.
	 */
	public static final int STATE_INSTANCE_PAUSED = 1;
	
	/**
	 * This state constant is used to signal that the instance bound to this context
	 * is started at the moment.
	 */
	public static final int STATE_INSTANCE_STARTED = 2;
	
	/**
	 * This state constant is used to signal that the instance bound to this context
	 * is stopped at the moment.
	 */
	public static final int STATE_INSTANCE_STOPPED = 4;
	
	/**
	 * The name of the instance as defined by the parent.
	 */
	private String name;
	
	/**
	 * A reference to the instance that is bound to this instance
	 * context object.
	 */
	private IInstance instance;

	/**
	 * A reference to the context of the factory that created the instance.
	 */
	private FactoryContext factory;
		
	/**
	 * The lease that has been issued to the parent instance.
	 */
	private Lease lease = null;
	
	/**
	 * The instance id of the parent instance.
	 */
	private ObjectID parentID = null;
	
	/**
	 * The conainer id of the parent instance.
	 */
	private ObjectID containerID = null;
	
	/**
	 * The system id of the parent instance.
	 */
	private SystemID systemID = null;
	
	/**
	 * The event emitter that is used to send events to the parent
	 * instance.
	 */
	private EventEmitter emitter = null;
	
	/**
	 * The skeleton that is used to receive calls from the parent
	 * instance.
	 */
	private InstanceSkeleton skeleton = null;

	
	/**
	 * Creates a new instance context object for the specified factory and container
	 * using the specified instance setup.
	 * 
	 * @param container The container of the instance.
	 * @param factory A reference to the context object of the factory that created the instance.
	 * @param setup The instance setup that describes how the instance should be configured.
	 */
	public InstanceContext(Container container, FactoryContext factory, InstanceSetup setup) {
		super(container, new InstanceTemplate(setup.getAssembly().getName()), 
				new InstanceStatus(setup.getAssembly().getName()));
		setState(STATE_INSTANCE_STOPPED);
		this.factory = factory;
		name = setup.getAssembly().getName();
		skeleton = factory.createSkeleton();
		InvocationBroker broker = InvocationBroker.getInstance();
		ObjectRegistry registry = broker.getObjectRegistry();
		ObjectID skeletonID = registry.registerObject(skeleton);
		skeleton.setObjectID(skeletonID);
		parentID = setup.getInstanceID();
		containerID = setup.getContainerID();
		systemID = setup.getSystemID();
		emitter = new EventEmitter();
		emitter.setSourceID(new ReferenceID(SystemID.SYSTEM, getIdentifier()));
		emitter.setTargetID(new ReferenceID(systemID, setup.getCollectorID()));
	}

	/**
	 * Returns the factory context of the factory that created the instance.
	 * 
	 * @return The factory context of the factory that created the instance.
	 */
	public FactoryContext getFactory() {
		return factory;
	}
	
// external methods used by the container
	
	/**
	 * Configures and starts the instance using the specified setup.
	 *
	 * @param demand The demand of the parent directed towards the instance.
	 * @param assembly The assembly that describes the configuration of the
	 * 	instance.
	 * @param phase The phase that is introduced by the startup.
	 * @return The state of the instance.
	 * @throws Throwable Thrown if the instance could not be started.
	 */
	public synchronized InstanceState startInstance(Contract demand, Assembly assembly, int phase) throws Throwable {
		setPhase(phase); // update the phase
		// initialize the instance for the first time
		if (getState() == STATE_INSTANCE_STOPPED) {
			instance = factory.createInstance();
			skeleton.setTarget(instance);
			instance.setContext(this);
		}
		// adjust the old demand to the new demand and the old provision to the new provision
		AbstractStatus status = getAbstractStatus();
		status.addContract(demand);
		AbstractTemplate template = getAbstractTemplate();
		template.addContract(assembly.getTemplate().getContract
				(Contract.TYPE_INSTANCE_PROVISION));
		// configure all bindings if possible
		MultiOperation operations = start(assembly);
		operations.performSynchronous();
		getAbstractStatus().clearEvents();
		// determine whether all configurations have finished successfully
		boolean success = isValid(false);
		if (success) {
			setState(STATE_INSTANCE_STARTED);
			instance.start();
		} else {
			Logging.debug(getClass(), "Instance not configured properly.");
		}
		addTemplateListener();
		success = isValid(true);
		if (lease == null) {
			LeaseRegistry registry = getContainer().getLeaseRegistry();
			lease = registry.create(new IListener() {
				public void handleEvent(Event event) {
					Logging.debug(getClass(), "Instance lease timeout.");
					getContainer().stopInstance(getIdentifier());
				}
			});
		}
		InstanceState iState = new InstanceState();
		Contract provision = getAbstractTemplate().getContract(Contract.TYPE_INSTANCE_PROVISION);
		iState.bind(getIdentifier(), skeleton.getObjectID(), provision, lease);
		iState.setSuccess(success);
		return iState;
	}
	
	/**
	 * Called to pause the instance and to configure the assembler denoted
	 * by the pointer.
	 * 
	 * @param pointer The pointer that denotes what needs to be configured.
	 * @param phase The phase that is introduced by the pause.
	 */
	public synchronized void pauseInstance(AssemblyPointer pointer, int phase) {
		setPhase(phase); // update the phase
		if (getState() == STATE_INSTANCE_STARTED) {
			removeTemplateListener();
			try {
				instance.pause();
			} catch (Throwable t) {
				Logging.debug(getClass(), "Exception while pausing instance.");
			}
			setState(STATE_INSTANCE_PAUSED);
		}
		// prepare assembly state with instance data
		AssemblyState state = new AssemblyState();
		state.setCreatorID(factory.getIdentifier());
		state.setElementID(getIdentifier());
		state.setName(name);
		state.setSystemID(SystemID.SYSTEM);
		// prepare assembly state with binding data
		setup(state);
		try {
			AssemblerProxy proxy = new AssemblerProxy();
			proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
			proxy.setTargetID(pointer.getAssemblerID());
			pointer = proxy.setup(pointer, state);
			MultiOperation operations = pause(pointer);
			operations.performSynchronous();
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not transfer state to assembler " + pointer.getAssemblerID() + ".", e);
		}
	}
	
	/**
	 * Stops the instance and releases all bindings that might remain.
	 */
	public synchronized void stopInstance() {
		if (lease != null) {
			LeaseRegistry registry = getContainer().getLeaseRegistry();
			registry.remove(lease, false);
			lease = null;
		}
		if (getState() == STATE_INSTANCE_STARTED || getState() == STATE_INSTANCE_PAUSED) {
			removeTemplateListener();
			try {
				setState(STATE_INSTANCE_STARTED);
				instance.stop();
				instance.unsetContext();
			} catch (Throwable e) {
				Logging.error(getClass(), "Exception while stopping instance.", e);
			}
		}
		setState(STATE_INSTANCE_STOPPED);
		stop();
	}
	
	/**
	 * Loads the state of the instance from the specified checkpoint.
	 * 
	 * @param checkpoint The checkpoint that contains the state.
	 */
	public void loadInstance(InstanceCheckpoint checkpoint) {
		try {
			instance.loadCheckpoint(new InstanceCheckpointAdapter(checkpoint));
		} catch (Throwable t) {
			Logging.error(getClass(), "Exception while loading instance.", t);
		}
	}
	
	/**
	 * Stores the state of the instance in a checkpoint and returns
	 * the state.
	 * 
	 * @return The state of the instance written into the checkpoint 
	 * 	or null if the call failed.
	 */
	public InstanceCheckpoint storeInstance() {
		try {
			InstanceCheckpoint checkpoint = new InstanceCheckpoint();
			instance.storeCheckpoint(new InstanceCheckpointAdapter(checkpoint));
			return checkpoint;
		} catch (Throwable t) {
			Logging.error(getClass(), "Exception while storing instance.", t);
			return null;
		}
	}
	
	/**
	 * Called to signal that the provision of some child has changed to the specified
	 * provision.
	 * 
	 * @param name The name of the child whose provision has changed.
	 * @param phase The phase of the message used to suppress outdated messages.
	 * @param provision The provision that has changed.
	 */
	public void changeInstance(String name, int phase, Contract provision) {
		synchronized (this) {
			if (phase == getPhase() && getState() == STATE_INSTANCE_STARTED) {
				InstanceBinding binding = (InstanceBinding)getInstance(name);
				if (binding != null) {
					binding.changeInstance(provision);			
				}
			}
		}
	}
	
	/**
	 * Called to signal that the instance with the specified name has been 
	 * removed.
	 * 
	 * @param name The name of the instance that has been removed.
	 * @param phase The phase of the message used to suppress outdated messages.
	 */
	public void removeInstance(String name, int phase) {
		synchronized (this) {
			if (getPhase() == phase) {
				InstanceBinding binding = (InstanceBinding)getInstance(name);
				if (binding != null) {
					if (getState() == STATE_INSTANCE_STARTED) {
						binding.removeInstance();				
					} else {
						binding.stop(false);
					}
				}
			}
		}
	}
	
	/**
	 * Called to signal that the instance with the specified name wants the
	 * application to be exited.
	 * 
	 * @param name The name of the instance that wants to stop the application.
	 * @param phase The phase of the message used to suppress outdated messages.
	 */
	public void exitApplication(String name, int phase) {
		synchronized (this) {
			if (phase == getPhase() && getState() == STATE_INSTANCE_STARTED) {
				InstanceBinding binding = (InstanceBinding)getInstance(name);
				if (binding != null) {
					exitApplication();
				}
			}
		}
	}
	
	/**
	 * Called to signal that the instance with the specified name wants the
	 * application to be saved.
	 * 
	 * @param name The name of the instance that wants to save the application.
	 * @param phase The phase of the message used to suppress outdated messages.
	 */
	public void saveApplication(String name, int phase) {
		synchronized (this) {
			if (phase == getPhase() && getState() == STATE_INSTANCE_STARTED) {
				InstanceBinding binding = (InstanceBinding)getInstance(name);
				if (binding != null) {
					saveApplication();
				}
			}
		}
	}


// methods that are inherited by the base class
	
	/**
	 * Called whenever a binding is changed or whenever a contract has been
	 * changed. If the template and status is no longer valid, the method
	 * will notify the parent about the change.
	 */
	protected void validate() {
		getAbstractStatus().fireEvents();
		final boolean valid = isValid(true); 
		final int phase = getPhase();
		if (! valid) {
			// invalidate contract
			getAbstractTemplate().addContract(new Contract(Contract.TYPE_INSTANCE_PROVISION, name));
		}
		// send notification if instance is started
		if (getState() == STATE_INSTANCE_STARTED) {
			final Contract provision = getAbstractTemplate().getContract(Contract.TYPE_INSTANCE_PROVISION, name);
			performOperation(new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					ContainerProxy proxy = new ContainerProxy();
					proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
					proxy.setTargetID(new ReferenceID(systemID, containerID));
					try {
						proxy.changeInstance(parentID, name, phase, provision);
						// if the previous change broke us, disable the instance
						if (!valid) {
							synchronized (InstanceContext.this) {
								// determine whether we have been updated in the mean time
								if (phase == getPhase() && getState() == STATE_INSTANCE_STARTED) {
									removeTemplateListener();
									try {
										instance.pause();
									} catch (Throwable t) {
										Logging.error(getClass(), "Could not pause instance.", t);
									}
									setState(STATE_INSTANCE_PAUSED);
								}
							}						
						}
					} catch (InvocationException e) {
						// stop the instance completely
						// without parent notification since unreachable
						getContainer().stopInstance(getIdentifier());
					}
				}
			});
		}
	}
	
	/**
	 * Called whenever the instance has performed an operation that crashed and
	 * the instance should be removed.
	 */
	protected void remove() {
		// remove the instance with parent notification
		removeInstance();
	}

	/**
	 * This is a helper method called by the base class to create a new instance
	 * binding with the specified type.
	 * 
	 * @param name The name of the instance binding to create.
	 * @return The instance binding for the specified name.
	 */
	protected AbstractBinding createInstance(String name) {
		return new InstanceBinding(this, name, factory.createProxy(name));
	}

	/**
	 * This is a helper method called by the base class to create a new resource
	 * binding with the specified type.
	 * 
	 * @param name The name of the resource binding to create.
	 * @return The resource binding with the specified name
	 */
	protected AbstractBinding createResource(String name) {
		return new ResourceBinding(this, name);
	}

// methods used by instances to access the container
	
	/**
	 * Returns the contract of the instance that is bound to this context object.
	 * The contract enables the instance to retrieve the requirements and the
	 * provisions that are currently issued to the instance.
	 * 
	 * @return The contract of the instance bound to this context object.
	 */
	public synchronized IInstanceStatus getStatus() {
		if (getState() == STATE_INSTANCE_STARTED) {
			return (IInstanceStatus)super.getAbstractStatus();
		} else {
			throw new IllegalStateException("Cannot retrieve status, instance is not started.");
		}
	}

	/**
	 * Returns the description of the instance managed by the factory. Using
	 * this description, the instance can manipulate its provision and its 
	 * requirements.
	 * 
	 * @return The description of the instance bound to this context object.
	 */
	public synchronized IInstanceTemplate getTemplate() {
		if (getState() == STATE_INSTANCE_STARTED) {
			return (IInstanceTemplate)super.getAbstractTemplate();
		} else {
			throw new IllegalStateException("Cannot retrieve template, instance is not started.");
		}
	}
	
	/**
	 * Returns the proxy for the dependency with the specified name.
	 * 
	 * @param name The name of the dependency as specified by the contract.
	 * @return The instance proxy for the specified dependency or null
	 * 	if the dependency is not resolved.
	 */
	public synchronized IInstanceProxy getProxy(String name) {
		if (getState() == STATE_INSTANCE_STARTED) {
			InstanceBinding binding = (InstanceBinding)getInstance(name);
			if (binding != null) {
				return binding.getProxy();	
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve proxy, instance is not started.");
		}		
	}
	
	/**
	 * Returns the state for the dependency with the specified name.
	 * 
	 * @param name The name of the dependency as specified by the contract.
	 * @return The instance state for the specified dependency or null
	 * 	if the dependency is not resolved.
	 */
	public synchronized IInstanceRestorer getRestorer(String name) {
		if (getState() == STATE_INSTANCE_STARTED) {
			InstanceBinding binding = (InstanceBinding)getInstance(name);
			if (binding != null) {
				return binding.getRestorer();	
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve proxy, instance is not started.");
		}		
	}

	/**
	 * Called to retrieve the event collector for the specified dependency.
	 * 
	 * @param name The name of the dependency whose event collector should
	 * 	be retrieved.
	 * @return The event collector for the specified dependency or null if
	 * 	the dependency does not exist.
	 */
	public synchronized IEventCollector getCollector(String name) {
		if (getState() == STATE_INSTANCE_STARTED) {
			InstanceBinding binding = (InstanceBinding)getInstance(name);
			if (binding != null) {
				return binding.getCollector();	
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve collector, instance is not started.");
		}	
	}
	
	/**
	 * Called to retrieve the resource accessor for the specified resource
	 * dependency. 
	 * 
	 * @param name The name of the resource whose accessor should be retrieved.
	 * @return The resource accessor or null, if the resource does not exist
	 * 	or if it does not have an accessor.
	 */
	public synchronized Object getAccessor(String name) {
		if (getState() == STATE_INSTANCE_STARTED) {
			ResourceBinding binding = (ResourceBinding)getResource(name);
			if (binding != null) {
				return binding.getAccessor();
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve accessor, instance is not started.");
		}
	}

	/**
	 * Returns the event emitter that can be used to send events to the
	 * parent instance.
	 * 
	 * @return The event emitter that can be used to send events to the
	 * 	parent.
	 */
	public synchronized IEventEmitter getEmitter() {
		if (getState() == STATE_INSTANCE_STARTED) {
			return emitter;
		} else {
			throw new IllegalStateException("Cannot retrieve emitter, instance is not started.");
		}
	}
	
	/**
	 * Called to signal that the instance bound to this context should
	 * can no longer fulfill its contract. This will forward the problem
	 * to the parent instance.
	 */
	public void changeInstance() {
		// copy the phase synchronously and send announcement
		synchronized (this) {
			if (getState() != STATE_INSTANCE_STARTED) {
				throw new IllegalStateException("Cannot change instance, instance is not started.");
			}
			// break the local contract
			getAbstractTemplate().removeContract(Contract.TYPE_INSTANCE_PROVISION, name);
			// validate and send change
			validate();
		}
		
	}
	
	/**
	 * Called to signal that the application that uses this instance should
	 * be stopped due to user request.
	 */
	public void exitApplication() {
		if (getState() == STATE_INSTANCE_STARTED) {
			final int phase = getPhase();
			performOperation(new IOperation() {
				public void perform(IMonitor monitor) {
					try {
						ContainerProxy proxy = new ContainerProxy();
						proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
						proxy.setTargetID(new ReferenceID(systemID, containerID));
						proxy.exitApplication(parentID, name, phase);
					} catch (InvocationException e) {
						Logging.error(getClass(), "Could not signal exit application.", e);
					}
				}
			});
		} else {
			throw new IllegalStateException("Cannot exit application, instance is not started.");
		}
	}

	/**
	 * Called to signal that the application that uses this instance should
	 * be saved due to user request.
	 */
	public void saveApplication() {
		if (getState() == STATE_INSTANCE_STARTED) {
			final int phase = getPhase();
			performOperation(new IOperation() {
				public void perform(IMonitor monitor) {
					try {
						ContainerProxy proxy = new ContainerProxy();
						proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
						proxy.setTargetID(new ReferenceID(systemID, containerID));
						proxy.saveApplication(parentID, name, phase);
					} catch (InvocationException e) {
						Logging.error(getClass(), "Could not signal save application.", e);
					}
				}
			});
		} else {
			throw new IllegalStateException("Cannot save application, instance is not started.");
		}
	}

	
	/**
	 * Called to signal that the instance bound to this context can no longer
	 * be executed and must be removed immediately. This will lead to a 
	 * forceful remove without any notifications.
	 */
	public void removeInstance() {
		final int phase;
		synchronized (this) {
			if (getState() != STATE_INSTANCE_STOPPED) {
				phase = getPhase();
				getContainer().stopInstance(getIdentifier());
			} else {
				throw new IllegalStateException("Cannot remove instance, instance is not started.");
			}			
		}
		performOperation(new IOperation() {
			public void perform(IMonitor monitor) throws Exception {
				// be polite and send notification to parent
				ContainerProxy proxy = new ContainerProxy();
				proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
				proxy.setTargetID(new ReferenceID(systemID, containerID));
				try {
					proxy.removeInstance(parentID, name, phase);
				} catch (InvocationException e) {
					// can be safely ignored
				}				
			};
		});
	}
	
	
// methods required by the ui to visualize container state	
	
	/**
	 * Returns the current description of the instance. This method
	 * is used by the user interface only. The resulting structure
	 * will be as follows: [objectID, name, template, status].
	 * 
	 * @return The current description of the instance.
	 */
	public Object[] getDescription() {
		Object[] description = new Object[4];
		description[0] = getIdentifier();
		description[1] = name;
		description[2] = getAbstractTemplate().getContract();
		description[3] = getAbstractStatus().getContract();
		return description;
	}
	
}
