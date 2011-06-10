package info.pppc.pcom.tutorial.tree;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class TreeProxy extends info.pppc.pcom.system.container.internal.component.InstanceProxy implements info.pppc.pcom.tutorial.tree.ITree {
	
	/**
	 * Default constructor to create a new object.
	 */
	public TreeProxy() { }
	
	/**
	 * Proxy method that creates and transfers an invocation for the interface method.
	 *
	 * @param s see info.pppc.tutorial.pcom.tree.ITree
	 * @throws info.pppc.base.system.InvocationException see info.pppc.tutorial.pcom.tree.ITree
	 * @see info.pppc.pcom.tutorial.tree.ITree
	 */
	public void println(java.lang.String s) throws info.pppc.base.system.InvocationException {
		Object[] __args = new Object[1];
		__args[0] = s;
		String __method = "void println(java.lang.String)";
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
