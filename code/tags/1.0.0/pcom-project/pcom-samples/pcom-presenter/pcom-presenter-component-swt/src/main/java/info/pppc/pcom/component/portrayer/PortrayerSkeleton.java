package info.pppc.pcom.component.portrayer;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class PortrayerSkeleton extends info.pppc.pcom.system.container.internal.component.InstanceSkeleton {
	
	/**
	 * Default constructor to create a new object.
	 */
	public PortrayerSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.component.portrayer.PortrayerInstance impl = (info.pppc.pcom.component.portrayer.PortrayerInstance)getTarget();
		try {
			if (method.equals("boolean isVisible()")) {
				Object result = new Boolean(impl.isVisible());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void hidePicture()")) {
				Object result = null;
				impl.hidePicture();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void showPicture(byte[])")) {
				Object result = null;
				impl.showPicture((byte[])args[0]);
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
