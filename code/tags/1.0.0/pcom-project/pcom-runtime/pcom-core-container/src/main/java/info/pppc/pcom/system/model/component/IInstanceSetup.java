package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.contract.writer.IInstanceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;

/**
 * The instance setup is a instance description that is fully modifiable. This
 * description can be created through the context object of a factory. It is 
 * used to create descriptions of possible instances for a certain demand. 
 * 
 * @author Mac
 */
public interface IInstanceSetup {

	/**
	 * Returns the current provision that is guaranteed by the instance as long
	 * as the instance is running and no unresolved dependencies. As each instance
	 * must have exactly one provision, this will never be null. 
	 * 
	 * @return The current provision of the instance.
	 */
	public IInstanceProvisionWriter getInstance();
	
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
	 * Returns the instance demands that are currently specified by the factory. 
	 * If a new instance demand is introduced, the container will try to startup 
	 * an application that fulfills the demand. If an instance contract is 
	 * removed, the container will stop the application. Note that the container 
	 * does not guarantee the availablity of an application, thus the container 
	 * may decide to ignore the addition if it is not possible to start the
	 * application.
	 * 
	 * @return The instance demands that are currently specified by the
	 * 	factory.
	 */
	public IInstanceDemandWriter[] getInstances();
	
	/**
	 * Returns the instance demand with the specified name. If this instance 
	 * demand does not exist, this method will return null. If the name is 
	 * null, this method will throw an exception. 
	 * 
	 * @param name The name of the instance demand to retrieve.
	 * @return The instance demand with the specified name.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IInstanceDemandWriter getInstance(String name);
	
	/**
	 * Adds a new instance demand with the specified name. Note that the name 
	 * of the instance demand must be unique and must not be null. If the name 
	 * is null, this method will throw an exception. If the name is not unique, 
	 * the method will replace any existing writer and it will return it as
	 * a result.
	 * 
	 * @param name The name of the instance demand to create.
	 * @return The replaced instance demand or null if nothing has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IInstanceDemandWriter createInstance(String name);
	
	/**
	 * Returns a deep copy of the resource setup.
	 * 
	 * @return A deep copy of the resource setup.
	 */
	public IInstanceSetup copySetup();
}
