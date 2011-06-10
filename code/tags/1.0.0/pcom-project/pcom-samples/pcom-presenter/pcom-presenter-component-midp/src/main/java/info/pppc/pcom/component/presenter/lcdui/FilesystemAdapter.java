package info.pppc.pcom.component.presenter.lcdui;

import java.util.Vector;

import info.pppc.base.lcdui.tree.TreeNode;

/**
 * The filesystem adapter provides a lazy file list.
 * 
 * @author Mac
 */
public class FilesystemAdapter extends TreeNode {

	/**
	 * A flag that indicates whether the adapter has
	 * been initialized.
	 */
	private boolean initialized;
	
	/**
	 * The content provider to retrieve the children.
	 */
	private FilesystemContentProvider content;
	
	/**
	 * The label provider to retrieve the label.
	 */
	private FilesystemLabelProvider label;
	
	/**
	 * The filesystem adapter enables the lazy retieval of
	 * filesystem objects form the filesystem.
	 * 
	 * @param data The data of the node.
	 * @param cp The content provider 
	 * @param lp The label provider.
	 */
	public FilesystemAdapter(Object data, FilesystemContentProvider cp, 
			FilesystemLabelProvider lp) {
		super(data, lp.getText(data), lp.getImage(data));
		this.content = cp;
		this.label = lp;
	}
	
	/**
	 * Returns the children. If not initialized, the children
	 * are retrieved from the content provider.
	 * 
	 * @return The vector of children.
	 */
	public Vector getChildren() {
		if (! initialized) {
			initialized = true;
			Object[] children = content.getChildren(getData());
			for (int i = 0; i < children.length; i++) {
				addChild(new FilesystemAdapter(children[i], content, label));
			}
		}
		return super.getChildren();
	}
	
	/**
	 * Determines whether the adapter has children.
	 * 
	 * @return True if there are children, false otherwise.
	 */
	public boolean hasChildren() {
		return content.hasChildren(getData());
	}
	
}
