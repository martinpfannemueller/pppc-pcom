package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;


/**
 * The dimension provision writer provides read-write access to dimension
 * provisions. The dimensions contain features to describe the provision
 * in detail. 
 * 
 * @author Mac
 */
public interface IDimensionProvisionWriter extends IContractOption {
	
	/**
	 * Returns the features provided by the dimension. The features are
	 * returned as mutable features. 
	 * 
	 * @return The features provided by the dimension.
	 */
	public IFeatureProvisionWriter[] getFeatures();
	
	/**
	 * Returns the feature with the specified name provided by the dimension.
	 * The features are returned as mutable features. If no such feature is
	 * present, this method returns null.
	 * 
	 * @param name The name of the feature to retrieve.
	 * @return The feature with the specified name provided by the dimension
	 * 	or null if no such feature exists.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IFeatureProvisionWriter getFeature(String name);
	
	/**
	 * Creates a new provided dynamic feature for the dimension with the specified
	 * name. The name must not be null. The value of the feature will be null
	 * but it will initially match all internal comparisons. Note that a dynamic
	 * feature must be replaced within the start method of the resource or instance
	 * that uses the dynamic feature to cover some demands.
	 * 
	 * @param name The name of the provided feature to create.
	 * @return The feature that has been replaced or null if no such feature
	 * 	was present.
	 * @throws NullPointerException Thrown if the name is null.
	 */	
	public IFeatureProvisionWriter createFeature(String name);
	
	/**
	 * Creates a new provided feature for the dimension with the specified
	 * name and value. The name and the value must not be null. Additionally,
	 * the value must be one of the supported types (i.e., string, integer,
	 * boolean or long). If a feature with the specified name already exists,
	 * it will be replaced.
	 * 
	 * @param name The name of the provided feature to create.
	 * @param value The value of the feature to create.
	 * @return The feature that has been replaced or null if no such feature
	 * 	was present.
	 * @throws NullPointerException Thrown if the name is null.
	 * @throws IllegalArgumentException Thrown if the value is malformed.
	 */
	public IFeatureProvisionWriter createFeature(String name, Object value);
	
}
