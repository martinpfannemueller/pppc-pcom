package info.pppc.pcom.system.application;

import java.util.Vector;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.system.container.IContainerDemander;

/**
 * The applicaiton manager is a system service that enables remote systems to 
 * start applications on this system. Note that local callers should not neccessarily
 * go through this interface. They might as well bind the local application manager
 * which allows them to ensure that their calls are processed. Also they can use
 * the event interface of the application manager to register for lifecylce related
 * events fired by the manager.
 * 
 * @author Mac
 */
public interface IApplicationManager extends IContainerDemander {
	
	/**
	 * The object id of the application manager service. Each system that hosts an 
	 * application manager will export the service using this object id.
	 */
	public static final ObjectID APPLICATION_MANAGER_ID = new ObjectID(4);

	
	/**
	 * Adds a new application to the application manager using the specified description.
	 * The description must at least contain one preference level. If the application 
	 * does not contain a preference, the method will throw an illegal argument exception.
	 * Note that this method will assign a new application id to the application descriptor
	 * no matter what the id of the descriptor was. Also note that this method will not
	 * start the application. An application start must be initiated using the start
	 * application method.
	 * 
	 * @param descriptor The descriptor that describes the application.
	 * @return The id assigned to the application.
	 * @throws IllegalArgumentException Thrown if the descriptor does not contain at
	 * 	least one preference level.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public ObjectID addApplication(ApplicationDescriptor descriptor) 
		throws IllegalArgumentException, InvocationException;
	
	/**
	 * Updates an existing application using the specified application descriptor. The
	 * application id contained in the descriptor will be used as key to associate the
	 * description with an existing application. If the id is null, this method will
	 * not apply the update. Note that the descriptor must contain at least one preference
	 * level otherwise, an illegal argument exception will be thrown.
	 * 
	 * @param descriptor The descriptor that should be retrieved.
	 * @throws IllegalArgumentException Thrown if the descriptor does not contain at
	 * 	least one preference level.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void updateAppliction(ApplicationDescriptor descriptor) 
		throws IllegalArgumentException, InvocationException;
	
	/**
	 * Removes the application with the specified id from the application manager's 
	 * repository. If the application id does not exist, this method will do nothing.
	 * If the application is currently running, this method will first stop the
	 * application and then remove it.
	 * 
	 * @param applicationID The id of the application to remove.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void removeApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to start an application that is currently not
	 * running. If the application is running, this method will simply return and
	 * do nothing. 
	 * 
	 * @param applicationID The id of the application that should be started.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void startApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to adapt an application that is currently running. 
	 * If the application is not running, this method will simply return and
	 * do nothing. 
	 * 
	 * @param applicationID The id of the application that should be adapted.
	 * @throws InvocationException Thrown by base if the call fails.
	 */	
	public void changeApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to stop the application with the specified application
	 * identifier. Note that this method does not remove the application from the
	 * application manger, the application manager will simply stop free components and
	 * resources.
	 * 
	 * @param applicationID The application identifier of the application that should 
	 * 	be stopped.
	 * @throws InvocationException Thrown by base if the call to the service fails.
	 */
	public void exitApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to save the application with the specified application
	 * identifier. This will essentially create checkpoints for all instances bound to
	 * the application.
	 * 
	 * @param applicationID The application identifier of the application that should 
	 * 	be saved.
	 * @throws InvocationException Thrown by base if the call to the service fails.
	 */
	public void saveApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to load the application with the specified application
	 * identifier. This will essentially load all checkpoints for all instances bound to
	 * the application.
	 * 
	 * @param applicationID The application identifier of the application that should 
	 * 	be loaded.
	 * @throws InvocationException Thrown by base if the call to the service fails.
	 */
	public void loadApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to query the state and configuration of a certain
	 * application. If the application has been stopped or if there is no application
	 * with the specified application identifier, this method will return null.
	 * 
	 * @param applicationID The id of the application whose state needs to be retrieved.
	 * @return The application descriptor of the specified application or null if the
	 * 	application does not exist.
	 * @throws InvocationException Thrown by base if the call to the service fails.
	 */
	public ApplicationDescriptor queryApplication(ObjectID applicationID) throws InvocationException;
	
	/**
	 * This method can be called to retrieve the identifiers of all applications 
	 * that are currently hosted by the application manager.
	 * 
	 * @return A vector of object identifiers that denote the applications that
	 * 	are currently stored within the application manager.
	 * @throws InvocationException Thrown by base if the call to the service fails.
	 */
	public Vector getApplications() throws InvocationException;
	
}
