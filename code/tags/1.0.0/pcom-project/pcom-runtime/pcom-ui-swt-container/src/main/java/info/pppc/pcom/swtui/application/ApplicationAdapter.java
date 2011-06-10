package info.pppc.pcom.swtui.application;

import java.io.ByteArrayInputStream;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.application.action.ChangeAction;
import info.pppc.pcom.swtui.application.action.DeleteAction;
import info.pppc.pcom.swtui.application.action.EditAction;
import info.pppc.pcom.swtui.application.action.LoadAction;
import info.pppc.pcom.swtui.application.action.SaveAction;
import info.pppc.pcom.swtui.application.action.StartAction;
import info.pppc.pcom.swtui.application.action.StopAction;
import info.pppc.pcom.swtui.application.viewer.IApplication;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.contract.Contract;

/**
 * The application adapter adapts application descriptors provided by
 * the application manager to user interface elements.
 * 
 * @author Mac
 */
public class ApplicationAdapter implements IApplication {

	/**
	 * The application descriptor of the adapter.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * The control that uses the adapter.
	 */
	private ApplicationControl control;
	
	/**
	 * The image of the application or null if none.
	 */
	private Image image;
	
	/**
	 * Creates a new application manager without any description
	 * for the given control.
	 * 
	 * @param control The control used by the adapter.
	 */
	public ApplicationAdapter(ApplicationControl control) {
		this.control = control;
	}
	
	/**
	 * Sets the application descriptor contained in this adapter.
	 * 
	 * @param descriptor The descriptor to set.
	 */
	public void setDescriptor(ApplicationDescriptor descriptor) {
		// dispose old image
		if (image != null) {
			image.dispose();
			image = null;
		}
		// create new image
		if (descriptor != null) {
			byte[] data = descriptor.getImage();
			if (data != null) {
				try {
					ImageData idata = new ImageData(new ByteArrayInputStream(data));					
					if (idata.transparentPixel > 0) {
						// ok this is a dirty little hack since transparent bitmaps
						// are not supported on ipaqs						
						idata.type = SWT.IMAGE_GIF;
						image = new Image(control.getDisplay(), idata, idata.getTransparencyMask());
					} else {
						image = new Image(control.getDisplay(), idata);	
					}
					
					
				} catch (SWTException e) {
					Logging.debug(getClass(), "Cannot load image.");
				}
			}			
		}
		// set descriptor
		this.descriptor = descriptor;

	}
	
	/**
	 * Returns the application descriptor of the adapter.
	 * 
	 * @return Returns the descriptor.
	 */
	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Returns the image used by the descriptor.
	 * 
	 * @return The image of the descriptor.
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Returns the menu actions available for the current state.
	 * 
	 * @return The menu actions that are available for this adapter
	 * 	considering the current state of the descriptor. 
	 */
	public Action[] getMenuActions() {
		Action start = new StartAction(control, descriptor);
		Action change = new ChangeAction(control, descriptor);
		Action stop = new StopAction(control, descriptor);
		Action save = new SaveAction(control, descriptor);
		Action load = new LoadAction(control, descriptor);
		boolean stopped = (descriptor.getState() == ApplicationDescriptor.STATE_APPLICATION_STOPPED);
		boolean started = (descriptor.getState() == ApplicationDescriptor.STATE_APPLICATION_STARTED);
		start.setEnabled(stopped);
		stop.setEnabled(! stopped);
		change.setEnabled(started);
		save.setEnabled(started);
		load.setEnabled(started);
		return new Action[] {
			start,
			change,
			stop,
			null,
			load,
			save,
			null,
			new EditAction(control, descriptor),
			new DeleteAction(control, descriptor)
		};
	}
	
	/**
	 * Returns the name of the descriptor.
	 * 
	 * @return The name of the descriptor.
	 */
	public String getName() {
		if (descriptor != null) {
			return descriptor.getName();
		}
		return null;
	}
	
	/**
	 * Returns the state of the application contained in the
	 * descriptor or stopped if the descriptor is not set.
	 * 
	 * @return The state constant to visualize the application.
	 */
	public int getState() {
		if (descriptor == null) {
			return IApplication.STATE_STOPPED;
		} else {
			switch (descriptor.getState()) {
				case ApplicationDescriptor.STATE_APPLICATION_STARTED:
					return IApplication.STATE_STARTED;
				case ApplicationDescriptor.STATE_APPLICATION_PAUSED:
					return IApplication.STATE_PAUSED;
				case ApplicationDescriptor.STATE_APPLICATION_STOPPED:
					return IApplication.STATE_STOPPED;
				default:
					// will never happen
					return IApplication.STATE_STOPPED;
			}
		}
	}
	
	/**
	 * Returns the current preference level or -1 if the application
	 * is not configured properly.
	 * 
	 * @return The current preference level.
	 */
	public int getLevel() {
		Vector preferences = descriptor.getPreferences();
		Contract status = descriptor.getStatus();
		if (status == null) return -1;
		for (int i = 0; i < preferences.size(); i++) {
			Contract pref = (Contract)preferences.elementAt(i);
			if (pref.equals(status)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Called to dispose the application adapter this method must
	 * be called if the adapter is no longer used to free the
	 * image data contained in it.
	 */
	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	

}
