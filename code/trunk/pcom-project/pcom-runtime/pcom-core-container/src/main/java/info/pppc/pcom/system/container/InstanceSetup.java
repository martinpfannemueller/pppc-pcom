package info.pppc.pcom.system.container;

import java.io.IOException;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.contract.Contract;

/**
 * The instance setup describes an instance that should be started and its
 * context in which it needs to be started.
 * 
 * @author Mac
 */
public class InstanceSetup implements ISerializable {

	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PI";
	
	/**
	 * The demand contract that describes the demand directed to the 
	 * instance.
	 */
	private Contract contract;
	
	/**
	 * The id of the parent instance used to signal changes to contracts
	 * or to the state of the instance.
	 */
	private ObjectID instanceID;
	
	/**
	 * The id of the parents collector to which the instance can send
	 * its events.
	 */
	private ObjectID collectorID;
	
	/**
	 * The id of the predecessor that hosts the parent instance.
	 */
	private ObjectID containerID;
	
	/**
	 * The system id of the system that hosts the instance.
	 */
	private SystemID systemID;
	
	/**
	 * The result object of the assembler that describes the instance
	 * that should be started.
	 */
	private Assembly assembly;
	
	/**
	 * Creates a new unitialized instance setup. This constructor
	 * is solely used for deserialization purposes.
	 */
	public InstanceSetup() {
		super();
	}
	
	/**
	 * Creates a new initialized successor setup. All fields must be set to
	 * some value that is not null.
	 * 
	 * @param demand The contract that describes the demand towards the successor.
	 * @param instanceID The instance id of the successor parent.
	 * @param collectorID The collector id of the successor parent.
	 * @param containerID The container id of the successor parent.
	 * @param systemID The system id of the successor parent.
	 * @param assembly The assembly used to configure the successor.
	 */
	public InstanceSetup(Contract demand, ObjectID instanceID, ObjectID collectorID, 
			ObjectID containerID, SystemID systemID, Assembly assembly) {
		if (demand == null || instanceID == null || collectorID == null || containerID == null
				|| systemID == null || assembly == null) 
			throw new IllegalArgumentException("Illegal successor setup.");
		this.contract = demand;
		this.instanceID = instanceID;
		this.collectorID = collectorID;
		this.containerID = containerID;
		this.systemID = systemID;
		this.assembly = assembly;
	}

	/**
	 * Returns the collector id that can be used to emit events to the
	 * parent instance.
	 * 
	 * @return The collector id used to emit events.
	 */
	public ObjectID getCollectorID() {
		return collectorID;
	}

	/**
	 * Returns the demand contract that describes the demand of the parent.
	 * 
	 * @return The demand contract with the demand of the parent.
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * Returns the instance id of the parent. This is the id that can be
	 * used to signal state changes.
	 * 
	 * @return The instance id of the parent.
	 */
	public ObjectID getInstanceID() {
		return instanceID;
	}

	/**
	 * Returns the predecessor that hosts the parent instance.
	 * 
	 * @return The predecessor that hosts the parent instance.
	 */
	public ObjectID getContainerID() {
		return containerID;
	}

	/**
	 * Returns the result of the assembler that describes the configuration of
	 * the instance.
	 * 
	 * @return The result of the assembler that describes the configuration of
	 * 	the instance.
	 */
	public Assembly getAssembly() {
		return assembly;
	}

	/**
	 * Returns the system id of the system that hosts the
	 * parent.
	 * 
	 * @return Returns the systemID.
	 */
	public SystemID getSystemID() {
		return systemID;
	}

	/**
	 * Deserializes the setup from the specified input stream.
	 * 
	 * @param input The input stream used for deserialization.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		contract = (Contract)input.readObject();
		instanceID = (ObjectID)input.readObject();
		collectorID = (ObjectID)input.readObject();
		containerID = (ObjectID)input.readObject();
		assembly = (Assembly)input.readObject();
		systemID = (SystemID)input.readObject();
	}
	
	/**
	 * Serializes the setup to the specified output stream.
	 * 
	 * @param output The output stream used for serialization.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeObject(contract);
		output.writeObject(instanceID);
		output.writeObject(collectorID);
		output.writeObject(containerID);
		output.writeObject(assembly);
		output.writeObject(systemID);
	}
	
	/**
	 * Returns a human readable string representation.
	 * 
	 * @return A human readable string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("DEMAND (");
		b.append(contract);
		b.append(") INSTANCE (");
		b.append(instanceID);
		b.append(") COLLECTOR (");
		b.append(collectorID);
		b.append(") CONTAINER (");
		b.append(containerID);
		b.append(") SYSTEM (");
		b.append(systemID);
		b.append(") RESULT (");
		b.append(assembly);
		b.append(")");
		return b.toString();
	}

}
