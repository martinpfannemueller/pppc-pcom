package info.pppc.pcom.tutorial.hello;

import info.pppc.base.system.InvocationException;

/**
 * The hello interface is the interface that is provided by hello instances.
 * It enables the using component to print a string.
 * 
 * @author Mac
 */
public interface IHello {

	/**
	 * Prints a string to the standard output.
	 * 
	 * @param s The string to print.
	 * @throws InvocationException Thrown if the call fails.
	 */
	public void println(String s) throws InvocationException;
	
}
