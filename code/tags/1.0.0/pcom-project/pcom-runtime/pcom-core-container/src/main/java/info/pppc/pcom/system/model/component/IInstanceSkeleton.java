package info.pppc.pcom.system.model.component;

import info.pppc.base.system.IInvocationHandler;

/**
 * The instance skeleton defines the skeleton interface for all pcom component
 * instances. Factories must be able to create skeletons for their instances
 * that adhere to this interface. Typically, these skeletons are not hand-crafted
 * but they are generated through the pcom component generator.
 * 
 * @author Mac
 */
public interface IInstanceSkeleton extends IInvocationHandler {

	/**
	 * Sets the target instance that will be used to dispatch incoming
	 * invocations. 
	 * 
	 * @param instance The instance that will be used as dispatch target
	 * 	of the skeleton.
	 */
	public void setTarget(IInstance instance);
	
}
