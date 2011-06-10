package info.pppc.pcom.eclipse.parser.validator;

import info.pppc.pcom.eclipse.parser.IParserValidator;
import info.pppc.pcom.eclipse.parser.ParserMarker;
import info.pppc.pcom.eclipse.parser.util.XMLUtility;
import info.pppc.pcom.eclipse.parser.xerces.LocatorDocument;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The reference validator checks whether all referenced types and
 * names are correctly specified within the component descriptor.
 * For each contract, the component descriptor must contain a super
 * set of 
 *  
 * @author Mac
 */
public class ReferenceValidator implements IParserValidator {

	/**
	 * The description of the reference validator.
	 */
	private static final String DESCRIPTION = "Reference Validation";

	/**
	 * The name of the deployment descriptor node.
	 */
	private static final String NODE_DEPLOYMENT = "deployment";
	
	/**
	 * The name of the contract descriptor nodes.
	 */
	private static final String NODE_CONTRACT = "contract";
	
	/**
	 * The name of the instance demand nodes.
	 */
	private static final String NODE_INSTANCE_DEMAND = "instance-demand";
	
	/**
	 * The name of the instance provision nodes.
	 */
	private static final String NODE_INSTANCE_PROVISION = "instance-provision";
	
	/**
	 * The name of the resource demand nodes.
	 */
	private static final String NODE_RESOURCE_DEMAND = "resource-demand";
	
	/**
	 * The name of the type nodes.
	 */
	private static final String NODE_TYPE = "type";
	
	/**
	 * The name of the event nodes.
	 */
	private static final String NODE_EVENT = "event";
	
	/**
	 * The name of the interface nodes.
	 */
	private static final String NODE_INTERFACE = "interface";

	/**
	 * The name of the proxy nodes that describe the output file for dependencies.
	 */
	private static final String NODE_PROXY = "proxy";


	/**
	 * Returns the description of the reference validator.
	 * 
	 * @return The description of the reference validator.
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Validates the document for references to types and interfaces
	 * from contracts that are not defined in the deployment descriptor
	 * used during generation.
	 * 
	 * @param document The document to check.
	 * @return An empty array if no failures have been found, or the 
	 * 	failures as parser markers if they have been found.
	 */
	public ParserMarker[] validate(LocatorDocument document) {
		// validate the instance declarations of the deployment descriptor
		Vector markers = validateInstances(document);
		if (! markers.isEmpty()) { 
			return (ParserMarker[])markers.toArray(new ParserMarker[0]);
		}
		// validate the resource declarations of the deployment descriptor
		markers = validateResources(document);
		if (! markers.isEmpty()) { 
			return (ParserMarker[])markers.toArray(new ParserMarker[0]);
		}
		// check whether contract dependencies contain only declared types
		markers = validateContracts(document);
		return (ParserMarker[])markers.toArray(new ParserMarker[0]);
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
	
	/**
	 * Returns the contract descriptors of the component descriptor
	 * or an empty vector if none are specified.
	 * 
	 * @param document The document whose contract descriptor should
	 * 	be retrieved.
	 * @return A vector that contains nodes that point to contract
	 * 	descriptors or an empty vector if none are specified.
	 */
	private Vector getContracts(Document document) {
		Node root = document.getFirstChild();
		if (root != null) {
			return XMLUtility.getChildren(root, NODE_CONTRACT);
		} else {
			return new Vector();
		}
	}
		
	/**
	 * Validates that the instance dependency declarations of the deployment descriptor
	 * have unique names and unique output files (i.e. proxy definitions).
	 * 
	 * @param document The document that contains the contract to validate.
	 * @return A vector that will contain the problem markers that have been
	 * 	created by the call to the method.
	 */
	private Vector validateInstances(LocatorDocument document) {
		Vector markers = new Vector();
		Node deploy = getDeployment(document.getDocument());
		// check whether deployment descriptor contains doubled declarations or proxies
		Vector instances = XMLUtility.getChildren(deploy, NODE_INSTANCE_DEMAND);
		while (!instances.isEmpty()) {
			Node instance = (Node)instances.elementAt(0);
			instances.removeElementAt(0);
			String name = XMLUtility.getName(instance);
			Node proxy = XMLUtility.getChild(instance, NODE_PROXY);
			String output = XMLUtility.getContent(proxy);
			for (int j = 0; j < instances.size(); j++) {
				Node instance2 = (Node)instances.elementAt(j);
				String name2 = XMLUtility.getName(instance2);	
				Node proxy2 = XMLUtility.getChild(instance2, NODE_PROXY);
				String output2 = XMLUtility.getContent(proxy2);
				// check declaration and proxies
				if (name != null && name.equals(name2)) {
					int line = document.getLine(instance2);
					int column = document.getColumn(instance2);
					markers.addElement(new ParserMarker("Instance demand name must be unique.", line, column));
					instances.removeElementAt(j);
					j -= 1;						
				} else if (output != null && output.equals(output2)) {
					int line = document.getLine(proxy2);
					int column = document.getColumn(proxy2);
					markers.addElement(new ParserMarker("Instance demand proxy must be unique.", line, column));
					instances.removeElementAt(j);
					j -= 1;						
				}
			}
		}
		return markers;
	}
	
	/**
	 * Validates that the resource dependency declarations of the deployment descriptor
	 * have unique types.
	 * 
	 * @param document The document that contains the component description.
	 * @return A vector that contains the problem markers that have been created during the
	 * 	validation of the document.
	 */
	private Vector validateResources(LocatorDocument document) {
		Vector markers = new Vector();
		Node deploy = getDeployment(document.getDocument());
		// check whether deployment descriptor contains doubled declarations or proxies
		Vector resources = XMLUtility.getChildren(deploy, NODE_RESOURCE_DEMAND);
		while (!resources.isEmpty()) {
			Node resource = (Node)resources.elementAt(0);
			resources.removeElementAt(0);
			Node typeNode = XMLUtility.getChild(resource, NODE_TYPE);
			String type = XMLUtility.getContent(typeNode);
			for (int j = 0; j < resources.size(); j++) {
				Node resource2 = (Node)resources.elementAt(j);
				Node typeNode2 = XMLUtility.getChild(resource2, NODE_TYPE);
				String type2 = XMLUtility.getContent(typeNode2);
				// check declaration and proxies
				if (type != null && type.equals(type2)) {
					int line = document.getLine(typeNode2);
					int column = document.getColumn(typeNode2);
					markers.addElement(new ParserMarker("Resource demand type must be unique.", line, column));
					resources.removeElementAt(j);
					j -= 1;						
				} 
			}
		}
		return markers;
	}

	/**
	 * Validates that the contracts contain only interfaces and events as well
	 * as dependencies that are declared in the deployment descriptor.
	 * 
	 * @param document The document to validate.
	 * @return A vector with problem markers that denote problems found during
	 * 	validation.
	 */
	private Vector validateContracts(LocatorDocument document) {
		Vector markers = new Vector();
		Node deploy = getDeployment(document.getDocument());
		// create declared provision interface and event lists
		Node declProvision = XMLUtility.getChild(deploy, NODE_INSTANCE_PROVISION);
		Vector declIfs = XMLUtility.getContents(declProvision, NODE_INSTANCE_PROVISION);
		Vector declEvt = XMLUtility.getContents(declProvision, NODE_INSTANCE_DEMAND);
		// create declared resource demand type list
		Vector declResources = XMLUtility.getChildren(deploy, NODE_RESOURCE_DEMAND);
		Vector declRes = new Vector();
		for (int i = 0; i < declResources.size(); i++) {
			Node resource = (Node)declResources.elementAt(i);
			Node type = XMLUtility.getChild(resource, NODE_TYPE);
			String t = XMLUtility.getContent(type);
			if (t != null && ! declRes.contains(t)) {
				declRes.addElement(t);
			}
		}
		// create hashmap for dependency interface and event lists
		Vector declInstances = XMLUtility.getChildren(deploy, NODE_INSTANCE_DEMAND);
		HashMap declProxyIfs = new HashMap();
		HashMap declProxyEvt = new HashMap();
		for (int i = 0; i < declInstances.size(); i++) {
			Node instance = (Node)declInstances.elementAt(i);
			String name = XMLUtility.getName(instance);
			if (name != null) {
				Vector ifs = XMLUtility.getContents(instance, NODE_INTERFACE);
				declProxyIfs.put(name, ifs);			
				Vector evt = XMLUtility.getContents(instance, NODE_EVENT);
				declProxyEvt.put(name, evt);
			}
		}
		Vector contracts = getContracts(document.getDocument());
		// validate every single contract
		for (int i = 0; i < contracts.size(); i++) {
			Node contract = (Node)contracts.elementAt(i);
			// validate provision against the declaration
			Node provision = XMLUtility.getChild(contract, NODE_INSTANCE_PROVISION);
			Vector proIfs = XMLUtility.getChildren(provision, NODE_INTERFACE);
			for (int j = 0; j < proIfs.size(); j++) {
				Node ifs = XMLUtility.getChild((Node)proIfs.elementAt(j), NODE_TYPE);
				String type = XMLUtility.getContent(ifs);
				if (! declIfs.contains(type)) {
					int line = document.getLine(ifs);
					int column = document.getColumn(ifs);
					markers.addElement(new ParserMarker("Interface provision must be declared.", line, column));
				}
			}
			Vector proEvt = XMLUtility.getChildren(provision, NODE_EVENT);
			for (int j = 0; j < proEvt.size(); j++) {
				Node evt = XMLUtility.getChild((Node)proEvt.elementAt(j), NODE_TYPE);
				String type = XMLUtility.getContent(evt);
				if (! declEvt.contains(type)) {
					int line = document.getLine(evt);
					int column = document.getColumn(evt);
					markers.addElement(new ParserMarker("Event provision must be declared.", line, column));
				}
			}
			// validate resources against declaration
			Vector resources = XMLUtility.getChildren(contract, NODE_RESOURCE_DEMAND);
			for (int j = 0; j < resources.size(); j++) {
				Node resource = XMLUtility.getChild((Node)resources.elementAt(j), NODE_TYPE);	
				String type = XMLUtility.getContent(resource);
				if (! declRes.contains(type)) {
					int line = document.getLine(resource);
					int column = document.getColumn(resource);
					markers.addElement(new ParserMarker("Resource type must be declared.", line, column));
				}
				
			}
			// validate dependencies againts declaration
			Vector instances = XMLUtility.getChildren(contract, NODE_INSTANCE_DEMAND);
			for (int j = 0; j < instances.size(); j++) {
				Node instance = (Node)instances.elementAt(j);
				String name = XMLUtility.getName(instance);
				if (declProxyEvt.containsKey(name) && declProxyIfs.containsKey(name)) {
					// check required events
					Vector dpe = (Vector)declProxyEvt.get(name);
					Vector events = XMLUtility.getChildren(instance, NODE_EVENT);
					for (int k = 0; k < events.size(); k++) {
						Node event = XMLUtility.getChild((Node)events.elementAt(k), NODE_TYPE);
						String type = XMLUtility.getContent(event);
						if (! dpe.contains(type)) {
							int line = document.getLine(event);
							int column = document.getColumn(event);
							markers.addElement(new ParserMarker("Event type must be declared.", line, column));
						}
					}
					// check required interfaces
					Vector dpi = (Vector)declProxyIfs.get(name);	
					Vector interfaces = XMLUtility.getChildren(instance, NODE_INTERFACE);
					for (int k = 0; k < interfaces.size(); k++) {
						Node interfase = XMLUtility.getChild((Node)interfaces.elementAt(k), NODE_TYPE);
						String type = XMLUtility.getContent(interfase);
						if (! dpi.contains(type)) {
							int line = document.getLine(interfase);
							int column = document.getColumn(interfase);
							markers.addElement(new ParserMarker("Interface type must be declared.", line, column));							
						}
					}
				} else {
					int line = document.getLine(instance);
					int column = document.getColumn(instance);
					markers.addElement(new ParserMarker("Instance demand must be declared.", line, column));										
				}
			}
			
		}
		return markers;
	}
	
}
