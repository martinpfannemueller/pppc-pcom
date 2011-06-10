package info.pppc.pcom.tutorial.tree;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceTemplate;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;

/**
 * The hello instance implements the hello interface. It is created
 * by the hello factory.
 * 
 * @author Mac
 */
public class TreeInstance implements IInstance, ITree {

	/**
	 * The context of the instance used for signaling
	 * and to retrieve the status and the template.
	 */
	private IInstanceContext context;
	
	/**
	 * Creates a new hello instance.
	 */
	public TreeInstance() {
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
		context.getStatus().addStatusListener(Event.EVENT_EVERYTHING, new IListener() {
			public void handleEvent(Event event) {
				Logging.log(getClass(), "Status event received:" + event);
			}
		});
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
		String localName = context.getTemplate().getInstance().getName();
		Logging.log(getClass(), "Print called in (" + localName + ") with" + s + ".");
		IInstanceTemplate template = context.getTemplate();
		IInstanceDemandReader[] demands = template.getInstances();
		for (int i = 0; i < demands.length; i++) {
			IInstanceDemandReader demand = demands[i];
			String name = demand.getName();
			IInstanceProxy proxy = context.getProxy(name);
			ITree tree = (ITree)proxy;
			try {
				tree.println(s);
			} catch (InvocationException e) {
				Logging.error(getClass(), "Could not call print on " + name + ".", e);
			}
		}
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
