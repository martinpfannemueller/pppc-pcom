package info.pppc.pcom.swtui.container.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.ContainerControl;
import info.pppc.pcom.system.container.ContainerProxy;
import info.pppc.pcom.system.container.IContainer;

/**
 * The abstract element action is the base class of actions that manipulate
 * the container represented by the control.
 * 
 * @author Mac
 */
public abstract class AbstractElementAction extends Action {

	/**
	 * The text that will be displayed as error message for invocation exceptions.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.AbstractElementAction.TEXT";
	
	/**
	 * The text that will be displayed as error message header.
	 */
	private static final String UI_HEAD = "info.pppc.pcom.swtui.container.action.AbstractElementAction.HEAD";
	
	/**
	 * The container control.
	 */
	private ContainerControl control;
	
	/**
	 * The id of the element.
	 */
	private ObjectID elementID;
	
	/**
	 * The taskname that will be shown while the action is executed.
	 */
	private String taskName;
	
	/**
	 * Creates a new abstract element control for the specified container
	 * control on the specified element.
	 * 
	 * @param name The name of the action as shown by the ui.
	 * @param descriptor The image descriptor of the image shown by the ui.
	 * @param taskName The taskname that will be displayed as long as the action
	 * 	is executed.
	 * @param control The container control used to derive container options.
	 * @param elementID The id of the corresponding element.
	 */
	public AbstractElementAction(String name, ImageDescriptor descriptor, String taskName, 
			ContainerControl control, ObjectID elementID) {
		super(name, descriptor);
		this.taskName = taskName;
		this.control = control;
		this.elementID = elementID;
	}

	/**
	 * Called whenever the action is executed. This will
	 * create a container proxy using the container control
	 * and it will safely execute the abstract invoke method.
	 */
	public void run() {
		IElementManager manager = control.getManager();
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(taskName + "...", 2);
					monitor.worked(1);
					ContainerProxy proxy = new ContainerProxy();
					proxy.setSourceID(new ReferenceID(SystemID.SYSTEM));
					proxy.setTargetID(new ReferenceID(control.getSystemID(), IContainer.CONTAINER_ID));
					try {
						invoke(proxy, elementID);
						monitor.worked(1);
						monitor.done();
					} catch (InvocationException e) {
						throw new InvocationTargetException(e);
					}					
				}
			}, false);			
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof InvocationException) {
				MessageBox b = new MessageBox(control.getShell(), SWT.ICON_WARNING | SWT.OK | SWT.APPLICATION_MODAL);
				b.setText(PcomUI.getText(UI_HEAD));
				b.setMessage(PcomUI.getText(UI_TEXT) + " (" + t.getMessage() + ").");
				b.open();
			} else {
				Logging.error(getClass(), "Caught an unknown exception.", t);
			}
		} catch (Throwable t) {
			Logging.error(getClass(), "Caught an unknown exception.", t);
		}
	}
	
	/**
	 * This method is called whenever the action is executed. This abstract
	 * class will handle failures and display an informative message if an
	 * invocation exception occurs. Note that this method is not called 
	 * within the gui thread.
	 * 
	 * @param container The container used to execute the action.
	 * @param elementID The object id of the targeted element.
	 * @throws InvocationException Thrown if the remote call fails. This
	 * 	exception will be caught gracefully.
	 */
	public abstract void invoke(IContainer container, ObjectID elementID) throws InvocationException;
	
}
