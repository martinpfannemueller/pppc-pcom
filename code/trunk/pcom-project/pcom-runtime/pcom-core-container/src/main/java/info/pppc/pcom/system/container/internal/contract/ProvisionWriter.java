package info.pppc.pcom.system.container.internal.contract;

import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IFeatureProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;

import java.util.Hashtable;

/**
 * The provision writer provides a generic writer view for all provision contracts. Thus
 * it implements all provision writer types that can be accessed by instances, factories
 * allocators and resources.
 * 
 * @author Mac
 */
public class ProvisionWriter implements IDimensionProvisionWriter,
		IFeatureProvisionWriter, IInstanceProvisionWriter,
		IResourceProvisionWriter, ITypeProvisionWriter, IRemover {

	/**
	 * A cache that is used to cache the writers created by this provision writer.
	 */
	private Hashtable cache;  
	
	/**
	 * The parent remover that is used to remove this contract from the parent.
	 */
	private IRemover parent;
		
	/**
	 * The contract that is managed by this provision writer.
	 */
	private Contract contract;
	
	/**
	 * Creates a new provision writer with the specified parent for removal
	 * operations and the specified contract to retrieve values.
	 * 
	 * @param parent The parent used for removals.
	 * @param contract The contract that is managed by the writer.
	 */
	public ProvisionWriter(IRemover parent, Contract contract) {
		this.contract = contract;
		this.parent = parent;
	}
	
	/**
	 * Returns the writer for the specified contract. If the writer has been
	 * created already, this method will return the cached writer.
	 * 
	 * @param mod The contract whose writer should be retrieved.
	 * @return The provision writer for the specified contract or null if
	 * 	the contract was null already in the first place.
	 */
	private ProvisionWriter getWriter(Contract mod) {
		ProvisionWriter result = null;
		if (mod != null) {
			if (cache == null) {
				cache = new Hashtable();
			}
			result = (ProvisionWriter)cache.get(mod);
			if (result == null) {
				result = new ProvisionWriter(this, mod);
				cache.put(mod, result);
			}	
		}
		return result;
	}
	
	/**
	 * Returns the provision writer for the child contract with the specified
	 * name and type or null if such a child does not exist.
	 * 
	 * @param type The type of the child to retrieve.
	 * @param name The name of the child to retrieve.
	 * @return The writer for the child or null if the child does not exist.
	 */
	private ProvisionWriter getWriter(byte type, String name) {
		return getWriter(contract.getContract(type, name));
	}
	
	/**
	 * Returns the contract writers for the child contracts with the specified
	 * type.
	 * 
	 * @param type The type of the children to retrieve.
	 * @return The children writers of all children with the specified type.
	 */
	private ProvisionWriter[] getWriters(byte type) {
		Contract[] contracts = contract.getContracts(type);
		ProvisionWriter[] writers = new ProvisionWriter[contracts.length];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = getWriter(contracts[i]);
		}
		return writers;
	}
	
	/**
	 * Adds a contract to the base contract, removes and returns the possibly 
	 * replaced writer.
	 * 
	 * @param mod The contract that should be added to the base contract.
	 * @return The provision writer for the replaced contract or null if none
	 * 	has been replaced.
	 */
	private ProvisionWriter replaceWriter(Contract mod) {
		Contract replaced = contract.addContract(mod);
		ProvisionWriter writer = getWriter(replaced);
		if (cache != null && replaced != null) {
			cache.remove(replaced);	
		}
		return writer;
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
	public IFeatureProvisionWriter[] getFeatures() {
		return getWriters(Contract.TYPE_FEATURE_PROVISION);
	}

	/**
	 * Returns the feature contract with the specified name. Calling this 
	 * method is only valid for dimension contracts.
	 * 
	 * @param name The name of the feature provision to retrieve.
	 * @return The feature provision contract with the specified name or null
	 * 	if no such contract exists.
	 */
	public IFeatureProvisionWriter getFeature(String name) {
		return getWriter(Contract.TYPE_FEATURE_PROVISION, name);
	}

	/**
	 * Creates a new dynamic feature provision in the demand with the specified
	 * name. Calling this method is only valid for dimension contracts.
	 * 
	 * @param name The name of the feature to add.
	 * @return The feature provision that has been replaced through the addition
	 * 	or null if no such feature provision exists.
	 */
	public IFeatureProvisionWriter createFeature(String name) {
		Contract feature = new Contract(Contract.TYPE_FEATURE_PROVISION, name);
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_DYNAMIC, new Boolean(true));
		return replaceWriter(feature);
	}

	/**
	 * Determines whether the feature is dynamic. Calling this method is only
	 * valid for feature provisions.
	 * 
	 * @return True if the feature is dynamic, false otherwise.
	 */
	public boolean isDynamic() {
		return ((Boolean)contract.getAttribute(Contract.ATTRIBUTE_FEATURE_DYNAMIC)).booleanValue();
	}
	
	
	/**
	 * Creates a feature provision with the specified name and value object
	 * attribute. Calling this method is only valid for dimension contracts.
	 * 
	 * @param name The name of the feature to add.
	 * @param value The value attribute of the feature provision.
	 * @return The feature provision that has been replaced through the addition
	 * 	or null if no such feature provision exists.
	 */
	public IFeatureProvisionWriter createFeature(String name, Object value) {
		Contract feature = new Contract(Contract.TYPE_FEATURE_PROVISION, name);
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, value);
		feature.setAttribute(Contract.ATTRIBUTE_FEATURE_DYNAMIC, new Boolean(false));
		return replaceWriter(feature);
	}
	

	/**
	 * Removes the contract from the parent contract. Calling this method
	 * is only allowed for writers that can be removed.
	 */
	public void remove() {
		parent.remove(contract);
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
	 * Returns the interface provisions specified by this contract. Calling this
	 * method is only valid for instance and resource provisions.
	 * 
	 * @return The interface provisisions specified by this provision contract.
	 */
	public ITypeProvisionWriter[] getInterfaces() {
		return getWriters(Contract.TYPE_INTERFACE_PROVISION);
	}

	/**
	 * Returns the interface provision with the specified name. Calling this method
	 * is only valid for instance and resource provisions.
	 * 
	 * @param name The name of the interfac provision to retrieve.
	 * @return The interface provision with the specified name or null if such a
	 * 	provision does not exist.
	 */
	public ITypeProvisionWriter getInterface(String name) {
		return getWriter(Contract.TYPE_INTERFACE_PROVISION, name);
	}

	/**
	 * Creates an interface provision with the specified name. Calling this method
	 * is only valid for instance and resource provisions.
	 * 
	 * @param name The name of the interface provision to create.
	 * @return The interface provision that has been replaced by the operation. This
	 * 	will be null if none has been replaced.
	 */
	public ITypeProvisionWriter createInterface(String name) {
		Contract iface = new Contract(Contract.TYPE_INTERFACE_PROVISION, name);
		return replaceWriter(iface);
	}

	/**
	 * Returns the events specified by this provision. Calling this method is
	 * only valid for instance provisions.
	 * 
	 * @return The events psecified by this instance provision.
	 */
	public ITypeProvisionWriter[] getEvents() {
		return getWriters(Contract.TYPE_EVENT_PROVISION);
	}

	/**
	 * Returns the event provision with the specified name. Calling this method
	 * is only valid for instance provisions.
	 * 
	 * @param name The name of the event provision to retrieve.
	 * @return The event provision with the specified name or null if it does
	 * 	not exist.
	 */
	public ITypeProvisionWriter getEvent(String name) {
		return getWriter(Contract.TYPE_EVENT_PROVISION, name);
	}

	/**
	 * Creates an event provision with the specified name. Calling this method
	 * is only valid for instance provisions.
	 * 
	 * @param name The name of the event provision to create.
	 * @return The event provision that has been replaced or null if none
	 * 	has been replaced.
	 */
	public ITypeProvisionWriter createEvent(String name) {
		Contract event = new Contract(Contract.TYPE_EVENT_PROVISION, name);
		return replaceWriter(event);
	}

	/**
	 * Returns the dimension provisions that are specified by this provision.
	 * Calling this method is only valid for type provisions.
	 * 
	 * @return The event provisions that are specified by this provision.
	 */
	public IDimensionProvisionWriter[] getDimensions() {
		return getWriters(Contract.TYPE_DIMENSION_PROVISION);
	}

	/**
	 * Returns the dimension provision with the specified name. Calling this
	 * method is only valid for type provisions.
	 * 
	 * @param name The name of the dimension provision to retrieve.
	 * @return The dimension provision with the specified name or null if it
	 * 	does not exist.
	 */
	public IDimensionProvisionWriter getDimension(String name) {
		return getWriter(Contract.TYPE_DIMENSION_PROVISION, name);
	}
	
	/**
	 * Creates a dimension provision with the specified name. Calling this
	 * method is only valid for type provisions.
	 * 
	 * @param name The name of the dimension that should be created.
	 * @return The dimension provision writer that has been replaced or null
	 * 	if none has been replaced.
	 */
	public IDimensionProvisionWriter createDimension(String name) {
		Contract dimension = new Contract(Contract.TYPE_DIMENSION_PROVISION, name);
		return replaceWriter(dimension);
	}

	/**
	 * Removes the specified child from the contract. This method
	 * will do nothing if the contract is not a child of this contract.
	 * 
	 * @param remove The contract to remove.
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
