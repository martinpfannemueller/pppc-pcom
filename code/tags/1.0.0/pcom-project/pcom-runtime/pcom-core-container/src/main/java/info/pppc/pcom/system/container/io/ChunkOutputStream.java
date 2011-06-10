package info.pppc.pcom.system.container.io;

import java.io.IOException;
import java.io.OutputStream;


/**
 * An output stream that writes its bytes to a chunk vector.
 * 
 * @author Mac
 */
public class ChunkOutputStream extends OutputStream {

	/**
	 * The default chunk size in bytes.
	 */
	public static final int CHUNK_SIZE = 2048;

	/**
	 * The maximum number of bytes that are copied every operation
	 * that writes an byte array and that has a size that is larger
	 * than the specified size will not be copied into an existing
	 * chunk, but a new one will be created.
	 */
	public static final int CHUNK_COPY = 1024;

	/**
	 * Determines whether all bytes should be copied.
	 */
	protected boolean copy = false;
	
	/**
	 * The chunk vector to write to.
	 */
	protected ChunkVector chunks = null;

	/**
	 * The chunk that is currently written.
	 */
	protected byte[] chunk = null;
	
	/**
	 * The start position of the current chunk.
	 */
	protected int start = 0;
	
	/**
	 * The end position of the current chunk.
	 */
	protected int end = 0;

	/**
	 * The size of the current chunk.
	 */
	protected int size = 0;

	/**
	 * Creates a new output stream that writes its bytes to
	 * the specified chunk vector. The flag determines whether
	 * all bytes should be copied.
	 * 
	 * @param chunks The chunk vector to fill.
	 * @param copy Determines whether all bytes should be copied.
	 */
	public ChunkOutputStream(ChunkVector chunks, boolean copy) {
		this.chunks = chunks;
		this.copy = copy;
		chunk = new byte[CHUNK_SIZE];
		start = 0;
		end = 0;
		size = CHUNK_SIZE;
	}

	/**
	 * Writes one byte to the chunk vector.
	 * 
	 * @param oneByte The byte to write.
	 * @throws java.io.IOException Thrown if an error occurs.
	 */
	public void write(int oneByte) throws IOException {
		if (end >= size) {
			chunks.append(chunk, start, end);
			chunk = new byte[CHUNK_SIZE];
			start = 0;
			end = 0;
			size = CHUNK_SIZE;
		}			
		chunk[end] = (byte)oneByte;
		end += 1;
	}
	
	/**
	 * Writes the specified part of the array to the chunk vector.
	 * 
	 * @param buffer The buffer.
	 * @param offset The start index.
	 * @param count The number of bytes to write.
	 * @throws java.io.IOException This will never be thrown.
	 */
	public void write(byte[] buffer, int offset, int count)	throws IOException {
		if (copy || count < CHUNK_COPY) {
			int length = Math.min(count, size - end);
			System.arraycopy(buffer, offset, chunk, end, length);
			end += length;
			while (length != count) {
				chunks.append(chunk, start, end);
				chunk = new byte[CHUNK_SIZE];
				start = 0;
				end = 0;
				size = CHUNK_SIZE;			
				int l = Math.min(count - length, size);
				System.arraycopy(buffer, offset + length, chunk, end, l);
				end += l;
				length += l;
			}
		} else {
			chunks.append(chunk, start, end);
			chunks.append(buffer, offset, offset + count);
			start = end;			
		}
	}

	
	/**
	 * Closes the output stream. Note that this method does not
	 * flush the stream. Users should always flush the stream 
	 * before closing it.
	 * 
	 * @throws java.io.IOException This will never be thrown.
	 */
	public void close() throws IOException {
		super.close();
	}

	/**
	 * Flushes the stream by writing anything into the chunk vector
	 * that has not been written yet. It is not a good idea to
	 * flush to often since flushing might waste memory.
	 * 
	 * @throws java.io.IOException This will never be thrown.
	 */
	public void flush() throws IOException {
		if (end - start > 0) {
			chunks.append(chunk, start, end);
			start = end;			
		}	
		super.flush();
	}


}
