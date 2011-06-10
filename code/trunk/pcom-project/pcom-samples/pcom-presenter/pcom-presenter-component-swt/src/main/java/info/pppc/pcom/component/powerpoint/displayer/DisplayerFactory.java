package info.pppc.pcom.component.powerpoint.displayer;

import info.pppc.pcom.capability.com.ICOMAccessor;
import info.pppc.pcom.capability.ir.IIRAccessor;
import info.pppc.pcom.capability.swtui.ISwtAccessor;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.system.model.component.IFactory;
import info.pppc.pcom.system.model.component.IFactoryContext;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceSetup;
import info.pppc.pcom.system.model.component.IInstanceSkeleton;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;
import info.pppc.pcomx.contract.SimpleExtender;
import info.pppc.pcomx.contract.SimpleMapper;
import info.pppc.pcomx.contract.SimpleValidator;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class DisplayerFactory implements IFactory {
	
	/**
	 * The context used to gain access to the container.
	 */
	private IFactoryContext context;
	
	/**
	 * Default constructor to create a new object.
	 */
	public DisplayerFactory() { }
	
	/**
	 * Returns the human readable name of the factory.
	 *
	 * @return The human readable name of the factory.
	 */
	public String getName() {
		return "Displayer";
	}
	
	/**
	 * Called to set the context which can be used to access the container.
	 *
	 * @param context The context used to gain access to the container.
	 */
	public void setContext(IFactoryContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context that has been set earlier.
	 */
	public void unsetContext() {
		this.context = null;
	}
	
	/**
	 * Called to start the factory. Nothing to be done.
	 */
	public void start() { }
	
	/**
	 * Called to stop the factory. Nothing to be done.
	 */
	public void stop() { }
	
	/**
	 * Creates and returns a new uninitialized instance.
	 *
	 * @return The newly created instance.
	 */
	public IInstance createInstance() {
		return new DisplayerInstance(this);
	}
	
	/**
	 * Returns a new uninitialized proxy for the specified demand.
	 *
	 * @param name The name of the demand whose proxy should be retrieved.
	 * @return The new proxy for the demand or null if the name is illegal.
	 */
	public IInstanceProxy createProxy(String name) {
		return null;
	}
	
	/**
	 * Returns a new uninitialized skeleton for the instance.
	 *
	 * @return The newly created skeleton for the instance.
	 */
	public IInstanceSkeleton createSkeleton() {
		return new DisplayerSkeleton();
	}
	
	/**
	 * Returns the setups that can be derived for the demand or null
	 * if the demand cannot be met by instances of the factory.
	 *
	 * @param demand The demand that needs to be met.
	 * @return The setups for instances or null.
	 */
	public IInstanceSetup[] deriveSetups(IInstanceDemandReader demand) {
		// validate the contract
		if (demand.getEvents().length != 0) return null;
		if (demand.getInterfaces().length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(IPowerpoint.class.getName());
		if (iface == null) return null;
		SimpleValidator validator = new SimpleValidator();
		validator.addFeature("LOCATION", "POWERPOINT");
		validator.addDimension("DISPLAY");
		if (! validator.validate(iface)) return null;
		// validation complete, create offer
		IInstanceSetup setup = context.createSetup();
		// create a matching dynamic provision
		IInstanceProvisionWriter ipro = setup.getInstance();
		ipro.createInterface(IPowerpoint.class.getName());
		SimpleExtender extender = new SimpleExtender();
		extender.addDimension("DISPLAY");
		extender.addFeature("LOCATION", "POWERPOINT");
		extender.extend(iface, ipro.getInterface(IPowerpoint.class.getName()));
		// create a matching ir demand
		setup.createResource("IR");
		IResourceDemandWriter irdemand = setup.getResource("IR");
		irdemand.createInterface(IIRAccessor.class.getName());
		SimpleMapper irmapper = new SimpleMapper();
		irmapper.addFeature("LOCATION", "POWERPOINT", "IR", "LOCAL");
		irmapper.map(iface, irdemand.getInterface(IIRAccessor.class.getName()));
		// create a matching ui demand
		setup.createResource("UI");
		IResourceDemandWriter uidemand = setup.getResource("UI");
		uidemand.createInterface(ISwtAccessor.class.getName());
		SimpleMapper uimapper = new SimpleMapper();
		uimapper.addDimension("DISPLAY", "DISPLAY");
		uimapper.map(iface, uidemand.getInterface(ISwtAccessor.class.getName()));
		// create a matching com demand
		setup.createResource("COM");
		IResourceDemandWriter comdemand = setup.getResource("COM");
		comdemand.createInterface(ICOMAccessor.class.getName());
		return new IInstanceSetup[] { setup };
	}
	
}
