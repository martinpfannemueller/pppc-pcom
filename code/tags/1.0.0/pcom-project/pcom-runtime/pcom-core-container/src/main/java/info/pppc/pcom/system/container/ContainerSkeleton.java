package info.pppc.pcom.system.container;

/**
 * Do not modify this file. This class has been generated.
 * Use inheritance or composition to add functionality.
 *
 * @author 3PC Base Tools
 */
public class ContainerSkeleton extends info.pppc.base.system.Skeleton  {
	
	/**
	 * Default constructor to create a new object.
	 */
	public ContainerSkeleton() { }
	
	/**
	 * Dispatch method that dispatches incoming invocations to the skeleton's implementation.
	 *
	 * @param method The signature of the method to call.
	 * @param args The parameters of the method call.
	 * @return The result of the method call.
	 */
	protected info.pppc.base.system.Result dispatch(String method, Object[] args) {
		info.pppc.pcom.system.container.IContainer impl = (info.pppc.pcom.system.container.IContainer)getImplementation();
		try {
			if (method.equals("java.util.Vector getResourcesUI(info.pppc.base.system.ObjectID)")) {
				Object result = impl.getResourcesUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Hashtable getTemplates(java.util.Vector)")) {
				Object result = impl.getTemplates((java.util.Vector)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.container.InstanceCheckpoint storeInstance(info.pppc.base.system.ObjectID)")) {
				Object result = impl.storeInstance((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void saveApplication(info.pppc.base.system.ObjectID, java.lang.String, int)")) {
				Object result = null;
				impl.saveApplication((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Vector getInstancesUI(info.pppc.base.system.ObjectID)")) {
				Object result = impl.getInstancesUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void changeInstance(info.pppc.base.system.ObjectID, java.lang.String, int, info.pppc.pcom.system.contract.Contract)")) {
				Object result = null;
				impl.changeInstance((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue(), (info.pppc.pcom.system.contract.Contract)args[3]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void stopInstance(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.stopInstance((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Hashtable getResources()")) {
				Object result = impl.getResources();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Vector getFactoriesUI()")) {
				Object result = impl.getFactoriesUI();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void pauseInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.assembler.AssemblyPointer, int)")) {
				Object result = null;
				impl.pauseInstance((info.pppc.base.system.ObjectID)args[0], (info.pppc.pcom.system.assembler.AssemblyPointer)args[1], ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("java.util.Vector getAllocatorsUI()")) {
				Object result = impl.getAllocatorsUI();
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void changeInstanceUI(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.changeInstanceUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void commitInstanceUI(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.commitInstanceUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void commitResourceUI(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.commitResourceUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void changeResourceUI(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.changeResourceUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("info.pppc.pcom.system.container.InstanceState startInstance(info.pppc.pcom.system.container.InstanceSetup, int)")) {
				Object result = impl.startInstance((info.pppc.pcom.system.container.InstanceSetup)args[0], ((Integer)args[1]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void stopInstanceUI(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.stopInstanceUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void stopResourceUI(info.pppc.base.system.ObjectID)")) {
				Object result = null;
				impl.stopResourceUI((info.pppc.base.system.ObjectID)args[0]);
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void exitApplication(info.pppc.base.system.ObjectID, java.lang.String, int)")) {
				Object result = null;
				impl.exitApplication((info.pppc.base.system.ObjectID)args[0], (java.lang.String)args[1], ((Integer)args[2]).intValue());
				return new info.pppc.base.system.Result(result, null);
			}
			else if (method.equals("void loadInstance(info.pppc.base.system.ObjectID, info.pppc.pcom.system.container.InstanceCheckpoint)")) {
				Object result = null;
				impl.loadInstance((info.pppc.base.system.ObjectID)args[0], (info.pppc.pcom.system.container.InstanceCheckpoint)args[1]);
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
