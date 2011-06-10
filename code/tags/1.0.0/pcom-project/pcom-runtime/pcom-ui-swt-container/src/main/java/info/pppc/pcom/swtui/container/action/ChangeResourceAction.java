package info.pppc.pcom.swtui.container.action;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;
import info.pppc.pcom.system.container.IContainer;

/**
 * The change resource action is an action that executes a change resource call on 
 * the container that signals that the resource can no longer run with its current
 * configuration. 
 * 
 * @author Mac
 */
public class ChangeResourceAction extends AbstractElementAction {

	/**
	 * The taskname displayed while the action runs.
	 */
	private static final String UI_TASK = "info.pppc.pcom.swtui.container.action.ChangeResourceAction.TASK";
	
	/**
	 * The text of the action shown by the gui.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.ChangeResourceAction.TEXT";
	
	/**
	 * Creates a new change resource action that signals an resource change
	 * of the specified resource to its parent. This will initiate a reconfiguration. 
	 * 
	 * @param control The control that is used for the action.
	 * @param resourceID The id of the resource to change.
	 */
	public ChangeResourceAction(ContainerControl control, ObjectID resourceID) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_BLUE), 
				PcomUI.getText(UI_TASK), control,  resourceID);
	}

	/**
	 * Invokes the change resource call on the container.
	 * 
	 * @param container The container that executes the resource.
	 * @param elementID The object id of the resource.
	 * @throws InvocationException Thrown if the call fails.
	 */
	public void invoke(IContainer container, ObjectID elementID) throws InvocationException {
		container.changeResourceUI(elementID);
	}

}
