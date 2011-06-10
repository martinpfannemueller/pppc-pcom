package info.pppc.pcom.tutorial.hello;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;

/**
 * The hello instance implements the hello interface. It is created
 * by the hello factory.
 * 
 * @author Mac
 */
public class HelloInstance implements IInstance, IHello {

	/**
	 * The context of the instance used for signaling
	 * and to retrieve the status and the template.
	 */
	private IInstanceContext context;
	
	/**
	 * Creates a new hello instance.
	 */
	public HelloInstance() {
		super();
	}
	
	/**
	 * Called by the container to set the context object
	 * of the instance. 
	 * 
	 * @param context The context object of the instance.
	 */
	public void setContext(IInstanceContext context) {
		Logging.debug(getClass(), "Context set to " + context + ".");
		this.context = context;
	}

	/**
	 * Called by the container to unset the context.
	 */
	public void unsetContext() {
		Logging.debug(getClass(), "Context unset from " + context + ".");
		context = null;
	}

	/**
	 * Called by the container to start the instance.
	 */
	public void start() {
		Logging.debug(getClass(), "Instance started.");
	}

	/**
	 * Called by the container to pause the instance.
	 */
	public void pause() {
		Logging.debug(getClass(), "Instance paused.");
	}

	/**
	 * Called by the container to stop the instance.
	 */
	public void stop() {
		Logging.debug(getClass(), "Instance stopped.");
	}

	/**
	 * Implementation of the hello interface. This method
	 * prints a passed string.
	 * 
	 * @param s The string to print.
	 */
	public void println(String s) {
		Logging.log(getClass(), "Printing: " + s);
	}
	
	/**
	 * Loads the state from a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		// nothing to be done for stateless components
	}
	
	/**
	 * Stores the state to a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to store to.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		// nothing to be done for stateless components		
	}
	
}
