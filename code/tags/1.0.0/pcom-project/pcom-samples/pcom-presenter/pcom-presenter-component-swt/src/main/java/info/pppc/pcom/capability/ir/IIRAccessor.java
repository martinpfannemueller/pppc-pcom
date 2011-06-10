package info.pppc.pcom.capability.ir;

import info.pppc.base.system.event.IListener;

/**
 * This is the accessor object of the ir allocator. It enables users to retrieve 
 * the current remote id as well as the last time, the remote id was refreshed
 * and the local id.  
 * 
 * @author Mac
 */
public interface IIRAccessor {

	/**
	 * This event is fired to listeners whenever a remote id has been
	 * received. The data object of the event will be the remote id
	 * as short. The source object will be the accessor.
	 */
	public static final int EVENT_REMOTE_RECEIVED = 1;
	
	/**
	 * The event is fired to listeners whenever the remote id changes
	 * its value. The data object of the event will be the remote id
	 * as short. The source object will be the accessor.
	 */
	public static final int EVENT_REMOTE_CHANGED = 2;
	
	/**
	 * Returns the local id of the allocator that is broadcasted to
	 * other devices.
	 * 
	 * @return The local id of the allocator.
	 */
	public short getLocalID();
	
	/**
	 * Returns the last remote id that has been received. Note that
	 * if the device has not received any id, the id will be 0.
	 * 
	 * @return The last id that has been received or 0 if no id has
	 * 	been received so far.
	 */
	public short getRemoteID();
	
	/**
	 * Returns the first time (as current millis), at which the current
	 * remote id has been received. If no remote id has been received so 
	 * far, this method will return 0.
	 * 
	 * @return The first point in time when the current remote id has been
	 * 	received or 0 if no remote id has been received so far.
	 */
	public long getRemoteFirst();
	
	/**
	 * Returns the last point in time, the remote id has been seen
	 * or 0 if no remote id has been received so far.
	 * 
	 * @return The last point in time, the remote id has been received
	 * 	or 0 if no remote id has been received so far.
	 */
	public long getRemoteLast();
	
	/**
	 * Adds a listener for the specified set of events. Possible events
	 * are defined by the event constants in this interface.
	 * 
	 * @param types The types of events to register for.
	 * @param listener The listener to register.
	 */
	public void addRemoteListener(int types, IListener listener);
	
	/**
	 * Removes the specified listener for the specified set of events.
	 * Possible events are defined by the event constants in this 
	 * interface.
	 * 
	 * @param types The types of events to unregister for.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been unregistered, false otherwise.
	 */
	public boolean removeRemoteListener(int types, IListener listener);
	
}
