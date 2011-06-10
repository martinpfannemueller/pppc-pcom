package info.pppc.pcom.swtui.action;

import org.eclipse.jface.action.Action;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;

/**
 * The application action displays the application manager.
 * 
 * @author Mac
 */
public class ApplicationAction extends Action {

	/**
	 * The key for the string of the action.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.action.ApplicationAction.TEXT";
	
	/**
	 * The element manager used to create the application.
	 */
	private IElementManager manager;
	
	/**
	 * Creates a new application action using the given element
	 * manager to create the application control.
	 * 
	 * @param manager The element manager to create the control.
	 */
	public ApplicationAction(IElementManager manager) {
		super(PcomUI.getText(UI_TEXT), BaseUI.getDescriptor(BaseUI.IMAGE_LOGO));
		this.manager = manager;
	}

	/**
	 * Called to execute the action. This will try to locate a
	 * running application manager. If none can be found, a new
	 * one will be created. Otherwise, the existing manager will
	 * be focused.
	 */
	public void run() {
		AbstractElementControl[] elements = manager.getElements();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] != null && elements[i] instanceof ApplicationControl) {
				manager.focusElement(elements[i]);
				return;
			}
		}
		ApplicationControl ac = new ApplicationControl(manager);
		manager.addElement(ac);
		manager.focusElement(ac);
	}
	
}
