package info.pppc.pcom.system.container.internal.component;

import info.pppc.pcom.system.container.internal.capability.AllocatorTemplate;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IFactoryTemplate;

/**
 * The factory template provides a view on the resource demands that
 * are currently issued by a factory. As such, it provides a writeable
 * view on resource demands. 
 * 
 * @author Mac
 */
public class FactoryTemplate extends AllocatorTemplate implements IFactoryTemplate {

	/**
	 * Creates a new factory template with the specified name in the
	 * base contract.
	 * 
	 * @param name The name used for the base contract of the template.
	 */
	public FactoryTemplate(String name) {
		this(new Contract(Contract.TYPE_FACTORY_TEMPLATE, name));
	}
	
	/**
	 * Creates a new factory template with the specified contract
	 * as base data model.
	 * 
	 * @param c The contract used as base data model.
	 */
	protected FactoryTemplate(Contract c) {
		super(c);
	}
	
}
