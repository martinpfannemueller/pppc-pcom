package info.pppc.pcom.capability.ir;

import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

/**
 * This class implements the ir accessor interface for the ir allocator.
 * 
 * @author Mac
 */
public class IRAccessor implements IIRAccessor {

	/**
	 * The local id of the accessor.
	 */
	private final short localID;
	
	/**
	 * The current remote id.
	 */
	private short remoteID;
	
	/**
	 * The first time the current remote id has been received.
	 */
	private long remoteFirst;
	
	/**
	 * The last time the remote id has been received.
	 */
	private long remoteLast;
	
	/**
	 * The listeners that have been registered at the accessor.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * This class implements the ir accessor interface.
	 * 
	 *  @param localID The local id of the accessor.
	 */
	protected IRAccessor(short localID) {
		this.localID = localID;
	}

	/**
	 * Returns the local id.
	 * 
	 * @return The local id.
	 */
	public short getLocalID() {
		return localID;
	}

	/**
	 * Returns the current remote id.
	 * 
	 * @return The current remote id.
	 */
	public short getRemoteID() {
		return remoteID;
	}

	/**
	 * Returns the first time the current remote id has been
	 * received.
	 * 
	 * @return The first time the current remote id has been
	 * 	received.
	 */
	public long getRemoteFirst() {
		return remoteFirst;
	}

	/**
	 * Returns the last time the current remote id has been
	 * received.
	 * 
	 * @return The last time the current remote id has been	
	 * 	received.
	 */
	public long getRemoteLast() {
		return remoteLast;
	}

	/**
	 * Adds the specified listener to the set of registered listeners.
	 * 
	 * @param types The types of events to receive.
	 * @param listener The listener to register.
	 */
	public void addRemoteListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}

	/**
	 * Removes the specified listener from the set of registered listeners.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been registered, false otherwise.
	 */
	public boolean removeRemoteListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	/**
	 * Updates the remote id, the first and last detection times of the remote id
	 * and potentially signals the event to all registered listeners.
	 * 
	 * @param remoteID The remote id that has been received.
	 * @param remoteFirst The first time the id has been received.
	 * @param remoteLast The last time the id has been received.
	 * @param notify A flag that indicates whether the listeners should be notified.
	 * 	If true, the listeners will be notified, if false, the update will be silent.
	 */
	protected void update(short remoteID, long remoteFirst, long remoteLast, boolean notify) {
		boolean change = this.remoteID != remoteID;
		this.remoteID = remoteID;
		this.remoteFirst = remoteFirst;
		this.remoteLast = remoteLast;
		if (notify) {
			if (change) {
				listeners.fireEvent(EVENT_REMOTE_CHANGED, new Short(remoteID));	
			} else {
				listeners.fireEvent(EVENT_REMOTE_RECEIVED, new Short(remoteID));				
			}
		}
	}

}
