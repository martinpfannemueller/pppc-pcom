package info.pppc.pcomx.assembler.gc.internal;

import java.util.Vector;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.contract.Contract;

/**
 * The abstract binding is an item used as common base for instance and
 * resource bindings.
 * 
 * @author Mac
 */
public abstract class AbstractBinding extends AbstractItem {

	/**
	 * This field represents the element that is configured by the 
	 * binding. This will be either an instance or a resource depending
	 * on the binding type.
	 */
	protected AbstractElement element;
	
	/**
	 * This field represents the demand that must be satisfied by the
	 * binding. Depending on the binding, this will either be a resource
	 * or a instance demand.
	 */
	protected Contract demand;
	
	/**
	 * Creates a new binding for the specified application. The binding
	 * has been created using the specified parent that runs on the
	 * specified system. The demand describes the demand that must be
	 * satisfied by the binding.
	 * 
	 * @param application The application that uses the binding.
	 * @param parent The parent of the binding, i.e. the instance or
	 * 	resource that uses the binding.
	 * @param systemID The system id of the parent.
	 * @param demand The demand that must be satisfied by the binding.
	 */
	public AbstractBinding(Application application, AbstractItem parent, SystemID systemID, Contract demand) {
		super(application, parent, systemID);
		this.demand = demand;
	}

	/**
	 * Returns the demand of the binding that needs to be resolved.
	 * 
	 * @return The demand of the binding.
	 */
	public Contract getDemand() {
		return demand;
	}
	
	/**
	 * Called whenever the binding is resolved, this will add the specified
	 * template to the list of templates.
	 * 
	 * @param systemID The system id of the potential template.
	 * @param creatorID The creator id of the potential template.
	 * @param temps The templates for the creator.
	 */
	public void addTemplates(SystemID systemID, ObjectID creatorID, Vector temps) {
		AssemblyState state = null;
		if (pointer != null) {
			state = application.getState(pointer);	
		}
		if (state != null && state.getSystemID().equals(systemID) && state.getCreatorID().equals(creatorID)) {
			templates.insertElementAt(new Object[] { systemID, creatorID, temps }, 0);	
		} else {
			templates.addElement(new Object[] { systemID, creatorID, temps });
		}
	}
	
	/**
	 * Releases all state held by the binding.
	 */
	public void release() {
		configured = false;
		if (element != null) {
			element.release();
			application.fireApplicationEvent(Application.EVENT_ITEM_REMOVED, element);
			element = null;
		}
		application.removeBinding(this);
		application.removeItem(this);
	}
	
	/**
	 * Externalizes the binding into an assembly recursively. Calling this 
	 * method is only allowed if the configuration process finished successfully.
	 * 
	 * @param parent The parent assembly that will be filled with the configuration
	 * 	of the binding.
	 */
	public abstract void externalize(Assembly parent);

}
