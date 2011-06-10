package info.pppc.pcomx.assembler.gc;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.io.ObjectStreamTranslator;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.AssemblerSkeleton;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.assembler.IAssembler;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcomx.assembler.gc.internal.Application;
import info.pppc.pcomx.assembler.gc.internal.Pointer;

import java.util.Vector;

/**
 * The GCAssembler implements a greedy assembler strategy that is completly self-contained
 * and centralized. It is intended to be used as the default strategy for containers to
 * configure resource allocators and factories. As such, it is intended to be extremely
 * lightweight. The adaptation strategy does not try to optimally reuse existing assignments
 * and instances, thus it might not be ideal in every case. However, it should be sufficiently
 * efficient for most cases.
 * 
 * @author Mac
 */
public class GCAssembler implements IAssembler {

	/**
	 * Initialize the abbreviations for classes used by the configuration
	 * manager and the container.
	 */
	static {
		ObjectStreamTranslator.register(Assembly.class.getName(), Assembly.ABBREVIATION);
		ObjectStreamTranslator.register(AssemblyPointer.class.getName(), AssemblyPointer.ABBREVIATION);
		ObjectStreamTranslator.register(AssemblyState.class.getName(), AssemblyState.ABBREVIATION);
		ObjectStreamTranslator.register(Contract.class.getName(), Contract.ABBREVIATION);
		ObjectStreamTranslator.register(Pointer.class.getName(), Pointer.ABBREVIATION);
	}

	/**
	 * This event is fired whenever a new application is added to the assembler.
	 * The event source will be the assembler. The data object will be the 
	 * application that has been added.
	 */
	public static final int EVENT_APPLICATION_ADDED = 1;
	
	/**
	 * This event is fired whenever an existin application is removed. This can
	 * either be due to a lease timeout or due to user request. The event source
	 * will be the assembler. The data object will be the application that has
	 * been added. 
	 */
	public static final int EVENT_APPLICATION_REMOVED = 2;
	
	/**
	 * The id under which the greedy assembler is registered.
	 */
	public static final ObjectID ASSEMBLER_ID = new ObjectID(5); 
	
	/**
	 * The local instance of the GCAssembler.
	 */
	protected static GCAssembler instance;
	
	/**
	 * Creates and returns the local instance of the greedy centralized
	 * assembler.
	 * 
	 * @return The local instance of the greedy centralized assembler.
	 */
	public static GCAssembler getInstance() {
		if (instance == null) {
			instance = new GCAssembler(InvocationBroker.getInstance());
		}
		return instance;
	}

	/**
	 * A vector that contains all running configurations.
	 */
	protected Vector applications = new Vector();
	
	/**
	 * The assembler listeners that are registered for events.
	 */
	protected ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * This is the listener that is registered for all leases issued
	 * by this assembler.
	 */
	protected IListener leaser = new IListener() {
		/**
		 * This method is called whenever a lease expires for one of the applications.
		 * It will remove the corresponding application.
		 * 
		 * @param event The event from the lease registry.
		 */
		public void handleEvent(Event event) {
			Logging.debug(getClass(), "Received lease expiration event " + event + ".");
			Lease lease = (Lease)event.getData();
			synchronized (applications) {
				for (int i = 0; i < applications.size(); i++) {
					Application application = (Application)applications.elementAt(i);
					if (application.getApplicationLease().equals(lease)) {
						Logging.debug(getClass(), "Removing application " 
							+ application.getApplicationID() + " due to lease timeout.");
						applications.removeElementAt(i);
						listeners.fireEvent(EVENT_APPLICATION_REMOVED, application);
						break;
					}
				}
			}
		}
	};
	
	/**
	 * A reference to the local lease registry used to create issue
	 * and remove leases.
	 */
	protected LeaseRegistry registry = LeaseRegistry.getInstance();
	
	/**
	 * Creates a new greedy assembler and registers it at the specified
	 * invocation broker.
	 * 
	 * @param broker The invocation broker that will host this assembler.
	 */
	protected GCAssembler(InvocationBroker broker) {
		AssemblerSkeleton skeleton = new AssemblerSkeleton();
		skeleton.setImplementation(this);
		final ObjectRegistry registry = broker.getObjectRegistry();
		registry.registerObject(ASSEMBLER_ID, skeleton, this);
		broker.addBrokerListener(InvocationBroker.EVENT_BROKER_SHUTDOWN, new IListener() {
			public void handleEvent(Event event) {
				Logging.debug(getClass(), "Removing assembler due to broker shutdown.");
				synchronized (applications) {
					while (! applications.isEmpty()) {
						Application application = (Application)applications.elementAt(0);
						applications.removeElementAt(0);
						GCAssembler.this.registry.remove(application.getApplicationLease());
					}
				}
				registry.removeObject(ASSEMBLER_ID);
				instance = null;
			}
		});
	}
	
	/**
	 * Adds the specified listener to the set of listeners that are registered
	 * for the specified types. Possible types are defined by the event constants
	 * in this class.
	 * 
	 * @param types The types to register for.
	 * @param listener The listener to register.
	 */
	public void addAssemblerListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes the previously registered listener for the set of events. If the
	 * listener has been found and removed successfully, this method returns true,
	 * otherwise false.
	 * 
	 * @param types The types of events to unregister for.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been removed successfully, false if it
	 * 	could not be found.
	 */
	public boolean removeAssemblerListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}

	/**
	 * Called to prepare the assembler for a new application. This will prepare
	 * a new empty application.
	 * 
	 * @param applicationID The id of the application.
	 * @return A lease for the application used to orphan state.
	 */
	public Lease prepare(ReferenceID applicationID)   {
		Logging.debug(getClass(), "Prepare called for application " + applicationID + ".");
		synchronized (applications) {
			for (int i = 0; i < applications.size(); i++) {
				Application application = (Application)applications.elementAt(i);
				if (application.getApplicationID().equals(applicationID)) {
					Logging.debug(getClass(), "Removing duplicate application " 
							+ applicationID + ".");
					registry.remove(application.getApplicationLease());
					applications.removeElementAt(i);
					listeners.fireEvent(EVENT_APPLICATION_REMOVED, application);
					break;
				}
			}
			Application application = new Application(applicationID);
			applications.addElement(application);
			listeners.fireEvent(EVENT_APPLICATION_ADDED, application);
			application.setApplicationLease(registry.create(leaser));
			return application.getApplicationLease();
		}
	}

	/**
	 * Called to configure an application that has been prepared previously. If
	 * the configuration was successful, this method will return true, otherwise
	 * it will return false.
	 * 
	 * @param applicationID The id of the application whose configuration process
	 * 	should be started.
	 * @return True if the configuration was successful, false otherwise.
	 */
	public Assembly configure(ReferenceID applicationID) {
		Logging.debug(getClass(), "Configure called for application " + applicationID + ".");
		// retrieve application
		Application application = null;
		synchronized (applications) {
			for (int i = 0; i < applications.size(); i++) {
				Application app = (Application)applications.elementAt(i);
				if (app.getApplicationID().equals(applicationID)) {
					application = app;
					break;
				}
			}
		}
		// configure the application
		if (application != null) {
			return application.configureApplication();
		} else {
			return null;	
		}
	}

	/**
	 * Called to configure the application anchor. Typically, this will not be 
	 * a component instance but just a plain set of preferences towards anchors.
	 * This method returns a context object that describes how children can pass
	 * their configuration into the assembler.
	 * 
	 * @param applicationID The id of the application to configure.
	 * @param state The setup that describes the configuration of the application
	 * 	anchor as well as the children that are currently used by it.
	 * @return A context object that enables the children of the anchor to pass
	 * 	their configuration into the assembler.
	 */
	public AssemblyPointer setup(ReferenceID applicationID, AssemblyState state) {
		Logging.debug(getClass(), "Setup called.");
		synchronized (applications) {
			for (int i = 0; i < applications.size(); i++) {
				Application application = (Application)applications.elementAt(i);
				if (application.getApplicationID().equals(applicationID)) {
					ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM, ASSEMBLER_ID);
					Pointer pointer = new Pointer();
					Pointer[] pointers = application.setupState(pointer, state);
					AssemblyPointer base = new AssemblyPointer(applicationID);
					base.setAssemblerID(assemblerID);
					base.setName(state.getName());
					base.setVariableID(pointer);
					for (int j = 0; j < pointers.length; j++) {
						AssemblyPointer child = new AssemblyPointer(applicationID);
						child.setName(pointers[j].getName(pointers[j].length() - 1));
						child.setVariableID(pointers[j]);
						child.setAssemblerID(assemblerID);
						if (pointers[j].isInstance(pointers[j].length() - 1)) {
							base.addInstance(child);
						} else {
							base.addResource(child);
						}
						
					}
					return base;
				}
			}
		}
		return null;
	}

	/**
	 * Called to configure an instance or resource used within the application.
	 * This method returns a context object that describes how children of the
	 * instance or resource can pass their configurations to the assembler.
	 * 
	 * @param context The context that describes where the passed instance or
	 * 	resource needs to be added.
	 * @param state The setup that describes the instance or resource and its 
	 * 	children.
	 * @return A context object that enables the children to pass their configuration
	 * 	into the assembler.
	 */
	public AssemblyPointer setup(AssemblyPointer context, AssemblyState state) {
		Logging.debug(getClass(), "Setup called (context, state).");
		ReferenceID applicationID = context.getApplicationID();
		synchronized (applications) {
			for (int i = 0; i < applications.size(); i++) {
				Application application = (Application)applications.elementAt(i);
				if (application.getApplicationID().equals(applicationID)) {
					ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM, ASSEMBLER_ID);
					Pointer pointer = (Pointer)context.getVariableID();
					Pointer[] pointers = application.setupState(pointer, state);
					for (int j = 0; j < pointers.length; j++) {
						Pointer p = pointers[j];
						AssemblyPointer child = new AssemblyPointer(applicationID);
						child.setName(p.getName(p.length() - 1));
						child.setVariableID(p);
						child.setAssemblerID(assemblerID);
						if (p.isInstance(p.length() - 1)) {
							context.addInstance(child);
						} else {
							context.addResource(child);
						}
						
					}
					return context;
				}
			}
		}
		return null;
	}

	/**
	 * Called to retrieve the configuration of a certain instance or resource after 
	 * the configuration process has been completed successfully.
	 * 
	 * @param context The context that is used to reference the configuration.
	 * @return The assembler result that describes the configuration that needs to
	 * 	be applied.
	 */
	public Assembly retrieve(AssemblyPointer context) {
		Logging.debug(getClass(), "WARNING: Retrieve called with context " + context + ".");
		// if the container implementation works correct, this will never happen
		// since this assembler does not use lazy assemblies (!)
		throw new IllegalStateException("Retrieved context for an eager configuration.");
	}

	/**
	 * Called to remove the configuration of a certain application.
	 * 
	 * @param applicationID The id of the application that should be removed.
	 */
	public void remove(ReferenceID applicationID) {
		Logging.debug(getClass(), "Remove called for application " + applicationID + ".");
		synchronized (applications) {
			for (int i = 0; i < applications.size(); i++) {
				Application application = (Application)applications.elementAt(i);
				if (application.getApplicationID().equals(applicationID)) {
					Logging.debug(getClass(), "Removing application " 
							+ applicationID + " due to request.");
					registry.remove(application.getApplicationLease(), false);
					applications.removeElementAt(i);
					listeners.fireEvent(EVENT_APPLICATION_REMOVED, application);
					break;
				}
			}
		}
	}

}
