package info.pppc.pcom.lcdui.action;

import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.pcom.lcdui.PcomUI;
import info.pppc.pcom.lcdui.application.ApplicationElement;

/**
 * The application action that is used to bring up the pcom
 * application viewer that lets a user manipulate the entries
 * of the pcom application manager.
 * 
 * @author Mac
 */
public class ApplicationAction extends ElementAction {

	/**
	 * The resource key for the label of the action.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.lcdui.action.ApplicationAction.TEXT";
	
	/**
	 * The manager of the application action.
	 */
	private IElementManager manager;
	
	/**
	 * Creates an application action using the specified manager.
	 * 
	 * @param manager The manager of the action.
	 */
	public ApplicationAction(IElementManager manager) {
		super(PcomUI.getText(UI_TEXT), true);
		this.manager = manager;
	}
	
	/**
	 * Runs the action and focuses on the last created 
	 * application manager or creates a new one if none
	 * exists yet.
	 */
	public void run() {
		AbstractElement[] elements = manager.getElements();
		for (int i = elements.length - 1; i >= 0; i--) {
			if (elements[i] instanceof ApplicationElement) {
				manager.focusElement(elements[i]);
				return;
			}
		}
		ApplicationElement application = new ApplicationElement(manager);
		manager.addElement(application);
		manager.focusElement(application);
	}
	
}
