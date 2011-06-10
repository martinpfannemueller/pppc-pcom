package info.pppc.pcom.system.model.component;

import java.io.IOException;

/**
 * The instance history is used to log the invocations that
 * have been sent to some instance. The history contains entries
 * that that can be reordered and changed. The history can also
 * be cleared and enabled or disabled.
 * 
 * @author Mac
 */
public interface IInstanceHistory {

	/**
	 * An entry of the instance history.
	 * 
	 * @author Mac
	 */
	public interface IEntry {
		
		/**
		 * The signature of the method that is represented
		 * by this entry.
		 * 
		 * @return The signature of the method.
		 */
		public String getSignature();
		
		/**
		 * Returns the arguments of the method that is 
		 * represented by this entry.
		 * 
		 * @return The arguments represented by this entry.
		 */
		public Object[] getArguments();
		
		/**
		 * A flag that indicates whether this call should be
		 * executed in parallel with the previous entry.
		 * 
		 * @return True if parallel, false if sequentially.
		 */
		public boolean isParallel();
	}
	
	/**
	 * Creates a new entry that is not part of the history using
	 * the specified signature and arguments. The arguments must
	 * be serializable.
	 * 
	 * @param signature The signature of the call.
	 * @param arguments The arguments of the call.
	 * @return The new history entry.
	 * @throws IOException Thrown if the arguments passed to the 
	 * 	method are not serializable.
	 */
	public IEntry createEntry(String signature, Object[] arguments) throws IOException;
	
	
	/**
	 * Returns the number of entries contained in the history.
	 * 
	 * @return The number of entries contained in the history.
	 */
	public int size();
	
	/**
	 * Appends the specified entry at the end of the history
	 * and updates the parallel flag. If the entry has already 
	 * been part of the history, it will be removed before it 
	 * is added again.
	 * 
	 * @param entry The entry to add.
	 * @param parallel A flag that indicates whether the entry
	 * 	should be executed in parallel with the previous entry.
	 */
	public void addEntry(IEntry entry, boolean parallel);
	
	/**
	 * Adds the specified history entry to the list of entries
	 * at the specified position. If the parallel flag is set, the
	 * call will be executed in parallel with the previous call.
	 * If the entry has already been part of the history, it will
	 * be removed before it is added again.
	 * 
	 * @param entry The entry that should be manipulated.
	 * @param index The new index of the entry.
	 * @param parallel A flag that indicates whether the entry
	 * 	should be executed in parallel with the previous entry.
	 */
	public void setEntry(IEntry entry, int index, boolean parallel);
	
	/**
	 * Removes the specified entry from the history. If the
	 * entry is not part of the history, the method will do
	 * nothing.
	 * 
	 * @param entry The entry that should be removed.
	 */
	public void removeEntry(IEntry entry);
	
	/**
	 * Returns all entries of the history in the order in
	 * which they should be executed.
	 * 
	 * @return The entries of the history in the order in
	 * 	which they should be executed.
	 */
	public IEntry[] getEntries();
	
	/**
	 * Clears the history by removing all (!) entries
	 * from the history. 
	 */
	public void clear();
	
	/**
	 * Enables or disables invocation tracking in the
	 * history. Calls that are currently executed will
	 * not be affected by setting this value. All calls
	 * that are executed after the value has been set
	 * will be affected.
	 * 
	 * @param enabled True to enable, false to disable.
	 */
	public void setEnabled(boolean enabled);
	
}
