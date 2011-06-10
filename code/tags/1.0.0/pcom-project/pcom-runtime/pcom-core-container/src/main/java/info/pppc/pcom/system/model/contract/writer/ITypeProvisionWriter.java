package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;

/**
 * The type provision writer is used to describe a single interface, event
 * or resource type that is provided. The provison can be enriched with
 * dimensions to model non-functional properties. 
 * 
 * @author Mac
 */
public interface ITypeProvisionWriter extends IContractOption {

	/**
	 * Returns the dimensions provided by this type. The dimensions
	 * are used to describe the non-functional properties of the
	 * provided type.
	 * 
	 * @return The dimensions provided by this type.
	 */
	public IDimensionProvisionWriter[] getDimensions();
	
	/**
	 * Returns the dimension provided by this type that has the 
	 * specified name or null if no such dimension exists.
	 * 
	 * @param name The name of the dimensions that should be 
	 * 	returned.
	 * @return The provided dimension with the specified type or null
	 * 	if it does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IDimensionProvisionWriter getDimension(String name);
	
	/**
	 * Creates a new provided dimension with the specified name.
	 * If such a dimension already exists, it will be replaced and
	 * returned.
	 * 
	 * @param name The name of the dimension to create. 
	 * @return The dimension that has been replaced or null if nothing
	 * 	has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IDimensionProvisionWriter createDimension(String name);
		
}
