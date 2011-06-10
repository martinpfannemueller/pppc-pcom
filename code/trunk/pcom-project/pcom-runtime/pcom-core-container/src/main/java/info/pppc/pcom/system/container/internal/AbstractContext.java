package info.pppc.pcom.system.container.internal;

import java.util.Vector;

import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.NullMonitor;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.IElementContext;

/**
 * The common context is the abstract base class that is used by all
 * context objects. It contains methods to store and retrieve resource
 * bindings. However, it does not try to manage any of them.
 * 
 * @author Mac
 */
public abstract class AbstractContext implements IElementContext {

	/**
	 * A reference to the container of the context object.
	 */
	private Container container;
	
	/**
	 * The identifier of the element managed by the context.
	 */
	private ObjectID identifier = ObjectID.create();
	
	/**
	 * The vector that contains the resource bindings of the context.
	 */
	private Vector resources = new Vector();
	
	/**
	 * The template that describes the layout of the element bound
	 * to the context.
	 */
	private AbstractTemplate template;
	
	/**
	 * The status that describes the status of the element bound to
	 * the context.
	 */
	private AbstractStatus status;
	
	/**
	 * The state of the element bound to the class. The meaning is
	 * defined by the subclass.
	 */
	private int state;
	
	/**
	 * Creates a new element context for the specified container.
	 * 
	 * @param container The container of the context.
	 * @param template The template of the element bound to this context.
	 * @param status The status of the element bound to this context.
	 */
	protected AbstractContext(Container container,  AbstractTemplate template, AbstractStatus status) {
		this.container = container;
		this.template = template;
		this.status = status;
	}
	
	/**
	 * Returns the state of the element bound to the context.
	 * 
	 * @return Returns the state.
	 */
	protected int getState() {
		return state;
	}

	/**
	 * Sets the state of the element bound to the context.
	 * 
	 * @param state The state to set.
	 */
	protected void setState(int state) {
		this.state = state;
	} 
	
	/**
	 * Returns a direct reference to the status object.
	 * 
	 * @return A direct reference to the status object.
	 */
	protected AbstractStatus getAbstractStatus() {
		return status;
	}
	
	/**
	 * Returns a direct reference to the template object.
	 * 
	 * @return A direct reference to the template object.
	 */
	protected AbstractTemplate getAbstractTemplate() {
		return template;
	}
	
	/**
	 * Removes the specified child of the template from the template.
	 * 
	 * @param contract The contract that can be removed from the template.
	 */
	protected void removeTemplate(Contract contract) {
		template.removeContract(contract);
	}
	
	/**
	 * Determines whether the template is matched by the status.
	 * 
	 * @param provision A flag that indicates whether the provision of the
	 * 	element bound to the context should be validated, too. If
	 * 	the flag is false, only the dependencies of the element will
	 * 	be validated.
	 * @return True if the requirements contained in template and
	 * 	status match each other.
	 */
	protected boolean isValid(boolean provision) {
		return template.matches(status, provision);
	}
	
	/**
	 * Returns the reference to the container that hosts the
	 * context object.
	 * 
	 * @return The container of the context object.
	 */
	public Container getContainer() {
		return container;
	}
	
	/**
	 * Returns the identifier of the element.
	 * 
	 * @return The identifier of the element.
	 */
	public ObjectID getIdentifier() {
		return identifier;
	}
	
	/**
	 * Performs the specified operation with some default monitor. If the
	 * operation crashes, the element bound to this context will be removed.
	 * 
	 * @param operation The operation to perform.
	 */
	public void performOperation(IOperation operation) {
		performOperation(operation, new NullMonitor());
	}

	/**
	 * Performs the specified operation with the specified monitor. If the 
	 * operation crashes, the element bound to this context will be removed.
	 * 
	 * @param operation The operation to perform.
	 * @param monitor The monitor used by the operation.
	 */
	public void performOperation(final IOperation operation, IMonitor monitor) {
		final InvocationBroker broker = InvocationBroker.getInstance();
		broker.performOperation(new IOperation() {
			public void perform(IMonitor monitor) {
				try {
					operation.perform(monitor);
				} catch (Throwable t) {
					Logging.error(getClass(), "Operation crashed in " + identifier + ".", t);
					monitor.done();
					remove();	
				}
			}			
		}, monitor);
	}

	/**
	 * Called whenever an operation executed by the element crashes and 
	 * the element should be removed. Subclasses should call this
	 * implementation after they finished their cleanup.
	 */
	protected abstract void remove();	
	
	/**
	 * Adds the specified binding to the list of resources.
	 * 
	 * @param binding The binding that should be added.
	 * @return The binding that has been replaced or null if none.
	 */
	protected AbstractBinding addResource(AbstractBinding binding) {
		AbstractBinding result = removeResource(binding.getName());
		resources.addElement(binding);
		return result;
	}

	/**
	 * Returns the resource binding with the specified name or null
	 * if such a binding does not exist.
	 * 
	 * @param name The name of the resource binding to retrieve.
	 * @return The resource binding or null if it does not exist.
	 */
	protected AbstractBinding getResource(String name) {
		for (int i = 0; i < resources.size(); i++) {
			AbstractBinding b = (AbstractBinding)resources.elementAt(i);
			if (b.getName().equals(name)) {
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Returns all resource bindings that are currently contained
	 * in this context.
	 * 
	 * @return The resource bindings contained in the context.
	 */
	protected AbstractBinding[] getResources() {
		AbstractBinding[] result = new AbstractBinding[resources.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (AbstractBinding)resources.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Removes the resource binding with the specified name from the
	 * list of resource bindings.
	 * 
	 * @param name The name of the resource binding to remove.
	 * @return The resource binding that has been removed or null
	 * 	if none has been removed.
	 */
	protected AbstractBinding removeResource(String name) {
		for (int i = 0; i < resources.size(); i++) {
			AbstractBinding b = (AbstractBinding)resources.elementAt(i);
			if (b.getName().equals(name)) {
				resources.removeElementAt(i);
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Removes the specified resource binding and returns true if
	 * it has been returned or false if it has not been stored in
	 * the context.
	 * 
	 * @param binding The binding that should be removed.
	 * @return True if the binding has been removed, false otherwise.
	 */
	protected boolean removeResource(AbstractBinding binding) {
		return resources.removeElement(binding);
	}

	/**
	 * Stops the resource with the specified name.
	 * 
	 * @param name The name of the resource to stop.
	 */
	protected void stopResource(String name) {
		AbstractBinding resource = removeResource(name);
		if (resource != null) {
			resource.stop(true);	
		}
	}

	/**
	 * Starts a resource using the specified assembly.
	 * 
	 * @param assembly The assembly used to start the resource
	 */
	protected void startResource(Assembly assembly) {
		String name = assembly.getName();
		AbstractBinding binding = getResource(name);
		if (binding == null) {
			binding = createResource(name);
			addResource(binding);
		}
		binding.start(assembly);
	}
	
	/**
	 * Creates a resource binding for the specified name.
	 * 
	 * @param name The name of the resource binding to create.
	 * @return A new resource binding for the specified name.
	 */
	protected abstract AbstractBinding createResource(String name);
	
	
	/**
	 * Stops the context object and releases all resource bindings
	 * that are still contained in the context.
	 */
	protected void stop() {
		AbstractBinding[] resources = getResources();
		for (int i = 0; i < resources.length; i++) {
			removeResource(resources[i]);
			resources[i].stop(true);
		}
	}
		
	/**
	 * Returns a description of the internal state of the element
	 * bound to the context. The implementation depends on the
	 * type of element bound to the context. This method is used
	 * by the user interface.
	 * 
	 * @return The description of the element bound to the context.
	 */
	public abstract Object[] getDescription();
	
	/**
	 * Called by a binding to signal that the binding has changed
	 * and needs to be validated.
	 */
	protected abstract void validate();

}
