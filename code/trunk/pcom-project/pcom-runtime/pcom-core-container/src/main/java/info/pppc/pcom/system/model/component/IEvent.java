package info.pppc.pcom.system.model.component;

/**
 * This interface is used by event listeners that can be registered
 * at collectors. An event has a certain source, namely the collector whose
 * binding has emitted the event, a type that is specified by the
 * instance that created the event and a data object that is specified
 * by the creator too.
 * 
 * @author Mac
 */
public interface IEvent {

	/**
	 * Returns the source of the event. As events are emitted
	 * by instances that are bound to an instance, this method
	 * will point to the corresponding source emitter that 
	 * received the event.
	 * 
	 * @return The source of the event.
	 */
	public IEventCollector getSource();
	
	/**
	 * The type of the event. This will be the string that is
	 * negotiated in the contract. 
	 * 
	 * @return The type of the event that has been sent. This
	 * 	will be the parameter as defined in the contract.
	 */
	public String getType();
	
	/**
	 * The data object that has been sent as event object. 
	 * 
	 * @return The data object of this event.
	 */
	public Object getData();

}
