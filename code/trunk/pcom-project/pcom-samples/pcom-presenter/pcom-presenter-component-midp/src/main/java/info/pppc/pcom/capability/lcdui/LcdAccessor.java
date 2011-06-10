package info.pppc.pcom.capability.lcdui;

import java.util.Vector;

import info.pppc.base.lcdui.Application;
import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.util.Logging;

/**
 * The ui accessor provides checked access to the base/pcom ui. The
 * accessor stores references to all elements and contributions that
 * have been added by the user and if the accessor is disposed, it 
 * will automatically remove all elements that still remain.
 * 
 * @author Mac
 */
public class LcdAccessor implements ILcdAccessor {

	/**
	 * The application of the ui accessor.
	 */
	protected final Application application;
	
	/**
	 * A flag that indicates whether the accessor is disposed.
	 */
	protected boolean disposed = false;
	
	/**
	 * The actions that have been created and added by the 
	 * element that uses this accessor.
	 */
	protected Vector actions = new Vector();
	
	/**
	 * The elements that have been created and added by the element
	 * that uses this accessor.
	 */
	protected Vector elements = new Vector();
	
	/**
	 * A counter that counts the number of threads that are 
	 * currently inside the accessor.
	 */
	protected int inside = 0;
	
	/**
	 * Creates a new ui accessor for the specified application.
	 * 
	 * @param application The application that will be used as
	 * 	element manager.
	 */
	protected LcdAccessor(Application application) {
		this.application = application;
	}
	
	/**
	 * Disposes all elements created by the accessor. 
	 */
	protected synchronized void dispose() {
		if (disposed) return;
		try {
			while (inside != 0) {
				wait();
			}			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
		disposed = true;
		application.run(new Runnable() {
			public void run() {
				for (int i = 0; i < actions.size(); i++) {
					ElementAction c = (ElementAction)actions.elementAt(i);
					application.removeAction(c);
				}
				actions.removeAllElements();
				for (int i = 0; i < elements.size(); i++) {
					AbstractElement e = (AbstractElement)elements.elementAt(i);
					application.removeElement(e);	
				}
				elements.removeAllElements();			
			}
		});
	}

	/**
	 * Adds the specified action to the menu.
	 * 
	 * @param action The action to add.
	 */
	public void addAction(ElementAction action) {
		enter();
		try {
			application.addAction(action);
			actions.addElement(action);
		} finally {
			exit();
		}
		
	}

	/**
	 * Removes the specified action from the menu.
	 * 
	 * @param action The action to remove.
	 */
	public void removeAction(ElementAction action) {
		enter();
		try {
			application.removeAction(action);
			actions.removeElement(action);
		} finally {
			exit();
		}
	}

	/**
	 * Returns the elements that are currently open.
	 * 
	 * @return The elements that are open.
	 */
	public AbstractElement[] getElements() {
		enter();
		try {
			return application.getElements();
		} finally {
			exit();
		}
	}

	/**
	 * Returns the element with the specified name.
	 * 
	 * @param name The name of the element to retrieve.
	 * @return The element with the specified name.
	 */
	public AbstractElement getElement(String name) {
		enter();
		try {
			return application.getElement(name);
		} finally {
			exit();
		}
	}

	/**
	 * Focuses the specified element.
	 * 
	 * @param element The element to focus.
	 */
	public void focusElement(AbstractElement element) {
		enter();
		try {
			application.focusElement(element);
		} finally {
			exit();
		}
	}

	/**
	 * Adds the specified element.
	 * 
	 * @param element The element to add.
	 * @return True if successful, false otherwise.
	 */
	public boolean addElement(AbstractElement element) {
		enter();
		try {
			if (application.addElement(element)) {
				elements.addElement(element);
				return true;
			} else {
				return false;
			}
		} finally {
			exit();
		}
	}

	/**
	 * Removes the specified element from the view.
	 * 
	 * @param element The element to remove.
	 * @return True if successful, false otherwise.
	 */
	public boolean removeElement(AbstractElement element) {
		enter();
		try {
			elements.removeElement(element);
			return application.removeElement(element);
		} finally {
			exit();
		}
	}

	/**
	 * Updates the specified element if it is focused
	 * at the moment. If the element is not part of the
	 * application or not focused, this method does nothing.
	 * 
	 * @param element The element to update if focused.
	 */
	public void updateElement(AbstractElement element) {
		enter();
		try {
			application.updateElement(element);
		} finally {
			exit();
		}
	}

	/**
	 * Executes the specified runable within the display thread.
	 * 
	 * @param runable The runable to execute.
	 */
	public void run(Runnable runable) {
		enter();
		try {
			application.run(runable);
		} finally {
			exit();
		}
	}

	/**
	 * Executes the specified runable with a progress bar.
	 * 
	 * @param runnable The runnable to execute.
	 * @param cancel True if the operation can be canceled.
	 * @throws InterruptedException Thrown if the thread
	 * 	got interrupted.
	 */
	public void run(IOperation runnable, boolean cancel) throws InterruptedException {
		enter();
		try {
			application.run(runnable, cancel);
		} finally {
			exit();
		}
	}
	
	/**
	 * Returns the height of the display.
	 * 
	 * @return The height of the display.
	 */
	public int getDisplayHeight() {
		enter();
		try {
			return application.getDisplayHeight();
		} finally {
			exit();
		}
	}
	
	/**
	 * Returns the width of the display.
	 * 
	 * @return The width of the display.
	 */
	public int getDisplayWidth() {
		enter();
		try {
			return application.getDisplayWidth();
		} finally {
			exit();
		}
	}
	
	
	/**
	 * Called whenever a thread enters the accessor. Throws
	 * an exception if the accessor is disposed. Increments
	 * the inside counter if not. 
	 */
	private synchronized void enter() {
		if (disposed) {
			throw new IllegalStateException("UI accessor is disposed.");
		}
		inside += 1;
	}
	
	/**
	 * Called whenver a thread leaves the accessor. Decrements
	 * the inside counter and notifies all waiting threads if
	 * the inside counter returns to 0.
	 */
	private synchronized void exit() {
		inside -= 1;
		if (inside == 0) notifyAll();
	}
	
}
