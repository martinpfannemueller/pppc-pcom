package info.pppc.pcom.component.presenter;

import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.pcom.capability.lcdui.ILcdAccessor;
import info.pppc.pcom.component.filesystem.IFilesystem;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.system.model.component.IFactory;
import info.pppc.pcom.system.model.component.IFactoryContext;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceSetup;
import info.pppc.pcom.system.model.component.IInstanceSkeleton;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeDemandWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleExtender;
import info.pppc.pcomx.contract.SimpleMapper;
import info.pppc.pcomx.contract.SimpleValidator;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class PresenterFactory implements IFactory {
	
	/**
	 * Called whenever an instance that is constrained to a certain
	 * powerpoint should change its instance provision.
	 */
	public static final int EVENT_INSTANCE_CHANGE = 1;
	
	/**
	 * The context used to gain access to the container.
	 */
	private IFactoryContext context;
	
	/**
	 * The listeners that are informed whenever the remote id changes.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * Default constructor to create a new object.
	 */
	public PresenterFactory() { }
	
	/**
	 * Returns the human readable name of the factory.
	 *
	 * @return The human readable name of the factory.
	 */
	public String getName() {
		return "Presenter";
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
	 * Called to start the factory.
	 */
	public void start() { }
	
	/**
	 * Called to stop the factory.
	 */
	public void stop() { }
	
	/**
	 * Creates and returns a new uninitialized instance.
	 *
	 * @return The newly created instance.
	 */
	public IInstance createInstance() {
		return new PresenterInstance(this);
	}
	
	/**
	 * Returns a new uninitialized proxy for the specified demand.
	 *
	 * @param name The name of the demand whose proxy should be retrieved.
	 * @return The new proxy for the demand or null if the name is illegal.
	 */
	public IInstanceProxy createProxy(String name) {
		if (name.equals("FS")) {
			return new FilesystemProxy();
		}
		if (name.equals("PPT")) {
			return new PowerpointProxy();
		}
		return null;
	}
	
	/**
	 * Returns a new uninitialized skeleton for the instance.
	 *
	 * @return The newly created skeleton for the instance.
	 */
	public IInstanceSkeleton createSkeleton() {
		return new PresenterSkeleton();
	}
	
	/**
	 * Returns the setups that can be derived for the demand or null
	 * if the demand cannot be met by instances of the factory.
	 *
	 * @param demand The demand that needs to be met.
	 * @return The setups for instances or null.
	 */
	public IInstanceSetup[] deriveSetups(IInstanceDemandReader demand) {
		if (demand.getEvents().length != 0) return null;
		if (demand.getInterfaces().length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(IPresenter.class.getName());
		if (iface == null) return null;
		SimpleValidator validator = new SimpleValidator();
		validator.addFeature("LOCATION", "POWERPOINT");
		validator.addDimension("DISPLAY");
		if (! validator.validate(iface)) return null;
		// validation complete, create offer
		IInstanceSetup setup = context.createSetup();
		IInstanceProvisionWriter ipro = setup.getInstance();
		ipro.createInterface(IPresenter.class.getName());
		// add fixed presenter location
		ITypeProvisionWriter ifpro = ipro.getInterface(IPresenter.class.getName());
		ifpro.createDimension("LOCATION");
		// create a matching dynamic provision
		SimpleExtender extender = new SimpleExtender();
		extender.addDimension("DISPLAY");
		extender.addFeature("LOCATION", "POWERPOINT");
		extender.extend(iface, ipro.getInterface(IPresenter.class.getName()));
		// create a matching ppt demand
		setup.createInstance("PPT");
		IInstanceDemandWriter pptdemand = setup.getInstance("PPT");
		pptdemand.createInterface(IPowerpoint.class.getName());
		ITypeDemandWriter pptifdem = pptdemand.getInterface(IPowerpoint.class.getName());
		SimpleMapper ptmapper = new SimpleMapper();
		ptmapper.addFeature("LOCATION", "POWERPOINT", "LOCATION", "POWERPOINT");
		ptmapper.addDimension("DISPLAY", "DISPLAY");
		ptmapper.map(iface, pptifdem);
		// create a matching filesystem demand
		setup.createInstance("FS");
		IInstanceDemandWriter fsdemand = setup.getInstance("FS");
		fsdemand.createInterface(IFilesystem.class.getName());
		// create a matching ui demand
		setup.createResource("UI");
		IResourceDemandWriter uidemand = setup.getResource("UI");
		uidemand.createInterface(ILcdAccessor.class.getName());
		return new IInstanceSetup[] { setup };
	}
	
	/**
	 * Adds a listener to the factory that receives instance events.
	 * Possible events are described by the event constants defined
	 * in this class.
	 * 
	 * @param types The types of events to listenen for.
	 * @param listener The listener to register.
	 */
	protected void addInstanceListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes a previously installed listener from the set of registered
	 * listeners.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been unregistered, false otherwise.
	 */
	protected boolean removeInstanceListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	
}
