package info.pppc.pcom.capability.com;

import java.util.Vector;

import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.NullMonitor;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.capability.IAllocator;
import info.pppc.pcom.system.model.capability.IAllocatorContext;
import info.pppc.pcom.system.model.capability.IResourceContext;
import info.pppc.pcom.system.model.capability.IResourceSetup;
import info.pppc.pcom.system.model.capability.IResourceTemplate;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;
import info.pppc.pcom.utility.LibraryLoader;

/**
 * The com allocator is a pcom allocator that can be installed into a
 * container that resides on a windows pc. It provides resource accessors
 * that adhere to the ms com interface. Using this resource, elements
 * can access the com bridge.
 * 
 * @author Mac
 */
public class COMAllocator implements IAllocator, IOperation, ICOMAccessor {

	/**
	 * The name of the allocator as shown in user interfaces.
	 */
	private static final String CAPABILITY_NAME = "Microsoft COM";
	
	/**
	 * The context of the allocator after the set context method
	 * has been called.
	 */
	private IAllocatorContext context;
	
	/**
	 * The monitor that the allocator uses during start and
	 * stop.
	 */
	private NullMonitor monitor;
	
	/**
	 * The queue that stores the commands that have been issued.
	 * This queue contains object arrays of size 2. The first
	 * entry will be the command and the second entry will be
	 * used to store any exceptions that might have occured.
	 */
	private Vector commands = new Vector();
	
	/**
	 * A reference to the thread that exeuctes the com thread.
	 * This is used for re-entrant calls to the accessor.
	 */
	private Thread executor;
	
	/**
	 * Creates a new ms com allocator.
	 */
	public COMAllocator() {
		super();
	}

	/**
	 * Returns the name of the allocator as shown in the
	 * user interfaces of pcom.
	 * 
	 * @return The name of the allocator.
	 */
	public String getName() {
		return CAPABILITY_NAME;
	}
	
	/**
	 * Called to set the context of the allocator.
	 * 
	 * @param context The context of the allocator.
	 */
	public void setContext(IAllocatorContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context of the allocator.
	 */
	public void unsetContext() {
		context = null;
	}
	
	/**
	 * Called to start the allocator. This will start
	 * a new thread that loads the com bridge dll and
	 * waits for future dispatch calls.
	 */
	public void start() {
		monitor = new NullMonitor();
		context.performOperation(this, monitor);
	}
	
	/**
	 * Called to stop the allocator. This will stop the
	 * thread created in the start method.
	 */
	public void stop() {
		if (monitor != null) {
			synchronized (monitor) {
				monitor.cancel();
				try {
					while (! monitor.isDone()) {
						monitor.wait();	
					}	
				} catch (InterruptedException e) {
					Logging.error(getClass(), "Thread got interrupted.", e);
				}				
			}
		}
	}

	
	/**
	 * Derives the possible setups for the specified demand.
	 * 
	 * @param demand The demand for which a setup should be
	 * 	derived.
	 * @return The setups for the demand.
	 */
	public IResourceSetup[] deriveSetups(IResourceDemandReader demand) {
		// only accept demands with an interface demand towards the com accessor
		if (demand.getInterfaces().length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(ICOMAccessor.class.getName());
		if (iface == null) return null;
		if (iface.getDimensions().length != 0) return null;
		// create a setup that provides an interface provision of the accessor
		IResourceSetup setup = context.createSetup();
		IResourceProvisionWriter provision = setup.getResource();
		provision.createInterface(ICOMAccessor.class.getName());
		return new IResourceSetup[] { setup };
	}
	
	/**
	 * Estimates the resource usage of the template. Since the
	 * number of users of this allocator is unlimited, each
	 * template has a total cost of 0.
	 * 
	 * @param template The template that should be estimated.
	 * @return The estimate for the template, always 0.
	 */
	public int[] estimateTemplate(IResourceTemplate template) {
		return new int[] { 0 };
	}
	
	/**
	 * Returns the amount of free resources of the allocator.
	 * Since each assignment does not cost anything the available
	 * resources are also set to 0.
	 * 
	 * @return The free resources of the allocator, i.e. 0.
	 */
	public int[] freeResources() {
		return new int[] { 0 };
	}
	
	/**
	 * Returns the total amount of resources of the allocator.
	 * Since the allocator's resource assignments are free,
	 * the total amount of available resources is 0.
	 * 
	 * @return The total resources of the allocator, i.e. 0.
	 */
	public int[] totalResources() {
		return new int[] { 0 };
	}


	/**
	 * Starts the specified resource assignment. This will
	 * setup the accessor object. The resource allocation
	 * will never fail, since this allocator can provide
	 * unlimited assignments.
	 * 
	 * @param context The context object that needs to be
	 * 	reserved.
	 * @return Always true.
	 */
	public boolean startResource(IResourceContext context) {
		context.setAccessor(this);
		return true;
	}
	
	/**
	 * Pauses the specified resource assignment. For this
	 * allocator, this method does nothing.
	 * 
	 * @param context The context to pause.
	 */
	public void pauseResource(IResourceContext context) { }

	/**
	 * Stops the specified resource assignment. For this
	 * allocator, this method does nothing.
	 * 
	 * @param context The context to stop.
	 */
	public void stopResource(IResourceContext context) { }
	

	/**
	 * This method runs the thread that executes the commands
	 * issued through the resource assignments.
	 * 
	 * @param monitor The monitor that is used to cancel the
	 * 	operation.
	 * @throws InterruptedException Thrown if the thread gets
	 * 	interrupted (should never happen).
	 */
	public void perform(IMonitor monitor) throws Exception {
		// load the library for jacob
		String library;
		switch (LibraryLoader.getOS()) {
			case LibraryLoader.OS_WINDOWS_X64:
				library = "jacob-1.15-M4-x64";
				break;
			case LibraryLoader.OS_WINDOWS_X86:
				library = "jacob-1.15-M4-x86";
				break;
			default:
				library = null;
		}
		if (library != null) {
			LibraryLoader.load(library, false);
		}
		try {
			synchronized (monitor) {
				executor = Thread.currentThread();
			}
			run: while (true) {
				Object[] data = null;
				synchronized (monitor) {
					while (commands.size() == 0) {
						if (monitor.isCanceled()) break run;
						monitor.wait();
					}
					data = (Object[])commands.elementAt(0);
				}
				ICOMCommand command = (ICOMCommand)data[0];
				data[1] = run(command);
				synchronized (monitor) {
					commands.removeElementAt(0);
					monitor.notifyAll();
				}
			}		
		} finally {
			synchronized (monitor) {
				executor = null;
				monitor.notifyAll();
			}
		}
	}
	
	/**
	 * Called whenever a command needs to be executed asynchronously. 
	 * 
	 * @param command The command to execute.
	 * @throws IllegalStateException Thrown if the allocator is stopped.
	 */
	public void runAsynchronous(ICOMCommand command) throws IllegalStateException {
		if (command == null) return;
		synchronized (monitor) {
			if (monitor == null || monitor.isCanceled()) 
				throw new IllegalStateException("Com allocator is not started.");
			Object[] data = new Object[] { command, null };
			commands.addElement(data);
		}
	}
	
	/**
	 * Called whenever a command needs to be executed synchronously. 
	 * 
	 * @param command The command to execute.
	 * @throws COMException Thrown if the command throws an exception.
	 * @throws InterruptedException Thrown if the 
	 * @throws IllegalStateException Thrown if the allocator is stopped.
	 */
	public void runSynchronous(ICOMCommand command)
		throws COMException, InterruptedException, IllegalStateException {
		if (command == null) return;
		synchronized (monitor) {
			if (executor != Thread.currentThread()) {
				Object[] data = new Object[] { command, null };
				commands.addElement(data);
				monitor.notifyAll();
				while (commands.contains(data)) {
					if (monitor == null || monitor.isCanceled()) {
						commands.removeElement(data);
						throw new IllegalStateException("Com allocator is not started.");
					}
					monitor.wait();
				}
			} else {
				COMException e = run(command);
				if (e != null) {
					throw e;
				}
			}
		}
	}
	
	/**
	 * Executes the specified command using the current thread and
	 * returns any exception as com exception.
	 * 
	 * @param command The command to execute.
	 * @return The exception or null if none.
	 */
	private COMException run(ICOMCommand command) {
		try {
			command.run();
			return null;
		} catch (Throwable t) {
			return new COMException("Exception while executing command.", t);
		}
	}
	
}
