package info.pppc.pcom.eclipse.generator.model;

/**
 * The skeleton model is used to 
 * 
 * @author Mac
 */
public class SkeletonModel extends TypeModel {

	/**
	 * The target class type used by the skeleton
	 * to cast the implementation.
	 */
	private String target;
	
	/**
	 * Creates a skeleton model with the specified
	 * class name.
	 *
	 * @param classname The classname of the skeleton.
	 */
	public SkeletonModel(String classname) {
		super(classname);
	}
	
	/**
	 * Sets the class name of the target class. This
	 * is the class that the skeleton will use to
	 * cast the type before dispatch.
	 * 
	 * @param classname The class name used for casting.
	 */
	public void setTarget(String classname) {
		target = classname;
	}
	
	/**
	 * Returns the class name of the target class.
	 * 
	 * @return The class name of the target class.
	 */
	public String getTarget() {
		return target;
	}

}
