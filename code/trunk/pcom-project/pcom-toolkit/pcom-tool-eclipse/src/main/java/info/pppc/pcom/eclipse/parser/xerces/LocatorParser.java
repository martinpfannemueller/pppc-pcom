package info.pppc.pcom.eclipse.parser.xerces;

import java.util.HashMap;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * The locator parser extends the functionality of the basic xerces parser.
 * It provides locator documents that can be used to retrieve the line 
 * numbers and column positions of nodes contained in the document. In 
 * contrast to the base xerces parser, this parser configures itself as 
 * validating using schemas. This default behaviour can be changed by
 * setting the corresponding features. 
 * 
 * @author Mac
 */
public class LocatorParser extends DOMParser {

	/**
	 * The hashmap that maps document nodes to line numbers as integers.
	 */
	protected HashMap lines;
	
	/**
	 * The hashmap that maps document nodes to column positions as integers.
	 */
	protected HashMap columns;

	/**
	 * The locator that is referenced during parsing in order to retrieve
	 * the line numbers and column positions of nodes.
	 */
	protected XMLLocator locator;

	/**
	 * Creates a new locator parser that is validating inputs using XML 
	 * schema.
	 */
	public LocatorParser() {
		try {
			setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE, true);
			setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING, true);
			setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, true);
			setFeature(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE,true);
			setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.DEFER_NODE_EXPANSION_FEATURE, false);
		} catch (SAXException ex) {
			throw new RuntimeException("Unknown exception caught in locator parser.");	
		}
	}

	/**
	 * Overwritten method that adds the parsed node to the node list.
	 * 
	 * @param element The name of the element to create.
	 * @param attributes The attributes of the element.
	 * @param augs Additional augmentations.
	 * @throws XNIException Thrown by the super implementation.
	 */
	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		super.startElement(element, attributes, augs);
		updateLocator();
	}
	
	/**
	 * Called whenever a new document is parsed. This will set the internal
	 * locator that is used to retrieve the source position (i.e. column and
	 * line number) for the present node.
	 * 
	 * @param locator The locator that maintains the position.
	 * @param encoding The document encoding.
	 * @param namespaceContext The namespace context.
	 * @param augs Additional augmentations.
	 * @throws XNIException Thrown by the super implementation.
	 */
	public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
		this.locator = locator;
		super.startDocument(locator, encoding, namespaceContext, augs);
		updateLocator();
	}
	
	/**
	 * Called whenever a new node is parsed. This method will try to lookup
	 * the current node that is created by the super implementation and it
	 * will adjust the local hashmaps with line numbers and column numbers
	 * of the corresponding node.
	 */
	protected void updateLocator() {
		try {
			Node node = (Node)getProperty(CURRENT_ELEMENT_NODE);
			if (node != null) {
				lines.put(node, new Integer(locator.getLineNumber()));
				columns.put(node, new  Integer(locator.getColumnNumber()));
			}
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown exception caught in update locator.");		
		}
	}

	/**
	 * Called whenever a parsing process is about to be started.
	 * This will initalize the local maps and then it will call 
	 * the super implementation to intialize the base class data
	 * structures.
	 * 
	 * @throws XNIException Thrown by the base implementation.
	 */
	public void reset() throws XNIException {
		lines = new HashMap();
		columns = new HashMap();
		locator = null;
		super.reset();
	}

	/**
	 * Returns the locator document that has been created by the
	 * last call to one of the parse methods. If the parse method
	 * has not been called so far, this method returns null.
	 * 
	 * @return Null or the locator document that has been parsed
	 * 	during the last call to one of the parse methods.
	 */
	public LocatorDocument getLocatorDocument() {
		Document document = getDocument();
		if (document != null) {
			return new LocatorDocument(document, lines, columns);	
		} else {
			return null;
		}
	}
 
}