package info.pppc.pcom.system.model.contract.reader;

/**
 * The instance demand reader provides read-only access to a single
 * instance demand. The instance demand describes the requirements 
 * towards a component instance at runtime. The demand is expressed
 * in terms of required interfaces and events. These can be in turn
 * enriched with non-functional parameter dimenstions.
 * 
 * @author Mac
 */
public interface IInstanceDemandReader extends IResourceDemandReader {

	/**
	 * Returns the type demands that represent event demands directed
	 * towards the instance. The event demands can be enriched with
	 * non-functional parameters to demand a certain quality.
	 * 
	 * @return The events that must be provided by the instance.
	 */
	public ITypeDemandReader[] getEvents();

	/**
	 * Returns the type demand that represents an event demand with
	 * the specified name or null if such an event is not demanded.
	 * 
	 * @param name The name of the event demand.
	 * @return The event demand with the specified name or null if
	 * 	such an interface demand does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeDemandReader getEvent(String name);
	
}
