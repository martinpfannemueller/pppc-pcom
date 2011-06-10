package info.pppc.pcomx.assembler.gd.internal;

import info.pppc.base.system.SystemID;

/**
 * The remove event removes a device from the configuration.
 * 
 * @author Mac
 */
public class RemoveEvent extends AbstractEvent {
	
	/**
	 * The system that should be removed.
	 */
	protected SystemID system;
	
	/**
	 * Creates a remove event using the specified application and device.
	 * 
	 * @param application The application where the device should be removed.
	 * @param system The device that should be removed from the configuration.
	 */
	public RemoveEvent(Application application, SystemID system) {
		super(application);
		this.system = system;
	}
	
	/**
	 * Performs the event on the specified application.
	 */
	public void perform() {
		application.removeSystem(system);
	}
	
}
