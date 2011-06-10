package info.pppc.pcom.component.powerpoint.common;


import info.pppc.base.system.util.Logging;
import info.pppc.pcom.capability.com.COMException;
import info.pppc.pcom.capability.com.ICOMAccessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Provides common elements for all presenters.
 * 
 * @author Stephan
 */
public class Powerpoint {

	/**
	 * The application of the bridge.
	 */
	protected Application application = null;
	
	/**
	 * The current presentation if there is one.
	 */
	protected Presentation presentation = null;

	/**
	 * The slide show window.
	 */
	protected SlideShowWindow window = null;
	
	/**
	 * The presentation view.
	 */
	protected SlideShowView view = null;

	/**
	 * The accessor used to interface with com.
	 */
	protected ICOMAccessor accessor;
	
	/**
	 * Creates a new powerpoint that uses the specified
	 * accessor to interface with com.
	 * 
	 * @param accessor The accessor used to interface with
	 * 	com in a safe manner.
	 */
	public Powerpoint(ICOMAccessor accessor) {
		if (accessor == null) throw new NullPointerException("Accessor is null.");
		this.accessor = accessor;
	}

	/**
	 * Returns the application used to display presentations.
	 * 
	 * @return The application that displays presentations.
	 */
	protected Application getApplication() {
		if (application == null) {
			application = new Application();
			application.set_Visible(1);
		}
		return application;
	}
	
	/**
	 * Opens a presentation and initializes the local variable.
	 * 
	 * @param file The presentation as byte array.
	 * @param activate True to activate the powerpoint presentation, false
	 * 	otherwise.
	 * @throws IllegalArgumentException Thrown if the call fails.
	 */
	public void open(final byte[] file, final boolean activate) throws IllegalArgumentException {
		System.out.println("open");
		PowerpointCommand command = new PowerpointCommand() {
			public void run() throws Throwable {
				if (isOpen()) close();
				Presentations pres = getApplication().get_Presentations();
				File tfile = File.createTempFile("PRESENTATION", ".pptx");
				FileOutputStream out = new FileOutputStream(tfile);
				out.write(file);					
				out.close();
				presentation = pres.Open(tfile.getAbsolutePath());
				if (activate) {
					activate();
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Open failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalArgumentException("Open failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}

	/**
	 * Activates the slide show and displays the presentation in a new slide show
	 * window and view.
	 * 
	 * @throws IllegalStateException Thrown if the application is not open.
	 */
	public void activate() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() throws Throwable {
				if (isOpen()) {
					window = presentation.get_SlideShowSettings().Run();
					view = window.get_View();
				} else {
					throw new IllegalStateException("Could not activate slide show.");	
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Activate failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Activate failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}

	/**
	 * Determines whether there is an open presentation.
	 * 
	 * @return True if there is an open presentation, false
	 * 	otherwise.
	 */
	public boolean isOpen() {
		return (presentation != null);
	}
	
	/**
	 * Determines whether the presentation is open and the slide show
	 * windows and view are defined.
	 * 
	 * @return True if the presentation is open and the windows and views
	 * 	are ok, otherwise false.
	 */
	public boolean isActivated() {
		if (isOpen()) {
			return (window != null && view != null);
		}
		return false;
	}
	

	/**
	 * Closes any open presentation.
	 */
	public void close() {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() {
				window = null;
				view = null;		
				if (! isOpen()) return;
				try {
					presentation.Close();
					application.set_WindowState(PpWindowState.ppWindowMinimized);							
				} finally {
					presentation = null;	
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Close failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Close failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}

	/**
	 * Returns the number of available slides.
	 * 
	 * @return The number of slides.
	 * @throws IllegalStateException Thrown if the slide number could not be retrieved.
	 */
	public int getSlides() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public Object runResult() throws Throwable  {
				if (! isOpen()) throw new IllegalStateException("Presentation not open.");
				return new Integer(presentation.get_Slides().get_Count());	
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Get slides failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Get slides failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
		return ((Integer)command.getResult()).intValue();
	}

	/**
	 * Returns a specific slide of the currently opened powerpoint
	 * presentation converted into a byte array which represents an
	 * image that has the specified resolution, i.e. the specified 
	 * width and height in pixels. If the resolution is not valid, 
	 * i.e. the width or the height is smaller than or equal to
	 * zero then the method will throw an exception. If there is no 
	 * currently opened presentation, this method will throw an
	 * exception. 
	 * 
	 * @param slide The number of the slide to convert. Valid slide
	 * 	ranges lie between zero and the number of slides - 1.
	 * @param width The width of the resulting image in pixels.
	 * @param height The height of the resulting image in pixels.
	 * @return A byte array that contains an image with the desired
	 * 	resolution.
	 * @throws IllegalArgumentException Thrown if the resolution or
	 * 	the number of the slide is illegal.
	 * @throws IllegalStateException Thrown if there is no currently
	 * 	opened powerpoint presentation.
	 */	
	public byte[] getSlide(final int slide, final int width, final int height) throws IllegalArgumentException, IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public Object runResult() throws Throwable {
				if (! isOpen()) throw new IllegalStateException("Presentation not open.");
				File efile = File.createTempFile("slide", "jpeg");
				presentation.get_Slides().Item(new Integer(slide)).Export(efile.getAbsolutePath(), ".gif", width, height);
				byte[] b = new byte[(int)efile.length()];
				FileInputStream ifs = new FileInputStream(efile);
				int read = ifs.read(b);
				while (read < b.length) {
					read += ifs.read(b, read, b.length - read);
				}
				ifs.close();
				efile.delete();
				return b;
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Export slide failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			if (throwable != null && throwable instanceof IllegalStateException) {
				throw (IllegalStateException)throwable;
			}
			throw new IllegalArgumentException("Export slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
		return (byte[])command.getResult();
	}
		
	/**
	 * Jumps to the last slide of the presentation.
	 * 
	 * @throws IllegalStateException Thrown if the presentation is not open.
	 */
	public void lastSlide() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() {
				if (! isActivated()) throw new IllegalStateException("Presentation not activated.");
				view.Last();
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Last slide failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Last slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}

	/**
	 * Jumps to the next slide of the presentation.
	 * 
	 * @throws IllegalStateException Thrown if the presentation is not open.
	 */
	public void nextSlide() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() {
				if (! isActivated()) throw new IllegalStateException("Presentation not activated.");
				if (presentation.get_Slides().get_Count() > view.get_CurrentShowPosition()) {
					view.Next();	
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Next slide failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Next slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}
	
	/**
	 * Jumps to the previous slide of the presentation.
	 * 
	 * @throws IllegalStateException Thrown if the presentation is not open.
	 */
	public void previousSlide() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() {
				if (! isActivated()) throw new IllegalStateException("Presentation not activated.");
				if (view.get_CurrentShowPosition() != 1) {
					view.Previous();	
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Previous slide failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Previous slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}

	/**
	 * Jumps to the first slide of the presentation.
	 * 
	 * @throws IllegalStateException Thrown if the presentation is not open.
	 */
	public void firstSlide() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() {
				if (! isActivated()) throw new IllegalStateException("Presentation not activated.");
				view.First();
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Previous slide failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Previous slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}
	
	/**
	 * Jumps to the specified slide of the presentation.
	 * 
	 * @param slide The number of the slide to jump to.
	 * @throws IllegalStateException Thrown if the presentation is not open.
	 * @throws IllegalArgumentException Thrown if the argument is malformed.
	 */
	public void setSlide(final int slide) throws IllegalArgumentException, IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() throws Throwable {				
				if (! isActivated()) throw new IllegalStateException("Presentation not activated.");
				if (presentation.get_Slides().get_Count() < slide || slide < 1) {
					throw new IllegalArgumentException("Slide does not exist.");
				} else {
					view.GotoSlide(slide);
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Previous slide failed.", e);
			Throwable throwable = e.getReason();
			if (throwable != null && throwable instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)throwable;
			}
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Previous slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
	}

	/**
	 * Returns the number of the slide that is currently displayed.
	 * 
	 * @return The number of the slide that is displayed.
	 * @throws IllegalStateException Thrown if the presentation is not open.
	 */
	public int getSlide() throws IllegalStateException {
		PowerpointCommand command = new PowerpointCommand() {
			public Object runResult() {
				if (! isActivated()) throw new IllegalStateException("Presentation not activated.");
				return new Integer(view.get_CurrentShowPosition());
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Get slide failed.", e);
			Throwable throwable = e.getReason();
			String cause = throwable==null?"Unknown reason.":throwable.getMessage();
			throw new IllegalStateException("Get slide failed: " + cause);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
			throw new IllegalStateException("Thread got interrupted.");
		}
		return ((Integer)command.getResult()).intValue();
	}

	/**
	 * Shuts down the powerpoint application. After calling this method it is no 
	 * longer safe to call any other method of the powerpoint application.
	 */
	public void quit() {
		PowerpointCommand command = new PowerpointCommand() {
			public void run() throws Throwable {
				try {
					if (presentation != null) {
						presentation.Close();
					}
					if (application != null) {
						application.set_WindowState(PpWindowState.ppWindowMinimized);
						application.Quit();
					
					}

				} finally {
					presentation = null;
					view = null;
					window = null;
					application = null;
				}
			}
		};
		try {
			accessor.runSynchronous(command);	
		} catch (COMException e) {
			Logging.error(getClass(), "Quit failed.", e);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}	
	}
	
}
