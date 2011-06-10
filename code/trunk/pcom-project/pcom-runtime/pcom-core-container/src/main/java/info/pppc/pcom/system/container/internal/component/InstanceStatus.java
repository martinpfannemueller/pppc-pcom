package info.pppc.pcom.system.container.internal.component;

import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.contract.DemandReader;
import info.pppc.pcom.system.container.internal.contract.ProvisionReader;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IInstanceStatus;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceProvisionReader;

/**
 * The instance status is used to describe the provision that is currently
 * granted to some instance as well as the demand that is currently directed
 * towards the instance. All information contained in the status is read
 * only with respect to the instance.
 * 
 * @author Mac
 */
public class InstanceStatus extends AbstractStatus implements IInstanceStatus {

	/**
	 * Creates a new instance status with the specified name used as
	 * name for the base contract.
	 * 
	 * @param name The name of the base contract of the status.
	 */
	public InstanceStatus(String name) {
		this(new Contract(Contract.TYPE_INSTANCE_STATUS, name));
	}
	
	/**
	 * Creates a new instance status wih the specified contract as data
	 * model.
	 * 
	 * @param c The contract used as data model.
	 */
	protected InstanceStatus(Contract c) {
		super(c);
	}
	
	/**
	 * Creates a view for the specified contract. The extensions made for
	 * instances are basically the demand directed towards the instance
	 * and the provisions granted from other instances.
	 * 
	 * @param c The contract whose view needs to be created.
	 * @return The view for the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_INSTANCE_DEMAND) {
			return new DemandReader(c);
		} if (c.getType() == Contract.TYPE_INSTANCE_PROVISION) {
			return new ProvisionReader(c);
		} else {
			return super.createView(c);	
		}
	}
	
	/**
	 * Extens the event model by the instance changed event that signals
	 * a change to the provision of an instance.
	 * 
	 * @param type The type of the source event as defined by the base
	 * 	class.
	 * @param contract The contract that caused the base event.
	 * @return The event constant that needs to be fired or -1 if the
	 * 	event should be supressed.
	 */
	protected int createEvent(int type, Contract contract) {
		if (contract.getType() == Contract.TYPE_INSTANCE_PROVISION) {
			switch (type) {
				case EVENT_CONTRACT_REPLACED:
					return EVENT_INSTANCE_CHANGED;
				case EVENT_CONTRACT_REMOVED:
					return EVENT_INSTANCE_REMOVED;
				default:			
			}
		}
		return super.createEvent(type, contract);
	}
	
	/**
	 * Returns the instance demand that is currently directed towards the
	 * instance that uses this status.
	 * 
	 * @return The instance demand directed to the instance that holds the
	 * 	status.
	 */
	public IInstanceDemandReader getInstance() {
		Contract demand = getContract().getContract(Contract.TYPE_INSTANCE_DEMAND);
		return (IInstanceDemandReader)getView(demand);
	}
	
	/**
	 * Returns the instance provision for the instance with the specified name.
	 * 
	 * @param name The name of the instance provision that should be retrieved.
	 * @return The instance provision with the specified name or null if it
	 * 	does not exist.
	 */
	public IInstanceProvisionReader getInstance(String name) {
		Contract pro = getContract().getContract
			(Contract.TYPE_INSTANCE_PROVISION, name);
		return (IInstanceProvisionReader)getView(pro);
	}
	
	/**
	 * Returns all instance provisions that are currently issued to the instance
	 * that holds the status object.
	 * 
	 * @return The instance provisions that are currently issued to the instance.
	 */
	public IInstanceProvisionReader[] getInstances() {
		Contract[] pros = getContract().getContracts
			(Contract.TYPE_INSTANCE_PROVISION);
		IInstanceProvisionReader[] readers = 
				new IInstanceProvisionReader[pros.length];
		for (int i = 0; i < pros.length; i++) {
			readers[i] = (IInstanceProvisionReader)getView(pros[i]);
		}
		return readers;
	}
	
}
