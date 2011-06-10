package info.pppc.pcom.system.container.internal.capability;

import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IAllocatorStatus;

/**
 * The allocator status provides the view of the allocator to the resources
 * provided to it at runtime. In addition to the abstract status, this class
 * adds the addtional events used to signal contract removals and additions.
 * 
 * @author Mac
 */
public class AllocatorStatus extends AbstractStatus implements IAllocatorStatus {

	/**
	 * Creates a new allocator status with the specified name as base
	 * contract.
	 * 
	 * @param name The name of the base contract of the allocator.
	 */
	public AllocatorStatus(String name) {
		this(new Contract(Contract.TYPE_ALLOCATOR_STATUS, name));
	}
	
	/**
	 * Creates an allocator status from the specified contract.
	 * 
	 * @param c The contract used as the base data model of the
	 * 	status.
	 */
	protected AllocatorStatus(Contract c) {
		super(c);
	}

	/**
	 * Creates the event type for the specified event and contract.
	 * 
	 * @param type The event type as defined by the base class.
	 * @param contract The contract that was affected by the change.
	 * @return The translated event constant or -1 if the event
	 * 	is not used.
	 */
	protected int createEvent(int type, Contract contract) {
		if (contract.getType() == Contract.TYPE_RESOURCE_PROVISION) {
			switch (type) {
				case EVENT_CONTRACT_ADDED:
					return EVENT_RESOURCE_ADDED;
				default:
			}
		}
		return super.createEvent(type, contract);
	}
	

	
}
