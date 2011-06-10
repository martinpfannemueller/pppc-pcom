package info.pppc.pcom.component.presenter.lcdui;

import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.system.event.ListenerBundle;

/**
 * The control action is a generic action that post an event to the specified
 * listeners whenever the action is executed.
 * 
 * @author Mac
 */
public class PresenterAction extends ElementAction {

	/**
	 * The listener bundle that will receive the event.
	 */
	protected ListenerBundle bundle;
	
	/**
	 * The constant of the event to post to the listeners.
	 */
	protected int event;

	/**
	 * Creates a new control action with the specified text, bundle and
	 * event.
	 * 
	 * @param text The text of the action.
	 * @param bundle The bundle of the action.
	 * @param event The event of the action.
	 */
	public PresenterAction(String text, ListenerBundle bundle, int event) {
		super(text, true);
		this.bundle = bundle;
		this.event = event;
	}
	
	/**
	 * Called whenever the action is executed. This will post the
	 * event to all listeners.
	 */
	public void run() {
		bundle.fireEvent(event);
	}


}
