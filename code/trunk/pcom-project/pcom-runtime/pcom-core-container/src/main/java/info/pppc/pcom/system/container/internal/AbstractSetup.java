package info.pppc.pcom.system.container.internal;

import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.IContract;

import java.util.Hashtable;

/**
 * The abstract setup is the base class for all setups, status and template
 * objects. Using readers and writers, it provides readable and writable
 * views on the contract object. This class contains a view cache that 
 * caches the readers and writers for their corresponding contract objects.
 * 
 * @author Mac
 */
public abstract class AbstractSetup {
	
	/**
	 * The base contract that is managed by this setup object.
	 */
	private Contract contract;
	
	/**
	 * The view cache that hashes readers and writers for their 
	 * corresponding contract objects.
	 */
	private Hashtable cache = new Hashtable();
	
	/**
	 * Creates a new setup for the specified contract.
	 * 
	 * @param contract The base contract of the setup.
	 */
	protected AbstractSetup(Contract contract) {
		this.contract = contract;
	}

	/**
	 * Returns the base contract that is managed by this setup.
	 * 
	 * @return The base contract managed by this setup.
	 */
	public Contract getContract() {
		return contract;
	}
	
	/**
	 * Adds a contract child to the base contract and returns the child that 
	 * has possibly been removed. This method will automatically adjust the 
	 * view cache and release unused views. Thus, implementing instances 
	 * should only use this method to modify the contract.
	 * 
	 * @param part The contract child that should be added to
	 * 	the base contract managed by this setup.
	 * @return The contract that has been replaced through the
	 * 	addition or null if none has been replaced.
	 */
	public Contract addContract(Contract part) {
		Contract replaced = contract.addContract(part);
		if (replaced != null) {
			removeView(replaced);	
		}
		return replaced;
	}
	
	/**
	 * Removes the contract with the specified type and name from the base 
	 * contract managed by this setup. This method will automatically adjust 
	 * the view cache and release unused views. Thus, implementing instances 
	 * should only use this method to modify the contract.
	 * 
	 * @param type The type of the contract to remove.
	 * @param name The name of the contract to remove.
	 * @return The contract that has been removed or null if no such
	 * 	contract exists.
	 */
	public Contract removeContract(byte type, String name) {
		Contract removed = contract.removeContract(type, name);
		if (removed != null) {
			removeView(removed);	
		}
		return removed;
	}
	
	/**
	 * Returns the contract with the specified type and name.
	 * 
	 * @param type The type of the contract to retrieve.
	 * @param name The name of the contract to retrieve.
	 * @return The contract with the specified type and name or
	 * 	null if it does not exist.
	 */
	public Contract getContract(byte type, String name) {
		return contract.getContract(type, name);
	}
	
	/**
	 * Returns the contracts with the specified type.
	 * 
	 * @param type The type of the contract that should be retrieved.
	 * @return The contracts with the specified type.
	 */
	public Contract[] getContracts(byte type) {
		return contract.getContracts(type);
	}
	
	/**
	 * Returns the first contract with the specified type.
	 * 
	 * @param type The type of the contract to retrieve.
	 * @return The first occurence of the contract.
	 */
	public Contract getContract(byte type) {
		return contract.getContract(type);
	}
	
	/**
	 * Removes the specified contract from the base contract. This method will 
	 * automatically adjust the view cache and release unused views. Thus, 
	 * implementing instances should only use this method to modify the contract.
	 * 
	 * @param part The contract that needs to be removed.
	 * @return True if the contract has been removed, false if the contract was
	 * 	not a child of the base contract.
	 */
	public boolean removeContract(Contract part) {
		if (part != null) {
			if (contract.removeContract(part)) {
				removeView(part);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates the view for the specified contract part. Implementing 
	 * clients must implement this method and return the specified reader
	 * or writer for the contract. This method should never be called
	 * by implementing clients directly, instead, they should use the
	 * get view method that will create the view on demand.
	 * 
	 * @param spec The contract whose view needs to be created.
	 * @return The view for the contract.
	 */
	protected abstract IContract createView(Contract spec);
	
	/**
	 * Returns the view for the specified contract. If the view has been
	 * created and cached already, this method will return the view,
	 * otherwise the method will create a new view and cache it for 
	 * future reuse.
	 * 
	 * @param spec The contract whose view should be retrieved.
	 * @return The view for the contract or null if the contract was
	 * 	null.
	 */
	protected IContract getView(Contract spec) {
		IContract result = null; 
		if (spec != null) {
			result = (IContract)cache.get(spec);
			if (result == null) {
				result = createView(spec);
				cache.put(spec, result);
			}
		}
		return result;
	}
	
	/**
	 * Removes the cached view for a contract.
	 * 
	 * @param spec The contract whose view should be removed.
	 */
	private void removeView(Contract spec) {
		if (spec != null) {
			cache.remove(spec);
		}
	}
	
	/**
	 * Removes all contracts and their views from the base contract
	 * and returns them.
	 * 
	 * @return The contracts that have been removed due to request.
	 */
	public Contract[] removeContracts() {
		Contract[] contracts = contract.getContracts();
		for (int i = 0; i < contracts.length; i++) {
			removeContract(contracts[i]);
		}
		return contracts;
	}
	
}
