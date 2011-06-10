package info.pppc.pcomx.assembler.swtui.figure;

import java.util.Vector;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.system.DeviceDescription;
import info.pppc.base.system.ObjectID;
import info.pppc.pcomx.assembler.swtui.AssemblerUI;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The device figure is used to represent a device within
 * the visualization.  
 * 
 * @author Mac
 */
public class DeviceFigure extends Figure {
	
	/**
	 * The resource key used to retrieve the description text.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.swtui.figure.DeviceFigure.TEXT";
	
	/**
	 * The resource key used to retrieve the service text and the number of services known.
	 */
	private static final String UI_SERVICE = "info.pppc.pcomx.assembler.swtui.figure.DeviceFigure.SERVICE";
	
	
	/**
	 * The device description of the device.
	 */
	protected DeviceDescription description;
	
	/**
	 * A flag that indicates whether the device is currently availabe.
	 */
	protected boolean available = true;
	
	/**
	 * A flag that indicates whether the device has been resolved, i.e.
	 * its resources have been retrieved.
	 */
	protected boolean resolved = true;
	
	/**
	 * The border used to draw the device figure.
	 */
	protected LineBorder border = new LineBorder(ColorConstants.black, 3) {
		public Insets getInsets(IFigure figure) {
			return new Insets(7, 7, 7, 7);
		};
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
	 * The layout of the device figure.
	 */
	protected ToolbarLayout layout = new ToolbarLayout();
	
	/**
	 * The label that contains the name and the image of the device.
	 */
	protected Label deviceLabel = new Label();
	
	/**
	 * The label that contains the description of the device figure.
	 */
	protected Label descriptionLabel = new Label();
	
	/**
	 * The labels that describe the services of the device.
	 */
	protected Vector serviceLabels = new Vector();
	
	/**
	 * The highest id of the well known service that has a description
	 * in the resource file. 
	 */
	protected int services = 0;

	/**
	 * Creates a new device figure from the specified device description.
	 * 
	 * @param description The description of the device.
	 */
	public DeviceFigure(DeviceDescription description) {
		// retrieve the number of well known services
		String snumber = AssemblerUI.getText(UI_SERVICE);
		try {
			services = Integer.parseInt(snumber);
		} catch (NumberFormatException e) {	}
		// configure the overall layout and the labels
		setLayoutManager(layout);
		setBorder(border);
		setBackgroundColor(ColorConstants.white);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	    layout.setStretchMinorAxis(false);
	    layout.setSpacing(2);
		add(deviceLabel, BorderLayout.CENTER);
		deviceLabel.setIcon(BaseUI.getImage(BaseUI.IMAGE_DEVICE + "." + description.getType()));
		deviceLabel.setIconAlignment(PositionConstants.LEFT);
		deviceLabel.setIconDimension(new Dimension(16, 16));
		deviceLabel.setTextAlignment(PositionConstants.CENTER);
		deviceLabel.setText(description.getName());
		add(descriptionLabel);
		descriptionLabel.setForegroundColor(ColorConstants.white);
		descriptionLabel.setText(AssemblerUI.getText(UI_TEXT));
		ObjectID[] services = description.getServices();
		for (int i = 0; i < services.length; i++) {
			Label serviceLabel = new Label();
			String serviceText = getServiceText(services[i]);
			if (serviceText != null) {
				serviceLabel.setText(serviceText);
				serviceLabels.addElement(serviceLabel);
				add(serviceLabel);				
			}
		}
	}
	
	/**
	 * Sets the available flag to the specified value and
	 * updates the device figure based on the current settings.
	 * 
	 * @param available True to indicate that the device is 
	 * 	available, false otherwise.
	 */
	public void setAvailable(boolean available) {
		this.available = available;
		updateDevice();
	}
	
	/**
	 * Determines whether the available flag is set.
	 * 
	 * @return True if the device is available.
	 */
	public boolean isAvailable() {
		return available;
	}
	
	/**
	 * Determines whether the device's resources have been
	 * resolved.
	 * 
	 * @return True if they are resolved, false otherwise.
	 */
	public boolean isResolved() {
		return resolved;
	}

	/**
	 * Sets a flag that indicates whether the resources
	 * are resolved, true if resolved, false otherwise.
	 * 
	 * @param resolved True to indicate that the resources
	 * 	are resolved.
	 */
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	
	/**
	 * Recomputes the border color based on the flags.
	 */
	protected void updateDevice() {
		if (! isAvailable()) {
			border.setColor(ColorConstants.red);
		} else {
			if (resolved) {
				border.setColor(ColorConstants.lightBlue);
			} else {
				border.setColor(ColorConstants.lightGray);
			}
		}
	}
	
	/**
	 * Resolves the service text for a given object id.
	 * 
	 * @param oid The object id that needs to be transformed.
	 * @return The service text as shown in the ui or null if none.
	 */
	private String getServiceText(ObjectID oid) {
		if (oid == null) return null;
		for (int i = 1; i <= services; i++) {
			ObjectID id = new ObjectID(i);
			if (id.equals(oid)) {
				return AssemblerUI.getText(UI_SERVICE + "." + i);
			}
		}
		return null;
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
		int x1 = r.x + 4;
		int x2 = r.x + r.width - 4;
		graphics.setBackgroundColor(ColorConstants.lightBlue);
		graphics.fillRectangle(x1, y1, x2 - x1, y2 - y1);
	}

}
