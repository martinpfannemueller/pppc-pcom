package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.IElementContext;

/**
 * This interface contains the container methods that are provided to a certain 
 * factory. It contains methods to start new operations as well as methods to 
 * perform resource reservations.
 * 
 * @author Mac
 */
public interface IFactoryContext extends IElementContext {
	
	/**
	 * Returns the factory description that can be used to express resource 
	 * requirements. If a resource requirement is added, the container 
	 * will try to reserve the corresponding resource. The success of this 
	 * operation can be checked by retrieving the container contract that is 
	 * accessible through the get container method. If a modification to this
	 * contract is made or if the attributes of the contract are read, users
	 * must synchronize on the template.
	 * 
	 * @return A factory template that can be used to formulate resource
	 * 	requirements.
	 */
	public IFactoryTemplate getTemplate();

	/**
	 * Returns the current factory status that denotes the resource provisions that are 
	 * currently issued to the factory. This is a read-only object. Note that users must 
	 * synchronize on the object in order to read values.
	 * 
	 * @return The factory status that can be used to check which resources are 
	 * 	currently reserved by the factory.
	 */
	public IFactoryStatus getStatus();

	/**
	 * Creates a new instance setup that can be used to create descriptions
	 * upon requests.
	 * 
	 * @return An empty instance setups that can be used to create a setups for a request.
	 */
	public IInstanceSetup createSetup();
	
	/**
	 * Returns the accessor object for some resource that is provided to the factory.
	 * The name of the accessor object will be the name as specified by the factory's
	 * template object. Since the resource assignments of factories are not guaranteed,
	 * the factory must be capable of dealing with the unavailablity of resources and
	 * their accessors.
	 * 
	 * @param name The name of the resource as specified in the factory template whose
	 * 	accessor object should be retrieved.
	 * @return The resource accessor for the specified resource or null if such a resource
	 * 	or accessor does not exist.
	 */
	public Object getAccessor(String name);
	
}
