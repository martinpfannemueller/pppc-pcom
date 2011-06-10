package info.pppc.pcom.component.presenter;

import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.NullMonitor;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.component.filesystem.File;
import info.pppc.pcom.component.filesystem.FileException;
import info.pppc.pcom.component.powerpoint.IPowerpoint;
import info.pppc.pcom.component.presenter.lcdui.FilesystemContentProvider;
import info.pppc.pcom.component.presenter.lcdui.FilesystemElement;
import info.pppc.pcom.component.presenter.lcdui.FilesystemLabelProvider;
import info.pppc.pcom.component.presenter.lcdui.PresenterElement;
import info.pppc.pcom.component.presenter.lcdui.PresenterUI;
import info.pppc.pcom.system.model.component.IInstance;
import info.pppc.pcom.system.model.component.IInstanceCheckpoint;
import info.pppc.pcom.system.model.component.IInstanceContext;
import info.pppc.pcom.system.model.component.IInstanceRestorer;
import info.pppc.pcom.system.model.contract.reader.IInstanceProvisionReader;
import info.pppc.pcom.system.model.contract.reader.ITypeProvisionReader;
import info.pppc.pcom.system.model.contract.writer.IInstanceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcomx.contract.SimpleMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Image;

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
	private PresenterElement control;

	/**
	 * The open dialog that is shown whenever the user clicks
	 * on the open button.
	 */
	private FilesystemElement dialog; 
	
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
	 * A flag that indicates whether the component instance is
	 * started.
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
					control = new PresenterElement(manager);
					control.addControlListener(Event.EVENT_EVERYTHING, new IListener() {
						public void handleEvent(Event event) {
							switch (event.getType()) {
								case PresenterElement.EVENT_CLOSE:
									close();
									break;
								case PresenterElement.EVENT_DISPOSED:
									control.removeControlListener(Event.EVENT_EVERYTHING, this);
									control = null;
									if (started) {
										context.exitApplication();	
									}
									break;
								case PresenterElement.EVENT_NEXT:
									next();
									break;
								case PresenterElement.EVENT_PREVIOUS:
									previous();
									break;
								case PresenterElement.EVENT_OPEN:
									open();
									break;
								case PresenterElement.EVENT_RESIZE:
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
				if (dialog != null) {
					manager.focusElement(control);
					manager.removeElement(dialog);
					dialog = null;
				}
				control.setEnabled(PresenterElement.CONTROL_CLOSE, false);
				control.setEnabled(PresenterElement.CONTROL_NEXT, false);
				control.setEnabled(PresenterElement.CONTROL_PREVIOUS, false);
				control.setEnabled(PresenterElement.CONTROL_OPEN, true);
				Image slide = PresenterUI.getImage(PresenterUI.IMAGE_ADAPTING);
				control.setSlide(slide);
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
			manager.run(new Runnable() {
				public void run() {
					manager.removeElement(control);	
				}
			});
		}
		context.getStatus().removeStatusListener(Event.EVENT_EVERYTHING, this);
		factory.removeInstanceListener(PresenterFactory.EVENT_INSTANCE_CHANGE, this);
	}
	
	
	/**
	 * Called to show the next slide of the current presentation.
	 */
	private void next() {
		try {
			manager.run(new IOperation() {
				public void perform(IMonitor monitor) {
					monitor.setName(PresenterUI.getText(UI_NEXT));
					monitor.start(5);
					monitor.step(1);
					try {
						powerpoint.nextSlide();
					} catch (InvocationException e) {
						Logging.error(getClass(), "Remote exception in next slide.", e);
						presentation = null;
					} catch (IllegalStateException e) {
						Logging.error(getClass(), "Illegal state exception in next slide.", e);
						presentation = null;
					}
					monitor.step(1);
					refresh(monitor, true);
					monitor.done();
				}			
			}, false);			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
	}
	
	/**
	 * Called to close the presentation.
	 */
	private void close() {
		try {
			manager.run(new IOperation() {
				public void perform(IMonitor monitor) {
					monitor.setName(PresenterUI.getText(UI_CLOSE));
					monitor.start(5);
					monitor.step(1);
					try {
						powerpoint.close();
					}  catch (InvocationException e) {
						Logging.error(getClass(), "Remote exception in close.", e);
					} catch (IllegalStateException e) {
						Logging.error(getClass(), "Illegal state exception in close.", e);
					}
					restorer.createCheckpoint();
					presentation = null;
					monitor.step(1);
					refresh(monitor, true);
					monitor.done();
				}			
			}, false);			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
	}

	/**
	 * Called to show the previous slide of the presentation.
	 */
	private void previous() {
		try {
			manager.run(new IOperation() {
				public void perform(IMonitor monitor) {
					monitor.setName(PresenterUI.getText(UI_PREVIOUS));
					monitor.start(5);
					monitor.step(1);
					try {
						powerpoint.previousSlide();
					} catch (InvocationException e) {
						Logging.error(getClass(), "Remote exception in previous slide.", e);
						presentation = null;
					} catch (IllegalStateException e) {
						Logging.error(getClass(), "Illegal state exception in previous slide.", e);
						presentation = null;
					}
					monitor.step(1);
					refresh(monitor, true);
					monitor.done();
				}			
			}, false);			
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
		FilesystemContentProvider content = new FilesystemContentProvider(filesystem);
		FilesystemLabelProvider label = new FilesystemLabelProvider();
		dialog = new FilesystemElement(manager, content, label);
		dialog.addControlListener(Event.EVENT_EVERYTHING, new IListener() {
			public void handleEvent(Event event) {
				if (dialog == null) return; // could happen if pause is called 
				switch (event.getType()) {
					case FilesystemElement.EVENT_SELECTION:
						boolean enabled = false;
						Object data = event.getData();
						if (data != null && data instanceof File) {
							File file = (File)data;
							if (file.getName() != null && file.getName().endsWith(".pptx")) {
								enabled = true;
							}
						}
						dialog.setEnabled(enabled);
						break;
					case FilesystemElement.EVENT_OK:
						refresh(new NullMonitor(), false);
						manager.focusElement(control);
						manager.removeElement(dialog);
						try {
							manager.run(new IOperation() {
								public void perform(IMonitor monitor) {
									monitor.setName(PresenterUI.getText(UI_OPEN));
									monitor.start(5);
									monitor.step(1);
									File file = (File)dialog.getSelection();
									try {
										byte[] contents = filesystem.getFile(file);
										Logging.debug(getClass(), "File retrieved: " + contents.length);
										monitor.step(1);
										powerpoint.open(contents);
										presentation = contents;				
									} catch (FileException e) {
										Logging.error(getClass(), "File exception while opening file.", e);
									} catch (InvocationException e) {
										Logging.error(getClass(), "Remote exception while opening file.", e);
									}
									monitor.step(1);
									refresh(monitor, true);
									monitor.done();
								}			
							}, false);
						} catch (InterruptedException e) {
							Logging.error(getClass(), "Thread got interrupted.", e);
						}
						dialog = null;
						break;
					case FilesystemElement.EVENT_CANCEL:
						refresh(new NullMonitor(), false);
						manager.focusElement(control);
						manager.removeElement(dialog);
						dialog = null;
						break;
					default:
						// will never happen
				}
			}
		});
		manager.addElement(dialog);
		manager.focusElement(dialog);
	}
	
	/**
	 * Refreshes the current display. This is a convenience method
	 * for the refresh method with progress monitor.
	 */
	private void refresh() {
		manager.run(new Runnable() {
			public void run() {
				try {
					manager.run(new IOperation() {
						public void perform(IMonitor monitor) {
							monitor.setName(PresenterUI.getText(UI_RESTORE));
							monitor.start(3);
							refresh(monitor, true);
							monitor.done();
						}			
					}, false);				
				} catch (InterruptedException e) {
					Logging.log(getClass(), "Thread got interrupted.");
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
	private void refresh(IMonitor monitor, boolean update) {
		int worked = update?3:0;
		if (presentation != null) {
			control.setEnabled(PresenterElement.CONTROL_CLOSE, true);
			control.setEnabled(PresenterElement.CONTROL_NEXT, true);
			control.setEnabled(PresenterElement.CONTROL_PREVIOUS, true);
			control.setEnabled(PresenterElement.CONTROL_OPEN, false);
			try {
				slide = powerpoint.getSlide();
				monitor.step(1);
				worked -= 1;
				int width = control.getSlideWidth();
				int height = control.getSlideHeight();
				byte[] bytes = powerpoint.getSlide(slide, width, height);
				monitor.step(1);
				worked -= 1; 
				if (bytes != null) {
					try {
						Image image = Image.createImage(new ByteArrayInputStream(bytes));
						control.setSlide(image);	
					} catch (IOException e) {
						Logging.error(getClass(), "Could not convert slide.", e);
					}
				} else {
					Logging.debug(getClass(), "Get slide returned null.");
					control.setSlide(null);
				}
				monitor.step(1);
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
			control.setEnabled(PresenterElement.CONTROL_CLOSE, false);
			control.setEnabled(PresenterElement.CONTROL_NEXT, false);
			control.setEnabled(PresenterElement.CONTROL_PREVIOUS, false);
			control.setEnabled(PresenterElement.CONTROL_OPEN, true);
			Image image = PresenterUI.getImage(PresenterUI.IMAGE_LOGO);
			control.setSlide(image);
		}
		if (worked > 0) {
			monitor.step(worked);
		}
	}
	
	/**
	 * Restores the last state stored in the presenter.
	 */
	private void restore() {
		manager.run(new Runnable() {
			public void run() {
				try {
					manager.run(new IOperation() {
						public void perform(IMonitor monitor) {
							monitor.setName(PresenterUI.getText(UI_RESTORE));
							monitor.start(3);
							try {
								monitor.step(1);
								if (presentation != null) {
									powerpoint.open(presentation);
									monitor.step(1);
									powerpoint.setSlide(slide);
									monitor.step(1);
								} else {
									powerpoint.close();
									monitor.step(2);
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
		});
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
			ITypeProvisionReader pptiface = pptreader.getInterface(IPowerpoint.class.getName());
			SimpleMapper ptmapper = new SimpleMapper();
			ptmapper.addDimension("DISPLAY", "DISPLAY");
			ptmapper.addFeature("LOCATION", "POWERPOINT", "LOCATION", "POWERPOINT");
			ptmapper.map(pptiface, target);
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
		this.slide = checkpoint.getInteger(CHECKPOINT_PRESENTATION);
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
