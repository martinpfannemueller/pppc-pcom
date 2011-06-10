package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The type provision reader provides read-only access to a single provided
 * type. They are used to model types of interfaces, events and
 * resources. Using dimensions the type can describe its non-functional
 * properties in detail.
 *  
 * @author Mac
 */
public interface ITypeProvisionReader extends IContract {
	
	/**
	 * Returns the dimensions provided by this type. The dimensions
	 * are used to describe the non-functional properties of the
	 * provided type.
	 * 
	 * @return The dimensions provided by this type.
	 */
	public IDimensionProvisionReader[] getDimensions();

	/**
	 * Returns the dimension provided by this type that has the 
	 * specified name or null if no such dimension exists.
	 * 
	 * @param name The name of the dimension that should be returned.
	 * @return The provided dimension with the specified type or
	 * 	null if no such dimension exists.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public IDimensionProvisionReader getDimension(String name);

}
