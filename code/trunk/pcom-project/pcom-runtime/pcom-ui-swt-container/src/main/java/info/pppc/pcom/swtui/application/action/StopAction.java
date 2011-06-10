package info.pppc.pcom.swtui.application.action;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.jface.action.Action;

/**
 * The stop action stops an application using a control and a descriptor.
 * 
 * @author Mac
 */
public class StopAction extends Action {

	/**
	 * The resource key for the action's text.
	 */
	private static String UI_TEXT = "info.pppc.pcom.swtui.application.action.StopAction.TEXT";
	
	/**
	 * The control used to perform the action.
	 */
	private ApplicationControl control;
	
	/**
	 * The application descriptor that describes the applicaton.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * Creates a new stop action for the specified application descriptor
	 * on the specified application control.
	 * 
	 * @param control The application control.
	 * @param descriptor The application descriptor.
	 */
	public StopAction(ApplicationControl control, ApplicationDescriptor descriptor) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_RED));
		this.control = control;
		this.descriptor = descriptor;
	}
	
	/**
	 * Called whenever the action should be executed.
	 */
	public void run() {
		control.stopApplication(descriptor);
	}

}
