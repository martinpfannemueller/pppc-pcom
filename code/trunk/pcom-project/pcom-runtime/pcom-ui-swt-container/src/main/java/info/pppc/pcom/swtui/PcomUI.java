package info.pppc.pcom.swtui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import info.pppc.base.swtui.Application;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.action.ApplicationAction;
import info.pppc.pcom.swtui.action.PcomSystemAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The pcom ui is a helper class that starts the base ui with some
 * slight extensions to support pcom system services.
 * 
 * @author Mac
 */
public class PcomUI {
	/**
	 * The image key for the container image.
	 */
	public static final String IMAGE_CONTAINER = "CONTAINER";

	/**
	 * The image key for the factory image.
	 */
	public static final String IMAGE_FACTORY = "FACTORY";

	/**
	 * The image key for the instance image.
	 */
	public static final String IMAGE_INSTANCE = "INSTANCE";
	
	/**
	 * The image key for the contract image.
	 */
	public static final String IMAGE_CONTRACT = "CONTRACT";
	
	/**
	 * The image key for the allocator image.
	 */
	public static final String IMAGE_ALLOCATOR = "ALLOCATOR";
	
	/**
	 * The image key for the resource image.
	 */
	public static final String IMAGE_RESOURCE = "RESOURCE";

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
	 * The image key for the builder image.
	 */
	public static final String IMAGE_BUILDER = "BUILDER";
	
	/**
	 * The resource bundle that adds resource on top of pcom.
	 */
	private static ResourceBundle resourceBundle;
	
	/**
	 * The image registry used to share common images.
	 */
	private static ImageRegistry imageRegistry;
	
	/**
	 * The system browser that has been registered with the register system browser method.
	 */
	private static ActionContributionItem systemBrowser;
	
	/**
	 * The application browser that has been registered with the register application browser
	 * method.
	 */
	private static ActionContributionItem applicationBrowser;
	
	/**
	 * Registers the system browser at the current base ui. If the browser
	 * has already been registered, this method does nothing.
	 */
	public static void registerSystemBrowser() {
		if (systemBrowser == null) {
			final Application application = Application.getInstance();
			application.run(new Runnable() {
				public void run() {
					systemBrowser = new ActionContributionItem(new PcomSystemAction(application));
					application.addContribution(systemBrowser);

				}
			});
		}
	}

	/**
	 * Removes a previously registered system browser from the current base ui. 
	 * If the browser has not been registered, this method does nothing.
	 */
	public static void unregisterSystemBrowser() {
		if (systemBrowser != null) {
			final Application application = Application.getInstance();
			application.run(new Runnable() {
				public void run() {
					application.removeContribution(systemBrowser);
					systemBrowser = null;
				}
			});
		}
	}
	
	/**
	 * Registers the application browser at the current base ui. If the browser
	 * has already been registered, this method does nothing.
	 */
	public static void registerApplicationBrowser() {
		if (applicationBrowser == null) {
			final Application application = Application.getInstance();
			application.run(new Runnable() {
				public void run() {
					applicationBrowser = new ActionContributionItem(new ApplicationAction(application));
					application.addContribution(applicationBrowser);

				}
			});
		}
	}
	
	/**
	 * Removes a previously registered application browser from the current base ui. 
	 * If the browser has not been registered, this method does nothing.
	 */
	public static void unregisterApplicationBrowser() {
		if (applicationBrowser != null) {
			final Application application = Application.getInstance();
			application.run(new Runnable() {
				public void run() {
					application.removeContribution(applicationBrowser);
					applicationBrowser = null;
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
			loadImage(IMAGE_CONTRACT, "contract.gif");
			loadImage(IMAGE_RESOURCE, "resource.gif");
			loadImage(IMAGE_ALLOCATOR, "allocator.gif");
			loadImage(IMAGE_CONTAINER, "container.gif");
			loadImage(IMAGE_FACTORY, "factory.gif");
			loadImage(IMAGE_INSTANCE, "instance.gif");
			loadImage(IMAGE_BLUE, "blue.gif");
			loadImage(IMAGE_GREEN, "green.gif");
			loadImage(IMAGE_RED, "red.gif");
			loadImage(IMAGE_APPLICATION, "application.gif");
			loadImage(IMAGE_BUILDER, "builder.gif");
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
			resourceBundle = ResourceBundle.getBundle("info/pppc/pcom/swtui/resource/text");
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
		getImageRegistry().put(key, ImageDescriptor.createFromURL
			(PcomUI.class.getResource("resource/" + image)));
	}

}
