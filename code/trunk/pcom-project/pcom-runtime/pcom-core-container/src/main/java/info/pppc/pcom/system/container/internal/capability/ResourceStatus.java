package info.pppc.pcom.system.container.internal.capability;

import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.contract.DemandReader;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IResourceStatus;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;

/**
 * The resource status provides the status for a resource that has been
 * assigned. It contains the provisions of the resources that are currently
 * issued to the resource as well as the demand that is currently demanded
 * from the resource.
 * 
 * @author Mac
 */
public class ResourceStatus extends AbstractStatus implements IResourceStatus {

	/**
	 * Creates a new resource status with the specified name.
	 * 
	 * @param name The name of the resource status.
	 */
	public ResourceStatus(String name) {
		this(new Contract(Contract.TYPE_RESOURCE_STATUS, name));
	}
	
	/**
	 * Creates a new resource status with the specified contract.
	 * 
	 * @param c The contract used as resource status.
	 */
	protected ResourceStatus(Contract c) {
		super(c);
	}
	
	/**
	 * Creates a view for the specified contract. In addition to the
	 * allocator status, the resource status also specifies the demand
	 * that must be fulfilled by the resource. 
	 * 
	 * @param c The contract for which a view needs to be created.
	 * @return The view for the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_RESOURCE_DEMAND) {
			return new DemandReader(c);
		} else {
			return super.createView(c);
		}
	}
	
	/**
	 * Returns the resource demand that is currently demanded from the
	 * resource that holds the status.
	 * 
	 * @return The resource demand of the resource that holds this
	 * 	status.
	 */
	public IResourceDemandReader getResource() {
		Contract c = getContract();
		Contract demand = c.getContract(Contract.TYPE_RESOURCE_DEMAND);
		return (IResourceDemandReader)getView(demand);
	}
	
	
}
