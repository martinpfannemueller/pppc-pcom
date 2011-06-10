package info.pppc.pcomx.assembler.gc.internal;

import java.util.Vector;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.contract.Contract;


/**
 * This class represents an instance within the gc assembler. If the
 * parent of the instance is null, the instance is assumed to be
 * the application anchor. The instance can only be used by instance
 * bindings.
 * 
 * @author Mac
 */
public class Instance extends AbstractElement {

	
	/**
	 * Creates a new instance in the specified application using the specified contracts, system 
	 * ids and creator ids. The pointer denotes whether the instance has a predefined state in
	 * stored by the application. If the pointer is set to null, this means that the instance
	 * will be new. Note that this constructor is intended for the anchor. Others should call
	 * the constructor that defines a parent.
	 * 
	 * @param application The application that uses the instance.
	 * @param templates The contract that are possible for this instance.
	 * @param systemID The system id of the instance's creator.
	 * @param creatorID The object id of the instance's creator.
	 * @param pointer The pointer that associates this instance with an exsiting state.
	 */
	public Instance(Application application, SystemID systemID, ObjectID creatorID, Vector templates, Pointer pointer) {
		this(application, null, systemID, creatorID, templates, pointer);
	}
	
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
	public Instance(Application application, AbstractItem parent, SystemID systemID, ObjectID creatorID, Vector templates, Pointer pointer) {
		super(application, parent, systemID, creatorID, templates, pointer);
		application.fireApplicationEvent(Application.EVENT_ITEM_ADDED, this);
	}

	/**
	 * Configures the instance. If the instance is already configured,
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
				Contract t = (Contract)templates.elementAt(template);
				// create bindings for contract
				Contract[] idemand = t.getContracts(Contract.TYPE_INSTANCE_DEMAND);
				for (int i = 0; i < idemand.length; i++) {
					// create instance bindings for the contracts and
					// schedule them for resolution.
					InstanceBinding binding = new InstanceBinding
						(application, this, systemID, idemand[i], pointer);
					bindings.addElement(binding);
					application.addBinding(binding);
				}
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
				// notify the parent that the configuration failed.
				if (parent != null) parent.notifyFailure();
				else application.fireApplicationEvent(Application.EVENT_ITEM_REMOVED, this);
			}
		}
	}

}
