package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * Stub object for a single powerpoint slide.
 * 
 * @author Mac
 */
public class Slide {

	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;

	/**
	 * Creates a new slide from the dispatch object.
	 * 
	 * @param dispatch The dispatch object of the slide.
	 */
	public Slide(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch;
	}
	
	/**
	 * Exports the slide at the specified location with the specified extension,
	 * width and height.
	 * 
	 * @param name The name of the file.
	 * @param extension The extension of the file.
	 * @param width The width of the file.
	 * @param height The height of the file.
	 */
	public void Export(String name, String extension, int width, int height) {
		Dispatch.invokeSub(dispatch, 0x7e9, Dispatch.Method, new Object[] { name, extension, new Integer(width), new Integer(height)}, new int[1]);
	}
}
