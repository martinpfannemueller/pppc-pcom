package info.pppc.pcom.system.container.io;

import java.util.Vector;

/**
 * A chunk vector is a series of ordered byte arrays that is used by
 * the plugin architecture to allow prepending and appending of byte
 * arrays without copying them. 
 * Note that the methods in the chunk vector are not synchronized. 
 * Also they do not perform sanity checks on the values passed to
 * them, in order not to slow down the implementation. Note also that
 * chunks added to the vector are not copied, so they must not 
 * be overwritten in order not to change and thus invalidate them.
 * 
 * @author Mac
 */
public class ChunkVector {

	/**
	 * Integers that denote the start positions of the date in the
	 * corresponding byte arrays stored in the chunk vector.
	 */
	protected Vector starts = new Vector();
	
	/**
	 * Integers that denote the ends of the data in the corresponding
	 * byte arrays stored in the chunk vector.
	 */
	protected Vector ends = new Vector();
	
	/**
	 * The chunks stored in this chunk vector as byte arrays.
	 */
	protected Vector chunks = new Vector();

	/**
	 * The total length of the content stored by the vector.
	 */
	protected int total = 0;

	/**
	 * Creates a new chunk vector.
	 */
	public ChunkVector() {
		super();
	}
	
	/**
	 * Appends the specified chunk vector to this vector 
	 * without copying the arrays.
	 * 
	 * @param vector The vector to append.
	 */
	public void append(ChunkVector vector) {
		for (int i = 0; i < vector.chunks.size(); i++) {
			chunks.addElement(vector.chunks.elementAt(i));
			Integer istart = ((Integer)vector.starts.elementAt(i));
			Integer iend = ((Integer)vector.ends.elementAt(i));
			starts.addElement(istart);
			ends.addElement(iend);
			int start = istart.intValue();
			int end = iend.intValue();
			total += (end - start);
		}
	}
	
	/**
	 * Prepends the specified chunk vector to this vector
	 * without copying the arrays.
	 * 
	 * @param vector The vector to prepend.
	 */
	public void prepend(ChunkVector vector) {
		for (int i = 0; i < vector.chunks.size(); i++) {
			chunks.insertElementAt(vector.chunks.elementAt(i), 0);
			Integer istart = ((Integer)vector.starts.elementAt(i));
			Integer iend = ((Integer)vector.ends.elementAt(i));
			starts.insertElementAt(istart, 0);
			ends.insertElementAt(iend, 0);
			int start = istart.intValue();
			int end = iend.intValue();
			total += (end - start);
		}		
	}
	

	/**
	 * Appends the chunk to the chunk vector. 
	 * 
	 * @param chunk The chunk to append.
	 * @param start The start of the chunk.
	 * @param end The end value of the chunk.
	 */
	public void append(byte[] chunk, int start, int end) {
		starts.addElement(new Integer(start));
		ends.addElement(new Integer(end));
		chunks.addElement(chunk);
		total += (end - start);
	}
	
	/**
	 * Prepends the specified chunk to the vector.
	 * 
	 * @param chunk The chunk to prepend.
	 * @param start The start of the chunk.
	 * @param end The end of the chunk.
	 */
	public void prepend(byte[] chunk, int start, int end) {
		starts.insertElementAt(new Integer(start), 0);
		ends.insertElementAt(new Integer(end), 0);
		chunks.insertElementAt(chunk, 0);
		total += (end - start);		
	}

	/**
	 * Appends the complete chunk to the vector.
	 * 
	 * @param chunk The chunk to append.
	 */
	public void append(byte[] chunk) {
		starts.addElement(new Integer(0));
		ends.addElement(new Integer(chunk.length));
		chunks.addElement(chunk);
		total += (chunk.length);		
	}

	/**
	 * Prepends the complete chunk to the vector.
	 * 
	 * @param chunk The chunk to prepend.
	 */
	public void prepend(byte[] chunk) {
		starts.insertElementAt(new Integer(0), 0);
		ends.insertElementAt(new Integer(chunk.length), 0);
		chunks.insertElementAt(chunk, 0);
		total += (chunk.length);				
	}
	
	/**
	 * Retrieves the start index of the specified chunk.
	 * 
	 * @param chunk The chunk to retrieve.
	 * @return The start index of the specified chunk.
	 */
	public int getStart(int chunk) {
		return ((Integer)starts.elementAt(chunk)).intValue();		
	}

	/**
	 * Retrieves the end index of the specified chunk.
	 * 
	 * @param chunk The chunk to retrieve.
	 * @return The end index of the specified chunk.
	 */	
	public int getEnd(int chunk) {
		return ((Integer)ends.elementAt(chunk)).intValue();
	}

	/**
	 * Retrieves the chunk at the specified index.
	 * 
	 * @param chunk The chunk to retrieve.
	 * @return The chunk at the specified index.
	 */	
	public byte[] getChunk(int chunk) {
		return (byte[])chunks.elementAt(chunk);
	}
	
	/**
	 * Returns the number of chunks stored in this 
	 * chunk vector.
	 * 
	 * @return The number of chunks.
	 */
	public int getChunks() {
		return chunks.size();	
	}

	/**
	 * Returns the total length of chunks stored in the
	 * chunk vector.
	 * 
	 * @return The length of the bytes stored in this 
	 * 	vector.
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Removes the specified chunk from the vector.
	 * 
	 * @param chunk The index of the chunk to remove.
	 */
	public void removeChunk(int chunk) {
		Integer element = (Integer)starts.elementAt(chunk);
		starts.removeElement(element);
		int start = element.intValue();
		element = (Integer)ends.elementAt(chunk);
		ends.removeElement(element);
		int end = element.intValue();
		chunks.removeElement(chunks.elementAt(chunk));
		total -= (end - start);
	}
	
	/**
	 * Chops the specified number of bytes from the beginning of
	 * the chunk vector and creates a new vector from that. This
	 * method will also affect this chunk vector since the bytes
	 * will be "virtually" removed. Note that the number of bytes
	 * must be smaller or equal than the total number of bytes of 
	 * this vector otherwise this vector will be fully removed and 
	 * pasted into the other vector. 
	 * 
	 * @param bytes The number of bytes to chop.
	 * @return The new chunk vector containing the first n bytes.
	 */
	public ChunkVector chop(int bytes) {
		ChunkVector result = new ChunkVector();
		if (bytes >= total) {
			result.chunks = chunks;
			result.ends = ends;
			result.starts = starts;
			result.total = total;
			chunks = new Vector();
			ends = new Vector();
			starts = new Vector();
			total = 0;
		} else {
			// first copy chunks (possibly +1 that must be split)
			while (result.total < bytes) {
				Integer istart = ((Integer)starts.elementAt(0));
				starts.removeElement(istart);
				Integer iend = ((Integer)ends.elementAt(0));
				ends.removeElement(iend);
				int start = istart.intValue();
				int end = iend.intValue();
				Object element = chunks.elementAt(0);
				chunks.removeElement(element);
				result.chunks.addElement(element);
				result.starts.addElement(istart);
				result.ends.addElement(iend);
				total -= (end - start);
				result.total += (end - start);							
			}
			// split last chunk if necessary
			if (result.total > bytes) {
				int split = result.total - bytes;
				Integer element = (Integer)result.ends.elementAt(result.ends.size() - 1);
				result.ends.removeElement(element);
				int end = element.intValue();
				Integer iend = new Integer(end - split);
				result.ends.addElement(iend);
				result.total -= split;
				byte[] chunk = (byte[])result.chunks.elementAt
							(result.chunks.size() - 1);
				chunks.insertElementAt(chunk, 0);
				ends.insertElementAt(new Integer(end), 0);
				starts.insertElementAt(iend, 0);
				total += split;
			}
		}	
		return result;
	}

	/**
	 * Returns a byte array that represents all bytes. This method
	 * is very inefficient, it should only be used for debugging.
	 * 
	 * @return The byte array representing this chunk vector.
	 */
	public byte[] getBytes() {
		byte[] bytes = new byte[total];
		int pos = 0;
		for (int i = 0; i < getChunks(); i++) {
			int start = getStart(i);
			int end = getEnd(i);
			byte[] chunk = getChunk(i);
			System.arraycopy(chunk, start, bytes, pos, (end - start));
			pos += (end - start);
		}
		return bytes;
	}
}
