package info.pppc.pcom.swtui.system.action;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;

import org.eclipse.jface.action.Action;


/**
 * Creates a container browser for the specified system. 
 * 
 * @author Mac
 */
public class ContainerAction extends Action {

	/**
	 * The resource id of the action text.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.system.action.ContainerAction.TEXT";

	/**
	 * The system id of the system that hosts the container.
	 */
	private SystemID systemID;
	
	/**
	 * The element manager that will receive the container browser.
	 */
	private IElementManager manager;

	/**
	 * The name of the system on which the container runs.
	 */
	private String name;

	/**
	 * Creates a new container browse action that will display a container
	 * browser for the specified system.
	 * 
	 * @param manager The element manager that will receive the new browser.
	 * @param system The system for which a browser will be created.
	 * @param name The human readable name of the container.
	 */
	public ContainerAction(IElementManager manager, SystemID system, String name) {
		super(PcomUI.getText(UI_TEXT), PcomUI.getDescriptor(PcomUI.IMAGE_CONTAINER));
		this.manager = manager;
		this.systemID = system;
		this.name = name;
	}
	
	/**
	 * Called by jface whenever the action is executed. This action will
	 * create a new container browser that will receive the focus.
	 */
	public void run() {
		AbstractElementControl[] cs = manager.getElements();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] != null && cs[i] instanceof ContainerControl) {
				ContainerControl c = (ContainerControl)cs[i];
				if (c.getSystemID().equals(systemID)) {
					manager.focusElement(c);
					return;
				}
			}
		}
		ContainerControl cc = new ContainerControl(manager, systemID, name);
		manager.addElement(cc);
		manager.focusElement(cc);
	}

}
