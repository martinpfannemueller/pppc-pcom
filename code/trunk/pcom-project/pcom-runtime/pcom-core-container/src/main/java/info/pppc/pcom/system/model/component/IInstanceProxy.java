package info.pppc.pcom.system.model.component;

import info.pppc.base.system.ReferenceID;

/**
 * The instance proxy is the basic interface for proxies created
 * by the pcom component generator. Currently, this interface defines
 * a method that can be used to retrieve the reference id to the remote
 * instance.
 * 
 * @author Mac
 */
public interface IInstanceProxy {

	/**
	 * Returns the reference id of the component that is bound
	 * to this proxy.
	 * 
	 * @return The reference id of the component that is bound
	 * 	to the proxy.
	 */
	public ReferenceID getTargetID();	
	
}
