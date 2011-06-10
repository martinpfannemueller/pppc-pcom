package info.pppc.pcomx.container;

import info.pppc.base.system.ReferenceID;
import info.pppc.pcom.system.container.IContainerStrategy;

/**
 * The static strategy is a strategy that points statically to
 * one assembler. It does not perform any lookups, etc. It 
 * simply returns the id of the assembler that has been passed
 * to the construtor.
 * 
 * @author Mac
 */
public class FixedContainerStrategy implements IContainerStrategy {

	/**
	 * The reference id of the assembler.
	 */
	private ReferenceID assemblerID;
	
	/**
	 * Creates a new static strategy that will return the
	 * specified assembler id upon request.
	 * 
	 * @param assemblerID The id of the assembler that will
	 * 	be returned upon request.
	 */
	public FixedContainerStrategy(ReferenceID assemblerID) {
		this.assemblerID = assemblerID;
	}

	/**
	 * Returns the assembler id that will be used to
	 * configure resource assignments for factories and
	 * allocators.
	 * 
	 * @return The reference id of the assembler to use.
	 */
	public ReferenceID getAssemblerID() {
		return assemblerID;
	}

}
