package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.IElementTemplate;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;

/**
 * The resource template is used to model the requirements of a certain
 * resource assignment as well as its provision. The provision can be
 * changed at runtime using the corresponding writer. However, a change
 * operation must always synchronize on the template and it must call
 * the commit method to signal the change to the container.  
 * 
 * @author Mac
 */
public interface IResourceTemplate extends IElementTemplate {

	/**
	 * Returns the provision that is currently granted by the resource. The 
	 * provision can be changed using the writer. However, changes must be
	 * commited using the commit method.
	 * 
	 * @return The provision granted by the assignment.
	 */
	public IResourceProvisionWriter getResource();

	/**
	 * Returns the resource demands that are currently specified by this resource 
	 * template. The container will guarantee, that the demands are met as long
	 * as the resource is used.
	 * 
	 * @return The resource demands that are currently required by the resource.
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
