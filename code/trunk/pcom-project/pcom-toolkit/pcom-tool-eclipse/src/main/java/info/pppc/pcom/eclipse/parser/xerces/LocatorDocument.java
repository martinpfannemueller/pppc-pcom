package info.pppc.pcom.eclipse.parser.xerces;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The locator document is the result object of the locator parser. Apart
 * from the parsed W3C DOM Document this result object contains hashmaps
 * that can be used to resolve the document's nodes to line numbers and
 * column positions using the corresponding getters.
 * 
 * @author Mac
 */
public class LocatorDocument {

	/**
	 * Hashmap that maps document nodes to lines as integers.
	 */
	protected HashMap lines;
	
	/**
	 * Hashmap that maps document nodes to columns as integers.
	 */
	protected HashMap columns;
	
	/**
	 * Base document of the locator document.
	 */
	protected Document document;

	/**
	 * Creates a new locator document from a base document and
	 * two hashmaps that contain the line numbers and the column
	 * numbers of the nodes contained in the document.
	 * 
	 * @param document The base document this must not be null. If this is
	 * 	set to null an illegal argument exception will be thrown.
	 * @param lines The hashmap that maps nodes of the document to line numbers
	 * 	as integers. This may be null if unknown.
	 * @param columns The hashmap that maps nodes of the document to columns as
	 * 	integers. This may be null if unknown.
	 * @throws IllegalArgumentException Thrown if the document is null.
	 */
	protected LocatorDocument(Document document, HashMap lines, HashMap columns) {
		if (document == null) 
			throw new IllegalArgumentException("Document must not be null.");
		this.document = document;
		this.lines = lines;
		this.columns = columns;
	}
	
	/**
	 * Returns the base document of the locator document this is guaranteed
	 * to be set.
	 * 
	 * @return The base document of the locator. This is never null.
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * Returns the line number of the node or -1 if the node is not
	 * part of the document.
	 * 
	 * @param node The node to lookup.
	 * @return The line number of the node or -1 if the node is not
	 * 	set.
	 */
	public int getLine(Node node) {
		if (lines != null && node != null) {
			Integer line = (Integer)lines.get(node);
			if (line != null) {
				return line.intValue();
			}
		}
		return -1;
	}
	
	/**
	 * Returns the column number of the node or -1 if the node is not
	 * part of the document.
	 * 
	 * @param node The node to lookup.
	 * @return The column number of the node or -1 if the node is not
	 * 	set.
	 */
	public int getColumn(Node node) {
		if (columns != null && node != null) {
			Integer column = (Integer)columns.get(node);
			if (column != null) {
				return column.intValue();
			}
		}
		return -1;
	}
	
	/**
	 * Returns a string representation of the document.
	 * 
	 * @return A string representation of the document.
	 */
	public String toString() {
		if (document == null) {
			return "Document is null";
		} else {
			StringBuffer b = new StringBuffer();	
			Node root = document.getFirstChild();
			toString(root, b);
			return b.toString();			
		}
	}
	
	/**
	 * Returns a recursive string representation of the specified node.
	 * 
	 * @param node A recursive string representation of the specified node.
	 * @param b The string buffer that will contain the representation.
	 */
	protected void toString(Node node, StringBuffer b) {
		b.append(node.getNodeName());
		b.append("(");
		b.append(getLine(node));
		b.append(", ");
		b.append(getColumn(node));
		b.append(") ");
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			toString(list.item(i), b);
		}
	}

}
