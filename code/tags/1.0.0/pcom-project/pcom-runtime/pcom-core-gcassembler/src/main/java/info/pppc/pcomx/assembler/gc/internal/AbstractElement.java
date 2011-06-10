package info.pppc.pcomx.assembler.gc.internal;

import java.util.Vector;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.contract.Contract;

/**
 * The abstract element is the common base class for instances and resources.
 * 
 * @author Mac
 */
public abstract class AbstractElement extends AbstractItem {

	/**
	 * The object id of the creator that would create the instance or resource.
	 */
	protected ObjectID creatorID;
	
	/**
	 * The set of instance or resource bindings that are currently used 
	 * by the element.
	 */
	protected Vector bindings = new Vector();
	
	/**
	 * Creates a new element using the specified application. The element
	 * has been created by the specified parent and it would run on the
	 * specified system created by the specified creator.
	 * 
	 * @param application The application that uses the element.
	 * @param parent The parent of the element, i.e. the binding that uses
	 * 	the element.
	 * @param systemID The system that would run the element.
	 * @param creatorID The factory or allocator that would create the element.
	 * @param templates The templates that can be created by the creator for
	 * 	the element. This will be either instance or resource templates depending
	 * 	on the element type.
	 * @param pointer The pointer that can be used to retrieve the state of
	 * 	the corresponding running element. This may be null if the element is
	 * 	new and cannot be reused. 
	 */
	public AbstractElement(Application application, AbstractItem parent, SystemID systemID, ObjectID creatorID, Vector templates, Pointer pointer) {
		super(application, parent, systemID);
		this.creatorID = creatorID;
		this.templates = templates;
		this.pointer = pointer;
	}
	
	/**
	 * Releases all state held by the element.
	 */
	public void release() {
		configured = false;
		for (int i = bindings.size() - 1; i >= 0; i--) {
			AbstractBinding binding = (AbstractBinding)bindings.elementAt(i);
			bindings.removeElementAt(i);
			binding.release();
			application.fireApplicationEvent(Application.EVENT_ITEM_REMOVED, binding);
		}
		application.removeItem(this);
	}
	
	/**
	 * Externalizes the assembly description using the specified name.
	 * 
	 * @param name The name as defined by the binding that uses the element.
	 * @return The assembly for the element and its children.
	 */
	public Assembly externalize(String name) {
		Assembly assembly = new Assembly();
		assembly.setContainerID(IContainer.CONTAINER_ID);
		assembly.setTemplate((Contract)templates.elementAt(template));
		assembly.setCreatorID(creatorID);
		// set element if we are reused, else ignore
		if (pointer != null) {
			AssemblyState state = application.getState(pointer);
			assembly.setElementID(state.getElementID());
		} else {
			assembly.setElementID(null);	
		}
		assembly.setName(name);
		assembly.setSystemID(systemID);
		for (int i = 0; i < bindings.size(); i++) {
			AbstractBinding b = (AbstractBinding)bindings.elementAt(i);
			b.externalize(assembly);
		}
		return assembly;
	}

}
