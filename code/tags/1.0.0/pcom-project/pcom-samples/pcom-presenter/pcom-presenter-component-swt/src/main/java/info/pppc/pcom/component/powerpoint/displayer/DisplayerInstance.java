package info.pppc.pcom.component.powerpoint.displayer;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.pcom.capability.com.ICOMAccessor;
import info.pppc.pcom.capability.ir.IIRAccessor;
import info.pppc.pcom.capability.swtui.ISwtAccessor;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.component.powerpoint.common.Powerpoint;
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
public class DisplayerInstance implements IPowerpoint, IInstance, IListener {
	
	/**
	 * The factory that has created the instance.
	 */
	protected DisplayerFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * The powerpoint instance used by this converter.
	 */
	protected Powerpoint powerpoint;
	
	/**
	 * The presentation that is currently opened.
	 */
	protected byte[] presentation;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public DisplayerInstance(DisplayerFactory factory) {
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
		ICOMAccessor accessor = (ICOMAccessor)context.getAccessor("COM");
		if (accessor == null) throw new IllegalStateException("Accessor not set.");
		if (powerpoint == null) powerpoint = new Powerpoint(accessor);
		powerpoint.activate();
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
		if (powerpoint != null) {
			powerpoint.quit();
			powerpoint = null;
		}
		// stop status recomputation and we are outa here
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return True if a presentation is open.
	 */
	public boolean isOpen() {
		return powerpoint.isOpen();
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return The number of slides in the presentation.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */	
	public int getSlides() throws IllegalStateException {
		return powerpoint.getSlides();
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
		return powerpoint.getSlide(slide, width, height);
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void close() throws IllegalStateException {
		this.presentation = null;
		powerpoint.close();
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param file The file to open.
	 * @throws IllegalArgumentException Thrown if the presentation cannot be opened.
	 */
	public void open(byte[] file) throws IllegalArgumentException {
		this.presentation = file;
		powerpoint.open(file, true);
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void firstSlide() throws IllegalStateException {
		powerpoint.firstSlide();
		
	}
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return The number of the current slide.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public int getSlide() throws IllegalStateException {
		try {
			return powerpoint.getSlide();	
		} catch (Throwable t) {
			t.printStackTrace();
			throw (IllegalStateException)t;
		}
		
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void lastSlide() throws IllegalStateException {
		powerpoint.lastSlide();
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void nextSlide() throws IllegalStateException {
		powerpoint.nextSlide();
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void previousSlide() throws IllegalStateException {
		powerpoint.previousSlide();
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param slide The slide to go to.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 * @throws IllegalArgumentException Thrown if the args are malformed.
	 */
	public void setSlide(int slide) throws IllegalArgumentException, IllegalStateException {
		powerpoint.setSlide(slide);
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
		instance.createInterface(IPowerpoint.class.getName());
		ITypeProvisionWriter target = instance.getInterface(IPowerpoint.class.getName()); 
		// map the ui resource
		IResourceProvisionReader uireader = context.getStatus().getResource("UI");
		SimpleMapper uimapper = new SimpleMapper();
		uimapper.addDimension("DISPLAY", "DISPLAY");
		uimapper.map(uireader.getInterface(ISwtAccessor.class.getName()), target);
		// map the ir resource
		IResourceProvisionReader irreader = context.getStatus().getResource("IR");
		SimpleMapper irmapper = new SimpleMapper();
		irmapper.addFeature("IR", "LOCAL", "LOCATION", "POWERPOINT");
		irmapper.map(irreader.getInterface(IIRAccessor.class.getName()), target);
		context.getTemplate().commitTemplate();
	}
	
	/**
	 * Loads the current presentation and slide from the checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		presentation = (byte[])checkpoint.getObject(CHECKPOINT_PRESENTATION);
		int currentSlide = checkpoint.getInteger(CHECKPOINT_SLIDE);
		if (presentation != null) {
			open(presentation);
			setSlide(currentSlide);
		} else {
			close();
		}
	}
	
	/**
	 * Stores the current presentation and slide to the checkpoint.
	 * 
	 * @param checkpoint The checkpoint to store.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		checkpoint.setComplete(true);
		int slide = 1;
		if (powerpoint.isActivated()) slide = powerpoint.getSlide();
		checkpoint.putInteger(CHECKPOINT_SLIDE, slide);
		checkpoint.putObject(CHECKPOINT_PRESENTATION, presentation);
	}
	
}
