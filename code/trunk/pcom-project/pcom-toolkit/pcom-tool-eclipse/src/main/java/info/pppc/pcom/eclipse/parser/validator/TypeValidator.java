package info.pppc.pcom.eclipse.parser.validator;

import java.util.Vector;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Document;
import org.w3c.dom.Node; 

import info.pppc.pcom.eclipse.generator.util.JavaUtility;
import info.pppc.pcom.eclipse.parser.IParserValidator;
import info.pppc.pcom.eclipse.parser.ParserMarker;
import info.pppc.pcom.eclipse.parser.util.XMLUtility;
import info.pppc.pcom.eclipse.parser.xerces.LocatorDocument;

/**
 * The type validator tries to locate all types contained in the 
 * document. If a type cannot be found, it generates a corresponding
 * parser marker.
 * 
 * @author Mac
 */
public class TypeValidator implements IParserValidator {

	/**
	 * The description of the validator.
	 */
	private static final String DESCRIPTION = "Type Validation";
	
	/**
	 * The name of the deployment descriptor node.
	 */
	private static final String NODE_DEPLOYMENT = "deployment";
	
	/**
	 * The name of the instance demand nodes.
	 */
	private static final String NODE_INSTANCE_DEMAND = "instance-demand";
	
	/**
	 * The name of the instance provision nodes.
	 */
	private static final String NODE_INSTANCE_PROVISION = "instance-provision";
	
	/**
	 * The name of the interface nodes.
	 */
	private static final String NODE_INTERFACE = "interface";
	
	
	/**
	 * The java project used to locate the types.
	 */
	private IJavaProject project;
	
	/**
	 * Creates a new type validator that uses the specified java
	 * project to lookup types.
	 * 
	 * @param project The java project used to search for types.
	 */
	public TypeValidator(IJavaProject project) {
		this.project = project;
	}

	/**
	 * Returns the human readable description of the validator.
	 * 
	 * @return A human readable description.
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Validates the document by retrieving all interfaces references
	 * by the document from the java project passed to the constructor.
	 * 
	 * @param document The document to validate.
	 * @return The parser markers that denote errors.
	 */
	public ParserMarker[] validate(LocatorDocument document) {
		Vector markers = new Vector();
		Node deployment = getDeployment(document.getDocument());
		Vector nodes = new Vector();
		Node iprovision = XMLUtility.getChild(deployment, NODE_INSTANCE_PROVISION);
		if (iprovision != null) nodes.addElement(iprovision);
		Vector idemands = XMLUtility.getChildren(deployment, NODE_INSTANCE_DEMAND);
		if (idemands != null) nodes.addAll(idemands);
		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node)nodes.elementAt(i);
			Vector ifaces = XMLUtility.getChildren(node, NODE_INTERFACE);
			for (int j = 0; j < ifaces.size(); j++) {
				Node iface = (Node)ifaces.get(j);
				Vector imarkers = validateInterface(document, iface);
				markers.addAll(imarkers);
			}
		}
		return (ParserMarker[])markers.toArray(new ParserMarker[0]);		
	}
	
	/**
	 * Checks whether the interface can be retrieved completely from
	 * the java project. The method will try to locate the interface
	 * as java type, then it will derive the implemented interfaces
	 * and then it will recursively check them.
	 * 
	 * @param document The document used to generate line numbers and
	 * 	columns.
	 * @param iface An interface node that contains some interface name.
	 * @return A vector of parser markers that describe problems with
	 * 	the interface.
	 */
	private Vector validateInterface(LocatorDocument document, Node iface) {
		String iname = XMLUtility.getContent(iface);
		int line = document.getLine(iface);
		int column = document.getColumn(iface);
		Vector markers = new Vector();
		try {
			// locate type in project
			IType type = project.findType(iname);
			if (type == null) {
				markers.add(new ParserMarker("Cannot resolve interface " 
						+ iname + ".", line, column));
			} else if (! type.isInterface()) {
				markers.add(new ParserMarker("Type is not an interface " 
						+ iname + ".", line, column));
			} else {
				// locate referenced sub-interfaces in project recursively
				Vector subinterfaces = new Vector();
				subinterfaces.addElement(type);
				while (! subinterfaces.isEmpty()) {
					IType subType = (IType)subinterfaces.remove(0);
					String[] subTypeNames = subType.getSuperInterfaceNames();
					for (int i = 0; i < subTypeNames.length; i++) {
						String subSubName = subTypeNames[i];
						try {
							String name = JavaUtility.getQualifiedType(subType, subSubName);
							IType subSubType = project.findType(name);
							if (subSubType == null) {
								markers.add(new ParserMarker("Cannot resolve referenced interface " 
										+ name + ".", line, column));
							} else {
								subinterfaces.add(subSubType);
							}
						} catch (JavaModelException e) {
							markers.add(new ParserMarker("Cannot resolve referenced interface " + subSubName 
									+ " (" + e.getMessage() + ").", line, column));				
						}
					}
				}
			}
		} catch (JavaModelException e) {
			markers.add(new ParserMarker("Cannot resolve interface " + iname 
					+ " (" + e.getMessage() + ").", line, column));
		}
		return markers;
	}
	
	/**
	 * Returns the deployment descriptor of the component descriptor
	 * or null if it does not exist.
	 * 
	 * @param document The document whose deployment descriptor should
	 * 	be retrieved.
	 * @return The node of the descriptor or null if it does not
	 * 	exist.
	 */
	private Node getDeployment(Document document) {
		Node root = document.getFirstChild();
		if (root != null) {
			return XMLUtility.getChild(root, NODE_DEPLOYMENT);
		} else {
			return null;
		}
	}
	

}
