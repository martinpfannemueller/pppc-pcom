package info.pppc.pcom.capability.lcdui;

import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.lcdui.element.IElementManager;

/**
 * This is the ui accessor interface that enables users of ui resources
 * to access the local ui.
 * 
 * @author Mac
 */
public interface ILcdAccessor extends IElementManager {
	
	/**
	 * Adds the specified action to a menu that can
	 * be seen by the user while he is in the launcher.
	 * 
	 * @param action The action to add.
	 */
	public void addAction(ElementAction action);
	
	/**
	 * Removes the specified action from the menu that
	 * can be seen by the user while he is in the launcher
	 * 
	 * @param action The action to remove.
	 */
	public void removeAction(ElementAction action);
	
}
