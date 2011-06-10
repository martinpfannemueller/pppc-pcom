package info.pppc.pcom.lcdui.system;

import info.pppc.base.lcdui.BaseUI;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.base.lcdui.system.SystemElement;
import info.pppc.base.lcdui.tree.TreeNode;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.lcdui.PcomUI;

/**
 * The pcom system element is an extended version of the base
 * system browser element. It adds externalized strings for
 * pcom services.
 * 
 * @author Mac
 */
public class PcomSystemElement extends SystemElement {

	/**
	 * The device labels for various services introduced by pcom.
	 */
	private static final String UI_SERVICE = "info.pppc.pcom.lcdui.system.PcomSystemElement.SERVICE";

	
	/**
	 * Creates a new system element with pcom extensions on the
	 * specified manager.
	 * 
	 * @param manager The manager that is used by the element.
	 */
	public PcomSystemElement(IElementManager manager) {
		super(manager);
	}

	/**
	 * Returns the tree node for a pcom service or if the service is
	 * not a pcom service, it returns the base internal description.
	 * 
	 * @param objectID The object id whose node should be returned.
	 * @return The tree node for the pcom or base well known service.
	 */
	protected TreeNode createTree(ObjectID objectID) {
		for (int i = 3; i <= 5; i++) {
			if (objectID.equals(new ObjectID(i))) {
				TreeNode node = new TreeNode(objectID, 
						PcomUI.getText(UI_SERVICE + "." + i), 
						BaseUI.getImage(BaseUI.IMAGE_SERVICE));
				return node;
			}
		}
		return super.createTree(objectID);
	}
	
}
