package info.pppc.pcom.system.model.component;

import info.pppc.base.system.InvocationException;

/**
 * The event emitter is the basic interface for all event emitters.
 * A handle to the event emitter can be created through the container
 * object of an instance that is currently executing.
 * 
 * @author Mac
 */
public interface IEventEmitter {

	/**
	 * Emits an event to the parent of the component instance. The
	 * event is declared as an event of the specified type and the
	 * data object is sent as the data object of the event.
	 * 
	 * @param type The type of the event that should be announced.
	 * 	This field must be defined in the contract.
	 * @param data The data object of the emitter.
	 * @throws InvocationException Thrown if the event could not
	 * 	be delivered.
	 */
	public void emitEvent(String type, Object data) throws InvocationException;

}
