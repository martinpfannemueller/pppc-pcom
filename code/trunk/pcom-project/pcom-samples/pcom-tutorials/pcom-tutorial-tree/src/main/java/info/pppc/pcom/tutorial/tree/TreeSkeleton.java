package info.pppc.pcom.tutorial.tree;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class TreeSkeleton extends info.pppc.pcom.system.container.internal.component.InstanceSkeleton {
	
	/**
	 * Default constructor to create a new object.
	 */
	public TreeSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.tutorial.tree.TreeInstance impl = (info.pppc.pcom.tutorial.tree.TreeInstance)getTarget();
		try {
			if (method.equals("void println(java.lang.String)")) {
				Object result = null;
				impl.println((java.lang.String)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
