package info.pppc.pcomx.assembler.gc.internal;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.contract.Contract;

import java.util.Vector;

/**
 * This class represents a resource within the gc assembler.
 * It can be used by resource bindings.
 * 
 * @author Mac
 */
public class Resource extends AbstractElement {
	
	/**
	 * Creates a new instance in the specified application using the specified contracts, system 
	 * ids and creator ids. The pointer denotes whether the instance has a predefined state in
	 * stored by the application. If the pointer is set to null, this means that the instance
	 * will be new. The parent points to the binding that will be notified if the configuration
	 * of the instance failed completely. 
	 * 
	 * @param application The application that uses the instance.
	 * @param templates The contract that are possible for this instance.
	 * @param systemID The system id of the instance's creator.
	 * @param creatorID The object id of the instance's creator.
	 * @param pointer The pointer that associates this instance with an exsiting state.
	 * @param parent The parent (binding) that uses the instance.
	 */
	public Resource(Application application, AbstractItem parent, SystemID systemID, ObjectID creatorID, Vector templates, Pointer pointer) {
		super(application, parent, systemID, creatorID, templates, pointer);
		application.fireApplicationEvent(Application.EVENT_ITEM_ADDED, this);
	}
	
	/**
	 * Called whenever an instance or resource binding gives up.
	 * This will set the configured flag of the instance to false
	 * and it will increment the template counter and it will
	 * schedule the resource for new configuration round. Additionally,
	 * if this resource has any reserved resources, a call to this
	 * method will release them.
	 */
	public void notifyFailure() {
		if (configured) {
			Device device = application.getDevice(systemID);
			Contract temp = (Contract)templates.elementAt(template);
			device.release(creatorID, temp);
		}
		super.notifyFailure();
	}

	/**
	 * Configures the resource. If the resource is already configured,
	 * this method will do nothing. Otherwise, it will release all
	 * configuration data and it will check whether there is still a 
	 * level that can be configured. If so, it will try to configure
	 * the level.
	 */
	public void configure() {
		if (configured) return;
		else {
			release();
			if (template < templates.size()) {
				// perform resource reservation
				Device device = application.getDevice(systemID);
				Contract temp = (Contract)templates.elementAt(template);
				if (device.reserve(creatorID, temp)) {
					// resolve dependencies
					Contract t = (Contract)templates.elementAt(template);
					// create bindings for contract
					Contract[] rdemand = t.getContracts(Contract.TYPE_RESOURCE_DEMAND);
					for (int i = 0; i < rdemand.length; i++) {
						// create resource bindings for the contracts and
						// schedule them for resolution.
						ResourceBinding binding = new ResourceBinding
							(application, this, systemID, rdemand[i], pointer);
						bindings.addElement(binding);
						application.addBinding(binding);
					}
					// consider ourselves done
					configured = true;					
				} else {
					// resource reservation failed, notify parent.
					if (parent != null) parent.notifyFailure();
				}
			} else {
				// notify the parent that the configuration failed.
				if (parent != null) parent.notifyFailure();	
			}
		}
	}

	/**
	 * Releases all state held by the resource. This will release
	 * the bindings held by the resource and if the resource is
	 * configured, this will also release all resources that have
	 * been reservated for the resource.
	 */
	public void release() {
		if (configured) {
			Device device = application.getDevice(systemID);
			Contract temp = (Contract)templates.elementAt(template);
			device.release(creatorID, temp);
			configured = false;
		}
		super.release();
	}

}
