package info.pppc.pcom.capability.swtui;

import info.pppc.base.swtui.element.IElementManager;

import org.eclipse.jface.action.IContributionItem;

/**
 * This is the ui accessor interface that enables users of ui resources
 * to access the local ui.
 * 
 * @author Mac
 */
public interface ISwtAccessor extends IElementManager {
	
	/**
	 * Adds the specified contribution item to a menu that can
	 * be seen by the user all the time.
	 * 
	 * @param contribution The contribution item to add.
	 */
	public void addContribution(IContributionItem contribution);
	
	/**
	 * Removes the specified contribution item from the menu that
	 * can be seen by the user all the time.
	 * 
	 * @param contribution The contribution to remove.
	 */
	public void removeContribution(IContributionItem contribution);
	
}
