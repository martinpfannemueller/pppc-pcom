package info.pppc.pcom.swtui.container.action;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;
import info.pppc.pcom.system.container.IContainer;

/**
 * The stio resource action is an action that executes a stop resource call on 
 * the container that signals that the resource should be stopped.
 * 
 * @author Mac
 */
public class StopResourceAction extends AbstractElementAction {

	/**
	 * The taskname displayed while the action runs.
	 */
	private static final String UI_TASK = "info.pppc.pcom.swtui.container.action.StopResourceAction.TASK";
	
	/**
	 * The text of the action shown by the gui.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.StopResourceAction.TEXT";
	
	/**
	 * Creates a new stop resource action that issues a stop request.
	 * 
	 * @param control The control that is used for the action.
	 * @param resourceID The id of the resource to change.
	 */
	public StopResourceAction(ContainerControl control, ObjectID resourceID) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_RED), 
				PcomUI.getText(UI_TASK), control,  resourceID);
	}

	/**
	 * Invokes the stop resource call on the container.
	 * 
	 * @param container The container that executes the resource.
	 * @param elementID The object id of the resource.
	 * @throws InvocationException Thrown if the call fails.
	 */
	public void invoke(IContainer container, ObjectID elementID) throws InvocationException {
		container.stopResourceUI(elementID);
	}

}
