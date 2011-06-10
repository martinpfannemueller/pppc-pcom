package info.pppc.pcom.eclipse.parser;

import info.pppc.pcom.eclipse.parser.xerces.LocatorDocument;


/**
 * The parser validator interface can be used to implement additional
 * validation steps. These steps can then be added to the parser in 
 * order to have them performed whenever the parser tries to load a 
 * document.
 * 
 * @author Mac
 */
public interface IParserValidator {

	/**
	 * Returns a human readable description of the validation task.
	 * 
	 * @return A human readable description of the validation task.
	 */
	public String getDescription();

	/**
	 * Validates the specified document according to the rules of
	 * the validator.
	 * 
	 * @param document The document to validate.
	 * @return Null or an empty array if no failures have been found
	 * 	and the structure is valid. If the structure is not valid,
	 * 	a non-empty array that contains the failures. 
	 */
	public ParserMarker[] validate(LocatorDocument document);

}
