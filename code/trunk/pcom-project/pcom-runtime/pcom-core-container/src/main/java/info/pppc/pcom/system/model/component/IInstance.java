package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.IElement;

/**
 * The instance interface is the basic interface of instances. Appart from the methods to 
 * set a reference on the component container, this interface defines the life-cycle methods
 * of each instance. To manage the life-cycle, each instance must implement the start, stop 
 * and pause methods. The container guarantees that a start method is called only after the
 * container has been setup. After the start method is called, the instance must fulfill its 
 * contract. At some point, the container might decide to call a pause method. While the 
 * instance is in the paused state, it must not use the resources and it must not use the 
 * instances that are bound to the instance. At some point later, the container might
 * either call the stop method to stop the instance or it might again call the start method. 
 * Note that the start, stop and pause methods must not block. If they block, the container 
 * will hang which will not only break the running application, but it will break others too.
 * 
 * @author Mac
 */
public interface IInstance extends IElement {

	/**
	 * Sets the instance's context that can be used to access the template of the instance 
	 * as well as the status provided by the container. This method is guaranteed to be 
	 * called exactly once befor the first call to the start method. During the execution 
	 * of an instance, this reference will remain unchanged until the unsetContainer
	 * method is called.
	 * 
	 * @param context The instance's container.
	 */
	public void setContext(IInstanceContext context);
	
	/**
	 * Unsets the instance's context. After a call to this method, the instance must no 
	 * longer access the container. An instance should immediately remove all references to
	 * its container during the execution of this method.
	 */
	public void unsetContext();
	
	/**
	 * This method is called whenever the instance is started. This can be either after the 
	 * instance has been created or after a call to the pause method. Within this method,
	 * the instance should update its provision based on the provisions contained in the
	 * status object. 
	 * 
	 */
	public void start();

	/**
	 * This method only called after the instance has been started. It indicates that the 
	 * instance should immediately stop communicating with other instances and that it
	 * should wait with performing anything until the start or the stop method is called.
	 */	
	public void pause();
	
	/**
	 * This method is only called while the instance is in the started or in the paused 
	 * state. It indicates that the instance should be stopped. Note that this method
	 * should immediately release all threads that have been created by the instance.
	 */
	public void stop();

	/**
	 * Requests that the instance should store its state in the passed checkpoint object.
	 * 
	 * @param checkpoint The checkpoint object to store the state.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint);
	
	/**
	 * Requests that the instance should load its internal state from the passed
	 * checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load the state.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint);
}
