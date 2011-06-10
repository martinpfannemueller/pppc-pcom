package info.pppc.pcom.eclipse.generator.model;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The factory model is used to generate factories. Factories
 * need to know the types of instances that they create and
 * they need to know the types of the proxies that will be
 * retrieved at runtime as well as the class name of the skeletons.
 * 
 * @author Mac
 */
public class FactoryModel extends Model {

	/**
	 * The name of the instances.
	 */
	private String instance;
	
	/**
	 * The class name of the skeleton.
	 */
	private String skeleton;
	
	/**
	 * The human readable name of the component.
	 */
	private String name;
	
	/**
	 * The dependency name to proxies mapping.
	 */
	private Hashtable proxies = new Hashtable();
	
	/**
	 * Creates a new factory model with the specified
	 * class name.
	 * 
	 * @param classname The class name of the factory.
	 */
	public FactoryModel(String classname) {
		super(classname);
	}

	/**
	 * Sets the class name of the instances created
	 * by the factory.
	 * 
	 * @param classname The class name of the instances.
	 */
	public void setInstance(String classname) {
		this.instance = classname;
	}
	
	/**
	 * Returns the class name of the instances created
	 * by the factory.
	 * 
	 * @return The class name of the instances.
	 */
	public String getInstance() {
		return instance;
	}
	
	/**
	 * Sets the class name of the skeleton for instances of
	 * the factory.
	 * 
	 * @param classname The skeleton class name for instances
	 * 	of the factory.
	 */
	public void setSkeleton(String classname) {
		this.skeleton = classname;
	}
	
	/**
	 * Returns the class name of the skeleton for instances of
	 * the factory.
	 * 
	 * @return The class name of skeletons for instances of the
	 * 	factory.
	 */
	public String getSkeleton() {
		return skeleton;
	}
	
	/**
	 * Adds the proxy name together with its class name
	 * to the set of proxies supported by the factory.
	 * 
	 * @param name The name of the proxy (ie. dependency).
	 * @param classname The class name of the proxy.
	 */
	public void addProxy(String name, String classname) {
		proxies.put(name, classname);
	}
	
	/**
	 * Removes the specified proxy name from the set of
	 * proxy names contained in the factory model.
	 * 
	 * @param name The proxy name to remove from the
	 * 	model.
	 */
	public void removeProxy(String name) {
		proxies.remove(name);
	}
	
	/**
	 * Returns the proxy class name for the proxy name.
	 * 
	 * @param name The name of the proxy (i.e. dependency).
	 * @return The class name of the proxy.
	 */
	public String getProxy(String name) {
		return (String)proxies.get(name);
	}
	
	/**
	 * Returns the names of proxies created by the factory.
	 * 
	 * @return The names of proxies created by the factory.
	 */
	public String[] getProxies() {
		String[] result = new String[proxies.size()];
		Enumeration e = proxies.keys();
		for (int i = 0; i < result.length; i++) {
			result[i] = (String)e.nextElement();
		}
		return result;
	}

	/**
	 * Returns the human readable name of the component.
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the human readable name of the component.
	 * 
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
