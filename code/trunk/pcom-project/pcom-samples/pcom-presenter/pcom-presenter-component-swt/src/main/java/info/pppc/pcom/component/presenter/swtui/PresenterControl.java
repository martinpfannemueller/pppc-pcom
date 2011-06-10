package info.pppc.pcom.component.presenter.swtui;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.element.action.RemoveAction;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The user interface of the control for the pervasive presenter
 * application.
 * 
 * @author Mac
 */
public class PresenterControl extends AbstractElementControl {

	/**
	 * Localized string for open buttons.
	 */
	public static final String UI_OPEN = "info.pppc.pcom.component.presenter.swtui.PresenterControl.OPEN";
	
	/**
	 * Localized string for close buttons.
	 */
	public static final String UI_CLOSE = "info.pppc.pcom.component.presenter.swtui.PresenterControl.CLOSE";
	
	/**
	 * Localized string for next buttons.
	 */
	public static final String UI_NEXT = "info.pppc.pcom.component.presenter.swtui.PresenterControl.NEXT";
	
	/**
	 * Localized string for previous buttons.
	 */
	public static final String UI_PREVIOUS = "info.pppc.pcom.component.presenter.swtui.PresenterControl.PREVIOUS";
	
	/**
	 * Localized string for element title.
	 */
	public static final String UI_TITLE = "info.pppc.pcom.component.presenter.swtui.PresenterControl.TITLE";


	/**
	 * The event that signals that the control has been disposed.
	 * The data object of the event will be null, the source 
	 * object will be this control.
	 */
	public static final int EVENT_DISPOSED = 1;

	/**
	 * The event that signals that the next button has been pressed.
	 * The user object will be null.
	 */
	public static final int EVENT_NEXT = 2;
	
	/**
	 * The event that signals that the prev button has been pressed.
	 * The user object will be null.
	 */
	public static final int EVENT_PREVIOUS = 4;
	
	/**
	 * The event that signals that the open button has been pressed.
	 * The user object will be null.
	 */
	public static final int EVENT_OPEN = 8;
	
	/**
	 * The event that signals that the close button has been pressed.
	 * The user object will be null.
	 */
	public static final int EVENT_CLOSE = 16;
	
	/**
	 * The event that signals that the control has been resized.
	 * The user object will be null.
	 */
	public static final int EVENT_RESIZE = 32;	

	/**
	 * A constant that is used to reference the next button.
	 */
	public static final int CONTROL_NEXT = 1;
	
	/**
	 * A constant that is used to reference the prev button.
	 */
	public static final int CONTROL_PREVIOUS = 2;
	
	/**
	 * A constant that is used to reference the open button.
	 */
	public static final int CONTROL_OPEN = 3;
	
	/**
	 * A constant that is used to reference the close button.
	 */
	public static final int CONTROL_CLOSE = 4;

	/**
	 * The event listeners that listen for events posted by the
	 * gui.
	 */
	protected ListenerBundle listeners = new ListenerBundle(this);

	/**
	 * The image that is displayed by the control.
	 */
	protected Image slide;

	/**
	 * The actions that are available from the menu.
	 */
	protected Action[] actions;

	/**
	 * The canvas that shows the slide image.
	 */
	protected Canvas canvas;

	/**
	 * The next button or null if the widget is not displayed.
	 */
	protected Button next;
	
	/**
	 * The previous button or null if the widget is not displayed.
	 */
	protected Button previous;

	/**
	 * The menu manager that updates the menu on the canvas.
	 */
	protected IMenuManager menu;

	/**
	 * Creates a new control element with the specified manager.
	 * 
	 * @param manager The element manager of the control.
	 */
	public PresenterControl(IElementManager manager) {
		super(manager);
		actions = new Action[] {
			new PresenterAction(PresenterUI.getText(UI_OPEN),
				PresenterUI.getDescriptor(PresenterUI.IMAGE_OPEN),
					listeners, EVENT_OPEN),
			new PresenterAction(PresenterUI.getText(UI_CLOSE),
				PresenterUI.getDescriptor(PresenterUI.IMAGE_CLOSE),
					listeners, EVENT_CLOSE),
			null,
			new PresenterAction(PresenterUI.getText(UI_NEXT), 
				PresenterUI.getDescriptor(PresenterUI.IMAGE_NEXT),
					listeners, EVENT_NEXT),
			new PresenterAction(PresenterUI.getText(UI_PREVIOUS),
				PresenterUI.getDescriptor(PresenterUI.IMAGE_PREVIOUS),
					listeners, EVENT_PREVIOUS),
			null, // separator
			new RemoveAction(this, manager)
		};
	}

	/**
	 * Returns the name of the control.
	 * 
	 * @return The name of the control.
	 */
	public String getName() {
		return PresenterUI.getText(UI_TITLE);
	}

	/**
	 * Returns the image of the control.
	 * 
	 * @return The image of the control.
	 */
	public Image getImage() {
		return PresenterUI.getImage(PresenterUI.IMAGE_PRESENTER);
	}

	/**
	 * Called by the gui whenever the control should be shown.
	 * 
	 * @param parent The parent of the control.
	 */
	public void showControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, true));
		// create the canvas
		canvas = new Canvas(control, SWT.BORDER);
		canvas.addListener (SWT.Paint, new Listener () {
			public void handleEvent (Event e) {
				GC gc = e.gc;
				Image draw = slide;
				if (draw != null) {
					Rectangle d = draw.getBounds ();
					Rectangle c = canvas.getClientArea ();
					gc.drawImage (draw, 0, 0, d.width, d.height, 0, 0, c.width, c.height);						
				}
			}
		});
		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				listeners.fireEvent(EVENT_RESIZE);
			}
		});
		GridData canvasData = new GridData(SWT.FILL, SWT.FILL, true, true);
		canvasData.horizontalSpan = 2;
		canvas.setLayoutData(canvasData);
		// create menu
		MenuManager m = new MenuManager();
		for (int i = 0; i < actions.length; i++) {
			if (actions[i] != null) {
				m.add(actions[i]);
			} else {
				m.add(new Separator());
			}
		}
		m.update(true);
		canvas.setMenu(m.createContextMenu(canvas));
		menu = m;
		// create the previous button
		previous = new Button(control, SWT.FLAT);
		previous.setText(PresenterUI.getText(UI_PREVIOUS));
		previous.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) { }
			public void widgetSelected(SelectionEvent arg0) {
				listeners.fireEvent(EVENT_PREVIOUS);
			}
		});
		GridData previousData = new GridData(SWT.FILL, SWT.FILL, false, false);
		previous.setLayoutData(previousData);		
		// create the next button
		next = new Button(control, SWT.FLAT);
		next.setText(PresenterUI.getText(UI_NEXT));
		next.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) { }
			public void widgetSelected(SelectionEvent arg0) {
				listeners.fireEvent(EVENT_NEXT);
			}
		});
		GridData nextData = new GridData(SWT.FILL, SWT.FILL, false, false);
		next.setLayoutData(nextData);		
		setControl(control);
		// disable all actions and buttons
		for (int i = 0; i < actions.length - 1; i++) {
			if (actions[i] != null) {
				actions[i].setEnabled(false);
			}
		}
		previous.setEnabled(false);
		next.setEnabled(false);
	}

	/**
	 * Called whenever the control is disposed due to user
	 * request.
	 */
	public void disposeControl() {
		super.disposeControl();
		slide = null;
		next = null;
		previous = null;
		canvas = null;
		menu = null;
		listeners.fireEvent(EVENT_DISPOSED);
	}

	/**
	 * Adds the specified listener to the set of registered listeners.
	 * At the present time the only event supported is EVENT_DISPOSED.
	 * 
	 * @param type The type of event to register for.
	 * @param listener The listener to register.
	 */
	public void addControlListener(int type, IListener listener) {
		listeners.addListener(type, listener);
	}
	
	/**
	 * Removes the specified listener for events of the specified 
	 * type.
	 * 
	 * @param type The type of events to unregister for.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been unregistered, false +
	 * 	otherwise.
	 */
	public boolean removeControlListener(int type, IListener listener) {
		return listeners.removeListener(type, listener);
	}
	
	/**
	 * Returns the menu actions that are available for this control.
	 * The available menu actions are the remove action and the
	 * open and close actions that are enabled and disabled depending
	 * on the state of the presentation.
	 * 
	 * @return The actions for the view.
	 */
	public Action[] getMenuActions() {
		return actions;
	}


	/**
	 * Returns the image of the current slide that is displayed by
	 * this control or null if no image is displayed.
	 * 
	 * @return The image that is currently displayed.
	 */
	public Image getSlide() {
		return slide;
	}

	/**
	 * Sets the image that is currently displayed by the control or
	 * null if the control does not display an image.
	 * 
	 * @param image The image that is displayed by the control or
	 * 	null if none is displayed.
	 */
	public void setSlide(final Image image) {
		Display d = getDisplay();
		if (d != null && ! d.isDisposed()) {
			d.syncExec(new Runnable() {
				public void run() {
					if (slide != null) {
						slide.dispose();	
					}
					slide = image;
					canvas.redraw();
				}
			});
		} else {
			if (slide != null) {
				slide.dispose();	
			}
			slide = image;	
		}
		
	}
	
	/**
	 * Returns the height of the widget part that presents 
	 * 	the slide.
	 * 
	 * @return The height of the widget part that presents 
	 * 	the slide.
	 */
	public int getSlideHeight() {
		final int[] result = new int[1];
		Display d = getDisplay();
		if (d != null && ! d.isDisposed()) {
			d.syncExec(new Runnable() {
				public void run() {
					Rectangle c = canvas.getClientArea ();
					result[0] = c.height;
				}
			});
		}
		return result[0];		
	}

	/**
	 * Returns the width of the widget part that presents 
	 * 	the slide.
	 * 
	 * @return The width of the widget part that presents 
	 * 	the slide.
	 */	
	public int getSlideWidth() {
		final int[] result = new int[1];
		Display d = getDisplay();
		if (d != null && ! d.isDisposed()) {
			d.syncExec(new Runnable() {
				public void run() {
					Rectangle c = canvas.getClientArea ();
					result[0] = c.width;
				}
			});
		}
		return result[0];
	}
	
	/**
	 * Sets the enabled state of a certain control. The control is
	 * referenced via a CONTROL constant.
	 * 
	 * @param control The control whose state should be changed.
	 * @param enabled The enabled state of the control.
	 */
	public void setEnabled(final int control, final boolean enabled) {
		Display d = getDisplay();
		if (d != null && ! d.isDisposed()) {
			d.syncExec(new Runnable() {
				public void run() {
					switch (control) {
						case CONTROL_CLOSE:
							actions[1].setEnabled(enabled);
							break;
						case CONTROL_NEXT:
							actions[3].setEnabled(enabled);
							if (next != null) {
								next.setEnabled(enabled);
							}
							break;
						case CONTROL_OPEN:
							actions[0].setEnabled(enabled);
							break;
						case CONTROL_PREVIOUS:
							actions[4].setEnabled(enabled);
							if (previous != null) {
								previous.setEnabled(enabled);
							}
							break;
					}
					if (menu != null) {
						menu.update(true);
					}
					
				}
			});
		} 
		getManager().updateElement();	
	}
	
	/**
	 * Retrieves the enabled state of a certain control. The control
	 * is referenced via a CONTROL constant.
	 * 
	 * @param control The control whose state should be retrieved.
	 * @return True if the control is enabled, false otherwise.
	 */
	public boolean isEnabled(int control) {
		Display d = getDisplay();
		if (d != null && ! d.isDisposed()) {
			switch (control) {
				case CONTROL_CLOSE:
					return actions[1].isEnabled();
				case CONTROL_NEXT:
					return actions[3].isEnabled();
				case CONTROL_OPEN:
					return actions[0].isEnabled();
				case CONTROL_PREVIOUS:
					return actions[4].isEnabled();
			}			
		}
		return false;
	}
	
}
