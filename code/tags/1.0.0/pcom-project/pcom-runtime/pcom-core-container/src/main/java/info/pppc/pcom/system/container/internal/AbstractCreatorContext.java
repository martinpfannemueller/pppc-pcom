package info.pppc.pcom.system.container.internal;

import java.util.Vector;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.contract.Contract;

/**
 * The creator context is a common context for factories and allocators.
 * It provides a stop lock and methods to manage the resource dependencies.
 * 
 * @author Mac
 */
public abstract class AbstractCreatorContext extends AbstractContext {

	/**
	 * The stop lock implements a lock that can be accessed multiple times
	 * in parallel by readers and exactly once by a writer. Readers and the
	 * writer are mutexed. The readers gain access by calling the aquire
	 * method. They must release the lock later on by a call to the release
	 * method. A single thread can access the stop method. If the stop
	 * method has been called all calls to the aquire method will return false
	 * and further calls to the stop method will also fail with false as
	 * return value.
	 * This lock is used to stop factories and allocators safely while allowing
	 * multiple threads to create instances and resources.
	 * 
	 * @author Mac
	 */
	public class StopLock {

		/**
		 * A flag that indicates whether there has been already a thread
		 * that has called the stop method.
		 */	
		private boolean running = true;
		
		/**
		 * The number of readers that have currently aquired the lock without
		 * releasing it.
		 */
		private int readers = 0;
		
		
		/**
		 * Creates a new remove lock without readers.
		 */
		public StopLock() {
			super();
		}

		/**
		 * Aquires a reader lock if the writer has not been already granted
		 * access. If the reader lock has been aquired successfully, this
		 * method will return true. If the writer has already accessed the
		 * lock, this method will return false. If false is returned, the
		 * release method must not be called, otherwise it must be called
		 * after the critical section has been passed.
		 * 
		 * @return True if the lock has been aquired, false if the lock
		 * 	cannot be aquired because the writer has already accessed the
		 * 	lock.
		 */
		public synchronized boolean aquire() {
			if (running) {
				readers += 1;
			}
			return running;
		}
		
		/**
		 * Releases a previously aquired lock and notifies all potentially
		 * waiting threads about the release. Each thread that has aquired
		 * a lock must eventually release it in order to enable calls to the
		 * stop method.
		 */
		public synchronized void release() {
			readers -= 1;
			notifyAll();
		}
		
		/**
		 * Stops the lock. A call to this method will prevent any more readers
		 * to access the critical section and it will wait until all readers
		 * released their locks. If multiple threads access this method, only
		 * the first one will be considered. The first thread that stopped the
		 * lock will receive true as result, all further threads will receive
		 * a false as return value.
		 * 
		 * @return True if the accessing thread was the one that stopped the
		 * 	lock, false if the lock was already stopped by some other access
		 * 	to this method.
		 */
		public synchronized boolean stop() {
			if (running) {
				running = false;
				while (readers != 0) {
					try {
						wait();
					} catch (Throwable e) {
						Logging.error(getClass(), "Thread got interrupted.", e);
					}
				}
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	/**
	 * The lock that is used for clean shutdowns. Whenever a creator
	 * is about to be used, the lock must be aquired. Whenever the
	 * creator is shut down, the lock must be stopped.
	 */
	private StopLock lock = new StopLock();
	
	/**
	 * The committer is a template listener that listens to commit
	 * events of the abstract template. If a template is commited,
	 * the listener will try to sync all requirements and try to
	 * bind all new requirements.
	 */
	private IListener committer = new IListener() {
		public void handleEvent(Event event) {
			validate();
			if (getCreatorResources().size() > 0) {
				getContainer().updateResources();
			}
		};
	};
	
	/**
	 * Creates a new context object for the specified status, and
	 * template using the specified container.
	 * 
	 * @param container The container that created the context.
	 * @param template The template of the context.
	 * @param status The status of the context.
	 */
	public AbstractCreatorContext(Container container, AbstractTemplate template, AbstractStatus status) {
		super(container, template, status);
	}
	
	/**
	 * Returns the name of the creator.
	 * 
	 * @return The name of the creator.
	 */
	public abstract String getName(); 

	/**
	 * Returns the lock that is used to stop the creator in
	 * a clean way. If a new element is about to be created,
	 * the lock needs to be aquired. If the creator is about
	 * to be shut down, the lock needs to be stopped.
	 * 
	 * @return The lock of the creator used to perform a
	 * 	clean shut down.
	 */
	public StopLock getLock() {
		return lock;
	}
	
	/**
	 * Called whenever the creator is started. This will add the
	 * commit listener to the template of the creator and it will
	 * clear any existing events contained in the status.
	 */
	protected void start() {
		getAbstractTemplate().addTemplateListener
			(AbstractTemplate.EVENT_TEMPLATE_COMMITTED, committer);
		getAbstractStatus().clearEvents();
	}
	
	/**
	 * Called whenever the creator is stopped. This will remove the
	 * commit listener from the template, it will remove any existing
	 * resource bindings and it will clear the status events.
	 */
	protected void stop() {
		getAbstractTemplate().removeTemplateListener
			(AbstractTemplate.EVENT_TEMPLATE_COMMITTED, committer);
		super.stop();
	}
	
	
	/**
	 * Returns the resource demands of the creator that are not
	 * satisfied yet. This will be a vector of contracts.
	 * 
	 * @return The resource demands of the creator that are not
	 * 	satisfied yet.
	 */
	public Vector getCreatorResources() {
		Contract template = getAbstractTemplate().getContract();
		Contract[] demands = template.getContracts(Contract.TYPE_RESOURCE_DEMAND);
		Contract status = getAbstractStatus().getContract();
		Contract[] provisions = status.getContracts(Contract.TYPE_RESOURCE_PROVISION);
		Vector result = new Vector();
		j: for (int j = 0; j < demands.length; j++) {
			Contract demand = demands[j];
			for (int i = 0; i < provisions.length; i++) {
				Contract provision = provisions[i];
				if (provision.getName().equals(demand.getName())) {
					continue j;
				}
			}
			result.addElement(demand);
		}
		return result;
	}

	/**
	 * Resolves the specified resource demand of the creator using
	 * the passed assembly.
	 * 
	 * @param assembly The assembly used to create the resource.
	 */
	public void startCreatorResource(Assembly assembly) {
		startResource(assembly);
		validate();
	}
	
	/**
	 * Called whenever a resource bound to the creator changes its provision.
	 * This will release all resources that are no longer required and those
	 * that do not fulfill a desired requirement 
	 */
	protected void validate() {
		Contract template = getAbstractTemplate().getContract();
		Contract[] demands = template.getContracts(Contract.TYPE_RESOURCE_DEMAND);
		Contract status = getAbstractStatus().getContract();
		Contract[] provisions = status.getContracts(Contract.TYPE_RESOURCE_PROVISION);
		i: for (int i = 0; i < provisions.length; i++) {
			Contract provision = provisions[i];
			for (int j = 0; j < demands.length; j++) {
				Contract demand = demands[j];
				if (provision.matches(demand, false)) {
					continue i;
				}
			}
			stopResource(provision.getName());
		}
		// find all bindings that do not make a resource provision and remove em
		AbstractBinding[] resources = getResources();
		i: for (int i = 0; i < resources.length; i++) {
			AbstractBinding resource = resources[i];
			for (int j = 0; j < provisions.length; j++) {
				Contract provision = provisions[j];
				if (resource.getName().equals(provision.getName())) {
					continue i;
				}
			}
			stopResource(resource.getName());
		}
		getAbstractStatus().fireEvents();
	}


	

}
