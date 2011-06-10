package info.pppc.pcom.swtui.application.builder;

import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

import org.eclipse.swt.widgets.Composite;

/**
 * The base class for all builders used by the application builder control.
 * 
 * @author Mac
 */
public abstract class AbstractBuilder {

	/**
	 * The event that signals that the builder is done. The source will
	 * be the builder, the data object will be null.
	 */
	public static final int EVENT_DONE = 1;
	
	/**
	 * The composite used to display the builder.
	 */
	private Composite composite;
	
	/**
	 * The listeners that are registered for done events.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * Creates a new builder using the specified composite.
	 * 
	 * @param composite The composite that displays the builder.
	 */
	public AbstractBuilder(Composite composite) {
		this.composite = composite;
	}
	
	/**
	 * Returns the composite that is used to display the builder.
	 * 
	 * @return The composite used to display the builder.
	 */
	protected Composite getComposite() {
		return composite;
	}
	
	/**
	 * Creates the control for the specified tree node.
	 * 
	 * @param node The tree node that provides the input.
	 */
	public abstract void createControl(TreeNode node);
	
	
	/**
	 * Adds the specified listener to the set of listeners registered
	 * for the specified event. Possible events are defined by the event
	 * constants in this class.
	 * 
	 * @param types The types of events to register for.
	 * @param listener The listener to register.
	 */
	public void addBuilderListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes the specified listener from the set of specified events.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister.
	 * @return True if successful, false otherwise.
	 */
	public boolean removeBuilderListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
	
	/**
	 * Called to signal that the builder has finished.
	 */
	protected void done() {
		listeners.fireEvent(EVENT_DONE);
	}

	/**
	 * Disposes the controls of the builder. This method should
	 * be overwritten by concrete builders.
	 */
	public void dispose() {
		// nothing to be done here 
	}
	
}
