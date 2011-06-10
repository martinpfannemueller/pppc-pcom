package info.pppc.pcomx.assembler.gd.internal;

import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.contract.Contract;

import java.util.Vector;

/**
 * The instance binding describes a dependency that is resolved
 * by some (local or remote) system.
 * 
 * @author Mac
 */
public class InstanceBinding {

	/**
	 * A state that denotes that the instance binding is
	 * not configured yet, but will be eventually.
	 */
	public static final int STATE_RESOLVING = 1;

	/**
	 * A state that denotes that the instance binding is
	 * is resolved.
	 */
	public static final int STATE_RESOLVED = 2;

	/**
	 * A state that denotes that the instance binding is
	 * currently about to be released. 
	 */
	public static final int STATE_RELEASING = 3;
	
	/**
	 * The parent instance that uses this dependency.
	 */
	protected final Instance parent;
	
	/**
	 * The pointer of the instance binding.
	 */
	protected final Pointer pointer;
	
	/**
	 * The contract that should be resolved by the binding.
	 */
	protected final Contract contract;
	
	/**
	 * The systems that should be used to satisfy the dependency.
	 * The first system in the vector is the one that is currently
	 * configured.
	 */
	protected final Vector systems = new Vector();
	
	/**
	 * The current state of the instance dependency.
	 */
	protected int state;

	/**
	 * The assembly that has been configured for the subtree 
	 * in cases that the state is resolved.
	 */
	protected Assembly assembly;
	
	/**
	 * Creates a new instance binding for the specified parent using the
	 * specified pointer and the specified systems.
	 * 
	 * @param parent The parent that uses the instance.
	 * @param pointer The pointer that denotes the instance.
	 * @param contract The contract that must be resolved by the binding.
	 */
	public InstanceBinding(Instance parent, Pointer pointer, Contract contract) {
		this.parent = parent;
		this.pointer  = pointer;
		this.contract = contract;
	}
	

}
