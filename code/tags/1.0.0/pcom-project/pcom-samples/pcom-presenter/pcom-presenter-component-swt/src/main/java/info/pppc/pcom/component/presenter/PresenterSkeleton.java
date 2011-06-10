package info.pppc.pcom.component.presenter;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class PresenterSkeleton extends info.pppc.pcom.system.container.internal.component.InstanceSkeleton {
	
	/**
	 * Default constructor to create a new object.
	 */
	public PresenterSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		try {
			return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
