package info.pppc.pcom.swtui.container;

/**
 * This inteface defines the tree structure of the container model. The
 * tree will be structured as follows:
 * 
 * NULL
 * -FACTORIES
 * -- FACTORY
 * --- FACTORY_IDENTIFIER
 * --- FACTORY_NAME
 * --- FACTORY_TEMPLATE
 * --- FACTORY_STATUS
 * --- INSTANCES
 * ---- INSTANCE
 * ------ INSTANCE_IDENTIFIER
 * ------ INSTANCE_NAME
 * ------ INSTANCE_TEMPLATE
 * ------ INSTANCE_STATUS
 * - ALLOCATORS
 * -- ALLOCATOR
 * --- ALLOCATOR_IDENTIFIER
 * --- ALLOCATOR_NAME
 * --- ALLOCATOR_TOTAL
 * --- ALLOCATOR_FREE
 * --- ALLOCATOR_TEMPLATE
 * --- ALLOCATOR_FREE
 * --- RESOURCES
 * ---- RESOURCE
 * ----- RESOURCE_IDENTIFIER
 * ----- RESOURCE_NAME
 * ----- RESOURCE_USE
 * ----- RESOURCE_TEMPLATE
 * ----- RESOURCE_STATUS
 * 
 * The nodes containing contracts will contain subnotes of type
 * CONTRACT and ATTRIBUTE
 * 
 * @author Mac
 */
public interface IContainerModel {

	/**
	 * The exception type denotes an invocation exception. The data object
	 * will be the exception that has been caught.
	 */
	public static final short TYPE_EXCEPTION = -1;

	/**
	 * The null type denotes the root node. The data object will be the
	 * system id of the container.
	 */
	public static final short TYPE_NULL = 0;
	
	/**
	 * The factories node for the device. The data object of this node
	 * will be null.
	 */
	public static final short TYPE_FACTORIES = 1;
	
	/**
	 * The factory type denotes a factory. The data object will contain
	 * null.
	 */
	public static final short TYPE_FACTORY = 2;
	
	/**
	 * The identifier of the factory. The data object will contain the
	 * object id of the factory.
	 */
	public static final short TYPE_FACTORY_ID = 3;
	
	/**
	 * The name of the factory. The data object will contain the name
	 * of the factory.
	 */
	public static final short TYPE_FACTORY_NAME = 4;

	/**
	 * The name of the factory template. The data object will contain
	 * the contract.
	 */
	public static final short TYPE_FACTORY_TEMPLATE = 5;
	
	/**
	 * The name of the factory status. The data object will contain
	 * the contract.
	 */
	public static final short TYPE_FACTORY_STATUS = 6;
	
	/**
	 * The instances type denotes instances. The data object will be the
	 * id of the factory.
	 */
	public static final short TYPE_INSTANCES = 7;

	/**
	 * The instance type denotes an instance. The data object will be null.
	 */
	public static final short TYPE_INSTANCE = 8;
	
	/**
	 * The instance id type. The data object will be the id of some instance.
	 */
	public static final short TYPE_INSTANCE_ID = 9;
	
	/**
	 * The instance name type. The data object will be the name of some instance.
	 */
	public static final short TYPE_INSTANCE_NAME = 10;
	
	/**
	 * The instance template type. The data object will be the template of the
	 * instance as contract.
	 */
	public static final short TYPE_INSTANCE_TEMPLATE = 11;
	
	/**
	 * The instance status type. The data object will be the status of the
	 * instance as contract.
	 */
	public static final short TYPE_INSTANCE_STATUS = 12;
	
	/**
	 * The installed allocators of the container. The data object will
	 * be null.
	 */
	public static final short TYPE_ALLOCATORS = 13;

	/**
	 * A specific type of allocator on the container. The data object will
	 * be a string that denotes the type.
	 */
	public static final short TYPE_ALLOCATOR = 14;
	
	/**
	 * The allocator id type. The data object will be the object id of 
	 * some allocator.
	 */
	public static final short TYPE_ALLOCATOR_ID = 15;
	
	/**
	 * The allocator name type. The data object will be the name of some
	 * allocator.
	 */
	public static final short TYPE_ALLOCATOR_NAME = 16;
	
	/**
	 * The allocator total type. The data object will be null or an integer
	 * array that contains the amount of total resources.
	 */
	public static final short TYPE_ALLOCATOR_TOTAL = 17;
	
	/**
	 * The allocator free type. The data object will be null or an integer
	 * array that contains the amount of free resources.
	 */
	public static final short TYPE_ALLOCATOR_FREE = 18;
	
	/**
	 * The type for allocator templates. The data object will be a contract.
	 */
	public static final short TYPE_ALLOCATOR_TEMPLATE = 19;
	
	/**
	 * The type for allocator status. The data object will be a contract.
	 */
	public static final short TYPE_ALLOCATOR_STATUS = 20;
	
	/**
	 * The resources type that denotes the resources of some allocator.
	 * The data object will be the id of the allocator.
	 */
	public static final short TYPE_RESOURCES = 21;
	
	/**
	 * The resource type that denotes a specific resource. The data object
	 * will be null.
	 */
	public static final short TYPE_RESOURCE = 22;
	
	/**
	 * The resource id type that denotes a specific resource id. The data
	 * object will contain the object id of the resource. 
	 */
	public static final short TYPE_RESOURCE_ID = 23;
	
	/**
	 * The resource name type. The data object will contain a string that
	 * describes the resource.
	 */
	public static final short TYPE_RESOURCE_NAME = 24;
	
	/**
	 * The resource usage of the resource as estimated by its parent.
	 */
	public static final short TYPE_RESOURCE_USE = 25;
	
	/**
	 * The resource template type. The data object will contain the template
	 * of the resource as string.
	 */
	public static final short TYPE_RESOURCE_TEMPLATE = 26;
	
	/**
	 * The resource status type. The data object will contain the status
	 * of the resource as string.
	 */
	public static final short TYPE_RESOURCE_STATUS = 27;
	
	/**
	 * The type that is used to describe a contract fragment. The data
	 * object will contain a contract.
	 */
	public static final short TYPE_CONTRACT = 28;
	
	/**
	 * The type that is used to describe a contract attribute. The data
	 * object will contain a byte that identifies the attribute type.
	 */
	public static final short TYPE_CONTRACT_ATTRIBUTE = 29;
}
