package info.pppc.pcom.system.container.internal.capability;

import info.pppc.base.system.SystemID;
import info.pppc.pcom.system.assembler.Assembly;
import info.pppc.pcom.system.assembler.AssemblyPointer;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcom.system.container.internal.AbstractBinding;
import info.pppc.pcom.system.container.internal.AbstractContext;
import info.pppc.pcom.system.contract.Contract;


/**
 * The resource binding models a bound resource.
 * 
 * @author Mac
 */
public class ResourceBinding extends AbstractBinding {
	
	/**
	 * The binding of the resource.
	 */
	private ResourceContext context;
	
	/**
	 * Creates a new resource binding with the specified name
	 * 
	 * @param name The name of the resource binding.
	 * @param context The context of the resource user. 
	 */
	public ResourceBinding(AbstractContext context, String name) {
		super(context, name);
	}

	/**
	 * Returns the resource accessor used to access the
	 * resource represented by the binding.
	 * 
	 * @return The resource accessor of the resource represented
	 * 	by the binding.
	 */
	public Object getAccessor() {
		if (isBound()) {
			return context.getAccessor();	
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the context that uses the resource binding.
	 * 
	 * @return The context that uses the binding.
	 */
	protected AbstractContext getParent() {
		return (AbstractContext)super.getContext();
	}

	/**
	 * Returns the demand that is currently directed towards the resource
	 * binding.
	 * 
	 * @return The demand directed towards the resource.
	 */
	protected Contract getDemand() {
		// we do not need to copy the demand as this will not change
		return getTemplate(Contract.TYPE_RESOURCE_DEMAND);
	}

	/**
	 * Starts the resource using the specified assembly.
	 * 
	 * @param assembly The assembly that describes the resource
	 * 	configuration.
	 */
	protected void start(Assembly assembly) {
		boolean reuse = assembly.getElementID() != null;
		if (! reuse && isBound()) {
			stop(true);			
		}
		context = getContext().getContainer().startResource(assembly, this);
		if (context != null) {
			setCreatorID(context.getAllocator().getIdentifier());
			setElementID(context.getIdentifier());
			// determine success based on provision
			Contract provision = context.getProvision();
			if (provision == null) {
				removeStatus(Contract.TYPE_RESOURCE_PROVISION);
			} else {
				// we do need to copy the provision, it might change
				setStatus(context.getProvision().copy());
			}
		} else {
			setCreatorID(null);
			setElementID(null);
			removeStatus(Contract.TYPE_RESOURCE_PROVISION);
		}
	}

	/**
	 * Determines whether the binding is bound to some
	 * context object.
	 * 
	 * @return True if the binding is bound, false otherwise.
	 */
	public boolean isBound() {
		return (super.isBound() && context != null);
	}

	/**
	 * Pauses the binding using the specified pointer to setup
	 * the state of the binding.
	 * 
	 * @param pointer The pointer of the assembly.
	 */
	protected void pause(AssemblyPointer pointer) {
		if (isBound()) {
			context.pauseResource(pointer);
		}
		
	}

	/**
	 * Prepares the using the data of the binding.
	 * 
	 * @param state The state that should be prepared.
	 */
	protected void setup(AssemblyState state) {
		if (isBound()) {
			state.putResource(getName(), SystemID.SYSTEM);
		}
	}

	/**
	 * Stops the resource bound to the binding.
	 * 
	 * @param notify A flag that indicates whether the context
	 * 	should be notified.
	 */
	protected void stop(boolean notify) {
		if (isBound()) {
			if (notify) {
				getContext().getContainer().stopResource(context.getIdentifier());
			}
			setCreatorID(null);
			setElementID(null);
			context = null;
			removeStatus(Contract.TYPE_RESOURCE_PROVISION);
		}
	}
	
	/**
	 * Called to signal that the resource bound to the binding has
	 * changed its provision. If the provision is null, the resource
	 * needs to be changed.
	 * 
	 * @param provision The provision that has changed.
	 */
	protected void changeResource(Contract provision) {
		if (provision == null) {
			removeStatus(Contract.TYPE_RESOURCE_PROVISION);
		} else {
			// we do need to copy the provision
			setStatus(provision.copy());
		}
		notifyChange();
	}
	
	/**
	 * Called to signal that the resource bound to the binding has
	 * been removed in a emergency removal operation.
	 */
	protected void removeResource() {
		stop(false);
		notifyChange();
	}
	
}
