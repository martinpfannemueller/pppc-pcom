package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;

/**
 * The feature provision writer describes a single provided feature. 
 * This interface provides read-write access to features. 
 * 
 * @author Mac
 */
public interface IFeatureProvisionWriter extends IContractOption {

	/**
	 * Returns the value of the feature. The type of the value will be
	 * the one specified by the type constant of the feature.
	 * 
	 * @return The value of the feature.
	 */
	public Object getValue();
	
	/**
	 * Determines whether the feature provision is dynamic. A
	 * dynamic feature provision does not have to have a value.
	 * 
	 * @return True if the feature provision is dynamic.
	 */
	public boolean isDynamic();
}
