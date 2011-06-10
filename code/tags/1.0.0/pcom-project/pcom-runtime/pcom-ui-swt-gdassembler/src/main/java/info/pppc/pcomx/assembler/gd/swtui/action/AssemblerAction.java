package info.pppc.pcomx.assembler.gd.swtui.action;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI;
import info.pppc.pcomx.assembler.gd.swtui.control.AssemblerControl;

import org.eclipse.jface.action.Action;

/**
 * The gc assembler action opens the gc assembler visualization
 * control.
 * 
 * @author Mac
 */
public class AssemblerAction extends Action {

	/**
	 * The text of the action as shown in the menu.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gd.swtui.action.AssemblerAction.TEXT";
	
	/**
	 * The manager that will be used to create the visualizer.
	 */
	private IElementManager manager;
	
	/**
	 * Creates a new visualizer action that will add a visualizer
	 * to the specified manager.
	 * 
	 * @param manager The manager used to show the visualizer.
	 */
	public AssemblerAction(IElementManager manager) {
		super(GDAssemblerUI.getText(UI_TEXT), GDAssemblerUI.getDescriptor(GDAssemblerUI.IMAGE_GD));
		this.manager = manager;
	}
	
	/**
	 * Called whenever the action is executed. If the manager
	 * does not have a visualizer, it will be created. Otherwise,
	 * the manager will be asked to put the focus on the existing
	 * visualizer.
	 */
	public void run() {
		AbstractElementControl[] controls = manager.getElements();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] instanceof AssemblerControl) {
				manager.focusElement(controls[i]);
				return;
			}
		}
		AssemblerControl control = new AssemblerControl(manager);
		manager.addElement(control);
		manager.focusElement(control);
	}
	
	
}
