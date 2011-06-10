package info.pppc.pcom.swtui.application.action;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;
import info.pppc.pcom.swtui.application.builder.ApplicationBuilderControl;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.jface.action.Action;

/**
 * The edit action lets a user edit an existing application descriptor.
 * 
 * @author Mac
 */
public class EditAction extends Action {

	/**
	 * Returns the resource key for the text of the action.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.application.action.EditAction.TEXT";
	
	/**
	 * The application control that will be used to create a new
	 * builder.
	 */
	private ApplicationControl control;
	
	/**
	 * The application descriptor that is modified by the builder.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * Creates a new edit action for the specified control and descriptor.
	 * 
	 * @param control The control that has created the edit action.
	 * @param descriptor The descriptor that should be edited.
	 */
	public EditAction(ApplicationControl control, ApplicationDescriptor descriptor) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_BUILDER));
		this.control = control;
		this.descriptor = descriptor;
	}

	/**
	 * Called when the action has been clicked. If the
	 * descriptor does not have a builder control yet,
	 * this action will create a new builder control for
	 * it.
	 */
	public void run() {
		IElementManager manager = control.getManager();
		AbstractElementControl[] controls = manager.getElements();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null && controls[i] instanceof ApplicationBuilderControl) {
				ApplicationBuilderControl builder = (ApplicationBuilderControl)controls[i];
				if (descriptor.getApplicationID() != null) {
					if (descriptor.getApplicationID().equals
							(builder.getDescriptor().getApplicationID())) {
						manager.focusElement(builder);
						return;
					}
				}
			}
		}
		ApplicationBuilderControl builder = new ApplicationBuilderControl(control, descriptor);
		manager.addElement(builder);
		manager.focusElement(builder);
	}
}
