package info.pppc.pcom.system.container.internal.capability;

import info.pppc.pcom.system.container.internal.AbstractStatus;
import info.pppc.pcom.system.container.internal.AbstractTemplate;
import info.pppc.pcom.system.container.internal.contract.DemandReader;
import info.pppc.pcom.system.container.internal.contract.ProvisionWriter;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.capability.IResourceTemplate;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;

/**
 * The resource template is the template used by resources. It provides a
 * readable view on the demands of the resource and a writable view on its
 * provision. Provision changes must be commited using the commit method.
 * However, they must always be within the bounds of the demand specified
 * in the resource status of the resource that uses the template.
 * 
 * @author Mac
 */
public class ResourceTemplate extends AbstractTemplate implements IResourceTemplate {

	/**
	 * Creates a new resource template with a resource template contract with
	 * the specified name.
	 * 
	 * @param name The name of the template contract.
	 */
	public ResourceTemplate(String name) {
		this(new Contract(Contract.TYPE_RESOURCE_TEMPLATE, name));
	}
	
	/**
	 * Creates a new resource template with the specified contract.
	 * 
	 * @param c The contract of the resource template.
	 */
	public ResourceTemplate(Contract c) {
		super(c);
	}

	/**
	 * Creates the view for a certain part of the resource contract. The
	 * resource template contains readable demands and a writable provision.
	 * 
	 * @param c The contract for which a view should be retrieved.
	 * @return The view of the contract.
	 */
	protected IContract createView(Contract c) {
		if (c.getType() == Contract.TYPE_RESOURCE_DEMAND) {
			return new DemandReader(c);
		} else if (c.getType() == Contract.TYPE_RESOURCE_PROVISION) {
			return new ProvisionWriter(null, c);
		}
		throw new IllegalArgumentException("Cannot create view.");
	}

	/**
	 * Returns the resource provision writer which is a writable view on 
	 * the resource provision contract stored in the template.
	 * 
	 * @return The resource provision writer of the template's provision.
	 */
	public IResourceProvisionWriter getResource() {
		Contract pro = getContract().getContract(Contract.TYPE_RESOURCE_PROVISION);
		return (IResourceProvisionWriter)getView(pro);
	}

	/**
	 * Returns the resource demands that are specified in the template.
	 * 
	 * @return The resource demands specified by the template.
	 */
	public IResourceDemandReader[] getResources() {
		Contract[] dems = getContract().getContracts(Contract.TYPE_RESOURCE_DEMAND);
		IResourceDemandReader[] readers = new IResourceDemandReader[dems.length];
		for (int i = 0; i < dems.length; i++) {
			readers[i] = (IResourceDemandReader)getView(dems[i]);
		}
		return readers;
	}

	/**
	 * Returns the resource demand with the specified type as specified by
	 * the template's contract.
	 * 
	 * @param name The type of the resource demand to retrieve.
	 * @return The resource demand with the specified type or null if such
	 * 	a demand does not exist.
	 */
	public IResourceDemandReader getResource(String name) {
		return (IResourceDemandReader)getView(getContract().getContract
			(Contract.TYPE_RESOURCE_DEMAND, name));
	}
	
	/**
	 * Match the instance template with the instance status.
	 * 
	 * @param status The status used for matching.
	 * @param provision A flag that indicates whether the provision of the
	 * 	resource should be checked too. If the flag is set to false, only
	 * 	the dependencies of the resource are checked.
	 * @return True if the instance template is met by the instance status.
	 */
	public boolean matches(AbstractStatus status, boolean provision) {
		if (status == null || !(status instanceof ResourceStatus)) return false;
		if (provision) {
			Contract dem = status.getContract(Contract.TYPE_RESOURCE_DEMAND);
			Contract pro = getContract(Contract.TYPE_RESOURCE_PROVISION);
			if (pro == null || ! pro.matches(dem, false)) {
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
