package info.pppc.pcom.swtui.system;

import info.pppc.base.swtui.system.SystemLabelProvider;
import info.pppc.base.system.ObjectID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.application.IApplicationManager;
import info.pppc.pcom.system.container.IContainer;

/**
 * The extended label provider extends the system browser of the
 * base ui with well known service labels for pcom-related serives.
 * 
 * @author Mac
 */
public class PcomSystemLabelProvider extends SystemLabelProvider {

	/**
	 * The origin key for services.
	 */
	public static final String UI_SERVICE = "info.pppc.pcom.swtui.system.PcomSystemLabelProvider.SERVICE";
	
	/**
	 * Creates a new extended label provider for the system browser.
	 */
	public PcomSystemLabelProvider() {
		super();
	}

	/**
	 * Extends the labels of services with pcom-related services.
	 * 
	 * @param id The object id of the well known service.
	 * @return The text of the service.
	 */
	protected String getServiceText(ObjectID id) {
		if (IContainer.CONTAINER_ID.equals(id)) {
			return PcomUI.getText(UI_SERVICE + "." + 3);
		} else if (IApplicationManager.APPLICATION_MANAGER_ID.equals(id)) {
			return PcomUI.getText(UI_SERVICE + "." + 4);
		} else if (new ObjectID(5).equals(id)) {
			return PcomUI.getText(UI_SERVICE + "." + 5);
		} else if (new ObjectID(6).equals(id)) {
			return PcomUI.getText(UI_SERVICE + "." + 6);
		} else if (new ObjectID(7).equals(id)) {
			return PcomUI.getText(UI_SERVICE + "." + 7);
		} else {
			return super.getServiceText(id);	
		}
	}
	
	
}
