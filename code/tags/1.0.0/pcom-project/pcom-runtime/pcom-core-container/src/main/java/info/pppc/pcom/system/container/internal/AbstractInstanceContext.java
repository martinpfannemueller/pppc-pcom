package info.pppc.pcom.system.container.internal;

import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.MultiOperation;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.contract.Contract;

import java.util.Vector;

/**
 * The abstract instance context extends the resource related management
 * fuctions of resource context objects to instances. It supports automated
 * management of instance bindings based on assemblies and states.
 * 
 * @author Mac
 */
public abstract class AbstractInstanceContext extends AbstractResourceContext {
	
	/**
	 * The phase is used by instances to detect outdated messages.
	 */
	private int phase = 0;
	
	/**
	 * The vector that contains the instance bindings used by the element
	 * managed by this context.
	 */
	private Vector instances = new Vector();
	
	/**
	 * Creates a new context using the specified container, template
	 * and status. The template and the status objects must support
	 * instances.
	 * 
	 * @param container The container that has created the context.
	 * @param template The template of the element that uses instances.
	 * @param status The status of the element that uses instances.
	 */
	protected AbstractInstanceContext(Container container, AbstractTemplate template, AbstractStatus status) {
		super(container, template, status);
	}
	
	/**
	 * Returns the current phase of the context object.
	 * 
	 * @return Returns the phase of the context.
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * Sets the current phase of the context.
	 * 
	 * @param phase The phase to set.
	 */
	protected void setPhase(int phase) {
		this.phase = phase;
	}
	
	/**
	 * Prepares the bindings according to the information provided
	 * in the assemblies. This will start all instances and resources
	 * that need to be started and it will release all that are no
	 * longer needed.
	 * 
	 * @param assembly The assembly that provides the configuration
	 * 	information.
	 * @return The multi operation that must be executed to finish
	 * 	the start request.
	 */
	protected MultiOperation start(Assembly assembly) {
		// adapt all resources to new assembly
		MultiOperation result = super.start(assembly);
		// addapt instances to new assembly
		AbstractTemplate template = getAbstractTemplate();
		Contract[] oldDemands = template.getContracts(Contract.TYPE_INSTANCE_DEMAND);
		Contract[] newDemands = assembly.getTemplate().getContracts(Contract.TYPE_INSTANCE_DEMAND);
		i: for (int i = 0; i < oldDemands.length; i++) {
			Contract oldDemand = oldDemands[i];
			template.removeContract(oldDemand);
			for (int j = 0; j < newDemands.length; j++) {
				Contract newDemand = newDemands[i];
				if (newDemand.getName().equals(oldDemand.getName())) {
					// old contract exists, add new one and reuse binding
					continue i;
				}
			}
			// old contract does not have a counterpart
			AbstractBinding binding = getInstance(oldDemand.getName());
			if (binding != null) {
				binding.stop(true);
				removeInstance(binding);
			}
		}
		// add new contracts and add bindings where they dont exist
		for (int i = 0; i < newDemands.length; i++) {
			Contract newDemand = newDemands[i];
			template.addContract(newDemand);
			AbstractBinding binding = getInstance(newDemand.getName());
			if (binding == null) {
				binding = createInstance(newDemand.getName());
				addInstance(binding);
			}
		}
		// remove all instance related status messages from the status
		AbstractStatus status = getAbstractStatus();
		Contract[] oldProvisions = status.getContracts(Contract.TYPE_INSTANCE_PROVISION);
		for (int i = 0; i < oldProvisions.length; i++) {
			status.removeContract(oldProvisions[i]);
		}
		// start all bindings using the assembly
		Assembly[] assemblies = assembly.getInstances();
		for (int i = 0; i < assemblies.length; i++) {
			final Assembly a = assemblies[i];
			final AbstractBinding binding = getInstance(a.getName());
			IOperation operation = new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					binding.start(a);		
				};
			};
			result.addOperation(operation);
		}
		return result;
	}
	
	/**
	 * Performs a setup of the assembly state using the instance and
	 * resource bindings that are currently contained in this context.
	 * 
	 * @param state The state of the assembly that needs to be updated.
	 */
	protected void setup(AssemblyState state) {
		super.setup(state);
		AbstractBinding[] instances = getInstances();
		for (int i = 0; i < instances.length; i++) {
			AbstractBinding binding = instances[i];
			binding.setup(state);
		}
	}
	
	/**
	 * Pauses the bindings contained in the context object and uses
	 * the pointer information to stop them.
	 * 
	 * @param pointer The pointer that will be used to pause the
	 * 	bindings.
	 * @return The multioperation that must be executed to finish
	 * 	the pause request.
	 */
	protected MultiOperation pause(AssemblyPointer pointer) {
		MultiOperation result = super.pause(pointer);
		AssemblyPointer[] ipointers = pointer.getInstances();
		for (int i = 0; i < ipointers.length; i++) {
			final AssemblyPointer ipointer = ipointers[i];
			final AbstractBinding binding = getInstance(ipointer.getName());
			IOperation operation = new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					binding.pause(ipointer);		
				};
			};
			result.addOperation(operation);
		}
		return result;
	}
	
	/**
	 * Stops all bindings contained in the context object and informs
	 * them that they are no longer needed.
	 */
	protected void stop() {
		super.stop();
		AbstractBinding[] instances = getInstances();
		for (int i = 0; i < instances.length; i++) {
			removeInstance(instances[i]);
			instances[i].stop(true);
		}
	}
	
	/**
	 * Abstract factory method that must return an instance binding
	 * for the instance dependency with the specified name.
	 * 
	 * @param name The name of the instance binding that must be created.
	 * @return A new uninitialized instance binding with the specified name.
	 */
	protected abstract AbstractBinding createInstance(String name);
	
	/**
	 * Adds the instance binding to the set of bindings managed by
	 * this context object.
	 * 
	 * @param binding The binding that should be added.
	 * @return The binding that might have been replaced or null,
	 * 	if none.
	 */
	protected AbstractBinding addInstance(AbstractBinding binding) {
		AbstractBinding result = removeInstance(binding.getName());
		instances.addElement(binding);
		return result;
	}
	
	/**
	 * Returns the instance binding with the specified name or null
	 * if such a binding does not exist.
	 * 
	 * @param name The name of the instance binding to retrieve.
	 * @return The instance binding with the specified name or null.
	 */
	protected AbstractBinding getInstance(String name) {
		for (int i = 0; i < instances.size(); i++) {
			AbstractBinding b = (AbstractBinding)instances.elementAt(i);
			if (b.getName().equals(name)) {
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Returns all instance bindings that are contained in this context.
	 * 
	 * @return All instance bindings contained in this context.
	 */
	protected AbstractBinding[] getInstances() {
		AbstractBinding[] result = new AbstractBinding[instances.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (AbstractBinding)instances.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Removes the instance binding with the specified name and returns
	 * the binding that has been removed or null if the binding did not
	 * exist.
	 * 
	 * @param name The name of the instance binding to remove.
	 * @return The instance binding that has been removed, or null if none.
	 */
	protected AbstractBinding removeInstance(String name) {
		for (int i = 0; i < instances.size(); i++) {
			AbstractBinding b = (AbstractBinding)instances.elementAt(i);
			if (b.getName().equals(name)) {
				instances.removeElementAt(i);
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Removes the specified instance binding from the set of bindings
	 * and returns true if the binding has been removed, false if it
	 * was not present.
	 * 
	 * @param binding The binding that should be removed.
	 * @return True if the binding has been found, false otherwise.
	 */
	protected boolean removeInstance(AbstractBinding binding) {
		return instances.removeElement(binding);
	}
	
}
