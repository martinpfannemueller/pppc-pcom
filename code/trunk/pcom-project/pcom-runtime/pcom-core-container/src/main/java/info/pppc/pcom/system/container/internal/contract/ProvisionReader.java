package info.pppc.pcom.system.container.internal.contract;

import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.reader.IDimensionProvisionReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureProvisionReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceProvisionReader;
import info.pppc.pcom.system.model.contract.reader.IResourceProvisionReader;
import info.pppc.pcom.system.model.contract.reader.ITypeProvisionReader;

import java.util.Hashtable;

/**
 * The provision reader provides a generic reader view for all provision contracts. Thus
 * it implements all provision reader types that can be accessed by instances, factories
 * allocators and resources.
 * 
 * @author Mac
 */
public class ProvisionReader implements IDimensionProvisionReader,
		IFeatureProvisionReader, IInstanceProvisionReader,
		IResourceProvisionReader, ITypeProvisionReader {

	/**
	 * The contract that is managed by the reader.
	 */
	private Contract contract;
	
	/**
	 * A cache for the readers that have been issued for children of this
	 * reader.
	 */
	private Hashtable cache;
	
	/**
	 * Creates a new provision reader for the specified contract.
	 * 
	 * @param contract The contract managed by this reader.
	 */
	public ProvisionReader(Contract contract) {
		this.contract = contract;
	}
	
	/**
	 * Returns the provision reader for the specified contract. If the reader
	 * has not been created and cached already, this method will create a 
	 * new reader.
	 * 
	 * @param contract The contract that should be retrieved.
	 * @return The provision reader for the contract or null if the contract
	 * 	was null in the first place.
	 */
	private ProvisionReader getReader(Contract contract) {
		ProvisionReader result = null;
		if (contract != null) {
			if (cache == null) {
				cache = new Hashtable();
			}
			result = (ProvisionReader)cache.get(contract);
			if (result == null) {
				result = new ProvisionReader(contract);
				cache.put(contract, result);
			}			
		}
		return result;
	}
	
	/**
	 * Returns the provision reader for the specified child with the specified
	 * name and type or null if no such child exists.
	 * 
	 * @param type The type of the child contract.
	 * @param name The name of the child contract.
	 * @return The child contract with the specified name or null if no such
	 * 	child exists.
	 */
	private ProvisionReader getReader(byte type, String name) {
		return getReader(contract.getContract(type, name));
	}
	
	/**
	 * Returns the readers for the children with the specified type.
	 * 
	 * @param type The type of the children to retrieve.
	 * @return The readers for the children with the specified type.
	 */
	private ProvisionReader[] getReaders(byte type) {
		Contract[] contracts = contract.getContracts(type);
		ProvisionReader[] readers = new ProvisionReader[contracts.length];
		for (int i = 0; i < contracts.length; i++) {
			readers[i] = getReader(contracts[i]);
		}
		return readers;
	}
	
	/**
	 * Returns the name of the provision contract. 
	 * 
	 * @return The name of the provsion contract.
	 */
	public String getName() {
		return contract.getName();
	}

	/**
	 * Returns the feature contracts of this provision contract. Calling this
	 * method is only valid for dimension contracts. 
	 * 
	 * @return The feature provisions of this contract.
	 */
	public IFeatureProvisionReader[] getFeatures() {
		return getReaders(Contract.TYPE_FEATURE_PROVISION);
	}

	/**
	 * Returns the feature contract with the specified name. Calling this 
	 * method is only valid for dimension contracts.
	 * 
	 * @param name The name of the feature provision to retrieve.
	 * @return The feature provision contract with the specified name or null
	 * 	if no such contract exists.
	 */
	public IFeatureProvisionReader getFeature(String name) {
		return getReader(Contract.TYPE_FEATURE_PROVISION, name);
	}

	/**
	 * Returns the attribute value of the contract. Calling this method is only
	 * valid for feature contracts.
	 * 
	 * @return The attribute value of this contract.
	 */
	public Object getValue() {
		return contract.getAttribute(Contract.ATTRIBUTE_FEATURE_VALUE);
	}
	
	/**
	 * Returns the dynamic value of the feature. True if the feature is dynamic,
	 * false if the feature is fixed.
	 * 
	 * @return True if the feature is dynamic, false otherwise.
	 */
	public boolean isDynamic() {
		return ((Boolean)contract.getAttribute(Contract.ATTRIBUTE_FEATURE_DYNAMIC)).booleanValue();
	}

	/**
	 * Returns the interface provisions specified by this contract. Calling this
	 * method is only valid for instance and resource provisions.
	 * 
	 * @return The interface provisisions specified by this provision contract.
	 */
	public ITypeProvisionReader[] getInterfaces() {
		return getReaders(Contract.TYPE_INTERFACE_PROVISION);
	}

	/**
	 * Returns the interface provision with the specified name. Calling this method
	 * is only valid for instance and resource provisions.
	 * 
	 * @param name The name of the interfac provision to retrieve.
	 * @return The interface provision with the specified name or null if such a
	 * 	provision does not exist.
	 */
	public ITypeProvisionReader getInterface(String name) {
		return getReader(Contract.TYPE_INTERFACE_PROVISION, name);
	}

	/**
	 * Returns the events specified by this provision. Calling this method is
	 * only valid for instance provisions.
	 * 
	 * @return The events psecified by this instance provision.
	 */
	public ITypeProvisionReader[] getEvents() {
		return getReaders(Contract.TYPE_EVENT_PROVISION);
	}

	/**
	 * Returns the event provision with the specified name. Calling this method
	 * is only valid for instance provisions.
	 * 
	 * @param name The name of the event provision to retrieve.
	 * @return The event provision with the specified name or null if it does
	 * 	not exist.
	 */
	public ITypeProvisionReader getEvent(String name) {
		return getReader(Contract.TYPE_EVENT_PROVISION, name);
	}

	/**
	 * Returns the dimension provisions that are specified by this provision.
	 * Calling this method is only valid for type provisions.
	 * 
	 * @return The event provisions that are specified by this provision.
	 */
	public IDimensionProvisionReader[] getDimensions() {
		return getReaders(Contract.TYPE_DIMENSION_PROVISION);
	}
	
	/**
	 * Returns the dimension provision with the specified name. Calling this
	 * method is only valid for type provisions.
	 * 
	 * @param name The name of the dimension provision to retrieve.
	 * @return The dimension provision with the specified name or null if it
	 * 	does not exist.
	 */
	public IDimensionProvisionReader getDimension(String name) {
		return getReader(Contract.TYPE_DIMENSION_PROVISION, name);
	}
	
	/**
	 * Returns a string representation of the reader's contract.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		return contract.toString();
	}

	
}
