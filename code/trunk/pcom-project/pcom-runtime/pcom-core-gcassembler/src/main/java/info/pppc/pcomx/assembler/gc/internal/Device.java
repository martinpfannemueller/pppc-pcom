package info.pppc.pcomx.assembler.gc.internal;

import java.util.Hashtable;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.contract.Contract;

/**
 * The device is used to store the data of a certain device. This is typically the 
 * amount of resources that are available on the device.
 * 
 * @author Mac
 */
public class Device {

	/**
	 * The system id of the device.
	 */
	private SystemID systemID;
	
	/**
	 * The resources of the device.
	 */
	private Hashtable resources;
	
	/**
	 * Creates a new uninitialized device with the specified
	 * system id.
	 * 
	 * @param systemID The system id of the device represented
	 * 	by this object.
	 * @param resources The free resources on the device.
	 */
	public Device(SystemID systemID, Hashtable resources) {
		this.systemID = systemID;
		this.resources = resources;
	}
	
	/**
	 * Returns the system id of the device represented by this
	 * object.
	 * 
	 * @return The system id of the device represented by this
	 * 	object.
	 */
	public SystemID getSystemID() {
		return systemID;
	}
	
	/**
	 * Reserves the specified amount of resources from the specified
	 * creator on this device.
	 * 
	 * @param creatorID The creator id. This must denote a resource
	 * 	allocator id of some allocator present on the system represented
	 *  by this device object.
	 * @param c The contract (a resource template) that describes the
	 * 	amount of resources needed from the allocator.
	 * @return True if the amount of resources can be reserved, false
	 * 	if the amount is not available. Note that this method will 
	 * 	globally allocate the resources only if the reservation is
	 * 	successful. If it is not successful, the available resources
	 * 	will not be changed.
	 */
	public boolean reserve(ObjectID creatorID, Contract c) {
		if (c.getType() != Contract.TYPE_RESOURCE_TEMPLATE) {
			return false;
		} else {
			int[] estimate = (int[])c.getAttribute(Contract.ATTRIBUTE_RESOURCE_ESTIMATE);
			if (estimate == null) return false;
			int[] free = (int[])resources.get(creatorID);
			if (free == null) return false;
			if (estimate.length != free.length) return false;
			for (int i = 0; i < estimate.length; i++) {
				free[i] -= estimate[i];
				if (free[i] < 0) {
					for (int j = 0; j <= i; j++) {
						free[j] += estimate[j];
					}
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Releases the resources for a previously reserved contract. Note
	 * that this method does not check whether the resources have been
	 * reserved at all. Thus, calling this method multiple times will
	 * lead to unnaturally increased available resources on the device.
	 * If this happens it is likely that the assembler will return
	 * invalid assemblies as result.
	 * 
	 * @param creatorID The creator that has provided the resources.
	 * @param c The contract that should be released.
	 */
	public void release(ObjectID creatorID, Contract c) {
		int[] estimate = (int[])c.getAttribute(Contract.ATTRIBUTE_RESOURCE_ESTIMATE);
		if (estimate == null) return;
		int[] free = (int[])resources.get(creatorID);
		if (free == null) return;
		if (estimate.length != free.length) return;
		for (int i = 0; i < estimate.length; i++) {
			free[i] += estimate[i];
		}
	}

}
