package info.pppc.pcom.system.container.internal.component;

import info.pppc.base.system.IInvocationHandler;
import info.pppc.base.system.ISession;
import info.pppc.base.system.Invocation;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.Result;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.MultiOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceSkeleton;
import info.pppc.pcom.system.model.component.IInstanceHistory.IEntry;

/**
 * This class implements the base skeleton for all component instances
 * and event collectors. It enables the introduction of interceptors
 * that intercept, change and stop calls from being processed before
 * they are issued to the implementation instance. 
 * 
 * @author Mac
 */
public abstract class InstanceSkeleton implements IInvocationHandler, IInstanceSkeleton {

	/**
	 * The signature of the build-in method that is used to request
	 * a checkpoint from the instance. The call does not have any
	 * parameters and it returns the checkpoint. 
	 */
	protected static String SIGNATURE_STORE = ";PCOM_STORE";
	
	/**
	 * The signature of the built-in method that is used to restore
	 * the state of the bound instance using a checkpoint and a
	 * history. The parameters of the invocation are the checkpoint
	 * and the signature and the return type is void.
	 */
	protected static String SIGNATURE_RESTORE = ";PCOM_RESTORE";
	
	/**
	 * The dispatch target of the skeleton. This target must be set
	 * before a call is dispatched.
	 */
	private IInstance target;

	/**
	 * The object id under which the skeleton is registered. This
	 * object id must be maintained externally.
	 */
	private ObjectID objectID;

	/**
	 * Creates a new interceptable skeleton without any interceptors
	 * and with an undefined dispatch target implementation.
	 */
	public InstanceSkeleton() {
		super();
	}

	/**
	 * Sets the dispatch target of this skeleton.
	 * 
	 * @param target The dispatch target of the skeleton.
	 */
	public void setTarget(IInstance target) {
		this.target = target;
	}

	/**
	 * Returns the dispatch target of this skeleton.
	 * 
	 * @return The dispatch target of the skeleton.
	 */
	public IInstance getTarget() {
		return target;
	}

	/**
	 * Returns the object id of the skeleton.
	 * 
	 * @return The object id of the skeleton.
	 */
	public ObjectID getObjectID() {
		return objectID;
	}
	
	/**
	 * Sets the object if of the skeleton.
	 * 
	 * @param id The new object id of the skeleton.
	 */
	public void setObjectID(ObjectID id) {
		objectID = id;
	}

	/**
	 * Called whenever an invocation should be executed. This
	 * implementation uses the intercept bundle and the intercept
	 * handler to dispatch the invocation as interceptable call.
	 * 
	 * @param inv The invocation object that must be dispatched.
	 * @param session The session that is used.
	 */
	public void invoke(Invocation inv, ISession session) {
		String signature = inv.getSignature();
		if (signature.equals(SIGNATURE_STORE)) {
			try {
				Logging.debug(getClass(), "Storing checkpoint.");
				InstanceCheckpoint checkpoint = new InstanceCheckpoint();
				target.storeCheckpoint(new InstanceCheckpointAdapter(checkpoint));
				inv.setResult(checkpoint);
			} catch (Throwable t) {
				Logging.error(getClass(), "Could not store state.", t);
				inv.setException(new InvocationException("Could not store state."));
			}
		} else if (signature.equals(SIGNATURE_RESTORE)) {
			try {
				Object[] arguments = inv.getArguments();
				InstanceCheckpoint checkpoint = (InstanceCheckpoint)arguments[0];
				InstanceHistory history = (InstanceHistory)arguments[1];
				if (checkpoint != null) {
					Logging.debug(getClass(), "Restoring checkpoint.");
					target.loadCheckpoint(new InstanceCheckpointAdapter(checkpoint));
				}
				IEntry[] entries = history.getEntries();
				Logging.debug(getClass(), "Restoring " + entries.length + " history entries.");
				MultiOperation multi = new MultiOperation(InvocationBroker.getInstance());
				for (int i = 0; i < entries.length; i++) {
					final IEntry entry = entries[i];
					IOperation operation = new IOperation() {
						public void perform(IMonitor monitor) throws Exception {
							Result r = dispatch(entry.getSignature(), entry.getArguments());
							if (r.hasException()) {
								Logging.error(getClass(), "Exception during restoration in " + entry.getSignature() + ".", r.getException());
							}
						}
					};
					multi.addOperation(operation);
					if (i == entries.length - 1 || ! entries[i + 1].isParallel()) {
						multi.performSynchronous();
						multi = new MultiOperation(InvocationBroker.getInstance());
					}
				}
			} catch (Throwable t) {
				Logging.error(getClass(), "Could not restore state.", t);
				inv.setException(new InvocationException("Could not restore state."));				
			}
		} else {
			Result result = dispatch(inv.getSignature(), inv.getArguments());
			inv.setResult(result.getValue());
			inv.setException(result.getException());			
		}
	}
	
	/**
	 * This method is implemented by the pcom generator tools for
	 * a specific set of interfaces contained in a component 
	 * description.
	 * 
	 * @param method The signature of the method that is called.
	 * @param parameters The parameters of the method call.
	 * @return A result object that encapsulates the result of the
	 * 	method call.
	 */
	protected abstract Result dispatch(String method, Object[] parameters);
}
