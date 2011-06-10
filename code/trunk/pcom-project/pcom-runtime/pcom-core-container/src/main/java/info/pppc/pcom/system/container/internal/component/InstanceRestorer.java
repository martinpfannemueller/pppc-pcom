package info.pppc.pcom.system.container.internal.component;

import java.io.IOException;
import java.util.Hashtable;

import info.pppc.base.system.Invocation;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceHistory;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceRestorer;
import info.pppc.pcom.system.model.component.IInstanceHistory.IEntry;

/**
 * The implementation of the instance restorer interface. This
 * implementation is added to each proxy in order to support
 * stateful components.
 * 
 * @author Mac
 */
public class InstanceRestorer implements IInstanceRestorer, IListener {
	
	/**
	 * The instance proxy uses this instance state.
	 */
	protected InstanceProxy proxy;
	
	/**
	 * The instance checkpoint that (might) be stored
	 * in this state.
	 */
	protected InstanceCheckpoint checkpoint;
	
	/**
	 * The instance's invocation history.
	 */
	protected InstanceHistory history = new InstanceHistory();
	
	/**
	 * The cost summand that describes the cost for manual
	 * part of the state restoration procedure.
	 */
	protected int costSummand = 0;
	
	/**
	 * The listener bundle that registers state listeners.
	 */
	protected ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * A flag that indicates whether a checkpoint is currently
	 * requested.
	 */
	protected boolean checkpointing = false;
	
	/**
	 * A hashtable that maps invocations to history entries. The
	 * hashtable contains all outgoing invocations that have
	 * not been completed.
	 */
	protected Hashtable outgoing = new Hashtable();
	
	/**
	 * Creates a new instance state for the specified proxy.
	 * 
	 * @param proxy The proxy using this state.
	 */
	public InstanceRestorer(InstanceProxy proxy) {
		this.proxy = proxy;
		proxy.__addInvocationListener(Event.EVENT_EVERYTHING, this);
	}
	
	/**
	 * Adds a state listener at the bundle for some event types.
	 * 
	 * @param listener The listener to add.
	 * @param types The types to register for.
	 */
	public void addStateListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}

	/**
	 * Requests the creation of a checkpoint.
	 * 
	 * @return True if the checkpoint has been created.
	 */
	public boolean createCheckpoint() {
		// block new outgoing and block until all current
		// outgoing have returned.
		synchronized (this) {
			if (checkpointing) throw new RuntimeException("Duplicate checkpoint request.");
			checkpointing = true;
			while (! outgoing.isEmpty()) {
				try {
					this.wait();	
				} catch (InterruptedException e) {
					Logging.error(getClass(), "Thread got interrupted.", e);
				}
			}
		}
		boolean result = false;
		Invocation invocation = proxy.__create
			(InstanceSkeleton.SIGNATURE_STORE, new Object[0]);
		proxy.__invoke(invocation, false);
		if (invocation.getException() == null) {
			InstanceCheckpoint checkpoint = (InstanceCheckpoint)invocation.getResult();
			if (checkpoint.isComplete() || this.checkpoint == null) {
				this.checkpoint = checkpoint;
				this.checkpoint.setComplete(true);
			} else {
				checkpoint.apply(this.checkpoint);
			}
			history.clear();
			result = true;
		} 
		// unblock all outgoing
		synchronized (this) {
			checkpointing = false;
			this.notifyAll();
		}
		return result;
	}

	/**
	 * Returns the checkpoint that is currently stored
	 * or null if none is set.
	 * 
	 * @return The current instance checkpoint.
	 */
	public IInstanceCheckpoint getCheckpoint() {
		return new InstanceCheckpointAdapter(checkpoint);
	}
	
	/**
	 * Removes the checkpoint that is currently set.
	 */
	public void clearCheckpoint() {
		this.checkpoint = null;
	}

	/**
	 * Returns the cost summand value.
	 * 
	 * @return The cost summand value.
	 */
	public int getCostSummand() {
		return costSummand;
	}

	/**
	 * Computes the total cost of the state restoration 
	 * procedure including all manual parts.
	 * 
	 * @return The total cost.
	 */
	public int getCostTotal() {
		// TODO compute
		return 0;
	}

	/**
	 * Returns the history used by the restorer.
	 * 
	 * @return The history.
	 */
	public IInstanceHistory getHistory() {
		return history;
	}

	/**
	 * Returns the proxy bound to the restorer.
	 * 
	 * @return The proxy.
	 */
	public IInstanceProxy getProxy() {
		return proxy;
	}

	/**
	 * Returns all listeners that have been registered for state
	 * changes.
	 * 
	 * @return The registered listeners.
	 */
	public IListener[] getStateListeners() {
		return listeners.getListeners();
	}

	/**
	 * Removes the specified state listener for the specified set
	 * of types.
	 * 
	 * @param types The types to unregister.
	 * @param listener The listener to unregister.
	 * @return True if successful, false otherwise.
	 */
	public boolean removeStateListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}

	/**
	 * Sets the cost summand to the specified value. If the
	 * summand is negative, it will be set to 0.
	 * 
	 * @param summand The new cost summand.
	 */
	public void setCostSummand(int summand) {
		if (summand < 0) summand = 0;
		this.costSummand = summand;
	}
	
	/**
	 * Called by the binding when the restore should take
	 * place.
	 */
	protected void restore() {
		// wait until all outgoing have returned
		synchronized (this) {
			while (checkpointing || ! outgoing.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					Logging.error(getClass(), "Thread got interrupted.", e);
				}
			}
		}
		boolean perform = listeners.fireUndoableEvent(EVENT_RESTORE, proxy);
		if (perform && (checkpoint != null || history.size() != 0)) {
			Logging.debug(getClass(), "Restoring state " + (checkpoint == null?"without":"with") 
				+ " checkpoint and history size " + history.size() + ".");
			Invocation invocation = proxy.__create(InstanceSkeleton.SIGNATURE_RESTORE, 
				new Object[] { checkpoint, history });
			proxy.__invoke(invocation, false);
		}
	}

	/**
	 * Called by the binding when the restore has been 
	 * completed.
	 */
	protected void release() {
		listeners.fireEvent(EVENT_RELEASE, proxy);
	}
	
	/**
	 * This method is called, when the proxy sends an invocation
	 * to the remote system and when it returns from the remote
	 * system.
	 * 
	 * @param event The event.
	 */
	public void handleEvent(Event event) {
		Invocation invocation = (Invocation)event.getData();
		switch (event.getType()) {
			case InstanceProxy.EVENT_PRE_INVOKE: 
			{
				try {
					IEntry entry = history.createEntry(invocation.getSignature(), invocation.getArguments());	
					synchronized (this) {
						while (checkpointing) {
							try {
								this.wait();	
							} catch (InterruptedException e) {
								Logging.error(getClass(), "Thread got interrupted.", e);
							}
						}
						if (history.isEnabled()) {
							history.addEntry(entry, ! outgoing.isEmpty());
							outgoing.put(invocation, entry);							
						}
					}
				} catch (IOException e) {
					invocation.setException(e);
					event.undo();
				}
			}
			break;
			case InstanceProxy.EVENT_POST_INVOKE:
			{
				synchronized (this) {
					// if invocation exception, remove entry and adjust
					// parallel flag of next invocation, if any
					Throwable t = invocation.getException();
					IEntry e = (IEntry)outgoing.remove(invocation);
					if (e != null && t != null && t instanceof InvocationException) {
						int idx = -1;
						IEntry[] ex = history.getEntries();
						for (int i = ex.length - 1; i >= 0; i--) {
							if (ex[i] == e) {
								if (i < ex.length - 1) idx = i;
								break;
							} 
						}
						history.removeEntry(e);
						if (idx != -1) history.setEntry(ex[idx + 1], idx, e.isParallel());
					}
					if (outgoing.isEmpty()) this.notifyAll();
				}
			}
			break;
			default:
				// nothing to be done
		}
	}

	
}
