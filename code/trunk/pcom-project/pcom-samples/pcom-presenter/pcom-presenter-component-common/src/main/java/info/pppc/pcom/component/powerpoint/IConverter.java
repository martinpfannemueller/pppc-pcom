package info.pppc.pcom.component.powerpoint;

import info.pppc.base.system.InvocationException;

/**
 * This interface is used by components that can convert microsoft 
 * powerpoint presentations to images. The general usage would be
 * to load a powerpoint presentation, to retrieve the number of
 * images and to convert them one by one in the desired resolution. 
 * 
 * @author Mac
 */
public interface IConverter {
	
	/**
	 * The checkpoint attribute that stores the file as sequence
	 * of bytes. If none is open, the file is null.
	 */
	public static final String CHECKPOINT_PRESENTATION = "component.powerpoint.presentation";
	
	/**
	 * Opens a presentation. The byte array must contain the 
	 * complete powerpoint presentation as it has been read 
	 * from the file system. If there is already an open 
	 * presentation, this method will close it before it opens 
	 * the new one.
	 * 
	 * @param file The bytes that are contained in the file.
	 * @throws IllegalArgumentException Thrown if the file does
	 * 	not represent a powerpoint presentation.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void open(byte[] file) throws IllegalArgumentException, InvocationException;

	/**
	 * Determines whether there is a currently opened powerpoint
	 * presentation. If a presentation is open, this method will
	 * return true, otherwise false.
	 * 
	 * @return True if the presentation is opened, false otherwise.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public boolean isOpen() throws InvocationException;

	/**
	 * Closes an opened powerpoint presentation, if there is one.
	 * If there is no opened powerpoint presentation, this method
	 * will throw an exception.
	 * 
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void close() throws InvocationException;	

	
	/**
	 * Returns the number of slides of the currently opened 
	 * powerpoint presentation. If there is no opened powerpoint
	 * presentation, this method will throw an exception.
	 * 
	 * @return The number of slides of the currently opened 
	 * 	powerpoint presentation.
	 * @throws IllegalStateException Thrown if there is no 
	 * 	currently opened powerpoint presentation.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public int getSlides() throws IllegalStateException, InvocationException;
	
	/**
	 * Returns a specific slide of the currently opened powerpoint
	 * presentation converted into a byte array which represents an
	 * image that has the specified resolution, i.e. the specified 
	 * width and height in pixels. If the resolution is not valid, 
	 * i.e. the width or the height is smaller than or equal to
	 * zero then the method will throw an exception. If there is no 
	 * currently opened presentation, this method will throw an
	 * exception. 
	 * 
	 * @param slide The number of the slide to convert. Valid slide
	 * 	ranges lie between zero and the number of slides - 1.
	 * @param width The width of the resulting image in pixels.
	 * @param height The height of the resulting image in pixels.
	 * @return A byte array that contains an image with the desired
	 * 	resolution.
	 * @throws IllegalArgumentException Thrown if the resolution or
	 * 	the number of the slide is illegal.
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public byte[] getSlide(int slide, int width, int height) 
			throws IllegalArgumentException, IllegalStateException, InvocationException;


}
