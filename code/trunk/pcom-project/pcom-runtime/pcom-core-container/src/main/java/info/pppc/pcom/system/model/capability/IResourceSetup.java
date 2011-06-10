package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;

/**
 * The resource setup is used by resource allocators to create descriptions of
 * possible resource assignments. Thus this class provides a read/write view on
 * resource templates. Resource allocators can retrieve instances of this class
 * through the context object of the container.
 * 
 * @author Mac
 */
public interface IResourceSetup {
	
	/**
	 * Returns the provision that is currently granted by the assignment.
	 * 
	 * @return The provision granted by the assignment.
	 */
	public IResourceProvisionWriter getResource();
	
	/**
	 * Returns the resource demands that are currently specified by this factory 
	 * contract. Note that the container does not guarantee the availablity of 
	 * the resources that are required by a factory. However, if the container 
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
	 * If the type is not unique, this method will return null.
	 * 
	 * @param type The type of the resource demand to create.
	 * @return Null if the type of the resource demand is not unique or the 
	 * 	newly created resource demand, if the type of the resource is unique.
	 * @throws NullPointerException Thrown if the resource type is null.
	 */
	public IResourceDemandWriter createResource(String type);
	
	/**
	 * Returns a deep copy of the resource setup.
	 * 
	 * @return A deep copy of the resource setup.
	 */
	public IResourceSetup copySetup();

}
