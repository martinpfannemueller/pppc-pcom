package info.pppc.pcomx.assembler.gc.swtui;

import info.pppc.base.swtui.Application;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcomx.assembler.gc.swtui.action.AssemblerAction;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The gc assembler ui provides the resource bundle and image registry
 * for the pcom gc assembler visualizer extension.
 * 
 * @author Mac
 */
public class GCAssemblerUI {
	
	/**
	 * The image shown for the gc visualizer action.
	 */
	public static final String IMAGE_GC = "GC";
	
	/**
	 * The resource bundle that adds resource on top of pcom.
	 */
	private static ResourceBundle resourceBundle;
	
	/**
	 * The image registry used to share common images.
	 */
	private static ImageRegistry imageRegistry;
	
	/**
	 * The contribution item that has been registered with the register
	 * call.
	 */
	private static ActionContributionItem assemblerBrowser;
	
	/**
	 * Registers the gc assembler browser at the current base ui. If the browser has 
	 * been registered already, this method does nothing.
	 */
	public static void registerAssemblerBrowser() {
		if (assemblerBrowser == null) {
			final Application application = Application.getInstance();
			application.run(new Runnable() {
				public void run() {
					AssemblerAction gca = new AssemblerAction(application);
					assemblerBrowser = new ActionContributionItem(gca);
					Application.getInstance().addContribution(assemblerBrowser);	
				}
			});
		}
	}
	
	/**
	 * Removes a previously registered gc assembler browser from the current base ui. 
	 * If the browser has not been registered, this method does nothing.
	 */
	public static void unregisterAssemblerBrowser() {
		if (assemblerBrowser != null) {
			final Application application = Application.getInstance();
			application.run(new Runnable() {
				public void run() {
					application.removeContribution(assemblerBrowser);
					assemblerBrowser = null;
				}
			});
		}
	}
	

	/**
	 * Returns a localized text message for the specified key.
	 * 
	 * @param id The key used to retrieve a localized text message.
	 * @return The string for the key or an error message if the
	 * 	key is not contained in the resource file.
	 */
	public static String getText(String id) {
		String text = null;
		try {
			text = getResourceBundle().getString(id);	
		} catch (MissingResourceException e) {
			Logging.log(PcomUI.class, "Could not get text for " + id + ".");
		}			
		if (text == null || text.equals("")) {
			return "(!) MISSING: " + id;	
		} else {
			return text;	
		}
	}
	
	/**
	 * Returns the local resource bundle with the default 
	 * localization.
	 * 
	 * @return The local resouce bundle.
	 */
	private static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle("info/pppc/pcomx/assembler/gc/swtui/resource/text");
		}
		return resourceBundle;
	}
	
	
	/**
	 * Returns an image for the specified key or null if the key is
	 * not registered or the image cannot be loaded.
	 * 
	 * @param id The key to lookup.
	 * @return The image or null if it cannot be loaded.
	 */
	public static Image getImage(String id) {
		return getImageRegistry().get(id);
	}
	
	/**
	 * Returns the image descriptor that is associated with the specified
	 * key or null if the key is not registered.
	 * 
	 * @param id The key to lookup.
	 * @return The image descriptor of the key or null.
	 */
	public static ImageDescriptor getDescriptor(String id) {
		return getImageRegistry().getDescriptor(id);
	}
	
	/**
	 * Returns the image registry and loads it if it has not been
	 * loaded already.
	 * 
	 * @return The local image registry.
	 */
	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
			loadImage(IMAGE_GC, "gc.gif");
		}
		return imageRegistry;
	}

	
	/**
	 * Loads the specified image from the default image folder
	 * and places it into the image registry. 
	 * 
	 * @param key The key used to place the image.
	 * @param image The file name of the image to load.
	 */
	public static void loadImage(String key, String image) {
		getImageRegistry().put(key, ImageDescriptor.createFromURL
			(GCAssemblerUI.class.getResource("resource/" + image)));
	}
}
