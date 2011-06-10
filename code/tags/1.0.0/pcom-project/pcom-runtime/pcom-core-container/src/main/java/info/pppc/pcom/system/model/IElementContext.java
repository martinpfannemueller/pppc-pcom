package info.pppc.pcom.system.model;

import info.pppc.base.system.operation.IOperator;

/**
 * The element context is the base interface for context object issued
 * by the container. Each element that receives a context object can use
 * the context object to initiate threads whose failure is fatal. In order
 * to ease debugging, each element should only rely on such threads.
 * 
 * @author Mac
 */
public interface IElementContext extends IOperator {
		
}
