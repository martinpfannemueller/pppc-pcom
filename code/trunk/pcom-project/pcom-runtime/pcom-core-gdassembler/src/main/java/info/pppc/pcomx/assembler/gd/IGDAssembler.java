package info.pppc.pcomx.assembler.gd;

import java.util.Vector;

import info.pppc.base.lease.Lease;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.IAssembler;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcomx.assembler.gd.internal.Pointer;

/**
 * This is the internal remote interface of greedy assemblers. It is used
 * for assembler-to-assembler communication.
 * 
 * @author Mac
 */
public interface IGDAssembler extends IAssembler {
	
	/**
	 * Prepares the greedy assembler to act as slave of another assembler for
	 * a given application. The system ids are the devices that can be used 
	 * during configuration.
	 * 
	 * @param applicationID The id of the application that is configured.
	 * @param master The master that controls the process.
	 * @param systems The systems that participate in the process.
	 * @return A lease for the internal state of the assembler.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Lease prepare(ReferenceID applicationID, SystemID master, Vector systems) throws InvocationException;

	/**
	 * Removes the specified system id from the specified application.
	 * 
	 * @param applicationID The id of the application.
	 * @param system The system that should be removed.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void remove(ReferenceID applicationID, SystemID system) throws InvocationException;
	
	/**
	 * Resolves the specified instance demand contract for the specified pointer
	 * in the specified application. 
	 * 
	 * @param applicationID The id of the application.
	 * @param pointer The pointer that denotes the position in the tree.
	 * @param phase The current phase of the configuration algorithm.
	 * @param system The requesting system that uses the instance.
	 * @param contract The instance demand that should be resolved.
	 * @param reuse A flag that indicates whether the parent is reused.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void resolve(ReferenceID applicationID, int phase, Pointer pointer, SystemID system, Contract contract, boolean reuse) throws InvocationException;
	
	/**
	 * Releases all configured data for the specified pointer.
	 * 
	 * @param applicationID The id of the application.
	 * @param phase The current phase of the configuration algorithm.
	 * @param pointer The pointer that denotes the instance to release.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void release(ReferenceID applicationID, int phase, Pointer pointer) throws InvocationException;
	
	/**
	 * Reports that the specified pointer has been configured successfully or
	 * unsuccessfully by the specified system.
	 * 
	 * @param applicationID The id of the application.
	 * @param pointer The pointer that denotes the 
	 * @param phase The current phase of the configuration algorithm.
	 * @param assembly The assembly for the subtree or null if the configuration
	 * 	failed.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void report(ReferenceID applicationID, int phase, Pointer pointer, Assembly assembly) throws InvocationException;
	
}
