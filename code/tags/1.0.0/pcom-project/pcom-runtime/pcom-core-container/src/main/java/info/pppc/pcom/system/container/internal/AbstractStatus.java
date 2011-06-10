package info.pppc.pcom.system.container.internal;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.pcom.system.container.internal.contract.ProvisionReader;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.IElementStatus;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IResourceProvisionReader;

import java.util.Vector;

/**
 * The abstract status extends the setup objects with an event
 * model that signals changes to the base contract using events.
 * 
 * @author Mac
 */
public abstract class AbstractStatus extends AbstractSetup implements IElementStatus {

	/**
	 * The event that is fired if a contract is added. The source will be this
	 * object, the data object will be the view for the contract that has been added.
	 */
	public static final int EVENT_CONTRACT_ADDED = 1;
	
	/**
	 * The event that is fired if a contract is removed. The source will be this
	 * object, the data object will be the view for the contract that has been 
	 * removed.
	 */
	public static final int EVENT_CONTRACT_REMOVED = 2;
	
	/**
	 * The event that is fired if a contract is replaced by another contract.
	 * The source object will be this object, the data object will be the view
	 * for the contract that has been added instead of the old one.
	 */
	public static final int EVENT_CONTRACT_REPLACED = 4;

	/**
	 * The listeners that listen to status events fired by this status.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * The events that are cued up, ready to be fired through the fire event
	 * method.
	 */
	private Vector events = new Vector();

	/**
	 * Creates a new status for the specified contract.
	 * 
	 * @param contract The contract that is managed by this status.
	 */
	protected AbstractStatus(Contract contract) {
		super(contract);
	}

	/**
	 * Adds a listener to the set of listeners that listen to contract
	 * events.
	 * 
	 * @param types The types of events to register for. Multiple events
	 * 	can be registered by oring them.
	 * @param listener The listener to register.
	 */
	public void addStatusListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes a listener for a set of events from the set of status
	 * listeners.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been unregistered, false otherwise.
	 */
	public boolean removeStatusListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	/**
	 * Adds an event of the specified type to the event queue. The events
	 * are not immediately fired, instead they are fired whenever a client
	 * calls the fire method.
	 * 
	 * @param type The type of the event.
	 * @param spec The contract of the event.
	 * @param create A boolean flag that indicates whether the view should
	 * 	be created or retrieved from cache. 
	 */
	private void addEvent(int type, Contract spec, boolean create) {
		int t = createEvent(type, spec);
		if (t != -1) {
			IContract view;
			if (create) {
				view = createView(spec);
			} else {
				view = getView(spec);
			}
			Event e = new Event(t, this, view, false);
			events.addElement(e);			
		}
	}
	
	/**
	 * This method must be implemented by implementing clients. It returns
	 * a user definable event constant for a certain event type for a certain
	 * contract. The returned event constant must be a positive integer. If
	 * the method returns -1, the event will be silently dropped.
	 * 
	 * @param type The type of the event whose type should be retrieved.
	 * @param contract The contract that created the event.
	 * @return An event constant for the event that should be created. If the
	 * 	method returns -1, no event will be created.
	 */
	protected int createEvent(int type, Contract contract) {
		if (contract.getType() == Contract.TYPE_RESOURCE_PROVISION) {
			switch (type) {
				case EVENT_CONTRACT_REPLACED:
					return EVENT_RESOURCE_CHANGED;
				case EVENT_CONTRACT_REMOVED:
					return EVENT_RESOURCE_REMOVED;
				default:
			}
		}
		return -1;
	}
	
	/**
	 * Fires all events that have been stored in the event queue so far.
	 */
	public void fireEvents() {
		while (events.size() > 0) {
			Event e = (Event)events.elementAt(0);
			events.removeElementAt(0);
			listeners.fireEvent(e);
		}
	}

	/**
	 * Removes all events that have been stored in the event queue and
	 * that have not been fired so far.
	 */
	public void clearEvents() {
		events.removeAllElements();
	}

	/**
	 * Overwrites the base method and adds the eventing mechanism.
	 * Adds a contract to the base contract and returns the replaced
	 * contract.
	 * 
	 * @param c The contract that should be added.
	 * @return The contract that has been replaced.
	 */
	public Contract addContract(Contract c) {
		Contract replaced = super.addContract(c);
		if (replaced != null) {
			addEvent(EVENT_CONTRACT_REPLACED, c, false);
		} else {
			addEvent(EVENT_CONTRACT_ADDED, c, false);
		}
		return replaced;
	}
	
	/**
	 * Overwrites the base method and adds the eventing mechanism.
	 * Removes a certain contract from the base contract and returns
	 * the removed contract.
	 * 
	 * @param type The type of the contract to remove.
	 * @param name The name of the contract to remove.
	 * @return The contract that has been removed or null if none.
	 */
	public Contract removeContract(byte type, String name) {
		Contract spec = super.removeContract(type, name);
		if (spec != null) {
			addEvent(EVENT_CONTRACT_REMOVED, spec, true);
		}
		return spec;
	}
	
	/**
	 * Overwrites the base method and adds the eventing mechanism.
	 * Removes a certain contract from the base contract and returns
	 * true if the contract has been removed or false if the contract
	 * was not a child of the base contract.
	 * 
	 * @param c The contract that should be removed.
	 * @return True if the contract has been removed, false if the
	 * 	contract was not a child or the base contract.
	 */
	public boolean removeContract(Contract c) {
		if (super.removeContract(c)) {
			addEvent(EVENT_CONTRACT_REMOVED, c, true);
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a view for the specified contract. Since all the status
	 * objects will hold resources, this method will always create
	 * provision readers.
	 * 
	 * @param c The contract for which a view needs to be created.
	 * @return The view of the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_RESOURCE_PROVISION) {
			return new ProvisionReader(c);	
		}
		throw new IllegalArgumentException("Cannot create view.");
	}
	
	/**
	 * Returns the resource provision reader for the resource provision with
	 * the specified name.
	 * 
	 * @param name The name of the resource provision reader to retrieve.
	 * @return The resource provision reader for the resource or null if
	 * 	no such reader exists.
	 */
	public IResourceProvisionReader getResource(String name) {
		Contract cnt = getContract();
		Contract c = cnt.getContract(Contract.TYPE_RESOURCE_PROVISION, name);
		return (IResourceProvisionReader)getView(c);
		
	}
	
	/**
	 * Returns the resource provision readers for all resource provisions
	 * specified in this status.
	 * 
	 * @return An array of resource provisions specified by this status.
	 */
	public IResourceProvisionReader[] getResources() {
		Contract c = getContract();
		Contract[] cs = c.getContracts(Contract.TYPE_RESOURCE_PROVISION);
		IResourceProvisionReader[] readers = new IResourceProvisionReader[cs.length];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = (IResourceProvisionReader)getView(cs[i]);
		}
		return readers;
	}
	
}
