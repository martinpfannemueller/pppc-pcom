package info.pppc.pcom.swtui.application;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.element.action.RemoveAction;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.action.CreateAction;
import info.pppc.pcom.swtui.application.viewer.ApplicationViewer;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.application.ApplicationManager;

/**
 * The application control is a custom control that enables users to browse
 * the local application manager. It can be used to create new applications
 * and to start/stop and save exsiting applications.
 * 
 * @author Mac
 */
public class ApplicationControl extends AbstractElementControl {

	/**
	 * The resource key for the string used as control name.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.application.ApplicationControl.TEXT";

	/**
	 * The resource key for the string shown when an application is started.
	 */
	private static final String UI_START = "info.pppc.pcom.swtui.application.ApplicationControl.START";

	/**
	 * The resource key for the string shown when an application is stopped.
	 */
	private static final String UI_STOP = "info.pppc.pcom.swtui.application.ApplicationControl.STOP";
	
	/**
	 * The resource key for the string shown when an application is added.
	 */
	private static final String UI_ADD = "info.pppc.pcom.swtui.application.ApplicationControl.ADD";
	
	/**
	 * The resource key for the string shown when an application is removed.
	 */
	private static final String UI_REMOVE = "info.pppc.pcom.swtui.application.ApplicationControl.REMOVE";
	
	/**
	 * The resource key for the string shown when an application is updated.
	 */
	private static final String UI_UPDATE = "info.pppc.pcom.swtui.application.ApplicationControl.UPDATE";

	/**
	 * The resource key for the string shown when an application is adapted.
	 */
	private static final String UI_CHANGE = "info.pppc.pcom.swtui.application.ApplicationControl.CHANGE";

	/**
	 * The resource key for the string shown when an application is saved.
	 */
	private static final String UI_SAVE = "info.pppc.pcom.swtui.application.ApplicationControl.SAVE";

	/**
	 * The resource key for the string shown when an application is saved.
	 */
	private static final String UI_LOAD = "info.pppc.pcom.swtui.application.ApplicationControl.LOAD";

	
	/**
	 * The application viewer used to display the stored 
	 * applications.
	 */
	protected ApplicationViewer viewer;
	
	/**
	 * Contains the application descriptor to application adaptors.
	 */
	protected Vector adapters = new Vector();
	
	/**
	 * A reference to the application manager that is controlled by this control.
	 */
	protected ApplicationManager applicationManager = ApplicationManager.getInstance();
	
	/**
	 * Called whenever the state of the application manager changes as long
	 * as the control is not disposed. 
	 */
	protected IListener update = new IListener() {
		public void handleEvent(Event event) {
			final ApplicationDescriptor descriptor = (ApplicationDescriptor)event.getData();
			switch (event.getType()) {
				case ApplicationManager.EVENT_APPLICATION_ADDED:
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							ApplicationAdapter adapter = new ApplicationAdapter
								(ApplicationControl.this);
							adapter.setDescriptor(descriptor);
							adapters.addElement(adapter);
							viewer.addApplication(adapter);
							getManager().updateElement();
						}
					});
					break;
				case ApplicationManager.EVENT_APPLICATION_REMOVED:
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							for (int i = 0; i < adapters.size(); i++) {
								ApplicationAdapter adapter = 
									(ApplicationAdapter)adapters.elementAt(i);
								if (adapter.getDescriptor().getApplicationID().
										equals(descriptor.getApplicationID())) {
									viewer.removeApplication(adapter);
									adapters.removeElementAt(i);
									break;
								}
							}
							getManager().updateElement();
						};
					});
					break;
				case ApplicationManager.EVENT_APPLICATION_STARTED:
				case ApplicationManager.EVENT_APPLICATION_STOPPED:
				case ApplicationManager.EVENT_APPLICATION_PAUSED:
				case ApplicationManager.EVENT_APPLICATION_CHANGED:
					if (descriptor != null) {
						getDisplay().asyncExec(new Runnable() {
							public void run() {
								for (int i = 0; i < adapters.size(); i++) {
									ApplicationAdapter adapter = (ApplicationAdapter)adapters.elementAt(i);
									if (adapter.getDescriptor().getApplicationID().equals
											(descriptor.getApplicationID())) {
										adapter.setDescriptor(descriptor);
										viewer.update(adapter);
										break;
									}
								}	
								getManager().updateElement();
							}
						});						
					}
					break;
				default:
					// should never happen, ignore
			}
		};
	};
	
	/**
	 * Creates a new application browser using the specified
	 * manager.
	 * 
	 * @param manager The manager that manages the ui.
	 */
	public ApplicationControl(IElementManager manager) {
		super(manager);
	}

	/**
	 * Returns the image for the application browser.
	 * 
	 * @return The image for the application browser.
	 */
	public Image getImage() {
		return BaseUI.getImage(BaseUI.IMAGE_LOGO);
	}
	
	/**
	 * Retruns the name of the application browser.
	 * 
	 * @return The name of the application browser.
	 */
	public String getName() {
		return PcomUI.getText(UI_TEXT);
	}
	
	/**
	 * Called whenever the control needs to be displayed.
	 * 
	 * @param parent The parent composite of the control
	 */
	public void showControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout());
		setControl(c);
		viewer = new ApplicationViewer(c, SWT.NONE);
		viewer.addListener(ApplicationViewer.EVENT_SELECTED, new IListener() {
			public void handleEvent(Event event) {
				getManager().updateElement();
			}
		});
		// add elements to application viewer
		Vector applications = applicationManager.getApplications();
		for (int i = 0; i < applications.size(); i++) {
			ApplicationAdapter adapter = new ApplicationAdapter(this);
			ObjectID applicationID = (ObjectID)applications.elementAt(i);
			ApplicationDescriptor descriptor = applicationManager.queryApplication(applicationID);
			if (descriptor != null) {
				adapter.setDescriptor(descriptor);
				adapters.addElement(adapter);
				viewer.addApplication(adapter);				
			}
		}
		// add the listeners to refresh the pane automatically
		applicationManager.addApplicationListener(Event.EVENT_EVERYTHING, update);
	}
	
	/**
	 * Returns the menu actions for the control. 
	 * 
	 * @return The menu actions for the control.
	 */
	public Action[] getMenuActions() {
		ApplicationAdapter adapter = (ApplicationAdapter)viewer.getSelection();
		if (adapter != null) {
			Action[] actions = adapter.getMenuActions();
			Action[] result = new Action[actions.length + 4];
			result[0] = new CreateAction(this);
			result[1] = null;
			System.arraycopy(actions, 0, result, 2, actions.length);
			result[actions.length + 2] = null;
			result[actions.length + 3] = new RemoveAction(this, getManager());
			return result;
		} else {
			return new Action[] {
				new CreateAction(this),
				null,
				new RemoveAction(this, getManager())
			};			
		}
	}
	
	/**
	 * Disposes the control as well as the adapters since they 
	 * may contain cached image data.
	 */
	public void disposeControl() {
		// remove the listeners to update the pane
		applicationManager.removeApplicationListener(Event.EVENT_EVERYTHING, update);
		super.disposeControl();
		for (int i = adapters.size() - 1; i >= 0; i--) {
			ApplicationAdapter adapter = (ApplicationAdapter)adapters.elementAt(i);
			adapters.removeElementAt(i);
			adapter.dispose();
		}
	}
	
	/**
	 * Called whenever an application builder control has finished the build
	 * process. I.e. if the user has clicked the ok button.
	 * 
	 * @param descriptor The descriptor that has changed.
	 */
	public void updateApplication(final ApplicationDescriptor descriptor) {
		final boolean add = (descriptor.getApplicationID() == null);
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(add?UI_ADD:UI_UPDATE), 2);
					monitor.worked(1);
					if (add) {
						applicationManager.addApplication(descriptor);
					} else {
						applicationManager.updateAppliction(descriptor);
					}					
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while " + (add?"adding.":"updating."), e);
		}
	}
	
	/**
	 * A call to this method will cause the application bound to the descriptor to 
	 * perform a reconfiguration if the application is currently still executed.
	 * Otherwise, the application will simply do nothing.
	 * 
	 * @param descriptor The descriptor that points to the application to change.
	 */
	public void changeApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_CHANGE), 2);
					monitor.worked(1);
					applicationManager.changeApplication(descriptor.getApplicationID());
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while changing.", e);
		}		
	}
	
	/**
	 * Called whenever a start action is executed on some descriptor.
	 * 
	 * @param descriptor The descriptor that describes the application
	 * 	to start.
	 */
	public void startApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_START), 2);
					monitor.worked(1);
					applicationManager.startApplication(descriptor.getApplicationID());
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while starting.", e);
		}
	}

	/**
	 * Called whenever a stop action is executed on some descriptor.
	 * 
	 * @param descriptor The descriptor that describes the application
	 * 	to stop.
	 */

	public void stopApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_STOP), 2);
					monitor.worked(1);
					applicationManager.exitApplication(descriptor.getApplicationID());
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while stopping.", e);
		}
	}
	
	/**
	 * Called whenever a save action is executed on some descriptor.
	 * 
	 * @param descriptor The descriptor that describes the application
	 * 	to save.
	 */

	public void saveApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_SAVE), 2);
					monitor.worked(1);
					applicationManager.saveApplication(descriptor.getApplicationID());
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while saving.", e);
		}
	}
	
	/**
	 * Called whenever a load action is executed on some descriptor.
	 * 
	 * @param descriptor The descriptor that describes the application
	 * 	to load.
	 */

	public void loadApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_LOAD), 2);
					monitor.worked(1);
					applicationManager.loadApplication(descriptor.getApplicationID());
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while saving.", e);
		}
	}
	
	/**
	 * Called whenever a delete action is executed on some descriptor.
	 * 
	 * @param descriptor The descriptor that describes the application
	 * 	to delete.
	 */
	public void deleteApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_REMOVE), 2);
					monitor.worked(1);
					applicationManager.removeApplication(descriptor.getApplicationID());
					monitor.worked(1);
					monitor.done();
				}
			}, false);			
		} catch (Exception e) {
			Logging.error(getClass(), "Exception while removing.", e);
		}
	}

}
