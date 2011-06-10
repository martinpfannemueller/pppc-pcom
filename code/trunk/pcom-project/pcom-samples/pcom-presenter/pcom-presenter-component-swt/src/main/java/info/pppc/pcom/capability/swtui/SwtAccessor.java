package info.pppc.pcom.capability.swtui;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.operation.IRunnableWithProgress;

import info.pppc.base.swtui.Application;
import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.system.util.Logging;

/**
 * The ui accessor provides checked access to the base/pcom ui. The
 * accessor stores references to all elements and contributions that
 * have been added by the user and if the accessor is disposed, it 
 * will automatically remove all elements that still remain.
 * 
 * @author Mac
 */
public class SwtAccessor implements ISwtAccessor {

	/**
	 * The application of the ui accessor.
	 */
	protected final Application application;
	
	/**
	 * A flag that indicates whether the accessor is disposed.
	 */
	protected boolean disposed = false;
	
	/**
	 * The contributions that have been created and added by the 
	 * element that uses this accessor.
	 */
	protected Vector contributions = new Vector();
	
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
	protected SwtAccessor(Application application) {
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
		for (int i = 0; i < contributions.size(); i++) {
			IContributionItem c = (IContributionItem)contributions.elementAt(i);
			application.removeContribution(c);
		}
		contributions.removeAllElements();
		for (int i = 0; i < elements.size(); i++) {
			AbstractElementControl e = (AbstractElementControl)elements.elementAt(i);
			if (application.getShell() != null && ! application.getShell().isDisposed()) {
				application.removeElement(e);	
			}
		}
		elements.removeAllElements();
	}

	/**
	 * Adds the specified contribution to the menu.
	 * 
	 * @param contribution The contribution to add.
	 */
	public void addContribution(IContributionItem contribution) {
		enter();
		try {
			application.addContribution(contribution);
			contributions.addElement(contribution);
		} finally {
			exit();
		}
		
	}

	/**
	 * Removes the specified contribution from the menu.
	 * 
	 * @param contribution The contribution to remove.
	 */
	public void removeContribution(IContributionItem contribution) {
		enter();
		try {
			application.removeContribution(contribution);
			contributions.removeElement(contribution);
		} finally {
			exit();
		}
	}

	/**
	 * Returns the elements that are currently open.
	 * 
	 * @return The elements that are open.
	 */
	public AbstractElementControl[] getElements() {
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
	public AbstractElementControl getElement(String name) {
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
	public void focusElement(AbstractElementControl element) {
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
	public boolean addElement(AbstractElementControl element) {
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
	public boolean removeElement(AbstractElementControl element) {
		enter();
		try {
			elements.removeElement(element);
			return application.removeElement(element);
		} finally {
			exit();
		}
	}

	/**
	 * Updates the specified element.
	 */
	public void updateElement() {
		enter();
		try {
			application.updateElement();
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
	 * @throws InvocationTargetException Thrown if the
	 * 	runnable throws an exception.
	 * @throws InterruptedException Thrown if the thread
	 * 	got interrupted.
	 */
	public void run(IRunnableWithProgress runnable, boolean cancel) throws InvocationTargetException, InterruptedException {
		enter();
		try {
			application.run(runnable, cancel);
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
