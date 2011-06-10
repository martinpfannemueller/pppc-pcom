package info.pppc.pcomx.assembler.gc.internal;

import info.pppc.base.system.SystemID;

import java.util.Vector;

/**
 * The abstract item is the common base class for all data structures
 * created during the configuration or adaptation of an application.
 * Items can either be bindings or elements.
 * 
 * @author Mac
 */
public abstract class AbstractItem {

	/**
	 * A reference to the application that uses this item.
	 */
	protected Application application;
	
	/**
	 * The templates that could be used to configure this item. The
	 * contents of the vector depend on the type of item. For bindings,
	 * this vector contains the descriptions of resources or instances.
	 * For elements this vector contains the plain templates of the
	 * element.
	 */
	protected Vector templates = new Vector();

	/**
	 * The system id of the system that would run the item. For bindings,
	 * this field will point to the system of the parent.
	 */
	protected SystemID systemID;
	
	/**
	 * The template that will be configured next time. This index is
	 * shifted in every configuration run.
	 */
	protected int template = 0;
	
	/**
	 * The pointer associated with this item or null if the item is newly 
	 * configured.
	 */
	protected Pointer pointer;
	
	/**
	 * The parent of the item or null if the item represents the anchor.
	 */
	protected AbstractItem parent;

	/**
	 * A flag that indicates whether the item is configured properly.
	 */
	protected boolean configured = false;
	
	/**
	 * Creates a new item using the specified application, parent and
	 * system id.
	 * 
	 * @param application The application that uses the item.
	 * @param parent The parent of the item or null if the item is an
	 * 	anchor.
	 * @param systemID The system id of the item. For elements this will
	 * 	be the executing system id. For bindings, this will be the 
	 * 	system id of the parent.
	 */
	public AbstractItem(Application application, AbstractItem parent, SystemID systemID) {
		this.application = application;
		this.parent = parent;
		this.systemID = systemID;
	}
	
	/**
	 * Returns the state of the configured flag. Returns true if the
	 * item assumes that it is configured properly. False if the item
	 * is not configured properly. An item thinks that it is configured
	 * properly whenever it has selected a set of children and delegated the 
	 * configuration successfully to them.
	 * 
	 * @return The state of the configured flag.
	 */
	public boolean isConfigured() {
		return configured;
	}
	
	/**
	 * Returns the index of the current selected template. If no template
	 * is selected, this index will be the size of the templates vector.
	 * 
	 * @return The index of the current selected template.
	 */
	public int getTemplate() {
		return template;
	}
	
	/**
	 * Returns the number of available templates that can be used to
	 * configure the item.
	 * 
	 * @return The number of available templates that can be used to
	 * 	configure the item.
	 */
	public int getTemplates() {
		return templates.size();
	}
	
	/**
	 * Returns the system id of the item. For bindings, this will be the
	 * system id of the parent. For elements, this will be the system
	 * id of the system that will eventually execute them.
	 * 
	 * @return The system id of the item.
	 */
	public SystemID getSystemID() {
		return systemID;
	}

	/**
	 * Returns the pointer that points to the state of the item
	 * or null if the item is not reusable.
	 * 
	 * @return The pointer that points to the state of the item
	 * 	or null if the item is not reusable.
	 */
	public Pointer getPointer() {
		return pointer;
	}
	
	/**
	 * Returns the parent of the item or null if this item is the anchor.
	 * 
	 * @return The parent of the item.
	 */
	public AbstractItem getParent() {
		return parent;
	}
	
	/**
	 * Returns the application of the item. This is a reference to the
	 * application that has created the item.
	 * 
	 * @return The application that has created the item.
	 */
	public Application getApplication() {
		return application;
	}
	
	/**
	 * Called whenever a child of the item could not configure itself.
	 */
	public void notifyFailure() {
		configured = false;
		template += 1;
		application.addItem(this);
	}

	/**
	 * Called whenever the parent does no longer require the item.
	 * This should recursively remove all state of the subtree.
	 */
	public abstract void release();
	
	/**
	 * Called by the configuration algorithm whenever the item
	 * should configure itself.
	 */
	public abstract void configure();
	
}
