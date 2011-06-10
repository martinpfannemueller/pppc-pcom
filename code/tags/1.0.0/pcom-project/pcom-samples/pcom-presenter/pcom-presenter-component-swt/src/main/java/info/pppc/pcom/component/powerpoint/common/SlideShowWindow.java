package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * Stub object for the slide show window.
 * 
 * @author Mac
 */
public class SlideShowWindow {

	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;

	/**
	 * Creates a new slide show window from
	 * the dispatch object.
	 * 
	 * @param dispatch The dispatch object
	 * 	pointing to the window.
	 */
	public SlideShowWindow(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch; 
	}
	
	/**
	 * Returns the view of the slide show
	 * window.
	 * 
	 * @return The view of the window.
	 */
	public SlideShowView get_View() {
		return new SlideShowView(Dispatch.get(dispatch, 0x7d3).toDispatch());
	}
	
}
