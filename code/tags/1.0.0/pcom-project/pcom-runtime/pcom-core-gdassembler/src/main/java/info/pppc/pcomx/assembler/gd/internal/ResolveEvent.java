package info.pppc.pcomx.assembler.gd.internal;

import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.contract.Contract;

/**
 * The resolve event configures a single instance according to its 
 * contract.
 * 
 * @author Mac
 */
public class ResolveEvent extends AbstractEvent {

	/**
	 * The pointer that denotes the instance to configure.
	 */
	protected Pointer pointer;
	
	/**
	 * The contract that describes the dependency.
	 */
	protected Contract contract; 
	
	/**
	 * A boolean flag that indicates whether the parent is reused.
	 */
	protected boolean reuse;
	
	/**
	 * The system that requests the configuration.
	 */
	protected SystemID system;
	
	/**
	 * Creates a new resolve event that configures an instance.
	 * 
	 * @param application The application that is manipulated.
	 * @param phase The algorithm phase of the event.
	 * @param pointer The pointer that denotes the instance's
	 * 	position within the tree.
	 * @param reuse A flag that indicates whether the parent is reused.
	 * @param system The system that requests the configuration.
	 * @param contract The dependency to resolve.
	 */
	public ResolveEvent(Application application, int phase, Pointer pointer, SystemID system, Contract contract, boolean reuse) {
		super(application, phase);
		this.pointer = pointer;
		this.contract = contract;
		this.reuse = reuse;
		this.system = system;
	}

	/**
	 * Configures the requested instance.
	 */
	public void perform() {
		application.resolveRequest(pointer, system, contract, reuse);
	}

}
