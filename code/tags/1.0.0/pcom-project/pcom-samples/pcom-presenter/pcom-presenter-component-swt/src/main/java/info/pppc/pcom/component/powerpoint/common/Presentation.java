package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * Stub class for powerpoint presentation object.
 * 
 * @author Mac
 */
public class Presentation {
	
	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;
	
	/**
	 * Creates a new presentation from a given dispatch object.
	 * 
	 * @param dispatch The dispatch object.
	 */
	public Presentation(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch;
	}

	/**
	 * Returns the slide show settings.
	 * 
	 * @return The slide show settings.
	 */
	public SlideShowSettings get_SlideShowSettings() {
		return new SlideShowSettings(Dispatch.get(dispatch, 0x7df).toDispatch());
	}
	
	/**
	 * Returns the slide collection.
	 * 
	 * @return The slide collection.
	 */
	public Slides get_Slides() {
		return new Slides(Dispatch.get(dispatch, 0x7db).toDispatch());
	}
	
	/**
	 * Closes the presentation.
	 */
	public void Close() {
		Dispatch.invoke(dispatch, 0x7f7, Dispatch.Method, new Object[0], new int[1]);
	}
}
