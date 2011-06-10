package info.pppc.pcomx.assembler.gd.internal;


/**
 * The release event is used to notify the application that a
 * certain previously configured instance is no longer required
 * and should be released.
 * 
 * @author Mac
 */
public class ReleaseEvent extends AbstractEvent {

	/**
	 * The pointer that denotes the instance to release.
	 */
	protected Pointer pointer;
	
	/**
	 * Creates a new release event for the specified application and
	 * pointer.
	 * 
	 * @param application The application that is affected by the release.
	 * @param phase The phase of the release event.
	 * @param pointer The pointer that denotes the instance to release.
	 */
	public ReleaseEvent(Application application, int phase, Pointer pointer) {
		super(application, phase);
		this.pointer = pointer;
	}

	/**
	 * Tells the specified instance that it should be released. The
	 * instance will first release its children and when all children
	 * are released, the instance will remove itself and its resources
	 * and will report an unsuccessful configuration.
	 */
	public void perform() {
		application.releaseRequest(pointer);
	}

}
