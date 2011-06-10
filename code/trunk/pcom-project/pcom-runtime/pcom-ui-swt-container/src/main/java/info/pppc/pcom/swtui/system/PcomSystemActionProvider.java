package info.pppc.pcom.swtui.system;

import java.util.Vector;

import info.pppc.base.swtui.system.SystemActionProvider;
import info.pppc.base.swtui.system.SystemControl;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.swtui.system.action.ContainerAction;
import info.pppc.pcom.system.container.IContainer;

import org.eclipse.jface.action.Action;

/**
 * The extended action provider extends the system browser of the
 * base ui with pcom related-actions on systems. E.g. open the
 * container browser, etc.
 * 
 * @author Mac
 */
public class PcomSystemActionProvider extends SystemActionProvider {

	/**
	 * Creates a new browser action provider. 
	 */
	public PcomSystemActionProvider() {
		super();
	}

	/**
	 * Extends the menu actions for specific elements
	 * of the browser model. In particular, this class
	 * adds the container brower action point to 
	 * systems that are equipped with containers.
	 * 
	 * @param control The control that performs the request.
	 * @param element The selected element.
	 * @return The actions for the selected element.
	 */
	public Action[] getMenuActions(SystemControl control, Object element) {
		Vector actions = new Vector();
		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode)element;
			actions = getMenuActions(control, node);
		}
		Action[] existing = super.getMenuActions(control, element);
		Action[] result = new Action[actions.size() + existing.length];
		System.arraycopy(existing, 0, result, 0, existing.length);
		for (int i = 0; i < actions.size(); i++) {
			result[i + existing.length] = (Action)actions.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Returns the menu actions that need to be added to the existing
	 * actions for the specified tree node.
	 * 
	 * @param control The control that requested the actions.
	 * @param node The node that is selected in the control.
	 * @return A vector containing additional actions that need to
	 * 	be added to the already existing actions.
	 */
	private Vector getMenuActions(SystemControl control, TreeNode node) {
		Vector result = new Vector();
		TreeNode device = null;
		if (node.getType() == TYPE_DEVICE) {
			device = node;
		} else {
			node.getParent(TYPE_DEVICE);
		}
		if (device != null && hasContainer(device)) {
			TreeNode identifier = device.getChildren(TYPE_IDENTIFIER, false)[0];
			TreeNode name = device.getChildren(TYPE_NAME, false)[0]; 
			String n = (String)name.getData();
			SystemID id = (SystemID)identifier.getData();
			result.addElement(new ContainerAction(control.getManager(), id, n));
		}
		return result;
	}
	
	/**
	 * Determines whether the specified device node of the browser
	 * model also has a container system service.
	 * 
	 * @param device The device node.
	 * @return True if the device node also has a container system service
	 * 	node, false if it does not have one or if the node is not a device
	 * 	node.
	 */
	private boolean hasContainer(TreeNode device) {
		if (device.getType() == TYPE_DEVICE) {
			TreeNode[] services = device.getChildren(TYPE_SERVICE, true);
			for (int i = 0; i < services.length; i++) {
				if (IContainer.CONTAINER_ID.equals(services[i].getData())) {
					return true;
				}
			}
		}
		return false;
	}
	
}

	
