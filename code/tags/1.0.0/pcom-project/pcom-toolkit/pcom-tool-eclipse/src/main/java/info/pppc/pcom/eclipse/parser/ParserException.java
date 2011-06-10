package info.pppc.pcom.eclipse.parser;

import java.util.Vector;

/**
 * The parser exception is used by the parser to signal that a certain
 * parsed document does not have a valid structure. The structural
 * failures are signaled through markers. Markers can either denote
 * specific failures within the document or they can denote unspecific
 * positions if the parser cannot determine the exact location of the
 * failure.
 * 
 * @author Mac
 */
public class ParserException extends Exception {

	/**
	 * Serialization id,
	 */
	private static final long serialVersionUID = 1L;
	 
	/**
	 * The markers carried by the exception. 
	 */
	private Vector markers = new Vector();

	/**
	 * Creates a new parser exception without a detail message
	 * and with no details.
	 */
	public ParserException() {
		super();
	}
	
	/**
	 * Creates a new parser exception with the specified detail
	 * message.
	 * 
	 * @param message The detail message.
	 */
	public ParserException(String message) {
		super(message);
	}
	
	/**
	 * Adds a marker that signals one of the failures that occured.
	 * 
	 * @param marker The marker to specify.
	 */
	public void addMarker(ParserMarker marker) {
		markers.addElement(marker);
	}
	
	/**
	 * Returns all markers that are carried by the exception. 
	 * 
	 * @return All markers carried by the exception.
	 */
	public ParserMarker[] getMarkers() {
		return (ParserMarker[])markers.toArray(new ParserMarker[0]);
	}
	
	/**
	 * Creates a human readable string representation of
	 * the contents.
	 * 
	 * @return The string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		String msg = getMessage();
		if (msg != null) {
			b.append(msg);
		} else {
			b.append("No details available.");
		}
		for (int i = 0; i < markers.size(); i++) {
			b.append("\n   ");
			b.append(markers.elementAt(i).toString());
		}
		return b.toString();
	}

}
