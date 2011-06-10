package info.pppc.pcom.system.container;

import info.pppc.base.lease.Lease;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.pcom.system.contract.Contract;

import java.io.IOException;

/**
 * The instance state is the result object issued by containers to start instance
 * requests. It describes the state of the instance and contains the identifiers
 * used to create dispatch objects. 
 * 
 * @author Mac
 */
public class InstanceState implements ISerializable {

	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PZ";
	
	/**
	 * A flag that indicates whether the request was a success.
	 */
	private boolean success;
	
	/**
	 * The reference id of the instance that has been created. This 
	 * field is null if the instance state does not indicate a
	 * success.
	 */
	private ObjectID instanceID;
	
	/**
	 * The reference id of the instance's skeleton that has been
	 * created. This field is null if the instance state does not
	 * indicate a success.
	 */
	private ObjectID skeletonID;
	
	/**
	 * The provision of the instance that has been created. This
	 * field is null if the instance state does not indicate a
	 * success.
	 */
	private Contract provision;
	
	/**
	 * The lease that might have been created in response to the
	 * start instance request.  
	 */
	private Lease lease;
	
	/**
	 * Create a new instance state that signals that the instance
	 * bound to this state does not exist and has not been started.
	 */
	public InstanceState() {
		super();
	}
	
	/**
	 * Binds the successor state to the specified instance. None of the
	 * fields may be null.
	 * 
	 * @param instanceID The instance id that describes the instance.
	 * @param skeletonID The skeleton id that points to the instance's skeleton.
	 * @param provision The provision of the instance.
	 * @param lease The lease of the instance.
	 */
	public void bind(ObjectID instanceID, ObjectID skeletonID, Contract provision, Lease lease) {
		if (instanceID == null || skeletonID == null || provision == null || lease == null)
			throw new IllegalArgumentException("Illegal successor binding.");
		this.instanceID = instanceID;
		this.skeletonID = skeletonID;
		this.provision = provision;
		this.lease = lease;
	}
	
	/**
	 * Determine whether the successor state points to a bound instance.
	 * 
	 * @return True if the instance denoted by the successor state is bound.
	 */
	public boolean isBound() {
		return (instanceID != null && skeletonID != null && provision != null && lease != null);
	}

	/**
	 * Sets the success flag that determines whether the instance is
	 * configured completely. I.e. it does not have any broken 
	 * dependencies.
	 * 
	 * @param success The flag that denotes whether the instance has
	 * 	been started successfully.
	 */
	public void setSuccess(boolean success) {
		if (success && ! isBound()) 
			throw new IllegalArgumentException("An instance that is not bound cannot be successful.");
		this.success = success;
	}
	
	/**
	 * Returns true if the instance has been configured successfully, false
	 * otherwise.
	 * 
	 * @return True to indicate success, false otherwise. 
	 */
	public boolean isSuccess() {
		return success;
	}
	
	/**
	 * Returns the unique id of the instance. This is the id under which
	 * the instance will send events and contract changes. This field
	 * will be null if the state does not indicate success.
	 * 
	 * @return The unique id of the instance.
	 */
	public ObjectID getInstanceID() {
		return instanceID;
	}
	
	/**
	 * Returns the skeleton id of the instance. This is the id under which
	 * the instance will be able to receive calls from a proxy. This field
	 * should be null if the state does not indicate success.
	 * 
	 * @return The skeleton id of the instance id.
	 */
	public ObjectID getSkeletonID() {
		return skeletonID;
	}
	
	/**
	 * Returns the provision part of the instances contract. This is
	 * the current provision of the instance. This field should be null
	 * if the state does not indicate success.
	 * 
	 * @return The provision of the instance as it is currently granted.
	 */
	public Contract getProvision() {
		return provision;
	}
	
	/**
	 * Returns the lease that has been created by the instance upon startup.
	 * This field should be null if the state does not indicate success.
	 * 
	 * @return The lease created by the instance.
	 */
	public Lease getLease() {
		return lease;
	}
	
	/**
	 * Deserializes the instance state from the specified input stream.
	 * 
	 * @param input The input stream to read from.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		success = input.readBoolean();
		instanceID = (ObjectID)input.readObject();
		skeletonID = (ObjectID)input.readObject();
		provision = (Contract)input.readObject();
		lease = (Lease)input.readObject();
	}
	
	/**
	 * Serializes the instance state to the specified output stream.
	 * 
	 * @param output The output stream used for serialization.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeBoolean(success);
		output.writeObject(instanceID);
		output.writeObject(skeletonID);
		output.writeObject(provision);
		output.writeObject(lease);
	}
	
	/**
	 * Returns a human readable string representation.
	 * 
	 * @return A human readable string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("SUCCESS (");
		b.append(success?"YES":"NO");
		b.append(") INSTANCE (");
		b.append(instanceID);
		b.append(") SKELETON (");
		b.append(skeletonID);
		b.append(") PROVISION (");
		b.append(provision);
		b.append(") LEASE (");
		b.append(lease);
		b.append(")");
		return b.toString();
	}
}
