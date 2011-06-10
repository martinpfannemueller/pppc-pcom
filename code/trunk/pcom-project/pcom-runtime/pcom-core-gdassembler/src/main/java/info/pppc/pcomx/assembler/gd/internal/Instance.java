package info.pppc.pcomx.assembler.gd.internal;

import java.util.Hashtable;
import java.util.Vector;

import info.pppc.base.system.ObjectID;

/**
 * The instance contains the configuration data required
 * to configure an instance. An instance is always configured
 * for a certain request and whenever it is resolved, it
 * will contain bindings in its bindings hashtable and 
 * assemblies in its resource table.
 * 
 * @author Mac
 */
public class Instance {

	/**
	 * The creator that will create the instance.
	 */
	protected final ObjectID creator;
	
	/**
	 * The templates that can be provided by the creator.
	 */
	protected final Vector templates;
	
	/**
	 * The instance request that created the instance.
	 */
	protected final InstanceRequest request;
	
	/**
	 * The bindings for the instance hashed by name.
	 */
	protected final Hashtable bindings = new Hashtable();
	
	/**
	 * The assemblies for the resources hashed by name.
	 */
	protected final Hashtable resources = new Hashtable();
	
	/**
	 * A flag that indicates whether the instance is reused.
	 */
	protected final boolean reuse;
	
	/**
	 * Creates a new instance for the specified factory
	 * using the specified templates.
	 * 
	 * @param request The request that created the instance.
	 * @param creator The creator of the instance.
	 * @param templates The possible templates.
	 * @param reuse A flag that indicates whether the instance
	 * 	would cause a reuse.
	 */
	public Instance(InstanceRequest request, ObjectID creator, Vector templates, boolean reuse) {
		this.creator = creator;
		this.templates = templates;
		this.request = request;
		this.reuse = reuse;
	}

	
	
}
