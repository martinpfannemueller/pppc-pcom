package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The dimension demand reader provides a read-only view on a dimension
 * that is demanded by someone. Dimensions are used to describe
 * non-functional properties of type demands. They have a type unique 
 * name-level pair that identifies the demand.
 * 
 * @author Mac
 */
public interface IDimensionDemandReader extends IContract {
	
	/**
	 * Returns the features that are required by this dimension demand.
	 * The features describe certain non-functional properties of this
	 * dimension. Each feature demand has a unique name within this
	 * dimension.
	 * 
	 * @return The features that are required by this dimension. 
	 */
	public IFeatureDemandReader[] getFeatures();
	
	/**
	 * Returns a feature demand that has the specified name or null, if 
	 * such a feature demand does not exist. If the name is null, this 
	 * method will throw an exception. 
	 * 
	 * @param name The name of the feature demand to retrieve.
	 * @return The feature demand that has the specified name or null, if 
	 * 	such a feature demand does not exist.
	 * @throws NullPointerException Thrown if the feature name is null.
	 */
	public IFeatureDemandReader getFeature(String name);

}
