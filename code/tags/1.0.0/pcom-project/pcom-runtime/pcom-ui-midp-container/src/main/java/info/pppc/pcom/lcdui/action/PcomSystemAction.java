package info.pppc.pcom.lcdui.action;

import info.pppc.base.lcdui.action.SystemAction;
import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.pcom.lcdui.system.PcomSystemElement;

/**
 * The pcom system action can be used as drop in replacement
 * to create a base system browser with pcom extensions.
 * 
 * @author Mac
 */
public class PcomSystemAction extends SystemAction {

	
	/**
	 * Creates a new system browser action using the specified manager.
	 * 
	 * @param manager The manager used by the action.
	 */
	public PcomSystemAction(IElementManager manager) {
		super(manager);
	}
	
	/**
	 * Called whenever the action is executed. This will focus the
	 * last existing system browser with pcom extensions or it will 
	 * open a new one if none exists.
	 */
	public void run() {
		AbstractElement[] elements = manager.getElements();
		for (int i = elements.length - 1; i >= 0; i--) {
			if (elements[i] instanceof PcomSystemElement) {
				manager.focusElement(elements[i]);
				return;
			}
		}
		PcomSystemElement system = new PcomSystemElement(manager);
		manager.addElement(system);
		manager.focusElement(system);
	}
	
}
