package info.pppc.pcom.system.application;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseListener;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.NullMonitor;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.AssemblerProxy;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.contract.Contract;

import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is used internally by the application manager. It represents
 * an application.
 * 
 * @author Mac
 */
public class Application implements IOperation {

	/**
	 * The time (in ms) that the application thread will wait after an unsuccessful
	 * assembly attempt. 
	 */
	private static final int TIMEOUT_ASSEMBLY = 10000;
	
	/**
	 * The number of consecutive unsuccessful reconfiguration attempts that the
	 * application thread will tolerate until it gives up and stops the process.
	 */
	private static final int RETRY_ASSEMBLY = 3;
	
	/**
	 * The application manager that has created the application.
	 */
	private final ApplicationManager manager;
	
	/**
	 * The object id of the application that identifies the application
	 * uniquely.
	 */
	private final ObjectID identifier = ObjectID.create();
	
	/**
	 * The monitor of the application's configuration thread. Whenever
	 * the application thread has finished a stage, it notifies this
	 * monitor. All synched operations use this monitor
	 */
	private NullMonitor monitor = new NullMonitor();

	/**
	 * The preferences that must be met by the application at runtime.
	 */
	private Vector preferences = new Vector();

	/**
	 * The id of the assembler that initially configures and adapts
	 * the application.
	 */
	private ReferenceID assemblerID = null;
	
	/**
	 * The (human-readable) name of the application (ui only).
	 */
	private String name = null;
	
	/**
	 * The image used to represent the applicaiton (ui only).
	 */
	private byte[] image = null;
	
	/**
	 * A vector of the current successors of the application.
	 */
	private Vector bindings = new Vector(); 

	/**
	 * A hashtable that hashes binding names stated in contracts to
	 * their corresponding instance checkpoints.
	 */
	private Hashtable checkpoints = new Hashtable();
	
	/**
	 * The contract that determines the preference level that is
	 * currently executed.
	 */
	private Contract contract;
	
	/**
	 * The state of the application. This will be one of the state
	 * constants defined in the application descriptor.
	 */
	private int state = ApplicationDescriptor.STATE_APPLICATION_STOPPED;
	
	/**
	 * The current phase. This phase is increased whenver an application
	 * is paused or tried to be started.
	 */
	private int phase = 0;
	
	/**
	 * Creates an application using the specified manager from the
	 * specified application descriptor.
	 * 
	 * @param manager The manager of the application.
	 * @param descriptor The application descriptor that should be used
	 * 	as initialization.
	 */
	protected Application(ApplicationManager manager, ApplicationDescriptor descriptor) {
		this.manager = manager;
		monitor.done();
		if (descriptor.getPreferences() == null || descriptor.getPreferences().size() == 0) {
			throw new IllegalArgumentException("Illegal descriptor.");
		}
		// initialize the name, image, assembler id and preferences
		this.name = descriptor.getName();
		this.image = descriptor.getImage();
		this.assemblerID = descriptor.getAssemblerID();
		this.preferences = descriptor.getPreferences();
	}

// internal methods
	
	/**
	 * Increments the phase. This method must be called before the
	 * next wave containing configuration data is flooded to the
	 * children of the application. I.e., whenever an application
	 * is started, stopped or paused.
	 */
	private void incrementPhase() {
		phase += 1;
	}
	
	/**
	 * Returns the application binding with the specified name or
	 * null if it does not exist.
	 * 
	 * @param name The application binding with the specified name.
	 * @return The binding with the specified name or null if it
	 * 	does not exist.
	 */
	private ApplicationBinding getBinding(String name) {
		for (int i = 0; i < bindings.size(); i++) {
			ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(i);
			if (binding.getName().equals(name)) {
				return binding;
			}
		}
		return null;
	}
	
	/**
	 * Configures or reconfigures the application. This method should only
	 * be called if the state of the application is broken.
	 */
	private void start() {
		try {
			// first increase the phase so that no more events will be processed 
			Logging.debug(getClass(), "Configure called for application " + identifier + ".");
			incrementPhase(); // increment phase for next pause call
			// prepare the assembler for reconfiguration
			if (assemblerID == null) return;
			AssemblerProxy assembler = new AssemblerProxy();
			assembler.setSourceID(new ReferenceID(SystemID.SYSTEM, IApplicationManager.APPLICATION_MANAGER_ID));
			assembler.setTargetID(assemblerID);
			Logging.debug(getClass(), "Preparing assembler " + assemblerID + " for application " + identifier + ".");
			Lease lease = assembler.prepare(new ReferenceID(SystemID.SYSTEM, identifier));
			// add the lease to the lease registry with deferred notification
			LeaseListener leaseListener = new LeaseListener();
			LeaseRegistry registry = manager.getLeaseRegistry();
			registry.hook(lease, leaseListener);
			ReferenceID applicationID = new ReferenceID(SystemID.SYSTEM, identifier);
			try {
				// setup the preferences and initialize all possible running instances
				Logging.debug(getClass(), "Initializing assembler with application " + identifier + ".");
				AssemblyState assemblerSetup = new AssemblyState();
				assemblerSetup.setContracts(preferences);
				assemblerSetup.setCreatorID(ObjectID.create());
				assemblerSetup.setElementID(ObjectID.create());
				assemblerSetup.setName(name);
				assemblerSetup.setSystemID(SystemID.SYSTEM);
				for (int i = 0; i < bindings.size(); i++) {
					ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(i);
					binding.prepare(assemblerSetup);
				}
				AssemblyPointer assemblerContext = assembler.setup(applicationID, assemblerSetup);
				// initialize all existing component instances
				AssemblyPointer[] instances = assemblerContext.getInstances();  
				for (int i = 0; i < instances.length; i++) {
					// retrieve matching context
					AssemblyPointer successorContext = instances[i];
					for (int j = 0; j < bindings.size(); j++) {
						ApplicationBinding s = (ApplicationBinding)bindings.elementAt(j);
						if (s.getName().equals(successorContext.getName())) {
							s.pause(successorContext);
							break;
						}
					}
				}
				// configure the application
				try {
					Logging.debug(getClass(), "Starting assembly process for application " + identifier + ".");
					long start = System.currentTimeMillis();
					Assembly assembly = assembler.configure(applicationID);
					long stop = System.currentTimeMillis();
					Logging.debug(getClass(), "Assembly process finished " + (assembly != null?"successfully":"unsuccessfully") 
						+ " for application " + identifier + " in " + (stop - start) + "ms.");
					incrementPhase(); // increment phase for next start call
					if (assembly != null) {
						Logging.debug(getClass(), "Applying new assembly to application " + identifier + ".");
						// set the configured preference level to the level contained in the assembly
						contract = assembly.getTemplate();
						// resolve all context objects from assembler to determine nodes
						Assembly[] assemblies = assembly.getInstances();
						// remove all unused instances, before the new ones are started
						bindings: for (int j = bindings.size() - 1; j >= 0 ; j--) {
							ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(j);
							for (int i = 0; i < assemblies.length; i++) {
								Assembly a = assemblies[i];
								if (binding.getName().equals(a.getName())) {
									Logging.debug(getClass(), "Reusing binding " + binding.getName());
									continue bindings;
								}
							}
							// remove this successor as it is not part of the new configuration
							Logging.debug(getClass(), "Removing binding " + binding.getName() + ".");
							binding.stop(true);
							bindings.removeElementAt(j);
						}
						// start the new configuration as defined by the assembler
						for (int i = 0; i < assemblies.length; i++) {
							Assembly a = assemblies[i];
							Logging.debug(getClass(), "Starting binding " + a.getName() + ".");
							ApplicationBinding binding = getBinding(a.getName());
							if (binding == null) {
								binding = new ApplicationBinding(this, a.getName());
								bindings.addElement(binding);
							}
							binding.start(a);
						}
						// check the bindings and determine whether configuration is complete
						for (int i = 0; i < bindings.size(); i++) {
							ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(i);
							if (! binding.isValid()) {
								Logging.debug(getClass(), "Could not resolve binding " + binding.getName());
								return;
							} 
						}
						// set the application's state to started if any preference level has been
						// configured
						state = ApplicationDescriptor.STATE_APPLICATION_STARTED;
						manager.fireApplicationEvent(ApplicationManager.EVENT_APPLICATION_STARTED, getDescriptor());
					} else {
						// set the configured preference level to null
						contract = null;
					}
				} catch (InvocationException e) {
					Logging.error(getClass(), "Could not configure the application " + identifier + ".", e);
				} finally {
					registry.unhook(lease, leaseListener, false);
					try {
						assembler.remove(applicationID);
					} catch (InvocationException e2) {
						Logging.error(getClass(), "Could not release application " + identifier + " state from assembler " + assemblerID + ".", e2);
					}
				}
			} catch (InvocationException e) {
				Logging.error(getClass(), "Could not initialize assembler with application " + identifier + ".", e);
				registry.unhook(lease, leaseListener, false);
			}
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not prepare assembler for application " + identifier + ".", e);
		}
	}
	
	/**
	 * Stops the application and releases all successors that are bound to it.
	 * This method should only be called if the state of the application is 
	 * stopping.
	 */
	private void stop() {
		Logging.debug(getClass(), "Stopping application " + identifier + ".");
		incrementPhase(); // increment phase for next stop call
		for (int i = bindings.size() - 1; i >= 0; i--) {
			ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(i);
			binding.stop(true);
			bindings.removeElementAt(i);
		}
		contract = null;
		state = ApplicationDescriptor.STATE_APPLICATION_STOPPED;
		manager.fireApplicationEvent(ApplicationManager.EVENT_APPLICATION_STOPPED, getDescriptor());
	}

// methods used by bindings	
	
	/**
	 * Returns the current phase of the application. The phase is used
	 * to detect outdated messages. Messages are outdated if the phase
	 * of the application does not meet the phase of the message. 
	 * 
	 * @return The phase of the application.
	 */
	protected int getPhase() {
		synchronized (monitor) {
			return phase;	
		}
	}
	
	/**
	 * Returns the application manager for the application.
	 * 
	 * @return The application manager of the application.
	 */
	protected ApplicationManager getManager() {
		return manager;
	}
	
	/**
	 * Returns the demand with the specified name or null if the demand
	 * is not specified.
	 * 
	 * @param name The name of the demand.
	 * @return The demand or null if this is not specified.
	 */
	protected Contract getDemand(String name) {
		if (contract == null) return null;
		else return contract.getContract(Contract.TYPE_INSTANCE_DEMAND, name);
	}
	
	/**
	 * Returns the locally unique identifier of the application.
	 * 
	 * @return The locally unique identifier of the application.
	 */
	protected ObjectID getIdentifier() {
		return identifier;
	}
	
// methods used by the application manager
	
	/**
	 * Updates the state of the application using the specified descriptor.
	 * 
	 * @param descriptor The descriptor that contains the preferences, name, etc.
	 */
	protected void updateApplication(ApplicationDescriptor descriptor) {
		if (descriptor.getPreferences() == null || descriptor.getPreferences().size() == 0) {
			throw new IllegalArgumentException("Illegal descriptor.");
		}
		synchronized (monitor) {
			// a flag that indicates whether the configuration thread needs to be updated
			boolean update = false;
			// update name and image but ignore changes
			this.name = descriptor.getName();
			this.image = descriptor.getImage();
			// update assembler id  and determine whether change has occured
			ReferenceID newID = descriptor.getAssemblerID();
			if (newID != assemblerID && newID != null && ! newID.equals(assemblerID)) {
				assemblerID = newID;
				update = true;
			} 
			// update preferences and determine whether change has occured
			Vector p = descriptor.getPreferences();
			boolean changed = false;
			if (p.size() == preferences.size()) {
				for (int i = 0; i < p.size(); i++) {
					Contract p1 = (Contract)p.elementAt(i);
					Contract p2 = (Contract)preferences.elementAt(i);
					if (! p1.equals(p2)) {
						changed = true;
						break;
					}
				}
			} else {
				changed = true;
			}
			if (changed) {
				preferences = descriptor.getPreferences();
				update = true;
			}
			if (update) {
				changeApplication();
			}
		}
		manager.fireApplicationEvent(ApplicationManager.EVENT_APPLICATION_CHANGED, getDescriptor());
	}
	
	
	/**
	 * Called to reconfigure a running application. If an application is currently
	 * running and this method is called, the application will be reconfigured. If
	 * the application is paused or stopped, calling this method will not do anything.
	 */
	protected void changeApplication() {
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED) {
				contract = null;
				state = ApplicationDescriptor.STATE_APPLICATION_PAUSED;
				manager.fireApplicationEvent(ApplicationManager.EVENT_APPLICATION_PAUSED, getDescriptor());
				monitor.notifyAll();
			}
		}
	}

	/**
	 * Starts the application's lifecycle maintaining thread.
	 */
	protected void startApplication() {
		synchronized (monitor) {
			if (monitor.isDone()) {
				monitor = new NullMonitor();
				InvocationBroker broker = InvocationBroker.getInstance();
				broker.performOperation(this, monitor);
			}			
		}
	}

	
	/**
	 * Forces the application to stop.
	 */
	protected void exitApplication() {
		synchronized (monitor) {
			if (! monitor.isCanceled()) {
				monitor.cancel();
				monitor.notifyAll();
				while (! monitor.isDone()) {
					try {
						monitor.join();	
					} catch (InterruptedException e) {
						Logging.error(getClass(), "Thread got interrupted.", e);
					}
				}
			}			
		}
	}
	
	/**
	 * Creates checkpoints for all bindings attached to an application.
	 */
	protected void saveApplication() {
		// step through each binding and create a checkpoint.
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED) {
				for (int i = 0; i < bindings.size(); i++) {
					ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(i);
					InstanceCheckpoint checkpoint = binding.store();
					if (checkpoint != null) {
						if (checkpoint.isComplete()) {
							checkpoints.put(binding.getName(), checkpoint);
						} else {
							InstanceCheckpoint old = (InstanceCheckpoint)checkpoints.get(binding.getName());
							if (old != null) {
								checkpoint.apply(old);
							} else {
								checkpoint.setComplete(true);
								checkpoints.put(binding.getName(), checkpoint);
							}
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * Loads the latest application state stored in the checkpoints.
	 */
	protected void loadApplication() {
		// step through each binding and create a checkpoint.
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED) {
				// step through each binding and load the latest checkpoint
				for (int i = 0; i < bindings.size(); i++) {
					ApplicationBinding binding = (ApplicationBinding)bindings.elementAt(i);
					InstanceCheckpoint checkpoint = (InstanceCheckpoint)checkpoints.get(binding.getName());
					if (checkpoint != null) {
						binding.load(checkpoint);
					}
				}
			}
		}
	}
	
	/**
	 * Returns an application descriptor that contains the current state of
	 * the application.
	 * 
	 * @return The descriptor that describes the current state of the 
	 * 	application.
	 */
	protected ApplicationDescriptor getDescriptor() {
		synchronized (monitor) {
			ApplicationDescriptor descriptor = new ApplicationDescriptor();
			descriptor.setApplicationID(identifier);
			descriptor.setAssemblerID(assemblerID);
			descriptor.setImage(image);
			descriptor.setName(name);
			descriptor.setState(state);
			if (contract != null) descriptor.setStatus(contract.copy());	
			Vector copiedPreferences = new Vector();
			for (int i = 0; i < preferences.size(); i++) {
				Contract pref = (Contract)preferences.elementAt(i);
				copiedPreferences.addElement(pref.copy());
			}
			descriptor.setPreferences(copiedPreferences);
			return descriptor;			
		}
	}

	
// signals that are forwarded by the manager
	
	/**
	 * This method is called by the manager to signal that there was an incoming
	 * change instance request.
	 * 
	 * @param name The name of the successor.
	 * @param provision The new provision.
	 * @param phase The phase used to detect outdated messages.
	 */
	protected void changeInstance(String name, int phase, Contract provision) {
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED && phase == this.phase) {
				ApplicationBinding binding = getBinding(name);
				if (binding != null) {
					// update the binding and check whether recomputation is neccessary
					binding.setProvision(provision);
					if (! binding.isValid()) {
						contract = null;
						state = ApplicationDescriptor.STATE_APPLICATION_PAUSED;
						manager.fireApplicationEvent
							(ApplicationManager.EVENT_APPLICATION_PAUSED, getDescriptor());
						monitor.notifyAll();
					}
				}
			}
		}
	}

	/**
	 * This method is part of the predecessor interface. It is called to signal that a
	 * successor needs to be replaced.
	 * 
	 * @param name The name of the successor.
	 * @param phase The phase used to detect outdated messages.
	 */
	protected void removeInstance(String name, int phase) {
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED && phase == this.phase) {
				ApplicationBinding binding = getBinding(name);
				if (binding != null) {
					binding.stop(false);
					contract = null;
					state = ApplicationDescriptor.STATE_APPLICATION_PAUSED;
					manager.fireApplicationEvent
						(ApplicationManager.EVENT_APPLICATION_PAUSED, getDescriptor());
					monitor.notifyAll();
				}
			}
		}
	}
	
	/**
	 * This method is part of the predecessor interface. It is called to signal that the
	 * application of a successor should be stopped.
	 * 
	 * @param name The name of the successor.
	 * @param phase The phase used to detect outdated messages.
	 */
	protected void exitApplication(String name, int phase) {
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED && phase == this.phase) {
				ApplicationBinding binding = getBinding(name);
				if (binding != null) {
					exitApplication();
				}
			}
		}		
	}

	/**
	 * This method is part of the predecessor interface. It is called to signal that the
	 * application of a successor should be saved.
	 * 
	 * @param name The name of the successor.
	 * @param phase The phase used to detect outdated messages.
	 */
	protected void saveApplication(String name, int phase) {
		synchronized (monitor) {
			if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED && phase == this.phase) {
				ApplicationBinding binding = getBinding(name);
				if (binding != null) saveApplication();
			}
		}		
	}
	
	
// the lifecycle thread of an application (started with start/exit application)

	/**
	 * This method is part of the operation interface. It ensures that the
	 * application is running properly and that state changes are performed
	 * sequentially.
	 * 
	 * @param monitor The monitor used to cancel the application.
	 * @throws Exception Thrown if anything fails.
	 */
	public void perform(IMonitor monitor) throws Exception {
		// the number of unsuccessful configuration retries
		int retries = 0;
		// initialize the application
		state = ApplicationDescriptor.STATE_APPLICATION_PAUSED;
		manager.fireApplicationEvent(ApplicationManager.EVENT_APPLICATION_PAUSED, getDescriptor());
		// reconfigure the application as long as it is running
		synchronized (monitor) {
			while (! monitor.isCanceled()) {
				// stop application if it cannot be configured multiple times
				if (retries >= RETRY_ASSEMBLY) break;
				// re-configuration/configuration cycle
				if (state == ApplicationDescriptor.STATE_APPLICATION_PAUSED) {
					retries += 1; // increase the number of retries
					start();
					if (state != ApplicationDescriptor.STATE_APPLICATION_STARTED) {
						try {
							monitor.wait(TIMEOUT_ASSEMBLY);	
						} catch (InterruptedException e) {
							Logging.error(getClass(), "Thread got interrupted.", e);
						}
					}
					continue;
				} else if (state == ApplicationDescriptor.STATE_APPLICATION_STARTED) {
					retries = 0; // reset the retries since startup was success
					try {
						monitor.wait();	
					} catch (InterruptedException e) {
						Logging.error(getClass(), "Thread got interrupted.", e);
					}
				} 
			}
			stop();
			monitor.notifyAll();
		}	
	}


}
