package info.pppc.pcomx.assembler.swtui.figure;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcomx.assembler.swtui.AssemblerUI;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * The state figure is used to visualize some application state
 * of a running component.
 * 
 * @author Mac
 */
public class StateFigure extends Figure {

	/**
	 * The resource key that contains the description.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.swtui.figure.StateFigure.TEXT";
	
	/**
	 * The resource key for the name of an instance.
	 */
	private static final String UI_INSTANCE = "info.pppc.pcomx.assembler.swtui.figure.StateFigure.INSTANCE";

	/**
	 * The resource key for the name of a resource.
	 */
	private static final String UI_RESOURCE = "info.pppc.pcomx.assembler.swtui.figure.StateFigure.RESOURCE";
	
	/**
	 * The resource key for the name of an application.
	 */
	private static final String UI_APPLICATION = "info.pppc.pcomx.assembler.swtui.figure.StateFigure.APPLICATION";
	
	/**
	 * The type label that contains an image and an identifier that
	 * identifies this element as resource or instance.
	 */
	protected Label typeLabel = new Label();
	
	/**
	 * The description label that contains a descriptive text.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * The label that contains the name of the binding.
	 */
	protected Label nameLabel = new Label();
	
	/**
	 * A flag that indicates whether the instance or resource is reused.
	 * A true reuse flag will result in a blue border.
	 */
	protected boolean reused;
	
	/**
	 * A flag that indicates whether the instance or resource is available.
	 * A false available flag will result in a gray border that overrides
	 * the reused border.
	 */
	protected boolean available = true;
	
	/**
	 * The border of the item figure.
	 */
	protected LineBorder border = new LineBorder(ColorConstants.black, 2) {
		public Insets getInsets(IFigure figure) {
			return new Insets(5, 5, 5, 5);
		};
	};
	
	/**
	 * The layout of the pointer figure.
	 */
	protected ToolbarLayout layout = new ToolbarLayout();
	
	/**
	 * Creates a new state figure for an application.
	 */
	public StateFigure() {
		init(PcomUI.getImage(PcomUI.IMAGE_BUILDER),
			AssemblerUI.getText(UI_APPLICATION),  
			AssemblerUI.getText(UI_TEXT), 
			AssemblerUI.getText(UI_APPLICATION));
	}
	
	/**
	 * Creates a new state figure for an instance or resource
	 * with the specified name.
	 * 
	 * @param name The name of the state.
	 * @param instance A boolean that indicates whether the
	 * 	state figure should be an instane or a resource.
	 */
	public StateFigure(String name, boolean instance) {
		if (instance) {
			init(
				PcomUI.getImage(PcomUI.IMAGE_INSTANCE),
				AssemblerUI.getText(UI_INSTANCE),
				AssemblerUI.getText(UI_TEXT),			
				name);
		} else {
			init(
				PcomUI.getImage(PcomUI.IMAGE_RESOURCE),
				AssemblerUI.getText(UI_RESOURCE),
				AssemblerUI.getText(UI_TEXT),			
				name);
		}
	}
	
	/**
	 * Initializes the graphical representation using the 
	 * specified icon, type string, description string and
	 * name.
	 * 
	 * @param icon The icon visible in the top left corner.
	 * @param type The type drawn next to the icon.
	 * @param description The description in the middle.
	 * @param name The name of the element.
	 */
	private void init(Image icon, String type, String description, String name) {
		setOpaque(true);
		setBackgroundColor(ColorConstants.white);
		setLayoutManager(layout);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	    layout.setStretchMinorAxis(false);
	    layout.setSpacing(2);
		add(typeLabel);
		add(descriptionLabel);
		add(nameLabel);
		setBorder(border);
		typeLabel.setIconDimension(new Dimension(16, 16));
		typeLabel.setIcon(icon);
		typeLabel.setText(type);
		nameLabel.setText(name);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setText(description);
	}
	
	/**
	 * Called whenever the figure needs to be redrawn. The only thing
	 * that needs to be done is to draw the right background color for
	 * the description label.
	 * 
	 * @param graphics The grapichics object to draw the figure.
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		Rectangle d = descriptionLabel.getBounds();
		int y1 = d.y - 1;
		int y2 = d.y + d.height + 2;
		Rectangle r = getBounds();
		int x1 = r.x + 3;
		int x2 = r.x + r.width - 3;
		graphics.setBackgroundColor(ColorConstants.lightGray);
		graphics.fillRectangle(x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * Sets a flag that indicates whether the instance or resource
	 * is reused.
	 * 
	 * @param reused True if the pointer is reused, false otherwise.
	 */
	public void setReused(boolean reused) {
		this.reused = reused;
		if (available) {
			if (reused) {
				border.setColor(ColorConstants.lightBlue);
			} else {
				border.setColor(ColorConstants.black);
			}
			repaint();
		}
	}
	
	/**
	 * Determines whether the resource or instance is reused.
	 * 
	 * @return true if it is reused, false otherwise.
	 */
	public boolean isReused() {
		return reused;
	}
	
	/**
	 * Sets a flag that indicates whether the instance or resource
	 * is available.
	 * 
	 * @param available True if the pointer is available, false otherwise.
	 */
	public void setAvailable(boolean available) {
		this.available = available;
		if (available) {
			nameLabel.setForegroundColor(ColorConstants.black);
			typeLabel.setForegroundColor(ColorConstants.black);			
			setReused(reused);
		} else {
			nameLabel.setForegroundColor(ColorConstants.lightGray);
			typeLabel.setForegroundColor(ColorConstants.lightGray);
			border.setColor(ColorConstants.lightGray);
			repaint();
		}
	}
	
	/**
	 * Determines whether the resource or instance is available.
	 * 
	 * @return true if it is available, false otherwise.
	 */
	public boolean isAvailable() {
		return reused;
	}
	
}
