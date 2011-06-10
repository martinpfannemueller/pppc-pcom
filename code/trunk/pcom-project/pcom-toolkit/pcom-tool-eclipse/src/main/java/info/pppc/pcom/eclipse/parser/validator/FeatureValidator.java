package info.pppc.pcom.eclipse.parser.validator;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import info.pppc.pcom.eclipse.parser.IParserValidator;
import info.pppc.pcom.eclipse.parser.ParserMarker;
import info.pppc.pcom.eclipse.parser.util.XMLUtility;
import info.pppc.pcom.eclipse.parser.xerces.LocatorDocument;

/**
 * The feature validator checks whether the feature types correspond to
 * java parseable types and for required features, it checks whether the
 * feature value, minimum and maximum adhere to the specified comparator.
 * 
 * @author Mac
 */
public class FeatureValidator implements IParserValidator {

	/**
	 * The name of the feature validator.
	 */
	private static final String DESCRIPTION = "Feature Validation";
	
	/**
	 * The value of the type field that denotes booleans. Their 
	 * content must be true or false.
	 */
	private static final String TYPE_BOOLEAN = "boolean";
	
	/**
	 * The value of the type field that denotes integers. Their
	 * content must be a parsable integer.
	 */
	private static final String TYPE_INTEGER = "integer";
	
	/**
	 * The value of the type field that denotes string. 
	 */
	private static final String TYPE_STRING = "string";
	
	/**
	 * The value of the type field that denotes long values. Their
	 * content must be a parsable long.
	 */
	private static final String TYPE_LONG = "long";
	
	/**
	 * The value of the value, minimum and maximum fields if the type
	 * is boolean true. 
	 */
	private static final String BOOLEAN_TRUE = "true";

	/**
	 * The value of the value, minimum and maximum fields if the type
	 * is boolean false. 
	 */	
	private static final String BOOLEAN_FALSE = "false";
	
	/**
	 * The comparators that denote range comparators. They must
	 * have a minimum and a maximum.
	 */	
	private static final String[] COMPARATOR_RANGE = {
		"inRange", "outRange"
	};
	
	/**
	 * The name of the feature node to parse.
	 */
	private static final String NODE_FEATURE = "feature";
	
	/**
	 * The name of the comparator node to parse.
	 */
	private static final String NODE_COMPARATOR = "comparator";
	
	/**
	 * The name of the type node to parse.
	 */
	private static final String NODE_TYPE = "type";

	/**
	 * The name of the value node to parse.
	 */	
	private static final String NODE_VALUE = "value";
	
	/**
	 * The name of the maximum node to parse.
	 */
	private static final String NODE_MAXIMUM = "maximum";
	
	/**
	 * The name of the minimum node to parse.
	 */
	private static final String NODE_MINIMUM = "minimum";


	/**
	 * Returns the description of the feature validator.
	 * 
	 * @return The description of the feature validator.
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Validates the document for feature values that do not adhere to
	 * their type and for feature requirements that do not adhere to
	 * their comparator.
	 * 
	 * @param document The document to check.
	 * @return An empty array if no failures have been found, or the 
	 * 	failures as parser markers if they have been found.
	 */
	public ParserMarker[] validate(LocatorDocument document) {
		// resulting parser markers
		Vector markers = new Vector();
		// find all feature nodes
		Vector features = getFeatures(document.getDocument());	
		// check the validity of all feature nodes
		for (int i = 0; i < features.size(); i++) {
			// determine whether this is a requirement
			Vector problems = new Vector();
			Node feature = (Node)features.elementAt(i);
			Node type = XMLUtility.getChild(feature, NODE_TYPE);
			Node comparator = XMLUtility.getChild(feature, NODE_COMPARATOR);
			Node value = XMLUtility.getChild(feature, NODE_VALUE);
			if (comparator == null) {
				// this is a provision, otherwise it would have a comparator
				String problem = validateType(type, value);
				if (problem != null) problems.addElement(problem); 
			} else {
				// this is a demand, it has a comparator
				Node minimum = XMLUtility.getChild(feature, NODE_MINIMUM);
				Node maximum = XMLUtility.getChild(feature, NODE_MAXIMUM);
				if (isRange(comparator)) {
					// this node must have minimum and maximum but not value				
					if (minimum == null) {
						problems.addElement("Minimum does not exist.");
					} else {
						// ok, check minimum type
						String problem = validateType(type, minimum);
						if (problem != null) problems.addElement(problem);
					}
					if (maximum == null) {
						problems.addElement("Maximum does not exist.");
					} else {
						// ok, check maximum type
						String problem = validateType(type, maximum);
						if (problem != null) problems.addElement(problem);
					}
					if (value != null) {
						problems.addElement("Value must not exist.");
					}
				} else {
					// this node must have value but not minimum and maximum
					if (value == null) {
						problems.addElement("Value does not exist.");
					} else {
						// ok, check value type
						String problem = validateType(type, value);
						if (problem != null) problems.addElement(problem); 					
					}
					if (minimum != null) {
						problems.addElement("Minimum must not exist.");
					}
					if (maximum != null) {
						problems.addElement("Maximum must not exist.");
					}
				}
			}
			// create marker for problems
			if (! problems.isEmpty()) {
				StringBuffer b = new StringBuffer();
				b.append("Feature \"" + XMLUtility.getName(feature) + "\" is not valid: ");
				for (int j = 0; j < problems.size(); j++) {
					b.append((String)problems.elementAt(j));	
					if (j != problems.size() - 1) b.append(" ");
				}
				int column = document.getColumn(feature);
				int line = document.getLine(feature);
				markers.addElement(new ParserMarker(b.toString(), line, column));
			}
		}
		// wrap markers and return
		return (ParserMarker[])markers.toArray(new ParserMarker[0]);
	}
	
	/**
	 * Returns all nodes of a document (recursively) that specify features.
	 * At the moment this is checked by looking at the node name only.
	 * 
	 * @param document The document to search.
	 * @return A vector that contains the nodes of the document whose
	 * 	name equals the name of feature nodes.
	 */
	private Vector getFeatures(Document document) {
		Vector features = new Vector();
		Vector nodes = new Vector();
		Node root = document.getFirstChild();
		if (root != null) {
			nodes.addElement(root);
		}
		while (! nodes.isEmpty()) {
			Node node = (Node)nodes.elementAt(0);
			nodes.removeElementAt(0);
			if (node.getNodeName().equals(NODE_FEATURE)) {
				features.addElement(node);
			}
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				nodes.addElement(children.item(i));
			}
		}
		return features;
	}

	/**
	 * Determines whether the value of a certain node adheres
	 * to the type specified by a certain node.
	 * 
	 * @param type The node that specifies the type.
	 * @param value The node that specifies the value.
	 * @return A string that describes the validation failure or
	 * 	null if the validation is ok.
	 */
	private String validateType(Node type, Node value) {
		String t = XMLUtility.getContent(type);
		String v = XMLUtility.getContent(value);
		if (t.equals(TYPE_BOOLEAN))	{
			if (! v.equals(BOOLEAN_TRUE) && ! v.equals(BOOLEAN_FALSE)) {
				return "\"" + v + "\" does not specify a valid boolean."; 
			}
		} else if (t.equals(TYPE_INTEGER)) {	
			try {
				Integer.parseInt(v);	
			} catch (NumberFormatException e) {
				return "\"" + v + "\" does not specify a valid integer.";
			}
		} else if (t.equals(TYPE_LONG)) {
			try {
				Long.parseLong(v);	
			} catch (NumberFormatException e) {
				return "\"" + v + "\" does not specify a valid long.";
			}
		} else if (t.endsWith(TYPE_STRING)) {
			// nothing to be done here
		}
		return null;
	}
	
	/**
	 * Checks whether the value of a certain comparator node 
	 * denotes a range comparator.
	 * 
	 * @param comparator The comparator node whose value 
	 * 	should be checked.
	 * @return True if the value denotes a range comparator,
	 * 	false otherwise.
	 */
	private boolean isRange(Node comparator) {
		String c = XMLUtility.getContent(comparator);
		for (int i = 0; i < COMPARATOR_RANGE.length; i++) {
			if (c.equals(COMPARATOR_RANGE[i])) return true;
		}
		return false;
	}
}
