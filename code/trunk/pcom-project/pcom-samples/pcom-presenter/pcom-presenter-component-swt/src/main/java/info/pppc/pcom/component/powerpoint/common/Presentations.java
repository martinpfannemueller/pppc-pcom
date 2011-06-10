package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;

import com.jacob.com.Dispatch;

/**
 * Stub object for the presentations collection.
 * 
 * @author Mac
 */
public class Presentations {

	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;
	
	/**
	 * Creates a new presentations collection from the
	 * dispatch object.
	 * 
	 * @param dispatch The dispatch object pointing to
	 * 	the presentations.
	 */
	public Presentations(Dispatch dispatch) {
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
		this.dispatch = dispatch;
	}
	
	/**
	 * Opens a new presentation.
	 * 
	 * @param filename The file name to open.
	 * @return The presentation object bound.
	 */
	public Presentation Open(String filename) { 
		return new Presentation(
				Dispatch.invoke(dispatch, 0x7d5, Dispatch.Method, new Object[] { filename }, new int[3]).toDispatch()			
		);
	}

}
