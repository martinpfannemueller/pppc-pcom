package info.pppc.pcom.component.filesystem;

import info.pppc.pcom.system.model.component.IFactoryContext;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceProxy;
import info.pppc.pcom.system.model.component.IInstanceSetup;
import info.pppc.pcom.system.model.component.IInstanceSkeleton;
import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;

/**
 * This class implements the factory for filesystem instances.
 * These instances provide remote access to the local file system.
 *
 * @author Mac
 */
public class FilesystemFactory implements info.pppc.pcom.system.model.component.IFactory {
	
	/**
	 * The name of the factory used within the ui.
	 */
	private static final String COMPONENT_NAME = "Filesystem";
	
	/**
	 * The context used to gain access to the container.
	 */
	private IFactoryContext context;
	
	/**
	 * Default constructor to create a new object.
	 */
	public FilesystemFactory() { }
	
	/**
	 * Returns the human readable name of the factory.
	 *
	 * @return The human readable name of the factory.
	 */
	public String getName() {
		return COMPONENT_NAME;
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
	 * Called to start the factory. Nothing to be done here.
	 */
	public void start() { }
	
	/**
	 * Called to stop the factory. Nothing to be done here.
	 */
	public void stop() { }
	
	/**
	 * Creates and returns a new uninitialized instance.
	 *
	 * @return The newly created instance.
	 */
	public IInstance createInstance() {
		return new FilesystemInstance(this);
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
		return new FilesystemSkeleton();
	}
	
	/**
	 * Returns the setups that can be derived for the demand or null
	 * if the demand cannot be met by instances of the factory.
	 *
	 * @param demand The demand that needs to be met.
	 * @return The setups for instances or null.
	 */
	public IInstanceSetup[] deriveSetups(IInstanceDemandReader demand) {
		// check whether there are no events, if so return
		ITypeDemandReader[] events = demand.getEvents();
		if (events.length != 0) return null;
		// check whether the demand requires only a hello interface
		ITypeDemandReader[] ifaces = demand.getInterfaces();
		if (ifaces.length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(IFilesystem.class.getName());
		if (iface == null) return null;
		// check whether the demand requires no dimensions
		IDimensionDemandReader[] dims = iface.getDimensions();
		if (dims.length != 0) return null;
		// create contract for interface without any dimensions
		IInstanceSetup setup = context.createSetup();
		IInstanceProvisionWriter p = setup.getInstance();
		p.createInterface(IFilesystem.class.getName());
		return new IInstanceSetup[] { setup };	
	}
	
}
