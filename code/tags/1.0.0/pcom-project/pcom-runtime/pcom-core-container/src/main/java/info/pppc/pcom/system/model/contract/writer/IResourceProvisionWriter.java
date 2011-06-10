package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The resource provision writer is used to provide read-write access to 
 * a single provided resource.
 * 
 * @author Mac
 */
public interface IResourceProvisionWriter extends IContract {

	/**
	 * Returns the interfaces that are provided by this resource. The
	 * interfaces have a certain type and they can be enriched with
	 * dimensions to model non-functional parameters.
	 * 
	 * @return The interfaces provided by the resource or an empty
	 * 	array if there are no provided interfaces.
	 */
	public ITypeProvisionWriter[] getInterfaces();

	/**
	 * Returns the interface with the specified name that is provided
	 * by the resource.
	 * 
	 * @param name The name of the interface to retrieve.
	 * @return The interface with the specified name or null if no such
	 * 	interface exists.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeProvisionWriter getInterface(String name);
	
	/**
	 * Creates a new provided interface for the specified type name. If
	 * such an interface exists already, it will be replaced and returned.
	 * 
	 * @param name The name of the interface type to create.
	 * @return The mutable type provision that has been replaced or null
	 * 	if nothing has been replaced.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeProvisionWriter createInterface(String name);

}
