package info.pppc.pcomx.assembler.gc.internal;

import info.pppc.base.system.ObjectID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.contract.Contract;

import java.util.Vector;

/**
 * This class represents an instance binding within the gc assembler.
 * It can be used by instances only.
 * 
 * @author Mac
 */
public class InstanceBinding extends AbstractBinding {
	
	/**
	 * Creates a new instance binding using the specified application,
	 * that matches the specified demand for the parent that runs on
	 * the specified system and has the specified pointer.
	 * 
	 * 
	 * @param application The application in which the binding belongs.
	 * @param demand The demand of the parent that must be met.
	 * @param systemID The system id of the parent.
	 * @param parent The parent of the binding.
	 * @param pointer The pointer of the parent. The pointer may be null.
	 */
	public InstanceBinding(Application application, AbstractItem parent, SystemID systemID, Contract demand, Pointer pointer) {
		super(application, parent, systemID, demand);
		if (pointer != null) {
			this.pointer = new Pointer(pointer, true, demand.getName());	
		}
		application.fireApplicationEvent(Application.EVENT_ITEM_ADDED, this);
	}

	/**
	 * Configures the binding if possible. If that is not possible, the potential
	 * parent will be notified about the problem.
	 */
	public void configure() {
		if (configured) return;
		else {
			release();
			if (template < templates.size()) {
				Object[] t = (Object[])templates.elementAt(template);
				SystemID sysID = (SystemID)t[0];
				ObjectID creID = (ObjectID)t[1];
				Vector temps = (Vector)t[2];
				AssemblyState state = null;
				if (pointer != null) {
					state = application.getState(pointer);	
				}
				if (state != null && state.getSystemID().equals(sysID) && state.getCreatorID().equals(creID)) {
					element = new Instance(application, this, sysID, creID, temps, pointer);	
				} else {
					element = new Instance(application, this, sysID, creID, temps, null);
				}
				application.addItem(element);
				// consider ourselves done
				configured = true;
			} else {
				// we need to notify our parent that the configuration failed.
				if (parent != null) parent.notifyFailure();	
			}
		}
	}
	
	/**
	 * Externalizes the instance binding into an assembly
	 * recursively. Calling this method is only allowed if
	 * the configuration process finished successfully.
	 * 
	 * @param parent The parent assembly that will be filled
	 * 	with the instance bindings configuration.
	 */
	public void externalize(Assembly parent) {
		Assembly assembly = element.externalize(demand.getName());
		parent.addInstance(assembly);
	}

	
}
