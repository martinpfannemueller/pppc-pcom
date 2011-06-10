package info.pppc.pcom.system.container.internal.capability;

import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.AbstractTemplate;
import info.pppc.pcom.system.container.internal.contract.DemandWriter;
import info.pppc.pcom.system.container.internal.contract.IRemover;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IAllocatorTemplate;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;

/**
 * The allocator template provides a view on the template of an allocator at
 * runtime. Using the template an allocator can request resources. Thus,
 * the template provides writers to change the resource demands at runtime.
 * If a change is made, the allocator must use the commit method to signal
 * the change.
 * 
 * @author Mac
 */
public class AllocatorTemplate extends AbstractTemplate 
	implements IAllocatorTemplate, IRemover {
	
	/**
	 * Creates a new allocator template with the specified name.
	 * 
	 * @param name The name of the allocator template to create.
	 */
	public AllocatorTemplate(String name) {
		this(new Contract(Contract.TYPE_ALLOCATOR_TEMPLATE, name));
	}
	
	/**
	 * Creates a new allocator template with the specified contract.
	 * 
	 * @param contract The contract used as base contract by the
	 * 	template.
	 */
	protected AllocatorTemplate(Contract contract) {
		super(contract);
	}

	/**
	 * Creates a view for the specified contract. Since the allocator
	 * contract may solely contain resource demands that can be modified,
	 * this method will always return a resource demand writer for the
	 * contract.
	 * 
	 * @param c The contract whose view needs to be created.
	 * @return The writable demand view for the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_RESOURCE_DEMAND) {
			return new DemandWriter(this, c);	
		}
		throw new IllegalArgumentException("Cannot create view.");
	}

	/**
	 * Creates a resource demand with the specified name and returns the
	 * resource demand that has been replaced, if any.
	 * 
	 * @param name The name of the resource demand to create.
	 * @return The resource demand that has been replaced or null if
	 * 	none has been replaced.
	 */
	public IResourceDemandWriter createResource(String name) {
		IResourceDemandWriter replaced = getResource(name);
		Contract c = new Contract(Contract.TYPE_RESOURCE_DEMAND, name);
		addContract(c);
		return replaced;
	}
	
	/**
	 * Returns the resource demand with the specified name or null if no
	 * such demand exists.
	 * 
	 * @param name The name of the resource demand to retrieve.
	 * @return The resource demand with the specified name or null if it
	 * 	does not exist.
	 */
	public IResourceDemandWriter getResource(String name) {
		Contract c = getContract().getContract(Contract.TYPE_RESOURCE_DEMAND, name);
		return (IResourceDemandWriter)getView(c);
	}
	
	/**
	 * Returns all resource demands that are currently specified by this
	 * allocator template.
	 * 
	 * @return An array of all resource demands that are currently specified
	 *  by this template. 
	 */
	public IResourceDemandWriter[] getResources() {
		Contract[] cs = getContract().getContracts(Contract.TYPE_RESOURCE_DEMAND);
		DemandWriter[] writers = new DemandWriter[cs.length];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = (DemandWriter)getView(cs[i]);
		}
		return writers;
	}
	
	/**
	 * Implements the remover interface. A call to this method will remove
	 * the corresponding contract child from the base contract.
	 * 
	 * @param contract The contract that should be removed.
	 */
	public void remove(Contract contract) {
		removeContract(contract);	
	}

	/**
	 * Determines whether the template matches the status.
	 * 
	 * @param status The status to match with.
	 * @param ignored The flag that indicates whether the provision should
	 * 	also be matched. This can be ignored since allocators do not provide
	 * 	anything.
	 * @return True if the match was successful, false otherwise.
	 */
	public boolean matches(AbstractStatus status, boolean ignored) {
		if (status == null || ! (status instanceof AllocatorStatus)) return false;
		Contract demand = status.getContract(Contract.TYPE_RESOURCE_DEMAND);
		Contract provision = getContract(Contract.TYPE_RESOURCE_PROVISION);
		if (provision == null || ! provision.matches(demand, false)) {
			return false;
		}
		Contract[] resources = getContracts(Contract.TYPE_RESOURCE_DEMAND);
		for (int i = 0; i < resources.length; i++) {
			Contract d = resources[i];
			Contract p = status.getContract(Contract.TYPE_RESOURCE_PROVISION, d.getName());
			if (p == null || ! p.matches(d, false)) return false;
		}
		return true;
	}
	
}
