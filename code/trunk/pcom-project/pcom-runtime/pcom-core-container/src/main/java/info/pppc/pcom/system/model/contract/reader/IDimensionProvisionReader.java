package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The dimension provision provides a read-only view on a dimension 
 * that is provided by some type. The dimension itself is used to describe
 * a certain non-functional aspect and its properties that can be provided
 * to another component. 
 * 
 * @author Mac
 */
public interface IDimensionProvisionReader extends IContract {
	
	/**
	 * Returns all features that are provided by this dimension. The dimension
	 * might contain multiple features with the same name. They are considered
	 * to be or-ed together.
	 * 
	 * @return The features provided by this dimension.
	 */
	public IFeatureProvisionReader[] getFeatures();
	
	/**
	 * Returns the provided features with the specified name. If the name is
	 * null, this method will throw an exception. If there are no features
	 * with the specified name, this method will return null.
	 * 
	 * @param name The name of the provided features that should be returned.
	 * @return The feature with the specified name or null, if there is no such 
	 * 	feature.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IFeatureProvisionReader getFeature(String name);

}
