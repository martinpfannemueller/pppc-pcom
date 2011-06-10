package info.pppc.pcom.component.powerpoint.common;

import info.pppc.pcom.capability.com.ICOMCommand;

/**
 * The powerpoint command is used by powerpoint to interface
 * with the com bridge in a safe, single threaded way.
 * 
 * @author Mac
 */
public class PowerpointCommand implements ICOMCommand {

	/**
	 * The result that has been created.
	 */
	private Object result;
	
	/**
	 * Creates a new command.
	 */
	public PowerpointCommand() {
		super();
	}

	/**
	 * Returns the result of the call or null if none.
	 * 
	 * @return Returns the result.
	 */
	protected Object getResult() {
		return result;
	}

	/**
	 * Runs the run with result method and sets the
	 * return value as result. Overwrite this method
	 * if the method does not have a result.
	 * 
	 *  @throws Throwable Thrown if something fails.
	 */
	public void run() throws Throwable {
		result = runResult();
		
	}
	
	/**
	 * Returns null. Overwrite this method if the
	 * method does have a result.
	 * 
	 * @return The result of th method.
	 * @throws Throwable Thrown if something fails.
	 */
	public Object runResult() throws Throwable {
		return null;
	}

	
	
}
