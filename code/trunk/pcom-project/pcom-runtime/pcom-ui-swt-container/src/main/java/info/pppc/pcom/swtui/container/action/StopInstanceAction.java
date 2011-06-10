package info.pppc.pcom.swtui.container.action;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;
import info.pppc.pcom.system.container.IContainer;

/**
 * The stio instance action is an action that executes a stop instance call on 
 * the container that signals that the instance should be stopped.
 * 
 * @author Mac
 */
public class StopInstanceAction extends AbstractElementAction {

	/**
	 * The taskname displayed while the action runs.
	 */
	private static final String UI_TASK = "info.pppc.pcom.swtui.container.action.StopInstanceAction.TASK";
	
	/**
	 * The text of the action shown by the gui.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.StopInstanceAction.TEXT";
	
	/**
	 * Creates a new stop instance action that issues a stop request.
	 * 
	 * @param control The control that is used for the action.
	 * @param instanceID The id of the instance to change.
	 */
	public StopInstanceAction(ContainerControl control, ObjectID instanceID) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_RED), 
				PcomUI.getText(UI_TASK), control,  instanceID);
	}

	/**
	 * Invokes the stop instance call on the container.
	 * 
	 * @param container The container that executes the instance.
	 * @param elementID The object id of the instance.
	 * @throws InvocationException Thrown if the call fails.
	 */
	public void invoke(IContainer container, ObjectID elementID) throws InvocationException {
		container.stopInstanceUI(elementID);
	}

}
