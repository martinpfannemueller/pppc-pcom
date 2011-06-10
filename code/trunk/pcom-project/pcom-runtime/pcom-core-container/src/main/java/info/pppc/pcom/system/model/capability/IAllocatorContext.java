package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.IElementContext;

/**
 * This interface contains the container methods that are provided to a certain 
 * allocator. It contains methods to start new operations as well as methods to 
 * perform resource negotiations. The methods also enable resources to perform 
 * resource reservations.
 * 
 * @author Mac
 */
public interface IAllocatorContext extends IElementContext {

	/**
	 * Returns the resource template that can be used to express resource 
	 * requirements. If a resource requirement is added, the container 
	 * will try to reserve the corresponding resource. The success of this 
	 * operation can be checked by retrieving the resource contract that is 
	 * accessible through the get contract method. If a modification to this
	 * description is made or if the attributes of the contract are read, users
	 * must synchronize on the template.
	 * 
	 * @return A resource template that can be used to formulate resource
	 * 	requirements.
	 */
	public IAllocatorTemplate getTemplate();

	/**
	 * Returns the current resource status that denotes the resource provisions that 
	 * are currently issued to the allocator. This is a read-only object. Note that 
	 * users must synchronize on the object in order to read values.
	 * 
	 * @return The resource status that can be used to check which resources are 
	 * 	currently reserved by the resource.
	 */
	public IAllocatorStatus getStatus();
	
	/**
	 * Called to signal the container that open resource requirements of factories 
	 * or other resources that are currently not fulfilled should be recomputed.
	 */
	public void updateResources();
	
	/**
	 * Creates a new empty resource description that can be used as a response to
	 * derive calls issued to the resource of this context object.
	 * 
	 * @return The new and empty resource descripton.
	 */
	public IResourceSetup createSetup();

	/**
	 * Returns the resource accessor of the resource that is granted to the allocator
	 * in response to some resource demand specified in the allocator's template.
	 * 
	 * @param name The name of the resource whose accessor should be retrieved as
	 * 	specified in the template of the resource.
	 * @return The accessor object for the specified resource provision or null if
	 * 	such a resource provision or accessor object does not exist.
	 */
	public Object getAccessor(String name);
	
}
