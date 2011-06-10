package info.pppc.pcom.component.presenter.lcdui;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.base.lcdui.element.action.CloseAction;
import info.pppc.base.lcdui.form.FormCommandListener;
import info.pppc.base.lcdui.form.FormImageItem;
import info.pppc.base.lcdui.form.FormItem;
import info.pppc.base.lcdui.form.FormItemGroup;
import info.pppc.base.lcdui.form.FormSpacer;
import info.pppc.base.lcdui.form.FormStringItem;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

/**
 * The user interface of the control for the pervasive presenter
 * application.
 * 
 * @author Mac
 */
public class PresenterElement extends AbstractElement implements FormCommandListener {

	/**
	 * Localized string for open buttons.
	 */
	public static final String UI_OPEN = "info.pppc.pcom.component.presenter.lcdui.PresenterElement.OPEN";
	
	/**
	 * Localized string for close buttons.
	 */
	public static final String UI_CLOSE = "info.pppc.pcom.component.presenter.lcdui.PresenterElement.CLOSE";
	
	/**
	 * Localized string for next buttons.
	 */
	public static final String UI_NEXT = "info.pppc.pcom.component.presenter.lcdui.PresenterElement.NEXT";
	
	/**
	 * Localized string for previous buttons.
	 */
	public static final String UI_PREVIOUS = "info.pppc.pcom.component.presenter.lcdui.PresenterElement.PREVIOUS";

	/**
	 * Localized string for select commands
	 */
	public static final String UI_SELECT = "info.pppc.pcom.component.presenter.lcdui.PresenterElement.SELECT";

	/**
	 * Localized string for element title.
	 */
	public static final String UI_TITLE = "info.pppc.pcom.component.presenter.lcdui.PresenterElement.TITLE";


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
	public static final int CONTROL_NEXT = 0;
	
	/**
	 * A constant that is used to reference the prev button.
	 */
	public static final int CONTROL_PREVIOUS = 1;
	
	/**
	 * A constant that is used to reference the open button.
	 */
	public static final int CONTROL_OPEN = 2;
	
	/**
	 * A constant that is used to reference the close button.
	 */
	public static final int CONTROL_CLOSE = 3;

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
	protected ElementAction[] actions;

	/**
	 * The canvas that shows the slide image.
	 */
	protected FormImageItem canvas;

	/**
	 * The next button or null if the widget is not displayed.
	 */
	protected FormItemGroup next;
	
	/**
	 * The previous button or null if the widget is not displayed.
	 */
	protected FormItemGroup previous;

	/**
	 * The width of the image.
	 */
	protected int width;
	
	/**
	 * The height of the image.
	 */
	protected int height;
	
	/**
	 * The form items of the control.
	 */
	protected FormItem[] items;
	
	/**
	 * The select command used for the next and previous button.
	 */
	protected Command selectCommand;
	
	/**
	 * Creates a new control element with the specified manager.
	 * 
	 * @param manager The element manager of the control.
	 */
	public PresenterElement(IElementManager manager) {
		super(manager);
		actions = new ElementAction[] {
			new PresenterAction(PresenterUI.getText(UI_NEXT), 
						listeners, EVENT_NEXT),
			new PresenterAction(PresenterUI.getText(UI_PREVIOUS),
						listeners, EVENT_PREVIOUS),
			new PresenterAction(PresenterUI.getText(UI_OPEN),
					listeners, EVENT_OPEN),
			new PresenterAction(PresenterUI.getText(UI_CLOSE),
					listeners, EVENT_CLOSE),
            new CloseAction(this)
		};
		selectCommand = new Command(PresenterUI.getText(UI_SELECT), Command.ITEM, 1);
		// create the image canvas and init width and height
		width = manager.getDisplayWidth() - 8; 
		height = width / 3 * 2;
		width = (width <= 0)?(10):width;
		height = (height <= 0)?(10):height;
		canvas = new FormImageItem("", PresenterUI.getImage(PresenterUI.IMAGE_LOGO));
		canvas.setSize(width, height);
		canvas.setLayout(FormImageItem.LAYOUT_LINE_AFTER | FormImageItem.LAYOUT_LINE_BEFORE 
			| FormImageItem.LAYOUT_CENTER | FormImageItem.LAYOUT_VCENTER);
		// create the prev and next buttons as group item
		int buttonWidth = manager.getDisplayWidth() / 2 - 10;
		buttonWidth = (buttonWidth < 10)?(10):buttonWidth;
		FormStringItem prevString = new FormStringItem("", PresenterUI.getText(UI_PREVIOUS));
		prevString.setEnabled(true);
		FormImageItem prevImage = new FormImageItem("", PresenterUI.getImage(PresenterUI.IMAGE_PREVIOUS));
		prevImage.setEnabled(true);
		previous = new FormItemGroup("", prevImage, prevString, 
				FormItemGroup.POSITION_HORIZONTAL | FormItemGroup.POSITION_CENTER);
		previous.setLayout(FormItemGroup.LAYOUT_CENTER | FormItemGroup.LAYOUT_VCENTER);
		previous.setSize(buttonWidth, -1);
		previous.addCommand(selectCommand);
		previous.setDefaultCommand(selectCommand);
		previous.setItemCommandListener(this);
		FormStringItem nextString = new FormStringItem("", PresenterUI.getText(UI_NEXT));
		nextString.setEnabled(true);
		FormImageItem nextImage = new FormImageItem("", PresenterUI.getImage(PresenterUI.IMAGE_NEXT));
		nextImage.setEnabled(true);
		next = new FormItemGroup("", nextString, nextImage, 
				FormItemGroup.POSITION_HORIZONTAL | FormItemGroup.POSITION_CENTER);
		next.setLayout(FormItemGroup.LAYOUT_CENTER | FormItemGroup.LAYOUT_VCENTER);
		next.setSize(buttonWidth, -1);
		next.addCommand(selectCommand);
		next.setDefaultCommand(selectCommand);
		next.setItemCommandListener(this);
		FormSpacer buttonSpacer = new FormSpacer(0, 5);
		buttonSpacer.setLayout(FormItem.LAYOUT_CENTER | FormItem.LAYOUT_LINE_AFTER 
			| FormItem.LAYOUT_LINE_BEFORE);
		// disable all actions and buttons
		for (int i = 0; i < actions.length - 1; i++) {
			if (actions[i] != null) {
				actions[i].setEnabled(false);
			}
		}
		previous.setEnabled(false);
		next.setEnabled(false);
		items = new FormItem[] { canvas, buttonSpacer, previous, next };
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
	 * Called whenever the control is disposed due to user
	 * request.
	 */
	public void dispose() {
		super.dispose();
		slide = null;
		next = null;
		previous = null;
		canvas = null;
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
	public ElementAction[] getActions() {
		Vector result = new Vector();
		for (int i = 0; i < 4; i++) {
			if (isEnabled(i)) {
				result.addElement(actions[i]);
			}
		}
		result.addElement(actions[4]);
		ElementAction[] acts = new ElementAction[result.size()];
		for (int i = acts.length - 1; i >= 0; i--) {
			acts[i] = (ElementAction)result.elementAt(i);
		}
		return acts;
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
		getManager().run(new Runnable() {
			public void run() {
				slide = image;
				canvas.setImage(slide);
				canvas.repaint();
			}
		});
	}
	
	/**
	 * Returns the height of the widget part that presents 
	 * 	the slide.
	 * 
	 * @return The height of the widget part that presents 
	 * 	the slide.
	 */
	public int getSlideHeight() {
		return height;
	}

	/**
	 * Returns the width of the widget part that presents 
	 * 	the slide.
	 * 
	 * @return The width of the widget part that presents 
	 * 	the slide.
	 */	
	public int getSlideWidth() {
		return width;
	}
	
	/**
	 * Sets the enabled state of a certain control. The control is
	 * referenced via a CONTROL constant.
	 * 
	 * @param control The control whose state should be changed.
	 * @param enabled The enabled state of the control.
	 */
	public void setEnabled(final int control, final boolean enabled) {
		getManager().run(new Runnable() {
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
					default:
						// do nothing
				}
				getManager().updateElement(PresenterElement.this);	
			}
		});
	}
	
	/**
	 * Retrieves the enabled state of a certain control. The control
	 * is referenced via a CONTROL constant.
	 * 
	 * @param control The control whose state should be retrieved.
	 * @return True if the control is enabled, false otherwise.
	 */
	public boolean isEnabled(int control) {
		switch (control) {
			case CONTROL_CLOSE:
				return actions[1].isEnabled();
			case CONTROL_NEXT:
				return actions[3].isEnabled();
			case CONTROL_OPEN:
				return actions[0].isEnabled();
			case CONTROL_PREVIOUS:
				return actions[4].isEnabled();
			default:
				return false;
		}
	}
	
	/**
	 * Returns the items on the form.
	 * 
	 * @return The items on the form.
	 */
	public FormItem[] getItems() {
		return items;
	}
	
	/**
	 * Called whenever one of the buttons has been pressed.
	 * 
	 * @param command The command that has been executed.
	 * @param item The item that received the command.
	 */
	public void commandAction(Command command, FormItem item) {
		if (command == selectCommand) {
			if (item == next) {
				listeners.fireEvent(EVENT_NEXT);
			} else if (item == previous) {
				listeners.fireEvent(EVENT_PREVIOUS);
			}
		}
	}
	
}
