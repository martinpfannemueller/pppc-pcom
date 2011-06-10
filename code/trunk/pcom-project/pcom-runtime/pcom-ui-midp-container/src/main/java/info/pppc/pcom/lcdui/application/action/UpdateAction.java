package info.pppc.pcom.lcdui.application.action;

import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.pcom.lcdui.PcomUI;
import info.pppc.pcom.lcdui.application.ApplicationEditor;

/**
 * The update action is a menu action used by the editor
 * to store the current edits and apply them.
 * 
 * @author Mac
 */
public class UpdateAction extends ElementAction {

	/**
	 * The name of the action as shown in the user interface.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.lcdui.application.action.UpdateAction";
	
	/**
	 * The editor that uses the action.
	 */
	private ApplicationEditor editor;
	
	/**
	 * Creates a new update action for the specified editor.
	 * 
	 * @param editor The editor of the action.
	 */
	public UpdateAction(ApplicationEditor editor) {
		super(PcomUI.getText(UI_TEXT));
		this.editor = editor;
	}
	
	/**
	 * Called whenever the action needs to be executed.
	 */
	public void run() {
		editor.update();
	}


}
