package info.pppc.pcom.system.container.internal.component;

import java.io.IOException;
import java.util.Vector;

import info.pppc.base.system.io.IObjectInput;
import info.pppc.base.system.io.IObjectOutput;
import info.pppc.base.system.io.ISerializable;
import info.pppc.base.system.io.ObjectInputStream;
import info.pppc.base.system.io.ObjectOutputStream;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.container.io.ChunkInputStream;
import info.pppc.pcom.system.container.io.ChunkOutputStream;
import info.pppc.pcom.system.container.io.ChunkVector;
import info.pppc.pcom.system.model.component.IInstanceHistory;

/**
 * The instance history implements the instance history interface.
 * It is used to store outgoing invocations in such a way that
 * they can be replayed on a given component.
 * 
 * @author Mac
 */
public class InstanceHistory implements IInstanceHistory, ISerializable {
	
	/**
	 * The abbreviation for the serialized form of the history.
	 */
	public static final String ABBREVIATION = ";PH";
	
	/**
	 * A vector that contains all entries of the history
	 * in the order in which they should be executed.
	 */
	private Vector entries = new Vector();
	
	/**
	 * A flag that indicates whether the history is enabled.
	 */
	private boolean enabled = false;
	
	/**
	 * The entry is used to store a single invocation in the
	 * history. The actual invocation is not stored only the
	 * arguments of the call and the signature is stored in
	 * order to minimize the memory usage.
	 * 
	 * @author Mac
	 */
	public static class Entry implements IEntry {
		
		/**
		 * A flag that indicates whether the entry should
		 * be executed in parallel with the previous entry.
		 */
		private boolean parallel;
		
		/**
		 * The signature of the method call.
		 */
		private String signature;
		
		/**
		 * The arguments of the method call in serialized
		 * form.
		 */
		private ChunkVector arguments = new ChunkVector();
		
		/**
		 * Creates a new entry with the specified signature and
		 * arguments and a sequence number that denotes manual
		 * creation.
		 * 
		 * @param signature The signature of the method.
		 * @param arguments The arguments of the method.
		 * @throws IOException Thrown if the arguments are not
		 * 	serializable.
		 */
		public Entry(String signature, Object[] arguments) throws IOException {
			this.signature = signature;
			ChunkOutputStream cos = new ChunkOutputStream(this.arguments, false);
			ObjectOutputStream oos = new ObjectOutputStream(cos);
			oos.writeObject(arguments);
			oos.close();
		}
		
		/**
		 * Creates an entry using the specified signature and
		 * no arguments set.
		 * 
		 * @param signature The signature.
		 */
		protected Entry(String signature) {
			this.signature = signature;
		}
		
		/**
		 * Returns the arguments of the method call.
		 * 
		 * @return The arguments of the method call.
		 */
		public Object[] getArguments() {
			try {
				ChunkInputStream cis = new ChunkInputStream(arguments, false);
				ObjectInputStream ois = new ObjectInputStream(cis);
				return (Object[])ois.readObject();				
			} catch (IOException e) {
				// this should not happen, if the deserializer is correct
				Logging.error(getClass(), "Deserialization failed.", e);
				return null;
			}
		}
		
		/**
		 * Returns the signature of the method call.
		 * 
		 * @return The signature of the method call.
		 */
		public String getSignature() {
			return signature;
		}
		
		/**
		 * Determines whether the call must be executed
		 * in parallel with the previous call.
		 * 
		 * @return True if parallel, false otherwise.
		 */
		public boolean isParallel() {
			return parallel;
		}
	
		/**
		 * Marks the entry as parallel or non-parallel.
		 * Parallel entries will be executed in parallel
		 * with the previous entry others will be executed
		 * sequentially.
		 * 
		 * @param parallel A flag to set the parallel state.
		 */
		protected void setParallel(boolean parallel) {
			this.parallel = parallel;
		}
	
		/**
		 * Returns the chunk vector that stores the arguments
		 * in their serialized raw form.
		 * 
		 * @return The chunk vector with the arguments.
		 */
		protected ChunkVector getRawArguments() {
			return arguments;
		}
		
	}
	
	/**
	 * Creates a new instance history.
	 */
	public InstanceHistory() {
		super();
	}
	
	/**
	 * Removes all entries stored in the history.
	 */
	public void clear() {
		entries.removeAllElements();
	}
	
	/**
	 * Creates a new entry with the specified signature and arguments.
	 * 
	 * @param signature The signature.
	 * @param arguments The arguments.
	 * @return The newly created entry.
	 * @throws IOException Thrown if the arguments are not serializable.
	 */
	public IEntry createEntry(String signature, Object[] arguments) throws IOException {
		return new Entry(signature, arguments);
	}

	
	
	/**
	 * Returns the entries of the history in the order in which 
	 * they have been added.
	 * 
	 * @return The entries of the history.
	 */
	public IEntry[] getEntries() {
		IEntry[] result = new IEntry[entries.size()];
		for (int i = entries.size() - 1; i >= 0; i--) {
			result[i] = (IEntry)entries.elementAt(i);
		}
		return result;
	}

	/**
	 * Removes the specified entry from the history or does
	 * nothing if the entry is not part of the history.
	 * 
	 * @param entry The entry to remove.
	 */
	public void removeEntry(IEntry entry) {
		entries.removeElement(entry);
	}

	/**
	 * Enables or disables invocation tracing using the
	 * history.
	 * 
	 * @param enabled True to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Determines whether invocation recording is enabled.
	 * 
	 * @return True if enabled, false otherwise.
	 */
	protected boolean isEnabled() {
		return enabled;
	}

	/**
	 * Adds, moves or manipulates the specified entry. If
	 * the entry is already part of the history, it will be
	 * moved or manipulated. If the entry is not part, it will be 
	 * added.
	 * 
	 * @param entry The entry to set.
	 * @param index The new index of the entry.
	 * @param parallel A flag that indicates whether the entry
	 * 	should be executed in parallel.
	 */
	public void setEntry(IEntry entry, int index, boolean parallel) {
		entries.removeElement(entry);
		Entry e = (Entry)entry;
		e.setParallel(parallel);
		entries.insertElementAt(entry, index);
	}

	/**
	 * Appends the specified entry at the end of the history
	 * and updates the parallel flag.
	 * 
	 * @param entry The entry to add.
	 * @param parallel The parallel flag to set.
	 */
	public void addEntry(IEntry entry, boolean parallel) {
		entries.removeElement(entry);
		Entry e = (Entry)entry;
		e.setParallel(parallel);
		entries.addElement(entry);
	}
	
	/**
	 * Returns the number of entries contained in the history.
	 * 
	 * @return The number of entries contained in the history.
	 */
	public int size() {
		return entries.size();
	}
	
	
	/**
	 * Reads the history from the passed stream.
	 * 
	 * @param input The stream to read from.
	 * @throws IOException Thrown if the deserialization fails.
	 */
	public void readObject(IObjectInput input) throws IOException {
		int ex = input.readInt();
		for (int i = 0; i < ex; i++) {
			Entry e = new Entry(input.readUTF());
			e.setParallel(input.readBoolean());
			ChunkVector cv = e.getRawArguments();
			int todo = input.readInt();
			while (todo > 0) {
				byte[] chunk = new byte[Math.min(2048, todo)];
				input.readBytes(chunk);
				cv.append(chunk);
				todo -= chunk.length;
			}
			entries.addElement(e);
		}
	}
	
	/**
	 * Writes the history to the passed stream.
	 * 
	 * @param output The stream to write to.
	 * @throws IOException Thrown if the serialization fails.
	 */
	public void writeObject(IObjectOutput output) throws IOException {
		output.writeInt(entries.size());
		for (int i = 0; i < entries.size(); i++) {
			Entry e = (Entry)entries.elementAt(i);
			output.writeUTF(e.getSignature());
			output.writeBoolean(e.isParallel());
			ChunkVector cv = e.getRawArguments();
			output.writeInt(cv.getTotal());
			for (int j = 0; j < cv.getChunks(); j++) {
				output.writeBytes(cv.getChunk(j), cv.getStart(j), cv.getEnd(j) - cv.getStart(j));
			}
		}
	}
	
}
