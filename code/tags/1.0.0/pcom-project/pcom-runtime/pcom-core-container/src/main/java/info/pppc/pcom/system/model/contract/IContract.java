package info.pppc.pcom.system.model.contract;

/**
 * The contract is the base interface for all pcom contract specifications. 
 * Contract specifications are either readable or writable and readable. 
 * Contract specifications form a tree of contract specifications, starting
 * from templates and status objects, to resource and instance contracts, over 
 * type contracts to dimension contracts and feature contracts. The exact
 * structure and whether a instance, factory or resource has read/write access
 * depends on the base specification. 
 * 
 * @author Mac
 */
public interface IContract {

	/**
	 * The constant that denotes the greater than comparator. This comparator 
	 * evaluates true if the value of the corresponding feature provision is 
	 * larger than the value of the feature demand.
	 */
	public static final int IFGT = 0;
	
	/**
	 * The constant that denotes the greater than or equal comparator. This 
	 * comparator evaluates true if the value of the corresponding feature
	 * provision is larger than or equal to the value of the feature demand.
	 */
	public static final int IFGE = 1;
	
	/**
	 * The constant that denotes the equal to comparator. This comparator 
	 * evaluates true if the value of the corresponding feature provision is
	 * equal to the value of the feature demand.
	 */
	public static final int IFEQ = 2;
	
	/**
	 * The constant that denotes the less than or equal comparator. This
	 * comparator evaluates ture if the value of the corresponding feature
	 * provision is less than or equal to the value of the feature demand.
	 */
	public static final int IFLE = 3;
	
	/**
	 * The constant that denotes the less than comparator. This comparator
	 * evaluates true if the value of the corresponding feature provision
	 * is less than the value of the feature demand.
	 */
	public static final int IFLT = 4;
	
	/**
	 * The constant that denotes the in range comparator. This comparator
	 * evaluates true if the value of the corresponding feature provision
	 * lies within the value of the maximum and minimum specified by the
	 * feature demand.
	 */
	public static final int IFIR = 5;
	
	/**
	 * The constant that denotes the out range comparator. This comparator
	 * evaluates true if the value of the corresponding feature provision
	 * does not lie within the value of the maximum and minimum specified 
	 * by the feature demand.
	 */
	public static final int IFOR = 6;

	/**
	 * The constant that denotes the boolean type. This constant is used
	 * to specify the type of a feature during modification operations.
	 * It denotes an object of type java.lang.Boolean.
	 */
	public static final int TBOL = 0;

	/**
	 * The constant that denotes the integer type. This constant is used
	 * to specify the type of a feature during modification operations.
	 * It denotes an object of type java.lang.Integer.
	 */	
	public static final int TINT = 1;
	
	/**
	 * The constant that denotes the long type. This constant is used
	 * to specify the type of a feature during modification operations.
	 * It denotes an object of type java.lang.Long.
	 */
	public static final int TLNG = 2;
	
	/**
	 * The constant that denotes the string type. This constant is used
	 * to specify the type of a feature during modification operations.
	 * It denotes an object of type java.lang.String.
	 */
	public static final int TSTR = 3;
	
	/**
	 * Returns the name of the contract. This name is used for matching
	 * contracts. Depending on the concrete contract type, this attribute
	 * has different meanings.
	 * 
	 * @return The name of the contract.
	 */
	public String getName();
	
}
