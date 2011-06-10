package info.pppc.pcomx.assembler.gc.internal;

import java.io.IOException;

import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;

/**
 * The pointer is used to identify a certain position within an
 * application.
 * 
 * @author Mac
 */
public class Pointer implements ISerializable {

	/**
	 * The abbreviation of this class as used by the object stream translator.
	 */
	public static final String ABBREVIATION = ";PG";
	
	/**
	 * The entries of the pointer. These are string, boolean sequences
	 * that denote a certain position within the application. The string
	 * denotes the name of a dependency and the boolean denotes whether
	 * the string is a resource or instance dependency. True for instance,
	 * false for resource.
	 */
	private Object[] entries;
	
	/**
	 * Creates a pointer with no elements.
	 */
	public Pointer() {
		entries = new Object[0];
	}

	/**
	 * Creates a new pointer from the base pointer's elements and
	 * appends the specified instance or resource dependency.
	 * 
	 * @param base The base pointer to copy.
	 * @param instance The flag that determines whether the appended
	 * 	dependency is an instance dependency or a resource dependency.
	 * @param name The name of the insance or resource dependency.
	 */
	public Pointer(Pointer base, boolean instance, String name) {
		entries = new Object[base.entries.length + 2];
		System.arraycopy(base.entries, 0, entries, 0, base.entries.length);
		entries[base.entries.length] = new Boolean(instance);
		entries[base.entries.length + 1] = name;
	}

	/**
	 * Creates a new pointer from the base pointer's elements and
	 * removes the specified number of dependencies. The number of
	 * entries to remove must be smaller than the number of elements
	 * contained in the base pointer.
	 * 
	 * @param base The base pointer to copy.
	 * @param remove The number of elements to remove.
	 */
	public Pointer(Pointer base, int remove) {
		entries = new Object[base.entries.length - remove * 2];
		System.arraycopy(base.entries, 0, entries, 0, entries.length);
	}
	
	/**
	 * Returns the length of the pointer. The length corresponds
	 * to the number of dependencies contained in the pointer.
	 * 
	 * @return The length of the pointer.
	 */
	public int length() {
		return entries.length / 2;
	}
	
	/**
	 * Determines whether the dependency at the specified index is
	 * an instance dependency.
	 * 
	 * @param index The index of the dependency to retrieve.
	 * @return True if the dependency at the specified index is an
	 * 	instance dependency.
	 */
	public boolean isInstance(int index) {
		return ((Boolean)entries[index * 2]).booleanValue();
	}
	
	/**
	 * Retrieves the name of the dependency at the specified index.
	 * 
	 * @param index The index of the dependency to retrieve.
	 * @return The name of the dependency at the specified index.
	 */
	public String getName(int index) {
		return (String)entries[index * 2 + 1];
	}
	
	/**
	 * Returns a content based hashcode.
	 * 
	 * @return A content based hashcode.
	 */
	public int hashCode() {
		int code = 0;
		for (int i = 0; i < entries.length; i++) {
			code += entries[i].hashCode();
		}
		return code;
	}
	
	/**
	 * Determines whether the pointer equals the passed object.
	 * 
	 * @param o The object to compare with.
	 * @return True if the pointer and the object denote the
	 * 	same position within an application.
	 */
	public boolean equals(Object o) {
		if (o.getClass() != getClass()) return false;
		Pointer p = (Pointer)o;
		if (p.entries.length != entries.length) return false;
		for (int i = 0; i < entries.length; i++) {
			if (! entries[i].equals(p.entries[i])) return false;
		}
		return true;
	}
	
	/**
	 * Deserializes the pointer using the specified input stream.
	 * 
	 * @param input The input stream to read from.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void readObject(IObjectInput input) throws IOException {
		entries = (Object[])input.readObject();
	}
	
	/**
	 * Serializes the pointer using the specified output stream.
	 * 
	 * @param output The output stream to write to.
	 * @throws IOException Thrown by the underlying stream.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeObject(entries);
	}
}
