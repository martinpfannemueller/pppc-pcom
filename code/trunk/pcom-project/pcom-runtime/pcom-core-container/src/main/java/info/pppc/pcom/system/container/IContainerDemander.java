package info.pppc.pcom.system.container;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.system.contract.Contract;

/**
 * The predecessor remote interface is the interface of all remote
 * services that are capable of starting and controlling instances. 
 * In pcom, each container can use instances, thus each container
 * can be the predecessor of another container. Apart from containers,
 * the application booter also must implement this interface since
 * it uses containers to start the anchor instance.
 * 
 * @author Mac
 */
public interface IContainerDemander {

	/**
	 * Signals that the provision of some successor used by a predecessor has changed. 
	 * The predecessor id identifies the predecessor instance that is hosted by this 
	 * predecessor service. This will typically be the parent of an instance or the
	 * application itself. The successor id identifies the instance whose provision has 
	 * changed. The provision contains the new provision of that instance and the 
	 * phase identifies the current phase of the instance at the time the provision
	 * changed. This is used to suppress outdated messages. If the provision is null,
	 * the instance bound to the specified name can no longer provide any useful provision.
	 *
	 * @param predecessorID The id of the predecessor that is using the successor whose
	 * 	provision changed.
	 * @param name The name of the successor that has been changed.
	 * @param phase The phase used to detect outdated messages.
	 * @param provision The new provision of the successor.
	 * @throws InvocationException Thrown if the message could not be delivered by BASE.
	 */
	public void changeInstance(ObjectID predecessorID, String name, int phase, Contract provision) throws InvocationException;
		
	/**
	 * Signals that the successor used by a predecessor has been stopped and thus, it needs
	 * to be changed. This is a post-mortem message which means that whenever this method is 
	 * called, the bound instance has been released already. The predecessor id identifies 
	 * the predecessor instance that is hosted by this predecessor service. This will typically 
	 * be the parent instance or the application itself. The successor id identifies the instance 
	 * that has performed an emergency stop. The phase identifies the current phase of the instance at 
	 * the time the instance has been stopped. This is used to suppress outdated messages.
	 * 
	 * @param predecessorID The id of the predecessor whose successor has been stopped.
	 * @param name The name of the successor that has been changed.
	 * @param phase The phase used to detect outdated messages.
	 * @throws InvocationException Thrown if the message could not be delivered by BASE.
	 */
	public void removeInstance(ObjectID predecessorID, String name, int phase) throws InvocationException;
	
	
	/**
	 * Signals that the successor used by a predecessor has requested an application shutdown.
	 * If the predecessor has not already forwarded a similar message from some other component,
	 * it should forward this message to the application anchor. The predecessor id identifies 
	 * the predecessor instance that is hosted by this predecessor service. This will typically 
	 * be the parent instance or the application itself. The successor name identifies the instance 
	 * that has requested the stop. The phase identifies the current phase of the instance at 
	 * the time the instance has been stopped. This is used to suppress outdated messages.
	 * 
	 * @param predecessorID The id of the predecessor whose successor tries to stop the application.
	 * @param name The name of the successor that sends the request
	 * @param phase The phase used to detect outdated messages.
	 * @throws InvocationException Thrown if the message could not be delivered by BASE.
	 */
	public void exitApplication(ObjectID predecessorID, String name, int phase) throws InvocationException;
	
	/**
	 * Signals that the successor used by a predecessor has requested a checkpoint for the
	 * application anchor, i.e. it has requested to save the application state.  The predecessor
	 * id identifies the predecessor instance that is hosted by this predecessor service. This
	 * will typically be the parent instance or the application itself. The successor name identifies
	 * the instance that has requested the checkpoint or one of this child. The phase identifies
	 * the current phase of the instance in order to suppress outdated messages.
	 * 
	 * @param predecessorID The id of the predecessor whose successor tries to checkpoint the application.
	 * @param name The name of the successor that sends the request.
	 * @param phase The phase used to suppress outdated messages.
	 * @throws InvocationException Thrown if the message could not be delivered by BASE.
	 */
	public void saveApplication(ObjectID predecessorID, String name, int phase) throws InvocationException;
}
