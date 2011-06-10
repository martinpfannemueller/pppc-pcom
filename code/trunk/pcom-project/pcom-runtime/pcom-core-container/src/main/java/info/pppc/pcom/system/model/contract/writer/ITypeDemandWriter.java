package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;

/**
 * The type demand writer is used to demand a single interface, event or
 * resource. it can be enriched with dimensions to model non-functional
 * parameters of the demand. 
 * 
 * @author Mac
 */
public interface ITypeDemandWriter extends IContractOption {
	
	/**
	 * Returns all dimensions that are demanded by this type. The
	 * dimensions are used to describe the demand in more detail.
	 * 
	 * @return The dimensions demanded by this type.
	 */
	public IDimensionDemandWriter[] getDimensions();
	
	/**
	 * Returns the dimension with the specified name or null if no
	 * such dimension exists.
	 * 
	 * @param name The name of the dimension to retrieve.
	 * @return The dimension with the specified name or null if it
	 * 	does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IDimensionDemandWriter getDimension(String name);
	
	/**
	 * Creates a new dimension with the specified name. If such a dimension
	 * already exists, it will be replaced and returned.
	 * 
	 * @param name The name of the dimension to create.
	 * @return The dimension that has been replaced or null if nothing has
	 * 	been replaced.
	 * @throws NullPointerException Thrown if the name is null. 
	 */
	public IDimensionDemandWriter createDimension(String name);
		
}
