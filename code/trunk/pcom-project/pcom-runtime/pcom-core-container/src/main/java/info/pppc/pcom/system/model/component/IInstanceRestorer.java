package info.pppc.pcom.system.model.component;

import info.pppc.base.system.event.IListener;

/**
 * The instance state object enables an application developer
 * to control many aspects of the automated state retrieval
 * and restoration feature of PCOM. It can be used to add
 * reconfiguration costs, to enable and disable invocation
 * recording, to request checkpoints and to delete recorded
 * invocations and checkpoints.
 * 
 * @author Mac
 */
public interface IInstanceRestorer {

	/**
	 * This event signals that the currently bound instance needs 
	 * to be replaced. This method is only called when there is 
	 * an existing instance for the binding. A listener could,
	 * for example, try to request a checkpoint in order to 
	 * speed up the restoration process upon the reception of this
	 * event. The source of the event will be the restorer and
	 * the data object will be the proxy.
	 */
	public static final int EVENT_RELEASE = 1;
	
	/**
	 * This event signals that the state restoration takes place 
	 * after the listeners return. The event is undoable by the 
	 * listeners. If one of the listeners request an
	 * undo, the checkpoint and the history will not be applied
	 * to the newly bound instance. The source of this event 
	 * will be this restorer and the data object will be the 
	 * proxy of the instance.
	 */
	public static final int EVENT_RESTORE = 2;

	/**
	 * Requests that the specified listener should be
	 * registered for the specified event types. Possible
	 * event types are defined in this interface.
	 * 
	 * @param types The types of events to register for.
	 * @param listener The listener to register.
	 */
	public void addStateListener(int types, IListener listener);
	
	/**
	 * Removes the specified listener from the set of specified
	 * event types and returns whether the removal succeeded. 
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister.
	 * @return True if success, otherwise false.
	 */
	public boolean removeStateListener(int types, IListener listener);
	
	/**
	 * Returns an array containing all listeners that are 
	 * currently registered for automatic state restoration. 
	 * 
	 * @return The registered listeners.
	 */
	public IListener[] getStateListeners();
	
	/**
	 * Adds an additional cost summand to the cost calculation
	 * performed by the instance state. The summand can be used
	 * to express manual state restoration that needs to be 
	 * performed when the bound instance is changed. If the summand
	 * is negative, the summand will be set to 0, to indicate no
	 * additional cost.
	 * 
	 * @param summand The cost summand to add to the automated
	 * 	cost calculation.
	 */
	public void setCostSummand(int summand);
	
	/**
	 * Returns the current cost summand that is added to the
	 * state restoration cost. The default summand is 0 to express
	 * that all state restoration is performed automatically.
	 * The summand cannot be negative as this does not make
	 * any sense, at all, really.
	 * 
	 * @return The cost summand that is currently set.
	 */
	public int getCostSummand();
	
	/**
	 * Returns the current total cost for state restoration.
	 * This cost is computed from the size of the checkpoint
	 * and the size of the message history. It also includes
	 * the manual cost summand that can be set using the
	 * corresponding methods in this state.
	 * 
	 * @return The total cost for reconfiguring the 
	 */
	public int getCostTotal();
	
	/**
	 * Requests the creation and retrieval of a new checkpoint
	 * from the bound instance.
	 * 
	 * @return True if the checkpoint has been created
	 * 	and stored, false otherwise.
	 */
	public boolean createCheckpoint();
	
	/**
	 * Returns the checkpoint that is currently stored
	 * as part of the instance state. Calling this
	 * method will return null if no checkpoint has
	 * been stored so far.
	 * 
	 * @return The checkpoint that is currently stored or
	 * 	null if none is stored.
	 */
	public IInstanceCheckpoint getCheckpoint();
	
	/**
	 * Removes the checkpoint that is currently stored
	 * as part of the instance state. 
	 */
	public void clearCheckpoint();
	
	/**
	 * Returns the history of invocations that has been
	 * sent to the remote device since the last checkpoint
	 * has been stored. Using the history, the instance 
	 * developer can configure which calls should be traced. 
	 * In addition the developer can also reorganize the
	 * history or delete individual entries, etc.
	 * 
	 * @return The message history used for manual configuration
	 * 	of the automated logging mechanisms.
	 */
	public IInstanceHistory getHistory();
	
	/**
	 * Returns the instance proxy that is associated with this
	 * instance state object.
	 * 
	 * @return The proxy associated with the state.
	 */
	public IInstanceProxy getProxy();
	
}
