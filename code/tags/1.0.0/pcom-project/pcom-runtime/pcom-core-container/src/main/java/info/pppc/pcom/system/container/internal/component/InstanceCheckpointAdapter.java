package info.pppc.pcom.system.container.internal.component;

import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;

/**
 * The instance checkpoint adapter is used to adapt instance checkpoints
 * used by the container to instance checkpoints that can be used by
 * instances.
 * 
 * @author Mac
 */
public class InstanceCheckpointAdapter implements IInstanceCheckpoint {
	
	/**
	 * The checkpoint to adapt to.
	 */
	private InstanceCheckpoint checkpoint;
	
	/**
	 * Creates a new instance checkpoint adapter that adapts
	 * the specified checkpoint.
	 * 
	 * @param checkpoint The checkpoint to adapt to.
	 */
	public InstanceCheckpointAdapter(InstanceCheckpoint checkpoint) {
		this.checkpoint = checkpoint;
	}
	
	/**
	 * Determines whether the attribute with the specified
	 * name is contained in the checkpoint.
	 * 
	 * @param name The name of the attribute.
	 * @return True if contained, false otherwise.
	 */
	public boolean contains(String name) {
		return checkpoint.contains(name);
	}

	/**
	 * Enumerates the names of all attributes of the checkpoint.
	 * 
	 * @return The names of the attributes.
	 */
	public String[] enumerate() {
		return checkpoint.enumerate();
	}

	/**
	 * Return the attribute value with the specified
	 * name as boolean.
	 * 
	 * @param name The name of the attribute.
	 * @return The value.
	 */
	public boolean getBoolean(String name) {
		return ((Boolean)checkpoint.getObject(name)).booleanValue();
	}

	/**
	 * Return the attribute value with the specified
	 * name as integer.
	 * 
	 * @param name The name of the attribute.
	 * @return The value.
	 */

	public int getInteger(String name) {
		return ((Integer)checkpoint.getObject(name)).intValue();
	}

	/**
	 * Return the attribute value with the specified
	 * name as long.
	 * 
	 * @param name The name of the attribute.
	 * @return The value.
	 */

	public long getLong(String name) {
		return ((Long)checkpoint.getObject(name)).longValue();
	}

	/**
	 * Return the attribute value with the specified
	 * name as object.
	 * 
	 * @param name The name of the attribute.
	 * @return The value.
	 */

	public Object getObject(String name) {
		return checkpoint.getObject(name);
	}

	/**
	 * Return the attribute value with the specified
	 * name as string.
	 * 
	 * @param name The name of the attribute.
	 * @return The value.
	 */

	public String getString(String name) {
		return (String)checkpoint.getObject(name);
	}

	/**
	 * Returns the status of the complete flag.
	 * 
	 * @return The status of the complete flag.
	 */
	public boolean isComplete() {
		return checkpoint.isComplete();
	}

	/**
	 * Sets the attribute with the specified name to the
	 * specified boolean value.
	 * 
	 * @param name The name of the attribute.
	 * @param value The name of the value.
	 */
	public void putBoolean(String name, boolean value) {
		checkpoint.putObject(name, new Boolean(value));
	}

	/**
	 * Sets the attribute with the specified name to the
	 * specified integer value.
	 * 
	 * @param name The name of the attribute.
	 * @param value The name of the value.
	 */
	public void putInteger(String name, int value) {
		checkpoint.putObject(name, new Integer(value));
	}

	/**
	 * Sets the attribute with the specified name to the
	 * specified long value.
	 * 
	 * @param name The name of the attribute.
	 * @param value The name of the value.
	 */
	public void putLong(String name, long value) {
		checkpoint.putObject(name, new Long(value));
	}

	/**
	 * Sets the attribute with the specified name to the
	 * specified object value.
	 * 
	 * @param name The name of the attribute.
	 * @param value The name of the value.
	 */
	public void putObject(String name, Object value) {
		checkpoint.putObject(name, value);
	}

	/**
	 * Sets the attribute with the specified name to the
	 * specified string value.
	 * 
	 * @param name The name of the attribute.
	 * @param value The name of the value.
	 */
	public void putString(String name, String value) {
		checkpoint.putObject(name, value);
	}

	/**
	 * Remove the specified attribute. If the checkpoint
	 * is marked as differential, the removal will be recorded
	 * in order to apply it to the existing complete one.
	 * 
	 * @param name The name of the attribute to remove.
	 */
	public void remove(String name) {
		checkpoint.remove(name);
	}

	/**
	 * Sets a flag that determines whether the checkpoint
	 * is complete or differential.
	 * 
	 * @param complete True to flag complete, false for 
	 * 	differential.
	 */
	public void setComplete(boolean complete) {
		checkpoint.setComplete(complete);
	}

}
