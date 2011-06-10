package info.pppc.pcom.component.portrayer;

import java.io.ByteArrayInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.capability.ir.IIRAccessor;
import info.pppc.pcom.capability.swtui.ISwtAccessor;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceStatus;
import info.pppc.pcom.system.model.contract.reader.IResourceProvisionReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleMapper;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class PortrayerInstance implements IPortrayer, IInstance, IListener {
	
	/**
	 * The factory that has created the instance.
	 */
	protected PortrayerFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * The shell that holds the display.
	 */
	protected Shell shell;
	
	/**
	 * The display of the instance.
	 */
	protected Display display;

	/**
	 * The image to draw within the shell.
	 */
	protected Image image;
	
	/**
	 * The picture data of the current image.
	 */
	protected byte[] picture;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public PortrayerInstance(PortrayerFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Called to set the context of the instance.
	 *
	 * @param context The context of the instance.
	 */
	public void setContext(IInstanceContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context of the instance.
	 */
	public void unsetContext() {
		this.context = null;
	}
	
	/**
	 * Called to start the instance.
	 */
	public void start() {
		ISwtAccessor accessor = (ISwtAccessor)context.getAccessor("UI");
		if (accessor != null) {
			accessor.run(new Runnable() {
				public void run() {
					display = Display.getCurrent();
				}
			});			
		}
		if (! valid()) {
			throw new IllegalStateException("No display set.");
		}
		// recompute the mapping and perform future mappings
		handleEvent(null);
		context.getStatus().addStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);
	}
	
	/**
	 * Called to pause the instance. Nothing to be done.
	 */
	public void pause() { 
		// stop status recomputation and prohibit multiple listeners
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);
	}
	
	/**
	 * Called to stop the instance. Clear all state.
	 */
	public void stop() {
		// stop status recomputation and we are outa here
		context.getStatus().removeStatusListener(IInstanceStatus.EVENT_RESOURCE_CHANGED, this);
		hidePicture();
	}
	

	/**
	 * Displays the specified picture on the screen.
	 * 
	 * @param picture The picture to display.
	 */
	public void showPicture(byte[] picture) {
		show(picture);
	}
	
	/**
	 * Hides any open picture. 
	 */
	public void hidePicture() {
		if (! valid()) return;
		display.syncExec(new Runnable() {
			public void run() {
				if (shell != null && ! shell.isDisposed()) {
					shell.dispose();
				}
				if (image != null && ! image.isDisposed()) {
					image.dispose();
				}
				shell = null;
				image = null;
				picture = null;
			}
		});
	}
	
	/**
	 * Determines whether the shell is visisble.
	 * 
	 * @return True if the shell is visible, false otherwise.
	 */
	public boolean isVisible() {
		if (! valid()) return false;
		final boolean[] result = new boolean[] { false };
		display.syncExec(new Runnable() {
			public void run() {
				result[0] = (shell != null && ! shell.isDisposed());
			}
		});
		return result[0];
	}
	
	/**
	 * Displays the specified picture on the screen.
	 * 
	 * @param picture The picture to display.
	 */
	private void show(final byte[] picture) {
		if (! valid()) return;
		this.picture = picture;
		try {
			// change the image, create and open a shell that draws it
			display.syncExec(new Runnable() {
				public void run() {
					// dispose any existing image
					if (image != null) {
						image.dispose();
					}
					image = new Image(display, new ByteArrayInputStream(picture));
					if (shell == null || shell.isDisposed()) {
	                    // create a shell
						shell = new Shell(display);
						// draw the image in the buffer on every repaint
						shell.addListener (SWT.Paint, new Listener () {
							public void handleEvent (org.eclipse.swt.widgets.Event e) {
								Rectangle c = shell.getClientArea ();
								GC gc = e.gc;
								if (image != null) {
									Rectangle d = image.getBounds ();
									gc.drawImage (image, 0, 0, d.width, d.height, 0, 0, c.width, c.height);						
								} else {
									gc.fillRectangle(c.x, c.y, c.width, c.height);
								}
							}
						});
						shell.setBounds(display.getBounds());
						shell.open();	
					}
					shell.redraw();
				}
			});
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not display image.", t);
		}
	}

	/**
	 * Checks whether the display is valid and returns true if so and false
	 * if not.
	 * 
	 * @return True if the display is valid, false otherwise.
	 */
	private boolean valid() {
		return (display != null && ! display.isDisposed());
	}
	
	/**
	 * Called whenever the status changes and the mapping must be performed.
	 * This will map the provision of the ui resource and of the ir resource
	 * directly to the provided interface.
	 * 
	 * @param event Ignored here.
	 */
	public void handleEvent(Event event) {
		IInstanceProvisionWriter instance = context.getTemplate().getInstance();
		instance.createInterface(IPortrayer.class.getName());
		ITypeProvisionWriter target = instance.getInterface(IPortrayer.class.getName()); 
		// map the ui resource
		IResourceProvisionReader uireader = context.getStatus().getResource("UI");
		SimpleMapper uimapper = new SimpleMapper();
		uimapper.addDimension("DISPLAY", "DISPLAY");
		uimapper.map(uireader.getInterface(ISwtAccessor.class.getName()), target);
		// map the ir resource
		IResourceProvisionReader irreader = context.getStatus().getResource("IR");
		SimpleMapper irmapper = new SimpleMapper();
		irmapper.addFeature("IR", "LOCAL", "LOCATION", "PORTRAYER");
		irmapper.map(irreader.getInterface(IIRAccessor.class.getName()), target);
		context.getTemplate().commitTemplate();	
	}
	
	/**
	 * Initializes the portrayer using the specified checkpoint.
	 * 
	 * @param checkpoint The checkpoint used to initialize the portrayer.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		checkpoint.setComplete(true);
		checkpoint.putBoolean(CHECKPOINT_VISIBLE, isVisible());
		checkpoint.putObject(CHECKPOINT_PICTURE, picture);
		
	}
	
	/**
	 * Stores the current state of the portrayer into the checkpoint.
	 * 
	 * @param checkpoint The checkpoint used to store the state.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		if (! checkpoint.getBoolean(CHECKPOINT_VISIBLE)) {
			hidePicture();
		} else {
			byte[] picture = (byte[])checkpoint.getObject(CHECKPOINT_PICTURE);
			showPicture(picture);
		}
	}
	
}
