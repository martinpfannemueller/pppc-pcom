package info.pppc.pcom.system.container.internal;

import info.pppc.base.system.ObjectID;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.contract.Contract;

/**
 * The abstract binding is the base class for bindings. Bindings can either be
 * resource bindings or instance bindings. Resource bindings are issued to all
 * elements, instance bindings however, are solely used within instances.
 * 
 * @author Mac
 */
public abstract class AbstractBinding {
		
	/**
	 * The name of the binding. This will be the name as specified in the 
	 * resource or instance demand. The name is immutable.
	 */
	private String name;
	
	/**
	 * The context that created the binding. The context is immutable and
	 * either to a resource, allocator, instance or factory context.
	 */
	private AbstractContext context;
	
	/**
	 * The element id of the element that is represented by the binding.
	 */
	private ObjectID elementID;
	
	/**
	 * The creator id of the element that is represented by the binding.
	 */
	private ObjectID creatorID;

	/**
	 * Creates a new binding with the specified name.
	 * 
	 * @param name The name of the binding.
	 * @param context The context that will hold the binding.
	 */
	protected AbstractBinding(AbstractContext context, String name) {
		if (name == null) throw new NullPointerException("Binding name is null.");
		if (context == null) throw new NullPointerException("Context is null.");
		this.name = name;
		this.context = context;
	}
	
	/**
	 * Returns the template child with the specified type using the
	 * name of the binding. This can be used to retrieve the demand
	 * for the binding by setting the type constant accrodingly.
	 * 
	 * @param type The type of the template child to retrieve.
	 * @return The template child or null if there is none.
	 */
	protected Contract getTemplate(byte type) {
		AbstractTemplate template = getContext().getAbstractTemplate();
		synchronized (template) {
			return template.getContract(type, name);	
		}
	}
	
	/**
	 * Removes the status child with the specified type using the
	 * name of the binding and returns it.
	 * 
	 * @param type The type of the status to remove.
	 * @return The removed contract or null if none has been removed.
	 */
	protected Contract removeStatus(byte type) {
		AbstractStatus status = getContext().getAbstractStatus();
		synchronized (status) {
			return status.removeContract(type, name);	
		}
	}
	
	/**
	 * Adds the specified status child and returns the child that has
	 * been removed. Note that the name of the contract must correspond
	 * to the name of the binding, otherwise an exception will be thrown.
	 * 
	 * @param contract The contract that should be set.
	 * @return The contract that has been replaced or null if none.
	 */
	protected Contract setStatus(Contract contract) {
		if (! contract.getName().equals(name)) throw new IllegalArgumentException("Illegal status set.");
		AbstractStatus status = getContext().getAbstractStatus();
		synchronized (status) {
			return status.addContract(contract);	
		}
	}
	
	/**
	 * Returns the immutable name of the binding. The name is typically, the
	 * name of the instance or resource as specified in the demand contract.
	 * 
	 * @return The name of the instance or resource binding.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the context that created the binding. This will be
	 * some form of element context, depending on the usage.
	 * 
	 * @return The context for the binding.
	 */
	protected AbstractContext getContext() {
		return context;
	}
	
	/**
	 * Returns the id of the creator of the element that is bound to
	 * this binding.
	 * 
	 * @return The id of the creator of the element bound to this
	 * 	binding.
	 */
	protected ObjectID getCreatorID() {
		return creatorID;
	}

	/**
	 * Returns the id of the element that is represented by this 
	 * binding.
	 * 
	 * @return The id of the element represented by this binding.
	 */
	protected ObjectID getElementID() {
		return elementID;
	}
	
	/**
	 * Sets the creator id of the binding.
	 * 
	 * @param creatorID The creatorID to set.
	 */
	protected void setCreatorID(ObjectID creatorID) {
		this.creatorID = creatorID;
	}

	/**
	 * Sets the element id of the binding.
	 * 
	 * @param elementID The elementID to set.
	 */
	protected void setElementID(ObjectID elementID) {
		this.elementID = elementID;
	}
	
	/**
	 * Starts the binding using the specified assembly information.
	 * 
	 * @param assembly The assembly information of the binding.
	 */
	protected abstract void start(Assembly assembly);
	
	/**
	 * Pauses the instance bound to this binding using the
	 * specified assembly pointer to execute the pause.
	 * 
	 * @param pointer The pointer used to retrieve the configuration.
	 */
	protected abstract void pause(AssemblyPointer pointer);

	/**
	 * Prepares the specified assembly state using the bindings
	 * internal data. Depending on the context implementation, this
	 * will set the data of the state in such a way that the
	 * configuration algorithm will create the right set of 
	 * pointers.
	 * 
	 * @param state The state that needs to be prepared with
	 * 	data. 
	 */
	protected abstract void setup(AssemblyState state);
	
	/**
	 * Releases the binding in a binding specific manor. This
	 * method must be implemented by each subclass.
	 * 
	 * @param notify A flag that signals whether a bound
	 * 	instance should be informed.
	 */
	protected abstract void stop(boolean notify);

	/**
	 * Determines whether the binding is valid. If it is valid, then
	 * to the best of the current local knowledge, the element represented
	 * by the binding is still running. If it is not valid, then the 
	 * binding is certainly broken.
	 * 
	 * @return True if the binding is valid, false if it is not 
	 * 	valid.
	 */
	protected boolean isBound() {
		return (creatorID != null && elementID != null);
	}
	
	/**
	 * Called to signal the parent of the binding that the binding
	 * has changed and need to be validated.
	 */
	protected void notifyChange() {
		context.validate();
	}

}
