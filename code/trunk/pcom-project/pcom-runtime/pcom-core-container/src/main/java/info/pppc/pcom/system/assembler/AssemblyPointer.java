package info.pppc.pcom.system.assembler;

import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.base.system.io.ObjectInputStream;
import info.pppc.base.system.io.ObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * The assembler context is a pointer to some remote assembler that
 * denotes a variable within the assembler. This context object is
 * used to setup a preconfiguration and to retrieve a current configuraiton.
 * 
 * @author Mac
 */
public class AssemblyPointer implements ISerializable {

	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PP";
	
	/**
	 * The id of the application that is represented.
	 */
	private ReferenceID applicationID;
	
	/**
	 * The id of the assembler that is responsible for this variable.
	 */
	private ReferenceID assemblerID;
	
	/**
	 * The variable of the assembler context.
	 */
	private byte[] variableID;

	/**
	 * The name of the element represented by the context as used
	 * by the parent.
	 */
	private String name;
	
	/**
	 * Possible context objects for child instances of this variable.
	 */
	private Vector instances;
	
	/**
	 * Possible context objects for child assignments of this variable.
	 */
	private Vector resources;
	
	
	/**
	 * Creates a new assembly pointer without any application id.
	 * This constructor is solely intended for deserialization
	 * purposes. It must not be called from user code.
	 */
	public AssemblyPointer() {
		super();
	}
	
	/**
	 * Creates a new context object for the specified application.
	 * 
	 * @param applicationID The application id represented through
	 * 	this context.
	 */
	public AssemblyPointer(ReferenceID applicationID) {
		this.applicationID = applicationID;
	}

	/**
	 * Returns the application id of the assembler context object.
	 * 
	 * @return The application id of the assembler context.
	 */
	public ReferenceID getApplicationID() {
		return applicationID;
	}
	
	/**
	 * Returns the reference id of the assembler that is responsible
	 * for the variable referenced by this context.
	 * 
	 * @return The id of the assembler responsible for this variable.
	 */
	public ReferenceID getAssemblerID() {
		return assemblerID;
	}

	/**
	 * Sets the reference id of the assembler that is responsible 
	 * for the variable referenced through this context.
	 * 
	 * @param assemblerID The reference to the controlling assembler.
	 */
	public void setAssemblerID(ReferenceID assemblerID) {
		this.assemblerID = assemblerID;
	}

	/**
	 * Returns the variable referenced by this context object.
	 * 
	 * @return The referenced variable.
	 */
	public ISerializable getVariableID() {
		if (variableID == null) return null;
		// dual serialization is neccessary to ensure that containers do not
		// have to have the classes that an assembler uses internally.
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(variableID);
			ObjectInputStream ois = new ObjectInputStream(bis);
			return (ISerializable)ois.readObject();				
		} catch (IOException e) {
			throw new RuntimeException("Could not get variable id.");
		}
	}

	/**
	 * Sets the variable that is referenced by this context object.
	 * 
	 * @param variableID The variable referenced by this context.
	 */
	public void setVariableID(ISerializable variableID) {
		// retrieve the dual serialized variable id
		if (variableID == null) {
			this.variableID = null;
		} else {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(variableID);
				this.variableID = bos.toByteArray();				
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not set variable id.");
			}
		}	
	}
	
	/**
	 * Adds the context for the specified instance dependency.
	 * 
	 * @param context The context to put.
	 * @return The context that might have been replaced.
	 */
	public AssemblyPointer addInstance(AssemblyPointer context) {
		if (instances == null) {
			instances = new Vector();
		}
		AssemblyPointer replaced = null;
		for (int i = instances.size() - 1; i >= 0 ; i--) {
			AssemblyPointer c = (AssemblyPointer)instances.elementAt(i);
			if (c.getName().equals(context.getName())) {
				instances.removeElementAt(i);
				replaced = c;
				break;
			}
		}
		instances.addElement(context);
		return replaced;
	}
	
	/**
	 * Returns all context objects for instances specified in this 
	 * context object.
	 * 
	 * @return The context objects for instances specified in this
	 *  context object.
	 */
	public AssemblyPointer[] getInstances() {
		if (instances == null) return new AssemblyPointer[0];
		AssemblyPointer[] result = new AssemblyPointer[instances.size()];
		for (int i = instances.size() - 1; i >= 0; i--) {
			result[i] = (AssemblyPointer)instances.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Sets the context for the specified resource dependency.
	 * 
	 * @param context The context of the specified resource.
	 * @return The context that might have been replaced.
	 */
	public AssemblyPointer addResource(AssemblyPointer context) {
		if (resources == null) {
			resources = new Vector();
		}
		AssemblyPointer replaced = null;
		for (int i = resources.size() - 1; i >= 0 ; i--) {
			AssemblyPointer c = (AssemblyPointer)resources.elementAt(i);
			if (c.getName().equals(context.getName())) {
				resources.removeElementAt(i);
				replaced = c;
				break;
			}
		}
		resources.addElement(context);
		return replaced;	
	}
	
	/**
	 * Returns the assembler context objects for resources that are
	 * specified in this context object.
	 * 
	 * @return The assembler context objects for resources.
	 */
	public AssemblyPointer[] getResources() {
		if (resources == null) return new AssemblyPointer[0];
		AssemblyPointer[] result = new AssemblyPointer[resources.size()];
		for (int i = resources.size() - 1; i >= 0; i--) {
			result[i] = (AssemblyPointer)resources.elementAt(i);
		}
		return result;
	}
	
	
	/**
	 * Returns the name of the element represented by this
	 * context as used by the parent.
	 * 
	 * @return The name of the element represented by this context
	 * 	as used by the parent.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the element represented by this
	 * context as used by the parent.
	 * 
	 * @param name The name of the element represented by this
	 * 	context as used by the parent.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Deserializes the assembler context from the given input stream.
	 * 
	 * @param input The input stream to read from.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		applicationID = (ReferenceID)input.readObject();
		assemblerID = (ReferenceID)input.readObject();
		variableID = (byte[])input.readObject();
		instances = (Vector)input.readObject();
		resources = (Vector)input.readObject();
		name = (String)input.readObject();
	}
	
	/**
	 * Serializes the assembler context to the given output stream.
	 * 
	 * @param output The output stream to write to.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeObject(applicationID);
		output.writeObject(assemblerID);
		output.writeObject(variableID);
		output.writeObject(instances);
		output.writeObject(resources);
		output.writeObject(name);
	}
	
	/**
	 * Returns a human readable string representation.
	 * 
	 * @return A human readable string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("APPLICATION (");
		b.append(applicationID);
		b.append(") ASSEMBLER (");
		b.append(assemblerID);
		b.append(") VARIABLE (");
		b.append(variableID);
		b.append(") NAME (");
		b.append(name);
		b.append(") INSTANCES ( ");
		AssemblyPointer[] instances = getInstances();
		for (int i = 0; i < instances.length; i++) {
			b.append("(");
			b.append(instances[i]);
			b.append(") ");
		}
		b.append(") RESOURCES ( ");
		AssemblyPointer[] resources = getResources();
		for (int i = 0; i < resources.length; i++) {
			b.append("(");
			b.append(resources[i]);
			b.append(") ");			
		}
		b.append(")");
		return b.toString();
	}
	
}
