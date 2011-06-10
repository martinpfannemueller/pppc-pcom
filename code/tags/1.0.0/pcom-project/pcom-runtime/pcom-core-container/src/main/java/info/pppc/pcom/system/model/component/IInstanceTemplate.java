package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.IElementTemplate;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;

/**
 * The instance template is the basic view on a contract of a certain instance. 
 * Using the instance template, an instance can retrieve its requirements and 
 * manipulate its provision at runtime. The container will perform automated contract 
 * evaluations upon every change. Changes need to be signaled using the commit method
 * on the template. Note that the thread that performs the commit will also be 
 * reused for (possibly remote) event notifications.  
 * 
 * @author Mac
 */
public interface IInstanceTemplate extends IElementTemplate {
	
	/**
	 * Returns the current provision that is guaranteed by the instance as long
	 * as the instance is running and no unresolved dependencies. As each instance
	 * must have exactly one provision, this will never be null. This provision
	 * must never be out of the demand directed towards the instance. If this
	 * happens, the instance must call the change instance method on its context
	 * object. 
	 * 
	 * @return The current provision of the instance as modifiable view.
	 */
	public IInstanceProvisionWriter getInstance();
	
	/**
	 * Returns the instance demands that are currently specified by the instance. 
	 * The instance demands cannot be changed through this template. If they do
	 * no longer match the requirements of the instance, the instance must call
	 * the change instance method.
	 * 
	 * @return The instance demands that are currently required by the instance.
	 */
	public IInstanceDemandReader[] getInstances();
	
	/**
	 * Returns the instance demand with the specified name. If this instance 
	 * demand does not exist, this method will return null. If the name is 
	 * null, this method will throw an exception. 
	 * 
	 * @param name The name of the instance demand to retrieve.
	 * @return The instance demand with the specified name.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IInstanceDemandReader getInstance(String name);
	
	/**
	 * Returns the resource demands that are currently specified by this instance. 
	 * Note that the resource demands cannot be changed at runtime. If the instance
	 * needs to change its demands to maintain a compatible provision, the instance
	 * needs to call the change method on its context object.
	 * 
	 * @return The resource demands that are currently specified by this instance.
	 */
	public IResourceDemandReader[] getResources();
	
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
	public IResourceDemandReader getResource(String type);
	
}
