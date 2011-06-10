package info.pppc.pcom.lcdui.application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import info.pppc.base.lcdui.form.FormCommandListener;
import info.pppc.base.lcdui.form.FormDisplay;
import info.pppc.base.lcdui.form.FormItem;
import info.pppc.base.lcdui.form.FormStyle;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.lcdui.PcomUI;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.contract.Contract;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * The appliation item is used to visualize a single application descriptor. 
 * It supports actions according to the state described by the descriptor.
 * 
 * @author Mac
 */
public class ApplicationItem extends FormItem implements FormCommandListener {

	/**
	 * The resource key for the start action.
	 */
	private static final String UI_START = "info.pppc.pcom.lcdui.application.ApplicationItem.START";
	
	/**
	 * The resource key for the stop action.
	 */
	private static final String UI_STOP = "info.pppc.pcom.lcdui.application.ApplicationItem.STOP";

	/**
	 * The resource key for the adapt action.
	 */
	private static final String UI_ADAPT = "info.pppc.pcom.lcdui.application.ApplicationItem.ADAPT";

	/**
	 * The resource key for the edit action.
	 */
	private static final String UI_EDIT = "info.pppc.pcom.lcdui.application.ApplicationItem.EDIT";

	/**
	 * The size of the item, this is the width of the control. The
	 * height can be computed by adding the spacing to the icon
	 * size and the number of lines with a max of 3.
	 */
	private static final int ITEM_SIZE = 64;
	
	/**
	 * The spacing between the text and the icon.
	 */
	private static final int ITEM_SPACING = 3;
	
	/**
	 * The size of the icon, this is the width/height of the image.
	 */
	private static final int ICON_SIZE = 48;
	
	/**
	 * The size of the flag, this is the width/height of the flag.
	 */
	private static final int FLAG_SIZE = 16;
	
	/**
	 * The application descriptor of the item.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * The element that uses this application item.
	 */
	private ApplicationElement element;
	
	/**
	 * The command that is used to start the application.
	 */
	private Command startCommand;
	
	/**
	 * The command that is used to stop the application.
	 */
	private Command stopCommand;
	
	/**
	 * The command that is used to adapt the appication.
	 */
	private Command adaptCommand;
	
	/**
	 * The command that is used to edit the application.
	 */
	private Command editCommand;
	
	/**
	 * A flag that indicates whether the control has the focus.
	 */
	private boolean focus;
	
	/**
	 * The image that is currently displayed.
	 */
	private Image image = null;
	
	/**
	 * The state indicator that is currently displayed.
	 */
	private Image flag = null;
	
	/**
	 * The current level of the application.
	 */
	private String level = null;
	
	/**
	 * The name that is currently displayed.
	 */
	private String[] name;

	/**
	 * The initial with is the height of the icon.
	 */
	private int height = ICON_SIZE + 2 * ITEM_SPACING;
	
	/**
	 * The style of the form.
	 */
	private FormStyle style;
	
	/**
	 * Creates a new application item that is used by the
	 * application item.
	 * 
	 * @param element The application element of the item.
	 */
	public ApplicationItem(ApplicationElement element) {
		super("");
		this.element = element;
		this.style = FormStyle.getStyle();
		setItemCommandListener(this);
		startCommand = new Command(PcomUI.getText(UI_START), Command.ITEM, 1);
		stopCommand = new Command(PcomUI.getText(UI_STOP), Command.ITEM, 1);
		adaptCommand = new Command(PcomUI.getText(UI_ADAPT), Command.ITEM, 1);
		editCommand = new Command(PcomUI.getText(UI_EDIT), Command.ITEM, 1);
		setLayout(LAYOUT_CENTER | LAYOUT_VTOP);
	}
	
	/**
	 * Sets the application descriptor of the item.
	 * 
	 * @param descriptor The descriptor of the item.
	 */
	public void setDescriptor(ApplicationDescriptor descriptor) {
		synchronized (FormDisplay.UI_LOCK) {
			this.descriptor = descriptor;
			recomputeDescriptor();
			recomputeCommands();			
			invalidate();
		}
	}
	
	/**
	 * Returns the application descriptor of the item.
	 * 
	 * @return The application descriptor of the item.
	 */
	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Called whenever one of teh commands is pressed.
	 * 
	 * @param command The command that has been executed.
	 * @param item The item that has executed the command.
	 */
	public void commandAction(final Command command, FormItem item) {
		if (command == startCommand) {
			element.startApplication(descriptor);
		} else if (command == stopCommand) {
			element.stopApplication(descriptor);
		} else if (command == adaptCommand) {
			element.adaptApplication(descriptor);
		} else if (command == editCommand) {
			element.editApplication(descriptor);
		}
	}
	
	/**
	 * Returns the minimum height of the item.
	 * 
	 * @return The minimum height of the item.
	 */
	public int getMinimumHeight() {
		return height;
	}
	
	/**
	 * Returns the minimum width of the item.
	 * 
	 * @return The minimum width of the item.
	 */
	public int getMinimumWidth() {
		return ITEM_SIZE;
	}
	
	/**
	 * Draws the application item on the specified graphics object.
	 * 
	 * @param graphics The grapics to draw on.
	 * @param width The width of the item.
	 * @param height The height of the item.
	 */
	public void paint(Graphics graphics, int width, int height) {
		graphics.setFont(style.FONT_ITEM);
		int background;
		int foreground;
		if (focus) {
			background = style.COLOR_ACTIVE_BACKGROUND; 
			foreground = style.COLOR_ACTIVE_FOREGROUND;
		} else {
			background = style.COLOR_BACKGROUND;
			foreground = style.COLOR_FOREGROUND;
		}
		graphics.setColor(background);
		graphics.fillRect(0, 0, width, height);
		// draw image or default image
		Image draw = null;
		if (image != null) {
			draw = image;
		} else {
			draw = PcomUI.getImage(PcomUI.IMAGE_APPLICATION);
		}
		graphics.drawImage(draw, ITEM_SPACING, ITEM_SPACING, Graphics.TOP | Graphics.LEFT);
		// add state flag to image
		int flagX = (ITEM_SIZE - ICON_SIZE) / 2 + ICON_SIZE - FLAG_SIZE; 
		if (flag != null) {
			int flagY = ITEM_SPACING + ICON_SIZE - FLAG_SIZE;
			graphics.setColor(background);
			graphics.fillArc(flagX, flagY, FLAG_SIZE - 1, FLAG_SIZE - 1, 0, 360);
			graphics.setColor(foreground);
			graphics.drawArc(flagX, flagY, FLAG_SIZE - 1, FLAG_SIZE - 1, 0, 360);
			graphics.drawImage(flag, flagX, flagY, Graphics.TOP | Graphics.LEFT);
		}
		if (level != null) {
			int levelY = ICON_SIZE - FLAG_SIZE * 2;
			graphics.setColor(background);
			graphics.fillArc(flagX, levelY, FLAG_SIZE - 1, FLAG_SIZE - 1, 0, 360);
			graphics.setColor(foreground);
			graphics.drawArc(flagX, levelY, FLAG_SIZE - 1, FLAG_SIZE - 1, 0, 360);
			int textWidth = style.FONT_ITEM.stringWidth(level);
			int textHeight = style.FONT_ITEM.getHeight();
			graphics.drawString(level, flagX + (FLAG_SIZE - textWidth) / 2 + 1, levelY 
				+ (FLAG_SIZE - textHeight) / 2 + 1, Graphics.TOP | Graphics.LEFT);		
		}
		// draw name elements 
		if (name != null) {
			graphics.setColor(foreground);
			int y = ITEM_SPACING * 2 + ICON_SIZE;
			int textHeight = style.FONT_ITEM.getHeight();
			for (int i = 0, s = name.length; i < s; i++) {
				int textWidth = style.FONT_ITEM.stringWidth(name[i]);
				graphics.drawString(name[i], (ITEM_SIZE - textWidth) / 2, y, Graphics.TOP | Graphics.LEFT);
				y += textHeight;
			}
		}
	}
	
	/**
	 * Called whenever the item is traversed.
	 * 
	 * @param code The code that caused the traversal.
	 * @param w The width of the item.
	 * @param h The heigth of the item.
	 * @param visible The visible area of the item.
	 * @return True to keep the focus, false to pass it.
	 */
	public boolean traverse(int code, int w, int h, int[] visible) {
		if (focus) {
			return false;
		} else {
			visible[0] = getMinimumWidth() / 2;
			visible[1] = getMinimumHeight() / 2;
			visible[2] = 1;
			visible[3] = 1;
			focus = true;
			repaint();
			return true;
		}
	}
	
	/**
	 * Called whenever the item is no longer traversed.
	 */
	public void traverseOut() {
		focus = false;
		repaint();
	}
	
	/**
	 * Updates the item commands that are available for the current
	 * state of the application descriptor.
	 */
	private void recomputeCommands() {
		removeCommand(editCommand);
		removeCommand(startCommand);
		removeCommand(stopCommand);
		removeCommand(adaptCommand);
		if (descriptor != null) {
			switch (descriptor.getState()) {
				case ApplicationDescriptor.STATE_APPLICATION_PAUSED:
					addCommand(editCommand);
					addCommand(stopCommand);
					setDefaultCommand(editCommand);
					break;
				case ApplicationDescriptor.STATE_APPLICATION_STARTED:
					addCommand(editCommand);
					addCommand(adaptCommand);
					addCommand(stopCommand);
					setDefaultCommand(editCommand);
					break;
				case ApplicationDescriptor.STATE_APPLICATION_STOPPED:
					addCommand(editCommand);
					addCommand(startCommand);
					setDefaultCommand(editCommand);
					break;
				default:
					// do not add any actions here
			}
		}
	}
	
	/**
	 * Called whenever the contents of the descriptor changes. This will
	 * update the locally cached information about the descriptor.
	 */
	private void recomputeDescriptor() {
		if (descriptor == null) {
			this.name = null;
			this.image = null;
			this.flag = null;
			this.level = null;
		} else {
			int l = -1;
			Contract contract = descriptor.getStatus();
			Vector preferences = descriptor.getPreferences();
			if (contract != null && preferences != null) {
				for (int i = 0, s = preferences.size(); i < s; i++) {
					if (contract.equals(preferences.elementAt(i))) {
						l = i;
						break;
					}
				}				
			}
			if (l != -1) {
				this.level = "" + (l + 1);	
			} else {
				this.level = null;
			}
			if (descriptor.getName() != null) {
				this.name = new String[] { descriptor.getName() };	
			} else {
				this.name = null;
			}
			if (descriptor.getImage() != null) {
				try {
					this.image = Image.createImage(new ByteArrayInputStream(descriptor.getImage()));
				} catch (IOException e) {
					Logging.debug(getClass(), "Cannot load application image.");
					this.image = null;
				}
			} else {
				this.image = null;
			}
			this.flag = null;
			switch (descriptor.getState()) {
				case ApplicationDescriptor.STATE_APPLICATION_STARTED:
					flag = PcomUI.getImage(PcomUI.IMAGE_GREEN);
					break;
				case ApplicationDescriptor.STATE_APPLICATION_PAUSED:
					flag = PcomUI.getImage(PcomUI.IMAGE_BLUE);
					break;
				case ApplicationDescriptor.STATE_APPLICATION_STOPPED:
					flag = PcomUI.getImage(PcomUI.IMAGE_RED);
					break;
			}
		}
		// change text to maximum size of control
		if (name != null) {
			int textHeight = style.FONT_ITEM.getHeight();
			for (int i = 0; i < name.length; i++) {
				name[i] = name[i].trim();
				int textWidth = style.FONT_ITEM.stringWidth(name[i]);
				String remainder = "";
				while (name[i].length() != 0 && textWidth + 2 * ITEM_SPACING > ITEM_SIZE) {
					remainder = name[i].charAt(name[i].length() - 1) + remainder;
					name[i] = name[i].substring(0, name[i].length() - 1);
					textWidth = style.FONT_ITEM.stringWidth(name[i] + "-");
				} 
				if (remainder.length() > 0) {
					int space = name[i].lastIndexOf(' ');
					if (space != -1) {
						remainder = name[i].substring(space, name[i].length()) + remainder;
						name[i] = name[i].substring(0, space);
					} else {
						name[i] += "-";
					}
					String[] copy = new String[name.length + 1];
					System.arraycopy(name, 0, copy, 0, name.length);
					copy[name.length] = remainder;
					name = copy;
				}
			}				
			height = ICON_SIZE + 3 * ITEM_SPACING + textHeight * name.length;
		} else {
			height = ICON_SIZE + 2 * ITEM_SPACING;
		}
	}
	
}
