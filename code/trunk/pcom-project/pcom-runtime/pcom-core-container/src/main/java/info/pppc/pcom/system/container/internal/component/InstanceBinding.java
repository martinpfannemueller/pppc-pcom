package info.pppc.pcom.system.container.internal.component;

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
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.container.InstanceSetup;
import info.pppc.pcom.system.container.InstanceState;
import info.pppc.pcom.system.container.internal.AbstractBinding;
import info.pppc.pcom.system.container.internal.AbstractInstanceContext;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IEventCollector;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceRestorer;

/**
 * The instance binding implemenets a binding for an instance. It represents
 * a remote instance bound to a local instance at runtime. 
 * 
 * @author Mac
 */
public class InstanceBinding extends AbstractBinding implements IListener {
	
	/**
	 * The proxy to access the remote instance.
	 */
	private InstanceProxy proxy;
	
	/**
	 * The event collector for events from the remote instance.
	 */
	private EventCollector collector;
	
	/**
	 * The instance state that manages the state of the instance
	 * of this binding.
	 */
	private InstanceRestorer restorer;
	
	/**
	 * The lease for the instance used to keep it alive.
	 */
	private Lease lease;
	
	/**
	 * The system id of the system that hosts the instance.
	 */
	private SystemID systemID;
	
	/**
	 * The id of the remote container that hosts the instance.
	 */
	private ObjectID containerID;
	
	/**
	 * The phase is always setup when the configuration of the
	 * binding for the phase has been done completely.
	 */
	private int phase;
		
	/**
	 * A flag that indicates whether the instance bound to the
	 * binding requires an update of its internal state.
	 */
	private boolean update = false;
	
	/**
	 * Creates a new instance binding for an instance with the specified name
	 * for the specified instance.
	 * 
	 * @param context The instance context of the binding.
	 * @param name The name of the binding used by the parent.
	 * @param proxy The proxy for the binding.
	 */
	protected InstanceBinding(AbstractInstanceContext context, String name, InstanceProxy proxy) {
		super(context, name);
		collector = new EventCollector();
		this.proxy = proxy; 
		this.restorer = new InstanceRestorer(proxy);
	}
	
	/**
	 * Returns the context of the instance that uses this binding.
	 * 
	 * @return The context of the instance that uses this binding.
	 */
	private AbstractInstanceContext getParent() {
		return (AbstractInstanceContext)getContext();
	}

	/**
	 * Returns the demand that is directed towards this instance binding.
	 * 
	 * @return The demand directed towards this instance binding.
	 */
	private Contract getDemand() {
		return getTemplate(Contract.TYPE_INSTANCE_DEMAND);
	}
	
	/**
	 * Sets the provision of the instance to the specified provision.
	 * 
	 * @param provision The provision of the instance.
	 */
	private void setProvision(Contract provision) {
		setStatus(provision);
	}
	
	/**
	 * Removes the instance provision for the specified binding.
	 */
	private void removePovision() {
		removeStatus(Contract.TYPE_INSTANCE_PROVISION);
	}
	
	/**
	 * Performs a setup of the instance binding using the specified assembly.
	 * 
	 * @param assembly The assembly used to setup the binding.
	 */
	protected void start(Assembly assembly) {
		boolean reuse = false;
		boolean restore = false;
		if (assembly.getElementID() != null) {
			Logging.debug(getClass(), "Reusing previous instance binding.");
			reuse = true;
		} else if (isBound()) {
			Logging.debug(getClass(), "Replacing previous instance binding.");
			restorer.release();
			stop(true);
			collector = new EventCollector();
		} else {
			Logging.debug(getClass(), "Creating new instance binding.");
			collector = new EventCollector();
		}
		InstanceContext context = (InstanceContext)getContext();
		ObjectID instanceID = context.getIdentifier();
		ObjectID collectorID = collector.getIdentifier();
		InstanceSetup bindingSetup = new InstanceSetup
			(getDemand(), instanceID, collectorID, IContainer.CONTAINER_ID, SystemID.SYSTEM, assembly);
		ContainerProxy container = new ContainerProxy();
		container.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
		container.setTargetID(new ReferenceID(assembly.getSystemID(), assembly.getContainerID()));
		try {
			InstanceState state = container.startInstance(bindingSetup, getParent().getPhase());
			if (reuse && ! state.isBound()) {
				Logging.debug(getClass(), "Failed reuse of instance binding.");
				stop(false);
			} else if (reuse && state.isBound()) {
				Logging.debug(getClass(), "Successfully reusing instance binding.");
				// nothing to be done here, since reuse
			} else if (state.isBound()) {
				lease = state.getLease();
				LeaseRegistry registry = getContext().getContainer().getLeaseRegistry();
				registry.hook(lease, this);
				systemID = assembly.getSystemID();
				containerID = assembly.getContainerID();
				proxy.setTargetID(new ReferenceID(systemID, state.getSkeletonID()));
				proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, getParent().getIdentifier()));
				setElementID(state.getInstanceID());
				setCreatorID(assembly.getCreatorID());
			}
			// return the provision depending on the success of the whole subtree
			if (state.isSuccess()) {
				if (systemID.equals(SystemID.SYSTEM)) {
					// we need to copy the provision if the instance is on the local sytem
					setProvision(state.getProvision().copy());
				} else {
					setProvision(state.getProvision());	
				}
				restore = update;
			} else {
				removePovision();
			}			
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not setup instance binding.", e);
			removePovision();
		}
		// setup the phase since the configuraton has been done here
		phase = getParent().getPhase();
		if (restore) {
			update = false;
			restorer.restore();
		}
	}
	
	/**
	 * Called whenever the instance is about to be paused. A call to this method
	 * will update the assembly state of the parent instance according to the
	 * data contained in the binding.
	 * 
	 * @param state The state that needs to be updated.
	 */
	protected void setup(AssemblyState state) {
		if (isBound()) {
			state.putInstance(getName(), systemID);
		}
	}

	/**
	 * Called whenever the instance bound to this binding should be paused.
	 * A call to this method will signal the instance that it should pause
	 * if that is possible. Failures will be ignored silently.
	 * 
	 * @param pointer The pointer used to pause the instance.
	 */
	protected void pause(AssemblyPointer pointer) {
		try {
			ContainerProxy proxy = new ContainerProxy();
			proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
			proxy.setTargetID(new ReferenceID(systemID, containerID));
			proxy.pauseInstance(getElementID(), pointer, getParent().getPhase());
		} catch (InvocationException e) {
			Logging.debug(getClass(), "Could not pause instance.");
		}
		// setup the phase since the configuraton has been done here
		phase = getParent().getPhase();
	}
	
	/**
	 * Called to signal that the instance has changed its provision.
	 * 
	 * @param provision The new provision of the instance.
	 */
	protected void changeInstance(Contract provision) {
		if (isBound()) {
			if (provision != null) {
				if (systemID.equals(SystemID.SYSTEM)) {
					// we need to copy the provision if the instance is on the local sytem
					setProvision(provision.copy());
				} else {
					setProvision(provision);	
				}
			} else {
				removePovision();
			}
			notifyChange();
		} else {
			Logging.debug(getClass(), "Ignoring change instance request for unbound instance.");
		}
	}
	
	/**
	 * Called to signal that the instance has removed itself.
	 */
	protected void removeInstance() {
		if (isBound()) {
			stop(false);
			notifyChange();
		} else {
			Logging.debug(getClass(), "Ignoring remove instance request for unbound instance.");
		}
	}
	
	/**
	 * Returns the proxy used to access the instance bound to this binding.
	 * 
	 * @return The proxy used to access the instance.
	 */
	protected IInstanceProxy getProxy() {
		return proxy;
	}
	
	/**
	 * Returns the instance restorer used by this binding.
	 * 
	 * @return The instance restorer of the binding.
	 */
	protected IInstanceRestorer getRestorer() {
		return restorer;		
	}

	/**
	 * Returns the event collector for events from the remote instance.
	 * 
	 * @return The event collector for the remote instance.
	 */
	protected IEventCollector getCollector() {
		return collector;
	}

	/**
	 * Determines whether the instance is bound. Returns true if the
	 * instance is bound, false if the instance is not bound. For
	 * instance bindings, this is decided based on the lease.
	 * 
	 * @return True if the instance is bound, false otherwise.
	 */
	protected boolean isBound() {
		return (super.isBound() && lease != null && containerID != null && systemID != null);
	}

	/**
	 * Releases the instance that is currently bound to this binding.
	 * 
	 * @param notify A flag that determines whether the removal should
	 * 	notify the child bound to the binding.
	 */
	protected void stop(boolean notify) {
		update = true;
		if (isBound()) {
			LeaseRegistry registry = getContext().getContainer().getLeaseRegistry();
			registry.unhook(lease, this, false);
			collector.release();
			proxy.setTargetID(null);
			final ObjectID instanceID = getElementID();
			final ObjectID containerID = this.containerID;
			final SystemID systemID = this.systemID;
			if (notify) {
				getContext().performOperation(new IOperation() {
					public void perform(info.pppc.base.system.operation.IMonitor monitor) {
						ContainerProxy proxy = new ContainerProxy();
						proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
						proxy.setTargetID(new ReferenceID(systemID, containerID));
						try {
							proxy.stopInstance(instanceID);	
						} catch (Throwable t) {
							Logging.error(getClass(), "Could not release instance binding.", t);
						}									
					}
				});				
			}
			this.lease = null;
			this.systemID = null;
			this.containerID = null;
			this.collector = null;
			setCreatorID(null);
			setElementID(null);
			removePovision();
		}
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
					getParent().getContainer().removeInstance
						(getParent().getIdentifier(), getName(), firePhase);
				}
			});			
		}
	}
	
}
