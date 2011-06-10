package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The feature demand reader is used to provide read-only access to a 
 * demanded feature. Depending on the comparator the feature demand either
 * contains a value (for simple comparators) or a minimum and a maximum 
 * (for range comparators). The types of the value, minimum and maximum 
 * always adhere to the specified type that can be retrieved by
 * the get type method.
 * 
 * @author Mac
 */
public interface IFeatureDemandReader extends IContract {
	
	/**
	 * Returns the comparator of the feature. This must be one of
	 * the feature comparators defined by th feature constants 
	 * interface. If the comparator denotes a range comparator, 
	 * the minimum and maximum values of the feature demand 
	 * must be set, otherwise the value must be set.
	 * 
	 * @return The comparator of the feature.
	 */
	public int getComparator();
	
	/**
	 * The value of the feature used for features that do not have
	 * range comparators. The type of the feature value depends on
	 * the type field. All primitive types will be wrapped with their
	 * corresponding non-primitive type.
	 * 
	 * @return The feature value or null if the comparator is a 
	 * 	range comparator.
	 */
	public Object getValue();
	
	/**
	 * The minimum value of the feature used for features that use
	 * range comparators. The type of the feature value depends on
	 * the type field. All primitive types will be wrapped with their
	 * corresponding non-primitive type.
	 * 
	 * @return The minimum value or null if the comparator is not a
	 * 	range comparator.
	 */
	public Object getMinimum();
	
	/**
	 * The maximum value of the feature used for features that use 
	 * range comparators. The type of the feature value depends on
	 * the type field. All primitive types will be wrapped with their
	 * corresponding non-primitive type.
	 * 
	 * @return The maximum value or null if the comparator is not a
	 * 	range comparator.
	 */
	public Object getMaximum();

}
