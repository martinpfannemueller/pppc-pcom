package info.pppc.pcom.tutorial.hello;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.component.IFactory;
import info.pppc.pcom.system.model.component.IFactoryContext;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceSetup;
import info.pppc.pcom.system.model.component.IInstanceSkeleton;
import info.pppc.pcom.system.model.component.IInstanceTemplate;
import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;

/**
 * The hello factory is used to negotiate setups for hello instances. 
 * 
 * @author Mac
 */
public class HelloFactory implements IFactory {

	/**
	 * The context of the factory used to access the container.
	 */
	private IFactoryContext context;
	
	/**
	 * Creates a new factory of the component.
	 */
	public HelloFactory() {
		super();
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
		return new HelloInstance();
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
		ITypeDemandReader iface = demand.getInterface(IHello.class.getName());
		if (iface == null) return false;
		// check whether the demand requires no dimensions
		IDimensionDemandReader[] dims = iface.getDimensions();
		if (dims.length != 0) return false;
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
			Logging.debug(getClass(), "Setup derived.");
			// create contract for interface without any dimensions
			IInstanceSetup setup = context.createSetup();
			IInstanceProvisionWriter p = setup.getInstance();
			p.createInterface(IHello.class.getName());
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
		return getClass().getName();
	}

	/**
	 * Returns a new instance of the components created
	 * by this factory.
	 * 
	 * @return A new component instance.
	 */
	public IInstance createInstance() {
		return new HelloInstance();
	}

	/**
	 * Returns the skeleton of the component instances
	 * created by this factory.
	 * 
	 * @return A component instance skeleton.
	 */
	public IInstanceSkeleton createSkeleton() {
		return new HelloSkeleton();
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
		return null;
	}
	
}
