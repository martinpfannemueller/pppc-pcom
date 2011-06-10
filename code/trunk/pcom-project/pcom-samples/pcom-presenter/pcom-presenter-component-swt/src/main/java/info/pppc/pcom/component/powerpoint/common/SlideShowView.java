package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * Stub object for slide show views.
 * 
 * @author Mac
 */
public class SlideShowView {

	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;

	/**
	 * Creates a new slide show view from the
	 * dispatch object.
	 * 
	 * @param dispatch The dispatch object of
	 * 	the view.
	 */
	public SlideShowView(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch;
	}

	/**
	 * Moves the presentation to the first slide.
	 */
	public void First() {
		Dispatch.invokeSub(dispatch, 0x7e1, Dispatch.Method, new Object[0], new int[0]);
	}

	/**
	 * Moves the presentation to the last slide.
	 */
	public void Last() {
		Dispatch.invokeSub(dispatch, 0x7e2, Dispatch.Method, new Object[0], new int[0]);
	}

	/**
	 * Moves the presentation to the next slide.
	 */
	public void Next() {
		Dispatch.invokeSub(dispatch, 0x7e3, Dispatch.Method, new Object[0], new int[0]);
	}

	/**
	 * Moves the presentation to the previous slide.
	 */
	public void Previous() {
		Dispatch.invokeSub(dispatch, 0x7e4, Dispatch.Method, new Object[0], new int[0]);
	}

	/**
	 * Moves the presentation to the specified slide.
	 * 
	 * @param slide The slide number.
	 */
	public void GotoSlide(int slide) {
		Dispatch.invokeSub(dispatch, 0x7e5, Dispatch.Method, new Object[] { new Integer(slide) }, new int[0]);
	}

	/**
	 * Returns the current slide number.
	 * 
	 * @return The current slide number.
	 */
	public int get_CurrentShowPosition() {
		return Dispatch.get(dispatch, 0x7eb).getInt();
	}

}
