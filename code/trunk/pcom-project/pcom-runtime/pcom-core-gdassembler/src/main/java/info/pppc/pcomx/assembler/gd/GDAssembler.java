package info.pppc.pcomx.assembler.gd;

import info.pppc.base.lease.Lease;
import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.io.ObjectStreamTranslator;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcomx.assembler.gd.internal.Application;
import info.pppc.pcomx.assembler.gd.internal.Pointer;
import info.pppc.pcomx.assembler.gd.internal.ReleaseEvent;
import info.pppc.pcomx.assembler.gd.internal.RemoveEvent;
import info.pppc.pcomx.assembler.gd.internal.ReportEvent;
import info.pppc.pcomx.assembler.gd.internal.ResolveEvent;

import java.util.Vector;

/**
 * The GDAssembler implements a greedy assembler strategy that is fully distributed
 * and thus, it requires a greedy assembler on each device that should be part of
 * the assembly. If a device is not equipped with a greedy distributed assembler
 * the device's components will not be used within the configuration. This assembler 
 * is mainly intended for performance comparisons.
 * 
 * @author Mac
 */
public class GDAssembler implements IGDAssembler {

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
	 * The id under which the greedy assembler is registered.
	 */
	public static final ObjectID ASSEMBLER_ID = new ObjectID(6); 
	
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
	 * The listener bundle that manages the listeners for this assembler.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * The invocation broker used by the assembler.
	 */
	private InvocationBroker broker;
	
	/**
	 * The lease registry used to issue and maintain leases.
	 */
	private LeaseRegistry registry;
	
	/**
	 * The applications that are currently configured by the
	 * assembler.
	 */
	private Vector applications = new Vector();
	
	/**
	 * A flag that indicates whether the algorithm is running.
	 */
	private boolean running = true;
	
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
					if (lease.equals(application.getApplicationLease())) {
						Logging.debug(getClass(), "Removing application " 
							+ application.getApplicationID() + " due to lease timeout.");
						remove(application.getApplicationID());
						break;
					} 
				}
			}
		}
	};
	
	/**
	 * The instance of the gd assembler or null if none has been
	 * initialized so far.
	 */
	private static GDAssembler instance;
	
	/**
	 * Returns the instance of the gd assembler that is running. If 
	 * none is running, a new one is created using the invocation
	 * broker.
	 * 
	 * @return The current instance of the gd assembler. 
	 */
	public static GDAssembler getInstance() {
		if (instance == null) {
			instance = new GDAssembler(InvocationBroker.getInstance());
		}
		return instance;
	}
	
	
	/**
	 * Creates a new assembler using the specified broker.
	 * 
	 * @param broker The broker of the assembler.
	 */
	protected GDAssembler(InvocationBroker broker) {
		this.broker = broker;
		registry = LeaseRegistry.getInstance();
		final ObjectRegistry registry = broker.getObjectRegistry();
		GDAssemblerSkeleton skeleton = new GDAssemblerSkeleton();
		skeleton.setImplementation(this);
		registry.registerObject(ASSEMBLER_ID, skeleton, this);
		broker.addBrokerListener(InvocationBroker.EVENT_BROKER_SHUTDOWN, new IListener() {
			public void handleEvent(Event event) {
				registry.removeObject(ASSEMBLER_ID);
				synchronized (applications) {
					running = false;
					while (! applications.isEmpty()) {
						Application application = (Application)applications.elementAt(0);
						removeApplication(application);
					}
				}
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
	 * Returns the application with the specified id or null if there
	 * is none.
	 * 
	 * @param applicationID The id of the application to retrieve.
	 * @return The application or null if there is none.
	 */
	private Application getApplication(ReferenceID applicationID) {
		for (int i = applications.size() - 1; i >=0; i--) {
			Application application = (Application)applications.elementAt(i);
			if (application.getApplicationID().equals(applicationID)) {
				return application;
			}
		}
		return null;
	}
	
	/**
	 * Adds the specified application to the list of applications.
	 * If an appication with the specified id exists already, the
	 * existing application will be removed and the new one will
	 * be added.
	 * 
	 * @param application The application that needs to be added.
	 * @return The lease used for the application.
	 */
	private Lease addApplication(Application application) {
		ReferenceID id = application.getApplicationID();
		for (int i = applications.size() - 1; i >= 0; i--) {
			Application existing = (Application)applications.elementAt(i);
			if (id.equals(application.getApplicationID())) {
				removeApplication(existing);
				break;
			}
		}
		applications.addElement(application);
		listeners.fireEvent(EVENT_APPLICATION_ADDED, application);
		broker.performOperation(application, application.getApplicationMonitor());
		Lease lease = registry.create(leaser);
		application.setApplicationLease(lease);
		return lease;
	}
	
	/**
	 * Stops a previously started application and performs a clean shutdown. 
	 * The shutdown blocks until the application has been stopped successfully.
	 * 
	 * @param application The application that should be stopped.
	 */
	private void removeApplication(Application application) {
		listeners.fireEvent(EVENT_APPLICATION_REMOVED, application);
		application.getApplicationMonitor().cancel();
		while (! application.getApplicationMonitor().isDone()) {
			try {
				application.getApplicationMonitor().join();	
			} catch (InterruptedException e) {
				Logging.error(getClass(), "Thread got interrupted.", e);
			}
		}
		applications.removeElement(application);
		registry.remove(application.getApplicationLease(), false);
	}
	
	/**
	 * Called whenever a new application should be configured or adapted.
	 * 
	 * @param applicationID The unique id of the application.
	 * @return A lease for the application.
	 * @throws InvocationException Thrown if the assembler is shut down.
	 */
	public Lease prepare(ReferenceID applicationID) throws InvocationException {
		synchronized (applications) {
			if (! running) throw new InvocationException("Assembler is shut down.");
			SystemID[] devices = broker.getDeviceRegistry().getDevices(ASSEMBLER_ID);
			Vector systems = new Vector();
			for (int i = devices.length - 1; i >= 0; i--) {
				systems.add(devices[i]);
			}
			Application application = new Application(applicationID, registry, systems);
			return addApplication(application);
		}
	}

	/**
	 * Called whenever the application has been configured or adapted. 
	 * 
	 * @param applicationID The unique if of the application.
	 * @return The assembly of the application or null if the applicaiton
	 * 	could not be configured.
	 */
	public Assembly configure(ReferenceID applicationID) {
		Application application = null;
		synchronized (applications) {
			 application = getApplication(applicationID);	
		}
		if (application != null) {
			return application.configureApplication();
		}  else {
			return null;			
		}
	}

	/**
	 * Called whenever the application anchor is initialized with some data.
	 * 
	 * @param applicationID The id of the application.
	 * @param state The state of the anchor.
	 * @return An assembly pointer that contains pointers for all dependencies
	 * 	declared by the state. 
	 */
	public AssemblyPointer setup(ReferenceID applicationID, AssemblyState state) {
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
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
				if (pointers[j].isInstance(pointers[j].length() - 1)) {
					SystemID device = state.getInstance(child.getName());
					child.setAssemblerID(new ReferenceID(device, ASSEMBLER_ID));
					base.addInstance(child);
				} else {
					child.setAssemblerID(assemblerID);
					base.addResource(child);
				}
			}
			return base;
		} else {
			return null;
		}	
	}

	/**
	 * Called whenever a component or resource of the application is initialized.
	 * 
	 * @param context The pointer that denotes the dependency.
	 * @param state The state that denotes the configuration.
	 * @return The pointer that describes the child setup.
	 */
	public AssemblyPointer setup(AssemblyPointer context, AssemblyState state) {
		ReferenceID applicationID = context.getApplicationID();
		Logging.debug(getClass(), "Retrieved setup for " + applicationID + ".");
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
			ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM, ASSEMBLER_ID);
			Pointer[] pointers = application.setupState
				((Pointer)context.getVariableID(), state);
			for (int j = 0; j < pointers.length; j++) {
				Pointer p = pointers[j];
				AssemblyPointer child = new AssemblyPointer(applicationID);
				child.setName(p.getName(p.length() - 1));
				child.setVariableID(p);
				if (p.isInstance(p.length() - 1)) {
					SystemID device = state.getInstance(child.getName());
					child.setAssemblerID(new ReferenceID(device, ASSEMBLER_ID));
					context.addInstance(child);
				} else {
					child.setAssemblerID(assemblerID);
					context.addResource(child);
				}	
			}
			return context;
		} else {
			return null;
		}
	}

	/**
	 * Retrieves the specified assembly information that corresponds to
	 * the pointer.
	 * 
	 * @param pointer The pointer that points to some configured part of the
	 * 	application.
	 * @return The assembly for the pointer or null if the assembly for the pointer 
	 * 	cannot be retrieved.
	 */
	public Assembly retrieve(AssemblyPointer pointer) {
		ReferenceID applicationID = pointer.getApplicationID();
		Logging.debug(getClass(), "Received retrieve for " + applicationID + ".");
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
			return application.retrieveAssembly((Pointer)pointer.getVariableID());
		} else {
			return null;
		}
	}

	/**
	 * Removes all state of the application. If the application is the master
	 * of a configuration, all slaves will be released, too.
	 * 
	 * @param applicationID The id of the application to remove.
	 */
	public void remove(ReferenceID applicationID) {
		synchronized (applications) {
			Logging.debug(getClass(), "Received remove for " + applicationID + ".");
			Application application = getApplication(applicationID);
			if (application != null) {
				removeApplication(application);
			}
		}
	}

	/**
	 * Prepares the specified application as slave for the specified
	 * master.
	 * 
	 * @param applicationID The id of the application to configure.
	 * @param master The master device that controls the configuration.
	 * @param systems The systems that participate in the configuration.
	 * @return A lease for the application state contained in this assembler.
	 * @throws InvocationException Thrown if the assembler is shut down.
	 */
	public Lease prepare(ReferenceID applicationID, SystemID master, Vector systems) throws InvocationException {
		synchronized (applications) {
			Logging.debug(getClass(), "Retrieved prepare for " + applicationID + ".");
			if (!running) throw new InvocationException("Assembler is shut down.");
			Application application = new Application(applicationID, master, systems);
			return addApplication(application);
		}
	}
	
	/**
	 * Releases the configuration data for the specified pointer in the
	 * specified application.
	 * 
	 * @param applicationID The id of the application to configure.
	 * @param pointer The pointer that can be released.
     * @param phase The current phase of the algorithm.
	 */
	public void release(ReferenceID applicationID, int phase, Pointer pointer) {
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
			ReleaseEvent event = new ReleaseEvent(application, phase, pointer);
			application.scheduleEvent(event);
		}
	}

	/**
	 * Removes the specified system from the specified application.
	 * 
	 * @param applicationID The id of the application.
	 * @param system The system to remove.
	 */
	public void remove(ReferenceID applicationID, SystemID system) {
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
			RemoveEvent event = new RemoveEvent(application, system);
			application.scheduleEvent(event);
		}
	}
	
	/**
	 * Reports whether the configuration request for the specified pointer
	 * has been performed sucessfully or wheter it has been aborted.
	 * 
	 * @param applicationID The id of the application.
	 * @param pointer The pointer that has been configured or not.
	 * @param phase The current phase of the algorithm.
	 * @param assembly The assembly for the successfully configured subtree
	 * 	or null if the configuration failed.
	 */
	public void report(ReferenceID applicationID, int phase, Pointer pointer, Assembly assembly) {
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
			ReportEvent event = new ReportEvent
				(application, phase, pointer, assembly);
			application.scheduleEvent(event);
		}
	}
	
	/**
	 * Requests the configuration of a specified dependency in an application.
	 * 
	 * @param applicationID The id of the application.
	 * @param pointer A pointer that points to the configuration.
	 * @param system The system that requests the configuration.
	 * @param contract A contract that describes the dependency to resolve.
	 * @param phase The current phase of the algorithm.
	 * @param reuse A flag that indicates whether the parent is reused.
	 */
	public void resolve(ReferenceID applicationID, int phase, Pointer pointer, SystemID system, Contract contract, boolean reuse) {
		Application application;
		synchronized (applications) {
			application = getApplication(applicationID);
		}
		if (application != null) {
			ResolveEvent event = new ResolveEvent
				(application, phase, pointer, system, contract, reuse);
			application.scheduleEvent(event);
		}
	}

}
