package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.IElement;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;

/**
 * The interface of all factories. This interface defines methods to retrieve 
 * an offered description for a certain demand and methods to manage the 
 * life-cycle of the factory as well as methods to set the factory's handle
 * to the container.
 * 
 * @author Mac
 */
public interface IFactory extends IElement {

	/**
	 * Returns the human readable name of the factory.
	 *
	 * @return The human readable name of the factory.
	 */
	public String getName();
	
	/**
	 * Sets the container reference of the factory. This method
	 * is guaranteed to be called before the start method of the
	 * factory is called.
	 * 
	 * @param context The container reference of the factory.
	 */
	public void setContext(IFactoryContext context);
	
	/**
	 * Unsets the container reference of the factory. This method
	 * will be called after the stop method has been called.
	 */
	public void unsetContext();
	
	/**
	 * Called by the container to start the factory. This method
	 * is guaranteed to be called after the container reference
	 * has been set and before the first call to the create
	 * methods for contracts and instances is made. After this
	 * method has been called, the factory must respond to
	 * create calls. Note that this method must return and it
	 * must not block as this will break the container.
	 */
	public void start();
	
	/**
	 * Called by the container to stop the factory. This method
	 * is guaranteed to be called after the start method. If
	 * this method is called, all instances have been released
	 * already. Note that this method must return and it must
	 * not block as this will break the container.
	 */
	public void stop();
	
	/**
	 * Creates possible descriptions for a component instance. If this 
	 * method is called, the factory must derive descriptions from 
	 * the demand and it must return them. If the factory cannot 
	 * satisfy the demand then it might as well chose to return null 
	 * or an empty array.
	 * 
	 * @param demand The demand that must be satisfied.
	 * @return A set of descriptions that can fulfill the demands or
	 * 	null if the demand cannot be fulfilled.
	 */
	public IInstanceSetup[] deriveSetups(IInstanceDemandReader demand);
	
	/**
	 * Requests that the factory creates a new instance.
	 * 
	 * @return An instance of the factory. 
	 */
	public IInstance createInstance();

	/**
	 * Returns the skeleton for an instance.
	 * 
	 * @return The skeleton for an instance.
	 */
	public IInstanceSkeleton createSkeleton();
	
	/**
	 * Returns the proxy for the instance dependency with the specified
	 * name.
	 * 
	 * @param name The name of the dependency as specified by some instance
	 * 	dependency in the template created through this factory.
	 * @return The proxy for the instance dependency with the specified
	 * 	name.
	 */
	public IInstanceProxy createProxy(String name);
}
