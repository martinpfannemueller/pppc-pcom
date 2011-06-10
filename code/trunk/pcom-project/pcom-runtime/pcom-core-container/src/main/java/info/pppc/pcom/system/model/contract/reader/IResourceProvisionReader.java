package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The resource provision is used to provide read-only access to a single
 * provided resource.
 * 
 * @author Mac
 */
public interface IResourceProvisionReader extends IContract {

	/**
	 * Returns the interfaces that are provided by this resource. The
	 * interfaces have a certain type and they can be enriched with
	 * dimensions to model non-functional parameters.
	 * 
	 * @return The interfaces provided by the resource or an empty
	 * 	array if there are no provided interfaces.
	 */
	public ITypeProvisionReader[] getInterfaces();

	/**
	 * Returns the interface that is provided by this resource provision
	 * and has the specified name or null if such an interface does not
	 * exist.
	 * 
	 * @param name The name of the interface to retrieve.
	 * @return The interface with the specified name or null if it
	 * 	does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeProvisionReader getInterface(String name);
	
}
