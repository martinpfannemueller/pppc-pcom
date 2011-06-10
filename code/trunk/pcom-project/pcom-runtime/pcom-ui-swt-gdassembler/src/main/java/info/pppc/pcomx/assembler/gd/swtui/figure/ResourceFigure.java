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
 * The resource figure is used to visualize a single resource
 * with a certain name.
 * 
 * @author Mac
 */
public class ResourceFigure extends Figure {

	/**
	 * The resource key that fetches the figure type label.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gd.swtui.figure.ResourceFigure.TEXT";
		
	/**
	 * The resource key that fetches the resouce name.
	 */
	private static final String UI_RESOURCE = "info.pppc.pcomx.assembler.gd.swtui.figure.ResourceFigure.RESOURCE";
	
	/**
	 * The description label that contains a descriptive text.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * The label that contains the node type and image.
	 */
	protected Label typeLabel = new Label();

	/**
	 * The name label that contains the name of the assembly.
	 */
	protected Label nameLabel = new Label();
	
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
	 * Creates a new figure that visualizes a resource.
	 * 
	 * @param name The name of the resource.
	 * @param reuse A flag that indicates whether the resource is
	 * 	reused.
	 */
	public ResourceFigure(String name, boolean reuse) {
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	    layout.setStretchMinorAxis(false);
	    layout.setSpacing(2);
		setOpaque(true);
		setLayoutManager(layout);
		setBorder(border);
		setBackgroundColor(ColorConstants.white);
		add(typeLabel);
		add(descriptionLabel);
		add(nameLabel);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setBackgroundColor(ColorConstants.lightGray);
		descriptionLabel.setText(GDAssemblerUI.getText(UI_TEXT));
		typeLabel.setIcon(PcomUI.getImage(PcomUI.IMAGE_RESOURCE));
		typeLabel.setText(GDAssemblerUI.getText(UI_RESOURCE));
		typeLabel.setIconDimension(new Dimension(16, 16));
		nameLabel.setText(name);
		if (reuse) {
			border.setColor(ColorConstants.lightBlue);
		}
	}
	
}
