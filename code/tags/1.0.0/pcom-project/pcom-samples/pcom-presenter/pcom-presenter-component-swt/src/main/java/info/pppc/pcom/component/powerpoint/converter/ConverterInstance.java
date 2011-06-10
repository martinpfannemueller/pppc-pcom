package info.pppc.pcom.component.powerpoint.converter;

import info.pppc.pcom.capability.com.ICOMAccessor;
import info.pppc.pcom.component.powerpoint.IConverter;
import info.pppc.pcom.component.powerpoint.common.Powerpoint;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class ConverterInstance implements IConverter, IInstance {
	
	/**
	 * The factory that has created the instance.
	 */
	protected ConverterFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * The powerpoint instance used by this converter.
	 */
	protected Powerpoint powerpoint;
	
	/**
	 * The presentation that is currently opened, or null if none.
	 */
	protected byte[] presentation;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public ConverterInstance(ConverterFactory factory) {
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
	}
	
	/**
	 * Called to pause the instance. Nothing to be done.
	 */
	public void pause() { }
	
	/**
	 * Called to stop the instance.
	 */
	public void stop() {
		if (powerpoint != null) {
			powerpoint.quit();
			powerpoint = null;
		}
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
	 */
	public void close() {
		presentation = null;
		powerpoint.close();
	}
	
	/**
	 * Application interface, just forward the message.
	 * 
	 * @param file The file to open.
	 * @throws IllegalArgumentException Thrown if the presentation cannot be opened.
	 */
	public void open(byte[] file) throws IllegalArgumentException {
		presentation = file;
		powerpoint.open(file, false);
	}
	
	/**
	 * Creates a checkpoint with the current presentation.
	 * 
	 * @param checkpoint The checkpoint to create.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		checkpoint.setComplete(true);
		checkpoint.putObject(CHECKPOINT_PRESENTATION, presentation);
	}
	
	/**
	 * Loads a checkpoint with the current presentation.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		byte[] presentation = (byte[])checkpoint.getObject(CHECKPOINT_PRESENTATION);
		if (presentation == null) close();
		else open(presentation);
	}
	
}
