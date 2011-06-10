package info.pppc.pcom.component.powerpoint.forwarder;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class ConverterProxy extends info.pppc.pcom.system.container.internal.component.InstanceProxy implements info.pppc.pcom.component.powerpoint.IConverter {
	
	/**
	 * Default constructor to create a new object.
	 */
	public ConverterProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @return seeinfo.pppc.pcom.component.powerpoint.IConverter
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.powerpoint.IConverter
	 * @see info.pppc.pcom.component.powerpoint.IConverter
	 */
	public boolean isOpen() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "boolean isOpen()";
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
	 * @return seeinfo.pppc.pcom.component.powerpoint.IConverter
	 * @throws java.lang.IllegalStateException see info.pppc.pcom.component.powerpoint.IConverter
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.powerpoint.IConverter
	 * @see info.pppc.pcom.component.powerpoint.IConverter
	 */
	public int getSlides() throws java.lang.IllegalStateException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "int getSlides()";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof java.lang.IllegalStateException) {
				throw (java.lang.IllegalStateException)__result.getException();
			}
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return ((Integer)__result.getValue()).intValue();
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param slide see info.pppc.pcom.component.powerpoint.IConverter
	 * @param width see info.pppc.pcom.component.powerpoint.IConverter
	 * @param height see info.pppc.pcom.component.powerpoint.IConverter
	 * @return seeinfo.pppc.pcom.component.powerpoint.IConverter
	 * @throws java.lang.IllegalArgumentException see info.pppc.pcom.component.powerpoint.IConverter
	 * @throws java.lang.IllegalStateException see info.pppc.pcom.component.powerpoint.IConverter
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.powerpoint.IConverter
	 * @see info.pppc.pcom.component.powerpoint.IConverter
	 */
	public byte[] getSlide(int slide, int width, int height) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[3];
		__args[0] = new Integer(slide);
		__args[1] = new Integer(width);
		__args[2] = new Integer(height);
		String __method = "byte[] getSlide(int, int, int)";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof java.lang.IllegalArgumentException) {
				throw (java.lang.IllegalArgumentException)__result.getException();
			}
			if (__result.getException() instanceof java.lang.IllegalStateException) {
				throw (java.lang.IllegalStateException)__result.getException();
			}
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (byte[])__result.getValue();
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.powerpoint.IConverter
	 * @see info.pppc.pcom.component.powerpoint.IConverter
	 */
	public void close() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "void close()";
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
	 * @param file see info.pppc.pcom.component.powerpoint.IConverter
	 * @throws java.lang.IllegalArgumentException see info.pppc.pcom.component.powerpoint.IConverter
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.powerpoint.IConverter
	 * @see info.pppc.pcom.component.powerpoint.IConverter
	 */
	public void open(byte[] file) throws java.lang.IllegalArgumentException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = file;
		String __method = "void open(byte[])";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
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
	
}
