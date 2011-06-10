package info.pppc.pcom.system.model.component;

import info.pppc.pcom.system.model.capability.IAllocatorTemplate;

/**
 * The factory description is the basic description interface that is provided to 
 * factories and instances. Using this description, they can retrieve a writable 
 * view that enables them to manipulate the description that models their requirements 
 * towards the executing container. Essentially, they can either request resources 
 * using writers. This is only a marker interface that inherits its functionality
 * from the resource allocator templates. 
 * 
 * @author Mac
 */
public interface IFactoryTemplate extends IAllocatorTemplate {

}
