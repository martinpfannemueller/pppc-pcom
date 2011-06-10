package info.pppc.pcomx.assembler.gc.swtui.figure;

import info.pppc.pcomx.assembler.gc.internal.AbstractItem;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

/**
 * The item figure is the base figure for all items of the gc assembler.
 * It computes the label colors and the value of a contract field based
 * on the template/templates construct of the item.
 * 
 * @author Mac
 */
public class ItemFigure extends Figure {
	
	/**
	 * The item that is drawn by the figure. 
	 */
	protected AbstractItem item;
	
	/**
	 * A label that describes the number of contracts and the currently 
	 * selected contract of the item.
	 */
	protected Label contractLabel = new Label();
	
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
	 * A flag that determines whether the contents of the item have
	 * been created already.
	 */
	protected boolean created;
	
	/**
	 * Creates an item figure for the specified item.
	 * 
	 * @param item The item that is visualized by the figure.
	 */
	public ItemFigure(AbstractItem item) {
		this.item = item;
	}
	
	/**
	 * Creates the contents of the figure.
	 */
	protected void create() {
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	    layout.setStretchMinorAxis(false);
	    layout.setSpacing(2);
		setOpaque(true);
		setLayoutManager(layout);
		setBorder(border);
		setBackgroundColor(ColorConstants.white);
		add(contractLabel);
		created = true;
	}

	/**
	 * Called whenever the figure's contents need to be refreshed
	 * using the item. After this method has been called, the figure
	 * should be repainted.
	 */
	public void updateItem() {
		if (! created) create();
		int templates = item.getTemplates();
		int template = item.getTemplate();
		String contracts = "-/-";
		if (templates != 0) {
			if (template >= templates) {
				contracts = "-/" + templates;
			} else {
				contracts = (template + 1) + "/" + templates;
			}
		}
		contractLabel.setText(contracts);
		if (item.isConfigured()) {
			if (item.getPointer() != null) {
				border.setColor(ColorConstants.lightBlue);
			} else {
				border.setColor(ColorConstants.black);	
			}				
		} else {
			border.setColor(ColorConstants.lightGray);
		}			
		
	}
	
}
