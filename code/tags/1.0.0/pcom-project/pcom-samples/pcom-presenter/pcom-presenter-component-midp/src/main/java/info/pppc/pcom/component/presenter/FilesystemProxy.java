package info.pppc.pcom.component.presenter;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class FilesystemProxy extends info.pppc.pcom.system.container.internal.component.InstanceProxy implements info.pppc.pcom.component.filesystem.IFilesystem {
	
	/**
	 * Default constructor to create a new object.
	 */
	public FilesystemProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param file see info.pppc.pcom.component.filesystem.IFilesystem
	 * @return seeinfo.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.pcom.component.filesystem.FileException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @see info.pppc.pcom.component.filesystem.IFilesystem
	 */
	public info.pppc.pcom.component.filesystem.File getParent(info.pppc.pcom.component.filesystem.File file) throws info.pppc.pcom.component.filesystem.FileException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = file;
		String __method = "info.pppc.pcom.component.filesystem.File getParent(info.pppc.pcom.component.filesystem.File)";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.pcom.component.filesystem.FileException) {
				throw (info.pppc.pcom.component.filesystem.FileException)__result.getException();
			}
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (info.pppc.pcom.component.filesystem.File)__result.getValue();
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @return seeinfo.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @see info.pppc.pcom.component.filesystem.IFilesystem
	 */
	public java.util.Vector listRoots() throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[0];
		String __method = "java.util.Vector listRoots()";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Vector)__result.getValue();
	}
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param file see info.pppc.pcom.component.filesystem.IFilesystem
	 * @return seeinfo.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.pcom.component.filesystem.FileException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @see info.pppc.pcom.component.filesystem.IFilesystem
	 */
	public byte[] getFile(info.pppc.pcom.component.filesystem.File file) throws info.pppc.pcom.component.filesystem.FileException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = file;
		String __method = "byte[] getFile(info.pppc.pcom.component.filesystem.File)";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.pcom.component.filesystem.FileException) {
				throw (info.pppc.pcom.component.filesystem.FileException)__result.getException();
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
	 * @param directory see info.pppc.pcom.component.filesystem.IFilesystem
	 * @return seeinfo.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.pcom.component.filesystem.FileException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @throws info.pppc.base.system.InvocationException see info.pppc.pcom.component.filesystem.IFilesystem
	 * @see info.pppc.pcom.component.filesystem.IFilesystem
	 */
	public java.util.Vector listFiles(info.pppc.pcom.component.filesystem.File directory) throws info.pppc.pcom.component.filesystem.FileException, info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = directory;
		String __method = "java.util.Vector listFiles(info.pppc.pcom.component.filesystem.File)";
		info.pppc.base.system.Invocation __invocation = __create(__method, __args);
		info.pppc.base.system.Result __result = __invoke(__invocation);
		if (__result.hasException()) {
			if (__result.getException() instanceof info.pppc.pcom.component.filesystem.FileException) {
				throw (info.pppc.pcom.component.filesystem.FileException)__result.getException();
			}
			if (__result.getException() instanceof info.pppc.base.system.InvocationException) {
				throw (info.pppc.base.system.InvocationException)__result.getException();
			}
			throw (RuntimeException)__result.getException();
		}
		return (java.util.Vector)__result.getValue();
	}
	
}
