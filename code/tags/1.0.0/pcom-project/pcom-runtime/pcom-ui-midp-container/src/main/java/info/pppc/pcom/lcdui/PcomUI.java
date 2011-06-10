package info.pppc.pcom.lcdui;

import info.pppc.base.lcdui.BaseUI;
import info.pppc.base.lcdui.util.ResourceBundle;
import info.pppc.base.system.util.Logging;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.lcdui.Image;

/**
 * The pcom ui provides common images and externalized strings
 * for the pcom lcdui extensions.
 * 
 * @author Mac
 */
public class PcomUI {

	/**
	 * The image key for the blue flag resource.
	 */
	public static final String IMAGE_BLUE = "BLUE";
	
	/**
	 * The image key for the red flag resource.
	 */
	public static final String IMAGE_RED = "RED";
	
	/**
	 * The image key for the green flag resource.
	 */
	public static final String IMAGE_GREEN = "GREEN";
	
	/**
	 * The image key for the image that is shown in the 
	 * application viewer if the application does not 
	 * have an image specified.
	 */
	public static final String IMAGE_APPLICATION = "APPLICATION";
	
	/**
	 * The image key for the application editor that
	 * is used to change preferences.
	 */
	public static final String IMAGE_BUILDER = "BUILDER";
	
	/**
	 * The resource bundle used to provide localized strings.
	 */
	private static ResourceBundle resourceBundle;
	
	/**
	 * The image registry used to provide common shared images.
	 */
	private static Hashtable imageRegistry;
	
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
		} catch (RuntimeException e) {
			Logging.log(BaseUI.class, "Could not get text for " + id + ".");
		}			
		if (text == null || text.equals("")) {
			return "(!) MISSING: " + id;	
		} else {
			return text;	
		}
	}
	
	/**
	 * Returns the local resource bundle with the default localization.
	 * 
	 * @return The local resouce bundle.
	 */
	private static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle("info/pppc/pcom/lcdui/resource/text");
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
	public static void loadImage(String key, String image) {
		Hashtable registry = getImageRegistry();
		if (! registry.containsKey(key)) {
			try {
				Image raw = Image.createImage
					(PcomUI.class.getResourceAsStream("/info/pppc/pcom/lcdui/resource/" + image));
				registry.put(key, raw);
			} catch (IOException e) {
				Logging.log(PcomUI.class, "Could not load image: " + image);
			}
		}
	}
	
	/**
	 * Returns the image registry of the base user interface.
	 * 
	 * @return The image registry of the user interface.
	 */
	private static Hashtable getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new Hashtable();
			loadImage(IMAGE_APPLICATION, "application.gif");
			loadImage(IMAGE_BLUE, "blue.gif");
			loadImage(IMAGE_RED, "red.gif");
			loadImage(IMAGE_GREEN, "green.gif");
			loadImage(IMAGE_BUILDER, "builder.gif");
		}
		return imageRegistry;
	}
	
	/**
	 * Returns an image for the specified key or null if the key is
	 * not registered or the image cannot be loaded.
	 * 
	 * @param id The key to lookup.
	 * @return The image or null if it cannot be loaded.
	 */
	public static Image getImage(String id) {
		return (Image)getImageRegistry().get(id);
	}
	
}
