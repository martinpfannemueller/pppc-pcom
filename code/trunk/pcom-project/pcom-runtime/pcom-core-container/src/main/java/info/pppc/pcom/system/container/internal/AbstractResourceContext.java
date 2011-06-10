package info.pppc.pcom.system.container.internal;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.MultiOperation;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.contract.Contract;

/**
 * The abstract resource context is the common abstract base context for
 * all context object with managed resource dependencies. In addition to
 * the pure resource repository contained in the common context, this
 * context also supports the management of templates and status objects.
 * 
 * 
 * @author Mac
 */
public abstract class AbstractResourceContext extends AbstractContext {
	
	/**
	 * The listener that notifies the context whenever the instance template
	 * is comitted.
	 */
	private IListener committer = new IListener() {
		public void handleEvent(Event event) {
			validate();			
		}
	};
	
	/**
	 * Creates a new abstract resource context using the specified container,
	 * template and status objects.
	 * 
	 * @param container The container that created the context.
	 * @param template The template of the element bound to this context.
	 * @param status The status of the element bound to this context.
	 */
	public AbstractResourceContext(Container container, AbstractTemplate template, AbstractStatus status) {
		super(container, template, status);
	}
	
	/**
	 * Adds the template commit listener to the template.
	 * The addition happens after the instance is started.
	 */
	protected void addTemplateListener() {
		getAbstractTemplate().addTemplateListener
		(AbstractTemplate.EVENT_TEMPLATE_COMMITTED, committer);	
	}

	/**
	 * Removes the template commit listener from the template.
	 * The removal removal happens before the element is stopped or paused.
	 */
	protected void removeTemplateListener() {
		getAbstractTemplate().removeTemplateListener
		(AbstractTemplate.EVENT_TEMPLATE_COMMITTED, committer);	
	}
	
	/**
	 * Called whenever the element bound to the context is about to be
	 * started. This will setup the template and status and it will
	 * release all potentially superficial resource dependencies.
	 * Finally, this will add a contract listener that signals notfications
	 * whenever the template is comitted.
	 * 
	 * @param assembly The assembly that describes the configuration.
	 * @return The multioperation that needs to be started to finalize the
	 * 	startup.
	 */
	protected MultiOperation start(Assembly assembly) {
		// modify the context's template and release superficial bindings
		AbstractTemplate template = getAbstractTemplate();
		Contract[] oldDemands = template.getContracts(Contract.TYPE_RESOURCE_DEMAND);
		Contract[] newDemands = assembly.getTemplate().getContracts(Contract.TYPE_RESOURCE_DEMAND);
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
			AbstractBinding binding = getResource(oldDemand.getName());
			if (binding != null) {
				binding.stop(true);
				removeResource(binding);
			}
		}
		// add new contracts and add bindings where they dont exist
		for (int i = 0; i < newDemands.length; i++) {
			Contract newDemand = newDemands[i];
			template.addContract(newDemand);
			AbstractBinding binding = getResource(newDemand.getName());
			if (binding == null) {
				binding = createResource(newDemand.getName());
				addResource(binding);
			}
		}
		// remove all resource related data from the status
		AbstractStatus status = getAbstractStatus();
		Contract[] oldProvisions = status.getContracts(Contract.TYPE_RESOURCE_PROVISION);
		for (int i = 0; i < oldProvisions.length; i++) {
			status.removeContract(oldProvisions[i]);
		}
		// start all bindings using the assembly
		MultiOperation result = new MultiOperation(this);
		Assembly[] assemblies = assembly.getResources();
		for (int i = 0; i < assemblies.length; i++) {
			final Assembly a = assemblies[i];
			final AbstractBinding binding = getResource(a.getName());
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
	 * Called whenever the element bound to the context is about
	 * to be paused.
	 * 
	 * @param state The state of the element that should be prepared
	 * 	with resource binding information.
	 */
	protected void setup(AssemblyState state) {
		AbstractBinding[] resources = getResources();
		for (int i = 0; i < resources.length; i++) {
			AbstractBinding binding = resources[i];
			binding.setup(state);
		}
	}
	
	/**
	 * Called whenever the element bound to the context should
	 * be paused. Removes the template listener to avoid updates
	 * and pauses the bindings.
	 * 
	 * @param pointer The pointer that can be used to ship
	 * 	the instance's state.
	 * @return The multi operation that must be started to finalize
	 * 	the operation.
	 */
	protected MultiOperation pause(AssemblyPointer pointer) {
		MultiOperation result = new MultiOperation(this);
		AssemblyPointer[] rpointers = pointer.getResources();
		for (int i = 0; i < rpointers.length; i++) {
			final AssemblyPointer rpointer = rpointers[i];
			final AbstractBinding binding = getResource(rpointer.getName());
			IOperation operation = new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					binding.pause(rpointer);		
				};
			};
			result.addOperation(operation);
		}
		return result;
	}
	
	/**
	 * Called whenever the element bound to the cntext is stopped.
	 * Removes the template listener to avoid updates. 
	 */
	protected void stop() {
		super.stop();
	}
	
}
