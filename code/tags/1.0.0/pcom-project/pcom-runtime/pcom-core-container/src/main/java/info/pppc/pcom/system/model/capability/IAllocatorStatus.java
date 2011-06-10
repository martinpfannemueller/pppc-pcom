package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.IElementStatus;

/**
 * The allocator status is the basic view of the environment that is provided
 * to a resource at runtime. The resource contract describes the resources that 
 * are currently issued to the resource. In addition to the element status,
 * this interface defines two addtional events that are fired when resources
 * are added or removed.
 * 
 * @author Mac
 */
public interface IAllocatorStatus extends IElementStatus {
	
	/**
	 * Signals that the resource provison changed. The event source will be the
	 * status and the event type will be an resource provision reader of the 
	 * provision that changed.
	 */
	public static final int EVENT_RESOURCE_ADDED = 4;
	
}
