package info.pppc.pcom.capability.lcdui;

import info.pppc.base.lcdui.Application;
import info.pppc.pcom.system.model.capability.IAllocator;
import info.pppc.pcom.system.model.capability.IAllocatorContext;
import info.pppc.pcom.system.model.capability.IResourceContext;
import info.pppc.pcom.system.model.capability.IResourceSetup;
import info.pppc.pcom.system.model.capability.IResourceTemplate;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleValidator;

/**
 * The ui allocator enables elements to access the pcom ui in a clean
 * fashion. Elements can get access to the ui by demanding a resource
 * of the ui accessor type. This will enable them to create elements
 * on the element manager of the ui and add contributions to the menu.
 * 
 * @author Mac
 */
public class LcdAllocator implements IAllocator {


	/**
	 * The name of the allocator as shown in user interfaces.
	 */
	private static final String CAPABILITY_NAME = "3PC LCDUI";
	
	/**
	 * The context of the allocator after the set context method
	 * has been called.
	 */
	private IAllocatorContext context;
	
	/**
	 * The appliation that will be used as manager.
	 */
	private Application application;
	
	/**
	 * The width of the display.
	 */
	private int width = 0;
	
	/**
	 * The height of the display.
	 */
	private int height = 0;
	
	/**
	 * Creates a new ui allocator using the specified application as
	 * element manager.
	 * 
	 * @param application The application used as base element manager
	 * 	inside the resource accessor objects.
	 */
	public LcdAllocator(Application application) {
		if (application == null) 
			throw new NullPointerException("Application must not be null.");
		this.application = application;
	}
	
	/**
	 * Returns the name of the allocator as used within the ui.
	 * 
	 * @return The name of the allocator as used in the ui.
	 */
	public String getName() {
		return CAPABILITY_NAME;
	}
	
	/**
	 * Called to set the context of the allocator.
	 * 
	 * @param context The context of the allocator.
	 */
	public void setContext(IAllocatorContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context of the allocator.
	 */
	public void unsetContext() {
		context = null;
	}
	
	/**
	 * Called to start the allocator. This will compute
	 * the width and height of the display.
	 */
	public void start() { 
		application.run(new Runnable() {
			public void run() {
				height = application.getDisplayHeight();
				width = application.getDisplayWidth();
			};
		});
	}
	
	/**
	 * Called to stop the allocator. This will reset the
	 * width and height of the display.
	 */
	public void stop() { 
		this.width = 0;
		this.height = 0;
	}
	
	/**
	 * Derives the possible setups for the specified demand.
	 * 
	 * @param demand The demand for which a setup should be
	 * 	derived.
	 * @return The setups for the demand.
	 */
	public IResourceSetup[] deriveSetups(IResourceDemandReader demand) {
		// only accept demands with an interface demand towards the com accessor
		if (demand.getInterfaces().length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(ILcdAccessor.class.getName());
		if (iface == null) return null;
		SimpleValidator validator = new SimpleValidator();
		validator.addFeature("DISPLAY", "WIDTH", new Integer(width));
		validator.addFeature("DISPLAY", "HEIGHT", new Integer(height));
		if (! validator.validate(iface)) return null;
		// create a setup that provides an interface provision of the accessor
		IResourceSetup setup = context.createSetup();
		IResourceProvisionWriter rpro = setup.getResource();
		rpro.createInterface(ILcdAccessor.class.getName());
		ITypeProvisionWriter ipro = rpro.getInterface(ILcdAccessor.class.getName());
		ipro.createDimension("DISPLAY");
		IDimensionProvisionWriter dpro = ipro.getDimension("DISPLAY");
		dpro.createFeature("WIDTH", new Integer(width));
		dpro.createFeature("HEIGHT", new Integer(height));
		return new IResourceSetup[] { setup };
	}
	
	/**
	 * Estimates the resource usage of the template. Since the
	 * number of users of this allocator is unlimited, each
	 * template has a total cost of 0.
	 * 
	 * @param template The template that should be estimated.
	 * @return The estimate for the template, always 0.
	 */
	public int[] estimateTemplate(IResourceTemplate template) {
		return new int[] { 0 };
	}
	
	/**
	 * Returns the amount of free resources of the allocator.
	 * Since each assignment does not cost anything the available
	 * resources are also set to 0.
	 * 
	 * @return The free resources of the allocator, i.e. 0.
	 */
	public int[] freeResources() {
		return new int[] { 0 };
	}
	
	/**
	 * Returns the total amount of resources of the allocator.
	 * Since the allocator's resource assignments are free,
	 * the total amount of available resources is 0.
	 * 
	 * @return The total resources of the allocator, i.e. 0.
	 */
	public int[] totalResources() {
		return new int[] { 0 };
	}


	/**
	 * Starts the specified resource assignment. This will
	 * setup the accessor object. The resource allocation
	 * will never fail, since this allocator can provide
	 * unlimited assignments.
	 * 
	 * @param context The context object that needs to be
	 * 	reserved.
	 * @return Always true.
	 */
	public boolean startResource(IResourceContext context) {
		if (context.getAccessor() == null) {
			context.setAccessor(new LcdAccessor(application));	
		}
		return true;
	}
	
	/**
	 * Pauses the specified resource assignment. For this
	 * allocator, this method does nothing.
	 * 
	 * @param context The context to pause.
	 */
	public void pauseResource(IResourceContext context) { }

	/**
	 * Stops the specified resource assignment. This will
	 * disable the accessor object to stop the element that
	 * uses the resource from cluttering the ui.
	 * 
	 * @param context The context to stop.
	 */
	public void stopResource(IResourceContext context) { 
		LcdAccessor accessor = (LcdAccessor)context.getAccessor();
		if (accessor != null) {
			accessor.dispose();					
		}
	}
	
}
