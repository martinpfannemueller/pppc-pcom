package info.pppc.pcom.swtui.container.action;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;
import info.pppc.pcom.system.container.IContainer;

/**
 * The commit resource action is an action that executes a commit resource call on 
 * the container that signals that the template of the resource changed.
 * 
 * @author Mac
 */
public class CommitResourceAction extends AbstractElementAction {

	/**
	 * The taskname displayed while the action runs.
	 */
	private static final String UI_TASK = "info.pppc.pcom.swtui.container.action.CommitResourceAction.TASK";
	
	/**
	 * The text of the action shown by the gui.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.CommitResourceAction.TEXT";
	
	/**
	 * Creates a new commit resource action that signals an resource template change
	 * of the specified resource to its parent. 
	 * 
	 * @param control The control that is used for the action.
	 * @param resourceID The id of the resource to change.
	 */
	public CommitResourceAction(ContainerControl control, ObjectID resourceID) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_GREEN), 
				PcomUI.getText(UI_TASK), control,  resourceID);
	}

	/**
	 * Invokes the commit resource call on the container.
	 * 
	 * @param container The container that executes the resource.
	 * @param elementID The object id of the resource.
	 * @throws InvocationException Thrown if the call fails.
	 */
	public void invoke(IContainer container, ObjectID elementID) throws InvocationException {
		container.commitResourceUI(elementID);
	}

}
