package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * A stub object for the slide show settings.
 * 
 * @author Mac
 */
public class SlideShowSettings {

	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;
	
	/**
	 * Creates a new slide show settings from
	 * the dispatch object.
	 * 
	 * @param dispatch The dispatch object pointing
	 * 	to the settings.
	 */
	public SlideShowSettings(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch;
	}
	
	/**
	 * Runs the presentation in presentation mode.
	 * 
	 * @return The window showing the presentation.
	 */
	public SlideShowWindow Run() { 
		return new SlideShowWindow(Dispatch.invoke(dispatch, 0x7d8, Dispatch.Method, new Object[0], new int[1]).toDispatch());
	}
	
}
