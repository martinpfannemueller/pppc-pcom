package info.pppc.pcomx.assembler.gd.internal;

import java.util.Vector;

import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.contract.Contract;

/**
 * The instance request is used to describe an incoming instance
 * dependency. It contains the contract
 * 
 * @author Mac
 */
public class InstanceRequest {

	/**
	 * A flag that indicates whether the request is still required.
	 * If this is set to false, the request should be stopped.
	 */
	protected boolean required = true; 
	
	/**
	 * The system that requested the configuration.
	 */
	protected final SystemID system;
	
	/**
	 * The pointer to the application.
	 */
	protected final Pointer pointer;
		
	/**
	 * The contract that is currently resolved.
	 */
	protected final Contract contract;
	
	/**
	 * The candiate instances that can be configured for the 
	 * dependency. The first instance in the vector is the
	 * one that is currently configured.
	 */
	protected final Vector instances = new Vector();
	
	/**
	 * Creates a new instance request with the specified pointer for the
	 * specified system with the specified instance demand.
	 *  
	 * @param pointer The pointer that denotes the instance demand.
	 * @param system The system that requests the instance.
	 * @param contract The contract that describes the requirements.
	 */
	public InstanceRequest(Pointer pointer, SystemID system, Contract contract) {
		this.pointer = pointer;
		this.system = system;
		this.contract = contract;
	}

}
