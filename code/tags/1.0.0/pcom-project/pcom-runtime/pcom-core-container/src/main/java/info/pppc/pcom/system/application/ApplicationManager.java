package info.pppc.pcom.system.application;

import info.pppc.base.lease.LeaseRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;
import info.pppc.base.system.io.ObjectStreamTranslator;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.InstanceCheckpoint;
import info.pppc.pcom.system.container.InstanceSetup;
import info.pppc.pcom.system.container.InstanceState;
import info.pppc.pcom.system.contract.Contract;

import java.util.Vector;

/**
 * The application manager is used to execute and manage applications. It implements 
 * a BASE service with the predecessor interface. This enables the application manager
 * to receive events from containers that host instances of the application. On each 
 * device there will be at most one application manager service since it does not make 
 * sense to have more than one of them. Only those devices that actually start applications 
 * need to have an application manager. Note that in order to execute an application
 * manager, the device does not neeed to have a container.
 * 
 * @author Mac
 */
public class ApplicationManager implements IApplicationManager {	
	
	/**
	 * Initialize the abbreviations for classes used by the application manager
	 * and the container.
	 */
	static {
		ObjectStreamTranslator.register(ApplicationDescriptor.class.getName(), ApplicationDescriptor.ABBREVIATION);
		ObjectStreamTranslator.register(Assembly.class.getName(), Assembly.ABBREVIATION);
		ObjectStreamTranslator.register(AssemblyPointer.class.getName(), AssemblyPointer.ABBREVIATION);
		ObjectStreamTranslator.register(AssemblyState.class.getName(), AssemblyState.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceSetup.class.getName(), InstanceSetup.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceState.class.getName(), InstanceState.ABBREVIATION);
		ObjectStreamTranslator.register(InstanceCheckpoint.class.getName(), InstanceCheckpoint.ABBREVIATION);
		ObjectStreamTranslator.register(Contract.class.getName(), Contract.ABBREVIATION);
	}
	
	/**
	 * The event constant that signals that an application has been added to the
	 * manager. The data object of the event will be the new application descriptor
	 * and the source object will be the delivering application manager.
	 */
	public static final int EVENT_APPLICATION_ADDED = 1;

	/**
	 * The event constant that signals that an application has been removed from
	 * the manager. The data object of the event will be the application descriptor
	 * of the application that has been removed and the source object will be the 
	 * delivering application manager.
	 */
	public static final int EVENT_APPLICATION_REMOVED = 2;
	
	/**
	 * The event constant that signals that an application description contained
	 * in the manager has been changed due to some update request. The data object
	 * of the event will be the application descriptor and the source object will be 
	 * the delivering application manager.
	 */
	public static final int EVENT_APPLICATION_CHANGED = 4;
	
	/**
	 * The event constant that signals that an application has entered the started
	 * state. The source object of the event will be the issuing application manager,
	 * the data object will be the descriptor of the application.
	 */
	public static final int EVENT_APPLICATION_STARTED = 8;

	/**
	 * The event constant that signals that an application has entered the paused
	 * state. The source object of the event will be the issuing application manager,
	 * the data object will be the descriptor of the application.
	 */
	public static final int EVENT_APPLICATION_PAUSED = 16;

	/**
	 * The event constant that signals that an application has entered the stopped
	 * state. The source object of the event will be the issuing application manager,
	 * the data object will be the descriptor of the application.
	 */
	public static final int EVENT_APPLICATION_STOPPED = 32;
	
	/**
	 * The single application manager instance on the specified device.
	 */
	protected static ApplicationManager instance;
	
	/**
	 * Returns the local application manager of the system. If the application
	 * manager has not been created already, this method will create a new one.
	 * 
	 * @return The local application manager of the system.
	 */
	public static ApplicationManager getInstance() {
		if (instance == null) {
			instance = new ApplicationManager(InvocationBroker.getInstance());
		}
		return instance;
	}
	
	/**
	 * The application manager listeners that have been registered using the
	 * register methods in this class.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * The applications that are currently executed by this application
	 * manager.
	 */
	private Vector applications = new Vector();
	
	/**
	 * The lease registry that is used to manage leases.
	 */
	private LeaseRegistry leaseRegistry = LeaseRegistry.getInstance();
	
	/**
	 * Creates a new application manager that registers itself at the specified
	 * invocation broker.
	 * 
	 * @param broker The invocation broker used to register the application
	 * 	manager.
	 */
	protected ApplicationManager(InvocationBroker broker) {
		final ObjectRegistry objectRegistry = broker.getObjectRegistry();
		// register the application manager remote interface
		ApplicationManagerSkeleton aSkeleton = new ApplicationManagerSkeleton();
		aSkeleton.setImplementation(this);
		objectRegistry.registerObject(IApplicationManager.APPLICATION_MANAGER_ID, aSkeleton, this);
		// register the shutdown hook
		broker.addBrokerListener(InvocationBroker.EVENT_BROKER_SHUTDOWN, new IListener() {
			public void handleEvent(Event event) {
				// remove remote application manager service
				Logging.debug(getClass(), "Removing application manager due to shutdown.");
				objectRegistry.removeObject(IApplicationManager.APPLICATION_MANAGER_ID);
				// stop all applications that are still running
				Vector applicationIDs = getApplications();
				for (int i = 0; i < applicationIDs.size(); i++) {
					ObjectID applicationID = (ObjectID)applicationIDs.elementAt(i);
					removeApplication(applicationID);
				}
				// finalize instance and we're outa here
				instance = null;
			}
		});
	}

	/**
	 * Returns the application with the specified application id or null if
	 * the application does not exist in the manager.
	 * 
	 * @param applicationID The id of the application to retrieve.
	 * @return The application with the specified id or null if the application
	 * 	with this id does not exist.
	 */
	protected Application getApplication(ObjectID applicationID) {
		synchronized (applications) {
			for (int i = 0; i < applications.size(); i++) {
				Application application = (Application)applications.elementAt(i);
				if (application.getIdentifier().equals(applicationID)) {
					return application;
				}
			}
		}
		return null;
	}
	
	/**
	 * Fires an event of the specified type and delivers it to all 
	 * registered listeners. This method is used internally to notify
	 * listeners about removals and addtions and it is used by the
	 * applications to signal lifecycle state changes.
	 * 
	 * @param type The type of the event to fire.
	 * @param descriptor The application descriptor of the application
	 * 	that caused the event.
	 */
	protected void fireApplicationEvent(int type, ApplicationDescriptor descriptor) {
		listeners.fireEvent(type, descriptor);
	}
	
	/**
	 * Returns a reference to the lease registry that is used to manage
	 * application leases.
	 * 
	 * @return A reference to the lease registry used to manage leases.
	 */
	protected LeaseRegistry getLeaseRegistry() {
		return leaseRegistry;
	}
	
	/**
	 * Adds the specified listener to the set of application listeners that is
	 * informed whenever the specified set of event types occur. Possible types
	 * are EVENT_APPLICATION_STARTED, EVENT_APPLICATION_PAUSED and 
	 * EVENT_APPLICATION_STOPPED. These events are emitted whenever the state
	 * of the application changes. The source object of the event will be this
	 * application manager, the data object will be the reference id of the
	 * application. Futher event types that are useful for proactive gui's are
	 * EVENT_APPLICATION_ADDED and EVENT_APPLICATION_REMOVED. These events are
	 * fired whenever an application is added to or removed from the manager.
	 * Similar to the previously specified events, the data object will be the
	 * applciations id and the source object will be this manager. 
	 * 
	 * @param types The types to register for as specified by the event constants
	 * 	of this class.
	 * @param listener The listener that needs to be registered, never null.
	 * @throws NullPointerException Thrown if the listener is null.
	 */
	public void addApplicationListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes a previously registered listener from the set of registered
	 * application listeners. Returns true if the listener has been removed,
	 * false otherwise.
	 * 
	 * @param types The types of events to unregister.
	 * @param listener The listener to unregister, never null.
	 * @return True if the listener has been unregistered, false otherwise.
	 * @throws NullPointerException Thrown if the listener is null.
	 */
	public boolean removeApplicationListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}
	

// predecessor interface 	
	
	/**
	 * This method is part of the predecessor interface. It is called by a successor
	 * to signal that the instance has changed its contract.
	 * 
	 * @param predecessorID The id of the predecessor.
	 * @param name The name of the successor.
	 * @param provision The new provision.
	 * @param phase The phase used to detect outdated messages.
	 */
	public void changeInstance(ObjectID predecessorID, String name, int phase, Contract provision) {
		Application application = getApplication(predecessorID);
		if (application != null) application.changeInstance(name, phase, provision);
	}

	/**
	 * This method is part of the predecessor interface. It is called to signal that a
	 * successor needs to be replaced.
	 * 
	 * @param predecessorID The id of the predecessor.
	 * @param name The name of the successor.
	 * @param phase The phase used to detect outdated messages.
	 */
	public void removeInstance(ObjectID predecessorID, String name, int phase) {
		Application application = getApplication(predecessorID);
		if (application != null) application.removeInstance(name, phase);
	}
	
	/**
	 * This method is part of the predecessor interface. It is called to signal that the
	 * application of a successor should be stopped.
	 * 
	 * @param predecessorID The id of the predecessor.
	 * @param name The name of the successor.
	 * @param phase The phase used to detect outdated messages.
	 */
	public void exitApplication(ObjectID predecessorID, String name, int phase) {
		Application application = getApplication(predecessorID);
		if (application != null) application.exitApplication(name, phase);
	}
	
	/**
	 * This method is part of the predecessor interface. It is called to signal that the
	 * application of a successor should be saved.
	 * 
	 * @param predecessorID The id of the predecessor.
	 * @param name The name of the successor.
	 * @param phase The phase used to detect outdated messages.
	 */
	public void saveApplication(ObjectID predecessorID, String name, int phase) {
		Application application = getApplication(predecessorID);
		if (application != null) application.saveApplication(name, phase);
	}

// applicaiton manager interface

	/**
	 * This method is part of the application manager interface. It returns the
	 * application descriptor for the application with the specified id. If the
	 * application does not exist, this method will return null.
	 * 
	 * @param applicationID The reference to the application.
	 * @return The application descriptor of the application.
	 */
	public ApplicationDescriptor queryApplication(ObjectID applicationID) {
		Application application = getApplication(applicationID);
		if (application != null) return application.getDescriptor();
		return null;
	}

	/**
	 * This method is part of the application manager interface. It returns the
	 * object identifiers of applications that are currently hosted by this
	 * manager.
	 * 
	 * @return A vector of object ids that denote applications that are currently
	 * 	hosted by this application manager.
	 */
	public Vector getApplications() {
		synchronized (applications) {
			Vector applicationIDs = new Vector();
			for (int i = 0; i < applications.size(); i++) {
				Application application = (Application)applications.elementAt(i);
				applicationIDs.addElement(application.getIdentifier());
			}
			return applicationIDs;			
		}
	}

	/**
	 * This method is part of the application manager interface. It adds a
	 * new application that is created from the specified descriptor to
	 * the manager's internal application repository.
	 * 
	 * @param descriptor The descriptor of the application to add.
	 * @return The object id for the newly created application.
	 * @throws IllegalArgumentException Thrown if the descriptor is malformed.
	 */
	public ObjectID addApplication(ApplicationDescriptor descriptor) {
		Application application = new Application(this, descriptor);
		synchronized (applications) {
			applications.addElement(application);
		}
		fireApplicationEvent(EVENT_APPLICATION_ADDED, application.getDescriptor());
		return application.getIdentifier();
	}

	/**
	 * This method is part of the application manager interface. It updates
	 * the specified application using the specified descriptor.
	 * 
	 * @param descriptor The descriptor that describes the applications new
	 * 	state.
	 */
	public void updateAppliction(ApplicationDescriptor descriptor) {
		Application application = getApplication(descriptor.getApplicationID());
		if (application != null) {
			application.updateApplication(descriptor);	
		}
	}
	
	/**
	 * A call to this method will cause the specified application to reconfigure.
	 * If the current application is not started, calling this method will not
	 * do anything.
	 * 
	 * @param applicationID The id of the application to reconfigure.
	 */
	public void changeApplication(ObjectID applicationID) {
		Application application = getApplication(applicationID);
		if (application != null) {
			application.changeApplication();	
		}
	}

	/**
	 * This method is part of the application manager interface. It removes
	 * the specified applciation from the repository. If the application
	 * has not been stopped, the application will be stopped and removed.
	 * 
	 * @param applicationID The application id of the application to remove.
	 */
	public void removeApplication(ObjectID applicationID) {
		exitApplication(applicationID);
		Application application = getApplication(applicationID);
		if (application != null) {
			if (applications.removeElement(application)) {
				fireApplicationEvent(EVENT_APPLICATION_REMOVED, application.getDescriptor());	
			}
		}
	}

	/**
	 * This method is part of the application manager interface. It starts
	 * the specified application from the repository. If the application
	 * does not exist or if it is already running, this method does nothing.
	 * 
	 * @param applicationID The id of the application to start.
	 */
	public void startApplication(ObjectID applicationID) {
		Application application = getApplication(applicationID);
		if (application != null) {
			application.startApplication();
		}
	}
	
	/**
	 * This method is part of the application manager interface. It stops the application
	 * with the specified id, if the application is still running. If the application is
	 * no longer hosted by the manager, this method will simply return.
	 * 
	 * @param applicationID The identifier of the application to stop.
	 */
	public void exitApplication(ObjectID applicationID) {
		Application application = getApplication(applicationID);
		if (application != null) {
			application.exitApplication();
		}
	}
	
	/**
	 * This method is part of the application manager interface. It saves the application
	 * with the specified id, if the application is still running. If the application is
	 * no longer hosted by the manager, this method will simply return.
	 * 
	 * @param applicationID The identifier of the application to stop.
	 */
	public void saveApplication(ObjectID applicationID) {
		Application application = getApplication(applicationID);
		if (application != null) {
			application.saveApplication();
		}
	}

	/**
	 * This method is part of the application manager interface. It loads the application
	 * with the specified id, if the application is still running. If the application is
	 * no longer hosted by the manager, this method will simply return.
	 * 
	 * @param applicationID The identifier of the application to stop.
	 */
	public void loadApplication(ObjectID applicationID) {
		Application application = getApplication(applicationID);
		if (application != null) {
			application.loadApplication();
		}
	}
	
}
