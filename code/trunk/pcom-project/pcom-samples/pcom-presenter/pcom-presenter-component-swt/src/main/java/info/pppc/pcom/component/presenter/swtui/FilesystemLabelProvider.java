package info.pppc.pcom.component.presenter.swtui;

import info.pppc.pcom.component.filesystem.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * The control label provider provides labels for the elements
 * created by a control content provider.
 * 
 * @author Mac
 */
public class FilesystemLabelProvider extends LabelProvider {

	/**
	 * Creates a new filesystem label provider.
	 */
	public FilesystemLabelProvider() {
		super();
	}

	/**
	 * Returns the label for a file or a filesystem root.
	 * 
	 * @param element The file or filesystem root.
	 * @return The label for the file.
	 */
	public String getText(Object element) {
		if (element == null) {
			return "NULL";
		} else if (element instanceof File) {
			String name = ((File)element).getName();
			if (name == null || name.length() == 0) {
				name = ((File)element).getPath();
			}
			return name;
			
		} else {
			return "UNKNOWN";
		}
	}


	/**
	 * Returns the image for the specified element. If the element is 
	 * a file or a directory an adequate image will be displayed, otherwise
	 * there will be no image.
	 * 
	 * @param element The element whose image must be retrieved.
	 * @return The image of the element.
	 */
	public Image getImage(Object element) {
		if (element != null && element instanceof File) {
			File f = (File)element;
			if (f.isDirectory()) {
				return PresenterUI.getImage(PresenterUI.IMAGE_FOLDER);	
			} else {
				return PresenterUI.getImage(PresenterUI.IMAGE_FILE);
			}
		} else {
			return null;
		}
	}


}
