package info.pppc.pcom.system.contract;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.base.system.util.Comparator;
import info.pppc.pcom.system.model.contract.IContract;

/**
 * This class implements that data model for contracts. This data model is used globally
 * by assemblers and containers as well as other entities that might be interested in
 * instance or resource specifications. From within elements of the pcom component model, 
 * contracts are never accessed directly. Instead they are always accessed through views
 * called readers and writers that contraint model changes to those changes that are 
 * allowed within the current context.
 * In order to reduce the number of classes required for the contract model, the contract
 * implements a small and simple type system. Thus, each contract has a certain type. A
 * mapping array defines possible contract combinations. Some contract types may be 
 * enriched with attributes. All contracts have a name that is not null. 
 * 
 * @author Mac
 */
public class Contract implements ISerializable {
	
	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PC";
	
	/**
	 * The contract type for factory templates.
	 */
	public static final byte TYPE_FACTORY_TEMPLATE = 0;
	
	/**
	 * The contract type for instance templates and instance setups.
	 */
	public static final byte TYPE_INSTANCE_TEMPLATE = 1;
	
	/**
	 * The contract type for allocator templates.
	 */
	public static final byte TYPE_ALLOCATOR_TEMPLATE = 2;
	
	/**
	 * The contract type for resource templates and resource setups.
	 */
	public static final byte TYPE_RESOURCE_TEMPLATE = 3; 

	/**
	 * The contract type for factory status contracts.
	 */
	public static final byte TYPE_FACTORY_STATUS = 4;
	
	/**
	 * The contract type for instance status contracts.
	 */
	public static final byte TYPE_INSTANCE_STATUS = 5;
	
	/**
	 * The contract type for resource allocator status contracts.
	 */
	public static final byte TYPE_ALLOCATOR_STATUS = 6;
	
	/**
	 * The contract type for resource status contracts.
	 */
	public static final byte TYPE_RESOURCE_STATUS = 7;
	
	/**
	 * The contract type for dimension demand contracts.
	 */
	public static final byte TYPE_DIMENSION_DEMAND = 8;
	
	/**
	 * The contract type for feature demand contracts.
	 */
	public static final byte TYPE_FEATURE_DEMAND = 9;
	
	/**
	 * The contract type for instance demand contracts.
	 */
	public static final byte TYPE_INSTANCE_DEMAND = 10;

	/**
	 * The contract type for resource demand contracts.
	 */
	public static final byte TYPE_RESOURCE_DEMAND = 11;
	
	/**
	 * The contract type for interface demand contracts.
	 */
	public static final byte TYPE_INTERFACE_DEMAND = 12;
	
	/**
	 * The contract type for event demand contracts.
	 */
	public static final byte TYPE_EVENT_DEMAND = 13;
	
	/**
	 * The contract type for dimension provision contracts.
	 */
	public static final byte TYPE_DIMENSION_PROVISION = 14;
	
	/**
	 * The contract type for feature provision contracts.
	 */
	public static final byte TYPE_FEATURE_PROVISION = 15;
	
	/**
	 * The contract type for instance provision contracts.
	 */
	public static final byte TYPE_INSTANCE_PROVISION = 16;
	
	/**
	 * The contract type for resource provision contracts.
	 */
	public static final byte TYPE_RESOURCE_PROVISION = 17;
	
	/**
	 * The contract type for interface provision contracts.
	 */
	public static final byte TYPE_INTERFACE_PROVISION = 18;
	
	/**
	 * The contract type for event provision contracts.
	 */
	public static final byte TYPE_EVENT_PROVISION = 19;
	
	/**
	 * The attribute type for feature comparators.
	 */
	public static final byte ATTRIBUTE_FEATURE_COMPARATOR = 0;
	
	/**
	 * The attribute type for minimum feature values.
	 */
	public static final byte ATTRIBUTE_FEATURE_MINIMUM = 1;
	
	/**
	 * The attribute type for maximum feature values.
	 */
	public static final byte ATTRIBUTE_FEATURE_MAXIMUM = 2;
	
	/**
	 * The attribute type for feature values.
	 */
	public static final byte ATTRIBUTE_FEATURE_VALUE = 3;
	
	/**
	 * The attribute type for dynamic feature provisions.
	 */
	public static final byte ATTRIBUTE_FEATURE_DYNAMIC = 4;
	
	/**
	 * The attribute type for resource estimates.
	 */
	public static final byte ATTRIBUTE_RESOURCE_ESTIMATE = 5;
	
	/**
	 * The mapping table between contract types and child contract types.
	 * The first index denotes the type. The array bound to that index
	 * denotes the children supported by this type. A null array denotes
	 * that no children types at all are supported.
	 */
	private static final byte[][] CONTRACT_MAPPING = new byte[20][];
	
	/**
	 * The mapping table between contract types and supported attributes.
	 * The first index denotes the type. The array bound to that index
	 * denotes the attribute types that are supported by this contract 
	 * type. A null array denotes that no attributes are supported.
	 */
	private static final byte[][] ATTRIBUTE_MAPPING = new byte[20][];
	
	/**
	 * The static initializer initalizes the contract and attribute 
	 * mapping. Currently, attributes are only supported in feature
	 * contracts. Othewise, the hierarchy always follows the 
	 * base->resource/instance->interface/event->dimension->feature 
	 * structure where base denotes a template or status object.
	 */
	static {
		// feature demands may have values, minimum, maximum and comparators
		ATTRIBUTE_MAPPING[TYPE_FEATURE_DEMAND] = new byte[] {
				ATTRIBUTE_FEATURE_COMPARATOR, 
				ATTRIBUTE_FEATURE_VALUE, 
				ATTRIBUTE_FEATURE_MAXIMUM, 
				ATTRIBUTE_FEATURE_MINIMUM
		};
		ATTRIBUTE_MAPPING[TYPE_FEATURE_PROVISION] = new byte[] {
				ATTRIBUTE_FEATURE_VALUE,
				ATTRIBUTE_FEATURE_DYNAMIC
		};
		ATTRIBUTE_MAPPING[TYPE_RESOURCE_TEMPLATE] = new byte[] {
				ATTRIBUTE_RESOURCE_ESTIMATE
		};
		// factory templates may have resource demands and instance demands
		CONTRACT_MAPPING[TYPE_FACTORY_TEMPLATE] = new byte[] {
				TYPE_RESOURCE_DEMAND
		};
		CONTRACT_MAPPING[TYPE_INSTANCE_TEMPLATE] = new byte[] {
				TYPE_INSTANCE_PROVISION, TYPE_INSTANCE_DEMAND, TYPE_RESOURCE_DEMAND
		};
		CONTRACT_MAPPING[TYPE_ALLOCATOR_TEMPLATE] = new byte[] {
				TYPE_RESOURCE_DEMAND
		};
		CONTRACT_MAPPING[TYPE_RESOURCE_TEMPLATE] = new byte[] {
				TYPE_RESOURCE_PROVISION, TYPE_RESOURCE_DEMAND	
		};
		CONTRACT_MAPPING[TYPE_FACTORY_STATUS] = new byte[] {
				TYPE_RESOURCE_PROVISION
		};
		CONTRACT_MAPPING[TYPE_INSTANCE_STATUS] = new byte[] {
				TYPE_INSTANCE_DEMAND, TYPE_INSTANCE_PROVISION, TYPE_RESOURCE_PROVISION
		};
		CONTRACT_MAPPING[TYPE_ALLOCATOR_STATUS] = new byte[] {
				TYPE_RESOURCE_PROVISION
		};
		CONTRACT_MAPPING[TYPE_RESOURCE_STATUS] = new byte[] {
				TYPE_RESOURCE_PROVISION, TYPE_RESOURCE_DEMAND
		};
		CONTRACT_MAPPING[TYPE_DIMENSION_DEMAND] = new byte[] {
				TYPE_FEATURE_DEMAND
		};
		CONTRACT_MAPPING[TYPE_INSTANCE_DEMAND] = new byte[] {
				TYPE_INTERFACE_DEMAND, TYPE_EVENT_DEMAND
		};
		CONTRACT_MAPPING[TYPE_RESOURCE_DEMAND] = new byte[] {
				TYPE_INTERFACE_DEMAND
		};
		CONTRACT_MAPPING[TYPE_INTERFACE_DEMAND] = new byte[] {
				TYPE_DIMENSION_DEMAND
		};
		CONTRACT_MAPPING[TYPE_EVENT_DEMAND] = new byte[] {
				TYPE_DIMENSION_DEMAND
		};
		CONTRACT_MAPPING[TYPE_DIMENSION_PROVISION] = new byte[] {
				TYPE_FEATURE_PROVISION
		};
		CONTRACT_MAPPING[TYPE_INSTANCE_PROVISION] = new byte[] {
				TYPE_INTERFACE_PROVISION, TYPE_EVENT_PROVISION
		};
		CONTRACT_MAPPING[TYPE_RESOURCE_PROVISION] = new byte[] {
				TYPE_INTERFACE_PROVISION
		};
		CONTRACT_MAPPING[TYPE_INTERFACE_PROVISION] = new byte[] {
				TYPE_DIMENSION_PROVISION
		};
		CONTRACT_MAPPING[TYPE_EVENT_PROVISION] = new byte[] {
				TYPE_DIMENSION_PROVISION
		};
	}
	
	/**
	 * This array defines a type to name mapping that can be used for debugging. Using
	 * a contract type as index, this mapping will return the appropriate human-readable
	 * type name for a contract type.
	 */
	public static final String[] TYPE_NAMES = { "FACTORY_TEMPLATE", "INSTANCE_TEMPLATE",
		"RESOURCE_TEMPLATE", "ASSIGNMENT_TEMPLATE", "FACTORY_STATUS", "INSTANCE_STATUS",
		"RESOURCE_STATUS", "ASSIGNMENT_STATUS", "DIMENSION_DEMAND", "FEATURE_DEMAND",
		"INSTANCE_DEMAND", "RESOURCE_DEMAND", "INTERFACE_DEMAND", "EVENT_DEMAND",
		"DIMENSION_PROVISION", "FEATURE_PROVISION", "INSTANCE_PROVISION", 
		"RESOURCE_PROVISON", "INTERFACE_PROVISION", "EVENT_PROVISION"
	};
	
	/**
	 * This array defines an attribute to name mapping that can be used for debugging.
	 * Using an attribute type as index, this mapping will return the appropriate 
	 * human-readable name for an attribute.
	 */
	public static final String[] ATTRIBUTE_NAMES = { "FEATURE_COMPARATOR", 
		"FEATURE_MINIMUM", "FEATURE_MAXIMUM", "FEATURE_VALUE", "FEATURE_DYNAMIC", 
		"RESOURCE_ESTIMATE"
	};
	
	/**
	 * The type of the contract as defined by one of the type constants.
	 */
	private byte type;
	
	/**
	 * The name of the contract. This must not be null.
	 */
	private String name;
	
	/**
	 * A vector that might contain the children of the contract. This
	 * vector is initalized lazily in order to safe space.
	 */
	private Vector contracts;

	/**
	 * A hashtable that might contain attributes of the contract. This
	 * hashtable is initialized lazily in order to safe space.
	 */
	private Hashtable attributes;
	
	/**
	 * Creates a new and invalid contract. This constructor is solely 
	 * indended for deserialization purposes. It should never be called
	 * by user code.
	 */
	public Contract() {
		super();
	}
	
	/**
	 * Creates a new contract with the specified type and name. The type
	 * must be one of the type constants definded in this class. The name
	 * must not be null.
	 * 
	 * @param type The type of the contract.
	 * @param name The name of the contract.
	 * @throws IllegalArgumentException Thrown if the type is not one of
	 * 	the type constants defined in this class.
	 * @throws NullPointerException Thrown if the name is null.
	 */
	public Contract(byte type, String name) {
		if (type < 0 || type >= CONTRACT_MAPPING.length) 
			throw new IllegalArgumentException("Illegal contract type.");
		if (name == null) 
			throw new NullPointerException("Illegal contract name.");
		this.type = type;
		this.name = name;
		init();
	}
	
	/**
	 * Initializes the contracts storage depending on its type. If the
	 * contract may not have child contracts, its children vector is
	 * not initialized, if it may not have attributes, its attribute 
	 * table is not initialized.
	 */
	private void init() {
		if (CONTRACT_MAPPING[type] != null) {
			contracts = new Vector();
		}
		if (ATTRIBUTE_MAPPING[type] != null) {
			attributes = new Hashtable();
		}
	}

	/**
	 * Determines whether a certain attribute type is supported by this
	 * contract type.
	 * 
	 * @param attribute The attribute type to check.
	 * @throws IllegalArgumentException Thrown if this contract does not
	 * 	support the type.
	 */
	private void checkAttribute(byte attribute) {
		byte[] attributes = ATTRIBUTE_MAPPING[type];
		if (attributes == null) {
			attributes = new byte[0];
		}
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] == attribute) {
				return;
			}
		}
		String names = "";
		for (int i = 0; i < attributes.length; i++) {
			names += ATTRIBUTE_NAMES[attributes[i]];
			if (i != attributes.length - 1) names += ", ";
		}
		throw new IllegalArgumentException("Illegal access " + 
				ATTRIBUTE_NAMES[attribute] + " in " + ATTRIBUTE_NAMES[type] + 
				"supported types are (" + names + ")");
	}
	
	/**
	 * Determines whether a certain contract type is supported by
	 * the type of this contract.
	 * 
	 * @param part The type of the child to check.
	 * @throws IllegalArgumentException Thrown if the contract type
	 * 	is not supported by contracts of this type.
	 */
	private void checkContract(byte part) {
		byte[] types = CONTRACT_MAPPING[type];
		if (types == null) {
			types = new byte[0];
		}
		for (int i = 0; i < types.length; i++) {
			if (types[i] == part) {
				return;
			}
		}
		String names = "";
		for (int i = 0; i < types.length; i++) {
			names += TYPE_NAMES[types[i]];
			if (i != types.length - 1) names += ", ";
		}
		throw new IllegalArgumentException("Illegal access " + 
				TYPE_NAMES[part] + " in " + TYPE_NAMES[type] + 
				"supported types are (" + names + ")");
	}

	/**
	 * Returns the type of the contract. Note that the type is immutable.
	 * This will always be one of the contract constants defined in this
	 * class.
	 * 
	 * @return The type of the contract.
	 */
	public byte getType() {
		return type;
	}
	
	/**
	 * Returns the name of the contract. 
	 * 
	 * @return The name of the contract.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the contract.
	 * 
	 * @param name The name of the contract.
	 */
	public void setName(String name) {
		if (name == null) 
			throw new NullPointerException("Illegal contract name.");
		this.name = name;
	}
	
	/**
	 * Returns all attribute types of the attributes specified by
	 * this contract.
	 * 
	 * @return The attribute types of attributes contained in the
	 * 	contract.
	 */
	public byte[] getAttributes() {
		if (attributes == null) return new byte[0];
		byte[] result = new byte[attributes.size()];
		Enumeration e = attributes.keys();
		for (int i = 0; i < result.length; i++) {
			result[i] = ((Byte)e.nextElement()).byteValue();
		}
		return result;
	}
	
	/**
	 * Returns the value of the attribute with the specified type.
	 * 
	 * @param attribute The type of the attribute to retrieve.
	 * @return The value bound to the attribute.
	 * @throws IllegalArgumentException Thrown if the attribute type
	 * 	is not supported by contracts of this type.
	 */
	public Object getAttribute(byte attribute) {
		checkAttribute(attribute);
		return attributes.get(new Byte(attribute));
	}
	
	/**
	 * Sets the attribute of the specified type to the specified
	 * value.
	 * 
	 * @param attribute The attribute type to set.
	 * @param value The value of the attribute.
	 * @return The value that has been replaced or null if none.
	 * @throws IllegalArgumentException Thrown if the attribute type
	 * 	is not supported by contracts of this type.
	 */
	public Object setAttribute(byte attribute, Object value) {
		checkAttribute(attribute);
		return attributes.put(new Byte(attribute), value);
	}
	
	/**
	 * Removes the attribute value of the attribute with the specified
	 * type.
	 * 
	 * @param attribute The attribute to remove.
	 * @return The attribute value that has been removed or null if none.
	 * @throws IllegalArgumentException Thrown if the attribute type
	 * 	is not supported by contracts of this type.
	 */
	public Object removeAttribute(byte attribute) {
		checkAttribute(attribute);
		return attributes.remove(new Byte(attribute));
	}
	
	/**
	 * Adds the specified contract as child to this contract. If this
	 * contract already contains a child with the specified type and
	 * name, the existing contract will be replaced and returned.
	 * 
	 * @param c The contract that should be added.
	 * @return The contract that has been replaced or null if none.
	 * @throws IllegalArgumentException Thrown if the contract type is
	 * 	not supported by contracts of this type.
	 */
	public Contract addContract(Contract c) {
		checkContract(c.getType());
		Contract replaced = null;
		for (int i = 0; i < contracts.size(); i++) {
			Contract m = (Contract)contracts.elementAt(i);
			if (m.getType() == c.getType() && m.getName().equals(c.getName())) {
				replaced = m;
				contracts.removeElementAt(i);
				break;
			}
		}
		contracts.addElement(c);
		return replaced;
	}
	
	/**
	 * Removes and returns the child contract with the specified type
	 * and name.
	 * 
	 * @param type The type of the contract to remove.
	 * @param name The name of the contract to remove.
	 * @return The contract that has been removed, or null if none.
	 * @throws IllegalArgumentException Thrown if the contract type is
	 * 	not supported by contracts of this type.
	 */
	public Contract removeContract(byte type, String name) {
		checkContract(type);
		for (int i = 0; i < contracts.size(); i++) {
			Contract m = (Contract)contracts.elementAt(i);
			if (m.getType() == type && name.equals(m.getName())) {
				contracts.removeElementAt(i);
				return m;
			}
		}
		return null;
	}

	/**
	 * Removes the specified child contract and returns true if
	 * the contract was a child and false if the contract was not
	 * a child.
	 * 
	 * @param contract The contract that should be removed.
	 * @return True if the contract was a child and has been removed,
	 * 	false otherwise.
	 * @throws IllegalArgumentException Thrown if the contract type is
	 * 	not supported by contracts of this type.
	 */
	public boolean removeContract(Contract contract) {
		checkContract(contract.getType());
		return contracts.removeElement(contract);
	}
	
	
	/**
	 * Returns the child contract with the specified type and name.
	 * 
	 * @param type The type of the child contract to retrieve.
	 * @param name The name of the child contract to retrieve.
	 * @return The child contract with the specified type and name or
	 * 	null if such a child does not exist.
	 * @throws IllegalArgumentException Thrown if the contract type is
	 * 	not supported by contracts of this type.
	 */
	public Contract getContract(byte type, String name) {
		checkContract(type);
		for (int i = 0; i < contracts.size(); i++) {
			Contract m = (Contract)contracts.elementAt(i);
			if (m.getType() == type && name.equals(m.getName())) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Returns all child contracts that have the specified type.
	 * 
	 * @param type The type of the child contracts to retrieve.
	 * @return An array that contains all child contracts with the
	 * 	specified type. This will never be null.
	 * @throws IllegalArgumentException Thrown if the contract type is
	 * 	not supported by contracts of this type.
	 */
	public Contract[] getContracts(byte type) {
		checkContract(type);
		Vector result = new Vector();
		for (int i = 0; i < contracts.size(); i++) {
			Contract c = (Contract)contracts.elementAt(i);
			if (c.getType() == type) {
				result.addElement(c);
			}
		}
		Contract[] copy = new Contract[result.size()];
		for (int i = 0; i < result.size(); i++) {
			copy[i] = (Contract)result.elementAt(i);
		}
		return copy;
	}
	
	/**
	 * Returns all child contracts that are currently contained in
	 * this contract.
	 * 
	 * @return All child contracts of this contract.
	 */
	public Contract[] getContracts() {
		if (contracts == null) return new Contract[0];
		Contract[] result = new Contract[contracts.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (Contract)contracts.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Returns the first child contract with the specified type. This is
	 * a helper method for child contracts that are only contained once.
	 * 
	 * @param type The type of the contract to retrieve.
	 * @return The first child contract with the specified type.
	 * @throws IllegalArgumentException Thrown if the contract type is
	 * 	not supported by contracts of this type.
	 */
	public Contract getContract(byte type) {
		checkContract(type);
		for (int i = 0; i < contracts.size(); i++) {
			Contract c = (Contract)contracts.elementAt(i);
			if (c.getType() == type) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Deserializes a contract from the specified input stream
	 * 
	 * @param input The input stream to read from.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		// load type and name
		type = input.readByte();
		name = input.readUTF();
		// init class based on mappings
		init();
		// load potential children
		if (contracts != null) {
			int size = input.readInt();
			for (int i = 0; i < size; i++) {
				contracts.addElement(input.readObject());
			}
		}
		// load potential attributes
		if (attributes != null) {
			int size = input.readInt();
			for (int i = 0; i < size; i++) {
				byte key = input.readByte();
				Object value = input.readObject();
				attributes.put(new Byte(key), value);
			}
		}
		
	}
	
	/**
	 * Serializes the contract to the specified output stream.
	 * 
	 * @param output The output stream used for serialization.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		// write type and name
		output.writeByte(type);
		output.writeUTF(name);
		// write potential children
		if (contracts != null) {
			output.writeInt(contracts.size());
			for (int i = 0; i < contracts.size(); i++) {
				output.writeObject(contracts.elementAt(i));
			}
		}
		// write potential attributes
		if (attributes != null) {
			output.writeInt(attributes.size());
			Enumeration e = attributes.keys();
			while (e.hasMoreElements()) {
				Byte key = (Byte)e.nextElement();
				Object value = attributes.get(key);
				output.writeByte(key.byteValue());
				output.writeObject(value);
			}
		}
	}
	
	/**
	 * Determines whether the contract equals another object. If
	 * two contracts are compared, the comparison is based on their
	 * contents, not on their identity.
	 * 
	 * @param object The object to compare with.
	 * @return True if the two objects are the same, false otherwise.
	 */
	public boolean equals(Object object) {
		// if its not a contract, its not the same
		if (object.getClass() != getClass()) return false;
		Contract contract = (Contract)object;
		// if not the same type, its not the same
		if (contract.type != type) return false;
		// if not the same name, its not the same
		if (! contract.name.equals(name)) return false;
		// if not the same attributes, its not the same
		Hashtable as1 = attributes;
		Hashtable as2 = contract.attributes;
		if (as1 == null) as1 = new Hashtable();
		if (as2 == null) as2 = new Hashtable();
		if (as1.size() != as2.size()) return false;
		Enumeration e = as1.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object a1 = as1.get(key);
			Object a2 = as2.get(key);
			if (a1 == null && a2 != null) return false;
			if (a1 != null && a2 == null) return false;
			if (! a1.equals(a2)) return false;
		}
		// if not equal children, its not the same
		Vector cs1 = contracts;
		Vector cs2 = contract.contracts;
		if (cs1 == null) cs1 = new Vector();
		if (cs2 == null) cs2 = new Vector();
		if (cs1.size() != cs2.size()) return false;
		i: for (int i = 0; i < cs1.size(); i++) {
			Contract c1 = (Contract)cs1.elementAt(i);
			for (int j = 0; j < cs2.size(); j++) {
				Contract c2 = (Contract)cs2.elementAt(j);
				if (c1.equals(c2)) continue i;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Returns a content-based hashcode. Note that the compuatation
	 * of this hashcode is rather heavy-weight as it is not
	 * incremental. 
	 * 
	 * @return A content-based hashcode.
	 */
	public int hashCode() {
		int hashCode = name.hashCode() + type;
		if (contracts != null) {
			for (int i = 0; i < contracts.size(); i++) {
				hashCode += contracts.elementAt(i).hashCode();
			}			
		}
		if (attributes != null) {
			Enumeration e = attributes.keys();
			while (e.hasMoreElements()) {
				Object key = e.nextElement();
				hashCode += (key.hashCode() << 6);
				Object value = attributes.get(key);
				if (value != null) {
					hashCode += value.hashCode();
				}
			}
		}
		return hashCode;
	}
	
	/**
	 * Determines whether a contract matches another contract.
	 * A matching method can only be called on a provision 
	 * contract. The parameter passed to the method must be a
	 * demand contract of the corresponding type. If the caller
	 * does not adhere to this, the method will simply return
	 * false.
	 * 
	 * @param contract The contract to compare to (this must
	 * 	be a demand of a type that corresponds to this contract's
	 * 	type).
	 * @param allowDynamic A flag that indicates whether dynamic features
	 * 	should be considered as match. If true, dynamic features
	 * 	are not compared.
	 * @return True if this contract matches (fulfills) the 
	 * 	passed demand. False if it does not match or if this
	 * 	contract or the passed contract have an invalid type.
	 */
	public boolean matches(Contract contract, boolean allowDynamic) {
		if (contract == null) return false;
		switch (type) {
			case TYPE_INSTANCE_PROVISION:
				if (contract.type == TYPE_INSTANCE_DEMAND) {
					return matchesContracts(contract, allowDynamic);
				}
				break;
			case TYPE_RESOURCE_PROVISION:
				if (contract.type == TYPE_RESOURCE_DEMAND) {
					return matchesContracts(contract, allowDynamic);
				}
				break;
			case TYPE_INTERFACE_PROVISION:
				if (contract.type == TYPE_INTERFACE_DEMAND) {
					if (! name.equals(contract.getName())) return false;
					return matchesContracts(contract, allowDynamic);
				}
				break;
			case TYPE_EVENT_PROVISION:
				if (contract.type == TYPE_EVENT_DEMAND) {
					if (! name.equals(contract.getName())) return false;
					return matchesContracts(contract, allowDynamic);
				}
				break;
			case TYPE_DIMENSION_PROVISION:
				if (contract.type == TYPE_DIMENSION_DEMAND) {
					if (! name.equals(contract.getName())) return false;
					return matchesContracts(contract, allowDynamic);
				}
				break;
			case TYPE_FEATURE_PROVISION:
				if (contract.type == TYPE_FEATURE_DEMAND) {
					if (! name.equals(contract.getName())) return false;
					return matchesAttributes(contract, allowDynamic);
				}
				break;
			default:
				// return false, incompatible types
		}
		return false;
	}
	
	/**
	 * Returns a deep copy of the contract. Use careful as this might
	 * fill up the memory quickly.
	 * 
	 * @return A deep copy of the contract.
	 */
	public Contract copy() {
		Contract copy = new Contract(type, name);
		if (contracts != null) {
			copy.contracts = new Vector();
			for (int i = 0; i < contracts.size(); i++) {
				// deep copy recursively
				copy.contracts.addElement
					(((Contract)contracts.elementAt(i)).copy());
			}
		}
		if (attributes != null) {
			copy.attributes = new Hashtable();
			Enumeration e = attributes.keys();
			while (e.hasMoreElements()) {
				Object key = e.nextElement();
				Object value = attributes.get(key);
				copy.attributes.put(key, value);
			}
		}
		return copy;
	}

	/**
	 * Determines whether the children of the passed contract can
	 * be matched with the children of this contract.
	 * 
	 * @param contract The contract whose children needs to 
	 * 	be matched with the children of this contract.
	 * @param allowDynamic A boolean flag that indicates whether
	 * 	dynamic attributes are considered to be a match.
	 * @return True if the children of the passed contract can
	 * 	be matched with children of this contract.
	 */
	private boolean matchesContracts(Contract contract, boolean allowDynamic) {
		Vector dcs = contract.contracts;
		Vector pcs = contracts;
		if (dcs == null) dcs = new Vector();
		if (pcs == null) pcs = new Vector();
		i: for (int i = 0; i < dcs.size(); i++) {
			Contract dc = (Contract)dcs.elementAt(i);
			for (int j = 0; j < pcs.size(); j++) {
				Contract pc = (Contract)pcs.elementAt(j);
				if (pc.matches(dc, allowDynamic)) {
					continue i;
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Determines whether the passed feature demand can be
	 * matched with the attributes of this contract.
	 * 
	 * @param contract The feature demand to match.
	 * @param allowDynamic A flag that indicates whether dynamic
	 * 	attributes should be considered to be a match.
	 * @return True if the feature demand can be matched
	 * 	with this contract.
	 */
	private boolean matchesAttributes(Contract contract, boolean allowDynamic) {
		Boolean pdyn = (Boolean)getAttribute(ATTRIBUTE_FEATURE_DYNAMIC);
		if (pdyn == null) return false; // dynamic flag must be set
		if (pdyn.booleanValue()) {
			return allowDynamic;
		}
		// if attributes not set in demand, demand cannot be fulfilled
		if (contract.attributes == null) return false;
		// if comparator not set, demand cannot be fulfilled
		Integer dcomp = (Integer)contract.getAttribute(ATTRIBUTE_FEATURE_COMPARATOR);
		if (dcomp == null) return false;
		// retrieve values for comparison
		Object dmin = contract.getAttribute(ATTRIBUTE_FEATURE_MINIMUM);
		Object dmax = contract.getAttribute(ATTRIBUTE_FEATURE_MAXIMUM);
		Object dval = contract.getAttribute(ATTRIBUTE_FEATURE_VALUE);
		Object pval = getAttribute(ATTRIBUTE_FEATURE_VALUE);
		// compare depending on comparator type
		switch (dcomp.intValue()) {
			case IContract.IFEQ:
				if (dval == null) return false;
				else return (dval.equals(pval));
			case IContract.IFGE:
				if (dval == null) return false;
				else return Comparator.isMoreOrEqual(pval, dval);
			case IContract.IFGT:
				if (dval == null) return false;
				else return Comparator.isMore(pval, dval);
			case IContract.IFLE:
				if (dval == null) return false;
				else return Comparator.isLessOrEqual(pval, dval);
			case IContract.IFLT:
				if (dval == null) return false;
				else return Comparator.isLess(pval, dval);
			case IContract.IFIR:
				if (dmin == null || dmax == null) return false;
				else return Comparator.isMoreOrEqual(pval, dmin) 
					&& Comparator.isLessOrEqual(pval, dmax);
			case IContract.IFOR:
				if (dmin == null || dmax == null) return false;
				else return Comparator.isLess(pval, dmin) 
					|| Comparator.isMore(pval, dmax);
			default:
		}
		return false;
	}
	
	/**
	 * Returns a human readable string representation.
	 * 
	 * @return A human readable string representation.
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		toString(b);
		return b.toString();
	}

	/**
	 * Creates a recursive string representation using one string
	 * buffer only.
	 * 
	 * @param b The buffer used to create the representation.
	 */
	private void toString(StringBuffer b) {
		b.append("TYPE (");
		b.append(TYPE_NAMES[type]);
		b.append(") NAME (");
		b.append(name);
		b.append(")");
		if (contracts != null && contracts.size() > 0) {
			b.append(" CONTRACTS (");
			for (int i = 0; i < contracts.size(); i++) {
				b.append(contracts.elementAt(i));
				if (i != contracts.size() - 1) {
					b.append(", ");
				}
			}			
			b.append(")");
		}
		if (attributes != null && attributes.size() > 0) {
			b.append(" ATTRIBUTES (");
			Enumeration e = attributes.keys();
			for (int i = 0; i < attributes.size(); i++) {
				Byte attribute = (Byte)e.nextElement();
				Object value = getAttribute(attribute.byteValue());
				b.append(ATTRIBUTE_NAMES[(attribute.byteValue() & 0xff)] + "=" + value);
				if (i != attributes.size() - 1) {
					b.append(", ");
				}
			}			
			b.append(")");
		}
	}
}
