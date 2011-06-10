package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.IElementStatus;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceProvisionReader;

/**
 * The instance contract is the basic view of a status that is provided
 * to an instance at runtime. The instance that adheres to the status must
 * synchronize on the status object in order to avoid race condintions.
 *   
 * @author Mac
 */
public interface IInstanceStatus extends IElementStatus {
	
	/**
	 * Signals that the instance provison changed. The event source will be the
	 * status and the event type will be an event provision reader of the 
	 * provision that changed.
	 */
	public static final int EVENT_INSTANCE_CHANGED = 8;
	
	/**
	 * Signals that the resource provison has been removed. The event source will 
	 * be the status and the event type will be an event provision reader of the 
	 * provision that changed.
	 */
	public static final int EVENT_INSTANCE_REMOVED = 16;
	
	/**
	 * Returns the instance demand that is currently directed towards the
	 * instance. 
	 * 
	 * @return The instance demand that is currently directed towards the
	 * 	instance.
	 */
	public IInstanceDemandReader getInstance();
	
	/**
	 * Returns the instances that are provided to this instance or factory.
	 * The names of the instances are the same as the ones that have been
	 * requested by the instance or factory. Thus, there will be one 
	 * provision for each demand required by the instance or factory.
	 * 
	 * @return The instance provisions that are currently issued to the 
	 * 	instance or factory.
	 */
	public IInstanceProvisionReader[] getInstances();
	
	/**
	 * Returns the instance provision that is currently provided to the
	 * factory under the specified name. If the name is null, this method
	 * will throw an exception. If the container does not provide an
	 * instance with the specified name, this method will return null. 
	 * 
	 * @param name The name of the instance provision to retrieve.
	 * @return The instance provision with the specified name or null
	 * 	if such an instance provision does not exist.
	 * @throws NullPointerException Thrown if the instance name is null.
	 */
	public IInstanceProvisionReader getInstance(String name);
	
	
}
