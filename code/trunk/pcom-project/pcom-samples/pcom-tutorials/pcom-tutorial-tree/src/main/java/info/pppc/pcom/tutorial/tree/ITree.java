package info.pppc.pcom.tutorial.tree;

import info.pppc.base.system.InvocationException;

/**
 * The tree interface is the interface for all tree components.
 * A tree component can print a string and can send the string
 * its children.
 * 
 * @author Mac
 */
public interface ITree {

	/**
	 * Prints a string to the standard output and calls print
	 * on all children using the same string.
	 * 
	 * @param s The string to print and to forward to all
	 * 	children.
	 * @throws InvocationException Thrown if the call could not
	 * 	be performed.
	 */
	public void println(String s) throws InvocationException;
	
}
