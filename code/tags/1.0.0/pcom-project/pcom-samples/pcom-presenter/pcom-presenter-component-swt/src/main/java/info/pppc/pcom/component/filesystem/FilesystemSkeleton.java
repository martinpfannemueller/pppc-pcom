package info.pppc.pcom.component.filesystem;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class FilesystemSkeleton extends info.pppc.pcom.system.container.internal.component.InstanceSkeleton {
	
	/**
	 * Default constructor to create a new object.
	 */
	public FilesystemSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.component.filesystem.FilesystemInstance impl = (info.pppc.pcom.component.filesystem.FilesystemInstance)getTarget();
		try {
			if (method.equals("info.pppc.pcom.component.filesystem.File getParent(info.pppc.pcom.component.filesystem.File)")) {
				Object result = impl.getParent((info.pppc.pcom.component.filesystem.File)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Vector listRoots()")) {
				Object result = impl.listRoots();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("byte[] getFile(info.pppc.pcom.component.filesystem.File)")) {
				Object result = impl.getFile((info.pppc.pcom.component.filesystem.File)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Vector listFiles(info.pppc.pcom.component.filesystem.File)")) {
				Object result = impl.listFiles((info.pppc.pcom.component.filesystem.File)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
