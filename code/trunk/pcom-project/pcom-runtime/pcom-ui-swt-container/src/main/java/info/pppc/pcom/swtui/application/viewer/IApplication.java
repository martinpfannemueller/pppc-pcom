package info.pppc.pcom.swtui.application.viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

/**
 * The application interface must be implemented by applications
 * that are shown in the application viewer.
 * 
 * @author Mac
 */
public interface IApplication {

	/**
	 * The state indicator that signals that the application
	 * is currently executed.
	 */
	public static final int STATE_STARTED = 1;
	
	/**
	 * The state indicator that signals that the application
	 * is currently paused or assembling.
	 */
	public static final int STATE_PAUSED = 3;
	
	/**
	 * The state indicator that signals that the application
	 * is currently stopped.
	 */
	public static final int STATE_STOPPED = 2;
	
	/**
	 * Returns the image of the application.
	 * 
	 * @return The image of the application.
	 */
	public Image getImage();
	
	/**
	 * Returns the name of the application.
	 * 
	 * @return The name of the application.
	 */
	public String getName();
	
	/**
	 * Returns the state of the application. This
	 * must be one of the state constants.
	 * 
	 * @return The state of the application.
	 */
	public int getState();
	
	/**
	 * Returns the current preference level that is
	 * executed.
	 * 
	 * @return The current preference level that is
	 * 	executed.
	 */
	public int getLevel();
	
	/**
	 * Returns the menu actions for the application.
	 * 
	 * @return The menu actions for the application.
	 */
	public Action[] getMenuActions();
}
