package info.pppc.pcom.system.assembler;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class AssemblerProxy extends info.pppc.base.system.Proxy implements info.pppc.pcom.system.assembler.IAssembler {
	
	/**
	 * Default constructor to create a new object.
	 */
	public AssemblerProxy() { }
	
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
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param context see info.pppc.pcom.system.assembler.IAssembler
	 * @param setup see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.pcom.system.assembler.AssemblyPointer context, info.pppc.pcom.system.assembler.AssemblyState setup) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = context;
		__args[1] = setup;
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
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param applicationID see info.pppc.pcom.system.assembler.IAssembler
	 * @param setup see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.base.system.ReferenceID applicationID, info.pppc.pcom.system.assembler.AssemblyState setup) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[2];
		__args[0] = applicationID;
		__args[1] = setup;
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
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param context see info.pppc.pcom.system.assembler.IAssembler
	 * @return seeinfo.pppc.pcom.system.assembler.IAssembler
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.system.assembler.IAssembler
	 * @see info.pppc.pcom.system.assembler.IAssembler
	 */
	public info.pppc.pcom.system.assembler.Assembly retrieve(info.pppc.pcom.system.assembler.AssemblyPointer context) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = context;
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
	
}
