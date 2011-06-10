package info.pppc.pcom.swtui.system;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.system.SystemControl;

/**
 * The extended browser control provides the system browser with
 * support for pcom well known services. It replaces the label
 * and action providers of the base ui with drop in pcom replacements.
 * 
 * @author Mac
 */
public class PcomSystemControl extends SystemControl {

	/**
	 * Creates a new extended browser control with the specified
	 * manager.
	 * 
	 * @param manager The manager of the browser control.
	 */
	public PcomSystemControl(IElementManager manager) {
		super(manager);
		this.actionProvider = new PcomSystemActionProvider();
		this.labelProvider = new PcomSystemLabelProvider();
	}

}
