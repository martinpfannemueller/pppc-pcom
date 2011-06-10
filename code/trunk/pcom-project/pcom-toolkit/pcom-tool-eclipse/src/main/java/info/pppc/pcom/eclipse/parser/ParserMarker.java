package info.pppc.pcom.eclipse.parser;

/**
 * The parser marker denotes a failure that occured during parsing. The parser marker
 * can be used to point to a specific line and column of the source that produced the
 * error.
 * 
 * @author Mac
 */
public class ParserMarker {

	/**
	 * The value that denotes an undefined line or column number.
	 */
	public static final int UNDEFINED = -1;

	/**
	 * The column number. This is either -1 or any value larger or equal to 1.
	 */
	private int column = UNDEFINED;
	
	/**
	 * The line number of the failure. This is always greater or equal to -1.
	 */
	private int line = UNDEFINED;

	/**
	 * The message that denotes the parser failure.
	 */
	private String message;

	/**
	 * Creates a new parser marker with the specified detail
	 * message. The line and column number will be undefined. 
	 * The message must not be null.
	 * 
	 * @param message The message of the marker.
	 * @throws NullPointerException Thrown if the message is null. 
	 */
	public ParserMarker(String message) {
		if (message == null) throw new NullPointerException("Message must not be null.");
		this.message = message;
	}
	
	/**
	 * Creates a new parser marker that points to the specified
	 * position and provides the specified failure text.
	 * 
	 * @param message The message that describes the failure.
	 * @param line The line number. The first line is denoted by 0. If
	 * 	the line number is not defined, set it to UNDEFINED.
	 * @param column The column number. The first column is denoted by
	 * 	1. If the column is not defined, set it to UNDEFINDED.
	 */
	public ParserMarker(String message, int line, int column) {
		this(message);
		if (line >= -1) this.line = line;
		if (column >= 1) this.column = column;
	}
	
	/**
	 * The detail message that describes the failure.
	 * 
	 * @return The detail message that describes the failure.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Returns the line number of the failure.
	 * 
	 * @return The line number. Always larger or equal to -1.
	 */
	public int getLine() {
		return line;
	}
	
	/**
	 * Returns the column number of the failure.
	 * 
	 * @return The column number. Always -1 or larger or equal to 1.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns a string representation.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		return getMessage() + " (Line " + getLine() + " Column " + getColumn() + ")";
	}

}
