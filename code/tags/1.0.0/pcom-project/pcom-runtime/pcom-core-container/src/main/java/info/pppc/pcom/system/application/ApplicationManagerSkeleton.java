package info.pppc.pcom.system.application;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class ApplicationManagerSkeleton extends info.pppc.base.system.Skeleton  {
	
	/**
	 * Default constructor to create a new object.
	 */
	public ApplicationManagerSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.system.application.IApplicationManager impl = (info.pppc.pcom.system.application.IApplicationManager)getImplementation();
		try {
			if (method.equals("info.pppc.base.system.ObjectID addApplication(info.pppc.pcom.system.application.ApplicationDescriptor)")) {
				Object result = impl.addApplication((info.pppc.pcom.system.application.ApplicationDescriptor)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void loadApplication(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.loadApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void saveApplication(info.pppc.base.system.ObjectID, java.lang.String, int)")) {
				Object result = null;
				impl.saveApplication((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void changeInstance(info.pppc.base.system.ObjectID, java.lang.String, int, info.pppc.pcom.system.contract.Contract)")) {
				Object result = null;
				impl.changeInstance((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue(), (info.pppc.pcom.system.contract.Contract)args[3]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void startApplication(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.startApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void removeApplication(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.removeApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void exitApplication(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.exitApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.application.ApplicationDescriptor queryApplication(info.pppc.base.system.ObjectID)")) {
				Object result = impl.queryApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void updateAppliction(info.pppc.pcom.system.application.ApplicationDescriptor)")) {
				Object result = null;
				impl.updateAppliction((info.pppc.pcom.system.application.ApplicationDescriptor)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void changeApplication(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.changeApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Vector getApplications()")) {
				Object result = impl.getApplications();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void saveApplication(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.saveApplication((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void exitApplication(info.pppc.base.system.ObjectID, java.lang.String, int)")) {
				Object result = null;
				impl.exitApplication((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void removeInstance(info.pppc.base.system.ObjectID, java.lang.String, int)")) {
				Object result = null;
				impl.removeInstance((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}return new info.pppc.base.system.Result(null, new info.pppc.base.system.InvocationException("Illegal signature."));
		} catch (Throwable t) {
			return new info.pppc.base.system.Result(null, t);
		}
	}
	
}
