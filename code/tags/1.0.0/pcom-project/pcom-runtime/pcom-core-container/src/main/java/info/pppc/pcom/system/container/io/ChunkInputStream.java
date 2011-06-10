package info.pppc.pcom.system.container.io;

import java.io.IOException;
import java.io.InputStream;


/**
 * An input stream that reads its bytes from a chunk vector.
 * 
 * @author Mac
 */
public class ChunkInputStream extends InputStream {

	/**
	 * The chunk that is read by the stream.
	 */
	protected ChunkVector chunks = null;
	
	/**
	 * The index of the current chunk in the vector.
	 */
	protected int position = 0;
	
	/**
	 * The total number of bytes that has been read so far.
	 */
	protected int total_position = 0;
	
	/**
	 * The current chunk.
	 */ 
	protected byte[] chunk = null;
	
	/**
	 * The start index of the current chunk.
	 */
	protected int start = 0;
	
	/**
	 * The end index of the current chunk.
	 */
	protected int end = 0;

	/**
	 * A flag that determines whether the stream is consuming.
	 */
	protected boolean consume;

	/**
	 * Creates a new input stream that reads its bytes from the specified 
	 * chunk vector. The read operation on the chunk vector can either be
	 * consuming or not. If it is consuming, the chunk vector will be
	 * eventually released.
	 * 
	 * @param chunks The chunk vector that provides the	chunks.
	 * @param consume Determines whether the chunks of the
	 * 	chunk vector will be consumed while they are read. True to
	 * 	consume, false otherwise.
	 */
	public ChunkInputStream(ChunkVector chunks, boolean consume) {
		this.chunks = chunks;
		this.consume = consume;
		if (chunks.getChunks() > 0) {
			chunk = chunks.getChunk(0);
			start = chunks.getStart(0);
			end = chunks.getEnd(0);
			position = 0;
		}
	}

	/**
	 * Reads one byte from the chunk vector.
	 * 
	 * @return The byte to read.
	 * @throws java.io.IOException This is never thrown.
	 */
	public int read() throws IOException {
		while (chunk != null) {
			// is the chunk valid
			if (end - start > 0) {
				// the current chunk has data left
				int result = chunk[start] & 0xFF;
				start += 1;
				total_position += 1;	
				
				return result;
			} else if (chunks.getChunks() > position + 1) {
				// load the next chunk
				position += 1;
				chunk = chunks.getChunk(position);
				start = chunks.getStart(position);
				end = chunks.getEnd(position);
				// free the chunk and adjust counters
				if (consume) {
					position -= 1;
					chunks.removeChunk(position);
					total_position = 0;
				}
				continue;
			} else {
				// invalidate the current chunk
				chunk = null;
				start = 0;
				end = 0;
				// free the last chunk if neccessary
				if (consume) {
					position -= 1;
					chunks.removeChunk(position);
					total_position = 0;
				}
				continue;					
			}
		}
		// return an invalid result
		return -1;			
	}

	/**
	 * Returns the number of available bytes.
	 * 
	 * @return The number of available bytes.
	 * @throws java.io.IOException This is never thrown.
	 */
	public int available() throws IOException {
		return chunks.getTotal() - total_position;
	}


	/**
	 * Reads the specified number of bytes.
	 * 
	 * @param b The location to store the bytes.
	 * @param offset The offset within the location.
	 * @param length The length to read.
	 * @return The number of bytes read.
	 * @throws java.io.IOException This is never thrown.
	 */
	public int read(byte[] b, int offset, int length) throws IOException {
		int total = 0;
		// if chunk is valid, continue
		while (chunk != null) {
			if (end - start > 0) {
				// the current chunk has data left, fill as far as possible
				int l = Math.min(length, (end - start));
				System.arraycopy(chunk, start, b, offset, l);
				length -= l;
				start += l;
				if (length == 0) {
					return l + total;
				} else {
					total += l;
					offset += l;
				}
			} else if (chunks.getChunks() > position + 1) {
				// load the next chunk
				position += 1;
				chunk = chunks.getChunk(position);
				start = chunks.getStart(position);
				end = chunks.getEnd(position);
				// free the chunk and adjust counters
				if (consume) {
					position -= 1;
					chunks.removeChunk(position);
					total_position = 0;
				}
				continue;
			} else {
				// invalidate the current chunk
				chunk = null;
				start = 0;
				end = 0;
				// free the last chunk if neccessary
				if (consume) {
					position -= 1;
					chunks.removeChunk(position);
					total_position = 0;
				}
				continue;					
			}			
		}
		// if the read request was positive and nothing has been read
		// send an eof, else send the read value (can be zero for length
		// equal to zero).
		if (total == 0 && length != 0) {
			throw new IOException("Stream end reached.");
		} else {
			return total;
		}
	}

	/**
	 * Closes the stream and removes any chunks that should be freed.
	 * 
	 * @throws java.io.IOException
	 */
	public void close() throws IOException {
		if (consume) {
			if (end == start && chunk != null) {
				chunks.removeChunk(position);
				total_position = 0;
				start = 0;
				end = 0;
				chunk = null;	
			} else {
				chunks.removeChunk(position);
				chunks.prepend(chunk, start, end);
				total_position = 0;
			}
		}
		super.close();
	}

	/**
	 * Skips the specified number of bytes.
	 * 
	 * @param length The number of bytes to skip.
	 * @return The number of skipped bytes.
	 * @throws java.io.IOException Thrown if something
	 * 	happens.
	 */
	public long skip(long length) throws IOException {
		long total = 0;
		// if chunk is valid, continue
		while (chunk != null) {
			if (end - start > 0) {
				// the current chunk has data left, fill as far as possible
				long l = Math.min(length, (end - start));
				length -= l;
				start += l;
				if (length == 0) {
					return l + total;
				} else {
					total += l;
				}
			} else if (chunks.getChunks() > position + 1) {
				// load the next chunk
				position += 1;
				chunk = chunks.getChunk(position);
				start = chunks.getStart(position);
				end = chunks.getEnd(position);
				// free the chunk and adjust counters
				if (consume) {
					position -= 1;
					chunks.removeChunk(position);
					total_position = 0;
				}
				continue;
			} else {
				// invalidate the current chunk
				chunk = null;
				start = 0;
				end = 0;
				// free the last chunk if neccessary
				if (consume) {
					position -= 1;
					chunks.removeChunk(position);
					total_position = 0;
				}
				continue;					
			}			
		}
		// if the read request was positive and nothing has been read
		// send an eof, else send the read value (can be zero for length
		// equal to zero).
		return total;		
	}

}
