package info.pppc.pcomx.assembler.gc.swtui.figure;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcomx.assembler.gc.internal.AbstractBinding;
import info.pppc.pcomx.assembler.gc.internal.InstanceBinding;
import info.pppc.pcomx.assembler.gc.internal.ResourceBinding;
import info.pppc.pcomx.assembler.gc.swtui.GCAssemblerUI;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The binding figure is used to visualize a resource or instance binding
 * of the gc assembler.
 * 
 * @author Mac
 */
public class BindingFigure extends ItemFigure {
	
	/**
	 * The description shown in the binding figure after the name.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gc.swtui.figure.BindingFigure.TEXT";
	
	/**
	 * This contains the name of the binding.
	 */
	protected Label nameLabel = new Label();
	
	/**
	 * The description label that contains a descriptive text.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * A flag that indicates whether the binding has been resolved.
	 * This is used to determine the color of the border.
	 */
	protected boolean resolved = false;
	
	/**
	 * Creates a new binding figure from the specified binding.
	 * 
	 * @param binding The binding used as input into the figure.
	 */
	public BindingFigure(AbstractBinding binding) {
		super(binding);
	}
	
	/**
	 * Called to create the contents of the figure.
	 */
	protected void create() {
		border = new LineBorder() {
			public Insets getInsets(IFigure figure) {
				return new Insets(5, 5, 5, 5);
			}
			public void paint(IFigure figure, Graphics graphics, Insets insets) {
				tempRect.setBounds(getPaintRectangle(figure, insets));
				if (getWidth() % 2 == 1) {
					tempRect.width--;
					tempRect.height--;
				}
				tempRect.shrink(getWidth() / 2, getWidth() / 2);
				graphics.setLineWidth(getWidth());
				if (getColor() != null)
					graphics.setForegroundColor(getColor());
				graphics.drawRoundRectangle(tempRect, 15, 15);
			}
		};
		add(nameLabel);
		add(descriptionLabel);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setText(GCAssemblerUI.getText(UI_TEXT));
		setForegroundColor(ColorConstants.darkGray);
		super.create();
		setOpaque(false);
	}
	
	/**
	 * Sets the resolved flag to the specified value. After
	 * this method has been called, the binding should be
	 * repainted.
	 * 
	 * @param resolved The value of the resolved flag.
	 */
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	/**
	 * Determines whether the resolved flag of the figure is set.
	 * 
	 * @return True if the resolved flag is set, false otherwise.
	 */
	public boolean isResolved() {
		return resolved;
	}
	
	/**
	 * Reloads all data from the binding and updates the figure
	 * accordingly. After this method has been called, the binding
	 * should be repainted.
	 */
	public void updateItem() {
		super.updateItem();
		AbstractBinding binding = (AbstractBinding)item;
		nameLabel.setIconDimension(new Dimension(16, 16));
		if (binding instanceof ResourceBinding) {
			nameLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_ALLOCATOR));	
		} else if (binding instanceof InstanceBinding) {
			nameLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_FACTORY));
		} else {
			nameLabel.setIcon(null);
		}
		Contract demand = binding.getDemand();
		if (demand != null) {
			nameLabel.setText(demand.getName());	
		} else {
			nameLabel.setText("");
		}
		if (resolved) {
			if (item.getTemplate() >= item.getTemplates()) {
				border.setColor(ColorConstants.red);
			}
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
		graphics.fillRoundRectangle(getBounds(), 15, 15);
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
