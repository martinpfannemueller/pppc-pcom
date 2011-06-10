package info.pppc.pcomx.assembler.gd.internal;

/**
 * The gd assembler transforms incoming requests in events and schedules
 * the events using the application. At some later point in time, the
 * application will simply perform the event using the perform method.
 * 
 * @author Mac
 */
public abstract class AbstractEvent {

	/**
	 * The application of the event.
	 */
	protected Application application;
	
	/**
	 * The phase of the event or -1 to denote that this
	 * event should be processed independently from the
	 * current phase of the application.
	 */
	private int phase;
	
	/**
	 * Creates a new event for the specified application with an
	 * unconditional phase.
	 * 
	 * @param application The application that is modified
	 * 	by the event.
	 */
	protected AbstractEvent(Application application) {
		this.application = application;
		this.phase = -1;
	}
	
	/**
	 * Creates a new event for the specified application with the
	 * specified phase.
	 * 
	 * @param application The application that is modified by the
	 * 	event.
	 * @param phase The phase of the event, set to -1 for unconditinal
	 * 	execution.
	 */
	protected AbstractEvent(Application application, int phase) {
		this.application = application;
		this.phase = phase;
	}
	
	/**
	 * Returns the phase of the event or -1 for unconditional
	 * execution.
	 *  
	 * @return The phase of the event.
	 */
	public int getPhase() {
		return phase;
	}
	
	/**
	 * Executes the specified event.
	 */
	public abstract void perform();
	
}
