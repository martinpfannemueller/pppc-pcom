package info.pppc.pcom.component.powerpoint.converter;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Pcom Tools
 */
public class ConverterSkeleton extends info.pppc.pcom.system.container.internal.component.InstanceSkeleton {
	
	/**
	 * Default constructor to create a new object.
	 */
	public ConverterSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.component.powerpoint.converter.ConverterInstance impl = (info.pppc.pcom.component.powerpoint.converter.ConverterInstance)getTarget();
		try {
			if (method.equals("boolean isOpen()")) {
				Object result = new Boolean(impl.isOpen());
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
			else if (method.equals("void close()")) {
				Object result = null;
				impl.close();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void open(byte[])")) {
				Object result = null;
				impl.open((byte[])args[0]);
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
