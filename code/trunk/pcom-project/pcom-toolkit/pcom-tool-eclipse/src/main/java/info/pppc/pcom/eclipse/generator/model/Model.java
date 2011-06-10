package info.pppc.pcom.eclipse.generator.model;

/**
 * The model provides the base element of models for the generator.
 * Each model element that will be generated has at least a class
 * name that describes the output file name.
 * 
 * @author Mac
 */
public class Model {

	/**
	 * The class name of the model element.
	 */
	private String classname;
	
	/**
	 * Creates a new model element with the specified class name.
	 * 
	 * @param classname
	 */
	public Model(String classname) {
		this.classname = classname;
	}
	
	/**
	 * Returns the class name of the model element.
	 * 
	 * @return The class name of the model element.
	 */
	public String getClassname() {
		return classname;
	}

}
