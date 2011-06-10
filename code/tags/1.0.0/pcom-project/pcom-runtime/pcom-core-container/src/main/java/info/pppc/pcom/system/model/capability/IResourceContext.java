package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.IElementContext;


/**
 * The resource context is used to model a single resource assignment 
 * provided by some allocator. In contrast to other elements such as 
 * instances, factories or allocators, resources do not have an element
 * as they are completely controlled by the resource that created the
 * assignment and it does not make sense to create an additional class 
 * for them. 
 * 
 * @author Mac
 */
public interface IResourceContext extends IElementContext {

	/**
	 * Returns the resource status that describes the resources that are 
	 * currently provided to the assignment as well as the requirements
	 * that are directed towards the resource assignment. 
	 * 
	 * @return The resources currently provided to the resource as well
	 * 	as the requirements towards the resource.
	 */
	public IResourceStatus getStatus();
	
	/**
	 * Returns the resource description that describes the provided
	 * resource as well as the required resources.
	 * 
	 * @return The provision and the requirements of the assignment.
	 */
	public IResourceTemplate getTemplate();
	
	/**
	 * Sets the accessor object of the resource that can be used to
	 * access the resource.
	 * 
	 * @param accessor The accessor object of the resource.
	 */
	public void setAccessor(Object accessor);
	
	/**
	 * Returns the accessor object of the resource that can be used
	 * to access the resource.
	 * 
	 * @return The accessor object of the resource.
	 */
	public Object getAccessor();
	
	/**
	 * Requests a change to the resource. The resource itself
	 * however could be reused if the contract would be changed.
	 */
	public void changeResource();
	
	/**
	 * Requests that the resource must be removed immediately.
	 */
	public void removeResource();
	
	/**
	 * Returns the accessor object for a certain resource provision that
	 * has been granted to the resource context in response to a demand
	 * specified in the template of the resource.
	 * 
	 * @param name The name of the resource demand whose accessor
	 * 	should be retrieved.
	 * @return The accessor object of the resource demand or null
	 * 	if such a demand or accessor does not exist.
	 */
	public Object getAccessor(String name);

}
