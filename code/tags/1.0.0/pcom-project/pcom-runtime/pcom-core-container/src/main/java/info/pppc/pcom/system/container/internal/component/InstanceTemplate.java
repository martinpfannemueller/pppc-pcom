package info.pppc.pcom.system.container.internal.component;

import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.AbstractTemplate;
import info.pppc.pcom.system.container.internal.contract.DemandReader;
import info.pppc.pcom.system.container.internal.contract.ProvisionWriter;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IInstanceTemplate;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IInstanceDemandReader;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;

/**
 * The instance template provides a view on the contract fulfilled by 
 * an instance at runtime. The instance can change its provision using
 * a writer. However, it cannot change its demands directly. If the 
 * provision is changed, the change needs to be signalled explicitly using
 * the commit template method.
 * 
 * @author Mac
 */
public class InstanceTemplate extends AbstractTemplate implements IInstanceTemplate {

	/**
	 * Creates a new instance template with the specified name as
	 * name for the base instance template contract.
	 * 
	 * @param name The name of the base instance template contract
	 * 	that acts as data model.
	 */
	public InstanceTemplate(String name) {
		this(new Contract(Contract.TYPE_INSTANCE_TEMPLATE, name));
	}
	
	/**
	 * Creates a new instance template with the specified contract as
	 * data model.
	 * 
	 * @param contract The contract that will be used as the templates
	 * 	data model.
	 */
	protected InstanceTemplate(Contract contract) {
		super(contract);
	}
	
	/**
	 * Creates a view for the specified contract. For the provision,
	 * this method will create a writeable view. For the demands, this
	 * method will create readers only.
	 * 
	 * @param c The contract whose view needs to be created.
	 * @return The readable or writable view of the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_INSTANCE_PROVISION) {
			return new ProvisionWriter(null, c);
		} else if (c.getType() == Contract.TYPE_INSTANCE_DEMAND) {
			return new DemandReader(c);
		} else if (c.getType() == Contract.TYPE_RESOURCE_DEMAND) {
			return new DemandReader(c);
		}
		throw new IllegalArgumentException("Cannot create view.");
	}

	/**
	 * Returns the provision that is currently granted by the instance.
	 * If the provision is changed, the change needs to be comitted.
	 * 
	 * @return The provision that is currently granted.
	 */
	public IInstanceProvisionWriter getInstance() {
		Contract c = getContract();
		return (ProvisionWriter)getView
			(c.getContract(Contract.TYPE_INSTANCE_PROVISION));
	}

	/**
	 * Returns the instance demands that are currently issued by the
	 * instance that relies on this template.
	 * 
	 * @return The instance demands that are currently specified by
	 * 	the instance.
	 */
	public IInstanceDemandReader[] getInstances() {
		Contract c = getContract();
		Contract[] dems = c.getContracts(Contract.TYPE_INSTANCE_DEMAND);
		DemandReader[] readers = new DemandReader[dems.length];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = (DemandReader)getView(dems[i]);
		}
		return readers;
	}

	/**
	 * Returns the instance demand with the specified name or null
	 * if such a demand does not exist.
	 * 
	 * @param name The name of the instance demand to retrieve.
	 * @return The instance demand reader with the specified name or 
	 * 	null if it does not exist.
	 */
	public IInstanceDemandReader getInstance(String name) {
		Contract c = getContract();
		return (DemandReader)getView
			(c.getContract(Contract.TYPE_INSTANCE_DEMAND, name));
	}

	/**
	 * Returns the resource demands that are currently specified by the
	 * template.
	 * 
	 * @return The resource demands that are currently specified by the
	 * 	template.
	 */
	public IResourceDemandReader[] getResources() {
		Contract c = getContract();
		Contract[] dems = c.getContracts(Contract.TYPE_RESOURCE_DEMAND);
		DemandReader[] readers = new DemandReader[dems.length];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = (DemandReader)getView(dems[i]);
		}
		return readers;		
	}
 
	/**
	 * Returns the resource demand with the specified name or null if
	 * no such demand exists.
	 * 
	 * @param name The name of the demand to retrieve.
	 * @return The demand with the specified name or null if it does 
	 * 	not exist.
	 */
	public IResourceDemandReader getResource(String name) {
		Contract c = getContract();
		return (DemandReader)getView
			(c.getContract(Contract.TYPE_RESOURCE_DEMAND, name));
	}

	/**
	 * Match the instance template with the instance status.
	 * 
	 * @param status The status used for matching.
	 * @param provision A flag that indicates whether the provision of
	 * 	the instance should be checked too. If the flag is set to false,
	 * 	only the dependencies will be validated.
	 * @return True if the instance template is met by the instance status.
	 */
	public boolean matches(AbstractStatus status, boolean provision) {
		if (status == null || !(status instanceof InstanceStatus)) return false;
		if (provision) {
			Contract dem = status.getContract(Contract.TYPE_INSTANCE_DEMAND);
			Contract pro = getContract(Contract.TYPE_INSTANCE_PROVISION);
			if (pro == null || ! pro.matches(dem, false)) {
				return false;
			}			
		}
		Contract[] instances = getContracts(Contract.TYPE_INSTANCE_DEMAND);
		for (int i = 0; i < instances.length; i++) {
			Contract d = instances[i];
			Contract p = status.getContract(Contract.TYPE_INSTANCE_PROVISION, d.getName());
			if (p == null || ! p.matches(d, false)) {
				return false;
			}
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
