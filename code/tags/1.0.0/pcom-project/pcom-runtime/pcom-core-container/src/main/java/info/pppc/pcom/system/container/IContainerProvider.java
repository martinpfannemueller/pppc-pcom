package info.pppc.pcom.system.container;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.system.assembler.AssemblyPointer;

/**
 * The successor interface is the remote interface that must be implemented by all 
 * services that are capable of starting and controlling instances. It contains
 * methods to start and create an instance as well as to pause and stop them. 
 * 
 * @author Mac
 */
public interface IContainerProvider {

	/**
	 * Called to start an instance. The instance setup describes the configuration
	 * that must be used by the instance. The instance state describes the result
	 * of the operation.
	 * 
	 * @param setup The setup of the instance that describes the parent as well as
	 * 	the instance that should be created an started.
	 * @param phase The phase that is initiated through this call.
	 * @return The state of the instance that describes whether the startup was a
	 * 	success and contains the neccessary information to connect the parent with
	 * 	the new instance.
	 * @throws InvocationException Thrown by base if the invocation cannot be delivered.
	 */
	public InstanceState startInstance(InstanceSetup setup, int phase) throws InvocationException;
	
	/**
	 * Called to pause an instance. The instance id identifies the instance that
	 * needs to be paused and the assembler context describes the assembler that
	 * should be provided with the neccessary information about the instance.
	 * 
	 * @param successorID The instance id that identifies the instance that needs
	 * 	to be reconfigured.
	 * @param context The assembler context that describes the assembler that should
	 * 	be loaded with the information about the instance after the instance has
	 * 	been paused.
	 * @param phase The phase that is initiated through this call.
	 * @throws InvocationException Thrown by base if the invocation cannot be delivered.
	 */
	public void pauseInstance(ObjectID successorID, AssemblyPointer context, int phase) throws InvocationException;
	
	/**
	 * Called to stop an instance. The instance id identifies the instance that
	 * needs to be stopped.
	 * 
	 * @param successorID The instance id that identifies the instance that should
	 * 	be stopped.
	 * @throws InvocationException Thrown by base if the invocation cannot be delivered.
	 */
	public void stopInstance(ObjectID successorID) throws InvocationException;
	
	/**
	 * Stores and returns the state of the instance with the specified successor
	 * id in the checkpoint.
	 * 
	 * @param successorID The id of the successor to store.
	 * @return The checkpoint of the successor or null if the operation failed.
	 * @throws InvocationException Thrown by base if the invocation cannot be delivered.
	 */
	public InstanceCheckpoint storeInstance(ObjectID successorID) throws InvocationException;
	
	/**
	 * Loads the specified checkpoint into the instance with the specified successor id.
	 * 
	 * @param successorID The successor id that will receive the checkpoint.
	 * @param checkpoint The checkpoint that should be loaded.
	 * @throws InvocationException Thrown by base if the invocation cannot be delivered.
	 */
	public void loadInstance(ObjectID successorID, InstanceCheckpoint checkpoint) throws InvocationException;
}
