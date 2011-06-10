package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;

/**
 * The dimension demand writer provides a read-write access to demanded
 * dimensions. Conceptionally, it extends the dimension demand which provides
 * read-only access. However, it does not reflect this extension in its type
 * as this would lead to multiple cast operations. The dimension contains
 * features to describe non-functional properties. 
 * 
 * @author Mac
 */
public interface IDimensionDemandWriter extends IContractOption {
	
	/**
	 * Returns the features specified by this dimension. The returned 
	 * features are returned as writers.
	 * 
	 * @return The features specified by the dimension.
	 */
	public IFeatureDemandWriter[] getFeatures();
	
	/**
	 * Returns the feature with the specified name that is contained in
	 * this dimension or null if no such feature exists.
	 * 
	 * @param name The name of the feature to retrieve.
	 * @return The feature specified by the dimension or null.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IFeatureDemandWriter getFeature(String name);
	
	/**
	 * Creates a new feature with the specified name, comparator and value. The
	 * comparator must be a primitive comparator (i.e., not a range comparator).
	 * The type of the value must be one of the supported types (i.e., string,
	 * boolean, int or long). If a feature with the specified name already
	 * exists, it will be replaced and the replaced feature will be returned.
	 * 
	 * @param name The name of the feature demand to create.
	 * @param comparator A simple comparator (more, less, more or equal, less or
	 * 	equal, equal)
	 * @param value A non-null value of one of the supported types.
	 * @return Null if the feature was not present already or the feature that
	 * 	has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 * @throws IllegalArgumentException Thrown if the combination of arguments is 
	 * 	not legal.
	 */
	public IFeatureDemandWriter createFeature(String name, int comparator, Object value);
	
	/**
	 * Creates a new range feature with the specified name, comparator and minimum
	 * and maximum value. The comparator must be one of the range comparators. The
	 * minimum and maximum must be of the same type and the type must be one of
	 * the supported types (i.e., string, boolean, int or long). If a feature 
	 * with the same name is already present, it will be replaced and returned by
	 * the method.
	 * 
	 * @param name The name of the feature demand to create.
	 * @param comparator The comparator of the feature. This must be a range 
	 * 	comparator.
	 * @param min The minimum value of the feature demand.
	 * @param max The maximum value of the feature demand.
	 * @return Null if the feature was not present already or the feature that
	 * 	has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 * @throws IllegalArgumentException Thrown if the combination of arguments is 
	 * 	not legal.
	 */
	public IFeatureDemandWriter createFeature(String name, int comparator, Object min, Object max);
	
}
