package info.pppc.pcom.component.filesystem;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * This instance implements the filesystem interface. It provides 
 * remote access to the local filesystem.
 *
 * @author Mac
 */
public class FilesystemInstance implements IFilesystem, IInstance {
	
	/**
	 * The factory that has created the instance.
	 */
	protected FilesystemFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public FilesystemInstance(FilesystemFactory factory) {
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
	 * Called to start the instance. Nothing to be done.
	 */
	public void start() { }
	
	/**
	 * Called to pause the instance. Nothing to be done.
	 */
	public void pause() { }
	
	/**
	 * Called to stop the instance. Nothing to be done.
	 */
	public void stop() { }
	
	
	/**
	 * Returns a list of file system roots.
	 * 
	 * @return Returns the file system roots.
	 */
	public Vector listRoots() {
		try {
			java.io.File[] roots = java.io.File.listRoots();
			Vector result = new Vector();
			for (int i = 0; i < roots.length; i++) {
				String name = roots[i].getAbsolutePath().substring(0, 1);
				if (! (name.equalsIgnoreCase("A") || name.equalsIgnoreCase("B"))) { 
					result.addElement(create(roots[i]));
				}
			}
			return result;			
		} catch (Exception e) {
			Logging.error(getClass(), "Retrieving roots failed.", e);
			return new Vector();
		}
	}

	/**
	 * Returns the files contained in the specified directory.
	 * 
	 * @param directory The directory to search in.
	 * @return The files of the directory.
	 * @throws FileException Thrown if something goes wrong.
	 */
	public Vector listFiles(File directory) throws FileException {
		try {
			java.io.File dir = new java.io.File(directory.getPath());
			java.io.File[] contents = dir.listFiles();
			Vector result = new Vector();
			if (contents != null) {
				for (int i = 0; i < contents.length; i++) {
					result.addElement(create(contents[i]));
				}				
			}
			return result;
		} catch (Exception e) {
			Logging.error(getClass(), "Retrieving list failed.", e);
			throw new FileException(e.getMessage());
		}
	}

	/**
	 * Returns the content of the specified file.
	 * 
	 * @param file The file to retrieve.
	 * @return The content of the file as byte array.
	 * @throws FileException Thrown if something goes wrong.
	 */
	public byte[] getFile(File file) throws FileException {
		try {
			java.io.File f = new java.io.File(file.getPath());
			long longLength = f.length();
			if (longLength > Integer.MAX_VALUE) throw new IOException("Illegal file length.");
			int length = (int)longLength;
			byte[] result = new byte[length];
			int position = 0;
			int read = 0;
			FileInputStream fis = new FileInputStream(f);
			while ((length > 0) && ((read = fis.read(result, position, length)) != -1)) {
				length -= read;
				position += read;
			}
			return result;
		} catch (Exception e) {
			Logging.error(getClass(), "Retrieving content failed.", e);
			throw new FileException(e.getMessage());
		}
	}

	/**
	 * Returns the parent of the file or null if the file does not 
	 * have a parent.
	 * 
	 * @param file The file to lookup.
	 * @return The parent of the file or null if the file
	 * 	does not have a parent.
	 * @throws FileException Thrown if something goes wrong.
	 */
	public File getParent(File file) throws FileException {
		try {
			java.io.File f = new java.io.File(file.getPath());
			f = f.getParentFile();
			if (f != null) {
				return create(f);
			}
		} catch (Exception e) {
			Logging.error(getClass(), "Retrieving parent failed.", e);
			throw new FileException(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Creates a remote file from the specified file.
	 * 
	 * @param file The java file used to create a remote file.
	 * @return The remote file.
	 */
	private File create(java.io.File file) {
		File f = new File();
		f.setPath(file.getAbsolutePath());
		f.setName(file.getName());
		f.setDirectory(file.isDirectory());
		if (f.isDirectory()) {
			java.io.File[] files = file.listFiles();
			if (files != null) {
				f.setEmpty(files.length == 0);	
			}
		}
		return f;
	}
	
	/**
	 * Loads the state from a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		// nothing to be done for stateless components
	}
	
	/**
	 * Stores the state to a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to store to.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		// nothing to be done for stateless components		
	}
	
}
