package info.pppc.pcom.lcdui.application;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import info.pppc.base.lcdui.BaseUI;
import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.base.lcdui.element.IRefreshable;
import info.pppc.base.lcdui.element.action.CloseAction;
import info.pppc.base.lcdui.element.action.RefreshAction;
import info.pppc.base.lcdui.form.FormItem;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.lcdui.PcomUI;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.application.ApplicationManager;

/**
 * The application element provides an element that lets a user start, 
 * stop and pause pcom applications. Furthermore, a user can use this 
 * element to browser the preferences for a certain application and he 
 * can move around the preference levels and configure the configuration 
 * algorithm. More complex task such as build a new application are
 * currently not supported, as this would be too clumsy on mobile phones.
 * 
 * @author Mac
 */
public class ApplicationElement extends AbstractElement implements IRefreshable, IListener {

	/**
	 * The resource key for the name of the application browser.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.lcdui.application.ApplicationElement.TEXT";
	
	/**
	 * The resource key for the start label.
	 */
	private static final String UI_START = "info.pppc.pcom.lcdui.application.ApplicationElement.START";
	
	/**
	 * The resource key for the stop label.
	 */
	private static final String UI_STOP = "info.pppc.pcom.lcdui.application.ApplicationElement.STOP";
	
	/**
	 * The resource key for the update label.
	 */
	private static final String UI_UPDATE = "info.pppc.pcom.lcdui.application.ApplicationElement.UPDATE";
	
	/**
	 * The resource key for the change label.
	 */
	private static final String UI_CHANGE = "info.pppc.pcom.lcdui.application.ApplicationElement.CHANGE";
	
	/**
	 * The actions that are available for the element.
	 */
	private ElementAction[] actions;
	
	/**
	 * The items that are available for the element. Each item
	 * represents a single application descriptor.
	 */
	private ApplicationItem[] items = new ApplicationItem[0];
	
	/**
	 * A reference to the application manager that is controlled by this element.
	 */
	protected ApplicationManager applicationManager = ApplicationManager.getInstance();
	
	/**
	 * Creates a new application element using the specified
	 * manager.
	 * 
	 * @param manager The manager that will hold the element.
	 */
	public ApplicationElement(IElementManager manager) {
		super(manager);
		actions = new ElementAction[] {
			new RefreshAction(this),
			new CloseAction(this) 
		};
		applicationManager.addApplicationListener(Event.EVENT_EVERYTHING, this);
		refresh();
	}
	
	/**
	 * Returns the image of the application browser.
	 * 
	 * @return The image of the application browser.
	 */
	public Image getImage() {
		return BaseUI.getImage(BaseUI.IMAGE_LOGO);
	}

	/**
	 * Returns the items of the application browser. This
	 * are the applications that are stored in the manager.
	 * 
	 * @return The items of the application manager.
	 */
	public FormItem[] getItems() {
		return items;
	}

	/**
	 * Returns the name of the application browser as shown
	 * in the user interface.
	 * 
	 * @return The name of the application browser.
	 */
	public String getName() {
		return PcomUI.getText(UI_TEXT);
	}
	
	/**
	 * Returns the actions available for the application browser.
	 * These are only the global actions, not the item specific
	 * actions. These are provided by the items themselves.
	 * 
	 * @return The menu actions for the element.
	 */
	public ElementAction[] getActions() {
		return actions;
	}

	/**
	 * (Re-)loads the current state of all applicaitons from the 
	 * application manager.
	 */
	public void refresh() {
		Vector adapters = new Vector();
		Vector applicaitonIDs = applicationManager.getApplications();
		for (int i = 0, s = applicaitonIDs.size(); i < s; i++) {
			ObjectID applicationID = (ObjectID)applicaitonIDs.elementAt(i);
			ApplicationDescriptor descriptor = applicationManager.queryApplication(applicationID);
			if (descriptor != null) {
				ApplicationItem item = new ApplicationItem(this);
				item.setDescriptor(descriptor);
				adapters.addElement(item);
			}
		}
		items = new ApplicationItem[adapters.size()];
		for (int i = adapters.size() - 1; i >= 0; i--) {
			items[i] = (ApplicationItem)adapters.elementAt(i);
		}
		getManager().updateElement(this);
	}
	
	/**
	 * Called whenver the control is destroyed, this will remove
	 * the listener from the application repository.
	 */
	public void dispose() {
		applicationManager.removeApplicationListener(Event.EVENT_EVERYTHING, this);
		items = new ApplicationItem[0];
	}
	
	/**
	 * Called by the pcom application manager whenever the state
	 * of an application changes.
	 * 
	 * @param event The event that describes the state of the
	 * 	application.
	 */
	public void handleEvent(final Event event) {
		final ApplicationDescriptor descriptor = (ApplicationDescriptor)event.getData();
		if (descriptor == null) return;
		getManager().run(new Runnable() {
			public void run() {
				switch (event.getType()) {
					case ApplicationManager.EVENT_APPLICATION_ADDED: 
					{
						ApplicationItem item = new ApplicationItem(ApplicationElement.this);
						item.setDescriptor(descriptor);
						ApplicationItem[] copy = new ApplicationItem[items.length + 1];
						System.arraycopy(items, 0, copy, 0, items.length);
						copy[items.length] = item;
						items = copy;
						getManager().updateElement(ApplicationElement.this);
						break;
					}
					case ApplicationManager.EVENT_APPLICATION_REMOVED:
					{
						for (int i = 0, s = items.length; i < s; i++) {
							ApplicationItem item = items[i];
							if (item.getDescriptor().equals(descriptor.getApplicationID())) {
								ApplicationItem[] copy = new ApplicationItem[items.length - 1];
								if (i != 0) {
									System.arraycopy(items, 0, copy, 0, i);
								}
								if (i != items.length - 1) {
									System.arraycopy(items, i + 1, copy, i, copy.length - i);
								}
								items = copy;
								break;
							}
						}
						getManager().updateElement(ApplicationElement.this);
						break;
					}
					case ApplicationManager.EVENT_APPLICATION_STARTED:
					case ApplicationManager.EVENT_APPLICATION_STOPPED:
					case ApplicationManager.EVENT_APPLICATION_PAUSED:
					case ApplicationManager.EVENT_APPLICATION_CHANGED:
					{
						for (int i = 0, s = items.length; i < s; i++) {
							ApplicationItem item = items[i];
							if (item.getDescriptor().getApplicationID().equals
										(descriptor.getApplicationID())) {
								item.setDescriptor(descriptor);
								break;
							}
						}
						break;
					}
					default:
						// should never happen, ignore
				}
			}
		});
	}
	
	/**
	 * Called whenever an application item created by this element
	 * has received an edit command.
	 * 
	 * @param descriptor The application descriptor that contains
	 * 	the application to be edited.
	 */
	protected void editApplication(ApplicationDescriptor descriptor) {
		AbstractElement[] elements = getManager().getElements();
		for (int i = elements.length - 1; i >= 0; i--) {
			if (elements[i] instanceof ApplicationEditor) {
				ApplicationEditor editor = (ApplicationEditor)elements[i];
				if (editor.getDescriptor() == descriptor) {
					getManager().focusElement(editor);
					return;
				}
			}
		}
		ApplicationEditor editor = new ApplicationEditor
			(getManager(), this, descriptor);
		getManager().addElement(editor);
		getManager().focusElement(editor);
	}
	
	/**
	 * Called whenever an application item created by this element
	 * has received a start command.
	 * 
	 * @param descriptor The application descriptor that contains
	 * 	the application to be started.
	 */
	protected void startApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					monitor.setName(PcomUI.getText(UI_START));
					monitor.start(2);
					monitor.step(1);
					applicationManager.startApplication(descriptor.getApplicationID());		
					monitor.step(1);
				}
			}, false);			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
	}
	
	/**
	 * Called whenever an application item created by this element
	 * has received a stop command.
	 * 
	 * @param descriptor The application descriptor that contains
	 * 	the application to be stopped.
	 */
	protected void stopApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					monitor.setName(PcomUI.getText(UI_STOP));
					monitor.start(2);
					monitor.step(1);
					applicationManager.exitApplication(descriptor.getApplicationID());		
					monitor.step(1);
				}
			}, false);			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}
		
	}

	/**
	 * Called whenever an application item created by this element
	 * has received a adapt command.
	 * 
	 * @param descriptor The application descriptor that contains
	 * 	the application to be adapted.
	 */
	protected void adaptApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					monitor.setName(PcomUI.getText(UI_CHANGE));
					monitor.start(2);
					monitor.step(1);
					applicationManager.changeApplication(descriptor.getApplicationID());		
					monitor.step(1);
				}
			}, false);			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}

		
	}
	
	/**
	 * Called whenever an application description needs to be updated.
	 * 
	 * @param descriptor The descriptor of the application to update.
	 */
	protected void updateApplication(final ApplicationDescriptor descriptor) {
		try {
			getManager().run(new IOperation() {
				public void perform(IMonitor monitor) throws Exception {
					monitor.setName(PcomUI.getText(UI_UPDATE));
					monitor.start(2);
					monitor.step(1);
					applicationManager.updateAppliction(descriptor);		
					monitor.step(1);
				}
			}, false);			
		} catch (InterruptedException e) {
			Logging.error(getClass(), "Thread got interrupted.", e);
		}	
	}

}
