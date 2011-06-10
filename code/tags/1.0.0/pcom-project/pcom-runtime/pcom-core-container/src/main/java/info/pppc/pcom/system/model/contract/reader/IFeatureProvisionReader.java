package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The feature provision reader is used to provide read-only access to
 * features. Features model the concrete values of dimensions.
 * In contrast to feature demands, provisions only have a single
 * value.
 * 
 * @author Mac
 */
public interface IFeatureProvisionReader extends IContract {
	
	/**
	 * Returns the value of the feature provision. The value will
	 * always correspond to the type specified by the type field.
	 * For primitve types, the return value of this method will
	 * return the corresponding non-primitve wrapper type.
	 * 
	 * @return The value of the feature with the type specified
	 *  by the type field, this will never be null.
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
