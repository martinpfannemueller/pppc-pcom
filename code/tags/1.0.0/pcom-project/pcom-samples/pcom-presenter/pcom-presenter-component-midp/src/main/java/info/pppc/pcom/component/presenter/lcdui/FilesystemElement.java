package info.pppc.pcom.component.presenter.lcdui;

import javax.microedition.lcdui.Image;

import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.base.lcdui.form.FormItem;
import info.pppc.base.lcdui.form.FormStringItem;
import info.pppc.base.lcdui.tree.TreeItem;
import info.pppc.base.lcdui.tree.TreeNode;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

/**
 * The control file dialog lets a user select files from a tree
 * viewer. A selection change listener can be added in order to
 * receive selection events. The contents of the tree viewer can
 * be set with a content and a label provider.
 * 
 * @author Mac
 */
public class FilesystemElement extends AbstractElement {

	/**
	 * Localized text for the help label.
	 */
	public static final String UI_HELP = "info.pppc.pcom.component.presenter.lcdui.FilesystemElement.HELP";

	/**
	 * Localized text for the dialog title.
	 */
	public static final String UI_TITLE = "info.pppc.pcom.component.presenter.lcdui.FilesystemElement.TITLE";

	/**
	 * This event is used to notify its receiver of a changed 
	 * selection. The user object will be either null to indicate
	 * that there is none or it will be the selected item of 
	 * the tree viewer.
	 */
	public static final int EVENT_SELECTION = 1;
	
	/**
	 * The event that is fired whenever the ok button is pressed.
	 */
	public static final int EVENT_OK = 2;
	
	/**
	 * The event that is fired whenever the cancel button is
	 * pressed.
	 */
	public static final int EVENT_CANCEL = 4;

	/**
	 * The listeners registered for events.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);

	/**
	 * The last selected object of the dialog.
	 */
	private TreeNode selection = null;

	/**
	 * A flag that determines whether the ok button
	 * is enabled.
	 */
	private boolean enabled = false;

	/**
	 * The tree item that will eventually contain the
	 * files and folders to select.
	 */
	private TreeItem tree;
	
	/**
	 * The message item that shows the user whenever an
	 * item cannot be selected.
	 */
	private FormStringItem msg;
	
	/**
	 * The actions of the element.
	 */
	private ElementAction[] actions;
	
	/**
	 * The items of the element.
	 */
	private FormItem[] items;
	
	/**
	 * Createes a new control file dialog using the
	 * specified manager.
	 * 
	 * @param manager The manager of the dialog.
	 * @param cp The content provider of the dialog.
	 * @param lp The label provider of the dialog.
	 */
	public FilesystemElement(IElementManager manager, 
			FilesystemContentProvider cp, FilesystemLabelProvider lp) {
		super(manager);
		tree = new TreeItem(manager, "", "Select");
		tree.addSelectionListener(Event.EVENT_EVERYTHING, new IListener() {
			public void handleEvent(Event event) {
				switch (event.getType()) {
				case TreeItem.EVENT_SELECTION_CHANGED:
					selection = (TreeNode)event.getData();
					listeners.fireEvent(EVENT_SELECTION, selection.getData());
					break;
				case TreeItem.EVENT_SELECTION_FIRED:
					if (enabled) {
						listeners.fireEvent(EVENT_OK);
					}
					break;
				default:
					// will never happen
				}
			}
		});
		FormStringItem help = new FormStringItem("", PresenterUI.getText(UI_HELP));
		help.setLayout(FormItem.LAYOUT_EXPAND | FormItem.LAYOUT_LINE_AFTER 
			| FormItem.LAYOUT_LINE_BEFORE);
		msg = new FormStringItem("", "");
		msg.setLayout(FormItem.LAYOUT_EXPAND | FormItem.LAYOUT_LINE_AFTER 
				| FormItem.LAYOUT_LINE_BEFORE);
		items = new FormItem[] { help, tree, msg };
		actions = new ElementAction[] { new ElementAction("Cancel") {
			public void run() {
				listeners.fireEvent(EVENT_CANCEL);
			}
		}};
		tree.setContent(new FilesystemAdapter(cp.getRoot(), cp, lp));
		tree.setVisible(false);
	}

	/**
	 * Returns the image of the element as shown in the ui.
	 * 
	 * @return The image of the element.
	 */
	public Image getImage() {
		return PresenterUI.getImage(PresenterUI.IMAGE_PRESENTER);
	}
	
	/**
	 * Returns the name of the element as shown in the ui.
	 * 
	 * @return The name of the element.
	 */
	public String getName() {
		return PresenterUI.getText(UI_TITLE);
	}

	/**
	 * Returns the items of the element as shown in the
	 * ui.
	 * 
	 * @return The items of the element as shown in the ui.
	 */
	public FormItem[] getItems() {
		return items;
	}
	
	/**
	 * Returns the actions of the element.
	 * 
	 * @return Returns the actions of the element.
	 */
	public ElementAction[] getActions() {
		return actions;
	}
	
	/**
	 * Adds the specified listener to the set of registered listeners.
	 * At the present time the only event type supported is EVENT_SELECTION.
	 * 
	 * @param type The event type to register for.
	 * @param listener The listener to register.
	 */
	public void addControlListener(int type, IListener listener) {
		listeners.addListener(type, listener);
	}
	
	/**
	 * Removes the specified listener from the reception of the specified
	 * event types.
	 * 
	 * @param type The event types to unregister.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been removed, false otherwise.
	 */
	public boolean removeControlListener(int type, IListener listener) {
		return listeners.removeListener(type, listener);
	}
	
	/**
	 * Returns the item that is currently selected.
	 * 
	 * @return The item that is selected.
	 */
	public Object getSelection() {
		return selection.getData();
	}
	
	/**
	 * A flag that indicates whether the dialogs ok button is
	 * enabled.
	 *
	 * @return True if the dialog's ok button is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets a flag to determine whether the ok button of the
	 * dialog is enabled.
	 * 
	 * @param enabled True to enable the dialog's ok button,
	 * 	false to disable.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (selection != null) {
			selection.setSelectable(true);
		}
	}

}
