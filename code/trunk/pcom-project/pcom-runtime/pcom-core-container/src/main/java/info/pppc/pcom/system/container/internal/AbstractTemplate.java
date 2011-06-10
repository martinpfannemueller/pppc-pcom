package info.pppc.pcom.system.container.internal;

import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.IElementTemplate;

/**
 * The abstract template is the abstract base class for all templates.
 * It extends the setup with a commit event that can be fired by user
 * code through a call to the commit template method.
 * 
 * @author Mac
 */
public abstract class AbstractTemplate extends AbstractSetup implements IElementTemplate {

	/**
	 * The event that is fired if the template is committed. The 
	 * source of the event will be this template. The data object
	 * will be null.
	 */
	public static final int EVENT_TEMPLATE_COMMITTED = 1;
	
	/**
	 * The listeners that listen to events of the template.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * Creates a new template for the specified base contract.
	 * 
	 * @param spec The base contract managed by this template.
	 */
	protected AbstractTemplate(Contract spec) {
		super(spec);
	}
	
	/**
	 * Adds a template listener to this template that is registered for
	 * the specified set of events.
	 * 
	 * @param types The types of events to register for. Multiple event
	 * 	types can be ored.
	 * @param listener The listener that should be registered.
	 */
	public void addTemplateListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes a template listener from the set of listeners that 
	 * receive events from this template.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener that should be unregistered.
	 * @return True if the listener has been removed, false otherwise.
	 */
	public boolean removeTemplateListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	/**
	 * Commits the template and fires a committed event to all registered
	 * listeners.
	 */
	public void commitTemplate() {
		listeners.fireEvent(EVENT_TEMPLATE_COMMITTED);
	}
	
	/**
	 * Determines whether the template is currently matched by the status.
	 * 
	 * @param status The status used for matching.
	 * @param provision A flag that indicates whether the provision of
	 * 	the element bound to the context should be checked also. If the
	 * 	flag is set to false, only the demands will be checked.
	 * @return True if the template is matched by the status.
	 */
	public abstract boolean matches(AbstractStatus status, boolean provision);
	
}
