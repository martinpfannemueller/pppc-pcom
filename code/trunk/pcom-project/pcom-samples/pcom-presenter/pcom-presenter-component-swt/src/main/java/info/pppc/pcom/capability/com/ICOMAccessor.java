package info.pppc.pcom.capability.com;

/**
 * The ms com interface enables users to interface with Microsoft COM using the 
 * bridge 2 java COM bridge. Using the methods provided in this interface users 
 * can schedule the execution of commands.
 * These commands will be executed within the thread that loaded the dll. This is
 * neccessary since the library is single-threaded and requires that all calls are 
 * made using the same thread.
 * 
 * @author Mac
 */
public interface ICOMAccessor {

	/**
	 * Executes the specified command synchronously and returns any exceptions
	 * thrown by the command as an com exception with the specified reason. If
	 * the calling thread is interrupted before the command is executed completely,
	 * the call will throw an interrupted exception.
	 * 
	 * @param command The command that should be executed.
	 * @throws COMException Thrown if the command throws an exception.
	 * 	The reason of the exception will be the origianl exception.
	 * @throws InterruptedException Thrown if the thread is interrupted
	 * 	before the command has been executed completely.
	 * @throws IllegalStateException Thrown if the allocator that created the
	 * 	accessor has been stopped.
	 */
	public void runSynchronous(ICOMCommand command) throws 
		COMException, InterruptedException, IllegalStateException;
	
	/**
	 * Runs the specified command asynchronously. Exceptions thrown by the
	 * command will be dropped silently.
	 * 
	 * @param command The command that should be executed.
	 * @throws IllegalStateException Thrown if the allocator that created
	 * 	the accessor has been stopped.
	 */
	public void runAsynchronous(ICOMCommand command) throws IllegalStateException;
	
}
