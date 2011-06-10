package info.pppc.pcom.system.container.internal.component;

import info.pppc.base.system.Invocation;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.Result;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.nf.NFCollection;
import info.pppc.pcom.system.model.component.IInstanceProxy;

/**
 * This class implements the base proxy for all component instance
 * proxies and event emittors. It enables the introduction of interceptors
 * that intercept, change and stop calls from being processed before
 * they are issued to the implementation instance. 
 * 
 * @author Mac
 */
public class InstanceProxy implements IInstanceProxy {

	/**
	 * An event that signals that an invocation is about to be 
	 * transmitted to a remote system. The event is undoable. 
	 * If an undo is requested, the invocation will not be 
	 * executed and it will fail with an invocation exception.
	 * The source will be the proxy and the data object will 
	 * be the invocation.
	 */
	protected static final int EVENT_PRE_INVOKE = 1;
	
	/**
	 * An event that signals that an invocation has been 
	 * transmitted to some remote system. The source will be 
	 * the proxy and the data object will be the invocation.
	 */
	protected static final int EVENT_POST_INVOKE = 2;
	
	/**
	 * The reference to the target of this proxy.
	 */
	private ReferenceID target;
	
	/**
	 * The reference to the source of this proxy.
	 */
	private ReferenceID source;

	/**
	 * The listeners that listen to invocation events. 
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * Creates a new intercept proxy that enables the addition
	 * of interceptors in the dispatch path.
	 */
	public InstanceProxy() {
		super();
	}
    
	/**
	 * Adds an invocation listener for the specified types
	 * of events.
	 * 
	 * @param types The types of events as defined in this class.
	 * @param listener The listener to add.
	 */
	protected void __addInvocationListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes an invocation listener for the specified types
	 * of events.
	 * 
	 * @param types The types to unregister.
	 * @param listener The listener to unregister.
	 * @return True if success, false otherwise.
	 */
	protected boolean __removeInvocationListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	/**
	 * Returns the set of invocation listeners that is
	 * currently registered.
	 * 
	 * @return The set of registered listeners.
	 */
	protected IListener[] __getInvocationListeners() {
		return listeners.getListeners();
	}
	
	/**
	 * Sets the object id of the target object.
	 * 
	 * @param id The object id of the target object.
	 */
	public void setTargetID(ReferenceID id) {
		target = id;
	}
    
	/**
	 * Sets the object id of the target system.
	 *
	 * @param id The object id of the target system.
	 */
	public void setSourceID(ReferenceID id) {
		source = id;
	}
    
	/**
	 * Returns the object id of the target object.
	 * 
	 * @return ObjectID The object id of the target object.
	 */
	public ReferenceID getTargetID(){
		return target;
	}
    
	/**
	 * Returns the object id of the source object.
	 * 
	 * @return ObjectID The object id of the source object.
	 */
	public ReferenceID getSourceID() {
		return source;
	}

	/**
	 * Performs a synchronous remote invocation that can be
	 * intercepted using the listeners of this instance.
	 * 
	 * @param invocation The invocation that should be performed.
	 * @return The result object of the invocation.
	 */
	protected Result __invoke(Invocation invocation) {
		return __invoke(invocation, true);
	}

	/**
	 * Performs a synchronous remote invocation. The events
	 * flag determines whether the invocation should be sent
	 * without notifying any listeners.
	 * 
	 * @param invocation The invocation to transmit.
	 * @param events True to signal that the listeners should
	 * 	be notified, false otherwise.
	 * @return The result of the invocation.
	 */
	protected Result __invoke(Invocation invocation, boolean events) {
		if (invocation.getSource() == null) {
			invocation.setResult(null);
			invocation.setException(new InvocationException("Source is null."));
		} else if (invocation.getTarget() == null) {
			invocation.setResult(null);
			invocation.setException(new InvocationException("Target is null."));
		} else {
			boolean perform = true;
			if (events) perform = listeners.fireUndoableEvent(EVENT_PRE_INVOKE, invocation);				
			if (perform) {
				InvocationBroker broker = InvocationBroker.getInstance();	
				broker.invoke(invocation);
				if (events) listeners.fireEvent(EVENT_POST_INVOKE, invocation);
			}
		}
		return new Result(invocation.getResult(), invocation.getException());
	}
	
	
	/**
	 * Creates a new invocation that can later on be used to
	 * call invoke. 
	 * 
	 * @param name The name of the method to call.
	 * @param params The parameters of the method to call.
	 * @return The invocation for the method.
	 */
	protected Invocation __create(String name, Object[] params) {
		Invocation invocation = new Invocation();
		invocation.setSignature(name);
		invocation.setTarget(getTargetID());
		invocation.setSource(getSourceID());
		invocation.setArguments(params);
		invocation.setRequirements(NFCollection.getDefault
			(NFCollection.TYPE_SYNCHRONOUS, true));
		return invocation;		
	}
	
}
