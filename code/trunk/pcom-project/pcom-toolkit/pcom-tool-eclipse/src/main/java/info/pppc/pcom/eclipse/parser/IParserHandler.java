package info.pppc.pcom.eclipse.parser;

/**
 * The parser handler can be used to observer the status of the parser
 * at runtime. The parser will first call the begin method that signals
 * the total amount of work to do, then it will call the step method
 * whenever a task has performed. The step method signals the performed
 * amount of work and it has a name that describes the current step.
 * Whenever the parser finishes its processing, it calls the finish
 * method.
 * 
 * @author Mac
 */
public interface IParserHandler {

	/**
	 * Called whenever a parsing process starts.
	 * 
	 * @param name The name of the current task that has begun.
	 * @param total The total amount of work to do.
	 * @return Return true to continue, false to abort.
	 */
	public boolean begin(String name, int total);
	
	/**
	 * Called whenever the parsing process has some work done. 
	 * 
	 * @param work The number of steps performed (not cummulative).
	 * @param name The name of the current task that has begun.
	 * @return Return true to continue, false to abort.
	 */
	public boolean step(String name, int work);
	
	/**
	 * Called whenever the parsing process has finished.
	 * This is also called if the process finshes due to some
	 * false return value within the handler.
	 */
	public void finish();

}
