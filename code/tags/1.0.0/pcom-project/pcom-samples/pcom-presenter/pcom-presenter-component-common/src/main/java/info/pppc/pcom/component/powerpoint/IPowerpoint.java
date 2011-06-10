package info.pppc.pcom.component.powerpoint;

import info.pppc.base.system.InvocationException;

/**
 * This interface is used by components that can display microsoft 
 * powerpoint presentations. The general usage would be to load a
 * powerpoint presentation, to retrieve the number of slides and
 * to toggle between them until the presentation is closed.
 * 
 * @author Mac
 */
public interface IPowerpoint extends IConverter {
	
	/**
	 * The checkpoint attribute that stores the current slide or
	 * -1 if no slide is currently opened.
	 */
	public static final String CHECKPOINT_SLIDE = "component.powerpoint.slide";
	
	/**
	 * Shows the specified slide. If the slide number is not valid,
	 * this method will throw an exception. If there is no opened
	 * powerpoint presentation, this method will throw an exception.
	 * 
	 * @param slide The number of the slide to show. Valid slide
	 * 	ranges lie between zero and the number of slides - 1.
	 * @throws IllegalArgumentException Thrown if the number of the 
	 * 	slide is illegal.
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation.
	 * @throws InvocationException Thrown by base if the call fails.
	 */
	public void setSlide(int slide)
			throws IllegalArgumentException, IllegalStateException, InvocationException;

	/**
	 * Returns the index of the slide that is displayed. If there is 
	 * no opened powerpoint presentation, this method will throw an 
	 * exception.
	 * 
	 * @return The number of the slide to show. Valid slide
	 * 	ranges lie between zero and the number of slides - 1.
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation.
	 * @throws InvocationException Thrown by base if the call fails.
	 */			
	public int getSlide()
			throws IllegalStateException, InvocationException;

	/**
	 * Shows the next slide. If there is no next slide, this method 
	 * will throw an exception. If there is no opened powerpoint 
	 * presentation, this method will throw an exception.
	 * 
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation or if the end of the powerpoint
	 * 	presentation has been reached.
	 * @throws InvocationException Thrown by base if the call fails.
	 */			
	public void nextSlide()
			throws IllegalStateException, InvocationException;

	/**
	 * Shows the previous slide. If there is no previous slide, this 
	 * method will throw an exception. If there is no opened powerpoint 
	 * presentation, this method will throw an exception.
	 * 
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation or if the start of the powerpoint
	 * 	presentation has been reached.
	 * @throws InvocationException Thrown by base if the call fails.
	 */						
	public void previousSlide()
			throws IllegalStateException, InvocationException;
	
	/**
	 * Shows the first slide. If there is no first slide, this method 
	 * will throw an exception. If there is no opened powerpoint 
	 * presentation, this method will throw an exception.
	 * 
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation.
	 * @throws InvocationException Thrown by base if the call fails.
	 */	
	public void firstSlide()
			throws IllegalStateException, InvocationException;

	/**
	 * Shows the last slide. If there is no last slide, this method 
	 * will throw an exception. If there is no opened powerpoint 
	 * presentation, this method will throw an exception.
	 * 
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation.
     * @throws InvocationException Thrown by base if the call fails.
	 */				
	public void lastSlide() 
			throws IllegalStateException, InvocationException;
}
