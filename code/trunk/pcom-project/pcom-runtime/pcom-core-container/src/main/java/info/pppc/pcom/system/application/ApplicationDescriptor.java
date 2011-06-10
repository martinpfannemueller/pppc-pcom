package info.pppc.pcom.system.application;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.pcom.system.contract.Contract;

import java.io.IOException;
import java.util.Vector;

/**
 * The application descriptor is a data object that contains the application configuration. 
 * Using this object a gui can start an application with certain preferences. At the moment,
 * the application descriptor also specifies the assembler that will be used by the application
 * manager to configure the application. 
 * 
 * @author Mac
 */
public final class ApplicationDescriptor implements ISerializable {

	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PD";
	
	/**
	 * The state constant that signals that the application is up and running.
	 */
	public static final int STATE_APPLICATION_STARTED = 1;
	
	/**
	 * The state constant that signals that the application is reconfiguring
	 * and paused.
	 */
	public static final int STATE_APPLICATION_PAUSED = 2;
	
	/**
	 * The state constant that signals that the application is stopped.
	 */
	public static final int STATE_APPLICATION_STOPPED = 3;
	
	/**
	 * The current state of the application. This will be one of the
	 * state constants defined in this class.
	 */
	private int state = 0;
	
	/**
	 * The unique id of the application.
	 */
	private ObjectID applicationID = null;
	
	/**
	 * The remote reference of the assembler that will be used to
	 * start and adapt the application. This field must point to
	 * some assembler. 
	 */
	private ReferenceID assemblerID = null;
	
	/**
	 * The preferences that will be used to configure the application.
	 * The preferences must be non-null and must at least contain one
	 * entry.
	 */
	private Vector preferences = null;
	
	/**
	 * The human readable name of the application. This is not interpreted
	 * by the container or the application manager. The name may as well
	 * be null.
	 */
	private String name = null;
	
	/**
	 * The image of the application. This is not interpreted by the container
	 * or the application manager. The image may as well be null.
	 */
	private byte[] image = null;
	
	/**
	 * The current contract, if the application is executed. Otherwise
	 * this will be null.
	 */
	private Contract status;
	
	/**
	 * Creates a new uninitialized application descriptor. 
	 */
	public ApplicationDescriptor() {
		super();
	}

	/**
	 * Returns the state of the application. The state will be one of
	 * the state constants defined in this class.
	 * 
	 * @return The state of the application.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Sets the state of the application. The state must be one of the
	 * state constants defined in this class.
	 * 
	 * @param state The state of the application.
	 */
	protected void setState(int state) {
		this.state = state;
	}
		
	/**
	 * Returns the current preference level that is executed.
	 * 
	 * @return Returns the status.
	 */
	public Contract getStatus() {
		return status;
	}

	/**
	 * Sets the application status. This is the contract that is currently
	 * running (i.e. the preference level that is running).
	 * 
	 * @param status The status to set.
	 */
	protected void setStatus(Contract status) {
		this.status = status;
	}
	
	
	/**
	 * Returns the remote reference to the assembler that will be used to 
	 * configure and adapt the application.
	 * 
	 * @return The remote reference of the assembler.
	 */
	public ReferenceID getAssemblerID() {
		return assemblerID;
	}

	/**
	 * Sets the remote reference of the assembler that will be used to 
	 * configure and adapt the application.
	 * 
	 * @param assemblerID The remote refernce of the assembler used within
	 * 	this application.
	 */
	public void setAssemblerID(ReferenceID assemblerID) {
		this.assemblerID = assemblerID;
	}

	/**
	 * Returns the preferences that are directed towards the application.
	 * The preferences are expressed as a vector of contracts. The first
	 * index is the highest preference, the last index is the lowest.
	 * 
	 * @return The preferences towards the application.
	 */
	public Vector getPreferences() {
		return preferences;
	}

	/**
	 * Sets the preferences that are directed towards the application.
	 * The preferences are expressed as a vector of contracts. The first
	 * index is the highest preference, the last index is the lowest.
	 * 
	 * @param preferences The preferences towards the application.
	 */
	public void setPreferences(Vector preferences) {
		this.preferences = preferences;
	}
	
	/**
	 * Returns the image of the application that is used to visualize
	 * it as a byte array. This field is not interpreted by the application
	 * manager or by the container. 
	 * 
	 * @return Returns the image of the application.
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * Sets the image of the application that is used to visualize it.
	 * This field is not interpreted by the application manager or by the
	 * container.
	 * 
	 * @param image The image of the application as bytes.
	 */
	public void setImage(byte[] image) {
		this.image = image;
	}

	/**
	 * Returns the name of the application as string or null if none is
	 * set.
	 * 
	 * @return Returns the name of the application.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the application to the specified name.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the application id of the application described by this
	 * descriptor.
	 * 
	 * @return Returns the applicationID of the descriptor.
	 */
	public ObjectID getApplicationID() {
		return applicationID;
	}

	/**
	 * Sets the application id of the application described by this
	 * descriptor.
	 * 
	 * @param applicationID The applicationID to set.
	 */
	public void setApplicationID(ObjectID applicationID) {
		this.applicationID = applicationID;
	}
	
	/**
	 * Deserializes the descriptor from the specified input stream.
	 * 
	 * @param input The input stream to read from.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		state = input.readInt();
		status = (Contract)input.readObject();
		name = (String)input.readObject();
		image = (byte[])input.readObject();
		applicationID = (ObjectID)input.readObject();
		assemblerID = (ReferenceID)input.readObject();
		preferences = (Vector)input.readObject();
	}
	
	/**
	 * Serializes the descriptor to the specified output stream.
	 * 
	 * @param output The output stream to write to.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeInt(state);
		output.writeObject(status);
		output.writeObject(name);
		output.writeObject(image);
		output.writeObject(applicationID);
		output.writeObject(assemblerID);
		output.writeObject(preferences);
	}
	
	/**
	 * Returns a human readable string representation.
	 * 
	 * @return A human readable string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("NAME (");
		b.append(name);
		b.append(") IMAGE (");
		b.append((image == null)?"NONE":image.length + "B");
		b.append(")STATUS (");
		b.append(status);
		b.append(")STATE (");
		switch (state) {
			case STATE_APPLICATION_STARTED: b.append("STARTED"); break;
			case STATE_APPLICATION_STOPPED: b.append("STOPPED"); break;
			case STATE_APPLICATION_PAUSED: b.append("PAUSED"); break;
			default: b.append("UNKNOWN"); break;
		}
		b.append(") ID (");
		b.append(applicationID);
		b.append(") ASSEMBLER (");
		b.append(assemblerID);
		b.append(") PREFERENCES ( ");
		for (int i = 0; i < preferences.size(); i++) {
			b.append(i + 1);
			b.append(": (");
			b.append(preferences.elementAt(i));
			b.append(")");
		}
		b.append(")");
		return b.toString();
	}

}
