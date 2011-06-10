package info.pppc.pcom.eclipse.generator.model;

/**
 * The instance model is used to create instances.
 * 
 * @author Mac
 */
public class InstanceModel extends TypeModel {

	/**
	 * The class name of the factory.
	 */
	public String factory;
	
	/**
	 * Creates a new instance model for the specified
	 * class name.
	 * 
	 * @param classname The name of the instance.
	 */
	public InstanceModel(String classname) {
		super(classname);
	}
	
	/**
	 * Sets the factory class name used within the
	 * constructor to enable instances to access
	 * their factories.
	 * 
	 * @param classname The class name of the factory.
	 */
	public void setFactory(String classname) {
		this.factory = classname;
	}
	
	/**
	 * Returns the factory class name used within
	 * the construtor to enable instances accessing
	 * their factories.
	 * 
	 * @return The class name of the factory.
	 */
	public String getFactory() {
		return factory;
	}

}
