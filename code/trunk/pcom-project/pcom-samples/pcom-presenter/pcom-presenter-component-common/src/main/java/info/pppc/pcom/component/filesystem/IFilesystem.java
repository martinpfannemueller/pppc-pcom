package info.pppc.pcom.component.filesystem;

import info.pppc.base.system.InvocationException;

import java.util.Vector;

/**
 * This interface is used to encapsulate the local filesystem. It enables
 * the retrieval of files and it can be used to browse a filesystem.
 * 
 * @author Mac
 */
public interface IFilesystem {

	/**
	 * Returns a vector of files that describe the file system roots.
	 * 
	 * @return A vector of files that represent file system roots.
	 * @throws InvocationException Thrown if the policy stops rebinding.
	 */
	public Vector listRoots() throws InvocationException;

	/**
	 * Returns the files and directories in a certain directory as 
	 * a vector of files.
	 * 
	 * @param directory The directory to lookup.
	 * @return A vector of files that represents the contents of
	 * 	the directory.
	 * @throws FileException Thrown if the directory does not exist
	 * 	or if the directory is somehow malformed (i.e. if it is a file, etc.).
	 * @throws InvocationException Thrown if the policy stops rebinding.
	 */
	public Vector listFiles(File directory) throws FileException, InvocationException; 

	/**
	 * Returns the content of a file as a byte array.
	 * 
	 * @param file The file to open.
	 * @return The content of the file.
	 * @throws FileException Thrown if the file does not exist or
	 * 	if the file represents a directory.
	 * @throws InvocationException Thrown if the policy stops rebinding.
	 */
	public byte[] getFile(File file) throws FileException, InvocationException;
	
	/**
	 * Returns the parent file of the specified file or null if
	 * the file does not have a parent.
	 * 
	 * @param file The file to lookup.
	 * @return The parent of the file.
	 * @throws FileException Thrown if something goes wrong.
	 * @throws InvocationException Thrown if the policy stops rebinding.
	 */
	public File getParent(File file) throws FileException, InvocationException;
	
}
