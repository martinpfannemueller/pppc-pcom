package info.pppc.pcomx.assembler.gd;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class GDAssemblerProxy extends info.pppc.base.system.Proxy implements info.pppc.pcomx.assembler.gd.IGDAssembler {
	
	/**
	 * Default constructor to create a new object.
	 */
	public GDAssemblerProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param pointer see info.pppc.pcom.system.assembler.IAssembler
	 * @param state see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.pcom.system.assembler.AssemblyPointer pointer, info.pppc.pcom.system.assembler.AssemblyState state) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = pointer;
		__args[1] = state;
		String __method = "info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.pcom.system.assembler.AssemblyPointer, info.pppc.pcom.system.assembler.AssemblyState)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.assembler.AssemblyPointer)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param pointer see info.pppc.pcom.system.assembler.IAssembler
	 * @param state see info.pppc.pcom.system.assembler.IAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.system.FutureResult setupDef(info.pppc.pcom.system.assembler.AssemblyPointer pointer, info.pppc.pcom.system.assembler.AssemblyState state)  {
		Object[] __args = new Object[2];
		__args[0] = pointer;
		__args[1] = state;
		String __method = "info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.pcom.system.assembler.AssemblyPointer, info.pppc.pcom.system.assembler.AssemblyState)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.base.lease.Lease)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.system.FutureResult prepareDef(info.pppc.base.system.ReferenceID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void release(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		String __method = "void release(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer)";
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
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public info.pppc.base.system.FutureResult releaseDef(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer)  {
		Object[] __args = new Object[3];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		String __method = "void release(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void releaseAsync(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		String __method = "void release(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer)";
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
	 * @param pointer see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.Assembly retrieve(info.pppc.pcom.system.assembler.AssemblyPointer pointer) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = pointer;
		String __method = "info.pppc.pcom.system.assembler.Assembly retrieve(info.pppc.pcom.system.assembler.AssemblyPointer)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.assembler.Assembly)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param pointer see info.pppc.pcom.system.assembler.IAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.system.FutureResult retrieveDef(info.pppc.pcom.system.assembler.AssemblyPointer pointer)  {
		Object[] __args = new Object[1];
		__args[0] = pointer;
		String __method = "info.pppc.pcom.system.assembler.Assembly retrieve(info.pppc.pcom.system.assembler.AssemblyPointer)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param master see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param systems see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @return seeinfo.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID applicationID, info.pppc.base.system.SystemID master, java.util.Vector systems) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = applicationID;
		__args[1] = master;
		__args[2] = systems;
		String __method = "info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID, info.pppc.base.system.SystemID, java.util.Vector)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.base.lease.Lease)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param master see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param systems see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public info.pppc.base.system.FutureResult prepareDef(info.pppc.base.system.ReferenceID applicationID, info.pppc.base.system.SystemID master, java.util.Vector systems)  {
		Object[] __args = new Object[3];
		__args[0] = applicationID;
		__args[1] = master;
		__args[2] = systems;
		String __method = "info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID, info.pppc.base.system.SystemID, java.util.Vector)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public void remove(info.pppc.base.system.ReferenceID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void remove(info.pppc.base.system.ReferenceID)";
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
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.system.FutureResult removeDef(info.pppc.base.system.ReferenceID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void remove(info.pppc.base.system.ReferenceID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public void removeAsync(info.pppc.base.system.ReferenceID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "void remove(info.pppc.base.system.ReferenceID)";
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
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.Assembly configure(info.pppc.base.system.ReferenceID applicationID) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "info.pppc.pcom.system.assembler.Assembly configure(info.pppc.base.system.ReferenceID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.assembler.Assembly)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.system.FutureResult configureDef(info.pppc.base.system.ReferenceID applicationID)  {
		Object[] __args = new Object[1];
		__args[0] = applicationID;
		String __method = "info.pppc.pcom.system.assembler.Assembly configure(info.pppc.base.system.ReferenceID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param system see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void remove(info.pppc.base.system.ReferenceID applicationID, info.pppc.base.system.SystemID system) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = applicationID;
		__args[1] = system;
		String __method = "void remove(info.pppc.base.system.ReferenceID, info.pppc.base.system.SystemID)";
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
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param system see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public info.pppc.base.system.FutureResult removeDef(info.pppc.base.system.ReferenceID applicationID, info.pppc.base.system.SystemID system)  {
		Object[] __args = new Object[2];
		__args[0] = applicationID;
		__args[1] = system;
		String __method = "void remove(info.pppc.base.system.ReferenceID, info.pppc.base.system.SystemID)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param system see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void removeAsync(info.pppc.base.system.ReferenceID applicationID, info.pppc.base.system.SystemID system) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = applicationID;
		__args[1] = system;
		String __method = "void remove(info.pppc.base.system.ReferenceID, info.pppc.base.system.SystemID)";
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
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @param state see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.base.system.ReferenceID applicationID, info.pppc.pcom.system.assembler.AssemblyState state) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = applicationID;
		__args[1] = state;
		String __method = "info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.base.system.ReferenceID, info.pppc.pcom.system.assembler.AssemblyState)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		info.pppc.base.system.Result __result = proxyInvokeSynchronous(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.system.assembler.AssemblyPointer)__result.getValue();
	}
	/**
	 * Proxy method that creates and transfers a deferred synchronous invocation.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @param state see info.pppc.pcom.system.assembler.IAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.base.system.FutureResult setupDef(info.pppc.base.system.ReferenceID applicationID, info.pppc.pcom.system.assembler.AssemblyState state)  {
		Object[] __args = new Object[2];
		__args[0] = applicationID;
		__args[1] = state;
		String __method = "info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.base.system.ReferenceID, info.pppc.pcom.system.assembler.AssemblyState)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param system see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param contract see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param reuse see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void resolve(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer, info.pppc.base.system.SystemID system, info.pppc.pcom.system.contract.Contract contract, boolean reuse) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[6];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		__args[3] = system;
		__args[4] = contract;
		__args[5] = new Boolean(reuse);
		String __method = "void resolve(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer, info.pppc.base.system.SystemID, info.pppc.pcom.system.contract.Contract, boolean)";
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
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param system see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param contract see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param reuse see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public info.pppc.base.system.FutureResult resolveDef(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer, info.pppc.base.system.SystemID system, info.pppc.pcom.system.contract.Contract contract, boolean reuse)  {
		Object[] __args = new Object[6];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		__args[3] = system;
		__args[4] = contract;
		__args[5] = new Boolean(reuse);
		String __method = "void resolve(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer, info.pppc.base.system.SystemID, info.pppc.pcom.system.contract.Contract, boolean)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param system see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param contract see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param reuse see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void resolveAsync(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer, info.pppc.base.system.SystemID system, info.pppc.pcom.system.contract.Contract contract, boolean reuse) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[6];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		__args[3] = system;
		__args[4] = contract;
		__args[5] = new Boolean(reuse);
		String __method = "void resolve(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer, info.pppc.base.system.SystemID, info.pppc.pcom.system.contract.Contract, boolean)";
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
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param assembly see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void report(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer, info.pppc.pcom.system.assembler.Assembly assembly) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[4];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		__args[3] = assembly;
		String __method = "void report(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer, info.pppc.pcom.system.assembler.Assembly)";
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
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param assembly see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @return A future result that delivers the return value and exceptions. * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public info.pppc.base.system.FutureResult reportDef(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer, info.pppc.pcom.system.assembler.Assembly assembly)  {
		Object[] __args = new Object[4];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		__args[3] = assembly;
		String __method = "void report(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer, info.pppc.pcom.system.assembler.Assembly)";
		info.pppc.base.system.Invocation __invocation = proxyCreateSynchronous(__method, __args);
		return proxyInvokeDeferred(__invocation);
	}
	/**
	 * Proxy method that creates and transfers an asynchronous call.
	 *
	 * @param applicationID see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param phase see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param pointer see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @param assembly see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcomx.assembler.gd.IGDAssembler
	 * @see info.pppc.pcomx.assembler.gd.IGDAssembler
	 */
	public void reportAsync(info.pppc.base.system.ReferenceID applicationID, int phase, info.pppc.pcomx.assembler.gd.internal.Pointer pointer, info.pppc.pcom.system.assembler.Assembly assembly) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[4];
		__args[0] = applicationID;
		__args[1] = new Integer(phase);
		__args[2] = pointer;
		__args[3] = assembly;
		String __method = "void report(info.pppc.base.system.ReferenceID, int, info.pppc.pcomx.assembler.gd.internal.Pointer, info.pppc.pcom.system.assembler.Assembly)";
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
