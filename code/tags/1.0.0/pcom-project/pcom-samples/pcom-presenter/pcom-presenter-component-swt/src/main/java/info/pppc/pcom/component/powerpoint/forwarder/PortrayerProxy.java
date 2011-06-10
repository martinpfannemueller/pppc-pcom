package info.pppc.pcom.component.powerpoint.forwarder;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class PortrayerProxy extends info.pppc.pcom.system.container.internal.component.InstanceProxy implements info.pppc.pcom.component.portrayer.IPortrayer {
	
	/**
	 * Default constructor to create a new object.
	 */
	public PortrayerProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @return seeinfo.pppc.pcom.component.portrayer.IPortrayer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.portrayer.IPortrayer
	 * @see info.pppc.pcom.component.portrayer.IPortrayer
	 */
	public boolean isVisible() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "boolean isVisible()";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ((Boolean)__result.getValue()).booleanValue();
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.portrayer.IPortrayer
	 * @see info.pppc.pcom.component.portrayer.IPortrayer
	 */
	public void hidePicture() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "void hidePicture()";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
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
	 * @param picture see info.pppc.pcom.component.portrayer.IPortrayer
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.portrayer.IPortrayer
	 * @see info.pppc.pcom.component.portrayer.IPortrayer
	 */
	public void showPicture(byte[] picture) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = picture;
		String __method = "void showPicture(byte[])";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ;
	}
	
}
