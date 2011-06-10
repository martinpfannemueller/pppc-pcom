package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;

/**
 * The resource status is used by resources to query the provisions of
 * other resources used by the resource and the demand that must be fulfilled
 * by the resource. The instance of this class can be retrieved through the
 * context object of the resource.
 * 
 * @author Mac
 */
public interface IResourceStatus extends IAllocatorStatus {
	
	/**
	 * Returns the resource demand that must be fulfilled by the assignment.
	 * 
	 * @return The resource demand that must be fulfilled.
	 */
	public IResourceDemandReader getResource();
	
}
