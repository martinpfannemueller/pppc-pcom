package info.pppc.pcom.system.container;

import java.util.Hashtable;
import java.util.Vector;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;

/**
 * The container remote interface defines the basic functionality of a container 
 * that is accessible from remote. As each container is capable of starting and 
 * controlling instances as well as using instances from a remote container, it 
 * implements the predecessor interface and the successor interface. Furthermore,
 * each container provides methods that enable the configuration algorithms to 
 * perform their work. This includes methods to retrieve the available setups
 * from factories as well as methods to retrieve information about the resource
 * status of the container.
 * 
 * As the container acts as a kind of remote registry, this functionality is 
 * implemented as well known service. This enables the algorithms to perform
 * instance and resource negotiations without performing additonal lookups to
 * retrieve the containers available in the environment.
 * 
 * @author Mac
 */
public interface IContainer extends IContainerDemander, IContainerProvider {

	/**
	 * The well known object id of all remote containers. The id of a 
	 * pcom remote container defaults to 3.
	 */
	public static final ObjectID CONTAINER_ID = new ObjectID(3);

// assembler-container interface, used by the assembler to retrieve contracts and resources

	/**
	 * Returns the contracts that could be created by factories or allocators
	 * in response to a certain demand. The input to this method is a vector
	 * of resource demand or instance demand contracts. The result will be
	 * a hashtable of hashtables of vector. This structure will hash the
	 * contract that was sent as input value to a hashtable of creatorIDs to
	 * a vector of contracts.
	 * 
	 * @param demands A vector that must only contain instance or resource demand
	 * 	contracts.
	 * @return A hashtable of hashtable of vector, that hashes input contracts
	 * 	to creators to a vector of contracts.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Hashtable getTemplates(Vector demands) throws InvocationException;
	
	/**
	 * Returns the available resources of the device hashed by allocator 
	 * identifier. This will return a hashtable of integer arrays. The 
	 * integer arrays will denote the amount of available resources.
	 * 
	 * @return A hashtable that hashes allocator ids to integer arrays that
	 * 	denote the amount of available resources for the specified allocator.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Hashtable getResources() throws InvocationException;

// ui-container interface, used by the ui (!) only to retrieve the state of the container
	
	/**
	 * Retrieves the factories of the container. Returns a vector of
	 * object arrays [objectID, name, template, status].
	 * 
	 * @return The factories of the container as a vector of object arrays.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Vector getFactoriesUI() throws InvocationException;
	
	/**
	 * Retrieves the allocators of the container. Returns a vector of
	 * object arrays [objectID, name, total, free, template, status].
	 * 
	 * @return The allocators of the container as a vector of object arrays.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Vector getAllocatorsUI() throws InvocationException;
	
	/**
	 * Returns the instances of a certain factory. Returns a vector of
	 * object arrays [objectID, name, template, status].
	 * 
	 * @param id The id of the factory whose instances need to be retrieved.
	 * @return The instances of the factory as object arrays.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Vector getInstancesUI(ObjectID id) throws InvocationException;
	
	/**
	 * Returns the resources of a certain allocator. Returns a vector of
	 * object arrays [objectID, name, estimate, template, status].
	 * 
	 * @param id The id of the allocator whose resources needs to be retrieved.
	 * @return The resources issued by the allocator as object arrays.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public Vector getResourcesUI(ObjectID id) throws InvocationException;
	
	/**
	 * Signals a change instance request to the parent of the specified
	 * instance. This simulates a change instance request comming from
	 * the instance itself.
	 * 
	 * @param id The id of the instance that should signal the change.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void changeInstanceUI(ObjectID id) throws InvocationException;
	
	/**
	 * Signals a commit instance template request to the parent of the
	 * specified instance. This simulates a changed offer that is created
	 * by the specified instance.
	 * 
	 * @param id The id of the instance that should signal the commit.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void commitInstanceUI(ObjectID id) throws InvocationException;
	
	/**
	 * Signals a stop instance request to the specified instance. This
	 * will eventually cause the parent to fail.
	 * 
	 * @param id The id of the instance that should be stopped.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void stopInstanceUI(ObjectID id) throws InvocationException;
	
	/**
	 * Signals a change resource request to the parent of the specified
	 * instance. This simulates a change resource request comming from
	 * the resource itself.
	 * 
	 * @param id The id of the resource that should signal the change.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void changeResourceUI(ObjectID id) throws InvocationException;
	
	/**
	 * Signals a commit resource template request to the parent of the
	 * specified resource. This simulates a changed offer that is created
	 * by the specified resource.
	 * 
	 * @param id The id of the resource that should signal the commit.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void commitResourceUI(ObjectID id) throws InvocationException;
	
	/**
	 * Signals a stop resource request to the specified resource. This
	 * will eventually cause the parent to fail.
	 * 
	 * @param id The id of the resource that should be stopped.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void stopResourceUI(ObjectID id) throws InvocationException;
}
