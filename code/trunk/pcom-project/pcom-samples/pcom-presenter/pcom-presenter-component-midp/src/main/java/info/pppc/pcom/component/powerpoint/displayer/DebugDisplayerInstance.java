package info.pppc.pcom.component.powerpoint.displayer;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.capability.lcdui.ILcdAccessor;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceStatus;
import info.pppc.pcom.system.model.contract.reader.IResourceProvisionReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleMapper;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class DebugDisplayerInstance implements IPowerpoint, IInstance, IListener {
	
	/**
	 * The factory that has created the instance.
	 */
	protected DebugDisplayerFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public DebugDisplayerInstance(DebugDisplayerFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Called to set the context of the instance.
	 *
	 * @param context The context of the instance.
	 */
	public void setContext(IInstanceContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context of the instance.
	 */
	public void unsetContext() {
		this.context = null;
	}
	
	/**
	 * Called to start the instance.
	 */
	public void start() {
		// recompute the mapping and perform future mappings
		handleEvent(null);
		context.getStatus().addStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);

	}

	/**
	 * Called to pause the instance. Nothing to be done.
	 */
	public void pause() { 
		// stop status recomputation and prohibit multiple listeners
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);
	}
	
	/**
	 * Called to stop the instance.
	 */
	public void stop() {
		// stop status recomputation and we are outa here
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return True if a presentation is open.
	 */
	public boolean isOpen() {
		Logging.log(getClass(), "Is open called.");
		return true;
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return The number of slides in the presentation.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */	
	public int getSlides() throws IllegalStateException {
		Logging.log(getClass(), "Get slides called.");
		return 1;
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param slide The slide number.
	 * @param width The resulting width.
	 * @param height The resulting height.
	 * @return The converted slide as jpeg.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 * @throws IllegalArgumentException Thrown if the args are malformed.
	 */
	public byte[] getSlide(int slide, int width, int height) throws IllegalArgumentException, IllegalStateException {
		Logging.log(getClass(), "Get slide called (slide, width, height).");
		return null;
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void close() throws IllegalStateException {
		Logging.log(getClass(), "Close called.");
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param file The file to open.
	 * @throws IllegalArgumentException Thrown if the presentation cannot be opened.
	 */
	public void open(byte[] file) throws IllegalArgumentException {
		Logging.log(getClass(), "Open(byte) called.");
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void firstSlide() throws IllegalStateException {
		Logging.log(getClass(), "First slide called.");
		
	}
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return The number of the current slide.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public int getSlide() throws IllegalStateException {
		Logging.log(getClass(), "Get slide called.");
		return 1;
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void lastSlide() throws IllegalStateException {
		Logging.log(getClass(), "Last slide called.");
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void nextSlide() throws IllegalStateException {
		try {
			Thread.sleep(40000);
		} catch (InterruptedException e) { }

		Logging.log(getClass(), "Next slide called.");
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void previousSlide() throws IllegalStateException {
		Logging.log(getClass(), "Previous slide called.");
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param slide The slide to go to.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 * @throws IllegalArgumentException Thrown if the args are malformed.
	 */
	public void setSlide(int slide) throws IllegalArgumentException, IllegalStateException {
		Logging.log(getClass(), "Set slide(int) called.");
	}
	
	/**
	 * Called whenever the status changes and the mapping must be performed.
	 * This will map the provision of the ui resource and of the ir resource
	 * directly to the provided interface.
	 * 
	 * @param event Ignored here.
	 */
	public void handleEvent(Event event) {
		IInstanceProvisionWriter instance = context.getTemplate().getInstance();
		ITypeProvisionWriter target = instance.getInterface(IPowerpoint.class.getName()); 
		// map the ui resource
		IResourceProvisionReader uireader = context.getStatus().getResource("UI");
		SimpleMapper uimapper = new SimpleMapper();
		uimapper.addDimension("DISPLAY", "DISPLAY");
		uimapper.map(uireader.getInterface(ILcdAccessor.class.getName()), target);
		context.getTemplate().commitTemplate();
	}
	
	/**
	 * Loads the state from a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		// nothing to be done for stateless components
	}
	
	/**
	 * Stores the state to a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to store to.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		// nothing to be done for stateless components		
	}
	
}
