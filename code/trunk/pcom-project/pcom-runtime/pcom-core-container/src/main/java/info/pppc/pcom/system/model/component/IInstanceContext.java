package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.IElementContext;

/**
 * The instance context is the interface of the container that
 * is visible to an instance at runtime. It allows the manipulation
 * of templates at runtime and it enables the instance to request a reconfiguration
 * an immediate exit of the application or a kill of this instance.
 * 
 * @author Mac
 */
public interface IInstanceContext extends IElementContext {
	
	/**
	 * Returns the status that is currently provided and required by the container. 
	 * This data structure is read-only. Reads should be performed on a locked contract 
	 * to avoid race conditions.
	 * 
	 * @return The instance status that contains the resource provisions, the provisions 
	 * 	of dependant components and the requirements of the parent instance.
	 */
	public IInstanceStatus getStatus();
	
	/**
	 * Returns the description that is currently required and provided by the instance. This 
	 * data structure can also be manipulated at runtime. Modifications are limited to
	 * modifications that do not conflict with the instance's status. If the changes would
	 * lead to a conflict, the instance must call the change instance method. After
	 * the change has been completed, the instance must call commit on the template.
	 * 
	 * @return The instance description that contains the resource demands, the instance 
	 * 	demands and the offered provision.
	 */
	public IInstanceTemplate getTemplate();
	
	/**
	 * Called to signal that the application that is using this instance should be shut down.
	 */
	public void exitApplication();
	
	/**
	 * Called to signal that the application state should be saved. This will create a
	 * checkpoint of the application anchor, if possible.
	 */
	public void saveApplication();
	
	/**
	 * Called to signal that the instance can no longer adhere to the specified contract
	 * and needs to be reconfigured. Note that this is the "nice" variant of the kill
	 * method and thus, it should be prefered.
	 */
	public void changeInstance();
	
	/**
	 * Called to signal that the instance must be killed immediately. This method will
	 * not signal anything to the parent instance, but it will simply stop the instance
	 * and remove all instances and resources that might be held for it. Note that this
	 * method should only be used if the instance can no longer run. If the instance
	 * just needs to be reconfigured, the change instance should be called instead.
	 */
	public void removeInstance();
	
	/**
	 * Returns the proxy for the specified instance that is bound to this instance. The
	 * name of the instance corresponds to the name specified in the template of the
	 * instance contract. The proxy can be safely casted into whatever proxy has been
	 * created by the factory in response to a create proxy request for the specified
	 * name.
	 * 
	 * @param name The name of the instance whose proxy should be retrieved as specified
	 * 	in the instance template. 
	 * @return The instance proxy for the specified instance or null if such an instance
	 * 	or proxy does not exist.
	 */
	public IInstanceProxy getProxy(String name);
	
	/**
	 * Returns the instance state object for the specified instance that is bound to
	 * the instance. The name passed as argument corresponds to the name specified in
	 * the template of the contract. The instance state allows an application to control
	 * invocation recording and checkpoint. Both, can be used in order to improve the
	 * efficiency of the associated mechanisms. 
	 * 
	 * @param name The name of the instance whose state should be retrieved as specified
	 *  in the instance template.
	 * @return The instance state object that can be used to control the automated 
	 * 	application state restoration.
	 */
	public IInstanceRestorer getRestorer(String name);
	
	/**
	 * Returns the event collector for the specified instance that is bound to this
	 * instance. The name of the instance corresponds to the name specified in the 
	 * template. The events emitted by the collector correspond to the events specified
	 * in the template contract.
	 * 
	 * @param name The name of the instance whose event collector should be retrieved. 
	 * @return The event collector of the specified instance or null if such a collector
	 * 	or instance does not exist.
	 */
	public IEventCollector getCollector(String name);
	
	/**
	 * Returns the accessor object for the specified resource assignment that is bound
	 * to this instance. The name of the resource assignment corresponds to the name
	 * specified by the contract of the instance. The type of the accessor depends on
	 * the resource. Some resources might not have an accessor, thus this method might
	 * return null for them.
	 * 
	 * @param name The name of the resource dependency for which the accessor object
	 * 	should be retrieved.
	 * @return The accessor object or null if there is none. The type depends on the
	 * 	type of the resource bound to the specified name.
	 */
	public Object getAccessor(String name);
	
	/**
	 * Returns the event emitter of the instance. The event emitter can be used to 
	 * transfer events to the parent of the instance. If the instance denotes the
	 * top of the application, the event emitter will still be available, however,
	 * the implementation of the emitter will simply consume the events silently.
	 * 
	 * @return The event emitter of the instance.
	 */
	public IEventEmitter getEmitter();

}