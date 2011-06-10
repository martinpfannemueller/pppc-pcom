package info.pppc.pcom.swtui.container.action;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;
import info.pppc.pcom.system.container.IContainer;

/**
 * The change instance action is an action that executes a change instance call on 
 * the container that signals that the instance can no longer run with its current
 * configuration. 
 * 
 * @author Mac
 */
public class ChangeInstanceAction extends AbstractElementAction {

	/**
	 * The taskname displayed while the action runs.
	 */
	private static final String UI_TASK = "info.pppc.pcom.swtui.container.action.ChangeInstanceAction.TASK";
	
	/**
	 * The text of the action shown by the gui.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.ChangeInstanceAction.TEXT";
	
	/**
	 * Creates a new change instance action that signals an instance change
	 * of the specified instance to its parent. This will initiate a reconfiguration. 
	 * 
	 * @param control The control that is used for the action.
	 * @param instanceID The id of the instance to change.
	 */
	public ChangeInstanceAction(ContainerControl control, ObjectID instanceID) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_BLUE), 
				PcomUI.getText(UI_TASK), control,  instanceID);
	}

	/**
	 * Invokes the change instance call on the container.
	 * 
	 * @param container The container that executes the instance.
	 * @param elementID The object id of the instance.
	 * @throws InvocationException Thrown if the call fails.
	 */
	public void invoke(IContainer container, ObjectID elementID) throws InvocationException {
		container.changeInstanceUI(elementID);
	}

}
