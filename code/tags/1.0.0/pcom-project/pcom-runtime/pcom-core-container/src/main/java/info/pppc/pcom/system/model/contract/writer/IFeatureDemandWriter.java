package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;

/**
 * The feature demand describes a single demanded feature. The  feature demand 
 * reader provides read-write access to the feature. The feature itself is
 * immutable, however using the remove method, it can be removed.
 * 
 * @author Mac
 */
public interface IFeatureDemandWriter extends IContractOption {
	
	/**
	 * Returns the immutable comparator of the feature. If a range
	 * comparator is specified, the minimum and maximum values must
	 * be set. Otherwise, the value must be set.
	 * 
	 * @return The comparator of the feature.
	 */
	public int getComparator();
	
	/**
	 * Returns the value of the feature. The value will be of the
	 * type that is specified by the type constant. This value
	 * will be null if a range comparator is used.
	 * 
	 * @return The value of the feature or null if a range 
	 * 	comparator is used.
	 */
	public Object getValue();
		
	/**
	 * Returns the minimum value of the feature. The minimum will
	 * have the type specified by the type constant. If a simple
	 * comparator is used, the minimum will be null.
	 * 
	 * @return The minimum value of the feature.
	 */
	public Object getMinimum();
	
	/**
	 * Returns the maximum value of the feature. The maximum will
	 * have the type specified by the type constant. If a simple
	 * comparator is used, the maximum will be null.
	 * 
	 * @return The maximum value of the feature.
	 */
	public Object getMaximum();
	
}
