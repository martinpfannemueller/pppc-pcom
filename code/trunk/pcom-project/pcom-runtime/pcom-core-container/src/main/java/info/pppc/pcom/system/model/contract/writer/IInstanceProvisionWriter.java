package info.pppc.pcom.system.model.contract.writer;


/**
 * The instance provision writer describes the provision of a single 
 * instance. In contrast to the instance provision, the writer version
 * provides read-write access to interfaces and events.
 * 
 * @author Mac
 */
public interface IInstanceProvisionWriter extends IResourceProvisionWriter {
	
	/**
	 * Returns the events provided by this provision as mutable type
	 * provisions.
	 * 
	 * @return The events provided by this provision.
	 */
	public ITypeProvisionWriter[] getEvents();

	/**
	 * Returns the type provision writer of the event with the specified 
	 * name or null if no such event has been specified.
	 * 
	 * @param name The name of the interface to retrieve.
	 * @return The type provision writer for the event or null if it does
	 * 	not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeProvisionWriter getEvent(String name);
	
	/**
	 * Creates a new event provided by this provision with the
	 * specified type. If an event provision with the specified name 
	 * exists already, it will be replaced and returned.
	 * 
	 * @param name The type name of the event to provide.
	 * @return The event provision that has been replaced or null
	 * 	if nothing has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeProvisionWriter createEvent(String name);
		
}
