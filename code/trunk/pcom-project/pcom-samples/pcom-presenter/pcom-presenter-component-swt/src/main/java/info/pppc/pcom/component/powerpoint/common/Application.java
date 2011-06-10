package info.pppc.pcom.component.powerpoint.common;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.utility.LibraryLoader;
 
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * Wrapper class for powerpoint.
 * 
 * @author Mac
 */
public class Application {
	
	/**
	 * The dispatch object.
	 */
	Dispatch dispatch;
	
	/**
	 * Creates a new powerpoint instance.
	 */
	public Application() {
		ActiveXComponent xl = new ActiveXComponent("Powerpoint.Application");
		dispatch = xl.getObject();
		if (dispatch == null)
			Logging.log(getClass(), "Dispatch is null.");
	}

	/**
	 * Sets the visibility flag of the application.
	 * 
	 * @param bool 1 to set visible, 0 to set invisible.
	 */
	public void set_Visible(int bool) { 
		Dispatch.put(dispatch, 0x7ee, new Variant(bool));
	}
	
	/**
	 * Returns the presentations object.
	 * 
	 * @return The presentations.
	 */
	public Presentations get_Presentations() { 
		return new Presentations(Dispatch.invoke(dispatch, 0x7d1, Dispatch.Get, new Object[0], new int[1]).toDispatch());
	}
	
	/**
	 * Closes the application.
	 */
	public void Quit() { 
		Dispatch.invokeSub(dispatch, 0x7e5, Dispatch.Method, new Object[0], new int[1]);
	}
	
	/**
	 * Sets the window state of the application.
	 * 
	 * @param PpWindowState One of the constants defined in
	 * 	ppwindowstate.
	 */
	public void set_WindowState(int PpWindowState) {
		Dispatch.put(dispatch, 0x7ed, new Variant(PpWindowState));
	}
	
	public static void main(String[] args) throws Throwable{
		
		String library;
		switch (LibraryLoader.getOS()) { 
		case LibraryLoader.OS_WINDOWS_X64:
			library = "jacob-1.15-M4-x64";
			break;
		case LibraryLoader.OS_WINDOWS_X86:
			library = "jacob-1.15-M4-x86";
			break;
			default:
				library = null;
		}
		if (library != null) {
			LibraryLoader.load(library, false);
		}
		
		Application a = new Application();
		System.out.println("here");
		a.set_Visible(1);
		System.out.println("here");
		Presentations ps = a.get_Presentations();
		System.out.println("here");
		Presentation p = ps.Open("c:\\webda.pptx");
		System.out.println("here");
		Slides s = p.get_Slides();
		System.out.println("Slides: " + s.get_Count());
		SlideShowSettings set = p.get_SlideShowSettings();
		SlideShowWindow w = set.Run();
		SlideShowView v  = w.get_View();
		v.Next();
		v.Previous();
		v.Last();
		v.First();
		v.GotoSlide(3);
		p.Close();
		a.Quit();
	}
	
}
