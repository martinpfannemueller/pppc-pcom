package info.pppc.pcom.tutorial.tree;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.component.IFactory;
import info.pppc.pcom.system.model.component.IFactoryContext;
import info.pppc.pcom.system.model.component.IFactoryStatus;
import info.pppc.pcom.system.model.component.IFactoryTemplate;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceSetup;
import info.pppc.pcom.system.model.component.IInstanceSkeleton;
import info.pppc.pcom.system.model.component.IInstanceTemplate;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureDemandReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IDimensionDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeDemandWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;

/**
 * The hello factory is used to negotiate setups for hello instances. 
 * 
 * @author Mac
 */
public class TreeFactory implements IFactory {

	/**
	 * The context of the factory used to access the container.
	 */
	private IFactoryContext context;
	
	/**
	 * The flag that indicates whether instances of this component
	 * will rely on resouces of the tree interface type.
	 */
	private boolean resources;
	
	/**
	 * Creates a new factory of the component. The flag indicates
	 * whether the factory will create components that use resources
	 * or whether it will simply create components that use other
	 * components.
	 * 
	 * @param resources Set to true to use resources too, false to
	 * 	only rely on instances.
	 */
	public TreeFactory(boolean resources) {
		this.resources = resources;
	}
	
	/**
	 * Called to set the context of the factory.
	 * 
	 * @param context The context of the factory.
	 */
	public void setContext(IFactoryContext context) {
		Logging.debug(getClass(), "Context object set.");
		this.context = context;
	}

	/**
	 * Called to release the context of the factory.
	 * 
	 */
	public void unsetContext() {
		Logging.debug(getClass(), "Context object unset.");
		context = null;
	}

	/**
	 * Called to start the factory.
	 */
	public void start() {
		// update contract to reflect required resources
		Logging.debug(getClass(), "Factory started.");
		if (resources) {
			IFactoryTemplate template = context.getTemplate();
			template.createResource("Resource");
			IResourceDemandWriter resource = template.getResource("Resource");
			resource.createInterface(ITree.class.getName());
			context.getStatus().addStatusListener(Event.EVENT_EVERYTHING, new IListener() {
				public void handleEvent(Event event) {
					Logging.log(getClass(), "Status event received: " + event);
					if (event.getType() == IFactoryStatus.EVENT_RESOURCE_REMOVED) {
						context.getTemplate().commitTemplate();
					}
				}
			});
		}
		
	}

	/**
	 * Called to stop the factory.
	 */
	public void stop() {
		// cleanup and release resources
		Logging.debug(getClass(), "Factory stopped.");
	}

	/**
	 * Called to create an instance.
	 * 
	 * @param template The template for the instance.
	 * @return The instance that has been created or null if
	 * 	the instance cannot be created for the template.
	 */
	public IInstance createInstance(IInstanceTemplate template) {
		Logging.debug(getClass(), "Instance created.");
		return new TreeInstance();
	}

	/**
	 * Determines whether the factory can create an offer that is compatible
	 * with the specified demand.
	 * 
	 * @param demand The demand to check.
	 * @return True if the factory can create such a demand, false otherwise.
	 */
	private boolean checkDemand(IInstanceDemandReader demand) {
		// check whether there are no events, if so return
		ITypeDemandReader[] events = demand.getEvents();
		if (events.length != 0) return false;
		// check whether the demand requires only a hello interface
		ITypeDemandReader[] ifaces = demand.getInterfaces();
		if (ifaces.length != 1) return false;
		ITypeDemandReader iface = demand.getInterface(ITree.class.getName());
		if (iface == null) return false;
		// check whether the demand requires no dimensions
		IDimensionDemandReader[] dims = iface.getDimensions();
		if (dims.length != 2) return false;
		IDimensionDemandReader width = iface.getDimension("WIDTH");
		IDimensionDemandReader height = iface.getDimension("HEIGHT");
		if (width == null || height == null) return false;
		IFeatureDemandReader wvalue = width.getFeature("VALUE");
		IFeatureDemandReader hvalue = height.getFeature("VALUE");
		if (wvalue == null || hvalue == null) return false;
		if (wvalue.getComparator() != IContract.IFEQ 
				|| hvalue.getComparator() != IContract.IFEQ) return false;
		if (wvalue.getValue() == null 
				|| hvalue.getValue() == null) return false;
		if (! (wvalue.getValue() instanceof Integer) 
				|| ! (hvalue.getValue() instanceof Integer)) return false;
		return true;
	}
	
	/**
	 * Called to derive a contract for a specific instance demand.
	 * 
	 * @param demand The demand towards the component instance.
	 * @return Null if the demand cannot be met, instance contracts
	 * 	that can be used to meet the demand.
	 */
	public IInstanceSetup[] deriveSetups(IInstanceDemandReader demand) {
		if (checkDemand(demand)) {
			IInstanceSetup setup = context.createSetup();
			// create contract with reduced height and same width
			int width = ((Integer)demand.getInterface(ITree.class.getName())
				.getDimension("WIDTH").getFeature("VALUE").getValue()).intValue();
			int height = ((Integer)demand.getInterface(ITree.class.getName())
				.getDimension("HEIGHT").getFeature("VALUE").getValue()).intValue();
			// create compatible provision
			IInstanceProvisionWriter p = setup.getInstance();
			p.createInterface(ITree.class.getName());
			ITypeProvisionWriter t = p.getInterface(ITree.class.getName());
			t.createDimension("WIDTH");
			IDimensionProvisionWriter wd = t.getDimension("WIDTH");
			wd.createFeature("VALUE", new Integer(width));
			t.createDimension("HEIGHT");
			IDimensionProvisionWriter hd = t.getDimension("HEIGHT");
			hd.createFeature("VALUE", new Integer(height));
			// create demands depending on feature specification
			if (height > 0 && width > 0) {
				for (int i = 0; i < width; i++) {
					String name = demand.getName() + "<" + i + ", " + (height - 1) + ">";
					setup.createInstance(name);
					IInstanceDemandWriter idw = setup.getInstance(name);
					idw.createInterface(ITree.class.getName());
					ITypeDemandWriter itw = idw.getInterface(ITree.class.getName());
					itw.createDimension("WIDTH");
					IDimensionDemandWriter iww = itw.getDimension("WIDTH");
					iww.createFeature("VALUE", IContract.IFEQ, new Integer(width));
					itw.createDimension("HEIGHT");
					IDimensionDemandWriter ihw = itw.getDimension("HEIGHT");
					ihw.createFeature("VALUE", IContract.IFEQ, new Integer(height - 1));
				}
			}
			// check whether this instance should use resources, too
			if (resources) {
				setup.createResource("Resource");
				IResourceDemandWriter resource = setup.getResource("Resource");
				resource.createInterface(ITree.class.getName());
			}
			Logging.debug(getClass(), "Setup derived");
			return new IInstanceSetup[] { setup };			
		} else {
			Logging.debug(getClass(), "Setup not derived.");
			return null;
		}
	}


	/**
	 * Returns the human readable name of the factory.
	 * 
	 * @return The human readable name of the factory.
	 */
	public String getName() {
		Logging.debug(getClass(), "Factory name retrieved.");
		return getClass().getName();
	}

	/**
	 * Returns a new instance of the components created
	 * by this factory.
	 * 
	 * @return A new component instance.
	 */
	public IInstance createInstance() {
		Logging.debug(getClass(), "Instance created.");
		return new TreeInstance();
	}

	/**
	 * Returns the skeleton of the component instances
	 * created by this factory.
	 * 
	 * @return A component instance skeleton.
	 */
	public IInstanceSkeleton createSkeleton() {
		Logging.debug(getClass(), "Skeleton created.");
		return new TreeSkeleton();
	}

	/**
	 * Returns the proxy for the specified name. Since this
	 * component does not have a dependency, this method will
	 * never be called.
	 * 
	 * @param name The name of the dependency whose proxy should
	 * 	be returned.
	 * @return The proxy for the dependency, always null for this
	 * 	component.
	 */
	public IInstanceProxy createProxy(String name) {
		Logging.debug(getClass(), "Proxy created for " + name + ".");
		return new TreeProxy();
	}
	
}
