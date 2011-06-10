package info.pppc.pcom.system.assembler;

import java.io.IOException;
import java.util.Vector;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.contract.Contract;

/**
 * The assembler result denotes a certain configuration result that has been
 * created by the assembler. The result contains the configuration of a single
 * instance or resource assignment. To denote the application it contains the
 * globally unique application id. To describe the configuration of the resource
 * or instance it contains the elements contract. To denote the factory or
 * resource that should create the instance or assignment it contains a creator
 * id. If the instance or assignment exists already (possibly running a different
 * configuration) the result will contain the instance's or assignment's object
 * id. To enable the container to retrieve the configuration of the assignments
 * and instances that are further required, the result contains context objects
 * that provide remote pointers to the configuration algorithms that perform
 * the configuration of these dependencies.
 * 
 * @author Mac
 */
public class Assembly implements ISerializable {
	
	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PA";
	
	/**
	 * The system id of the system that holds the instance.
	 */
	private SystemID systemID;
	
	/**
	 * The id of the factory or resource that should create the instance or
	 * assignment.
	 */
	private ObjectID creatorID;
	
	/**
	 * The id of the reused instance or assignment. This may be null if the
	 * instance or resource must be newly created. 
	 */
	private ObjectID elementID;
	
	/**
	 * The id under which the container can be contacted. The container must
	 * at least implement the successor interface.
	 */
	private ObjectID containerID;
	
	/**
	 * The contract that should be fulfilled by the instance or assignment.
	 */
	private Contract template;
	
	/**
	 * The instances that should be bound.
	 */
	private Vector instances = null;
	
	/**
	 * The resources that should be bound.  
	 */
	private Vector resources = null;
	
	/**
	 * The name of the result as used by the parent.
	 */
	private String name = null;
	
	
	/**
	 * Creates a new assembly without any configured elements.
	 */
	public Assembly() {
		super();
	}
	
	/**
	 * Returns the contract under which the instance or assignment should be configured
	 * that is represented by this assembler result.
	 * 
	 * @return The contract of the instance or assignment represented by this result.
	 */
	public Contract getTemplate() {
		return template;
	}

	/**
	 * Sets the contract under which the instance or the assignment should be configured
	 * that is represented by this assembler result object.
	 * 
	 * @param contract The contract of the instance or assignment represented by this
	 * 	result.
	 */
	public void setTemplate(Contract contract) {
		this.template = contract;
	}

	/**
	 * Returns the object id of the factory or resource that is responsible for creating
	 * an instance or assignment that adheres to the specified contract.
	 * 
	 * @return The object id of the creator that is responsible for creating the instance
	 * 	or assignment with the specified setup.
	 */
	public ObjectID getCreatorID() {
		return creatorID;
	}

	/**
	 * Sets the object id of the factory or resource that is responsible for creating
	 * an instance or assignment that adheres to the specified contract.
	 * 
	 * @param creatorID The creator id of the object that should create the instance
	 * 	or assignment represented through this result.
	 */
	public void setCreatorID(ObjectID creatorID) {
		this.creatorID = creatorID;
	}

	/**
	 * Returns the element id that denotes the instance or resource assignment that
	 * should be reconfigured. If the element id is null, a new instance or assingment
	 * must be created.
	 * 
	 * @return The element id of the instance or assignment that should be reconfigured
	 * 	or null if the instance or assignment should be newly created.
	 */
	public ObjectID getElementID() {
		return elementID;
	}
	
	/**
	 * Returns the system id of the system that hosts the instance or
	 * assignment.
	 * 
	 * @return The system id of the system that hosts the instance or
	 * 	assignment.
	 */
	public SystemID getSystemID() {
		return systemID;
	}

	/**
	 * Sets the system id of the system that hosts the instance or 
	 * assignment.
	 * 
	 * @param systemID The system id of the system that hosts the 
	 * 	instance or assignment.
	 */
	public void setSystemID(SystemID systemID) {
		this.systemID = systemID;
	}
	
	/**
	 * Returns the id of the container that hosts the configured
	 * element. 
	 * 
	 * @return The id of the container that hosts the configured
	 * 	element.
	 */
	public ObjectID getContainerID() {
		return containerID;
	}

	/**
	 * Sets the id of the container that hosts the configured
	 * element.
	 * 
	 * @param containerID The new id of the container that hosts
	 * 	the element.
	 */
	public void setContainerID(ObjectID containerID) {
		this.containerID = containerID;
	}
	
	/**
	 * Returns the name of the resulting element as used by the
	 * parent.
	 * 
	 * @return The name of the resulting element as used by the
	 * 	parent. 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the resulting element as used by the
	 * parent.
	 * 
	 * @param name The name of the resulting element as used by
	 * 	the parent.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Sets the element id that denotes the instance or resource assignment that is
	 * reconfigured through this setup. If this field is set to null, a new assignment
	 * will be created.
	 * 
	 * @param elementID The element id that should be reconfigured or null if the element
	 * 	should be newly created.
	 */
	public void setElementID(ObjectID elementID) {
		this.elementID = elementID;
	}

	/**
	 * Sets the assembler context for the specified resource dependency that can be
	 * used to configure the dependency.
	 * 
	 * @param context The context object that points to the assembler that is 
	 * 	capable of configuring the specified resource dependency.
	 */
	public void addResource(AssemblyPointer context) {
		if (resources == null) resources = new Vector();
		resources.addElement(context);
	}
	
	/**
	 * Sets the assembler result for the specified resource dependency that can
	 * be used to configure the dependency.
	 * 
	 * @param result The result used to configure a resource dependency.
	 */
	public void addResource(Assembly result) {
		if (resources == null) resources = new Vector();
		resources.addElement(result);
	}
	
	/**
	 * Resolves the resources and returns them. If one or more could not
	 * be resolved, this method returns an empty array..
	 * 
	 * @return The resolved resources or an empty array.
	 */
	public Assembly[] getResources() {
		if (resources == null) return new Assembly[0];
		Assembly[] result = new Assembly[resources.size()];
		for (int i = 0; i < resources.size(); i++) {
			Object resource = resources.elementAt(i);
			if (resource instanceof Assembly) {
				result[i] = (Assembly)resource;
			} else {
				AssemblyPointer context = (AssemblyPointer)resource;
				try {
					AssemblerProxy proxy = new AssemblerProxy();
					proxy.setSourceID(new ReferenceID(SystemID.SYSTEM));
					proxy.setTargetID(context.getAssemblerID());
					result[i] = proxy.retrieve(context);
				} catch (InvocationException e) {
					Logging.error(getClass(), "Could not resolve resource from assembler " 
							+ context.getAssemblerID() + "." , e);
					return new Assembly[0];
				}				
			}
		}
		return result;
	}

	/**
	 * Sets the assembler context for the specified instance dependency that can be
	 * used to configure the dependency.
	 * 
	 * @param context The context object that points to the assembler that is 
	 * 	capable of configuring the specified instance dependency.
	 */
	public void addInstance(AssemblyPointer context) {
		if (instances == null) instances = new Vector();
		instances.addElement(context);
	}
	
	/**
	 * Sets the assembler result for the specified instance dependency that can be
	 * used to configure the dependency.
	 * 
	 * @param result The result used to configure a dependency.
	 */
	public void addInstance(Assembly result) {
		if (instances == null) instances = new Vector();
		instances.addElement(result);
	}
	
	/**
	 * Resolves the instances and returns them. If one or more could not
	 * be resolved, this method returns an empty array.
	 * 
	 * @return The resolved instances or an empty array if they could not
	 * 	be resolved.
	 */
	public Assembly[] getInstances() {
		if (instances == null) return new Assembly[0];
		Assembly[] result = new Assembly[instances.size()];
		for (int i = 0; i < instances.size(); i++) {
			Object instance = instances.elementAt(i);
			if (instance instanceof Assembly) {
				result[i] = (Assembly)instance;
			} else {
				AssemblyPointer context = (AssemblyPointer)instance;
				try {
					AssemblerProxy proxy = new AssemblerProxy();
					proxy.setSourceID(new ReferenceID(SystemID.SYSTEM));
					proxy.setTargetID(context.getAssemblerID());
					result[i] = proxy.retrieve(context);
				} catch (InvocationException e) {
					Logging.error(getClass(), "Could not resolve instance from assembler " 
							+ context.getAssemblerID() + "." , e);
					return new Assembly[0];
				}				
			}
		}
		return result;
	}
	
	/**
	 * Deserializes the assmbler result from the specified input stream.
	 * 
	 * @param input The input stream used for deserialization.
	 * @throws IOException Thrown by the underlying input stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		creatorID = (ObjectID)input.readObject();
		elementID = (ObjectID)input.readObject();
		template = (Contract)input.readObject();
		instances = (Vector)input.readObject();
		resources = (Vector)input.readObject();
		systemID = (SystemID)input.readObject();
		containerID = (ObjectID)input.readObject();
		name = (String)input.readObject();
	}
	
	/**
	 * Serializes the assembler reult to the specified output stream.
	 * 
	 * @param output The output stream used for serialization.
	 * @throws IOException Thrown by the underlying output stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeObject(creatorID);
		output.writeObject(elementID);
		output.writeObject(template);
		output.writeObject(instances);
		output.writeObject(resources);
		output.writeObject(systemID);
		output.writeObject(containerID);
		output.writeObject(name);
	}
	
	/**
	 * Returns a human readable string representation.
	 * 
	 * @return A human readable string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("SYSTEM (");
		b.append(systemID);
		b.append(") CONTAINER (");
		b.append(containerID);
		b.append(") CREATOR (");
		b.append(creatorID);
		b.append(") ELEMENT (");
		b.append(elementID);
		b.append(") CONTRACT (");
		b.append(template);
		b.append(") NAME (");
		b.append(name);
		b.append(") INSTANCES ( ");
		if (instances != null) {
			for (int i = 0; i < instances.size(); i++) {
				b.append("(");
				b.append(instances.elementAt(i));
				b.append(") ");				
			}
		}
		b.append(") RESOURCES ( ");
		if (resources != null) {
			for (int i = 0; i < resources.size(); i++) {
				b.append("(");
				b.append(resources.elementAt(i));
				b.append(") ");				
			}
		}
		b.append(")");
		return b.toString();
	}	
	
}
