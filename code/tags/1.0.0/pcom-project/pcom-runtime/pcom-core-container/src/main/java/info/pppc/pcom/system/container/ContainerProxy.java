package info.pppc.pcom.system.container;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class ContainerProxy extends info.pppc.base.system.Proxy implements info.pppc.pcom.system.container.IContainer {
	
	/**
	 * Default constructor to create a new object.
	 */
	public ContainerProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return seeinfo.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public java.util.Vector getResourcesUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "java.util.Vector getResourcesUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Vector)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult getResourcesUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "java.util.Vector getResourcesUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param demands see info.pppc.pcom.system.container.IContainer
	 * @return seeinfo.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public java.util.Hashtable getTemplates(java.util.Vector demands) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = demands;
		String __method = "java.util.Hashtable getTemplates(java.util.Vector)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Hashtable)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param demands see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult getTemplatesDef(java.util.Vector demands)  {
		Object[] __args = new Object[1];
		__args[0] = demands;
		String __method = "java.util.Hashtable getTemplates(java.util.Vector)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @return seeinfo.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.pcom.system.container.InstanceCheckpoint storeInstance(info.pppc.base.system.ObjectID successorID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = successorID;
		String __method = "info.pppc.pcom.system.container.InstanceCheckpoint storeInstance(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.container.InstanceCheckpoint)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.base.system.FutureResult storeInstanceDef(info.pppc.base.system.ObjectID successorID)  {
		Object[] __args = new Object[1];
		__args[0] = successorID;
		String __method = "info.pppc.pcom.system.container.InstanceCheckpoint storeInstance(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void saveApplication(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void saveApplication(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public info.pppc.base.system.FutureResult saveApplicationDef(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase)  {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void saveApplication(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void saveApplicationAsync(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void saveApplication(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return seeinfo.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public java.util.Vector getInstancesUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "java.util.Vector getInstancesUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Vector)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult getInstancesUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "java.util.Vector getInstancesUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @param provision see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void changeInstance(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase, info.pppc.pcom.system.contract.Contract provision) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[4];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		__args[3] = provision;
		String __method = "void changeInstance(info.pppc.base.system.ObjectID, java.lang.String, int, info.pppc.pcom.system.contract.Contract)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @param provision see info.pppc.pcom.system.container.IContainerDemander
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public info.pppc.base.system.FutureResult changeInstanceDef(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase, info.pppc.pcom.system.contract.Contract provision)  {
		Object[] __args = new Object[4];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		__args[3] = provision;
		String __method = "void changeInstance(info.pppc.base.system.ObjectID, java.lang.String, int, info.pppc.pcom.system.contract.Contract)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @param provision see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void changeInstanceAsync(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase, info.pppc.pcom.system.contract.Contract provision) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[4];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		__args[3] = provision;
		String __method = "void changeInstance(info.pppc.base.system.ObjectID, java.lang.String, int, info.pppc.pcom.system.contract.Contract)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public void stopInstance(info.pppc.base.system.ObjectID successorID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = successorID;
		String __method = "void stopInstance(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.base.system.FutureResult stopInstanceDef(info.pppc.base.system.ObjectID successorID)  {
		Object[] __args = new Object[1];
		__args[0] = successorID;
		String __method = "void stopInstance(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public void stopInstanceAsync(info.pppc.base.system.ObjectID successorID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = successorID;
		String __method = "void stopInstance(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @return seeinfo.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public java.util.Hashtable getResources() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "java.util.Hashtable getResources()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Hashtable)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult getResourcesDef()  {
		Object[] __args = new Object[0];
		String __method = "java.util.Hashtable getResources()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @return seeinfo.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public java.util.Vector getFactoriesUI() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector getFactoriesUI()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Vector)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult getFactoriesUIDef()  {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector getFactoriesUI()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @param context see info.pppc.pcom.system.container.IContainerProvider
	 * @param phase see info.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public void pauseInstance(info.pppc.base.system.ObjectID successorID, info.pppc.pcom.system.assembler.AssemblyPointer context, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = successorID;
		__args[1] = context;
		__args[2] = new Integer(phase);
		String __method = "void pauseInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.assembler.AssemblyPointer, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @param context see info.pppc.pcom.system.container.IContainerProvider
	 * @param phase see info.pppc.pcom.system.container.IContainerProvider
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.base.system.FutureResult pauseInstanceDef(info.pppc.base.system.ObjectID successorID, info.pppc.pcom.system.assembler.AssemblyPointer context, int phase)  {
		Object[] __args = new Object[3];
		__args[0] = successorID;
		__args[1] = context;
		__args[2] = new Integer(phase);
		String __method = "void pauseInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.assembler.AssemblyPointer, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @param context see info.pppc.pcom.system.container.IContainerProvider
	 * @param phase see info.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public void pauseInstanceAsync(info.pppc.base.system.ObjectID successorID, info.pppc.pcom.system.assembler.AssemblyPointer context, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = successorID;
		__args[1] = context;
		__args[2] = new Integer(phase);
		String __method = "void pauseInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.assembler.AssemblyPointer, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @return seeinfo.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public java.util.Vector getAllocatorsUI() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector getAllocatorsUI()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Vector)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult getAllocatorsUIDef()  {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector getAllocatorsUI()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void changeInstanceUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void changeInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult changeInstanceUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void changeInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void changeInstanceUIAsync(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void changeInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void commitInstanceUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void commitInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult commitInstanceUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void commitInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void commitInstanceUIAsync(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void commitInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void commitResourceUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void commitResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult commitResourceUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void commitResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void commitResourceUIAsync(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void commitResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void changeResourceUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void changeResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult changeResourceUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void changeResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void changeResourceUIAsync(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void changeResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param setup see info.pppc.pcom.system.container.IContainerProvider
	 * @param phase see info.pppc.pcom.system.container.IContainerProvider
	 * @return seeinfo.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.pcom.system.container.InstanceState startInstance(info.pppc.pcom.system.container.InstanceSetup setup, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = setup;
		__args[1] = new Integer(phase);
		String __method = "info.pppc.pcom.system.container.InstanceState startInstance(info.pppc.pcom.system.container.InstanceSetup, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.container.InstanceState)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param setup see info.pppc.pcom.system.container.IContainerProvider
	 * @param phase see info.pppc.pcom.system.container.IContainerProvider
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.base.system.FutureResult startInstanceDef(info.pppc.pcom.system.container.InstanceSetup setup, int phase)  {
		Object[] __args = new Object[2];
		__args[0] = setup;
		__args[1] = new Integer(phase);
		String __method = "info.pppc.pcom.system.container.InstanceState startInstance(info.pppc.pcom.system.container.InstanceSetup, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void stopInstanceUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void stopInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult stopInstanceUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void stopInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void stopInstanceUIAsync(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void stopInstanceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void stopResourceUI(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void stopResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainer
	 */
	public info.pppc.base.system.FutureResult stopResourceUIDef(info.pppc.base.system.ObjectID id)  {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void stopResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param id see info.pppc.pcom.system.container.IContainer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainer
	 * @see info.pppc.pcom.system.container.IContainer
	 */
	public void stopResourceUIAsync(info.pppc.base.system.ObjectID id) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = id;
		String __method = "void stopResourceUI(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void exitApplication(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void exitApplication(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public info.pppc.base.system.FutureResult exitApplicationDef(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase)  {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void exitApplication(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void exitApplicationAsync(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void exitApplication(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @param checkpoint see info.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public void loadInstance(info.pppc.base.system.ObjectID successorID, info.pppc.pcom.system.container.InstanceCheckpoint checkpoint) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = successorID;
		__args[1] = checkpoint;
		String __method = "void loadInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.container.InstanceCheckpoint)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @param checkpoint see info.pppc.pcom.system.container.IContainerProvider
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public info.pppc.base.system.FutureResult loadInstanceDef(info.pppc.base.system.ObjectID successorID, info.pppc.pcom.system.container.InstanceCheckpoint checkpoint)  {
		Object[] __args = new Object[2];
		__args[0] = successorID;
		__args[1] = checkpoint;
		String __method = "void loadInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.container.InstanceCheckpoint)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param successorID see info.pppc.pcom.system.container.IContainerProvider
	 * @param checkpoint see info.pppc.pcom.system.container.IContainerProvider
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerProvider
	 * @see info.pppc.pcom.system.container.IContainerProvider
	 */
	public void loadInstanceAsync(info.pppc.base.system.ObjectID successorID, info.pppc.pcom.system.container.InstanceCheckpoint checkpoint) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = successorID;
		__args[1] = checkpoint;
		String __method = "void loadInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.container.InstanceCheckpoint)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void removeInstance(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void removeInstance(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public info.pppc.base.system.FutureResult removeInstanceDef(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase)  {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void removeInstance(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param predecessorID see info.pppc.pcom.system.container.IContainerDemander
	 * @param name see info.pppc.pcom.system.container.IContainerDemander
	 * @param phase see info.pppc.pcom.system.container.IContainerDemander
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.container.IContainerDemander
	 * @see info.pppc.pcom.system.container.IContainerDemander
	 */
	public void removeInstanceAsync(info.pppc.base.system.ObjectID predecessorID, java.lang.String name, int phase) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = predecessorID;
		__args[1] = name;
		__args[2] = new Integer(phase);
		String __method = "void removeInstance(info.pppc.base.system.ObjectID, java.lang.String, int)";
		info.pppc.base.system.Invocation __invocation = proxyCreateAsynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeAsynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
}
