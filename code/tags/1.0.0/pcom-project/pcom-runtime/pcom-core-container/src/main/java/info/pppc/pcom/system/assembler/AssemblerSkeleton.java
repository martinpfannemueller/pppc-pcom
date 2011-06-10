package info.pppc.pcom.system.assembler;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class AssemblerSkeleton extends info.pppc.base.system.Skeleton  {
	
	/**
	 * Default constructor to create a new object.
	 */
	public AssemblerSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.system.assembler.IAssembler impl = (info.pppc.pcom.system.assembler.IAssembler)getImplementation();
		try {
			if (method.equals("info.pppc.base.lease.Lease prepare(info.pppc.base.system.ReferenceID)")) {
				Object result = impl.prepare((info.pppc.base.system.ReferenceID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.pcom.system.assembler.AssemblyPointer, info.pppc.pcom.system.assembler.AssemblyState)")) {
				Object result = impl.setup((info.pppc.pcom.system.assembler.AssemblyPointer)args[0], (info.pppc.pcom.system.assembler.AssemblyState)args[1]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void remove(info.pppc.base.system.ReferenceID)")) {
				Object result = null;
				impl.remove((info.pppc.base.system.ReferenceID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.assembler.AssemblyPointer setup(info.pppc.base.system.ReferenceID, info.pppc.pcom.system.assembler.AssemblyState)")) {
				Object result = impl.setup((info.pppc.base.system.ReferenceID)args[0], (info.pppc.pcom.system.assembler.AssemblyState)args[1]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.assembler.Assembly retrieve(info.pppc.pcom.system.assembler.AssemblyPointer)")) {
				Object result = impl.retrieve((info.pppc.pcom.system.assembler.AssemblyPointer)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.assembler.Assembly configure(info.pppc.base.system.ReferenceID)")) {
				Object result = impl.configure((info.pppc.base.system.ReferenceID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
