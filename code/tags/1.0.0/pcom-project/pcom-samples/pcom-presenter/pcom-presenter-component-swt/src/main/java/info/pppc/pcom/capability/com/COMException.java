package info.pppc.pcom.capability.com;

/**
 * The com exception is used by the com allocator to wrap exceptions
 * that occur while a command is executed.
 * 
 * @author Mac
 */
public class COMException extends Exception {

	/**
	 * The reason that caused the exception.
	 */
	protected Throwable reason;
	
	/**
	 * Creates a new exception with the specified detail
	 * message and the specified reason.
	 * 
	 * @param message The detail message of the exception.
	 * @param reason The reason of the exception.
	 */
	public COMException(String message, Throwable reason) {
		super(message);
		this.reason = reason;
	}
	
	/**
	 * Returns the reason of the com exception.
	 * 
	 * @return The reason of the exception.
	 */
	public Throwable getReason() {
		return reason;
	}
	
	/**
	 * Returns a string representation of the exception.
	 * 
	 * @return A string representation of the exception.
	 */
	public String toString() {
		return super.toString() + " Reason: " + reason;
	}

}
