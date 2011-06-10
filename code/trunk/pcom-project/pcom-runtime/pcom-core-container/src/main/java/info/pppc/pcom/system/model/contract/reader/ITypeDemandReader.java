package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The type demand provides read-only access to demanded types. Demanded
 * types model interfaces, events and resources. Using dimension demands,
 * the non-functional requirements of the types can be described in detail.
 * 
 * @author Mac
 */
public interface ITypeDemandReader extends IContract {
	
	/**
	 * Returns all dimensions that are demanded by this type. The
	 * dimensions are used to describe the demand in more detail.
	 * 
	 * @return The dimensions demanded by this type.
	 */
	public IDimensionDemandReader[] getDimensions();

	/**
	 * Returns the dimension with the specified name or null if such
	 * a dimension is not specified.
	 * 
	 * @param name The name of the dimension to retrieve.
	 * @return The dimension with the specified name or null if it
	 * 	does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IDimensionDemandReader getDimension(String name);

}
