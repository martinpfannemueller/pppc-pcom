package info.pppc.pcom.system.model.capability;

import info.pppc.pcom.system.model.IElement;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;

/**
 * The allocator is the basic interface that must be implemented by elements that
 * are capable of allocating resources. Allocators may isssue accessor objects and 
 * perform negotiations and reservations.
 * 
 * @author Mac
 */
public interface IAllocator extends IElement {

	/**
	 * Returns a non-null name of the resource that is human readable.
	 * 
	 * @return A human readable name of the resource.
	 */
	public String getName();
	
	/**
	 * Sets the context object of the allocator. The context object will be
	 * set before the start method is called. Using the context object, the
	 * resource can modify its demands towards other 
	 * 
	 * @param context The context object used to communicate with the container.
	 */
	public void setContext(IAllocatorContext context);
	
	/**
	 * Unsets the context object. The allocator must not access the context
	 * after this method has been called.
	 */
	public void unsetContext();
	
	/**
	 * Called to start the allocator. After this method has been called, the
	 * allocator must be able to handle incoming template requests.
	 */
	public void start();
	
	/**
	 * Called to stop the allocator. After this method has been called, the
	 * allocator will no longer be used.
	 */
	public void stop();
	
	/**
	 * Requests the allocator to derive its resource offers for a certain 
	 * resource demand.
	 * 
	 * @param demand The resource demand.
	 * @return The possible offers that match the resource demand.
	 */
	public IResourceSetup[] deriveSetups(IResourceDemandReader demand);

	
	/**
	 * Estimates a resource usage for the specified resource template.
	 * 
	 * @param template The template that should be estimated.
	 * @return A non-null array of ints that can be used to estimate
	 * 	the resource usage.
	 */
	public int[] estimateTemplate(IResourceTemplate template);
	
	/**
	 * Starts the specified context. This means that the resource needs
	 * to be created and the accessor object needs to be initialized.
	 * As long as the resource is started, the 
	 * 
	 * @param context The context object whose accessor should
	 * 	be created.
	 * @return True if successful, false if the resource cannot
	 * 	be reserved and created.
	 */
	public boolean startResource(IResourceContext context);

	/**
	 * Pauses the specified resource. This means that the resource will
	 * not be used at the moment. It might either be released or started
	 * again later on.
	 * 
	 * @param context The context that should be paused.
	 */
	public void pauseResource(IResourceContext context);
	
	/**
	 * Releases the specified resource assignment that has been created 
	 * or reserved earlier on through the reserve or create method.
	 * 
	 * @param context The resource that should be released.
	 */
	public void stopResource(IResourceContext context);

	/**
	 * Provides an estimate of the free resources that are currently
	 * available and could be reserved. 
	 * 
	 * @return A non-null integer array that denotes the amount of 
	 * 	available resources that could be used for resource reservations.
	 */
	public int[] freeResources();
	
	/**
	 * Provides an estimate of the total resources of this resource
	 * allocator. Note that some of these resources might be already
	 * granted to assignments.
	 * 
	 * @return A non-null integer array that denotes the amout of 
	 * 	total available resources that can be issued by this allocator.
	 */
	public int[] totalResources();
	
}

