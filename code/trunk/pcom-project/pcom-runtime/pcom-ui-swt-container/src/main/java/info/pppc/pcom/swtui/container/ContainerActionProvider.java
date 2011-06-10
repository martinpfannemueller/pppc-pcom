package info.pppc.pcom.swtui.container;

import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.container.action.ChangeInstanceAction;
import info.pppc.pcom.swtui.container.action.ChangeResourceAction;
import info.pppc.pcom.swtui.container.action.CommitInstanceAction;
import info.pppc.pcom.swtui.container.action.CommitResourceAction;
import info.pppc.pcom.swtui.container.action.StopInstanceAction;
import info.pppc.pcom.swtui.container.action.StopResourceAction;

import org.eclipse.jface.action.Action;

/**
 * This action provider provides the context sensitve actions for
 * the currently selected element. 
 *  
 * @author Mac
 */
public class ContainerActionProvider implements IContainerModel {

	/**
	 * The container control that is used to create actions.
	 */
	private ContainerControl control;

	/**
	 * Creates a new action provider for the specified control.
	 * 
	 * @param control The container control that is used to
	 * 	create the actions.
	 */
	public ContainerActionProvider(ContainerControl control) {
		this.control = control;
	}

	/**
	 * Returns the menu actions for the specified selection.
	 * 
	 * @param element The element that is currently selected.
	 * @return The context dependent actions.
	 */
	public Action[] getMenuActions(Object element) {
		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode)element;
			switch (node.getType()) {
				case TYPE_RESOURCE:
				case TYPE_RESOURCE_ID:
				case TYPE_RESOURCE_NAME:
				case TYPE_RESOURCE_STATUS:
				case TYPE_RESOURCE_TEMPLATE:
				case TYPE_RESOURCE_USE:
					return getResourceActions(node);
				case TYPE_INSTANCE:
				case TYPE_INSTANCE_ID:
				case TYPE_INSTANCE_NAME:
				case TYPE_INSTANCE_STATUS:
				case TYPE_INSTANCE_TEMPLATE:
					return getInstanceActions(node);
				default:
					return new Action[0];
			}
		} else {
			return new Action[0];
		}
	}
	
	/**
	 * Returns the actions for nodes of instances.
	 * 
	 * @param node Some instance node that has an instance parent node.
	 * @return The acitons for the node.
	 */
	private Action[] getInstanceActions(TreeNode node) {
		if (node.getType() != TYPE_INSTANCE) {
			node = node.getParent(TYPE_INSTANCE);
		}
		node = node.getChildren(TYPE_INSTANCE_ID, false)[0];
		ObjectID id = (ObjectID)node.getData();
		return new Action[] {
			new ChangeInstanceAction(control, id),
			new CommitInstanceAction(control, id),
			new StopInstanceAction(control, id)
		};
	}
	
	/**
	 * Returns the actions for nodes of resources.
	 * 
	 * @param node Some resource node that has a resource parent node.
	 * @return The actions for the node.
	 */
	private Action[] getResourceActions(TreeNode node) {
		if (node.getType() != TYPE_RESOURCE) {
			node = node.getParent(TYPE_RESOURCE);
		}
		node = node.getChildren(TYPE_RESOURCE_ID, false)[0];
		ObjectID id = (ObjectID)node.getData();
		return new Action[] {
			new ChangeResourceAction(control, id),
			new CommitResourceAction(control, id),
			new StopResourceAction(control, id)
		};
	}

}
