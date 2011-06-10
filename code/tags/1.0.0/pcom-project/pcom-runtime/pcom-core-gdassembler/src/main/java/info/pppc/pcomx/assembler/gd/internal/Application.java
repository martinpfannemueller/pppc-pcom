package info.pppc.pcomx.assembler.gd.internal;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.NullMonitor;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.application.IApplicationManager;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.container.ContainerProxy;
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcomx.assembler.gd.GDAssembler;
import info.pppc.pcomx.assembler.gd.GDAssemblerProxy;

/**
 * The application is the data container of the GCAssembler
 * that performs the configuration for a single application
 * part on one device. The application has a single thread
 * that performs all computation. To start the computation,
 * an event must be queued into the eventing queue. This
 * will then be performed by the application. The GCAssembler
 * starts and stops the thread whenever it creates or removes
 * an application.
 * 
 * @author Mac
 */
public class Application implements IOperation, IListener {

	/**
	 * Called whenever the setup method has been called. The data
	 * object will be an object array with two elements. The first
	 * will be an assembly pointer. The second will be the 
	 * assembly state that has been added. The source of the event will
	 * be the application that received the state.
	 */
	public static final int EVENT_STATE_ADDED = 1;
	
	/**
	 * Called whenever a request for a certain instance has been
	 * added and is about to be resolved. The data object will be
	 * the request that has been added. The source will be the 
	 * application.
	 */
	public static final int EVENT_REQEUST_ADDED = 2;
	
	/**
	 * Called whenever the state of a request has changed, this 
	 * happens whenever a binding reports its state or if the
	 * binding that causes the request requests a state change.
	 * The data object will be the request that has been added. 
	 * The source will be the application.
	 */
	public static final int EVENT_REQUEST_CHANGED = 4;
	
	/**
	 * Called whenever a request for certain instance is doomed
	 * and cannot be resolved. The data object will be the request
	 * that has been added. The source will be the application.
	 */
	public static final int EVENT_REQUEST_REMOVED = 8;
	
	/**
	 * Called whenever a binding of a certain request changed its
	 * state. The data object will contain the binding that has
	 * changed its state, the source will be the application.
	 */
	public static final int EVENT_BINDING_CHANGED = 16;

	/**
	 * Signals that the specified device is participating in 
	 * the configuration. The data object will contain the
	 * system id of the device that has been added. The
	 * source object will be the application.
	 */
	public static final int EVENT_DEVICE_ADDED = 32;
	
	/**
	 * Signals that the specified device has been removed
	 * from the configuration process and that the configuration
	 * is soon about to be restarted. The data object will 
	 * contain the device, the source object will be the
	 * application. 
	 */
	public static final int EVENT_DEVICE_REMOVED = 64;
	
	/**
	 * The id of the application represented by this assembler
	 * application.
	 */
	private ReferenceID id;
	
	/**
	 * The systems used to configure the application. This 
	 * vector is initialized with the available devices upon
	 * startup. While the algorithm is running, the contents
	 * of this vector decrease monitonically whenever a
	 * system cannot be contacted. If a system is removed,
	 * the assembler begins a complete new processing round.
	 * To supress outdated messages, the assembler increases
	 * its local phase whenever a system is removed. 
	 */
	private Vector systems;
	
	/**
	 * The master system that configures the application. To
	 * determine whether the local system is the master, compare
	 * this system id with the local system id.
	 */
	private SystemID master;
	
	/**
	 * The monitor used to stop the application. The event queue
	 * as well as the configuration thread block upon this monitor.
	 */
	private IMonitor monitor = new NullMonitor();
	
	/**
	 * The events that need to be performed by the application.
	 * Events are added by this assembler and the remote assemblers
	 * through the gd assembler facade.
	 */
	private Vector events = new Vector();
	
	/**
	 * A hashtable that stores the assembly states hashed by pointers.
	 * The states are used to initialize the preferences and to perform
	 * cost-aware adaptation.
	 */
	private Hashtable states = new Hashtable();
	
	/**
	 * A hashtable that stores the instance requests hashed by pointers.
	 * A request essentially represents a configuration request from 
	 * some system that participates in the assembly.
	 */
	private Hashtable requests = new Hashtable();
	
	/**
	 * A hashtable that contains the estimated available resources for
	 * the container. This table is initialized lazy and it is reset
	 * using the container's current resouces whenever a device is 
	 * removed from the distributed assembly group.
	 */
	private Hashtable resources;
	
	/**
	 * The current phase of the assembler. The phase is incremented 
	 * whenever a system is removed from the distributed assembly
	 * group. An increased phase invalidates all messages but the
	 * remove messages.
	 */
	private int phase = 0;
		
	/**
	 * The application lease that is used by the application service
	 * or the master configuration algorithm to maintain the state of 
	 * the application.
	 */
	private Lease lease;
	
	/**
	 * The listeners that are registered at the application.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * The reference to the container that is used to configure an 
	 * application. This is reused to avoid frequent instantiations.
	 */
	private IContainer container;
	
	/**
	 * The reference that is used to send messages to other assemblers.
	 * This is essentially reused to avoid frequent instantiations.
	 */
	private GDAssemblerProxy assembler;
	
// variables used by the master device, they are only accessible and useful there
	
	/**
	 * The leases used by the master device to detect unavailable devices.
	 * This field is only available if the device is a master.
	 */
	private Vector leases;
	
	/**
	 * The lease registry used to manage the state of slave algorithms.
	 * This field is solely available if the device is a master.
	 */
	private LeaseRegistry registry;
	
	/**
	 * A flag that indicates whether the algorithm is initialized.
	 */
	private boolean initialized = false;
	
	/**
	 * A flag that indicates whether termination has been detected.
	 */
	private boolean finished = true;
		
	/**
	 * Creates a new master assembler with the specified application id.
	 * 
	 * @param applicationID The application of the assembler.
	 * @param registry The lease registry of the assembler.
	 * @param systems The systems used by the assembler.
	 */
	public Application(ReferenceID applicationID, LeaseRegistry registry, Vector systems) {
		this.id = applicationID;
		this.master = SystemID.SYSTEM;
		this.registry = registry;
		this.systems = systems;
		this.leases = new Vector();
		init();
	}
	
	/**
	 * Creates a new assembly for the specified application.
	 * 
	 * @param applicationID The id of the application that is configured.
	 * @param master The system that is the master during the configuration.
	 * @param systems The systems that are used during the configuration.
	 */
	public Application(ReferenceID applicationID, SystemID master, Vector systems) {
		this.id = applicationID;
		this.master = master;
		this.systems = systems;
		this.initialized = true;
		init();
	}
	
	/**
	 * Initializes the proxies for the container and the
	 * remote assemblers that participate in the assembly
	 * process.  
	 */
	private void init() {
		ReferenceID sourceID = new ReferenceID(SystemID.SYSTEM, GDAssembler.ASSEMBLER_ID);
		ContainerProxy containerProxy = new ContainerProxy();
		containerProxy.setTargetID(new ReferenceID(SystemID.SYSTEM, Container.CONTAINER_ID));
		containerProxy.setSourceID(sourceID);
		container = containerProxy;
		assembler = new GDAssemblerProxy();
		assembler.setSourceID(sourceID);
		// fire listeners for all participating devices
		Enumeration s = systems.elements();
		while (s.hasMoreElements()) {
			listeners.fireEvent(EVENT_DEVICE_ADDED, s.nextElement());
		}
	}
	
	/**
	 * Adds the specified listener to the set of listeners that are registered
	 * for the specified types. Possible types are defined by the event constants
	 * in this class.
	 * 
	 * @param types The types to register for.
	 * @param listener The listener to register.
	 */
	public void addApplicationListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes the previously registered listener for the set of events. If the
	 * listener has been found and removed successfully, this method returns true,
	 * otherwise false.
	 * 
	 * @param types The types of events to unregister for.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been removed successfully, false if it
	 * 	could not be found.
	 */
	public boolean removeApplicationListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	/**
	 * Returns the lease that is used to keep track of clients
	 * that do no not perform explicit removes. This lease is
	 * only used externally by the GDAssembler.
	 * 
	 * @return The lease that has been issued to the creator of the
	 * 	assembly.
	 */
	public Lease getApplicationLease() {
		return lease;
	}

	/**
	 * Sets the lease that is used to keep track of clients
	 * that do not perform explicit removes. This lease is only
	 * used externally by the GDAssembler.
	 * 
	 * @param lease The lease that has been issued to the creator of
	 * 	the assembly.
	 */
	public void setApplicationLease(Lease lease) {
		this.lease = lease;
	}
	
	/**
	 * Returns the monitor that can be used to cancel the operation.
	 * The GCAssembler uses this monitor to synchronize on the
	 * thread of the application and the application uses it to
	 * sync its event queue and internal data structures.
	 * 
	 * @return The monitor used to start and cancel the operation.
	 */
	public IMonitor getApplicationMonitor() {
		return monitor;
	}
	
	/**
	 * Returns the application id of the assembly.
	 * 
	 * @return The appliation id of the assembly.
	 */
	public ReferenceID getApplicationID() {
		return id;
	}
	
	/**
	 * Adds the specified event to the set of events that should be executed.
	 * 
	 * @param event The event to execute at some later point in time.
	 */
	public void scheduleEvent(AbstractEvent event) {
		synchronized (monitor) {
			events.addElement(event);
			monitor.notifyAll();
		}
	}
	
	/**
	 * Starts the queue that performs the event processing. If the
	 * device is the master device that performs the configuration,
	 * the algorithm will first initialze further slave devices which
	 * will be released whenever the thread terminates.
	 * 
	 * @param monitor The monitor that is used to cancel the operation.
	 */
	public void perform(IMonitor monitor) {
		if (master.equals(SystemID.SYSTEM)) {
			// configure all other assemblers that participate
			for (int i = systems.size() - 1; i >= 0; i--) {
				final SystemID system = (SystemID)systems.elementAt(i);
				if (! system.equals(SystemID.SYSTEM)) {
					assembler.setTargetID(new ReferenceID(system, GDAssembler.ASSEMBLER_ID));
					try {
						Lease lease = assembler.prepare(id, SystemID.SYSTEM, systems);
						registry.hook(lease, Application.this);
						leases.addElement(lease);
					} catch (InvocationException e) {
						scheduleEvent(new RemoveEvent(Application.this, system));
					}	
				}
			}
		}
		// set the state to initialized to process incoming states
		synchronized (monitor) {
			initialized = true;
			monitor.notifyAll();
		}
		// perform events until the application is canceled.
		while (true) {
			AbstractEvent event = null;
			synchronized (monitor) {		
				while (events.isEmpty() && ! monitor.isCanceled()) {
					try {
						monitor.wait();	
					} catch (InterruptedException e) {
						continue;
					}
				}
				if (monitor.isCanceled()) {
					break;
				} else {
					event = (AbstractEvent)events.elementAt(0);
					events.removeElementAt(0);
				}
			}
			// conditionally perform events or throw away if outdated
			if (event.getPhase() == -1 || event.getPhase() == phase) {
				event.perform();	
			}
		}
		if (master.equals(SystemID.SYSTEM)) {
			// clean up state of all remote assemblers
			for (int i = leases.size() - 1; i >= 0; i--) {
				Lease lease = (Lease)leases.elementAt(i);
				ReferenceID target = new ReferenceID(lease.getCreator(), GDAssembler.ASSEMBLER_ID);
				assembler.setTargetID(target);
				try {
					assembler.remove(id);
				} catch (InvocationException e) {
					Logging.error(getClass(), "Could not remove assembler for " 
							+ id + " on " + target + ".", e);
				}
				registry.unhook(lease, Application.this, false);
			}
		}
	}
	
	/**
	 * Starts the configuration process and returns the assembly that 
	 * has been configured or null if none. The calling thread is
	 * blocked until the process has finished.
	 * 
	 * @return The assembly that has been configured or null if the
	 * 	configuration terminated unsuccessfully.
	 */
	public Assembly configureApplication() {
		synchronized (monitor) {
			finished = false;
			scheduleEvent(new ResolveEvent(this, 0, new Pointer(), SystemID.SYSTEM, null, true));
			// wait until the configuration process finished or got aborted
			while (! monitor.isDone() && !finished) {
				try {
					monitor.wait();	
				} catch (InterruptedException e) {
					Logging.error(getClass(), "Thread got interrupted.", e);
				}
			}
			if (monitor.isDone()) {
				// configuration has been abored early, return null.
				return null;
			} else {
				return retrieveAssembly(new Pointer());
			}				
		}
	}

	/**
	 * Sets up the state for the specified pointer and returns
	 * the pointers for the children.
	 * 
	 * @param pointer The pointer of the state.
	 * @param state The state of the pointer.
	 * @return The pointers for the children of the state.
	 */
	public Pointer[] setupState(Pointer pointer, AssemblyState state) {
		// wait until the application is initialized
		synchronized (monitor) {
			while (!monitor.isDone() && !initialized) {
				try {
					monitor.wait();	
				} catch (InterruptedException e) {
					Logging.error(getClass(), "Thread got interrupted.", e);
				}
			}	
		}
		// store the state and prepare the pointers.
		states.put(pointer, state);
		Vector pointers = new Vector();
		String[] instances = state.getInstances();
		for (int i = instances.length - 1; i >= 0; i--) {
			String iname = instances[i];
			if (systems.contains(state.getInstance(iname))) {
				Pointer ipointer = new Pointer(pointer, true, iname);
				pointers.addElement(ipointer);
			}
		}
		String[] resources = state.getResources();
		for (int i = resources.length - 1; i >= 0; i--) {
			String rname = resources[i];
			Pointer rpointer = new Pointer(pointer, false, rname);
			pointers.addElement(rpointer);
		}
		Pointer[] result = new Pointer[pointers.size()];
		for (int i = pointers.size() - 1; i >= 0; i--) {
			result[i] = (Pointer)pointers.elementAt(i);
		}
		listeners.fireEvent(EVENT_STATE_ADDED, new Object[] { pointer, state });
		return result;
	}

	/**
	 * Retrieves the assembly for the specified pointer.
	 * 
	 * @param pointer The pointer whose assembly should be retrieved.
	 * @return The assembly for the pointer or null if there
	 * 	is none.
	 */
	public Assembly retrieveAssembly(Pointer pointer) {
		if (pointer.length() == 0) {
			InstanceRequest request = (InstanceRequest)requests.get(pointer);
			if (request == null) return null;
			return prepareAssembly(request);
		} else {
			Pointer parent = new Pointer(pointer, 1);
			InstanceRequest request = (InstanceRequest)requests.get(parent);
			if (request == null) return null;
			Instance instance = (Instance)request.instances.elementAt(0);
			if (pointer.isInstance(pointer.length() - 1)) {
				InstanceBinding binding = (InstanceBinding)instance.bindings.get
					(pointer.getName(pointer.length() - 1));
				return binding.assembly;
			} else {
				return (Assembly)instance.resources.get
					(pointer.getName(pointer.length() - 1));
			}
		}
	}
	
	/**
	 * Called by the lease registry whenever a lease expires. This
	 * will schedule a remove event for the specified device.
	 * 
	 * @param event The event that has been fired.
	 */
	public void handleEvent(Event event) {
		if (event.getType() == LeaseRegistry.EVENT_LEASE_EXPIRED) {
			// schedule a remove event for the system that expired
			Lease lease = (Lease)event.getData();		
			scheduleEvent(new RemoveEvent(this, lease.getCreator()));			
		}		
	}
	
	/**
	 * Signals the master application that the configuration
	 * process terminated and unblocks the thread that waits
	 * in the configure method.
	 */
	protected void unblockApplication() {
		synchronized (monitor) {
			finished = true;
			monitor.notifyAll();
		}
	}
	
	/**
	 * This method is called whenever an incoming report event of some
	 * remote device is executed. This method will decide whether the
	 * event should be sent to the parent or whether the child should
	 * be reconfigured.
	 * 
	 * @param pointer The pointer that points to some child of a locally
	 * 	stored request.
	 * @param assembly The assembly that contains the configuration for
	 * 	the subtree or null if the subtree could not be configured.
	 */
	protected void reportInstance(Pointer pointer, Assembly assembly) {
		InstanceRequest request = (InstanceRequest)requests.get(new Pointer(pointer, 1));
		if (request == null) return; 
		String name = pointer.getName(pointer.length() - 1);
		if (request.instances.size() == 0);
		Instance instance = (Instance)request.instances.elementAt(0);
		InstanceBinding binding = (InstanceBinding)instance.bindings.get(name);
		if (binding == null) return;
		if (assembly != null && binding.state == InstanceBinding.STATE_RESOLVING) {
			// only process successful requests if binding still should be resolved
			binding.state = InstanceBinding.STATE_RESOLVED;
			binding.assembly = assembly;
			listeners.fireEvent(EVENT_BINDING_CHANGED, binding);
			Enumeration bs = instance.bindings.elements();
			boolean finished = true;
			while (bs.hasMoreElements()) {
				InstanceBinding b = (InstanceBinding)bs.nextElement();
				if (b.state != InstanceBinding.STATE_RESOLVED) {
					finished = false;
					break;
				}
			}
			// report success if all bindings are completely resolved
			if (finished) {
				sendReport(request, true);
			}
		} else if (assembly == null) {
			// deal with unsuccessful configurations, if instance is no longer required,
			// detect whether instance is completely unconfigured, otherwise try to 
			// configure the instance in some other way
			if (request.required) {
				// detect whether further options can be configured and configure them
				binding.systems.removeElementAt(0);
				binding.assembly = null;
				if (binding.systems.size() > 0) {
					// configure the next system for the binding
					SystemID system = (SystemID)binding.systems.elementAt(0);
					listeners.fireEvent(EVENT_BINDING_CHANGED, binding);
					try {
						assembler.setTargetID(new ReferenceID(system, GDAssembler.ASSEMBLER_ID));
						assembler.resolve(id, phase, binding.pointer, SystemID.SYSTEM, binding.contract, instance.reuse);
					} catch (InvocationException e) {
						sendRemove(system, e);
					}
				} else {
					// send release to all resolving or resolved bindings, if no more
					// resolved or resolving bindings, release resources, and remove
					// template and continue the configuration
					instance.bindings.remove(name);
					if (instance.bindings.size() > 0) {
						Enumeration is = instance.bindings.elements();
						while (is.hasMoreElements()) {
							InstanceBinding child = (InstanceBinding)is.nextElement();
							if (child.state != InstanceBinding.STATE_RELEASING) {
								child.state = InstanceBinding.STATE_RELEASING;
								listeners.fireEvent(EVENT_BINDING_CHANGED, child);
								SystemID system = (SystemID)child.systems.elementAt(0);
								try {
									assembler.setTargetID(new ReferenceID(system, GDAssembler.ASSEMBLER_ID));
									assembler.release(id, phase, child.pointer);
								} catch (InvocationException e) {
									sendRemove(system, e);
									break;
								}								
							}
						}							
					} else {
						// no more children need to be notified, we can abort the instance
						// release all remaining resources and then we can try to configure
						// the request again on the local system, if this fails, we send
						// a notification
						Enumeration rs = instance.resources.elements();
						while (rs.hasMoreElements()) {
							releaseResource((Assembly)rs.nextElement());
						}
						instance.resources.clear();
						instance.templates.removeElementAt(0);
						// try to configure the next configuration
						resolveRequest(request);
					}
				}
			} else {
				// detect whether unsuccessful abort should be reported, it must be
				// reported if there are no more instances that need to be removed
				instance.bindings.remove(name);
				if (instance.bindings.size() == 0) {
					requests.remove(request.pointer);
					listeners.fireEvent(EVENT_REQUEST_REMOVED, request);
					sendReport(request, false);
				} else {
					// wait for remaining bindings to be removed
				}
			}
		}
	}
	
	/**
	 * Signals the request bound to the pointer that the request
	 * no longer needs to be configured. This will schedule 
	 * recursive release calls and it will release the resources
	 * bound to the instance currently configured for the request.
	 * 
	 * @param pointer The pointer that points to some incoming
	 * 	request that is currently resolved on this device.
	 */
	protected void releaseRequest(Pointer pointer) {
		InstanceRequest request = (InstanceRequest)requests.get(pointer);
		if (request == null) return;
		// signal ongoing removal
		request.required = false;
		listeners.fireEvent(EVENT_REQUEST_CHANGED, request);
		// release all resources of the currently configured instance
		Instance instance = (Instance)request.instances.elementAt(0);
		Enumeration r = (Enumeration)instance.resources.elements();
		while (r.hasMoreElements()) {
			// recursively release all resources defined in the assembly
			Assembly assembly = (Assembly)r.nextElement();
			releaseResource(assembly);
		}
		instance.resources.clear();
		if (instance.bindings.size() == 0) {
			// no remote de-configuration neccessary, report success
			sendReport(request, false);
		} else {
			// signal release requests to all instances that are configured or configuring
			Enumeration b = instance.bindings.elements();
			while (b.hasMoreElements()) {
				InstanceBinding binding = (InstanceBinding)b.nextElement();
				if (binding.state != InstanceBinding.STATE_RELEASING) {
					binding.state = InstanceBinding.STATE_RELEASING;
					listeners.fireEvent(EVENT_BINDING_CHANGED, binding);
					SystemID target = (SystemID)binding.systems.elementAt(0);
					assembler.setTargetID(new ReferenceID(target, GDAssembler.ASSEMBLER_ID));
					try {
						assembler.release(id, phase, binding.pointer);
					} catch (InvocationException e) {
						sendRemove(target, e);
						// shortcut due to soon-to-happen restart
						break;
					}
				}
			}
			// event processed, now wait for incoming reports			
		}
	}
	
	/**
	 * Creates a request on the local system for the specified pointer
	 * and parent system. The request satisfies the specified contract.
	 * The reuse flag determines whether the parent is reused and thus
	 * if possible the child should be reused.
	 * 
	 * @param pointer The pointer that denotes the position within the
	 * 	application that needs to be configured.
	 * @param system The parent system that will receive status reports
	 * 	about the configuration.
	 * @param contract The contract that should be satisfied in order
	 * 	to provide a suitable child for the parent component.
	 * @param parentReuse A boolean that indicates whether the parent
	 * 	is reused. If the parent has been reused, we try to reuse the
	 * 	child if it exists.
	 */
	protected void resolveRequest(Pointer pointer, SystemID system, Contract contract, boolean parentReuse) {
		InstanceRequest request = new InstanceRequest(pointer, system, contract);
		requests.put(pointer, request);
		if (pointer.length() == 0) {
			// handle the application anchor specifics
			AssemblyState state = (AssemblyState)states.get(pointer);
			if (state != null) {
				Vector contracts = state.getContracts();
				Vector copy = new Vector();
				for (int i = 0; i < contracts.size(); i++) {
					copy.addElement(contracts.elementAt(i));
				}
				Instance instance = new Instance(request, state.getCreatorID(), copy , true);
				request.instances.addElement(instance);				
			}
		} else if (contract != null){
			try {
				ObjectID oldCreator = null;
				if (parentReuse) {
					AssemblyState state = (AssemblyState)states.get(pointer);
					if (state != null) {
						oldCreator = state.getCreatorID();
					}
				}
				Vector demands = new Vector();
				demands.addElement(contract);
				Hashtable result = container.getTemplates(demands);
				Hashtable offers = (Hashtable)result.get(contract);
				if (offers != null) {
					Enumeration e = offers.keys();
					while (e.hasMoreElements()) {
						ObjectID creator = (ObjectID)e.nextElement();
						Vector templates = (Vector)offers.get(creator);
						boolean reuse = (oldCreator != null && oldCreator.equals(creator));
						Instance instance = new Instance(request, creator, templates, reuse);
						if (reuse) {
							request.instances.addElement(instance);
						} else {
							request.instances.insertElementAt(instance, 0);
						}
					}
				}
			} catch (InvocationException e) {
				Logging.error(getClass(), "Cannot contact local container.", e);
			}
		} else {
			// should not happen
			Logging.debug(getClass(), "Received a null contract with root pointer.");
		}
		listeners.fireEvent(EVENT_REQEUST_ADDED, request);
		resolveRequest(request);
	}
	
	/**
	 * Signals the application that the specified system should be
	 * removed. If the device is the master for the application, 
	 * all slave applications will be informed about the removal.
	 * On all devices, this will increase the counter and reset
	 * the internal state.
	 * 
	 * @param system The system that should be removed.
	 */
	protected void removeSystem(SystemID system) {
		// check whether this system participates in the configuration
		if (! systems.contains(system)) return;
		// remove the system from the system list
		systems.remove(system);
		// remove all state from the application.
		requests.clear();
		resources = null;
		// increase the application phase to supress old events
		phase += 1;
		if (master.equals(SystemID.SYSTEM)) {
			// inform all remaining participating systems about the removal
			for (int i = systems.size() - 1; i >= 0; i--) {
				SystemID device = (SystemID)systems.elementAt(i);
				if (! device.equals(SystemID.SYSTEM)) {
					try {
						assembler.setTargetID(new ReferenceID(device, GDAssembler.ASSEMBLER_ID));
						assembler.remove(getApplicationID(), system);	
					} catch (InvocationException e) {
						RemoveEvent event = new RemoveEvent(this, device);
						scheduleEvent(event);
					}
				}
			}
			try {
				// inform the removed device about its removal if possible
				assembler.setTargetID(new ReferenceID(system, GDAssembler.ASSEMBLER_ID));
				assembler.remove(id);	
			} catch (InvocationException e) {
				// nothing to do here, could happen often
			}
			// remove the lease for the device from the lease list
			for (int i = leases.size() - 1; i >= 0; i--) {
				Lease lease = (Lease)leases.elementAt(i);
				if (lease.getCreator().equals(system)) {
					leases.removeElementAt(i);
					registry.unhook(lease, this, false);
				}
			}			
		}
		listeners.fireEvent(EVENT_DEVICE_REMOVED, system);
		// add configuration request if configuration has been running
		if (! finished) {
			scheduleEvent(new ResolveEvent(this, phase, new Pointer(), SystemID.SYSTEM, null, true));
		}
	}
	
// helper method for instance resolution
	
	/**
	 * Resolves an instance request with the next possible instance and
	 * template and returns true if the configuration step was successful
	 * and false if the configuration step resulted in a failure. This
	 * method assumes that the next available instance is not configured
	 * in any way. If the instance can be configured completely local or
	 * if the instance cannot be configured, the parent will be notified
	 * about the current situation.
	 * 
	 * @param request The instance request to configure.
	 */
	private void resolveRequest(final InstanceRequest request) {
		while (! request.instances.isEmpty()) {
			final Instance instance = (Instance)request.instances.elementAt(0);
			templates: while (! instance.templates.isEmpty()) {
				Contract template = (Contract)instance.templates.elementAt(0);
				// try to configure all resources for the template
				Contract[] resources = template.getContracts(Contract.TYPE_RESOURCE_DEMAND);
				for (int i = 0; i < resources.length; i++) {
					Contract resource = resources[i];
					Pointer rpointer = new Pointer(request.pointer, false, resource.getName());
					Assembly assembly = resolveResource(rpointer, resource, instance.reuse);
					if (assembly != null) {
						// add the configured resource to the list of assemblies
						instance.resources.put(resource.getName(), assembly);
					} else {
						// release all previously configured assemblies
						Enumeration e = instance.resources.elements();
						while (e.hasMoreElements()) {
							releaseResource((Assembly)e.nextElement());
						}
						instance.resources.clear();
						// release the current template
						instance.templates.removeElementAt(0);
						continue templates;
					}
				}
				listeners.fireEvent(EVENT_REQUEST_CHANGED, request);
				// try to resolve all instance bindings
				final Contract[] instances = template.getContracts(Contract.TYPE_INSTANCE_DEMAND);
				if (instances.length == 0) {
					// no instances to configure, immediately report success
					sendReport(request, true);
					return;
				} else {
					// send resolve request for required instances
					Random random = new Random();
					for (int i = 0; i < instances.length; i++) {
						final Contract idemand = instances[i];
						final Pointer ipointer = new Pointer(request.pointer, true, idemand.getName());
						final InstanceBinding binding = new InstanceBinding(instance, ipointer, idemand);
						AssemblyState state = (AssemblyState)states.get(request.pointer);
						int shift = 0;
						for (int j = 0; j < systems.size(); j++) {
							SystemID system = (SystemID)systems.elementAt(j);
							if (instance.reuse && state != null && system.equals(state.getInstance(idemand.getName()))) {
								binding.systems.insertElementAt(system, 0);
								shift = 1;
							} else {
								if (binding.systems.size() - shift > 0) {
									int idx = random.nextInt(binding.systems.size() - shift);
									idx += shift;
									binding.systems.insertElementAt(system, idx);											
								} else {
									binding.systems.addElement(system);
								}
							}
						}
						binding.state = InstanceBinding.STATE_RESOLVING;
						instance.bindings.put(idemand.getName(), binding);
						listeners.fireEvent(EVENT_BINDING_CHANGED, binding);
						SystemID target = (SystemID)binding.systems.elementAt(0);
						assembler.setTargetID(new ReferenceID(target, GDAssembler.ASSEMBLER_ID));
						try {
							assembler.resolve(id, phase, ipointer, SystemID.SYSTEM, idemand, instance.reuse);
						} catch (InvocationException e) {
							sendRemove(target, e);
							// shortcut due to a soon-to-happen restart
							return;
						}			
					}
					// we are done here, lets get out of here
					return;
				}
			}
			// all templates have been failed during resource allocation
			request.instances.removeElementAt(0);
			listeners.fireEvent(EVENT_REQUEST_CHANGED, request);
		}
		// no more instances to configure, remove request and report unsuccess
		requests.remove(request.pointer);
		listeners.fireEvent(EVENT_REQUEST_REMOVED, request);
		sendReport(request, false);
	}	
	
// helper methods for local resource resolution
	
	/**
	 * Tries to resolve the specified resource demand recursively and
	 * returns an assembly if suceessful or null if unsuccessful. 
	 * 
	 * @param pointer The pointer that denotes the position of the
	 * 	resource within the algorithm.
	 * @param demand The demand that needs to be resolved.
	 * @param reuse True if the parent is reused and thus, the 
	 * 	resource might be reused to, false otherwise.
	 * @return The assembly for the complete resource tree if the
	 * 	resources can be reserved or null if the resource cannot
	 * 	be reserved on this device.
	 */
	private Assembly resolveResource(Pointer pointer, Contract demand, boolean reuse) {
		try {
			// initialize the creator id and element id if the resource
			// can be reused under certain circumstances
			ObjectID reuseID = null;
			ObjectID elementID = null;
			if (reuse) {
				AssemblyState state = (AssemblyState)states.get(pointer);
				if (state != null) {
					reuseID = state.getCreatorID();
					elementID = state.getElementID();
				}
			}
			// retrieve the offers from the local container
			Vector demands = new Vector();
			demands.addElement(demand);
			Hashtable result = container.getTemplates(demands);
			Hashtable offers = (Hashtable)result.get(demand);
			if (offers != null) {
				// if there are offers, prepare a potential result assembly
				Assembly assembly = new Assembly();
				assembly.setContainerID(Container.CONTAINER_ID);
				assembly.setSystemID(SystemID.SYSTEM);
				assembly.setName(demand.getName());
				Enumeration cs = offers.keys();
				// if there is a chance that this resource can be reused,
				// sort the results in such a way that reuse is the first
				// option to try, else do not change the ordering
				if (reuseID != null) {
					Vector sorting = new Vector();
					while (cs.hasMoreElements()) {
						ObjectID cid = (ObjectID)cs.nextElement();
						if (reuseID.equals(cid)) {
							sorting.insertElementAt(cid, 0);
						} else {
							sorting.addElement(cid);
						}
					}
					cs = sorting.elements();
				}
				// iterate over creators and try to reserve the resources
				while (cs.hasMoreElements()) {
					ObjectID creatorID = (ObjectID)cs.nextElement();
					Vector templates = (Vector)offers.get(creatorID);
					boolean rreuse = (reuseID != null && creatorID.equals(reuseID));
					// adjust the assembly depending on the current creator 
					assembly.setCreatorID(creatorID);
					if (rreuse) {
						assembly.setElementID(elementID);
					} else {
						assembly.setElementID(null);
					}
					// try all possible templates
					templates: for (int i = 0; i < templates.size(); i++) {
						Contract template = (Contract)templates.elementAt(i);
						assembly.setTemplate(template);
						// first reserve the resource itself
						if (reserveResources(creatorID, template)) {
							Contract[] resources = template.getContracts(Contract.TYPE_RESOURCE_DEMAND);
							// ... then try to reserve its children
							for (int j = 0; j < resources.length; j++) {
								Contract rdemand = resources[j];
								String rname = rdemand.getName();
								Pointer rpointer = new Pointer(pointer, false, rname);
								Assembly rassembly = resolveResource(rpointer, rdemand, rreuse);
								if (rassembly != null) {
									// child reserved successfully, continue
									assembly.addResource(rassembly);
								} else {
									// child can not be resolved properly, try next
									// template of this creator
									releaseResource(assembly);
									continue templates;
								}
							}	
							// all children have been resolved, return result
							return assembly;
						}
						// initial reservation failed already
					}
					// no more templates from this creator, try next creator
				}
				// no more creators, reservation failed completely, 
				// return failure at end of method
			}
		} catch (InvocationException e) {
			Logging.error(getClass(), "Cannot contact local container", e);
		}
		return null;
	}
	
	/**
	 * Recursively releases the resources that are bound to the
	 * assembly including the resources that are bound to the
	 * assembly itself.
	 * 
	 * @param assembly The assembly whose resources should be
	 * 	released.
	 */
	private void releaseResource(Assembly assembly) {
		Vector assemblies = new Vector();
		assemblies.addElement(assembly);
		while (! assemblies.isEmpty()) {
			Assembly remove = (Assembly)assemblies.elementAt(0);
			assemblies.removeElementAt(0);
			// release the assembly itself
			Contract template = remove.getTemplate();
			ObjectID creatorID = remove.getCreatorID();
			releaseResources(creatorID, template);
			// add its children to the removal list
			Assembly[] children = remove.getResources();
			for (int i = children.length - 1; i >= 0; i--) {
				assemblies.addElement(children[i]);
			}
		}
	}

	/**
	 * Handles a communcation failure by sending a remove message
	 * for the device that failed to the master device.
	 * 
	 * @param system The system that could not be contacted.
	 * @param e The exception that occured while contacting the
	 * 	system.
	 */
	private void sendRemove(SystemID system, InvocationException e) {
		if (! system.equals(master)) {
			Logging.error(getClass(), "Could not contact slave assembler on " + system + ".", e);
			assembler.setTargetID(new ReferenceID(master, GDAssembler.ASSEMBLER_ID));
			try {
				assembler.remove(id, system);
			} catch (InvocationException me) {
				Logging.error(getClass(), "Could not contact master waiting for lease timeout.", e);				
			}
		} else {
			Logging.error(getClass(), "Could not contact master assembler on " + system + ".", e);
		}
	}
	
// helper methods for the communication subsystem
	
	/**
	 * Reports the successful configuration or the unsuccessful configuration
	 * of a request to its parent.
	 * 
	 * @param request The request that has been configured successfully or
	 * 	unsuccessfully.
	 * @param success The success flag that determines whether the configuration
	 * 	was successful or unsuccessful.
	 */
	private void sendReport(InstanceRequest request, boolean success) {
		try {
			assembler.setTargetID(new ReferenceID(request.system, GDAssembler.ASSEMBLER_ID));
			Assembly assembly = null;
			if (success & request.pointer.length() > 0) {
				// create an assembly whose references point to me
				assembly = prepareAssembly(request);
			} 
			assembler.report(id, phase, request.pointer, assembly);
		} catch (InvocationException e) {
			sendRemove(request.system, e);
		}		
	}
	
	/**
	 * Reserves the specified amount of resources from the specified
	 * creator on this device.
	 * 
	 * @param creatorID The creator id. This must denote a resource
	 * 	allocator id of some allocator present on the system represented
	 *  by this device object.
	 * @param c The contract (a resource template) that describes the
	 * 	amount of resources needed from the allocator.
	 * @return True if the amount of resources can be reserved, false
	 * 	if the amount is not available. Note that this method will 
	 * 	globally allocate the resources only if the reservation is
	 * 	successful. If it is not successful, the available resources
	 * 	will not be changed.
	 */
	private boolean reserveResources(ObjectID creatorID, Contract c) {
		if (resources == null) {
			try {
				resources = container.getResources();	
			} catch (InvocationException e) { 
				Logging.error(getClass(), "Cannot contact local container.", e);
				return false;
			}
		}
		if (c.getType() != Contract.TYPE_RESOURCE_TEMPLATE) {
			return false;
		} else {
			int[] estimate = (int[])c.getAttribute(Contract.ATTRIBUTE_RESOURCE_ESTIMATE);
			if (estimate == null) return false;
			int[] free = (int[])resources.get(creatorID);
			if (free == null) return false;
			if (estimate.length != free.length) return false;
			for (int i = 0; i < estimate.length; i++) {
				free[i] -= estimate[i];
				if (free[i] < 0) {
					for (int j = 0; j <= i; j++) {
						free[j] += estimate[j];
					}
					return false;
				}
			}
			return true;
		}
	}

// helper method to create assemblies
	
	/**
	 * Prepares the assembly for a single request. The assembly will
	 * only contain pointers to the local assembler. The data must
	 * be retrieved lazily.
	 * 
	 * @param request The request to create an assembly.
	 * @return The assembly for the specified request.
	 */
	private Assembly prepareAssembly(InstanceRequest request) {
		Pointer pointer = request.pointer;
		Instance instance = (Instance)request.instances.elementAt(0);
		Assembly assembly = new Assembly();
		if (pointer.length() == 0) {
			assembly.setContainerID(IApplicationManager.APPLICATION_MANAGER_ID);	
		} else {
			assembly.setContainerID(IContainer.CONTAINER_ID);
			assembly.setName(request.contract.getName());
		}
		assembly.setCreatorID(instance.creator);
		if (instance.reuse) {
			AssemblyState state = (AssemblyState)states.get(pointer);
			if (state != null) {
				assembly.setElementID(state.getElementID());	
			}
		}
		assembly.setSystemID(SystemID.SYSTEM);
		assembly.setTemplate((Contract)instance.templates.elementAt(0));
		Enumeration bs = instance.bindings.keys();
		while (bs.hasMoreElements()) {
			String name = (String)bs.nextElement();
			AssemblyPointer bpointer = new AssemblyPointer(id);
			bpointer.setAssemblerID(new ReferenceID(SystemID.SYSTEM, GDAssembler.ASSEMBLER_ID));
			bpointer.setName(name);
			bpointer.setVariableID(new Pointer(pointer, true, name));
			assembly.addInstance(bpointer);
		}
		Enumeration rs = instance.resources.keys();
		while (rs.hasMoreElements()) {
			String name = (String)rs.nextElement();
			AssemblyPointer rpointer = new AssemblyPointer(id);
			rpointer.setAssemblerID(new ReferenceID(SystemID.SYSTEM, GDAssembler.ASSEMBLER_ID));
			rpointer.setName(name);
			rpointer.setVariableID(new Pointer(pointer, false, name));
			assembly.addResource(rpointer);
		}
		return assembly;
	}


// helper methods to perform resource reservations

	/**
	 * Releases the resources for a previously reserved contract. Note
	 * that this method does not check whether the resources have been
	 * reserved at all. Thus, calling this method multiple times will
	 * lead to unnaturally increased available resources on the device.
	 * If this happens it is likely that the assembler will return
	 * invalid assemblies as result.
	 * 
	 * @param creatorID The creator that has provided the resources.
	 * @param c The contract that should be released.
	 */
	private void releaseResources(ObjectID creatorID, Contract c) {
		int[] estimate = (int[])c.getAttribute(Contract.ATTRIBUTE_RESOURCE_ESTIMATE);
		if (estimate == null) return;
		int[] free = (int[])resources.get(creatorID);
		if (free == null) return;
		if (estimate.length != free.length) return;
		for (int i = 0; i < estimate.length; i++) {
			free[i] += estimate[i];
		}
	}
	
}

