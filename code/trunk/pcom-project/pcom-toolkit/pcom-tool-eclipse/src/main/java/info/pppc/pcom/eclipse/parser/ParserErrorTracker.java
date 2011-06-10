package info.pppc.pcom.eclipse.parser;

import java.util.Vector;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * The parser error tracker implements an error handler for sax exceptions
 * that logs the exceptions as positioned parser markers.
 * 
 * @author Mac
 */
public class ParserErrorTracker implements ErrorHandler {

	/**
	 * The storage for markers that are tracked within this
	 * error tracker.
	 */
	private Vector markers = new Vector();

	/**
	 * Creates a new parser error tracker.
	 */
	public ParserErrorTracker() {
		super();
	}

	/**
	 * Determines whether the error tracker has tracked any
	 * errors.
	 * 
	 * @return True if the tracker has tracked errors.
	 */
	public boolean isEmpty() {
		return markers.isEmpty();
	}

	/**
	 * Removes all markers that have been tracked by this tracker.
	 */
	public void clear() {
		markers.clear();
	}

	/**
	 * Returns the position parse markers that have been tracked so far.
	 * 
	 * @return The position parse markers that have been tracked.
	 */
	public ParserMarker[] getMarkers() {
		return (ParserMarker[])markers.toArray
			(new ParserMarker[0]);
	}

	/**
	 * Called whenever an error is tracked. This creates a new marker
	 * from the sax exception.
	 * 
	 * @param se The sax exception that occured.
	 */
	private void create(SAXParseException se) {
		int line = se.getLineNumber();
		int column = se.getColumnNumber();
		String message = se.getMessage();
		ParserMarker marker = 
			new ParserMarker(message, line, column);
		markers.addElement(marker);
	}

	/**
	 * Called by xerces whenever an error occurs.
	 * 
	 * @param se The sax parse exception that caused the error.
	 */
	public void error(SAXParseException se) {
		create(se);
	}

	/**
	 * Called by xerces whenever a fatal error occurs.
	 * 
	 * @param se The sax parse exception that caused the fatal error.
	 */	
	public void fatalError(SAXParseException se) {
		create(se);
	}

	/**
	 * Called by xerces whenever a warning occurs.
	 * 
	 * @param se The sax parse exception that caused the warning.
	 */
	public void warning(SAXParseException se) {
		create(se);
	}

}
