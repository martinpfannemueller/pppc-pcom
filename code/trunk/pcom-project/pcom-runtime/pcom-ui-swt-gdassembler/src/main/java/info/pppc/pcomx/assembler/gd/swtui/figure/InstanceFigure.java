package info.pppc.pcomx.assembler.gd.swtui.figure;

import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;

/**
 * The request figure is used to visualize a single 
 * instance request that needs to be resolved.
 * 
 * @author Mac
 */
public class InstanceFigure extends Figure {

	/**
	 * The resource key that fetches the figure type label.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gd.swtui.figure.InstanceFigure.TEXT";
		
	/**
	 * The resource key that fetches the resouce name.
	 */
	private static final String UI_INSTANCE = "info.pppc.pcomx.assembler.gd.swtui.figure.InstanceFigure.INSTANCE";
	
	/**
	 * The description label that contains a descriptive text.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * The label that contains the node type and image.
	 */
	protected Label typeLabel = new Label();

	/**
	 * The options label of the instance figure.
	 */
	protected Label optionsLabel = new Label();
	
	/**
	 * The border of the item figure.
	 */
	protected LineBorder border = new LineBorder(ColorConstants.black, 2) {
		public Insets getInsets(IFigure figure) {
			return new Insets(5, 5, 5, 5);
		};
	};
	
	/**
	 * The layout of the item figure.
	 */
	protected ToolbarLayout layout = new ToolbarLayout();
	
	/**
	 * The options available for the request.
	 */
	protected int options;
	
	/**
	 * The request that needs to be resolved.
	 * 
	 * @param name The name of the request.
	 * @param options The options for the request.
	 */
	public InstanceFigure(String name, int options) {
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	    layout.setStretchMinorAxis(false);
	    layout.setSpacing(2);
		setOpaque(true);
		setLayoutManager(layout);
		setBorder(border);
		setBackgroundColor(ColorConstants.white);
		add(typeLabel);
		add(descriptionLabel);
		add(optionsLabel);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setBackgroundColor(ColorConstants.lightGray);
		descriptionLabel.setText(GDAssemblerUI.getText(UI_TEXT));
		typeLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_INSTANCE));
		typeLabel.setText(GDAssemblerUI.getText(UI_INSTANCE));
		typeLabel.setIconDimension(new Dimension(16, 16));
		optionsLabel.setText(name);
	}
	
}
