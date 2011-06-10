package info.pppc.pcom.swtui.application.viewer;

import info.pppc.pcom.swtui.PcomUI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The application image is a custom control that is used by the
 * application viewer to display applications.
 * 
 * @author Mac
 */
public class ApplicationImage extends Canvas implements Listener {

	/**
	 * The width of the control. The controls width will never be
	 * larger than that.
	 */
	public static final int CONTROL_WIDTH = 64;

	/**
	 * The default height of the control. The control might be larger
	 * than that.
	 */
	public static final int CONTROL_HEIGHT = 96;
	
	/**
	 * The spacing between the image, the text and the border.
	 */
	private static final int H_SPACE = 3;
	
	/**
	 * The spacing between the border and the text. 
	 */
	private static final int V_SPACE = 1;
	
	/**
	 * The size of the icon that is provided by the application.
	 */
	private static final int I_SIZE = 48;
	
	/**
	 * The size of the status icon that is shown on top of the icon.
	 */
	private static final int F_SIZE = 16;
	
	/**
	 * The application that is visualized.
	 */
	protected IApplication application;
	
	/**
	 * The image that is currently displayed.
	 */
	protected Image image = null;
	
	/**
	 * The state indicator that is currently displayed.
	 */
	protected Image flag = null;
	
	/**
	 * The current level of the application.
	 */
	protected String level = null;
	
	/**
	 * The name that is currently displayed.
	 */
	protected String[] name;
	
	/**
	 * The preferred size of the application image.
	 */
	protected Point size = new Point(CONTROL_WIDTH, CONTROL_HEIGHT);
	
	/**
	 * The menu manager that is used for the context menu.
	 */
	protected MenuManager manager;
	
	/**
	 * Creates a new application image on the specified parent.
	 * 
	 * @param parent The parent of the image.
	 * @param style The style of the image. Possible constants
	 * 	are identical to the swt canvas widget.
	 */
	public ApplicationImage(Composite parent, int style) {
		super(parent, style);
		addListener(SWT.Paint, this);
		addListener(SWT.KeyDown, this);
		addListener(SWT.FocusIn, this);
		addListener(SWT.FocusOut, this);
		addListener(SWT.Traverse, this);
		manager = new MenuManager();
		setMenu(manager.createContextMenu(this));
	}
	
	/**
	 * Handles the events for the control. Paint to redraw, keydown
	 * to receive a focus. Traverse to give back the focus.
	 * 
	 * @param event The event to handle.
	 */
	public void handleEvent(Event event) {
		switch (event.type) {
			case SWT.Paint:
				paint(event.gc);
				break;
			case SWT.KeyDown:
				break;
			case SWT.FocusIn:
				redraw();
				break;
			case SWT.FocusOut:
				redraw();
				break;
			case SWT.Traverse:
				event.doit = true;
				break;				
			default:
				// will never happen
		}
		
	}
	
	/**
	 * Sets the application that is visualized by this
	 * image. Set to null to uninitialize it.
	 * 
	 * @param application The application to visualize.
	 */
	public void setApplication(IApplication application) {
		checkWidget();
		this.application = application;
		recompute();
		redraw();
	}
	
	/**
	 * Returns the application that is visualized by this
	 * image.
	 * 
	 * @return The application visualized by the image.
	 */
	public IApplication getApplication() {
		return application;
	}
	
	/**
	 * Computes the size of the image.
	 * 
	 * @param wHint The width hint.
	 * @param hHint The height hint.
	 * @param changed The change flag.
	 * @return The size of the image.
	 */	
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return size;
	}
	
	/**
	 * Updates the cached values for the current application.
	 */
	protected void recompute() {
		if (this.image != null) {
			image.dispose();
		}
		this.size = new Point(CONTROL_WIDTH, CONTROL_HEIGHT);
		if (application == null) {
			this.name = null;
			this.image = null;
			this.flag = null;
			this.level = null;
		} else {
			int l = application.getLevel();
			if (l != -1) {
				this.level = "" + (l + 1);	
			} else {
				this.level = null;
			}
			if (application.getName() != null) {
				this.name = new String[] { application.getName() };	
			} else {
				this.name = null;
			}
			if (application.getImage() != null) {
				this.image = new Image(getDisplay(), application.getImage(), SWT.IMAGE_COPY);
			} else {
				this.image = null;
			}
			this.flag = null;
			switch (application.getState()) {
				case IApplication.STATE_STARTED:
					flag = PcomUI.getImage(PcomUI.IMAGE_GREEN);
					break;
				case IApplication.STATE_PAUSED:
					flag = PcomUI.getImage(PcomUI.IMAGE_BLUE);
					break;
				case IApplication.STATE_STOPPED:
					flag = PcomUI.getImage(PcomUI.IMAGE_RED);
					break;
			}
			manager.removeAll();
			Action[] actions = application.getMenuActions();
			if (actions != null) {
				for (int i = 0; i < actions.length; i++) {
					if (actions[i] == null) {
						manager.add(new Separator());
					} else {
						manager.add(actions[i]);
					}
				}
			}
			manager.updateAll(true);
		}
		// change text to maximum size of control
		if (name != null) {
			GC gc = new GC(this);
			int textHeight = gc.getFontMetrics().getHeight();
			for (int i = 0; i < name.length; i++) {
				name[i] = name[i].trim();
				Point p = gc.textExtent(name[i], SWT.DRAW_TRANSPARENT);
				int textWidth = p.x;
				String remainder = "";
				while (name[i].length() != 0 && textWidth + 2 * V_SPACE > CONTROL_WIDTH ) {
					remainder = name[i].charAt(name[i].length() - 1) + remainder;
					name[i] = name[i].substring(0, name[i].length() - 1);
					p = gc.textExtent(name[i] + "-", SWT.DRAW_TRANSPARENT);
					textWidth = p.x;
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
			this.size = new Point(CONTROL_WIDTH, I_SIZE + 3 * H_SPACE + textHeight * name.length);	
		} else {
			this.size = new Point(CONTROL_WIDTH, I_SIZE + 2 * H_SPACE);
		}
	}
	
	/**
	 * Draws the application image on the screen.
	 * 
	 * @param gc The graphics context of the image.
	 */
	protected void paint(GC gc) {
		if (isFocusControl()) {
			setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
			setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		}
		// fill background
		Rectangle c = getClientArea();
		gc.fillRectangle(c);
		// draw image or default image
		Image draw = null;
		if (image != null) {
			draw = image;
		} else {
			draw = PcomUI.getImage(PcomUI.IMAGE_APPLICATION);
		}
		Rectangle d = draw.getBounds();
		gc.drawImage(draw, d.x, d.y, d.width, d.height, 
				(CONTROL_WIDTH - I_SIZE) / 2, H_SPACE, I_SIZE, I_SIZE);
		// add state flag to image
		if (flag != null) {
			gc.fillOval((CONTROL_WIDTH - I_SIZE) / 2 + I_SIZE - F_SIZE, H_SPACE + I_SIZE - F_SIZE, F_SIZE, F_SIZE);
			gc.drawOval((CONTROL_WIDTH - I_SIZE) / 2 + I_SIZE - F_SIZE, H_SPACE + I_SIZE - F_SIZE, F_SIZE, F_SIZE);
			Rectangle b = flag.getBounds();
			gc.drawImage(flag, b.x, b.y, b.width, b.height, 
				(CONTROL_WIDTH - I_SIZE) / 2 + I_SIZE - F_SIZE + 1, H_SPACE + I_SIZE - F_SIZE + 1, F_SIZE, F_SIZE);
		}
		if (level != null) {
			gc.fillOval((CONTROL_WIDTH - I_SIZE) / 2 + I_SIZE - F_SIZE, I_SIZE - F_SIZE * 2, F_SIZE, F_SIZE);
			gc.drawOval((CONTROL_WIDTH - I_SIZE) / 2 + I_SIZE - F_SIZE, I_SIZE - F_SIZE * 2, F_SIZE, F_SIZE);
			Point p = gc.textExtent(level);
			int textWidth = p.x;
			int textHeight = gc.getFontMetrics().getHeight();
			gc.drawText(level, (CONTROL_WIDTH - I_SIZE) / 2 + I_SIZE - F_SIZE + (F_SIZE - textWidth) / 2 + 1, I_SIZE - F_SIZE * 2 + (F_SIZE - textHeight) / 2, true);
			
		}
		// draw name elements 
		if (name != null) {
			int y = H_SPACE * 2 + I_SIZE;
			int textHeight = gc.getFontMetrics().getHeight();
			for (int i = 0; i < name.length; i++) {
				Point p = gc.textExtent(name[i], SWT.DRAW_TRANSPARENT);
				int textWidth = p.x;
				gc.drawText(name[i], (CONTROL_WIDTH - textWidth) / 2, y, true);
				y += textHeight;
			}
		}
	}
	
	/**
	 * Disposes the control and the menu manager of the control.
	 */
	public void dispose() {
		super.dispose();
		if (manager != null) {
			manager.dispose();
			manager = null;			
		}
		if (image != null) {
			image.dispose();
			image = null;
		}		
	}
}
