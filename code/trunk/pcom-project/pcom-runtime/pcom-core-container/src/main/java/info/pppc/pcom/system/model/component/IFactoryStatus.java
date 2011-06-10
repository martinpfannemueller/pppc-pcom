package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.capability.IAllocatorStatus;

/**
 * The factory contract is the basic view of the environment that is provided
 * to a factory at runtime. The factory contract describes the resources that
 * are currently issued to a factory. This is only a marker interface that 
 * inherits the functionality of resource allocators.
 * 
 * @author Mac
 */
public interface IFactoryStatus extends IAllocatorStatus {

	
}
