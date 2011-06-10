package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * Stub object for a slides collection.
 * 
 * @author Mac
 */
public class Slides {

	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;

	/**
	 * Creates a slide from the dispatch object.
	 * 
	 * @param dispatch The dispatch object.
	 */
	public Slides(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch;
	}

	/**
	 * Returns the number of slides.
	 * 
	 * @return The number of slides.
	 */
	public int get_Count() {
		return Dispatch.get(dispatch, 0xb).getInt();
	}

	/**
	 * Returns the slide with the specified index.
	 * 
	 * @param index The index of the slide (note that
	 * 	in ppt the first slide is 1).
	 * @return The slide associated with the index.
	 */
	public Slide Item(Integer index) {
		return new Slide(Dispatch.invoke(dispatch,  0x0, Dispatch.Method, new Object[] { index }, new int[1]).toDispatch());
	}
}
