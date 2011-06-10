package info.pppc.pcom.system.model.contract;

/**
 * This interface is the basic interface for all writable contracts that
 * can be removed from their parent contracts. In addition to the getter for 
 * the name, this interface defines a remove method that removes the corresponding 
 * contract from its parent.
 * 
 * @author Mac
 */
public interface IContractOption extends IContract {

	/**
	 * Removes the contract from its parent contract.
	 */
	public void remove();
	
}
