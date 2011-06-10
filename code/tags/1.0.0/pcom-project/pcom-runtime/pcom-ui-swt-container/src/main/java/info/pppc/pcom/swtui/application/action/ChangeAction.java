package info.pppc.pcom.swtui.application.action;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.jface.action.Action;

/**
 * The change action lets a user reconfigure an application.
 * 
 * @author Mac
 */
public class ChangeAction extends Action {

	/**
	 * The resource key for the action description text.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.application.action.ChangeAction.TEXT";
	
	/**
	 * The application control that uses this action.
	 */
	private ApplicationControl control;
	
	
	/**
	 * The application descriptor that describes the applicaton.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * Creates a new change action for the specified application descriptor
	 * on the specified application control.
	 * 
	 * @param control The application control.
	 * @param descriptor The application descriptor.
	 */
	public ChangeAction(ApplicationControl control, ApplicationDescriptor descriptor) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_BLUE));
		this.control = control;
		this.descriptor = descriptor;
	}
	
	/**
	 * Called whenever the action should be executed.
	 */
	public void run() {
		control.changeApplication(descriptor);
	}

}
