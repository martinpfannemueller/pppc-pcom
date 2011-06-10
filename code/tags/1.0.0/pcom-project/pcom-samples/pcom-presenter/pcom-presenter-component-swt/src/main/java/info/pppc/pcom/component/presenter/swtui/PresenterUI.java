package info.pppc.pcom.component.presenter.swtui;

import info.pppc.base.system.util.Logging;

import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The resource class acts as a facade to localized string data and
 * globally shared images.
 * 
 * @author Mac
 */
public class PresenterUI {

	/**
	 * The image key for the identifier image.
	 */
	public static final String IMAGE_PRESENTER = "PRESENTER";

	/**
	 * The image key for the default device image.
	 */	
	public static final String IMAGE_FOLDER = "FOLDER";

	/**
	 * The image key for the default plugin image.
	 */		
	public static final String IMAGE_FILE = "FILE";
	
	/**
	 * The image key for the property image.
	 */
	public static final String IMAGE_OPEN = "OPEN";
	
	/**
	 * The image key for the default service image.
	 */
	public static final String IMAGE_CLOSE = "CLOSE";
	
	/**
	 * The image key for the default suitability image.
	 */
	public static final String IMAGE_NEXT = "NEXT";

	/**
	 * The image key for the default ability image.
	 */
	public static final String IMAGE_PREVIOUS = "PREVIOUS";

	/**
	 * The image key for the logo image that is shown when no slide is loaded.
	 */
	public static final String IMAGE_LOGO = "LOGO";
	
	/**
	 * The image key for the adaptation image that is shown when the application adapts.
	 */
	public static final String IMAGE_ADAPTING = "ADAPTING";
	
	/**
	 * The image registry used to share common images.
	 */
	private static ImageRegistry imageRegistry;
	
	/**
	 * The resource bundle used to provide localized strings.
	 */
	private static ResourceBundle resourceBundle;
	
	/**
	 * Returns a localized text message for the specified key.
	 * 
	 * @param id The key used to retrieve a localized text message.
	 * @return The string for the key or the key if the key cannot
	 * 	be found.
	 */
	public static String getText(String id) {
		String text = null;
		try {
			text = getResourceBundle().getString(id);	
		} catch (Exception e) {
			Logging.error(PresenterUI.class, "Could not get resource for " + id + ".", e);
		}
		if (text == null || text.equals("")) {
			return "(!) MISSING: " + id;	
		} else {
			return text;	
		}
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
			loadImage(IMAGE_PRESENTER, "presenter.gif");
			loadImage(IMAGE_FOLDER, "folder.gif");
			loadImage(IMAGE_FILE, "file.gif");
			loadImage(IMAGE_OPEN, "open.gif");
			loadImage(IMAGE_CLOSE, "close.gif");
			loadImage(IMAGE_NEXT, "next.gif");
			loadImage(IMAGE_PREVIOUS, "previous.gif");
			loadImage(IMAGE_ADAPTING, "adapting.gif");
			loadImage(IMAGE_LOGO, "logo.gif");
		}
		return imageRegistry;
	}
	
	/**
	 * Returns the local resource bundle with the default 
	 * localization.
	 * 
	 * @return The local resouce bundle.
	 */
	private static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle("info/pppc/pcom/component/presenter/swtui/text");
		}
		return resourceBundle;
	}

	/**
	 * Loads the specified image from the default image folder
	 * and places it into the image registry. 
	 * 
	 * @param key The key used to place the image.
	 * @param image The file name of the image to load.
	 */
	private static void loadImage(String key, String image) {
		imageRegistry.put(key, ImageDescriptor.createFromURL
			(PresenterUI.class.getResource(image)));
	}

}
