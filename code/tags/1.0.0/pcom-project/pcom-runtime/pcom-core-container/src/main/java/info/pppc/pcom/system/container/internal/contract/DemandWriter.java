package info.pppc.pcom.system.container.internal.contract;

import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.writer.IDimensionDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IFeatureDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeDemandWriter;

import java.util.Hashtable;

/**
 * The demand writer provides a generic writer view for all demand contracts. Thus
 * it implements all demand writer types that can be accessed by instances, factories
 * allocators and resources.
 * 
 * @author Mac
 */
public class DemandWriter implements IDimensionDemandWriter,
		IFeatureDemandWriter, IInstanceDemandWriter, IResourceDemandWriter,
		ITypeDemandWriter, IRemover {

	/**
	 * The cache used to cache the demand writers created by this writer.
	 */
	private Hashtable cache;  
	
	/**
	 * The parent used to remove this contract. This may be null for 
	 * writable demands that cannot be removed.
	 */
	private IRemover parent;
		
	/**
	 * The contract that is managed by this demand writer.
	 */
	private Contract contract;
	
	/**
	 * Creates a new demand writer that uses the specified parent for
	 * removal operations and manages the specified contract.
	 * 
	 * @param parent The parent used for removal operations.
	 * @param contract The contract that is managed.
	 */
	public DemandWriter(IRemover parent, Contract contract) {
		this.contract = contract;
		this.parent = parent;
	}

	/**
	 * Returns the writer for the specified contract or creates it if
	 * it has not been already created.
	 * 
	 * @param mod The contract whose writer must be created.
	 * @return The demand writer for the contract or null if the 
	 * 	contract itself was null.
	 */
	private DemandWriter getWriter(Contract mod) {
		DemandWriter result = null;
		if (mod != null) {
			if (cache == null) {
				cache = new Hashtable();
			}
			result = (DemandWriter)cache.get(mod);
			if (result == null) {
				result = new DemandWriter(this, mod);
				cache.put(mod, result);
			}	
		}
		return result;
	}
	
	/**
	 * Returns a demand writer for the child with the specified name and
	 * type or null if such a child does not exist.
	 * 
	 * @param type The type of the child contract.
	 * @param name The name of the child contract.
	 * @return The demand writer for the child or null if the child does
	 * 	not exist.
	 */
	private DemandWriter getWriter(byte type, String name) {
		return getWriter(contract.getContract(type, name));
	}
	
	/**
	 * Returns an array of demand writers for the children of the contract
	 * with the specified type.
	 * 
	 * @param type The type of the children to retrieve.
	 * @return An array of writers with the children with the specified type.
	 */
	private DemandWriter[] getWriters(byte type) {
		Contract[] contracts = contract.getContracts(type);
		DemandWriter[] writers = new DemandWriter[contracts.length];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = getWriter(contracts[i]);
		}
		return writers;
	}
	
	/**
	 * Replaces the specified contract and returns the view of the
	 * replaced contract thereby removing the view from the cache.
	 * 
	 * @param mod The contract that might replace another.
	 * @return The demand writer view for the replaced contract.
	 */
	private DemandWriter replaceWriter(Contract mod) {
		Contract replaced = contract.addContract(mod);
		DemandWriter writer = getWriter(replaced);
		if (cache != null && replaced != null) {
			cache.remove(replaced);	
		}
		return writer;
	}
	
	/**
	 * Returns the feature demand children of the contract. Calling this
	 * method is only valid for dimension demands.
	 * 
	 * @return The feature demands of this dimension demand.
	 */
	public IFeatureDemandWriter[] getFeatures() {
		return getWriters(Contract.TYPE_FEATURE_DEMAND);
	}

	/**
	 * Returns the feature demand child of the contract with the specified
	 * name. Calling this method is only valid for dimension demands.
	 * 
	 * @param name The name of the child.
	 * @return The feature demand with the specified name or null if it
	 * 	does not exist.
	 */
	public IFeatureDemandWriter getFeature(String name) {
		return getWriter(Contract.TYPE_FEATURE_DEMAND, name);
	}

	/**
	 * Creates a new feature demand for a non-range comparator with the specified
	 * name, comparator and value object. Returns the feature that might have been
	 * replaced within the process or null if there was no feature with the name.
	 * Calling this method is only valid for dimension demands.
	 * 
	 * @param name The name of the feature to create.
	 * @param comparator The comparator of the feature.
	 * @param value The value of the feature.
	 * @return The possibly replaced feature or null if none has been replaced.
	 */
	public IFeatureDemandWriter createFeature(String name, int comparator, Object value) {
		Contract feature = new Contract(Contract.TYPE_FEATURE_DEMAND, name);
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(comparator));
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, value);
		return replaceWriter(feature);
	}

	/**
	 * Creates a new feature demand for a range comparator with the specified name, 
	 * comparator, minimum and maximum. Returns the feature that might have been
	 * replaced within the process or null if there was no feature with the name.
	 * Calling this method is only valid for dimension demands.
	 * 
	 * @param name The name of the feature to create.
	 * @param comparator The comparator of the feature.
	 * @param min The minimum value of the feature.
	 * @param max The maximum value of the feature.
	 * @return The possibly replaced feature or null if none has been replaced.
	 */
	public IFeatureDemandWriter createFeature(String name, int comparator, Object min, Object max) {
		Contract feature = new Contract(Contract.TYPE_FEATURE_DEMAND, name);
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(comparator));
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_MINIMUM, min);
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_MAXIMUM, max);
		return replaceWriter(feature);
	}

	/**
	 * Removes the contract from the parent. Calling this method is only valid if
	 * the contract represented by this writer can be removed.
	 */
	public void remove() {
		parent.remove(contract);
	}

	/**
	 * Returns the name of the demand contract.
	 * 
	 * @return The name of the demand contract.
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
	public ITypeDemandWriter[] getInterfaces() {
		return getWriters(Contract.TYPE_INTERFACE_DEMAND);
	}

	/**
	 * Returns the interface demand with the specified name. Calling this method is 
	 * only valid for resource and instance demands.
	 * 
	 * @param name The name of the interface to retrieve.
	 * @return The interface demand with the specified name or null if such a demand
	 * 	does not exist.
	 */
	public ITypeDemandWriter getInterface(String name) {
		return getWriter(Contract.TYPE_INTERFACE_DEMAND, name);
	}

	/**
	 * Creates a new interface demand with the specified name and replaces any
	 * existing interface demand with the specified name. Calling this method is
	 * only valid for resource and instance demands.
	 * 
	 * @param name The name of the interface demand to create.
	 * @return The interface demand that has been replaced or null if such a demand
	 * 	does not exist.
	 */
	public ITypeDemandWriter createInterface(String name) {
		Contract iface = new Contract(Contract.TYPE_INTERFACE_DEMAND, name);
		return replaceWriter(iface);
	}

	/**
	 * Returns the event demands of the contract. Calling this method is only
	 * valid for instance demands.
	 * 
	 * @return The events specified by the contract.
	 */
	public ITypeDemandWriter[] getEvents() {
		return getWriters(Contract.TYPE_EVENT_DEMAND);
	}

	/**
	 * Returns the event demand with the specified name. Calling this method is 
	 * only valid for instance demands.
	 * 
	 * @param name The name of the event to retrieve.
	 * @return The event demand with the specified name or null if such a demand
	 * 	does not exist.
	 */
	public ITypeDemandWriter getEvent(String name) {
		return getWriter(Contract.TYPE_EVENT_DEMAND, name);
	}

	/**
	 * Creates a new event demand with the specified name and returns the 
	 * event demand that has been replaced. Calling this method is only valid for
	 * instance demands.
	 * 
	 * @param name The name of the event demand to create.
	 * @return The event demand that has been replaced or null if no such demand
	 * 	exists.
	 */
	public ITypeDemandWriter createEvent(String name) {
		Contract event = new Contract(Contract.TYPE_EVENT_DEMAND, name);
		return replaceWriter(event);
	}

	/**
	 * Returns the dimension demands that are specified by this contract. Calling
	 * this method is only valid for type demands.
	 * 
	 * @return The dimension demands specified by the contract.
	 */
	public IDimensionDemandWriter[] getDimensions() {
		return getWriters(Contract.TYPE_DIMENSION_DEMAND);
	}

	/**
	 * Returns the dimension demand with the specified name. Calling this method
	 * is only valid for type demands.
	 * 
	 * @param name The name of the dimension to retrieve.
	 * @return The dimension demand with the specified name or null if such a
	 * 	dimension demand does not exist.
	 */
	public IDimensionDemandWriter getDimension(String name) {
		return getWriter(Contract.TYPE_DIMENSION_DEMAND, name);
	}

	/**
	 * Creates a new dimension demand with the specified name. Calling this method
	 * is only valid for type demands.
	 * 
	 * @param name The name of the dimension demand to create.
	 * @return The dimension demand that has been replaced or null if no such 
	 * 	demand exists.
	 */
	public IDimensionDemandWriter createDimension(String name) {
		Contract dimension = new Contract(Contract.TYPE_DIMENSION_DEMAND, name);
		return replaceWriter(dimension);
	}

	/**
	 * Implements the remover method so that this demand writer can serve as 
	 * parent for removable demands.
	 * 
	 * @param remove The child contract that requested removal.
	 */
	public void remove(Contract remove) {
		contract.removeContract(remove);
		if (cache != null) {
			cache.remove(remove);
		}
	}
	
	/**
	 * Returns a string representation of the writer's contract.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		return contract.toString();
	}

	
}
