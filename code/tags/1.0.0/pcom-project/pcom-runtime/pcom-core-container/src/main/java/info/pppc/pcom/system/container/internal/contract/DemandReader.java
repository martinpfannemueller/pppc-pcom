package info.pppc.pcom.system.container.internal.contract;

import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureDemandReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;

import java.util.Hashtable;

/**
 * The demand reader provides a generic reader view for all demand contracts. Thus
 * it implements all demand reader types that can be accessed by instances, factories
 * allocators and resources.
 * 
 * @author Mac
 */
public class DemandReader implements IDimensionDemandReader,
		IFeatureDemandReader, IInstanceDemandReader, IResourceDemandReader,
		ITypeDemandReader {
	
	/**
	 * A view cache for the child elements issued by this reader. The
	 * cache is initialized lazy in order to safe space.
	 */
	private Hashtable cache;
	
	/**
	 * The contract that is managed by this view.
	 */
	private Contract contract;
	
	/**
	 * Creates a new demand reader for the specified contract.
	 * 
	 * @param contract The contract that provides the model.
	 */
	public DemandReader(Contract contract) {
		this.contract = contract;
	}
	
	/**
	 * Returns a demand reader either from cache or a new one for
	 * the contract passed to the reader.
	 * 
	 * @param contract The contract that provides the model.
	 * @return The demand reader for the contract. Returns null if
	 * 	the contract is null.
	 */
	private DemandReader getReader(Contract contract) {
		DemandReader result = null;
		if (contract != null) {
			if (cache == null) {
				cache = new Hashtable();
			}
			result = (DemandReader)cache.get(contract);
			if (result == null) {
				result = new DemandReader(contract);
				cache.put(contract, result);
			}			
		}
		return result;
	}
	
	/**
	 * Returns the demand reader for the child of the managed contract
	 * with the specified type and name.
	 * 
	 * @param type The type of the contract.
	 * @param name The name of the contract.
	 * @return The view of the contract or null if the contract does
	 * 	not exist.
	 */
	private DemandReader getReader(byte type, String name) {
		return getReader(contract.getContract(type, name));
	}
	
	/**
	 * Returns the demand readers for the children of the managed contract
	 * with the specified type.
	 * 
	 * @param type The type of the contract.
	 * @return Demand readers for the children of the contract with the
	 * 	specified type.
	 */
	private DemandReader[] getReaders(byte type) {
		Contract[] contracts = contract.getContracts(type);
		DemandReader[] readers = new DemandReader[contracts.length];
		for (int i = 0; i < contracts.length; i++) {
			readers[i] = getReader(contracts[i]);
		}
		return readers;
	}
	
	/**
	 * Returns the feature demand children of the contract. Calling this
	 * method is only valid for dimension demands.
	 * 
	 * @return The feature demands of this dimension demand.
	 */
	public IFeatureDemandReader[] getFeatures() {
		return getReaders(Contract.TYPE_FEATURE_DEMAND);
	}

	/**
	 * Returns the feature demand child of the contract with the specified
	 * name. Calling this method is only valid for dimension demands.
	 * 
	 * @param name The name of the child.
	 * @return The feature demand with the specified name or null if it
	 * 	does not exist.
	 */
	public IFeatureDemandReader getFeature(String name) {
		return getReader(Contract.TYPE_FEATURE_DEMAND, name);
	}

	/**
	 * Returns the name of this contract.
	 * 
	 * @return The name of this contract.
	 */
	public String getName() {
		return contract.getName();
	}

	/**
	 * Returns the comparator value of the contract. Calling this method is only
	 * valid for feature demands.
	 * 
	 * @return The comparator constant.
	 */
	public int getComparator() {
		Object c = contract.getAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR);
		return ((Integer)c).intValue();
	}

	/**
	 * Returns the value attribute of the contract. Calling this method is only
	 * valid for feature demands with primitive comparators.
	 * 
	 * @return The value of the contract.
	 */
	public Object getValue() {
		return contract.getAttribute(Contract.ATTRIBUTE_FEATURE_VALUE);
	}

	/**
	 * Returns the minimum value attribute of the contract. Calling this method is 
	 * only valid for feature demands with range comparators.
	 * 
	 * @return The minimum value of the contract.
	 */
	public Object getMinimum() {
		return contract.getAttribute(Contract.ATTRIBUTE_FEATURE_MINIMUM);
	}

	/**
	 * Returns the maximum value attribute of the contract. Calling this method is 
	 * only valid for feature demands with range comparators.
	 * 
	 * @return The maximum value of the contract.
	 */
	public Object getMaximum() {
		return contract.getAttribute(Contract.ATTRIBUTE_FEATURE_MAXIMUM);
	}

	/**
	 * Returns the interface demands of the contract. Calling this method is only
	 * valid for resource and instance demands.
	 * 
	 * @return The interfaces specified by the contract.
	 */
	public ITypeDemandReader[] getInterfaces() {
		return getReaders(Contract.TYPE_INTERFACE_DEMAND);
	}

	/**
	 * Returns the interface demand with the specified name. Calling this method is 
	 * only valid for resource and instance demands.
	 * 
	 * @param name The name of the interface to retrieve.
	 * @return The interface demand with the specified name or null if such a demand
	 * 	does not exist.
	 */
	public ITypeDemandReader getInterface(String name) {
		return getReader(Contract.TYPE_INTERFACE_DEMAND, name);
	}

	/**
	 * Returns the event demands of the contract. Calling this method is only
	 * valid for instance demands.
	 * 
	 * @return The events specified by the contract.
	 */
	public ITypeDemandReader[] getEvents() {
		return getReaders(Contract.TYPE_EVENT_DEMAND);
	}

	/**
	 * Returns the event demand with the specified name. Calling this method is 
	 * only valid for instance demands.
	 * 
	 * @param name The name of the event to retrieve.
	 * @return The event demand with the specified name or null if such a demand
	 * 	does not exist.
	 */
	public ITypeDemandReader getEvent(String name) {
		return getReader(Contract.TYPE_EVENT_DEMAND, name);
	}

	/**
	 * Returns the dimension demands that are specified by this contract. Calling
	 * this method is only valid for type demands.
	 * 
	 * @return The dimension demands specified by the contract.
	 */
	public IDimensionDemandReader[] getDimensions() {
		return getReaders(Contract.TYPE_DIMENSION_DEMAND);
	}

	/**
	 * Returns the dimension demand with the specified name. Calling this method
	 * is only valid for type demands.
	 * 
	 * @param name The name of the dimension to retrieve.
	 * @return The dimension demand with the specified name or null if such a
	 * 	dimension demand does not exist.
	 */
	public IDimensionDemandReader getDimension(String name) {
		return getReader(Contract.TYPE_DIMENSION_DEMAND, name);
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
