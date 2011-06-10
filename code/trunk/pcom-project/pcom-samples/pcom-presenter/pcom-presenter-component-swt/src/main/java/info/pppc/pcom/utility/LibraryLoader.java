package info.pppc.pcom.utility;

import info.pppc.base.system.util.Logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads libraries from jars using temp files.
 * 
 * @author Mac
 */
public class LibraryLoader {

	/**
	 * Type constant for unknown os.
	 */
	public static final int OS_UNKNOWN = 0;

	/**
	 * Type constant for windows on X86.
	 */
	public static final int OS_WINDOWS_X86 = 1;

	/**
	 * Type constant for windows on X64.
	 */
	public static final int OS_WINDOWS_X64 = 2;

	/**
	 * Returns the OS type as one of the contstants in this class.
	 * 
	 * @return One of the OS_constants in the class.
	 */
	public static int getOS() {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		if (os.toUpperCase().contains("WINDOWS")) {
			if (arch.toUpperCase().contains("X86")) {
				return OS_WINDOWS_X86;
			} else if (arch.toUpperCase().contains("X64")) {
				return OS_WINDOWS_X64;
			}
		}
		return OS_UNKNOWN;
	}

	/**
	 * Loads a particular library indirectly (possibly from a jar). To do this
	 * the method creates a temp file using the library and loads the temp file.
	 * 
	 * @param library The library to load.
	 * @param absolute A flag to determine whether the library name points to an absolute path.
	 * @throws IOException Thrown if the library cannot be copied.
	 */
	public static void load(String library, boolean absolute) throws IOException {
		String ending;
		InputStream stream;
		if (absolute) {
			stream = LibraryLoader.class.getResourceAsStream(library);	
			int index = library.lastIndexOf('.');
			if (index != -1) {
				ending = library.substring(index, library.length());
			} else {
				ending = "";
			}
		} else {
			switch (getOS()) {
				case OS_WINDOWS_X64:
				case OS_WINDOWS_X86:
					ending = ".dll";
					break;
				default:
					ending = "";
			}
			stream = LibraryLoader.class.getResourceAsStream("/" + library + ending);
		}
		File f = File.createTempFile("lib-jni-", ending);
		FileOutputStream out = new FileOutputStream(f);
		byte[] buffer = new byte[4096];
		int read = 0;
		while ((read = stream.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		out.flush();
		out.close();
		stream.close();
		Logging.debug(LibraryLoader.class, "Mapping: " + library + " to " + f.getName() + " (" + f.length() + " bytes).");
		Logging.debug(LibraryLoader.class, "Loading " + f.getAbsolutePath());
		System.load(f.getAbsolutePath());
	}
}
