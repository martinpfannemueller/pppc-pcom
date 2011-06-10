package info.pppc.pcom.system.model.component;

/**
 * The listener interface must be implemented by event listeners
 * that want to register for events emitted by component instances
 * that are bound to another instance.
 * 
 * @author Mac
 */
public interface IEventListener {

	/**
	 * Called whenever an event of a parent component is received.
	 * 
	 * @param event The event that has been received.
	 */
	public void receivedEvent(IEvent event);

}
