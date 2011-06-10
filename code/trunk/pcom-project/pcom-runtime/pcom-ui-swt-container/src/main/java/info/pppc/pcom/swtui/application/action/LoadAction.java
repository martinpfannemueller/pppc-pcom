package info.pppc.pcom.swtui.application.action;

import info.pppc.base.swtui.BaseUI;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.jface.action.Action;

/**
 * The load action loads an application using a control and a descriptor.
 * 
 * @author Mac
 */
public class LoadAction extends Action {

	/**
	 * The resource key for the action's text.
	 */
	private static String UI_TEXT = "info.pppc.pcom.swtui.application.action.LoadAction.TEXT";
	
	/**
	 * The control used to perform the action.
	 */
	private ApplicationControl control;
	
	/**
	 * The application descriptor that describes the application.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * Creates a new save action for the specified application descriptor
	 * on the specified application control.
	 * 
	 * @param control The application control.
	 * @param descriptor The application descriptor.
	 */
	public LoadAction(ApplicationControl control, ApplicationDescriptor descriptor) {
		super(PcomUI.getText(UI_TEXT), BaseUI.getDescriptor(BaseUI.IMAGE_LEFT));
		this.control = control;
		this.descriptor = descriptor;
	}
	
	/**
	 * Called whenever the action should be executed.
	 */
	public void run() {
		control.loadApplication(descriptor);
	}

}
