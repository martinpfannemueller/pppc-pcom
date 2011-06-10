package info.pppc.pcom.system.model.contract.reader;

import info.pppc.pcom.system.model.contract.IContract;

/**
 * The resource demand is used to provide read-only access to a single
 * requested resource.
 * 
 * @author Mac
 */
public interface IResourceDemandReader extends IContract {

	/**
	 * Returns the type demands that represent interface demands directed
	 * towards the resource. The interface demands can be enriched with
	 * non-functional parameters to demand a certain quality.
	 * 
	 * @return The interfaces that must be provided by the resource.
	 */
	public ITypeDemandReader[] getInterfaces();
	
	/**
	 * Returns the type demand that represents an interface demand with
	 * the specified name or null if such an interface is not demanded.
	 * 
	 * @param name The name of the interface demand.
	 * @return The interface demand with the specified name or null if
	 * 	such an interface demand does not exist.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeDemandReader getInterface(String name);
	
}
