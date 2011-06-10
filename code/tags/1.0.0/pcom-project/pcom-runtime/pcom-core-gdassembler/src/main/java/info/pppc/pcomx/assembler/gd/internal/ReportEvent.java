package info.pppc.pcomx.assembler.gd.internal;

import info.pppc.pcom.system.assembler.Assembly;


/**
 * The report event is used to notify the application about a successful
 * configuration or deconfiguration.
 * 
 * @author Mac
 */
public class ReportEvent extends AbstractEvent {

	/**
	 * A pointer that denotes the instance that is affected.
	 */
	protected Pointer pointer;
	
	/**
	 * The assembly for the subtree or null if the subtree
	 * could not be configured.
	 */
	protected Assembly assembly;
	
	/**
	 * Creates a new report event that reports the successful configuration
	 * or deconfiguration of an instance that has been used previously.
	 * 
	 * @param application The application that is affected by the event.
	 * @param phase The algorithm phase that created the event.
	 * @param pointer The pointer that denotes the instance that is configured.
	 * @param assembly The assembly for the subtree or null if the subtree
	 * 	could not be configured.
	 */
	public ReportEvent(Application application, int phase, Pointer pointer, Assembly assembly) {
		super(application, phase);
		this.pointer = pointer;
		this.assembly = assembly;
	}
	
	/**
	 * Reports the successful configuration or deconfiguration of an
	 * instance. If the root pointer reports the event, the thread 
	 * that waits for the configuration of the application will be
	 * informed, else the corresponding request will be informed.
	 */
	public void perform() {
		if (pointer.length() == 0) {
			// signal termination of application
			application.unblockApplication();
		} else {
			// continue request configuration/deconfiguration
			application.reportInstance(pointer, assembly);
		}
	}
	
	

}
