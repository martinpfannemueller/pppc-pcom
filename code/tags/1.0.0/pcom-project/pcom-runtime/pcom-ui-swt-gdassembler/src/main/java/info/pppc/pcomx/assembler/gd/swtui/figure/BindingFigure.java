package info.pppc.pcomx.assembler.gd.swtui.figure;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;

/**
 * The binding figure is used to visualize a single instance
 * binding that needs to be resolved.
 * 
 * @author Mac
 */
public class BindingFigure extends Figure {

	/**
	 * The resource key that fetches the figure type label.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gd.swtui.figure.BindingFigure.TEXT";
	
	/**
	 * The border used by the binding figure.
	 */
	private LineBorder border = new LineBorder() {
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
	
	/**
	 * The layout of the item figure.
	 */
	protected ToolbarLayout layout = new ToolbarLayout();

	/**
	 * The description label that contains a descriptive text.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * The label that contains the node type and image.
	 */
	protected Label nameLabel = new Label();
	
	/**
	 * The system label that contains the number of systems.
	 */
	protected Label optionsLabel = new Label();
	
	/**
	 * The options that were available when the binding was
	 * created.
	 */
	protected int options;
	
	/**
	 * Creates a new binding figure with the specified name
	 * and options.
	 * 
	 * @param name The name of the binding.
	 * @param options The number of options to resolve the
	 * 	binding.
	 */
	public BindingFigure(String name, int options) {
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	    layout.setStretchMinorAxis(false);
	    layout.setSpacing(2);
		setOpaque(true);
		setLayoutManager(layout);
		setBorder(border);
		setBackgroundColor(ColorConstants.white);
		add(nameLabel);
		add(descriptionLabel);
		add(optionsLabel);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setBackgroundColor(ColorConstants.lightGray);
		descriptionLabel.setText(GDAssemblerUI.getText(UI_TEXT));
		nameLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_INSTANCE));
		nameLabel.setIconDimension(new Dimension(16, 16));
		nameLabel.setText(name);
		optionsLabel.setText("-/-");
	}
	
}
