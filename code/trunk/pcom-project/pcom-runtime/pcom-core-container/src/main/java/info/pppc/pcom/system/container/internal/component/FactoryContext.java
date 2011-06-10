package info.pppc.pcom.system.container.internal.component;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.container.internal.AbstractBinding;
import info.pppc.pcom.system.container.internal.AbstractCreatorContext;
import info.pppc.pcom.system.container.internal.capability.ResourceBinding;
import info.pppc.pcom.system.container.internal.contract.DemandReader;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.component.IFactory;
import info.pppc.pcom.system.model.component.IFactoryContext;
import info.pppc.pcom.system.model.component.IFactoryStatus;
import info.pppc.pcom.system.model.component.IFactoryTemplate;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceSetup;

import java.util.Vector;

/**
 * The factory context manages a factory. It contains methods that
 * are used by the factory to modify its internal requirements and
 * to retrieve its current provisions. Furthermore, it contains internal
 * methods that are used by the container to manage the lifecycle of the
 * factory.
 * 
 * @author Mac
 */
public class FactoryContext extends AbstractCreatorContext implements IFactoryContext {

	/**
	 * The state that signals that the factory has been started.
	 */
	public static final int STATE_FACTORY_STARTED = 1;
	
	/**
	 * The state that signals that the factory has been stopped.
	 */
	public static final int STATE_FACTORY_STOPPED = 2;
	
	/**
	 * The reference to the factory managed by the context.
	 */
	private IFactory factory;
	
	/**
	 * Crates a new factory context for some factory.
	 * 
	 * @param container The container that hosts the context.
	 * @param factory The factory of the context object.
	 */
	public FactoryContext(Container container, IFactory factory) {
		super(container, new FactoryTemplate(factory.getName()), new FactoryStatus(factory.getName()));
		this.factory = factory;
		setState(STATE_FACTORY_STOPPED);
	}
	
	/**
	 * Returns the factory that is bound to this context object.
	 * 
	 * @return The factory bound to the context.
	 */
	public IFactory getFactory() {
		return factory;
	}
		
	/**
	 * Creates an instance of the factory bound to this context object.
	 * 
	 * @return An instance of the factory bound to this context.
	 */
	public IInstance createInstance() {
		return factory.createInstance();
	}
	
	/**
	 * Creates a proxy for the dependency of an instance created by this
	 * factory with the specified name.
	 * 
	 * @param name The name of the dependency whose proxy should be 
	 * 	retrieved.
	 * @return The instance proxy for the dependency.
	 */
	public InstanceProxy createProxy(String name) {
		return (InstanceProxy)factory.createProxy(name);
	}
	
	/**
	 * Creates a new skeleton for an instance of this factory.
	 * 
	 * @return The new skeleton for an instance of this factory.
	 */
	public InstanceSkeleton createSkeleton() {
		return (InstanceSkeleton)factory.createSkeleton();
	}
	
	/**
	 * Returns the human readable name of the factory.
	 * 
	 * @return The human readable name of the factory.
	 */
	public String getName() {
		return factory.getName();
	}
	
	/**
	 * Called to start the factory. It is safe to call this method
	 * multiple times. If the factory has been started already, this
	 * method does nothing.
	 * 
	 * @throws Throwable Thrown if the factory is defect.
	 */
	public void startFactory() throws Throwable {
		if (getState() == STATE_FACTORY_STOPPED) {
			setState(STATE_FACTORY_STARTED);
			// this might throw an exception which will be
			// caught by the container.
			factory.setContext(this);
			factory.start();				
		}
		start();
	}
	
	/**
	 * Called to stop the factory. It is safe to call this method
	 * multiple times. If the factory has been stopped already, this
	 * method does nothing.
	 */
	public void stopFactory() {
		if (getState() == STATE_FACTORY_STARTED) {
			try {
				factory.stop();
				factory.unsetContext();
			} catch (Throwable e) {
				Logging.error(getClass(), "Exception while stopping factory.", e);
			} 
			setState(STATE_FACTORY_STOPPED);
		}
		stop();
	}

	/**
	 * Derives all possible contracts for the specified contract. The contract
	 * must be of type instance demand.
	 * 
	 * @param contract The contract whose result set should be computed.
	 * @return The contracts created by the factory that meet the specified
	 * 	demand.
	 */
	public Vector deriveContracts(Contract contract) {
		if (contract.getType() == Contract.TYPE_INSTANCE_DEMAND) {
			DemandReader reader = new DemandReader(contract);
			try {
				IInstanceSetup[] setups = factory.deriveSetups(reader);
				if (setups != null) {
					Vector result = new Vector();
					for (int i = 0; i < setups.length; i++) {
						InstanceSetup setup = (InstanceSetup)setups[i];
						Contract c = setup.getContract();
						c.setName(contract.getName());
						Contract p = c.getContract(Contract.TYPE_INSTANCE_PROVISION);
						if (p == null) {
							Logging.debug(getClass(), "Received offer without provision from factory " + getIdentifier() + ".");
							continue;
						}
						if (p.matches(contract, true)) {
							p.setName(contract.getName());
							result.addElement(c); 
						} else {
							Logging.debug(getClass(), "Received incompatible offer from factory " + getIdentifier() + ".");
						}
					}
					return result;
				}
			} catch (Throwable t) {
				Logging.error(getClass(), "Could not create contracts.", t);
			}
		} else {
			Logging.debug(getClass(), "Illegal contract type received for derival.");
		}
		return null;
	}
	
	
	/**
	 * Called whenever the factory should be removed due to a crashed operation.
	 */
	protected void remove() {
		getContainer().removeFactory(getIdentifier());
	}

	/**
	 * This is a helper method called by the base class to create a new resource
	 * binding with the specified type.
	 * 
	 * @param name The name of the resource binding to create.
	 * @return The resource binding with the specified name
	 */
	protected AbstractBinding createResource(String name) {
		return new ResourceBinding(this, name);
	}
	
	/**
	 * Returns the factory description that describes the resource and 
	 * instance requirements of the factory.
	 * 
	 * @return The factory description for the factory bound to this 
	 * 	context.
	 */
	public IFactoryTemplate getTemplate() {
		if (getState() == STATE_FACTORY_STARTED) {
			return (IFactoryTemplate)getAbstractTemplate();	
		} else {
			throw new IllegalStateException("Cannot retrieve template, factory is not started.");
		}
	}

	/**
	 * Returns the factory contract that describes the resources and
	 * instances that are currently provided to the factory.
	 * 
	 * @return The factory contract for the factory bound to this
	 * 	context.
	 */
	public IFactoryStatus getStatus() {
		if (getState() == STATE_FACTORY_STARTED) {
			return (IFactoryStatus)getAbstractStatus();
		} else {
			throw new IllegalStateException("Cannot retrieve status, factory is not started.");
		}
	}
	
	/**
	 * Creates a new empty instance description that can be used by the factory
	 * to describe a provision and requirements of an instance for a certain
	 * instance demand.
	 * 
	 * @return A new and empty instance description that can be used to describe
	 * 	an instance.
	 */
	public IInstanceSetup createSetup() {
		if (getState() == STATE_FACTORY_STARTED) {
			return new InstanceSetup(factory.getName());	
		} else {
			throw new IllegalStateException("Cannot create setup, factory is not started.");
		}
	}
	
	/**
	 * Returns the resource accessor for the resource dependency with the specified
	 * name or null if the resource dependency has not been fulfilled.
	 * 
	 * @param name The name of the resource dependency whose accessor should
	 * 	be retrieved.
	 * @return The resource accesssor for the resource dependency with the 
	 * 	specified name or null if the dependency is not resolved.
	 */
	public Object getAccessor(String name) {
		if (getState() == STATE_FACTORY_STARTED) {
			ResourceBinding binding = (ResourceBinding)getResource(name);
			if (binding != null) {
				return binding.getAccessor();
			} else {
				return null;
			}
		} else {
			throw new IllegalStateException("Cannot retrieve accessor, factory is not started.");
		}
	}
	
	/**
	 * Returns the current description of the factory. This method
	 * is used by the user interface only. The resulting structure
	 * is [objectID, name, template, status].
	 * 
	 * @return The current description of the factory.
	 */
	public Object[] getDescription() {
		Object[] description = new Object[4];
		description[0] = getIdentifier();
		description[1] = factory.getName();
		description[2] = getAbstractTemplate().getContract();
		description[3] = getAbstractStatus().getContract();
		return description;
	}
	
}
