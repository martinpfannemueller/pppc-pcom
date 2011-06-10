package info.pppc.pcom.swtui.application.action;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;
import info.pppc.pcom.swtui.application.builder.ApplicationBuilderControl;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.jface.action.Action;

/**
 * The create action lets a user create a new application.
 * 
 * @author Mac
 */
public class CreateAction extends Action {

	/**
	 * The resource key for the action description text.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.application.action.CreateAction.TEXT";
	
	/**
	 * The application control that uses this action.
	 */
	private ApplicationControl control;
	
	/**
	 * Creates a new create action using the specified application control.
	 * 
	 * @param control The application control used to create a new
	 * 	element.
	 */
	public CreateAction(ApplicationControl control) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_BUILDER));
		this.control = control;
	}
	
	/**
	 * Executes the create action and creates a new
	 * contract builder with an empty contract.
	 */
	public void run() {
		ApplicationBuilderControl b = new ApplicationBuilderControl
			(control, new ApplicationDescriptor());
		IElementManager m = control.getManager();
		m.addElement(b);
		m.focusElement(b);
	}

}
