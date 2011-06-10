package info.pppc.pcom.system.container.internal.capability;

import java.util.Vector;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.container.internal.AbstractBinding;
import info.pppc.pcom.system.container.internal.AbstractCreatorContext;
import info.pppc.pcom.system.container.internal.contract.DemandReader;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IAllocator;
import info.pppc.pcom.system.model.capability.IAllocatorContext;
import info.pppc.pcom.system.model.capability.IAllocatorStatus;
import info.pppc.pcom.system.model.capability.IAllocatorTemplate;
import info.pppc.pcom.system.model.capability.IResourceSetup;

/**
 * The allocator context provides the context for a allocator. It enables a allocator
 * to perform operations in a safely manner. Furthermore, it provides the callback
 * methods to retrieve descriptions and contracts and it enables the allocator to
 * revoke resources and to intialize a reevaluation of all existing resources.
 *  
 * @author Mac
 */
public class AllocatorContext extends AbstractCreatorContext implements IAllocatorContext {

	/**
	 * The state of the allocator whenever it is stopped.
	 */
	private static final int STATE_ALLOCATOR_STOPPED = 1;
	
	/**
	 * The state of the allocator whenever it is started.
	 */
	private static final int STATE_ALLOCATOR_STARTED = 2;
	
	/**
	 * A reference to the allocator that is managed by the context object.
	 */
	private IAllocator allocator;
	
	/**
	 * Creates a new allocator context object for the specified allocator with the
	 * specified identifier on the specified container.
	 * 
	 * @param container The container that hosts the allocator.
	 * @param allocator The allocator managed by the context object.
	 */
	public AllocatorContext(Container container, IAllocator allocator) {
		super(container, new AllocatorTemplate(allocator.getName()), new AllocatorStatus(allocator.getName()));
		this.allocator = allocator;
		setState(STATE_ALLOCATOR_STOPPED);
	}

	/**
	 * Returns the name of the allocator as a human readable string.
	 * 
	 * @return The human readable name of the allocator.
	 */
	public String getName() {
		try {
			return allocator.getName();	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not retrieve name.", t);
		}
		return "";
	}
	
	/**
	 * Returns a reference to the allocator that is managed by this context object.
	 * 
	 * @return A reference to the allocator managed by this context.
	 */
	public IAllocator getAllocator() {
		return allocator;
	}
	/**
	 * Called to start the allocator. If the allocator
	 * throws an exception, it will be passed on to the
	 * container.
	 * 
	 * @throws Throwable Thrown if the allocator is malformed.
	 */
	public void startAllocator() throws Throwable {
		if (getState() == STATE_ALLOCATOR_STOPPED) {
			setState(STATE_ALLOCATOR_STARTED);
			// this might throw an exception
			// which will be caught by the container
			allocator.setContext(this);
			allocator.start();
		}
		start();
	}
	
	/**
	 * Called to stop the allocator. Exceptions of the
	 * allocator will be caught. 
	 */
	public void stopAllocator() {
		if (getState() == STATE_ALLOCATOR_STARTED) {
			// stop the allocator
			try {
				allocator.stop();	
				allocator.unsetContext();
			} catch (Throwable e) {
				Logging.error(getClass(), "Exception while stopping allocator.", e);
			}
			setState(STATE_ALLOCATOR_STOPPED);
		}
		stop();
	}
	
	/**
	 * Derives all possible contracts from the contract passed to the
	 * allocator context. The passed contract must be a resource demand
	 * contract.
	 * 
	 * @param contract The resource demand contract that needs to
	 * 	be fulfilled.
	 * @return The contracts that can be used to fulfill the contract.
	 */
	public Vector deriveContracts(Contract contract) {
		if (contract.getType() == Contract.TYPE_RESOURCE_DEMAND) {
			DemandReader reader = new DemandReader(contract);
			try {
				IResourceSetup[] setups = allocator.deriveSetups(reader);
				if (setups != null) {
					Vector result = new Vector();
					for (int i = 0; i < setups.length; i++) {
						ResourceSetup setup = (ResourceSetup)setups[i];
						Contract c = setup.getContract();
						c.setName(contract.getName());
						Contract p = c.getContract(Contract.TYPE_RESOURCE_PROVISION);
						if (p == null) continue; 
						p.setName(contract.getName());
						if (! p.matches(contract, true)) continue;
						ResourceTemplate template = new ResourceTemplate(c);
						int[] estimate = allocator.estimateTemplate(template);
						c.setAttribute(Contract.ATTRIBUTE_RESOURCE_ESTIMATE, estimate);
						result.addElement(c); 
					}
					return result;
				}
			} catch (Throwable t) {
				Logging.error(getClass(), "Could not create contracts.", t);
			}
		} else {
			Logging.debug(getClass(), "Illegal contract type received for derival.");
		}
		return null;
	}
	
	/**
	 * Returns a resource usage estimate on the specified contract.
	 * 
	 * @param contract The contract of the resource estimate.
	 * @return The resource estimate or null if the estimation failed.
	 */
	public int[] estimateTemplate(Contract contract) {
		ResourceTemplate template = new ResourceTemplate(contract);
		int[] result = null;
		try {
			result = allocator.estimateTemplate(template);	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not get estimate.", t);
		}
		return result;
	}
	
	/**
	 * Returns the available resources of the allocator.
	 * 
	 * @return The available resources of the allocator.
	 */
	public int[] freeResources() {
		int[] result = null;
		try {
			result = allocator.freeResources();	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not get free.", t);
		}
		if (result == null) result = new int[0];
		return result;
	}
	
	/**
	 * Returns the total resources of the allocator.
	 * 
	 * @return The total resources of the allocator.
	 */
	public int[] totalResources() {
		int[] result = null;
		try {
			result = allocator.totalResources();	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not get total.", t);
		}
		if (result == null) result = new int[0];
		return result;
	}
	
	

	
	/**
	 * Tries to start the specified resource and returns true if the
	 * resource has been started or false if the resource could not
	 * be started.
	 * 
	 * @param context The context that describes the resource.
	 * @return True if the resource could be started, false otherwise.
	 */
	public boolean startResource(ResourceContext context) {
		try {
			return allocator.startResource(context);	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not start resource.", t);
		}
		return false;
	}
	
	/**
	 * Stops the specified resource context.
	 * 
	 * @param context The resource to stop.
	 */
	public void stopResource(ResourceContext context) {
		try {
			allocator.stopResource(context);	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not stop resource.", t);
		}
	}
	
	
	/**
	 * Pauses the specified resource context.
	 * 
	 * @param context The context to pause.
	 */
	public void pauseResource(ResourceContext context) {
		try {
			allocator.pauseResource(context);	
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not pause resource.", t);
		}
	}
	
	/**
	 * Called whenever the allocator has performed an operation that crashed and
	 * the allocator should be removed.
	 */
	protected void remove() {
		getContainer().removeAllocator(getIdentifier());
	}
	
	/**
	 * This is a helper method called by the base class to create a new resource
	 * binding with the specified type.
	 * 
	 * @param name The name of the resource binding to create.
	 * @return The resource binding with the specified name
	 */
	protected AbstractBinding createResource(String name) {
		return new ResourceBinding(this, name);
	}
	
	/**
	 * Returns the description of the allocator that can be used to manipulate the
	 * allocator requirements of the allocator.
	 * 
	 * @return The description of the allocator that denote the requirements towards
	 * 	other allocators.
	 */
	public IAllocatorTemplate getTemplate() {
		if (getState() == STATE_ALLOCATOR_STARTED) {
			return (IAllocatorTemplate)getAbstractTemplate();	
		} else {
			throw new IllegalStateException("Cannot retrieve template, allocator not started.");
		}
	}

	/**
	 * Returns the contract of the allocator that can be used to retrieve the 
	 * current allocator provisions issued by the container.
	 * 
	 * @return The contract of the allocator that describes the allocators that
	 * 	are currently provided to the allocator.
	 */
	public IAllocatorStatus getStatus() {
		if (getState() == STATE_ALLOCATOR_STARTED) {
			return (IAllocatorStatus)getAbstractStatus();	
		} else {
			throw new IllegalStateException("Cannot retrieve status, allocator not started.");
		}
	}

	/**
	 * Called by the allocator to initiate a reevaluation of the current assigments.
	 * Typically, this will be called if the available allocators have been increased.
	 */
	public void updateResources() {
		getContainer().updateResources();
	}

	/**
	 * Creates a new allocator resource description that can be used to describe the
	 * provision and requirements of a certain allocator assignemnt for some
	 * request.
	 * 
	 * @return A new resource description that can be used to describe a resource.
	 */
	public IResourceSetup createSetup() {
		return new ResourceSetup(getName());
	}
	
	/**
	 * Returns the accessor for a allocator dependency that is specified by
	 * the contract of the allocator. If the dependency is not resolved,
	 * this method returns null.
	 * 
	 * @param name The name of the allocator whose accessor should be retrieved.
	 * @return The accessor of the allocator or null if the allocator is not
	 * 	resolved or does not have an accessor.
	 */
	public Object getAccessor(String name) {
		if (getState() == STATE_ALLOCATOR_STARTED) {
			ResourceBinding binding = (ResourceBinding)getResource(name);
			if (binding != null) {
				return binding.getAccessor();
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve accessor, allocator is not started.");
		}
	}
	
	/**
	 * Returns the current description of the allocator. This method
	 * is used by the user interface only. The resulting structure
	 * is as follows: [objectID, name, total, free, template, status].
	 * 
	 * @return The current description of the allocator.
	 */
	public Object[] getDescription() {
		Object[] description = new Object[6];
		description[0] = getIdentifier();
		description[1] = getName();
		description[2] = totalResources();
		description[3] = freeResources();
		description[4] = getAbstractTemplate().getContract();
		description[5] = getAbstractStatus().getContract();
		return description;
	}
	
}
