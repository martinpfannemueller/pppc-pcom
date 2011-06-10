package info.pppc.pcom.eclipse.parser.util;

import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The xml utility provides a number of simple and useful methods to
 * deal with xml doms that are commonly used throughout the plugin.
 * 
 * @author Mac
 */
public class XMLUtility {

	/**
	 * The name of the feature name node to parse.
	 */
	protected static final String NODE_NAME = "name";

	/**
	 * The name of the node that contains the value.
	 */
	protected static final String NODE_CONTENT = "#text";

	/**
	 * Returns the first child of a certain node that has the
	 * specified name.
	 * 
	 * @param node The node whose children should be searched.
	 * @param name The name to lookup.
	 * @return The first child of the node that has the specified
	 * 	name or null if such a child does not exist.
	 */
	public static Node getChild(Node node, String name) {
		NodeList children = node.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);
			if (child.getNodeName().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Returns all children with the specified name.
	 * 
	 * @param node The node whose children should be retrieved.
	 * @param name The name of the child elements that should 
	 * 	be retrieved.
	 * @return A vector that contains the child nodes or an 
	 * 	empty vector if no such children exist.
	 */
	public static Vector getChildren(Node node, String name) {
		Vector result = new Vector();
		NodeList children = node.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);
			if (child.getNodeName().equals(name)) {
				result.addElement(child);
			}
		}
		return result;
	}

	/**
	 * Returns the name of a parent element by searching for
	 * a name node and returing its contents.
	 * 
	 * @param parent The parent node whose name should be 
	 * 	retrieved.
	 * @return The name of the parent or null if it does not
	 * 	exist.
	 */
	public static String getName(Node parent) {
		Node name = getChild(parent, NODE_NAME);
		if (name != null) {
			return getContent(name);
		} else {
			return null;
		}
	}

	/**
	 * Returns the content of the node as string or null if
	 * the content is not specified.
	 * 
	 * @param node The node whose content should be retrieved.
	 * @return The content as string or null if the node does
	 * 	not have any content.
	 */
	public static String getContent(Node node) {
		Node c = getChild(node, NODE_CONTENT);
		if (c != null) {
			return c.getNodeValue();
		} else {
			return null;
		}
	}

	/**
	 * Returns all contents of the subnode that is part of the parent.
	 * 
	 * @param parent The parent node whose child nodes contents should
	 * 	be retrieved.
	 * @param name The name of the children to lookup.
	 * @return A vector of strings denoting the contents declared by the
	 * 	child nodes of the specified parent found under the specified name.
	 *  Duplicate values and nodes that do not contain a value will be removed.
	 */
	public static Vector getContents(Node parent, String name) {
		Vector nodes = getChildren(parent, name);
		Vector result = new Vector();
		for (int i = 0; i < nodes.size(); i++) {
			String content = getContent((Node)nodes.elementAt(i));
			if (content != null && ! result.contains(content)) {
				result.addElement(content);	
			}
		}
		return result;
	}

}
