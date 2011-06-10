package info.pppc.pcom.system.assembler;


import info.pppc.base.lease.Lease;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ReferenceID;

/** 
 * The interface between the container and the assemblers. The general approach to
 * (re-)configure the application is as follows: First, the container will call the
 * prepare method on the assembler. 
 * If no parts of the application have been configured so far, the container will call 
 * the configure method. If the assembler returns true, the container will use the retrieve 
 * methods to retrieve the configuration. If the assembler returns false, the container 
 * will stop the execution. 
 * If the application has been configured partially already, the container will use the
 * setup methods to transfer the configuration of the application to the assembler. If
 * all existing parts have been setup, the container will call the configure method.
 * If the method returns true, the container will retrieve the configuration from the
 * assembler using the retrieve methods and it will start the application, if it returns
 * false, the container will stop the application.
 * After the application has been started or stopped, the container will call the remove
 * method to signal that the state stored by the assembler can be safely removed.
 * 
 * @author Mac
 */
public interface IAssembler {

	/**
	 * Prepares the assembler for configuring the specified application. This must
	 * be the first call to the assembler. The assembler should reserve space for
	 * the application that is bound to some lease and return the lease. 
	 * 
	 * @param applicationID The id of the application that should be configured.
	 * @return A lease for the data that will be stored in the assembler. If this
	 * 	lease expires, the assembler will release all data and will stop the 
	 * 	configuration process.
	 * @throws InvocationException Thrown by the underlying middleware if the call
	 * 	cannot be delivered.
	 */
	public Lease prepare(ReferenceID applicationID) throws InvocationException;
	
	/**
	 * Configures an application that has been prepared using the prepare method.
	 * The return value determines whether the configuration has been completed
	 * successfully or whether it has been aborted.
	 * 
	 * @param applicationID The id of the application that should be configured.
	 * 	The application id must point to an application that has been passed to
	 *  the prepare method already.
	 * @return An assembly or null, depending whether the configuration was 
	 * 	successful or not.
	 * @throws InvocationException Thrown by the underlying middleware if the 
	 * 	call cannot be delivered.
	 */
	public Assembly configure(ReferenceID applicationID) throws InvocationException;
	
	/**
	 * Returns the root assembler context for a certain setup. The root assembler 
	 * context will contain the assembler contexts for the immediate children of
	 * the setup that is configured. The setup unit will declare the contracts for
	 * the parental preferences and it will denote which dependencies are met
	 * by existing components and which dependencies are not configured. That way
	 * the assembler can determine which configuration objects must be configured
	 * for which dependency.
	 * 
	 * @param applicationID The id of the application whose anchor is configured
	 * 	with the assembly state.
	 * @param state The assembler setup that describes the possible contracts
	 * 	for an existing component under the preferences.
	 * @return The assembler pointer object that contains the children pointers
	 * 	used to setup the children in this or other assemblers.
	 * @throws InvocationException Thrown by the underlying middleware if the call
	 * 	could not be performed.
	 */
	public AssemblyPointer setup(ReferenceID applicationID, AssemblyState state) throws InvocationException;
	
	/**
	 * Returns the assembler context for a certain setup within a certain
	 * context. The returned assembler context will contain the context objects
	 * for setups that are defined to be existent in the assembler setup passed
	 * to the method.
	 * 
	 * @param pointer The pointer object that has been retrieved when the parent
	 * 	of the instance or assignment bound to the setup has been configured.
	 * @param state The setup that describes the possible configurations under the
	 * 	levels specified by the parent component.
	 * @return The assembler context object that contains the children contexts
	 * 	used to setup the children in other assemblers.
	 * @throws InvocationException Thrown by the underlying middleware if the call
	 *	could not be performed.
	 */
	public AssemblyPointer setup(AssemblyPointer pointer, AssemblyState state) throws InvocationException;
	
	/**
	 * Returns the assembler result for a given context. The assembler result contains
	 * the current configuration for an instance as well as the context objects for the
	 * children of the instance configured by the result.
	 * 
	 * @param pointer The pointer object used to configure a certain instance.
	 * @return The assembler result that contains the configuration of a certain instance
	 * 	as well as the context objects of the configured children.
	 * @throws InvocationException Thrown by the underlying middleware if the call
	 *	could not be performed.
	 */
	public Assembly retrieve(AssemblyPointer pointer) throws InvocationException;
	
	/**
	 * Removes all state of a certain application that has been initalized previously
	 * using the prepare method.
	 * 
	 * @param applicationID The id of the application as specified in the prepare method.
	 * @throws InvocationException Thrown by the underlying middleware if the call
	 *	could not be performed.
	 */
	public void remove(ReferenceID applicationID) throws InvocationException;
	
}