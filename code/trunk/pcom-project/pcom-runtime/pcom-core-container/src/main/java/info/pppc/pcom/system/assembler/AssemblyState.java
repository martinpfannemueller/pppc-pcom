package info.pppc.pcom.system.assembler;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The setup object is used by the container to create the state required by the
 * assembler to reconfigure an application. Essentially, the container needs to
 * inform the assembler algorithm about possible configurations for a certain
 * instance or assignment under the configurations of its parent.
 * 
 * @author Mac
 */
public class AssemblyState implements ISerializable {
	
	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PS";
	
	/**
	 * The system id of the system that hosts the factory or resource that
	 * is responsible for creating the instance or assignment represented
	 * by this setup.
	 */
	private SystemID systemID;
	
	/**
	 * The object id of the factory or resource that has created the assignment
	 * or resource represented through this setup.
	 */
	private ObjectID creatorID;
	
	/**
	 * The object of the assignment or resource that is represented through
	 * this setup.
	 */
	private ObjectID elementID;
	
	/**
	 * Possible contracts under the current value assignment.
	 */
	private Vector contracts;
	
	/**
	 * The name of the element represented by the setup as used by the parent.
	 */
	private String name;

	/**
	 * A hashtable that contains system ids for all instances required by this
	 * instance that are configured and will require context objects to 
	 * specify setups.
	 */
	private Hashtable instances;
	
	/**
	 * A hashtable that contains the system ids for all assignments required by
	 * this instance or assignment that are configured and will require
	 * context objects to specify setups.
	 */
	private Hashtable resources;
	
	/**
	 * Creates a new assembler setup that is uninitialized.
	 */
	public AssemblyState() {
		super();
	}
	
	/**
	 * Returns the object id of the factory or resource that created the instance
	 * or assignment represented through this setup.
	 * 
	 * @return The object id of the factory or resource that created the instance
	 * 	or assignment. This should never be null.
	 */
	public ObjectID getCreatorID() {
		return creatorID;
	}

	/**
	 * Sets the object id of the factory or resource that created the instance or
	 * assignment represented through this setup.
	 * 
	 * @param creatorID The object id of the creator.
	 */
	public void setCreatorID(ObjectID creatorID) {
		this.creatorID = creatorID;
	}

	/**
	 * Returns the contracts that can be executed by the instance or setup represented
	 * throught this setup with respect to the levels of the parent.
	 * 
	 * @return The contracts that represent valid configurations for the instance with
	 * 	respect to the levels specified by the parent.
	 */
	public Vector getContracts() {
		return contracts;
	}

	/**
	 * Sets the contracts that can be executed by the instance or setup represented
	 * through this setup with respect to the levels of the parent.
	 * 
	 * @param contracts The contracts that can be executed with respect to the parent's
	 * 	levels.
	 */
	public void setContracts(Vector contracts) {
		this.contracts = contracts;
	}

	/**
	 * Returns the object identifier of the instance or assignment represented by
	 * this setup.
	 * 
	 * @return The object identifier of the instance or assignment.
	 */
	public ObjectID getElementID() {
		return elementID;
	}

	/**
	 * Sets the object identifier of the instance or assignment represented by this
	 * setup.
	 * 
	 * @param elementID The object identifier of the instance or assignment.
	 */
	public void setElementID(ObjectID elementID) {
		this.elementID = elementID;
	}

	/**
	 * Returns the system id of the system that executes the instance or assignment
	 * represented by this setup.
	 * 
	 * @return The system id of the system that executes the instance or assignment.
	 */
	public SystemID getSystemID() {
		return systemID;
	}

	/**
	 * Sets the system id of the system that executes the instance or assignment
	 * represented by this setup.
	 * 
	 * @param systemID The system id of the system that executes the instance or
	 * 	assignment.
	 */
	public void setSystemID(SystemID systemID) {
		this.systemID = systemID;
	}
	
	/**
	 * Sets the system id of the system that runs an instance for the specified
	 * dependency. 
	 * 
	 * @param name The name of the instance dependency.
	 * @param setup The system id of the system that configured an instance.
	 * @return The system id that might have been replaced.
	 */
	public SystemID putInstance(String name, SystemID setup) {
		if (instances == null) {
			instances = new Hashtable();
		}
		return (SystemID)instances.put(name, setup);
	}
	
	/**
	 * Returns the system id of the system that configured an instance
	 * for the specified instance dependency. 
	 * 
	 * @param name The name of the instance dependency.
	 * @return A system id of the system that executes an instance.
	 */
	public SystemID getInstance(String name) {
		if (instances != null) {
			return (SystemID)instances.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the names of instances contained in this state object.
	 * 
	 * @return The names of instances contained in this state object.
	 */
	public String[] getInstances() {
		if (instances == null) return new String[0];
		Vector v = new Vector();
		Enumeration e = instances.keys();
		while (e.hasMoreElements()) {
			String name = (String)e.nextElement();
			v.addElement(name);
		}
		String[] result = new String[v.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (String)v.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Sets the system id of the system that has the specified resource dependency
	 * satisfied.
	 * 
	 * @param name The name of the resource dependency whose flag should be set.
	 * @param setup The system id of the system that provides the assignment.
	 * @return The system id that might have been replaced.
	 */
	public SystemID putResource(String name, SystemID setup) {
		if (resources == null) {
			resources = new Hashtable();
		}
		return (SystemID)resources.put(name, setup);
	}
	
	/**
	 * Returns the system id  of the system that has the resource dependency configured
	 * or null if the dependency is not configured.
	 * 
	 * @param name The name of the resource dependency.
	 * @return The system id of the system that has the resource configured.
	 */
	public SystemID getResource(String name) {
		if (resources != null) {
			return (SystemID)resources.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the names of resources contained in this state object.
	 * 
	 * @return The names of resources contained in this state object.
	 */
	public String[] getResources() {
		if (resources == null) return new String[0];
		Vector v = new Vector();
		Enumeration e = resources.keys();
		while (e.hasMoreElements()) {
			String name = (String)e.nextElement();
			v.addElement(name);
		}
		String[] result = new String[v.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (String)v.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Returns the name of the represented element as used by 
	 * the parent.
	 * 
	 * @return The name of the represented element as used
	 * 	by the parent.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the represented element as used by
	 * the parent.
	 * 
	 * @param name The name of the represented element as
	 * 	used by the parent.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Deserializes the setup object from the specified input stream.
	 * 
	 * @param input The input stream used for deserialization.
	 * @throws IOException Thrown by the underlying input stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		systemID = (SystemID)input.readObject();
		creatorID = (ObjectID)input.readObject();
		elementID = (ObjectID)input.readObject();
		contracts = (Vector)input.readObject();
		instances = (Hashtable)input.readObject();
		resources = (Hashtable)input.readObject();
		name = (String)input.readObject();
	}
	
	/**
	 * Serializes the setup object to the specified output stream.
	 * 
	 * @param output The output stream used for serialization.
	 * @throws IOException Thrown by the underlying output stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeObject(systemID);
		output.writeObject(creatorID);
		output.writeObject(elementID);
		output.writeObject(contracts);
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
		b.append("SYSTEM (");
		b.append(systemID);
		b.append(") CREATOR (");
		b.append(creatorID);
		b.append(") ELEMENT (");
		b.append(elementID);
		b.append(") NAME (");
		b.append(name);
		b.append(") CONTRACTS (");
		if (contracts != null) {
			for (int i = 0; i < contracts.size(); i++) {
				b.append(" (");
				b.append(contracts.elementAt(i));
				b.append(")");
			}
		}
		b.append(" ) INSTANCES ( ");
		if (instances != null) {
			Enumeration i = instances.keys();
			while (i.hasMoreElements()) {
				Object key = i.nextElement();
				b.append(key);
				b.append(": (");
				b.append(instances.get(key));
				b.append(") ");
			}			
		}
		b.append(") RESOURCES ( ");
		if (resources != null) {
			Enumeration r = resources.keys();
			while (r.hasMoreElements()) {
				Object key = r.nextElement();
				b.append(key);
				b.append(": (");
				b.append(resources.get(key));
				b.append(") ");
			}			
		}
		b.append(")");
		return b.toString();
	}


}
