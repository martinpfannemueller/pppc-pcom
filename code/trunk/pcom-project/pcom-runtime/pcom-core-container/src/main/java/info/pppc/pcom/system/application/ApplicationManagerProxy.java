package info.pppc.pcom.system.application;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class ApplicationManagerProxy extends info.pppc.base.system.Proxy implements info.pppc.pcom.system.application.IApplicationManager {
	
	/**
	 * Default constructor to create a new object.
	 */
	public ApplicationManagerProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param descriptor see info.pppc.pcom.system.application.IApplicationManager
	 * @return seeinfo.pppc.pcom.system.application.IApplicationManager
	 * @throws java.lang.IllegalArgumentException see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.ObjectID addApplication(info.pppc.pcom.system.application.ApplicationDescriptor descriptor) throws java.lang.IllegalArgumentException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = descriptor;
		String __method = "info.pppc.base.system.ObjectID addApplication(info.pppc.pcom.system.application.ApplicationDescriptor)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof java.lang.IllegalArgumentException) {
				throw (java.lang.IllegalArgumentException)__result.getException();
			}
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.base.system.ObjectID)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param descriptor see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult addApplicationDef(info.pppc.pcom.system.application.ApplicationDescriptor descriptor)  {
		Object[] __args = new Object[1];
		__args[0] = descriptor;
		String __method = "info.pppc.base.system.ObjectID addApplication(info.pppc.pcom.system.application.ApplicationDescriptor)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void loadApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void loadApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult loadApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void loadApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void loadApplicationAsync(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void loadApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void startApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void startApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult startApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void startApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void startApplicationAsync(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void startApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void removeApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void removeApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult removeApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void removeApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void removeApplicationAsync(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void removeApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void exitApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void exitApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult exitApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void exitApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void exitApplicationAsync(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void exitApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return seeinfo.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.pcom.system.application.ApplicationDescriptor queryApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "info.pppc.pcom.system.application.ApplicationDescriptor queryApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.application.ApplicationDescriptor)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult queryApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "info.pppc.pcom.system.application.ApplicationDescriptor queryApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param descriptor see info.pppc.pcom.system.application.IApplicationManager
	 * @throws java.lang.IllegalArgumentException see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void updateAppliction(info.pppc.pcom.system.application.ApplicationDescriptor descriptor) throws java.lang.IllegalArgumentException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = descriptor;
		String __method = "void updateAppliction(info.pppc.pcom.system.application.ApplicationDescriptor)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof java.lang.IllegalArgumentException) {
				throw (java.lang.IllegalArgumentException)__result.getException();
			}
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
	 * @param descriptor see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult updateApplictionDef(info.pppc.pcom.system.application.ApplicationDescriptor descriptor)  {
		Object[] __args = new Object[1];
		__args[0] = descriptor;
		String __method = "void updateAppliction(info.pppc.pcom.system.application.ApplicationDescriptor)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void changeApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void changeApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult changeApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void changeApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void changeApplicationAsync(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void changeApplication(info.pppc.base.system.ObjectID)";
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
	 * @return seeinfo.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public java.util.Vector getApplications() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector getApplications()";
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
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult getApplicationsDef()  {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector getApplications()";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void saveApplication(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void saveApplication(info.pppc.base.system.ObjectID)";
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
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public info.pppc.base.system.FutureResult saveApplicationDef(info.pppc.base.system.ObjectID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void saveApplication(info.pppc.base.system.ObjectID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.application.IApplicationManager
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.application.IApplicationManager
	 * @see info.pppc.pcom.system.application.IApplicationManager
	 */
	public void saveApplicationAsync(info.pppc.base.system.ObjectID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void saveApplication(info.pppc.base.system.ObjectID)";
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
