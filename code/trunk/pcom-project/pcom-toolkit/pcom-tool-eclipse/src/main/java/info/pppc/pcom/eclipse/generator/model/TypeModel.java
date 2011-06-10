package info.pppc.pcom.eclipse.generator.model;

import java.util.Vector;

import org.eclipse.jdt.core.IType;

/**
 * The type model is used for model elements that must support
 * certain types and that might be stateful or not.
 * 
 * @author Mac
 */
public class TypeModel extends Model {

	/**
	 * The interfaces that a type model element must implement.
	 */
	private Vector ifaces = new Vector();
	
	/**
	 * A flag that indicates whether the type model is stateful.
	 */
	private boolean stateful;
	
	/**
	 * Creates a new type model with the specified
	 * class name.
	 * 
	 * @param classname The class name of the type model.
	 */
	public TypeModel(String classname) {
		super(classname);
	}

	/**
	 * Sets a flag that indicates whether the type model will 
	 * be stateful.
	 * 
	 * @param stateful True if the type model will be stateful.
	 */
	public void setStateful(boolean stateful) {
		this.stateful = stateful;
	}
	
	/**
	 * Returns the flag that indicates whether the type model
	 * will be stateful.
	 * 
	 * @return True if the type model will be stateful.
	 */
	public boolean isStateful() {
		return stateful;
	}
	
	/**
	 * Adds the specified type to the set of types that
	 * needs to be supported by the type model element.
	 * 
	 * @param type The interface that the model element
	 *  should support.
	 */
	public void addInterface(IType type) {
		if (type != null) {
			ifaces.add(type);
		}
	}
	
	/**
	 * Removes the specified type from the set of types
	 * that needs to be supported by the model element.
	 * 
	 * @param type The interface that the model element
	 *  should not support.
	 * @return True if the type has been removed, false
	 * 	if it could not be found.
	 */
	public boolean removeInterface(IType type) {
		if (type != null) {
			return ifaces.remove(type);
		}
		return false;
	}
	
	/**
	 * Returns the types of interfaces that the model 
	 * element needs to support.
	 * 
	 * @return The interface types that the proxy needs
	 * 	to support.
	 */
	public IType[] getInterfaces() {
		return (IType[])ifaces.toArray(new IType[0]);
	}
	
}
