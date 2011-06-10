package info.pppc.pcom.system.application;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.ContainerProxy;
import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.container.InstanceSetup;
import info.pppc.pcom.system.container.InstanceState;
import info.pppc.pcom.system.contract.Contract;

/**
 * The application binding is used internally by the application manager.
 * It represents a single instance that is bound to an application.
 * 
 * @author Mac
 */
public class ApplicationBinding implements IListener {

	/**
	 * The application that uses this binding.
	 */
	private final Application application;
	
	/**
	 * The (immutable) unique name of the binding.
	 */
	private final String name;
	
	/**
	 * The lease of the successor bound to this successor. The
	 * lease is returned whenever the successor is created.
	 */
	private Lease lease;
	
	/**
	 * The id of the container that hosts the successor.
	 */
	private ObjectID containerID;
	
	/**
	 * The id of the successor instance that is used to 
	 * address the successor in the container. 
	 */
	private ObjectID instanceID;
	
	/**
	 * The provision of the successor as stated in the
	 * last update.
	 */
	private Contract provision;
	
	/**
	 * The system id of the successor.
	 */
	private SystemID systemID;
	
	/**
	 * The phase at which the binding is currently running.
	 * The phase is always synced if the binding has been
	 * prepared completely for the next phase.
	 */
	private int phase;
	
	/**
	 * Creates a new application binding for the specified applicaiton
	 * with the specified name.
	 * 
	 * @param application The application that uses the binding.
	 * @param name The (immutable) name of the binding.
	 */
	protected ApplicationBinding(Application application, String name) {
		this.application = application;
		this.name = name;
	}

	/**
	 * Returns the application that uses this binding.
	 * 
	 * @return The appliction that uses the binding.
	 */
	protected Application getApplication() {
		return application;
	}
	
	/**
	 * Returns the name of the application binding as specified by the
	 * parent.
	 * 
	 * @return The name of the binding as specified by the parent.
	 */
	protected String getName() {
		return name;
	}
	
	/**
	 * Sets the provision of the binding to the specified value.
	 * 
	 * @param provision The provision of the binding.
	 */
	protected void setProvision(Contract provision) {
		this.provision = provision;
	}
	
	/**
	 * Prepares the assembly state of the application by initializing
	 * it with the internal state of the binding.
	 * 
	 * @param state The assembly state of the application that needs
	 * 	to be prepared.
	 */
	protected void prepare(AssemblyState state) {
		if (isBound()) {
			state.putInstance(name, systemID);
		}
	}
	
	/**
	 * Configures the binding using the specified assembly.
	 * 
	 * @param assembly The assembly that describes the configuration.
	 */
	protected void start(Assembly assembly) {
		// determine whether the assembly wants to reuse the current binding
		boolean reusing = (assembly.getElementID() != null);
		if (! reusing) {
			// no reuse, so stop if bound
			stop(true);
		}
		// setup the local stuff as far as possible and create the setup
		containerID = assembly.getContainerID();
		systemID = assembly.getSystemID();
		Contract demand = application.getDemand(name);
		ObjectID parentID = application.getIdentifier();
		ObjectID collectorID = ObjectID.create();
		SystemID parentSystemID = SystemID.SYSTEM;
		// send the setup to the container and configure the binding
		try {
			InstanceSetup setup = new InstanceSetup
				(demand, parentID, collectorID, IApplicationManager.APPLICATION_MANAGER_ID, parentSystemID, assembly);
			ContainerProxy container = new ContainerProxy();
			container.setSourceID(new ReferenceID(SystemID.SYSTEM, IApplicationManager.APPLICATION_MANAGER_ID));
			container.setTargetID(new ReferenceID(systemID, containerID));
			InstanceState state = container.startInstance(setup, application.getPhase());
			// if new binding, update lease
			if (state.isBound() && !reusing) {
				LeaseRegistry registry = application.getManager().getLeaseRegistry();
				lease = state.getLease();
				instanceID = state.getInstanceID();
				registry.hook(lease, this);
			} else if (! state.isBound()) {
				stop(false);
			}
			// if success, update provision
			if (state.isSuccess()) {
				provision = state.getProvision();
			}
		} catch (InvocationException e) {
			Logging.debug(getClass(), "Could not configure binding " + name + ".");
			stop(true);
		}
		// increase the phase since the binding's configuration is done
		phase = application.getPhase();
	}
	
	/**
	 * Pauses the binding using the specified pointer.
	 * 
	 * @param pointer The pointer that denotes what needs to be configured.
	 */
	protected void pause(AssemblyPointer pointer) {
		try {
			ContainerProxy container = new ContainerProxy();
			container.setSourceID(new ReferenceID(SystemID.SYSTEM, IApplicationManager.APPLICATION_MANAGER_ID));
			container.setTargetID(new ReferenceID(systemID, containerID));
			container.pauseInstance(instanceID, pointer, application.getPhase());
		} catch (InvocationException e) {
			Logging.error(getClass(), "Pausing binding failed, stopping.", e);
			stop(false);
		}
		// increase the phase since the binding's configuration is done
		phase = application.getPhase();
	}
	
	/**
	 * Stops the binding and releases all state that might remain.
	 * 
	 * @param notify A flag that indicates whether the child bound to the
	 * 	binding should be notified.
	 */
	protected void stop(boolean notify) {
		if (isBound()) {
			LeaseRegistry registry = application.getManager().getLeaseRegistry();
			registry.unhook(lease, this, false);
			lease = null;
			if (notify) {
				try {
					ContainerProxy container = new ContainerProxy();
					container.setSourceID(new ReferenceID(SystemID.SYSTEM, IApplicationManager.APPLICATION_MANAGER_ID));
					container.setTargetID(new ReferenceID(systemID, containerID));
					container.stopInstance(instanceID);
				} catch (InvocationException e) {
					// silently drop the thing
					Logging.error(getClass(), "Could not release binding " + name + ".", e); 
				} 
			}
			containerID = null;
			provision = null;
			instanceID = null;
			systemID = null;
		}
	}
	
	/**
	 * Loads the state of the instance bound to the binding
	 * using the specified checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	protected void load(InstanceCheckpoint checkpoint) {
		if (isBound()) {
			try {
				ContainerProxy container = new ContainerProxy();
				container.setSourceID(new ReferenceID(SystemID.SYSTEM, IApplicationManager.APPLICATION_MANAGER_ID));
				container.setTargetID(new ReferenceID(systemID, containerID));
				container.loadInstance(instanceID, checkpoint);
			} catch (InvocationException e) {
				Logging.error(getClass(), "Could not load instance " + name + ".", e); 
			} 
		}
	}
	
	/**
	 * Stores the state of the instance bound to the binding
	 * in a new checkpoint.
	 * 
	 * @return The checkpoint containing the state.
	 */
	protected InstanceCheckpoint store() {
		if (isBound()) {
			try {
				ContainerProxy container = new ContainerProxy();
				container.setSourceID(new ReferenceID(SystemID.SYSTEM, IApplicationManager.APPLICATION_MANAGER_ID));
				container.setTargetID(new ReferenceID(systemID, containerID));
				return container.storeInstance(instanceID);
			} catch (InvocationException e) {
				Logging.error(getClass(), "Could not store instance " + name + ".", e); 
			} 
		}
		return null;
	}

	/**
	 * Determines whether the binding has been resolved successfully.
	 * 
	 * @return True if the demand is matched by the provision, false 
	 * 	otherwise.
	 */
	protected boolean isValid() {
		Contract demand = application.getDemand(name);
		if (provision == null) return false;
		if (demand == null) return false;
		return provision.matches(demand, false);
	}
	
	/**
	 * Determines whether the binding is bound to some instance. For
	 * the moment, we hook this up to the lease of the instance.
	 * 
	 * @return True if the binding is bound to some instance.
	 */
	private boolean isBound() {
		return lease != null;
	}
	
	/**
	 * Called whenever a lease timeout occurs.
	 * 
	 * @param event The lease timeout event.
	 */
	public void handleEvent(Event event) {
		final int firePhase = phase;
		if (event.getType() == LeaseRegistry.EVENT_LEASE_EXPIRED) {
			Logging.debug(getClass(), "Lease expiration received.");
			InvocationBroker broker = InvocationBroker.getInstance();
			broker.performOperation(new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					application.removeInstance(name, firePhase);
				}
			});			
		}
	}
}
