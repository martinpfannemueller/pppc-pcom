package info.pppc.pcom.component.powerpoint.displayer;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class DisplayerSkeleton extends info.pppc.pcom.system.container.internal.component.InstanceSkeleton {
	
	/**
	 * Default constructor to create a new object.
	 */
	public DisplayerSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.component.powerpoint.displayer.DisplayerInstance impl = (info.pppc.pcom.component.powerpoint.displayer.DisplayerInstance)getTarget();
		try {
			if (method.equals("boolean isOpen()")) {
				Object result = new Boolean(impl.isOpen());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void nextSlide()")) {
				Object result = null;
				impl.nextSlide();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("int getSlides()")) {
				Object result = new Integer(impl.getSlides());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("byte[] getSlide(int, int, int)")) {
				Object result = impl.getSlide(((Integer)args[0]).intValue(), ((Integer)args[1]).intValue(), ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void previousSlide()")) {
				Object result = null;
				impl.previousSlide();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("int getSlide()")) {
				Object result = new Integer(impl.getSlide());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void close()")) {
				Object result = null;
				impl.close();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void open(byte[])")) {
				Object result = null;
				impl.open((byte[])args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void firstSlide()")) {
				Object result = null;
				impl.firstSlide();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void lastSlide()")) {
				Object result = null;
				impl.lastSlide();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void setSlide(int)")) {
				Object result = null;
				impl.setSlide(((Integer)args[0]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
