package info.pppc.pcom.system.container;

import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ObjectInputStream;
import info.pppc.base.system.io.ObjectOutputStream;
import info.pppc.pcom.system.container.io.ChunkInputStream;
import info.pppc.pcom.system.container.io.ChunkOutputStream;
import info.pppc.pcom.system.container.io.ChunkVector;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The instance checkpoint is used to store the state of an instance
 * that is hosted on some container. 
 * 
 * @author Mac
 */
public class InstanceCheckpoint {
	/**
	 * The abbreviation for a checkpoint in its serialized
	 * form.
	 */
	public static final String ABBREVIATION = ";PO";
	
	/**
	 * The complete flag that differentiates full and 
	 * differential checkpoints.
	 */
	private boolean complete = true;
	
	/**
	 * The hashtable that stores the values.
	 */
	private Hashtable values = new Hashtable();
	
	/**
	 * The vector that stores the removed attributes.
	 */
	private Vector removed = new Vector();
	
	/**
	 * Creates a new instance checkpoint with no values and
	 * an initialization as complete.
	 */
	public InstanceCheckpoint() {
		super();
	}
	
	/**
	 * Determines whether the attribute with the specified
	 * name is contained in the checkpoint.
	 * 
	 * @param name The name of the attribute.
	 * @return True if contained, false otherwise.
	 */
	public boolean contains(String name) {
		return values.containsKey(name);
	}

	/**
	 * Enumerates the names of all attributes of the checkpoint.
	 * 
	 * @return The names of the attributes.
	 */
	public String[] enumerate() {
		String[] result = new String[values.size()];
		Enumeration e = values.keys();
		for (int i = 0; i < result.length; i++) {
			result[i] = (String)e.nextElement();			
		}
		return result;
	}

	/**
	 * Returns the object with the specified name.
	 * 
	 * @param name The name of the object to return.
	 * @return The object stored under the name.
	 */
	public Object getObject(String name) {
		ChunkVector cv = (ChunkVector)values.get(name);
		try {
			ChunkInputStream cis = new ChunkInputStream(cv, false);
			ObjectInputStream ois = new ObjectInputStream(cis);
			return ois.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Deserialization failed.");
		}
	}

	/**
	 * Returns the status of the complete flag.
	 * 
	 * @return The status of the complete flag.
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets the attribute with the specified name to the
	 * specified object value.
	 * 
	 * @param name The name of the attribute.
	 * @param value The name of the value.
	 */
	public void putObject(String name, Object value) {
		removed.removeElement(name);
		try {
			ChunkVector cv = new ChunkVector();
			ChunkOutputStream cos = new ChunkOutputStream(cv, false);
			ObjectOutputStream oos = new ObjectOutputStream(cos);
			oos.writeObject(value);
			oos.close();
			values.put(name, cv);
		} catch (IOException e) {
			throw new RuntimeException("Serialization failed.");
		}
	}

	/**
	 * Remove the specified attribute. If the checkpoint
	 * is marked as differential, the removal will be recorded
	 * in order to apply it to the existing complete one.
	 * 
	 * @param name The name of the attribute to remove.
	 */
	public void remove(String name) {
		values.remove(name);
		if (! complete) {
			removed.removeElement(name);
			removed.addElement(name);			
		}
	}

	/**
	 * Sets a flag that determines whether the checkpoint
	 * is complete or differential.
	 * 
	 * @param complete True to flag complete, false for 
	 * 	differential.
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
		if (complete == true) {
			removed.removeAllElements();
		}
	}
	
	/**
	 * Deserializes the object from a given input stream.
	 * 
	 * @param input The input to read from.
	 * @throws IOException Thrown by the underlying io system.
	 */
	public void readObject(IObjectInput input) throws IOException {
		complete = input.readBoolean();
		removed = (Vector)input.readObject();
		int vx = input.readInt();
		for (int i = 0; i < vx; i++) {
			String key = input.readUTF();
			ChunkVector cv = new ChunkVector();
			values.put(key, cv);
			int todo = input.readInt();
			while (todo > 0) {
				byte[] chunk = new byte[Math.min(2048, todo)];
				input.readBytes(chunk);
				cv.append(chunk);
				todo -= chunk.length;
			}
		}
	}
	
	/**
	 * Serializes the object to a given output stream.
	 * 
	 * @param output The input to read from.
	 * @throws IOException Thrown by the underlying io system.
	 */	
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeBoolean(complete);
		output.writeObject(removed);
		output.writeInt(values.size());
		Enumeration e = values.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			output.writeUTF(key);
			Object value = values.get(key);
			ChunkVector cv = (ChunkVector)value;
			output.writeInt(cv.getTotal());
			for (int j = 0; j < cv.getChunks(); j++) {
				output.writeBytes(cv.getChunk(j), cv.getStart(j), cv.getEnd(j) - cv.getStart(j));
			}
		}
	}
	
	/**
	 * Applies this differential checkpoint to some complete existing
	 * checkpoint by modifying the checkpoint so that it reflects
	 * the new and complete checkpoint.
	 * 
	 * @param checkpoint The complete checkpoint that should receive
	 * 	the modifications.
	 */
	public void apply(InstanceCheckpoint checkpoint) {
		for (int i = 0; i < removed.size(); i++) {
			checkpoint.remove((String)removed.elementAt(i));			
		}
		Enumeration e = values.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			Object value = values.get(key);
			checkpoint.values.put(key, value);
		}
	}
	
}
