package info.pppc.pcom.system.model.contract.reader;

/**
 * The instance provision interface provides read-only access to an instance
 * provision description. This description contains a name that identifies
 * the provided instance and its provided interfaces and events.
 * 
 * @author Mac
 */
public interface IInstanceProvisionReader extends IResourceProvisionReader {

	/**
	 * Returns the events that are provided by this instance. The 
	 * events have a certain type and they can be enriched with
	 * dimensions to model non-functional parameters.
	 * 
	 * @return The events provided by the instance or an empty array
	 * 	if there are no provided events.
	 */
	public ITypeProvisionReader[] getEvents();

	/**
	 * Returns the event that is provided to this instance and 
	 * has the specified name or null if such an event does not
	 * exist.
	 * 
	 * @param name The name of the event to retrieve.
	 * @return The event with the specified name or null if it
	 * 	does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeProvisionReader getEvent(String name);
	
}
