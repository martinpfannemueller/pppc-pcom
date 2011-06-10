package info.pppc.pcom.component.presenter;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.component.filesystem.File;
import info.pppc.pcom.component.filesystem.FileException;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.component.presenter.swtui.FilesystemContentProvider;
import info.pppc.pcom.component.presenter.swtui.FilesystemDialog;
import info.pppc.pcom.component.presenter.swtui.FilesystemLabelProvider;
import info.pppc.pcom.component.presenter.swtui.PresenterControl;
import info.pppc.pcom.component.presenter.swtui.PresenterUI;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceRestorer;
import info.pppc.pcom.system.model.contract.reader.IDimensionProvisionReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureProvisionReader;
import info.pppc.pcom.system.model.contract.reader.IInstanceProvisionReader;
import info.pppc.pcom.system.model.contract.reader.ITypeProvisionReader;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IFeatureProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleMapper;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * This class file has been generated. It can be freely modified.
 * It will not be regenerated unless it is removed.
 *
 * @author 3PC Pcom Tools
 */
public class PresenterInstance implements IPresenter, IInstance, IListener {
	
	/**
	 * The factory that has created the instance.
	 */
	protected PresenterFactory factory;
	
	/**
	 * The context used to gain access to the container.
	 */
	protected IInstanceContext context;
	
	/**
	 * The localized string that is shown when the presentation is opened.
	 */
	private static final String UI_OPEN = "info.pppc.pcom.component.presenter.PresenterInstance.OPEN";

	/**
	 * The localized string that is shown when the next button is pressed.
	 */
	private static final String UI_NEXT = "info.pppc.pcom.component.presenter.PresenterInstance.NEXT";
	
	/**
	 * The localized string that is shown when the previous button is pressed.
	 */
	private static final String UI_PREVIOUS = "info.pppc.pcom.component.presenter.PresenterInstance.PREVIOUS";
	
	/**
	 * The localized string that is shown when the presentation is closed.
	 */
	private static final String UI_CLOSE = "info.pppc.pcom.component.presenter.PresenterInstance.CLOSE";

	/**
	 * The localized string that is shown when the presentation is rebound.
	 */
	private static final String UI_RESTORE = "info.pppc.pcom.component.presenter.PresenterInstance.RESTORE";

	/**
	 * A proxy to the file system.
	 */
	private FilesystemProxy filesystem;

	/**
	 * A proxy to the presenter.
	 */
	private PowerpointProxy powerpoint;
	
	/**
	 * The restorer for the powerpoint instance.
	 */
	private IInstanceRestorer restorer;

	/**
	 * The element manager provided by the resource manager.
	 */
	private IElementManager manager;
	
	/**
	 * The control element of the component that is hooked
	 * into the element manager.
	 */
	private PresenterControl control;

	/**
	 * The current silde of the presentation used whenever
	 * the state must be restored or -1 if the presentation
	 * is not loaded.
	 */
	private int slide = -1;
	
	/**
	 * The current open presentation or null if the presentation
	 * is closed.
	 */
	private byte[] presentation = null;
	
	/**
	 * Flag that indicates whether the presenter is running.
	 */
	private boolean started = false;
	
	/**
	 * Creates a new instance using the specified factory.
	 *
	 * @param factory The factory that created the instance.
	 */
	public PresenterInstance(PresenterFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Called to set the context of the instance.
	 *
	 * @param context The context of the instance.
	 */
	public void setContext(IInstanceContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context of the instance.
	 */
	public void unsetContext() {
		this.context = null;
	}
	
	/**
	 * Called to start the instance.
	 */
	public void start() {
		started = true;
		// retrieve the current manager and proxy
		manager = (IElementManager)context.getAccessor("UI");
		powerpoint = (PowerpointProxy)context.getProxy("PPT");
		restorer = context.getRestorer("PPT");
		restorer.getHistory().setEnabled(true);
		filesystem = (FilesystemProxy)context.getProxy("FS");
		if (manager == null) throw new IllegalStateException("Cannot access ui.");
		// re-create the control if neccessary
		if (control == null) {
			manager.run(new Runnable() {
				public void run() {
					control = new PresenterControl(manager);
					control.addControlListener(Event.EVENT_EVERYTHING, new IListener() {
						public void handleEvent(Event event) {
							switch (event.getType()) {
								case PresenterControl.EVENT_CLOSE:
									close();
									break;
								case PresenterControl.EVENT_DISPOSED:
									control.removeControlListener(Event.EVENT_EVERYTHING, this);
									control = null;
									if (started) {
										context.exitApplication();	
									}
									break;
								case PresenterControl.EVENT_NEXT:
									next();
									break;
								case PresenterControl.EVENT_PREVIOUS:
									previous();
									break;
								case PresenterControl.EVENT_OPEN:
									open();
									break;
								case PresenterControl.EVENT_RESIZE:
									// do nothing, refresh is slow
									break;
							}
						}
					});
					manager.addElement(control);
					manager.focusElement(control);
				}
			});			
		}
		factory.addInstanceListener(PresenterFactory.EVENT_INSTANCE_CHANGE, this);
		context.getStatus().addStatusListener(Event.EVENT_EVERYTHING, this);
		handleEvent(null);
		refresh();
	}
	
	/**
	 * Called to pause the instance.
	 */
	public void pause() {
		started = false;
		manager.run(new Runnable() {
			public void run() {
				control.setEnabled(PresenterControl.CONTROL_CLOSE, false);
				control.setEnabled(PresenterControl.CONTROL_NEXT, false);
				control.setEnabled(PresenterControl.CONTROL_PREVIOUS, false);
				control.setEnabled(PresenterControl.CONTROL_OPEN, true);
				ImageDescriptor descriptor = PresenterUI.getDescriptor(PresenterUI.IMAGE_ADAPTING);
				control.setSlide(descriptor.createImage(control.getDisplay()));
			}	
		});
		context.getStatus().removeStatusListener(Event.EVENT_EVERYTHING, this);
		factory.removeInstanceListener(PresenterFactory.EVENT_INSTANCE_CHANGE, this);
	}
	
	/**
	 * Called to stop the instance.
	 */
	public void stop() {
		started = false;
		if (manager != null && control != null) {
			manager.removeElement(control);
		}
		context.getStatus().removeStatusListener(Event.EVENT_EVERYTHING, this);
		factory.removeInstanceListener(PresenterFactory.EVENT_INSTANCE_CHANGE, this);
	}
	
	
	/**
	 * Called to show the next slide of the current presentation.
	 */
	private void next() {
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask(PresenterUI.getText(UI_NEXT), 5);
					monitor.worked(1);
					try {
						powerpoint.nextSlide();
					} catch (InvocationException e) {
						Logging.error(getClass(), "Remote exception in next slide.", e);
						presentation = null;
					} catch (IllegalStateException e) {
						Logging.error(getClass(), "Illegal state exception in next slide.", e);
						presentation = null;
					}
					monitor.worked(1);
					refresh(monitor, true);
					monitor.done();
				}			
			}, false);			
		} catch (InvocationTargetException e) {
			Logging.error(getClass(), "Illegal exception received.", e);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
	}
	
	/**
	 * Called to close the presentation.
	 */
	private void close() {
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask(PresenterUI.getText(UI_CLOSE), 5);
					monitor.worked(1);
					try {
						powerpoint.close();
					}  catch (InvocationException e) {
						Logging.error(getClass(), "Remote exception in close.", e);
					} catch (IllegalStateException e) {
						Logging.error(getClass(), "Illegal state exception in close.", e);
					}
					restorer.createCheckpoint();
					presentation = null;
					monitor.worked(1);
					refresh(monitor, true);
					monitor.done();
				}			
			}, false);			
		} catch (InvocationTargetException e) {
			Logging.error(getClass(), "Illegal exception received.", e);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
	}

	/**
	 * Called to show the previous slide of the presentation.
	 */
	private void previous() {
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask(PresenterUI.getText(UI_PREVIOUS), 5);
					monitor.worked(1);
					try {
						powerpoint.previousSlide();
					} catch (InvocationException e) {
						Logging.error(getClass(), "Remote exception in previous slide.", e);
						presentation = null;
					} catch (IllegalStateException e) {
						Logging.error(getClass(), "Illegal state exception in previous slide.", e);
						presentation = null;
					}
					monitor.worked(1);
					refresh(monitor, true);
					monitor.done();
				}			
			}, false);			
		} catch (InvocationTargetException e) {
			Logging.error(getClass(), "Illegal exception received.", e);
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
	}
	
	/**
	 * Called to open a presentation.
	 */
	private void open() {
		if (presentation != null) {
			close();	
		}
		final FilesystemDialog dialog = new FilesystemDialog(control.getShell());
		FilesystemContentProvider content = new FilesystemContentProvider(filesystem);
		dialog.setBlockOnOpen(true);
		dialog.setEnabled(false);
		dialog.setContentProvider(content);
		dialog.setLabelProvider(new FilesystemLabelProvider());
		dialog.setInput(content.getRoot());
		dialog.addControlListener(Event.EVENT_EVERYTHING, new IListener() {
			public void handleEvent(Event event) {
				switch (event.getType()) {
					case FilesystemDialog.EVENT_SELECTION:
						boolean enabled = false;
						Object data = event.getData();
						if (data != null && data instanceof File) {
							File file = (File)data;
							if (file.getName() != null &&  file.getName().endsWith(".pptx")) {
								enabled = true;
							}
						}
						dialog.setEnabled(enabled);
						break;
				}
			}
		});
		if (dialog.open() == FilesystemDialog.OK) {
			try {
				manager.run(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						monitor.beginTask(PresenterUI.getText(UI_OPEN), 5);
						monitor.worked(1);
						File file = (File)dialog.getSelection();
						try {
							byte[] contents = filesystem.getFile(file);
							monitor.worked(1);
							powerpoint.open(contents);
							presentation = contents;				
						} catch (FileException e) {
							Logging.error(getClass(), "File exception while opening file.", e);
						} catch (InvocationException e) {
							Logging.error(getClass(), "Remote exception while opening file.", e);
						}
						monitor.worked(1);
						refresh(monitor, true);
						monitor.done();
					}			
				}, false);			
			} catch (InvocationTargetException e) {
				Logging.error(getClass(), "Illegal exception received.", e);
			} catch (InterruptedException e) {
				Logging.error(getClass(), "Thread got interrupted.", e);
			}			
		} else {
			manager.run(new Runnable() {
				public void run() {
					refresh(new NullProgressMonitor(), false);
				}	
			});
		}
	}
	
	/**
	 * Refreshes the current display. This is a convenience method
	 * for the refresh method with progress monitor.
	 */
	private void refresh() {
		manager.run(new Runnable() {
			public void run() {
				try {
					manager.run(new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) {
							monitor.beginTask(PresenterUI.getText(UI_RESTORE), 3);
							refresh(monitor, true);
							monitor.done();
						}			
					}, false);				
				} catch (InterruptedException e) {
					Logging.log(getClass(), "Thread got interrupted.");
				} catch (InvocationTargetException e) {
					Logging.error(getClass(), "Could not refresh.", e.getCause());
				}	
			}
		});
	}
	
	/**
	 * Called to refresh the image of the presentation. A call to this 
	 * method will increase the progress monitor 3 times by one.
	 * 
	 * @param monitor The progress monitor used to report the state.
	 * @param update A flag that indicates whether the monitor should
	 *  receive worked events.
	 */
	private void refresh(IProgressMonitor monitor, boolean update) {
		int worked = update?3:0;
		if (presentation != null) {
			control.setEnabled(PresenterControl.CONTROL_CLOSE, true);
			control.setEnabled(PresenterControl.CONTROL_NEXT, true);
			control.setEnabled(PresenterControl.CONTROL_PREVIOUS, true);
			control.setEnabled(PresenterControl.CONTROL_OPEN, false);
			try {
				slide = powerpoint.getSlide();
				monitor.worked(1);
				worked -= 1;
				int width = control.getSlideWidth();
				int height = control.getSlideHeight();
				byte[] bytes = powerpoint.getSlide(slide, width, height);
				monitor.worked(1);
				worked -= 1; 
				if (bytes != null) {
					Image image = new Image
						(control.getDisplay(), new ByteArrayInputStream(bytes)); 	
					control.setSlide(image);
				} else {
					Logging.debug(getClass(), "Get slide returned null.");
					control.setSlide(null);
				}
				monitor.worked(1);
				worked -= 1;
			} catch (InvocationException e) {
				Logging.error(getClass(), "Remote exception in get slide.", e);
				presentation = null;
				refresh(monitor, false);
			} catch (IllegalStateException e) {
				Logging.error(getClass(), "Illegal state exception in get slide.", e);
				presentation = null;
				refresh(monitor, false);
			} catch (IllegalArgumentException e) {
				Logging.error(getClass(), "Illegal argument exception in get slide.", e);
				presentation = null;
				refresh(monitor, false);
			}
		} else {
			slide = -1;
			control.setEnabled(PresenterControl.CONTROL_CLOSE, false);
			control.setEnabled(PresenterControl.CONTROL_NEXT, false);
			control.setEnabled(PresenterControl.CONTROL_PREVIOUS, false);
			control.setEnabled(PresenterControl.CONTROL_OPEN, true);
			ImageDescriptor descriptor = PresenterUI.getDescriptor(PresenterUI.IMAGE_LOGO);
			control.setSlide(descriptor.createImage(control.getDisplay()));
		}
		if (worked > 0) {
			monitor.worked(worked);
		}
	}
	
	/**
	 * Restores the last state stored in the presenter.
	 */
	private void restore() {
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask(PresenterUI.getText(UI_RESTORE), 3);
					try {
						monitor.worked(1);
						if (presentation != null) {
							powerpoint.open(presentation);
							monitor.worked(1);
							powerpoint.setSlide(slide);
							monitor.worked(1);
						} else {
							powerpoint.close();
							monitor.worked(2);
						}						
					} catch (InvocationException e) {
						Logging.error(getClass(), "Could not restore state.", e);
						presentation = null;
					}
					refresh(monitor, true);
					monitor.done();
				}
			}, false);			
		} catch (Throwable e) {
			Logging.error(getClass(), "Could not restore state.", e);
		} 				
	}
	
	/**
	 * Called whenever the status changes. This will perform the
	 * resource/instance mapping and will check any constraints.
	 * 
	 * @param event The event that signals the change.
	 */
	public void handleEvent(Event event) {
		synchronized (context) {
			if (! started) return;
			IInstanceProvisionWriter instance = context.getTemplate().getInstance();
			ITypeProvisionWriter target = instance.getInterface(IPresenter.class.getName()); 
			// map the ui resource
			IInstanceProvisionReader pptreader = context.getStatus().getInstance("PPT");
			if (pptreader == null) return;
			ITypeProvisionReader pptiface = pptreader.getInterface(IPowerpoint.class.getName());
			SimpleMapper ptmapper = new SimpleMapper();
			ptmapper.addDimension("DISPLAY", "DISPLAY");
			ptmapper.addFeature("LOCATION", "POWERPOINT", "LOCATION", "POWERPOINT");
			ptmapper.map(pptiface, target);
			// determine whether our constraint matches
			IDimensionProvisionWriter locdim = target.getDimension("LOCATION");
			IFeatureProvisionWriter confeat = locdim.getFeature("CONSTRAINT");
			boolean constraint = ((Boolean)confeat.getValue()).booleanValue();
			if (constraint) {
				if (factory.getRemote() != 0) {
					if (target != null) {
						IDimensionProvisionReader locpro = pptiface.getDimension("LOCATION");
						if (locpro != null) {
							IFeatureProvisionReader pptloc = locpro.getFeature("POWERPOINT");
							if (pptloc != null && pptloc.getValue() != null && pptloc.getValue() instanceof Integer) {
								Integer loc = (Integer)pptloc.getValue();
								if (loc.shortValue() == factory.getRemote()) {
									context.getTemplate().commitTemplate();
									return;
								}
							}
						}
					}
				}
				started = false;
				context.changeInstance();
				return;
			} 
			context.getTemplate().commitTemplate();			
		}
	}
	
	/**
	 * Loads the state from a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to load.
	 */
	public void loadCheckpoint(IInstanceCheckpoint checkpoint) {
		this.presentation = (byte[])checkpoint.getObject(CHECKPOINT_PRESENTATION);
		this.slide = checkpoint.getInteger(CHECKPOINT_SLIDE);
		// refresh the control
		manager.run(new Runnable() {
			public void run() {
				// restore the content
				restore();		
			}	
		});
	}
	
	/**
	 * Stores the state to a checkpoint.
	 * 
	 * @param checkpoint The checkpoint to store to.
	 */
	public void storeCheckpoint(IInstanceCheckpoint checkpoint) {
		checkpoint.setComplete(true);
		checkpoint.putInteger(CHECKPOINT_SLIDE, slide);
		checkpoint.putObject(CHECKPOINT_PRESENTATION, presentation);
	}
	
}
