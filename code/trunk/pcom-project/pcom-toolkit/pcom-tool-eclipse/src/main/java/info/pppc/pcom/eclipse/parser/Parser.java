package info.pppc.pcom.eclipse.parser;

import info.pppc.pcom.eclipse.parser.xerces.LocatorDocument;
import info.pppc.pcom.eclipse.parser.xerces.LocatorParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The parser can be used to parse pcom component descriptions. The parser
 * performs inital validations using the xml schema and using more specific
 * application knowledge to evaluate the structure and contents of feature
 * descriptions.
 * 
 * @author Mac
 */
public class Parser {

	/**
	 * The validators to use. These validators are executed after the
	 */
	private Vector validators = new Vector();
	
	/**
	 * The parser handler that can be used to observe the parsing process.
	 */
	private IParserHandler handler;
	
	/**
	 * Creates a new parser without any validators.
	 */
	public Parser() {
		this(new IParserValidator[0]);
	}

	/**
	 * Creates a new parser with the specified validators. The validator array 
	 * must not be null.
	 *  
	 * @param validators The validators used to validate the description.
	 * @throws NullPointerException Thrown if the array of validators is null.
	 */
	public Parser(IParserValidator[] validators) {
		if (validators == null) throw new NullPointerException("Validators must not be null.");
		for (int i = 0; i < validators.length; i++) {
			if (validators[i] == null) throw new NullPointerException("Validator must not be null.");
			this.validators.addElement(validators[i]);
		}
	}

	/**
	 * Adds the specified parser validator to the set of validators.
	 * 
	 * @param validator The parser validator to add.
	 */
	public synchronized void addValidator(IParserValidator validator) {
		if (validator == null) throw new NullPointerException("Validator must not be null.");
		validators.add(validator);
	}
	
	/**
	 * Removes the specified parser validator from the set of validators.
	 * 
	 * @param validator The parser validator to remove.
	 * @return True if the parser validator has been found, false if it
	 * 	was not present.
	 */
	public synchronized boolean removeValidator(IParserValidator validator) {
		if (validator == null) throw new NullPointerException("Validator must not be null.");
		return validators.remove(validator);
	}
	
	/**
	 * Returns the parser handler that is currently registered or null if
	 * there is non registered. The parser handler can be used to observe 
	 * or abort a parsing process.
	 * 
	 * @return The parser handler that is currently registered or null.
	 */
	public synchronized IParserHandler getHandler() {
		return handler;
	}

	/**
	 * Sets the current parser handler and removes any previously registered
	 * handler. The parser handler can be used to observe or abort a parsing
	 * process.
	 * 
	 * @param handler The parser handler to register.
	 */
	public synchronized void setHandler(IParserHandler handler) {
		this.handler = handler;
	}

	/**
	 * Parses a pcom component description from the given file.
	 * 
	 * @param file The file that contains the contract.
	 * @return A document that contains the parsed and validated pcom
	 * 	component description.
	 * @throws ParserException The parser exception that contains more details
	 * 	about parse errors.
	 */
	public synchronized LocatorDocument parse(File file) throws ParserException {
		if (file == null) throw new ParserException("File must not be null.");
		// create uri from file name
		String uri = "file:" + file.getAbsolutePath();
	   	if (File.separatorChar == '\\') {
			uri = uri.replace('\\', '/');
		}
		// create input source from uri
		InputSource input = new InputSource(uri);
		return parse(input);
	}
	
	/**
	 * Parses a pcom component description from the given input stream.
	 * 
	 * @param stream The stream that contains the contract.
	 * @return A document that contains the parsed and validated pcom
	 * 	component description.
	 * @throws ParserException The parser exception that contains more details
	 * 	about parse errors.
	 */
	public synchronized LocatorDocument parse(InputStream stream) throws ParserException {
		if (stream == null) throw new ParserException("Stream must not be null.");
		InputSource input = new InputSource(stream);
		return parse(input);
	}
	
	/**
	 * Parses a pcom component description from the given input source.
	 * 
	 * @param input The input source to parse from.
	 * @return A document that contains the parsed and validated pcom
	 * 	component description.
	 * @throws ParserException The parser exception that contains more details
	 * 	about parse errors.
	 */
	private LocatorDocument parse(InputSource input) throws ParserException {
		try {
			// signal begin to parser handler
			if (handler != null) {
				if (! handler.begin("XML Parsing and Schema Validation", validators.size() + 1)) {
					throw new ParserException("XML Parsing and Schema Validation aborted.");
				}		
			}
			// create parser and register hooks for error handling
			LocatorParser parser = new LocatorParser();
			ParserErrorTracker tracker = new ParserErrorTracker();
			parser.setErrorHandler(tracker);
			// parse document
			parser.parse(input);
			// handle parse failures and xml schema failures
			if (! tracker.isEmpty()) {
				ParserException e = new ParserException("XML Parsing and Schema Validation failed.");
				ParserMarker[] markers = tracker.getMarkers();
				for (int i = 0; i < markers.length; i++) {
					e.addMarker(markers[i]);
				}
				throw e;
			}
			LocatorDocument document = parser.getLocatorDocument();
			// perform additional validation through validators
			for (int i = 0; i < validators.size(); i++) {
				IParserValidator validator = (IParserValidator)validators.elementAt(i);
				// signal next step to handler
				if (handler != null) {
					if (! handler.step(validator.getDescription(), 1)) {
						throw new ParserException("Validation aborted at " + validator.getDescription() + ".");
					}
				}
				// perform validation
				ParserMarker[] markers = validator.validate(document);
				if (markers != null && markers.length > 0) {
					ParserException e = new ParserException(validator.getDescription() + " failed.");
					for (int j = 0; j < markers.length; j++) {
						e.addMarker(markers[j]);
					}
					throw e;	
				} 
			}
			return document;
		} catch (SAXException ex) {
			throw new ParserException("SAX Exception (" + ex.getMessage() + ").");
		} catch (IOException ex) {
			throw new ParserException("IO Exception (" + ex.getMessage() + ").");
		} finally {
			if (handler != null) {
				handler.finish();	
			}	
		}
		
	}

	/**
	 * Starts a simple parser test.
	 * 
	 * @param args The command line parameters.
	 */
	public static void main(String[] args) {
		try {
			Parser p = new Parser();
			LocatorDocument d = p.parse(new File("C:\\text.xml"));
			if (d != null) {
				System.out.println(d);	
			}
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}

}
