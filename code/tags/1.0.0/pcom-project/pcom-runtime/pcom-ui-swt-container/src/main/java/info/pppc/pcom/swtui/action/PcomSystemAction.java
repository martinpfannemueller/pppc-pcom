package info.pppc.pcom.swtui.action;

import info.pppc.base.swtui.action.SystemAction;
import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.system.SystemControl;
import info.pppc.pcom.swtui.system.PcomSystemControl;

/**
 * The browser action can add a new system browser to the current view
 * of a element manager.
 * 
 * @author Mac
 */
public class PcomSystemAction extends SystemAction {

	/**
	 * Creates a new browser action that can add a system browser to
	 * the specified element manager.
	 * 
	 * @param manager The element manager used to create a new system
	 * 	browser.
	 */
	public PcomSystemAction(IElementManager manager) {
		super(manager);
	}
	
	/**
	 * Adds a new system browser to the specified element manager.
	 */
	public void run() {
		AbstractElementControl[] elements = manager.getElements();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] != null && elements[i] instanceof SystemControl) {
				manager.focusElement(elements[i]);
				return;
			}
		}
		PcomSystemControl bc = new PcomSystemControl(manager);
		manager.addElement(bc);
		manager.focusElement(bc);
	}


}
