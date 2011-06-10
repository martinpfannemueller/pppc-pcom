package info.pppc.pcom.component.powerpoint.forwarder;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.component.portrayer.IPortrayer;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceRestorer;
import info.pppc.pcom.system.model.component.IInstanceStatus;
import info.pppc.pcom.system.model.contract.reader.IInstanceProvisionReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleMapper;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class ForwarderInstance implements IPowerpoint, IInstance, IListener {
	
	/**
	 * The default width that is used if no width is known.
	 */
	public static final int DEFAULT_WIDTH = 640;
	
	/**
	 * The default height that is used if no height is known.
	 */
	public static final int DEFAULT_HEIGHT = 480;
	
	/**
	 * The factory that has created the instance.
	 */
	protected ForwarderFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * The converter component to convert the slides.
	 */
	private ConverterProxy converter;
	
	/**
	 * The converter restorer is used to restore the converter state.
	 */
	private IInstanceRestorer converterRestorer;
	
	/**
	 * The portrayer component to display the slides.
	 */
	private PortrayerProxy portrayer;
	
	/**
	 * The portrayer restorer is used to restore the portrayer state.
	 */
	private IInstanceRestorer portrayerRestorer;
	
	/**
	 * The current active presentation or null if there is none.
	 */
	private byte[] presentation;
	
	/**
	 * The current slide of the show
	 */
	private int currentSlide = 0;
	
	/**
	 * The current width of the portrayer display.
	 */
	private int width;
	
	/**
	 * The current height of the portrayer display.
	 */
	private int height;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public ForwarderInstance(ForwarderFactory factory) {
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
		converter = (ConverterProxy)context.getProxy("CV");
		converterRestorer = context.getRestorer("CV");
		converterRestorer.getHistory().setEnabled(true);
		portrayer = (PortrayerProxy)context.getProxy("PT");
		portrayerRestorer = context.getRestorer("PT");
		portrayerRestorer.getHistory().setEnabled(true);
		handleEvent(null);
		context.getStatus().addStatusListener(IInstanceStatus.EVENT_INSTANCE_CHANGED, this);
	}
	
	/**
	 * Called to pause the instance. Nothing to be done here.
	 */
	public void pause() { 
		// stop status recomputation and prohibit multiple listeners
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_INSTANCE_CHANGED, this);
	}
	
	/**
	 * Called to stop the instance.
	 */
	public void stop() { 
		// stop status recomputation and we are outa here
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_INSTANCE_CHANGED, this);		
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return True if a presentation is open.
	 */
	public boolean isOpen() {
		try {
			return converter.isOpen();	
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
			return false;
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return The number of slides in the presentation.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */	
	public int getSlides() throws IllegalStateException {
		try {
			return converter.getSlides();	
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
			return 0;
		}
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
		try {
			return converter.getSlide(slide, width, height);	
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
			return null;
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 */
	public void close() {
		try {
			presentation = null;
			currentSlide = 0;
			converter.close();
			portrayerRestorer.getHistory().clear();
			portrayer.hidePicture();	
			// create checkpoints to reduce the size
			converterRestorer.createCheckpoint();
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param file The file to open.
	 * @throws IllegalArgumentException Thrown if the presentation cannot be opened.
	 */
	public void open(byte[] file) throws IllegalArgumentException {
		try {
			converter.open(file);
			currentSlide = 1;
			presentation = file;
			update();			
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void firstSlide() throws IllegalStateException {
		try {
			currentSlide = 1;
			update();				
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @return The number of the current slide.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public int getSlide() throws IllegalStateException {
		if (presentation == null) throw new IllegalStateException("Presentation not open.");
		return currentSlide;
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void lastSlide() throws IllegalStateException {
		try {
			currentSlide = converter.getSlides();
			update();			
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void nextSlide() throws IllegalStateException {
		try {
			if (!(currentSlide < converter.getSlides())) return;
			currentSlide++;
			update();
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @throws IllegalStateException Thrown if no presentation is open.
	 */
	public void previousSlide() throws IllegalStateException {
		try {
			if (!(currentSlide > 1)) return;
			currentSlide--;
			update();
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param slide The slide to go to.
	 * @throws IllegalStateException Thrown if no presentation is open.
	 * @throws IllegalArgumentException Thrown if the args are malformed.
	 */
	public void setSlide(int slide) throws IllegalArgumentException, IllegalStateException {
		try {
			if (!(slide <= converter.getSlides())) throw new IllegalArgumentException("Invalid Slide number.");
			currentSlide = slide;
			update();			
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Converts and outputs the current slide on the portrayer.
	 * 
	 * @throws InvocationException Thrown if the components cannot be reached.
	 */
	private void update() throws InvocationException {
		portrayerRestorer.getHistory().clear();
		if (currentSlide > 0) {
			byte[] picture = converter.getSlide(currentSlide, width, height);
			portrayer.showPicture(picture);	
		} else {
			portrayer.hidePicture();	
		}
	}
	
	/**
	 * Called whenever the status changes and the mapping must be performed.
	 * This will map the provision of the portrayer to the corresponding 
	 * fields.
	 * 
	 * @param event Ignored here.
	 */
	public void handleEvent(Event event) {
		IInstanceProvisionWriter instance = context.getTemplate().getInstance();
		instance.createInterface(IPowerpoint.class.getName());
		ITypeProvisionWriter target = instance.getInterface(IPowerpoint.class.getName()); 
		// map the ui resource
		IInstanceProvisionReader ptreader = context.getStatus().getInstance("PT");
		SimpleMapper ptmapper = new SimpleMapper();
		ptmapper.addDimension("DISPLAY", "DISPLAY");
		ptmapper.addFeature("LOCATION", "PORTRAYER", "LOCATION", "POWERPOINT");
		ptmapper.map(ptreader.getInterface(IPortrayer.class.getName()), target);
		// retrieve the display width and height		
		context.getTemplate().commitTemplate();
	}
	
	/**
	 * Loads the current presentation and slide from the checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		presentation = (byte[])checkpoint.getObject(CHECKPOINT_PRESENTATION);
		currentSlide = checkpoint.getInteger(CHECKPOINT_SLIDE);
		try {
			if (presentation == null) {
				close();
			} else {
				converter.open(presentation);
				update();
			}			
		} catch (InvocationException e) {
			Logging.error(getClass(), "Could not deliver invocation.", e);
		}
	}
	
	/**
	 * Stores the current presentation and slide to the checkpoint.
	 * 
	 * @param checkpoint The checkpoint to store.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		checkpoint.setComplete(true);
		checkpoint.putInteger(CHECKPOINT_SLIDE, currentSlide);
		checkpoint.putObject(CHECKPOINT_PRESENTATION, presentation);
	}
	
	
}
