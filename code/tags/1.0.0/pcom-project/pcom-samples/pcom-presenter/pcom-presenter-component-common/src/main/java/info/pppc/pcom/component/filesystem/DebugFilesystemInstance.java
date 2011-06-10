package info.pppc.pcom.component.filesystem;

import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;

import java.util.Vector;

/**
 * This instance implements the filesystem interface. It provides 
 * remote access to a non existent file system.
 *
 * @author Mac
 */
public class DebugFilesystemInstance implements IFilesystem, IInstance {
	
	/**
	 * The file data is used internally to manage files.
	 * 
	 * @author Mac
	 */
	private class FileData {
		
		/**
		 * The parent of the file, may be null.
		 */
		public FileData parent;
		
		/**
		 * The file itself, should not be null.
		 */
		public File file;
		
		/**
		 * The children of the file, may be null.
		 */
		public Vector children;
		
		/**
		 * Creates a new file data object with the specified file
		 * and children.
		 * 
		 * @param file The file.
		 * @param children The children of the file or null.
		 */
		public FileData(File file, FileData[] children) {
			this.file = file;
			if (children != null) {
				this.children = new Vector();
				for (int i = 0; i < children.length; i++) {
					this.children.addElement(children[i]);
					children[i].parent = this;
				}
			}
			
		}
	}
	
	
	/**
	 * The factory that has created the instance.
	 */
	protected DebugFilesystemFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * The file system roots.
	 */
	private Vector roots = new Vector();
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public DebugFilesystemInstance(DebugFilesystemFactory factory) {
		this.factory = factory;
		// create hard disk
		File root1 = create("HDD", "C:/", true, false); 
		File windows = create("Windows", "C:/Windows", true, true);
		File presentations = create("Presentations", "C:/Presentations", true, false);
		File demo = create("Demo.ppt", "C:/Presentations/Demo.ppt", false, false);
		File demo2 = create("Demo2.ppt", "C:/Presentations/Demo2.ppt", false, false);
		File demo3 = create("Demo3.xls", "C:/Presentations/Demo3.xls", false, false);
		roots.addElement(new FileData(root1, new FileData[] { new FileData(windows, null),
				new FileData(presentations, new FileData[] { new FileData(demo, null),
						new FileData(demo2, null), new FileData(demo3, null)
				})
		}));
		// create floppy disk
		File root2 = create("FDD", "A:/", true, false);
		File stuff = create("Stuff", "A:/Stuff", true, false);
		File file1 = create("File1.xls", "A:/Stuff/File1.xls", false, false);
		File file2 = create("File2.xls", "A:/Stuff/File2.xls", false, false);
		roots.addElement(new FileData(root2, new FileData[] { 
			new FileData(stuff, new FileData[] {
			new FileData(file1, null), new FileData(file2, null)	
		})}));
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
		Logging.log(getClass(), "List roots called.");
		Vector v = new Vector();
		for (int i = 0; i < roots.size(); i++) {
			FileData data = (FileData)roots.elementAt(i);
			v.addElement(data.file);
		}
		return v;
	}

	/**
	 * Returns the files contained in the specified directory.
	 * 
	 * @param directory The directory to search in.
	 * @return The files of the directory.
	 * @throws FileException Thrown if something goes wrong.
	 */
	public Vector listFiles(File directory) throws FileException {
		Logging.log(getClass(), "List files called.");
		FileData data = get(directory);
		Vector v = new Vector();
		if (data != null && data.children != null) {
			Vector c = data.children;
			for (int i = 0; i < c.size(); i++) {
				FileData d = (FileData)c.elementAt(i);
				v.addElement(d.file);
			}
		}
		return v;
	}

	/**
	 * Returns the content of the specified file.
	 * 
	 * @param file The file to retrieve.
	 * @return The content of the file as byte array.
	 * @throws FileException Thrown if something goes wrong.
	 */
	public byte[] getFile(File file) throws FileException {
		Logging.log(getClass(), "Get file called.");
		return new byte[0];
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
		Logging.log(getClass(), "Get parent called.");
		FileData data = get(file);
		if (data == null || data.parent == null || data.parent.file == null) 
			throw new FileException("No parent available.");
		return data.parent.file;
	}
	
	/**
	 * Creates a remote file from the specified data
	 * 
	 * @param name The name of the file.
	 * @param path The path of the file.
	 * @param dir A bool that determines whether this is a directory.
	 * @param empty A flag that indicates whether the file is empty.
	 * @return The remote file.
	 */
	private File create(String name, String path, boolean dir, boolean empty) {
		File f = new File();
		f.setDirectory(dir);
		f.setPath(path);
		f.setName(name);
		f.setEmpty(empty);
		return f;
	}
	
	/**
	 * Searches for the specified file based on its path.
	 * 
	 * @param file The file to look for.
	 * @return The file data with the specified file.
	 */
	private FileData get(File file) {
		if (file == null || file.getPath() == null) return null;
		String path = file.getPath();
		Vector filedata = new Vector();
		for (int i = 0; i < roots.size(); i++) {
			filedata.addElement(roots.elementAt(i));
		}
		while (! filedata.isEmpty()) {
			FileData d = (FileData)filedata.elementAt(0);
			filedata.removeElementAt(0);
			if (d != null) {
				File f = d.file;
				if (path.equals(f.getPath())) {
					return d;
				}
				Vector c = d.children;
				if (c != null) {
					for (int i = 0; i < c.size(); i++) {
						filedata.addElement(c.elementAt(i));
					}
				}				
			}
		}
		return null;
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
