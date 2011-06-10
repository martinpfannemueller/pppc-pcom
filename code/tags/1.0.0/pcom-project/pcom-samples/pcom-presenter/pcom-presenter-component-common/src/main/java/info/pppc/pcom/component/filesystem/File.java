package info.pppc.pcom.component.filesystem;

import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;

import java.io.IOException;

/**
 * A data type that encapsulates file information.
 * 
 * @author Mac
 */
public class File implements ISerializable {

	/**
	 * Determines whether the file is a directory.
	 */
	private boolean directory = false;
	
	/**
	 * Determines whether the directory is empty.
	 */
	private boolean empty = true;
	
	/**
	 * The absolute path of the file. 
	 */
	private String path = null;
	
	/**
	 * The name of the file.
	 */
	private String name = null;
	
	/**
	 * Creates a new file. This constructor is solely used for
	 * serialization and deserialization. It should never be
	 * called by application code.
	 */
	public File() {
		super();
	}

	/**
	 * Reads the file from a given input stream.
	 * 
	 * @param input The input stream to read from.
	 * @throws IOException Thrown if the deserializion fails.
	 */
	public void readObject(IObjectInput input) throws IOException {
		directory = ((Boolean)input.readObject()).booleanValue();
		path = (String)input.readObject();
		name = (String)input.readObject();
		empty = input.readBoolean();
	}

	/**
	 * Writes the file to a given output stream.
	 * 
	 * @param output The output stream to write to.
	 * @throws IOException Thrown if the serializion fails.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeObject(new Boolean(directory));
		output.writeObject(path);
		output.writeObject(name);
		output.writeBoolean(empty);
	}

	/**
	 * Returns true if the file represents a directory, flase otherwise.
	 * 
	 * @return True if the file represents a directory, false otherwise.
	 */
	public boolean isDirectory() {
		return directory;
	}
	
	/**
	 * Sets the flag that indicates whether the file is a directory.
	 * 
	 * @param directory The flag that indicates whether the thing
	 * 	is a directory.
	 */
	protected void setDirectory(boolean directory) {
		this.directory = directory;
	}

	/**
	 * Returns the full path of the file.
	 * 
	 * @return The full path of the file.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Sets the path of the file.
	 * 
	 * @param path The path to set.
	 */
	protected void setPath(String path) {
		this.path = path;
	}

	/**
	 * Returns the name of the file.
	 * 
	 * @return The name of the file.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the file.
	 * 
	 * @param name The name of the file.
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Determines for a directory whether it is empty.
	 * 
	 * @return Returns whether the director is empty.
	 */
	public boolean isEmpty() {
		return empty;
	}
	
	/**
	 * Sets a flag for a directory that denotes whether it is empty.
	 * 
	 * @param empty The empty flag for the directory.
	 */
	protected void setEmpty(boolean empty) {
		this.empty = empty;
	}

}