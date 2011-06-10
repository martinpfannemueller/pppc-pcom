package info.pppc.pcom.system.container.internal.component;

import info.pppc.pcom.system.container.internal.AbstractSetup;
import info.pppc.pcom.system.container.internal.contract.DemandWriter;
import info.pppc.pcom.system.container.internal.contract.IRemover;
import info.pppc.pcom.system.container.internal.contract.ProvisionWriter;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IInstanceSetup;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.writer.IInstanceDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceDemandWriter;

/**
 * The instance setup is used by factories to create the template for instance.
 * It provides a completely writeable view on the provision and demands of the
 * instance. The setup is always created indirectly through the context object
 * of the factory.
 * 
 * @author Mac
 */
public class InstanceSetup extends AbstractSetup implements IInstanceSetup, IRemover {

	/**
	 * Creates an instance setup with the specified name as name for
	 * the base contract of the setup.
	 * 
	 * @param name The name of the instance setup to create.
	 */
	public InstanceSetup(String name) {
		this(new Contract(Contract.TYPE_INSTANCE_TEMPLATE, name));
	}
	
	/**
	 * Creates a new instance setup with the specified contract.
	 * 
	 * @param contract The contract used as base data model of the
	 * 	setup.
	 */
	protected InstanceSetup(Contract contract) {
		super(contract);
	}

	/**
	 * Creates the view for the specified contract. Depending on the
	 * contract type this will either be a demand or a provision 
	 * writer.
	 * 
	 * @param c The contract whose view needs to be created.
	 * @return The view for the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_INSTANCE_PROVISION) {
			return new ProvisionWriter(null, c);
		} else if (c.getType() == Contract.TYPE_INSTANCE_DEMAND){
			return new DemandWriter(this, c);
		}  else if (c.getType() == Contract.TYPE_RESOURCE_DEMAND){
			return new DemandWriter(this, c);
		}
		throw new IllegalArgumentException("Cannot create view.");
	}

	/**
	 * Returns the provision contract of the instance. If this is the
	 * first access to the provision, the provision will be created and
	 * added first.
	 * 
	 * @return The provision writer used to modify the instance provision.
	 */
	public IInstanceProvisionWriter getInstance() {
		Contract c = getContract();
		Contract p = c.getContract(Contract.TYPE_INSTANCE_PROVISION);
		if (p == null) {
			p = new Contract(Contract.TYPE_INSTANCE_PROVISION, c.getName());
			addContract(p);
		}
		return (ProvisionWriter)getView(p);
	}

	/**
	 * Returns the resource demands that have been currently specified for
	 * the setup object.
	 * 
	 * @return The resource demand writers that are currently specified
	 * 	for the setup.
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
	 * Returns the resource demand writer for the resource demand with
	 * the specified name or null if this demand is not specified.
	 * 
	 * @param name The name of the resource demand to retrieve.
	 * @return The resource demand writer or null if it does not exist.
	 */
	public IResourceDemandWriter getResource(String name) {
		Contract c = getContract();
		return (DemandWriter)getView(c.getContract(Contract.TYPE_RESOURCE_DEMAND, name));
	}

	/**
	 * Creates a new resource demand with the specified name and returns
	 * the resource demand that has been replaced in the operation.
	 * 
	 * @param name The name of the resource demand that should be created.
	 * @return The resource demand that has been replaced or null if none
	 * 	has been replaced.
	 */
	public IResourceDemandWriter createResource(String name) {
		DemandWriter replaced = (DemandWriter)getResource(name);
		Contract c = new Contract(Contract.TYPE_RESOURCE_DEMAND, name);
		addContract(c);
		return replaced;
	}

	/**
	 * Returns the instance demands that are currently specified by the
	 * setup.
	 * 
	 * @return The instance demands that are currently specified.
	 */
	public IInstanceDemandWriter[] getInstances() {
		Contract c = getContract();
		Contract[] dems = c.getContracts(Contract.TYPE_INSTANCE_DEMAND);
		DemandWriter[] writers = new DemandWriter[dems.length];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = (DemandWriter)getView(dems[i]);
		}
		return writers;
	}

	/**
	 * Returns the instance demand with the specified name or null if such
	 * a demand does not exist.
	 * 
	 * @param name The name of the instance demand to retrieve.
	 * @return The instance demand with the name or null if it does not exist.
	 */
	public IInstanceDemandWriter getInstance(String name) {
		Contract c = getContract();
		return (DemandWriter)getView(c.getContract(Contract.TYPE_INSTANCE_DEMAND, name));
	}

	/**
	 * Creates a new instance demand with the specified name and returns the
	 * instance demand that has been replaced, if any.
	 * 
	 * @param name The name of the instance demand that should be created.
	 * @return The instance demand that has been replaced or null if none
	 * 	has been replaced.
	 */
	public IInstanceDemandWriter createInstance(String name) {
		DemandWriter replaced = (DemandWriter)getInstance(name);
		Contract c = new Contract(Contract.TYPE_INSTANCE_DEMAND, name);
		addContract(c);
		return replaced;
	}

	/**
	 * Implements the remover interface for the demand contracts of the
	 * setup. A call to this method will remove the contract from the
	 * base contract. If the passed contract is not a child of the 
	 * contract, this method will do nothing.
	 * 
	 * @param c The contract that should be removed from the base.
	 */
	public void remove(Contract c) {
		removeContract(c);
	}

	/**
	 * Returns the contract of the setup. For instance setups, this will
	 * be the instance template that can be used to create an instance.
	 * 
	 * @return The contract of the template.
	 */
	public Contract getContract() {
		return super.getContract();
	}
	
	/**
	 * Creates a deep copy of this instance setup.
	 * 
	 * @return A deep copy of this instance setup.
	 */
	public IInstanceSetup copySetup() {
		return new InstanceSetup(getContract().copy());
	}
	
}
