package info.pppc.pcom.swtui.application.viewer;

import java.util.Vector;

import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The application viewer is a custom widget that displays a number of
 * applications.
 * 
 * @author Mac
 */
public class ApplicationViewer {

	/**
	 * Called whenever an application is selected. The source object
	 * of the event will be the control. The data object will be the
	 * selected application or null if all applications have been
	 * deselected.
	 */
	public static final int EVENT_SELECTED = 1;
	
	/**
	 * The composite that displays the content.
	 */
	protected Composite contentComposite;
	
	/**
	 * The scrolled composite that contains the content pane.
	 */
	protected ScrolledComposite scrolledComposite;
	
	/**
	 * The listener bundle that contains the listeners of the
	 * viewer.
	 */
	protected ListenerBundle listeners = new ListenerBundle(this);
	
	/**
	 * The applications that are currently displayed in the order
	 * in which they are displayed. The vector contains the
	 * application images of the applications.
	 */
	protected Vector images = new Vector();
	
	/**
	 * The selection listener that listens for focus changes.
	 */
	protected Listener selector = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.FocusOut:
				listeners.fireEvent(EVENT_SELECTED, null);
				break;
			case SWT.FocusIn:
				listeners.fireEvent(EVENT_SELECTED, getSelection());
				break;
			default:
				// will never happen
			}
		};
	};
	
	/**
	 * Creates a new application viewer on the specified parent.
	 * 
	 * @param parent The parent of the composite.
	 * @param style The style of the viewer. Possible styles
	 * 	corrspond to the styles of swt composite widgets.
	 */
	public ApplicationViewer(final Composite parent, int style) {
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		contentComposite = new Composite(scrolledComposite, SWT.NONE);
		contentComposite.setBackground(contentComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		scrolledComposite.setContent(contentComposite);
		scrolledComposite.setBackground(contentComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Listener updater = new Listener() {
			public void handleEvent(Event event) {
				recompute();
			}
		};
		parent.addListener(SWT.Resize, updater);
		scrolledComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				parent.removeListener(SWT.Resize, updater);
			}
		});
		recompute();
	}
	
	/**
	 * Returns the control of the application viewer.
	 * 
	 * @return The control of the viewer.
	 */
	public Control getControl() {
		return scrolledComposite;
	}
	
	/**
	 * Adds the specified listener to the application viewer
	 * for the specified types of events. Possible events are
	 * described by the event constants defined in this class.
	 * 
	 * @param types The types of events to register for. Multiple
	 * 	types can be or-ed together.
	 * @param listener The listener to register.
	 */
	public void addListener(int types, IListener listener) {
		listeners.addListener(types, listener);
	}
	
	/**
	 * Removes the specified listener from the application
	 * viewer for the specified set of events.
	 * 
	 * @param types The types of event to unregister.
	 * @param listener The listener to register.
	 * @return True if the listener has been unregistered, false
	 * 	otherwise.
	 */
	public boolean removeListener(int types, IListener listener) {
		return listeners.removeListener(types, listener);
	}

	/**
	 * Sets the applications that should be visualized by the
	 * viewer.
	 * 
	 * @param applications The applications that should be 
	 * 	visualized by the viewer.
	 */
	public void setApplications(IApplication[] applications) {
		for (int i = images.size() - 1; i >= 0 ; i--) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			image.removeListener(SWT.FocusIn, selector);
			image.removeListener(SWT.FocusOut, selector);
			image.dispose();
			images.removeElementAt(i);
		}
		for (int i = 0; i < applications.length; i++) {
			ApplicationImage image = new ApplicationImage(contentComposite, SWT.NONE);
			image.addListener(SWT.FocusIn, selector);
			image.addListener(SWT.FocusOut, selector);
			image.setApplication(applications[i]);
			images.addElement(image);
			updateImageLayout(image);
		}
		recompute();
	}
	
	/**
	 * Returns the applications that are currently visualized.
	 * 
	 * @return The applications that are currently visualized.
	 */
	public IApplication[] getApplications() {
		IApplication[] applications = new IApplication[images.size()];
		for (int i = 0; i < images.size(); i++) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			applications[i] = image.getApplication();
		}
		return applications;
	}
	
	/**
	 * Adds the specified application to the set of items.
	 * 
	 * @param application The application to add.
	 */
	public void addApplication(IApplication application) {
		ApplicationImage image = new ApplicationImage(contentComposite, SWT.NONE);
		image.addListener(SWT.FocusIn, selector);
		image.addListener(SWT.FocusOut, selector);
		image.setApplication(application);
		images.addElement(image);
		updateImageLayout(image);
		recompute();
	}
	
	/**
	 * Removes the specified application from the set of items.
	 * 
	 * @param application The application to remove.
	 */
	public void removeApplication(IApplication application) {
		for (int i = 0; i < images.size(); i++) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			if (image.getApplication().equals(application)) {
				image.removeListener(SWT.FocusIn, selector);
				image.removeListener(SWT.FocusOut, selector);
				image.dispose();
				images.removeElementAt(i);
				return;
			}
		}
	}

	/**
	 * Selects the application at the specified index.
	 * 
	 * @param index The index of the application.
	 */
	public void select(int index) {
		if (index < 0 || index >= images.size()) {
			return;
		} else {
			ApplicationImage image = (ApplicationImage) images.elementAt(index);
			image.setFocus();
		}
	}
	
	/**
	 * Returns the index of the current selection. If no
	 * application is selected, this method returns -1.
	 * 
	 * @return The index of the current selection.
	 */
	public int getSelectionIndex() {
		for (int i = 0; i < images.size(); i++) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			if (image.isFocusControl()) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the application that is currently selected
	 * or null if none is selected.
	 * 
	 * @return The application that is currently selected.
	 */
	public IApplication getSelection() {
		for (int i = 0; i < images.size(); i++) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			if (image.isFocusControl()) {
				return image.getApplication();
			}
		}
		return null;
	}
	
	/**
	 * Updates the state of the application according to
	 * the current data.
	 * 
	 * @param application The application to update.
	 */
	public void update(IApplication application) {
		for (int i = 0; i < images.size(); i++) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			if (image.getApplication().equals(application)) {
				image.setApplication(application);
				updateImageLayout(image);
				recompute();
				return;
			}
		}
		
	}
	
	/**
	 * Updates the state of all applications according
	 * to the current data.
	 */
	public void update() {
		for (int i = 0; i < images.size(); i++) {
			ApplicationImage image = (ApplicationImage)images.elementAt(i);
			image.setApplication(image.getApplication());
			updateImageLayout(image);
		}
		recompute();
	}
	
	/**
	 * Updates the layout using the specified application image.
	 * 
	 * @param image The image to update the layout.
	 */
	private void updateImageLayout(ApplicationImage image) {
		Point p = image.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int height = p.y / ApplicationImage.CONTROL_HEIGHT + 1;
		image.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false, 1, height));
	}
	
	/**
	 * Adjusts the size of the text viewer in such a way that the scroll
	 * pane is always filled. This method is called upon every resize of
	 * the scroll pane and upon every text change of the document that
	 * is currently displayed by the text viewer.
	 */
	private void recompute() {
		Display d = scrolledComposite.getDisplay();
		if (d != null) {
			d.asyncExec(new Runnable() {
				public void run() {
					if (scrolledComposite != null && contentComposite != null) {
						Rectangle r = scrolledComposite.getClientArea();
						contentComposite.setLayout(new GridLayout(Math.max(r.width / ApplicationImage.CONTROL_WIDTH, 3), false));
						Point p1 = contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						Point p2 = new Point(r.width, r.height);
						Point p = new Point(p2.x, p2.y);
						p.x = Math.max(p.x, p1.x);
						p.y = Math.max(p.y, p1.y);
						contentComposite.setSize(p);
						contentComposite.layout(true);
					}						
				}
			});			
		}
	}
	
	
}
