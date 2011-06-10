package info.pppc.pcom.system.container.internal.capability;

import info.pppc.pcom.system.container.internal.AbstractSetup;
import info.pppc.pcom.system.container.internal.contract.DemandWriter;
import info.pppc.pcom.system.container.internal.contract.IRemover;
import info.pppc.pcom.system.container.internal.contract.ProvisionWriter;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IResourceSetup;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;

/**
 * The resource setup is used by allocators to create templates for a resource.
 * In contrast to a resource template, the setup can be completely manipulated
 * as desired by the allocator. Thus, the setup provides a modifiable view on
 * the resouce demands and provision of a resource. Note that resource setups are 
 * created indirectly through the context object of the factory.
 * 
 * @author Mac
 */
public class ResourceSetup extends AbstractSetup implements IResourceSetup, IRemover {

	/**
	 * Creates a new resource setup with the specified name. The name will
	 * be used as name for the template that is the data model of the setup.
	 * The template can be retrieved using the get contract method.
	 * 
	 * @param name The name of the template.
	 */
	public ResourceSetup(String name) {
		this(new Contract(Contract.TYPE_RESOURCE_TEMPLATE, name));
	}

	/**
	 * Creates a new resource setup with the specified contract.
	 * 
	 * @param contract The contract used as data model by the setup.
	 */
	protected ResourceSetup(Contract contract) {
		super(contract);
	}

	/**
	 * Creates a view for the specified contract. If the contract is
	 * the provision contract, this method will return the a provision
	 * writer, otherwise this method will return a demand writer. 
	 * 
	 * @param c The contract used to create the view.
	 * @return The view for the specified contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_RESOURCE_PROVISION) {
			return new ProvisionWriter(null, c);
		} else if (c.getType() == Contract.TYPE_RESOURCE_DEMAND){
			return new DemandWriter(this, c);
		} 
		throw new IllegalArgumentException("Cannot create view for type " + c.getType() + ".");
	}

	/**
	 * Returns the resource provision writer that can be used to adjust
	 * the resource provision of the setup.
	 * 
	 * @return The resource provision writer used to adjust the provision.
	 */
	public IResourceProvisionWriter getResource() {
		Contract c = getContract();
		Contract p = c.getContract(Contract.TYPE_RESOURCE_PROVISION);
		if (p == null) {
			p = new Contract(Contract.TYPE_RESOURCE_PROVISION, c.getName());
			addContract(p);
		}
		return (ProvisionWriter)getView(p);
	}

	/**
	 * Returns the resource demand writers for the currently specified
	 * resource demands, these demands can be manipulated and removed.
	 * 
	 * @return The resource demand writers for the resource demands that
	 * 	are currently specified by the setup's contract.
	 */
	public IResourceDemandWriter[] getResources() {
		Contract c = getContract();
		Contract[] dems = c.getContracts(Contract.TYPE_RESOURCE_DEMAND);
		DemandWriter[] writers = new DemandWriter[dems.length];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = (DemandWriter)getView(dems[i]);
		}
		return writers;
	}

	/**
	 * Returns the resource demand writer for the resource demand with the 
	 * specified name or null if it does not exist.
	 * 
	 * @param name The name of the resource demand to retrieve.
	 * @return The resource demand writer for the demand with the specified 
	 * 	name or null if no such demand exists.
	 */
	public IResourceDemandWriter getResource(String name) {
		Contract c = getContract();
		return (DemandWriter)getView(c.getContract(Contract.TYPE_RESOURCE_DEMAND, name));
	}

	/**
	 * Creates a new resource demand with the specified name and returns the
	 * writer for the demand that has been replaced.
	 * 
	 * @param name The name of the resource demand that should be created.
	 * @return The resource demand writer of the contract that has been replaced
	 * 	or null if none has been replaced.
	 */
	public IResourceDemandWriter createResource(String name) {
		DemandWriter replaced = (DemandWriter)getResource(name);
		Contract c = new Contract(Contract.TYPE_RESOURCE_DEMAND, name);
		addContract(c);
		return replaced;
	}
	
	/**
	 * This method is the implementation of the remover interface. It removes
	 * a resource demand from the contract upon request.
	 * 
	 * @param c The contract that should be removed from the base contract.
	 */
	public void remove(Contract c) {
		removeContract(c);
	}
	
	/**
	 * Returns the contract of the setup. For resource setups, this will
	 * be the resource template that can be used to create a resource.
	 * 
	 * @return The contract of the template.
	 */
	public Contract getContract() {
		return super.getContract();
	}
	
	/**
	 * Returns a deep copy of this setup.
	 * 
	 * @return A deep copy of this setup.
	 */
	public IResourceSetup copySetup() {
		return new ResourceSetup(getContract().copy());
	}
	
}
