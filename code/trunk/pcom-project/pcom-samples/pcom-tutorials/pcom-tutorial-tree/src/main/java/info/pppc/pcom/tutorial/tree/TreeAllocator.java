package info.pppc.pcom.tutorial.tree;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.capability.IAllocator;
import info.pppc.pcom.system.model.capability.IAllocatorContext;
import info.pppc.pcom.system.model.capability.IResourceContext;
import info.pppc.pcom.system.model.capability.IResourceSetup;
import info.pppc.pcom.system.model.capability.IResourceTemplate;
import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;

import java.util.Vector;

/**
 * The tree allocator is a simple allocator that provides a fixed 
 * amount of resources. The resources correspond to the ITree
 * interface and provide an accessor object of that type.
 * 
 * @author Mac
 */
public class TreeAllocator implements IAllocator {

	/**
	 * The tree accessor is a simple accessor that is provided to
	 * support the tree interface resource accessor.
	 * 
	 * @author Mac
	 */
	private class TreeAccessor implements ITree {
		/**
		 * Appends some text to the string and prints it.
		 * 
		 * @param s The string to pring.
		 */
		public void println(String s) {
			Logging.debug(getClass(), "Tree accessor received print with: " + s);
		}
	}
	
	
	/**
	 * The amount of resources available from this allocator.
	 */
	private int amount;
	
	/**
	 * The context used to negotiate resource assignments.
	 */
	private IAllocatorContext context;
	
	/**
	 * The resource contexts of resources that are currently
	 * started.
	 */
	private Vector started = new Vector();
	
	/**
	 * The resource contexts of resources that are currently
	 * paused.
	 */
	private Vector paused = new Vector();
	
	/**
	 * Creates a new tree allocator that can allocate the specified
	 * amount of resources. 
	 * 
	 * @param amount The amount of resources generated by this
	 * 	tree allocator.
	 */
	public TreeAllocator(int amount) {
		if (amount < 0) throw new IllegalArgumentException
			("Resources cannot be negative (" + amount + ").");
		this.amount = amount;
	}

	/**
	 * Returns the name of the allocator.
	 * 
	 * @return The name of the allocator.
	 */
	public String getName() {
		Logging.debug(getClass(), "Name retrieved");
		return getClass().getName();
	}

	/**
	 * Called by the container before the allocator
	 * is started. This will set the allocator context
	 * to the specified value.
	 * 
	 * @param context The context object used to communicate
	 * 	with the container.
	 */
	public void setContext(IAllocatorContext context) {
		Logging.debug(getClass(), "Context set to " + context + ".");
		this.context = context;
	}

	/**
	 * Called to unset the context after the allocator
	 * has been stopped.
	 */
	public void unsetContext() {
		Logging.debug(getClass(), "Context unset.");
		context = null;
	}

	/**
	 * Called to start the allocator.
	 */
	public void start() {
		Logging.debug(getClass(), "Allocator started.");
	}

	/**
	 * Called to stop the allocator.
	 */
	public void stop() {
		Logging.debug(getClass(), "Allocator stopped (" + started.size() + "/" + paused.size() + ")");
		
	}

	/**
	 * Derives the resource setups for the specified demand.
	 * 
	 * @param demand The demand that describes the requirements to match.
	 * @return An array of matching contracts or null if none can
	 * 	be created.
	 */
	public IResourceSetup[] deriveSetups(IResourceDemandReader demand) {
		ITypeDemandReader[] ifaces = demand.getInterfaces();
		if (ifaces.length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(ITree.class.getName());
		if (iface == null) return null;
		IDimensionDemandReader[] dimensions = iface.getDimensions();
		if (dimensions.length != 0) return null;
		IResourceSetup setup = context.createSetup();
		IResourceProvisionWriter provision = setup.getResource();
		provision.createInterface(ITree.class.getName());
		return new IResourceSetup[] { setup };
	}

	/**
	 * Returns the resource estimate for the specified template. Each
	 * template created by this class requires one resource.
	 * 
	 * @param template The resource template to estimate.
	 * @return The resource estimate for the template.
	 */
	public int[] estimateTemplate(IResourceTemplate template) {
		return new int[] { 1 };
	}

	/**
	 * Starts (reserves and starts) the specified resource and tries
	 * to allocate the accessor object if this has not been done 
	 * already.
	 * 
	 * @param context The context object that needs to be started.
	 * @return True if the resouce could be started (ie. if there
	 * 	were enough resources to start it.
	 */
	public boolean startResource(IResourceContext context) {
		Logging.debug(getClass(), "Start resource called with " + context + ".");
		if (amount - started.size() > 0) {
			if (! paused.removeElement(context)) {
				context.setAccessor(new TreeAccessor());
				context.getStatus().addStatusListener(Event.EVENT_EVERYTHING, new IListener() {
					public void handleEvent(Event event) {
						Logging.log(getClass(), "Resource received: " + event);
					}
				});
			}
			started.addElement(context);
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Called to pause a resource. This will temporarily free the
	 * resource.
	 * 
	 * @param context The context object to pause.
	 */
	public void pauseResource(IResourceContext context) {
		Logging.debug(getClass(), "Pause resource called with " + context + ".");
		started.removeElement(context);
		paused.addElement(context);
	}

	/**
	 * Called to stop the resource. This will free the resource
	 * and finalize the accessor object.
	 * 
	 * @param context The context object to stop.
	 */
	public void stopResource(IResourceContext context) {
		Logging.debug(getClass(), "Stop resource called with " + context + ".");
		paused.removeElement(context);
		started.removeElement(context);
		context.setAccessor(null);
	}

	/**
	 * Returns the amount of available free resources. This is
	 * computed by the amount of total resources minus the number of
	 * started instances.
	 * 
	 * @return The free available resources.
	 */
	public int[] freeResources() {
		return new int[] { amount - started.size() };
	}

	/**
	 * Returns the amount of total resources that can be issued
	 * by this allocator. This is the value that has been set
	 * during initialization.
	 * 
	 * @return The amount of total resources that this allocator
	 * 	has.
	 */
	public int[] totalResources() {
		return new int[] { amount };
	}

}
