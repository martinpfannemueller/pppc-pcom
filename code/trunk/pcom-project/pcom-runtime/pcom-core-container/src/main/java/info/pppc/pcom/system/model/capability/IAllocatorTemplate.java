package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.IElementTemplate;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;

/**
 * The allocator template is the basic view on a contract of a certain allocator. 
 * Using the template, an allocator can manipulate its requirements. The container 
 * will perform automated contract evaluations upon every change. Note that the thread 
 * that performs the change will also be reused for event notifications. A change
 * must be explicitly signalled by a call to the commit template method. All accesses
 * to this method should be synchronized.
 * 
 * @author Mac
 */
public interface IAllocatorTemplate extends IElementTemplate {

	/**
	 * Returns the resource demands that are currently specified by this allocator 
	 * contract. Note that the container does not guarantee the availablity of 
	 * the resources that are required by an allocator. However, if the container 
	 * cannot fulfill the resource requirements, it will check whether it can 
	 * fulfill it whenever the available resources change.
	 * 
	 * @return The resource demands that are currently required by the factory.
	 */
	public IResourceDemandWriter[] getResources();
	
	/**
	 * Returns the resource demand for the specified type or null, if such a 
	 * resource demand does not exist. If the resource type is null, this method
	 * will throw an exception.
	 * 
	 * @param type The type of the resource demand to retrieve.
	 * @return The resource demand with the specified type or null if such a 
	 * 	resource demand does not exist.
	 * @throws NullPointerException Thrown if the resource type is null.
	 */
	public IResourceDemandWriter getResource(String type);
	
	/**
	 * Creates a new resource demand with the specified type. Note that the type
	 * must be unique. If the type is null, this method will throw an exception.
	 * If the type is not unique, this method will return the replaced demand.
	 * 
	 * @param type The type of the resource demand to create.
	 * @return Null if the type of the resource demand is not unique or the 
	 * 	newly created resource demand, if the type of the resource is unique.
	 * @throws NullPointerException Thrown if the resource type is null.
	 */
	public IResourceDemandWriter createResource(String type);
	
}
