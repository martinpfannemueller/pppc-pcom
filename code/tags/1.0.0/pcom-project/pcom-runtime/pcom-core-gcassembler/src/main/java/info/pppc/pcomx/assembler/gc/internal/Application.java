package info.pppc.pcomx.assembler.gc.internal;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import info.pppc.base.lease.Lease;
import info.pppc.base.system.DeviceRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.MultiOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.ContainerProxy;
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcomx.assembler.gc.GCAssembler;

/**
 * The gc assembly contains all state of the gc assembler that is required
 * for one application. Interested observers can register listeners that will
 * be notified whenever the internal state of the application changes.
 * 
 * @author Mac
 */
public class Application {

	/**
	 * Called whenever the setup method has been called. The data
	 * object will be an object array with two elements. The first
	 * will be an assembly pointer. The second will be the 
	 * assembly state that has been added. The source of the event will
	 * be the application that received the state.
	 */
	public static final int EVENT_STATE_ADDED = 1;
	
	/**
	 * Called whenever an item has been added. The item can either be an 
	 * instance, a resource or one of the corresponding bindings. The 
	 * source object of the event will be the application. The data object 
	 * will be the item.
	 */
	public static final int EVENT_ITEM_ADDED = 2;
	
	/**
	 * Called whenever a item has been configured. The item can either be 
	 * a binding or an element. The data object will be the item that has 
	 * been configured. The source object will be the application.
	 */
	public static final int EVENT_ITEM_CONFIGURED = 4;
	
	/**
	 * Called whenever an item has been removed. The item can either be a 
	 * binding or an element. The source object will be the application. 
	 * The data object will be the item.
	 */
	public static final int EVENT_ITEM_REMOVED = 8;
	
	/**
	 * Called whenever a binding has been resolved. The binding will 
	 * either be an instance or a resource binding. The data object
	 * of the event will be the binding. The source object will be
	 * the application.
	 */
	public static final int EVENT_BINDING_RESOLVED = 16;
	
	/**
	 * Called whenever the resources of some remote device have been
	 * resolved. The data object of the event will be the device. 
	 * The source object will be the application.
	 */
	public static final int EVENT_DEVICE_RESOLVED = 32;
	
	/**
	 * The id of the application represented by this application object.
	 */
	private ReferenceID applicationID;
	
	/**
	 * The lease of the application that has been issued to the user
	 * of the assembler.
	 */
	private Lease lease;
		
	/**
	 * The listeners that are registered at the application.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * The devices with their resources used within the assembly
	 * hashed by their system id.
	 */
	private Hashtable devices = new Hashtable();
	
	/**
	 * The assembly states hashed by pointers.
	 */
	private Hashtable states = new Hashtable();
	
	/**
	 * The bindings that have not been resolved so far.
	 */
	private Vector bindings = new Vector();
	
	/**
	 * The items that need to be configured.
	 */
	private Vector items = new Vector();
	
	/**
	 * Creates a new assembly for the specified application.
	 * 
	 * @param applicationID The id of the application that is
	 * 	represented by this assembly.
	 */
	public Application(ReferenceID applicationID) {
		this.applicationID = applicationID;
	}

	/**
	 * Returns the application id of the assembly.
	 * 
	 * @return The appliation id of the assembly.
	 */
	public ReferenceID getApplicationID() {
		return applicationID;
	}

	/**
	 * Adds the specified listener to the set of listeners registered
	 * for the specified events. The set of possible events is defined
	 * by the event constants in this class.
	 * 
	 * @param types The types of events to register for.
	 * @param listener The listener to register.
	 */
	public void addApplicationListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes the specified application listener from the set of listeners
	 * registered for this application.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been found and removed, false otherwise.
	 */
	public boolean removeApplicationListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	/**
	 * Returns the lease that is used to keep track of clients
	 * that do no not perform explicit removes.
	 * 
	 * @return The lease that has been issued to the creator of the
	 * 	assembly.
	 */
	public Lease getApplicationLease() {
		return lease;
	}

	/**
	 * Sets the lease that is used to keep track of clients
	 * that do not perform explicit removes.
	 * 
	 * @param lease The lease that has been issued to the creator of
	 * 	the assembly.
	 */
	public void setApplicationLease(Lease lease) {
		this.lease = lease;
	}
	
	/**
	 * Adds the state for the specified pointer and returns the pointers 
	 * that are required to retrieve the children of the state.
	 * 
	 * @param pointer The pointer used to identify the state.
	 * @param state The state that will be stored for later configuration.
	 * @return The pointers that have been created for the state.
	 */
	public Pointer[] setupState(Pointer pointer, AssemblyState state) {
		states.put(pointer, state);
		String[] instances = state.getInstances();
		String[] resources = state.getResources();
		Pointer[] pointers = new Pointer[instances.length + resources.length];
		for (int i = 0; i < instances.length; i++) {
			pointers[i] = new Pointer(pointer, true, instances[i]);
		}
		for (int i = instances.length; i < pointers.length; i++) {
			pointers[i] = new Pointer(pointer, false, resources[i - instances.length]);
		}
		fireApplicationEvent(EVENT_STATE_ADDED, new Object[] { pointer, state });
		return pointers;
	}
	
	/**
	 * Called to configure the application. A call to this method will
	 * try to configure the application and will return the assembly
	 * that denotes the new configuration or it will return null if
	 * the configuration cannot be determined.
	 * 
	 * @return The configured assembly or null if the application cannot
	 * 	be configured.
	 */
	public Assembly configureApplication() {
		// retrieve an up-to-date view of the available resources
		resolveDevices();
		// create the anchor component from the anchor setup
		Pointer rootPointer = new Pointer();
		AssemblyState state = getState(rootPointer);
		if (state == null) return null;
		Vector contracts = state.getContracts();
		SystemID systemID = state.getSystemID();
		ObjectID creatorID = state.getCreatorID();
		Instance anchor = new Instance(this, systemID, creatorID, contracts, rootPointer);
		addItem(anchor);
		while (items.size() != 0 || bindings.size() != 0) {
			configureItems();
			resolveBindings();
		}
		if (anchor.isConfigured()) {
			return anchor.externalize(state.getName());
		} else {
			return null;	
		}
	}
	
	/**
	 * Returns the assembly state for the pointer or null if the
	 * pointer is not associated with any state.
	 * 
	 * @param pointer The pointer used to retrieve the state.
	 * @return The state for the pointer or null if there is none.
	 */
	protected AssemblyState getState(Pointer pointer) {
		return (AssemblyState)states.get(pointer);
	}
	
	/**
	 * Fires the specified application event using the specified data
	 * object.
	 * 
	 * @param type The type of the event. This must be one of the event
	 * 	constants defined by this interface.
	 * @param data The data of the event. The type depends on the event
	 * 	type.
	 */
	protected void fireApplicationEvent(int type, Object data) {
		listeners.fireEvent(type, data);
	}
	
	
	/**
	 * Adds a configurable to the list of configurables that will be 
	 * configured later on.
	 * 
	 * @param configurable The configurable to add.
	 */
	protected void addItem(AbstractItem configurable) {
		items.insertElementAt(configurable, 0);
	}

	/**
	 * Called whenever a configurable that has been inserted into the
	 * queue can be removed and does not need to be configured anymore.
	 * 
	 * @param configurable The configurable that must no longer be configured.
	 * @return True if the configurable has been removed, false otherwise.
	 */
	protected boolean removeItem(AbstractItem configurable) {
		return items.removeElement(configurable);
	}
	
	/**
	 * Configures all configurables that are contained in the configurable
	 * list and removes them again.
	 */
	private void configureItems() {
		while (items.size() != 0) {
			AbstractItem configurable = (AbstractItem)items.elementAt(0);
			items.removeElementAt(0);
			configurable.configure();
			fireApplicationEvent(EVENT_ITEM_CONFIGURED, configurable);
		}
	}
	
	/***
	 * Adds a binding to the list of bindings that will be resolved later 
	 * on.
	 * 
	 * @param binding The binding to add.
	 */
	protected void addBinding(AbstractBinding binding) {
		bindings.addElement(binding);
	}
	
	/**
	 * Called whenever a binding can be removed from the "to-resolve" binding
	 * list.
	 * 
	 * @param binding The binding that can be removed.
	 * @return True if the binding was present, false otherwise.
	 */
	protected boolean removeBinding(AbstractBinding binding) {
		return bindings.removeElement(binding);
	}
	
	/**
	 * Resolves all bindings that are contained in the binding list,
	 * clears the binding list and adds them to the list of configurables.
	 */
	private void resolveBindings() {
		// prepare query operations for resolvables
		InvocationBroker broker = InvocationBroker.getInstance();
		DeviceRegistry registry = broker.getDeviceRegistry();
		MultiOperation resolvers = new MultiOperation(broker);
		SystemID[] systemIDs = registry.getDevices(IContainer.CONTAINER_ID);
		for (int i = 0; i < systemIDs.length; i++) {
			final SystemID systemID = systemIDs[i];
			final Vector queries = new Vector();
			final Vector demands = new Vector();
			for (int j = 0; j < bindings.size(); j++) {
				AbstractBinding binding = (AbstractBinding)bindings.elementAt(j);
				if (binding instanceof ResourceBinding) {
					if (systemID.equals(binding.getSystemID())) {
						queries.addElement(binding);
						demands.addElement(binding.getDemand());
					}
				} else {
					queries.addElement(binding);
					demands.addElement(binding.getDemand());
				}
			}
			IOperation operation = new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					ContainerProxy container = new ContainerProxy();
					container.setSourceID(new ReferenceID(SystemID.SYSTEM, GCAssembler.ASSEMBLER_ID));
					container.setTargetID(new ReferenceID(systemID, IContainer.CONTAINER_ID));
					Hashtable table = container.getTemplates(demands);
					for (int i = 0; i < demands.size(); i++) {
						AbstractBinding binding = (AbstractBinding)queries.elementAt(i);
						Contract demand = (Contract)demands.elementAt(i);
						Hashtable creators = (Hashtable)table.get(demand);
						if (creators != null) {
							Enumeration e = creators.keys();
							while (e.hasMoreElements()) {
								ObjectID creatorID = (ObjectID)e.nextElement();
								Vector templates = (Vector)creators.get(creatorID);
								synchronized (binding) {
									binding.addTemplates(systemID, creatorID, templates);	
								}
							}
						}
					}
				};
			};
			resolvers.addOperation(operation);
		}
		// execute query operations and wait for success
		resolvers.performSynchronous();
		// remove bindings from list and configure them later on
		while (bindings.size() != 0) {
			AbstractBinding configurable = (AbstractBinding)bindings.elementAt(0);
			bindings.removeElementAt(0);
			addItem(configurable);
			fireApplicationEvent(EVENT_BINDING_RESOLVED, configurable);
		}
	}

	/**
	 * Returns the device for the specified system. The device is used
	 * to make resource reservations and to determine whether a certain
	 * application can be started on a device. A call to this method
	 * will search for the cached resource information of the device
	 * and return it. If the resource information is not present, this
	 * method will try to retrieve the information. If this is not
	 * possible at the given time, the method will create a new device
	 * without any resources and it will simply return it. 
	 * 
	 * @param systemID The system id of the device to retrieve.
	 * @return The device information for the specified system that can
	 * 	be used to compute the available resources and to make resource
	 * 	reservations.
	 */
	protected Device getDevice(SystemID systemID) {
		Device device = (Device)devices.get(systemID);
		if (device == null) {
			try {
				ContainerProxy proxy = new ContainerProxy();
				proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, GCAssembler.ASSEMBLER_ID));
				proxy.setTargetID(new ReferenceID(systemID, IContainer.CONTAINER_ID));
				Hashtable table = proxy.getResources();
				device = new Device(systemID, table);
			} catch (InvocationException e) {
				Logging.error(getClass(), "Cannot retrieve resources from device.", e);
				device = new Device(systemID, new Hashtable());
			}	
			devices.put(systemID, device);
			fireApplicationEvent(EVENT_DEVICE_RESOLVED, device);
		}
		return device;
	}
	
	/**
	 * Retrieves the resources that are currently available for all devices
	 * that host a container.
	 */
	private void resolveDevices() {
		SystemID[] systems = InvocationBroker.getInstance().getDeviceRegistry().getDevices(IContainer.CONTAINER_ID);
		MultiOperation operations = new MultiOperation(InvocationBroker.getInstance());
		for (int i = 0; i < systems.length; i++) {
			final SystemID systemID = systems[i];
			IOperation operation = new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					try {
						ContainerProxy proxy = new ContainerProxy();
						proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, GCAssembler.ASSEMBLER_ID));
						proxy.setTargetID(new ReferenceID(systemID, IContainer.CONTAINER_ID));
						Hashtable table = proxy.getResources();
						Device device = new Device(systemID, table);
						devices.put(systemID, device);
						fireApplicationEvent(EVENT_DEVICE_RESOLVED, device);
					} catch (InvocationException e) {
						Logging.error(getClass(), "Cannot retrieve resources from device.", e);
					}
				};
			};
			operations.addOperation(operation);
		}
		operations.performSynchronous();
	}
	
}
