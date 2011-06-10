package info.pppc.pcomx.assembler.gc.swtui.figure;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcomx.assembler.gc.internal.AbstractElement;
import info.pppc.pcomx.assembler.gc.internal.Instance;
import info.pppc.pcomx.assembler.gc.internal.Resource;
import info.pppc.pcomx.assembler.gc.swtui.GCAssemblerUI;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;


/**
 * The element figure is used to visualize an instance or resource.
 * 
 * @author Mac
 */
public class ElementFigure extends ItemFigure {
	
	/**
	 * The resource key that contains the description of the element.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gc.swtui.figure.ElementFigure.TEXT";
	
	/**
	 * The resource key for the name of an instance.
	 */
	private static final String UI_INSTANCE = "info.pppc.pcomx.assembler.gc.swtui.figure.ElementFigure.INSTANCE";

	/**
	 * The resource key for the name of a resource.
	 */
	private static final String UI_RESOURCE = "info.pppc.pcomx.assembler.gc.swtui.figure.ElementFigure.RESOURCE";
	
	/**
	 * The resource key for the name of an application.
	 */
	private static final String UI_APPLICATION = "info.pppc.pcomx.assembler.gc.swtui.figure.ElementFigure.APPLICATION";
	
	/**
	 * The name label that contains an image and an identifier that
	 * identifies this element as resource or instance.
	 */
	protected Label nameLabel = new Label();
	
	/**
	 * The description label that contains a descriptive text.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * Creates a new element figure using the specified instance
	 * or resource.
	 * 
	 * @param element The resource or instance that specifies the
	 * 	contents of the figure.
	 */
	public ElementFigure(AbstractElement element) {
		super(element);
	}
	
	/**
	 * Creates the contents of the figure.
	 */
	protected void create() {
		add(nameLabel);
		add(descriptionLabel);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setText(GCAssemblerUI.getText(UI_TEXT));
		super.create();
	}
	
	/**
	 * Refreshes the visual representation of the element and recomputes
	 * all internal state. After this method has been called, the figure
	 * should be repainted.
	 */
	public void updateItem() {
		super.updateItem();
		nameLabel.setIconDimension(new Dimension(16, 16));
		AbstractElement element = (AbstractElement)item;
		if (element instanceof Resource) {
			nameLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_RESOURCE));	
			nameLabel.setText(GCAssemblerUI.getText(UI_RESOURCE));
		} else if (element instanceof Instance) {
			if (element.getParent() != null) {
				nameLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_INSTANCE));
				nameLabel.setText(GCAssemblerUI.getText(UI_INSTANCE));
			} else {
				nameLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_BUILDER));
				nameLabel.setText(GCAssemblerUI.getText(UI_APPLICATION));
			}
		} else {
			nameLabel.setIcon(null);
			nameLabel.setText("");
		}
		if (item.getTemplate() >= item.getTemplates()) {
			border.setColor(ColorConstants.red);
		}
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
}
