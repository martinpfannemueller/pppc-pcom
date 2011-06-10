package info.pppc.pcom.system.model.component;

/**
 * The instance checkpoint is used to create snapshots
 * of the state of an instance at runtime. That way, an
 * the system can purge the invocation history in order
 * to reduce the amount of state that needs to be stored
 * to enable the replacement of stateful components.
 * 
 * A checkpoint is essentially a list of name value pairs
 * whose value must be serializable. The number and structure
 * of the pairs supported by a component is up to the 
 * interface developer of the component interface.
 * 
 * A checkpoint can either be complete, in which case it
 * contains all information needed to restore the state of
 * the instance or it can be differential, in which case
 * it must be merged with the previously recorded 
 * checkpoint in order to get the complete information
 * that is necessary to restore the state of the instance. 
 * 
 * @author Mac
 */
public interface IInstanceCheckpoint {

	/**
	 * Sets the integer property with the specified name.
	 * 
	 * @param name The name of the property.
	 * @param value The value of the property to set.
	 */
	public void putInteger(String name, int value);
	
	/**
	 * Returns an integer property with the specified name.
	 * If the property is unset or if the propery is not a
	 * valid integer, the method will throw a runtime 
	 * exception.
	 * 
	 * @param name The name of the property to retreive.
	 * @return The current value of the property.
	 */
	public int getInteger(String name);
	
	/**
	 * Sets the integer property with the specified name.
	 * 
	 * @param name The name of the property.
	 * @param value The value of the property to set.
	 */
	public void putLong(String name, long value);
	
	/**
	 * Returns an long property with the specified name.
	 * If the property is unset or if the propery is not a
	 * valid long, the method will throw a runtime 
	 * exception.
	 * 
	 * @param name The name of the property to retreive.
	 * @return The current value of the property.
	 */
	public long getLong(String name);
	
	/**
	 * Sets the string property with the specified name.
	 * 
	 * @param name The name of the property.
	 * @param value The value of the property to set.
	 */
	public void putString(String name, String value);
	
	/**
	 * Returns an string property with the specified name.
	 * If the property is unset or if the propery is not a
	 * valid string, the method will throw a runtime 
	 * exception.
	 * 
	 * @param name The name of the property to retreive.
	 * @return The current value of the property.
	 */
	public String getString(String name);
	
	/**
	 * Sets the object property with the specified name.
	 * The object must be serializable, otherwise the
	 * method will throw a runtime exception.
	 * 
	 * @param name The name of the property.
	 * @param value The value of the property to set.
	 */
	public void putObject(String name, Object value);
	
	/**
	 * Returns an object property with the specified name.
	 * If the property is unset, the method will throw a 
	 * runtime exception.
	 * 
	 * @param name The name of the property to retreive.
	 * @return The current value of the property.
	 */
	public Object getObject(String name);
	
	/**
	 * Sets the boolean property with the specified name.
	 * 
	 * @param name The name of the property.
	 * @param value The value of the property to set.
	 */
	public void putBoolean(String name, boolean value);
	
	/**
	 * Returns an boolean property with the specified name.
	 * If the property is unset or if the propery is not a
	 * valid boolean, the method will throw a runtime 
	 * exception.
	 * 
	 * @param name The name of the property to retreive.
	 * @return The current value of the property.
	 */
	public boolean getBoolean(String name);
	
	/**
	 * Determines whether the property with the specified name
	 * is set.
	 * 
	 * @param name The name of the property to test.
	 * @return True if the property is set, false otherwise.
	 */
	public boolean contains(String name);
	
	/**
	 * Unsets the specified property. For a differential checkpoint
	 * calling this method records that the value should be 
	 * removed from the existing checkpoint.
	 * 
	 * @param name The name of the value to unset.
	 */
	public void remove(String name);
	
	/**
	 * Enumerates the names of properties that are currently
	 * stored in this checkpoint.
	 * 
	 * @return The names of properties that are stored in the
	 * 	checkpoint.
	 */
	public String[] enumerate();
	
	/**
	 * Determines whether the checkpoint is complete or whether
	 * it is marked as differential.
	 * 
	 * @return True if the checkpoint is self-contained, false
	 * 	if it is differential.
	 */
	public boolean isComplete();
	
	/**
	 * Sets a flag that marks the checkpoint as self-contained
	 * (if true) or differential (if false). Differential 
	 * checkpoints are merged with the checkpoint that might
	 * be contained on the side of the "using" instance in
	 * order to get a complete checkpoint. Thus, the checkpoints
	 * at the side of the "using" instance are always 
	 * complete.
	 * 
	 * @param complete A flag that marks the checkpoint as
	 * 	differential or complete.
	 */
	public void setComplete(boolean complete);
	
}
