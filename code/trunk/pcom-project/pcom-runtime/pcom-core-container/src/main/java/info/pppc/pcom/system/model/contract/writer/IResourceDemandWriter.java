package info.pppc.pcom.system.model.contract.writer;

import info.pppc.pcom.system.model.contract.IContractOption;

/**
 * The resource demand writer is used to provide read-write access to 
 * a single requested resource.
 * 
 * @author Mac
 */
public interface IResourceDemandWriter extends IContractOption {

	/**
	 * Returns the type demands that represent interface demands directed
	 * towards the resource. The interface demands can be enriched with
	 * non-functional parameters to demand a certain quality.
	 * 
	 * @return The interfaces that must be provided by the resource.
	 */
	public ITypeDemandWriter[] getInterfaces();
	
	/**
	 * Returns the type demand with the specified name or null if no 
	 * such demand exists.
	 * 
	 * @param name The name of the type demand to retrieve.
	 * @return The type demand with the specified name or null if no
	 * 	such demand exists.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public ITypeDemandWriter getInterface(String name);
	
	/**
	 * Creates a demanded interface with the specified type name.
	 * 
	 * @param name The name of the interface to create.
	 * @return The interface that has been created.
	 */
	public ITypeDemandWriter createInterface(String name);
	
}
