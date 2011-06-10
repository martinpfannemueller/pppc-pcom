package info.pppc.pcom.system.container.internal.component;

import info.pppc.pcom.system.container.internal.capability.AllocatorStatus;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IFactoryStatus;

/**
 * The factory status provides a readable view on the provisions of resources
 * that are currently issued to a factory. Changes to the status are signaled
 * by status events. 
 * 
 * @author Mac
 */
public class FactoryStatus extends AllocatorStatus implements IFactoryStatus {

	/**
	 * Creates a new factory status using the specified name as name
	 * for the base factory status contract that acts as data model.
	 * 
	 * @param name The name of the factory status contract.
	 */
	public FactoryStatus(String name) {
		this(new Contract(Contract.TYPE_FACTORY_STATUS, name));
	}
	
	/**
	 * Creates a new factory status with the specified contract.
	 * 
	 * @param c The contract of the factory status.
	 */
	protected FactoryStatus(Contract c) {
		super(c);
	}

}
