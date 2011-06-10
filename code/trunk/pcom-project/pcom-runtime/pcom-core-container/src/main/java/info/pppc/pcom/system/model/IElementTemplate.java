package info.pppc.pcom.system.model;

/**
 * The element description is a marker interface for descriptions of 
 * elements. Typcially, elements can change their provisions and 
 * demands by manipulating the element description.
 * 
 * @author Mac
 */
public interface IElementTemplate {

	/**
	 * Signals the container that the template has changed and that
	 * it needs to be transfered and updated.
	 */
	public void commitTemplate();
	
}
