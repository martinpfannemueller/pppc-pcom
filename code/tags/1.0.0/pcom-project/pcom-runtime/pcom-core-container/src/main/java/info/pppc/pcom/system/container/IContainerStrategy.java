package info.pppc.pcom.system.container;

import info.pppc.base.system.ReferenceID;

/**
 * The assembler strategy is the interface that the container uses to
 * retrieve a reference to the current assembler that will be used to
 * configure resource assignments for factories and for allocators.
 * 
 * @author Mac
 */
public interface IContainerStrategy {

	/**
	 * Returns the reference id of the current assembler. 
	 * If the assembler could not be found, this method
	 * returns null.
	 * 
	 * @return The reference id of the current assembler
	 * 	or null if the assembler cannot be found.
	 */
	public ReferenceID getAssemblerID();
	
}
