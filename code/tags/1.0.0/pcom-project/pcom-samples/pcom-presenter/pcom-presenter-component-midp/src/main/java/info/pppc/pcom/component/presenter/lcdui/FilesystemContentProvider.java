package info.pppc.pcom.component.presenter.lcdui;

import java.util.Hashtable;
import java.util.Vector;

import info.pppc.base.system.InvocationException;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.component.filesystem.File;
import info.pppc.pcom.component.filesystem.FileException;
import info.pppc.pcom.component.filesystem.IFilesystem;

/**
 * The control content provider provides the contents of a certain
 * filesystem. The contents that have been retrieved already are 
 * stored in a cache.
 * 
 * @author Mac
 */
public class FilesystemContentProvider {

	/**
	 * This hashtable hashes the child to parent mapping.
	 */
	private Hashtable parents = new Hashtable();

	/**
	 * This hashtable hashes the parent to children mapping.
	 */
	private Hashtable children = new Hashtable();

	/**
	 * The filesystem to use.
	 */
	private IFilesystem filesystem;
	
	/**
	 * The filesystem root to use.
	 */
	private Object root = new Object();

	/**
	 * Creates a new tree content provider for the specified 
	 * remote filesystem.
	 * 
	 * @param filesystem The filesystem used to create the
	 * 	contents.
	 */
	public FilesystemContentProvider(IFilesystem filesystem) {
		this.filesystem = filesystem;
	}
	
	/**
	 * Returns an object that indicates the root node.
	 * 
	 * @return An object that indicates the root node.
	 */
	public Object getRoot() {
		return root;
	}
	
	/**
	 * Returns the children of the element.
	 * 
	 * @param parentElement The element to check.
	 * @return The children of the element.
	 */
	public Object[] getChildren(Object parentElement) {
		if (children.contains(parentElement)) {
			return (Object[])children.get(parentElement);
		} else if (parentElement == root) {
			try {
				Vector roots = filesystem.listRoots();
				Object[] files = new Object[roots.size()];
				for (int i = 0; i < roots.size(); i++) {
					files[i] = roots.elementAt(i);
					parents.put(files[i], parentElement);
				}
				children.put(parentElement, files);
				return files;				
			} catch (InvocationException e) {
				Logging.error(getClass(), "Remote exception while listing roots.", e);
				return new Object[0];
			}
		} else {
			File file = (File)parentElement;
			if (file.isDirectory()) {
				try {
					Vector child = filesystem.listFiles(file);
					Object[] files = new Object[child.size()];
					for (int i = 0; i < child.size(); i++) {
						files[i] = child.elementAt(i);
						parents.put(files[i], parentElement);
					}
					children.put(parentElement, files);
					return files;				
				} catch (InvocationException e) {
					Logging.error(getClass(), "Remote exception while listing directory.", e);
					return new Object[0];					 
				} catch (FileException e) {
					Logging.error(getClass(), "File exception while listing directory.", e);
					return new Object[0];
				}
			} else {
				children.put(file, new Object[0]);
				return new Object[0];
			}
		}
	}
	
	/**
	 * Returns the parent of the element or null if the
	 * element is the root.
	 * 
	 * @param element The element to check.
	 * @return The parent of the element or null if the
	 * 	element is the root.
	 */
	public Object getParent(Object element) {
		if (element == root) {
			return null;
		} else {
			return parents.get(element);
		}
	}
	/**
	 * Determines whether the specified element has children
	 * 
	 * @param element The element to check.
	 * @return True if the element has children, false otherwise.
	 */
	public boolean hasChildren(Object element) {
		if (element == null) {
			return false;
		} else if (element == getRoot()) {
			return true;
		} else if (element instanceof File) {
			File f = (File)element;
			if (! f.isDirectory()) return false;
			else return ! f.isEmpty();
		} else {
			return false;
		}
	}
	
	/**
	 * Disposes the contents of the cache.
	 */
	public void dispose() {
		parents = new Hashtable();
		children = new Hashtable();
	}


}
