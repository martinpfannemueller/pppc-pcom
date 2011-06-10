package info.pppc.pcom.system.container.internal.capability;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.operation.MultiOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.AssemblerProxy;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.container.internal.AbstractBinding;
import info.pppc.pcom.system.container.internal.AbstractContext;
import info.pppc.pcom.system.container.internal.AbstractResourceContext;
import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.AbstractTemplate;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IResourceContext;
import info.pppc.pcom.system.model.capability.IResourceStatus;
import info.pppc.pcom.system.model.capability.IResourceTemplate;

/**
 * The assignment context is the context object used to model resource
 * assignments that are provided by some resource. It contains methods
 * to describe the overall requirements and provision of the assignment
 * and the requirements of it.
 * 
 * @author Mac
 */
public class ResourceContext extends AbstractResourceContext implements IResourceContext {

	/**
	 * The state constant that dentoes that the resource bound to this
	 * context has been started.
	 */
	public static final int STATE_RESOURCE_STARTED = 1;
	
	/**
	 * The state constant that denotes that the resource bound to this
	 * context has been paused.
	 */
	public static final int STATE_RESOURCE_PAUSED = 2;
	
	/**
	 * The state constant that denotes that the resource bound to this
	 * context has been stopped.
	 */
	public static final int STATE_RESOURCE_STOPPED = 4;
	
	/**
	 * The resource context that has issued the assignment.
	 */
	private AllocatorContext allocator;
	
	/**
	 * The accessor object of the resource. This is the object that
	 * enables other resources and instances to access the resource.
	 * The type of the accessor depends on the type of the resource.
	 */
	private Object accessor;
	
	/**
	 * The binding that uses this resource.
	 */
	private ResourceBinding binding;
	
	/**
	 * Creates a new resource context for the specified resource 
	 * on the specified container with the specified identifier.
	 * 
	 * @param container The container that hosts the assignment.
	 * @param allocator The resource context of the resource that 
	 * 	issued the assignment.
	 * @param binding The binding that uses the resource context.
	 */
	public ResourceContext(Container container, AllocatorContext allocator, ResourceBinding binding) {
		super(container, new ResourceTemplate(binding.getName()), new ResourceStatus(binding.getName()));
		this.allocator = allocator;
		this.binding = binding;
		setState(STATE_RESOURCE_STOPPED);
	}

	
	/**
	 * Returns the provision contract.
	 * 
	 * @return the provision
	 */
	protected Contract getProvision() {
		ResourceTemplate template = (ResourceTemplate)getAbstractTemplate();
		return template.getContract(Contract.TYPE_RESOURCE_PROVISION);
	}
	
	/**
	 * Returns the resource context of the resource that issued the
	 * assignment.
	 * 
	 * @return The resource context object that issued the assignment.
	 */
	public AllocatorContext getAllocator() {
		return allocator;
	}
	
	/**
	 * Called to start the resource. This method might be called
	 * multiple times. If the resource has already been started,
	 * this method simply returns.
	 * 
	 * @param assembly The assembly that describes the resource
	 * 	configuration.
	 */
	public void startResource(Assembly assembly) {
		if (getState() == STATE_RESOURCE_PAUSED || getState() == STATE_RESOURCE_STOPPED) {
			// set the new demand to the new demand and set the
			// new provision to the assumed provision
			AbstractStatus status = getAbstractStatus();
			status.addContract(binding.getDemand());
			AbstractTemplate template = getAbstractTemplate();
			template.addContract(assembly.getTemplate().getContract
						(Contract.TYPE_RESOURCE_PROVISION));
			// resolve dependencies
			MultiOperation operations = start(assembly);
			operations.performSynchronous();
			getAbstractStatus().clearEvents();
			// determine whether startup was successful
			if (isValid(false)) {
				setState(STATE_RESOURCE_STARTED);
				if (! allocator.startResource(this)) {
					setState(STATE_RESOURCE_STOPPED);
				} else {
					addTemplateListener();
				}
			}
		}
		if (getState() != STATE_RESOURCE_STARTED) {
			// remove the provision and signal false
			getAbstractTemplate().removeContract
				(Contract.TYPE_RESOURCE_PROVISION, binding.getName());
		} 
	}

	/**
	 * Called to stop the resource. This method might be called
	 * multiple times. If the resource has been already stopped,
	 * this method simply returns.
	 */
	public void stopResource() {
		if (getState() == STATE_RESOURCE_PAUSED || getState() == STATE_RESOURCE_STARTED) {
			removeTemplateListener();
			setState(STATE_RESOURCE_STARTED);
			allocator.stopResource(this);	
			setState(STATE_RESOURCE_STOPPED);
		}
		stop();
	}

	/**
	 * Called to pause the resource. This method might be called
	 * multiple times. If the resource has been already paused,
	 * this method simply returns.
	 * 
	 * @param pointer The pointer 
	 */
	public void pauseResource(AssemblyPointer pointer) {
		if (getState() == STATE_RESOURCE_STARTED) {
			removeTemplateListener();
			allocator.pauseResource(this);
			setState(STATE_RESOURCE_PAUSED);	
		}
		// prepare assembly state with instance data
		AssemblyState state = new AssemblyState();
		state.setCreatorID(allocator.getIdentifier());
		state.setElementID(getIdentifier());
		state.setName(binding.getName());
		state.setSystemID(SystemID.SYSTEM);
		// prepare assembly state with binding data
		setup(state);
		try {
			AssemblerProxy proxy = new AssemblerProxy();
			proxy.setSourceID(new ReferenceID(SystemID.SYSTEM, IContainer.CONTAINER_ID));
			proxy.setTargetID(pointer.getAssemblerID());
			pointer = proxy.setup(pointer, state);
			MultiOperation operations = pause(pointer);
			operations.performSynchronous();
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not transfer state to assembler " + pointer.getAssemblerID() + ".", e);
			// remove me and my children
			getContainer().stopResource(getIdentifier());
			// break the parent binding
			binding.stop(false);
		}	
	}
	
	/**
	 * Called to create a new resource binding with the specified name.
	 * 
	 * @param name The name of the resource binding to create.
	 * @return The resource binding with the specified name.
	 */
	protected AbstractBinding createResource(String name) {
		return new ResourceBinding(this, name);
	}
	
	/**
	 * Called whenever the assignment has performed an operation that
	 * has crashed and the assignment must be removed.
	 */
	protected void remove() {
		removeResource();
	}
	
	/**
	 * Returns the contract that is provided to the assignment.
	 * 
	 * @return The contract that is issued to the assignment.
	 */
	public IResourceStatus getStatus() {
		if (getState() == STATE_RESOURCE_STARTED) {
			return (IResourceStatus)getAbstractStatus();
		} else {
			throw new IllegalStateException("Cannot retrieve status, resource is not started.");
		}

	}

	/**
	 * Returns the description that describes the requirements of the
	 * assigment.
	 * 
	 * @return The description of the requirements of the assignment.
	 */
	public IResourceTemplate getTemplate() {
		if (getState() == STATE_RESOURCE_STARTED) {
			return (IResourceTemplate)getAbstractTemplate();
		} else {
			throw new IllegalStateException("Cannot retrieve status, resource is not started.");
		}
	}

	/**
	 * Called to set the accessor of the resource.
	 * 
	 * @param accessor The new accessor of the resource.
	 */
	public void setAccessor(Object accessor) {
		if (getState() == STATE_RESOURCE_STARTED) {
			this.accessor = accessor;
		} else {
			throw new IllegalStateException("Cannot set accessor, resource is not started.");
		}
	}

	/**
	 * Called to return the accessor of the resource.
	 * 
	 * @return The accessor of the resource.
	 */
	public Object getAccessor() {
		if (getState() == STATE_RESOURCE_STARTED) {
			return accessor;
		} else {
			throw new IllegalStateException("Cannot retrieve accessor, resource is not started.");
		}
	}
	
	/**
	 * Called to signal that the contract of the resource has been
	 * broken and it needs to be changed.
	 */
	public void changeResource() {
		// get the parent user that uses the resource
		AbstractContext rootContext = binding.getParent();
		while (rootContext instanceof ResourceContext) {
			rootContext = ((ResourceContext)rootContext).binding.getParent();
		}
		synchronized (rootContext) {
			synchronized (getContainer()) {
				if (getState() == STATE_RESOURCE_STARTED) {
					// break the contract of the resource
					getAbstractTemplate().addContract
						(new Contract(Contract.TYPE_RESOURCE_DEMAND, binding.getName()));
					// signal the removal to the child up to the non-resource user
					validate();
					// pause the resource quickly
					removeTemplateListener();
					allocator.pauseResource(this);
					setState(STATE_RESOURCE_PAUSED);
				} else {
					throw new IllegalStateException("Cannot change resource, resource is not started.");
				}
			}
		}
	}
	
	/**
	 * Called to signal that the resource needs to be removed immediately.
	 * This will lead to a forceful remove that might break applications.
	 */
	public void removeResource() {
		// get the parent user that uses the resource
		AbstractContext rootContext = binding.getParent();
		while (rootContext instanceof ResourceContext) {
			rootContext = ((ResourceContext)rootContext).binding.getParent();
		}
		synchronized (rootContext) {
			synchronized (getContainer()) {
				if (getState() != STATE_RESOURCE_STOPPED) {
					// signal the removal to the child up to the instance
					binding.removeResource();
					// stop the resource including all children
					getContainer().stopResource(getIdentifier());
				}
			}
		}
	}

	/**
	 * Returns the accessor of the resource dependency with the specified
	 * name. The type of the returned accessor object depends on the resource
	 * type. If the resource does not have an accessor or if the dependency
	 * is not resolved properly, this method returns null.
	 * 
	 * @param name The name of the resource dependency whose accessor should
	 * 	be retrieved.
	 * @return The accessor of the resource or null if the resource is not
	 * 	bound or does not have an accessor.
	 */
	public Object getAccessor(String name) {
		if (getState() == STATE_RESOURCE_STARTED) {
			ResourceBinding binding = (ResourceBinding)getResource(name);
			if (binding != null) {
				return binding.getAccessor();
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve accessor, resource not started.");
		}
	}

	/**
	 * Determines whether the resource can still be executed.
	 */
	protected void validate() {
		getAbstractStatus().fireEvents();
		if (! isValid(true)) {
			getAbstractTemplate().addContract(new Contract(Contract.TYPE_RESOURCE_PROVISION, binding.getName()));
		}
		Contract provision = getAbstractTemplate().getContract
			(Contract.TYPE_RESOURCE_PROVISION, binding.getName());
		binding.changeResource(provision);
	}
	
	/**
	 * Returns the current description of the resource. This method
	 * is used by the user interface only. The resulting structure
	 * will be as follows: [objectID, name, estimate, template, status].
	 * 
	 * @return The current description of the resource.
	 */
	public Object[] getDescription() {
		Object[] description = new Object[5];
		description[0] = getIdentifier();
		description[1] = binding.getName();
		description[2] = new int[0];
		description[3] = getAbstractTemplate().getContract();
		description[4] = getAbstractStatus().getContract();
		return description;
	}
	
}
