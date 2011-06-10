package info.pppc.pcom.system.model.component;


/**
 * The event collector is the basic interface of collectors provided
 * by the pcom container. It enables a component to register for events
 * created by a specific instance that is bound to some dependency.
 * 
 * @author Mac
 */
public interface IEventCollector {

	/**
	 * Adds a specified event listener to the set of event listeners
	 * that listen for events emitted by the instance bound to the
	 * dependency. If the listener is null, this method does 
	 * nothing. If the listener has been added already, this method
	 * will add it again. The order in which a listener is registered
	 * reflects the order in which the listeners will receive events.
	 * 
	 * @param listener The listener to register.
	 */
	public void addEventListener(IEventListener listener);
	
	/**
	 * Removes the specified event listener from the set of event
	 * listeners. If the listener is null or if it is not registered,
	 * this method does nothing. If the listener has been added
	 * multiple times, this method will remove one instance of the
	 * listener. The return value indicates whether a listener
	 * has been removed. The removed listener will be the first
	 * listener found using the ordering introduced during addition.
	 * 
	 * @param listener The listener that should be removed.
	 * @return True if the listener has been removed, false if no
	 * 	listener has been removed.
	 */
	public boolean removeEventListener(IEventListener listener);

	/**
	 * Returns all event listeners that are currently registered.
	 * The order of the listeners reflects the order in which
	 * the listeners have been added. If no listeners are registered,
	 * this method will return an array of size 0.
	 * 
	 * @return An array of event listeners that have been registered
	 * 	for the events of this instance. 
	 */
	public IEventListener[] getEventListeners();
	
}
