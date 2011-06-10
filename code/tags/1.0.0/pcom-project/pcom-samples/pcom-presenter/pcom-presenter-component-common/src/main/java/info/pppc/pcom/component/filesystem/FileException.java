package info.pppc.pcom.component.filesystem;

/**
 * An exception type that is thrown whenever the filesystem encounters
 * an error.
 * 
 * @author Mac
 */
public class FileException extends Exception {
	
	/**
	 * Creates a new file exception without a detail message.
	 */
	public FileException() {
		super();
	}

	/**
	 * Creates a file exception with the specified detail message.
	 * 
	 * @param message The detail message that describes the exception.
	 */		
	public FileException(String message) {
		super(message);
	}
}