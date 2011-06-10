package info.pppc.pcom.system.container.internal.contract;

import info.pppc.pcom.system.contract.Contract;

/**
 * The remover interface is implemented by parents of demand and provision
 * writers that enable their children to remove themselves. The remover
 * is passed to the writer in the constructor together with the contract 
 * that the writer manages.
 * 
 * @author Mac
 */
public interface IRemover {
	
	/**
	 * Removes the specified child contract from the parent contract of the
	 * remover.
	 * 
	 * @param contract The child contract that should be removed from the
	 * 	parent.
	 */
	public void remove(Contract contract);
	
}
