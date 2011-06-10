package info.pppc.pcom.component.portrayer;

import info.pppc.base.system.InvocationException;

/**
 * The interface of a component that can display pictures using the 
 * full screen.
 * 
 * @author Mac
 */
public interface IPortrayer {
	
	/**
	 * A flag that indicates whether a picture is visible.
	 */
	public static final String CHECKPOINT_VISIBLE = "component.portrayer.visible";
	
	/**
	 * The byte data of the picture, if one is visible.
	 */
	public static final String CHECKPOINT_PICTURE = "component.portrayer.picture";

	/**
	 * Displays the specified picture. The bytes passed to the method must 
	 * represent a picture (e.g. a gif or jpeg image).
	 * 
	 * @param picture The bytes of the picture to display.
	 * @throws InvocationException Thrown if the call cannot be delivered.
	 */
	public void showPicture(byte[] picture) throws InvocationException;

	/**
	 * Hides any picture that might be currently displayed.
	 * 
	 * @throws InvocationException Thrown if the call cannot be delivered.
	 */
	public void hidePicture() throws InvocationException;
	
	/**
	 * Determines whether any image is displayed at the moment.
	 * 
	 * @return True if a picture is visible, false otherwise.
	 * @throws InvocationException Thrown if the call cannot be delivered.
	 */
	public boolean isVisible() throws InvocationException;

}
