package info.pppc.pcom.system.model.contract.writer;


/**
 * The instance demand writer describes the demand directed towards
 * a single instance. In contrast to the instance demand reader, this 
 * interface provides read-write access to the instance demand.
 * 
 * @author Mac
 */
public interface IInstanceDemandWriter extends IResourceDemandWriter {
	
	/**
	 * Returns the type demands that represent event demands directed
	 * towards the instance. The event demands can be enriched with
	 * non-functional parameters to demand a certain quality.
	 * 
	 * @return The events that must be provided by the instance.
	 */
	public ITypeDemandWriter[] getEvents();
	
	/**
	 * Returns the event with the specified type name or null if no
	 * such event exists.
	 * 
	 * @param name The type name of the event to retrieve.
	 * @return The type demand with the specified type name or null if
	 * 	no such demand exists.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeDemandWriter getEvent(String name);
	
	/**
	 * Creates a new event demand for the specified type of event. If
	 * an event with the specified name exists already, it will be 
	 * replaced and returned.
	 * 
	 * @param name The type name of the event to create.
	 * @return The mutable version of the type demand that has been
	 * 	replaced or null if nothing has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeDemandWriter createEvent(String name);
	
}
