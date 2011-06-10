package info.pppc.pcomx.assembler.gd.swtui.control;

import java.util.Hashtable;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.DeviceDescription;
import info.pppc.base.system.DeviceRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcomx.assembler.gd.internal.InstanceBinding;
import info.pppc.pcomx.assembler.gd.internal.Application;
import info.pppc.pcomx.assembler.gd.internal.InstanceRequest;
import info.pppc.pcomx.assembler.gd.internal.Pointer;
import info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI;
import info.pppc.pcomx.assembler.swtui.control.AbstractApplicationControl;
import info.pppc.pcomx.assembler.swtui.figure.DeviceFigure;
import info.pppc.pcomx.assembler.swtui.figure.StateFigure;

/**
 * The application control visualizes a single application on
 * one of the devices that participate in the configuration
 * process of a gd assembler.
 * 
 * @author Mac
 */
public class ApplicationControl extends AbstractApplicationControl implements IListener {

	/**
	 * The text of the control shown in the tab.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gd.swtui.control.ApplicationControl.TEXT";
	
	/**
	 * The application object used to configure an application.
	 */
	private Application application;
	
	/**
	 * The elements with their corresponding visualization. This table
	 * hashes elements of the configuration algorithm to their corresponding
	 * figures.
	 */
	private Hashtable assemblerElements = new Hashtable();

	/**
	 * The elements with their corresponding visualization. This table
	 * hashes elements of the application states to their corresponding
	 * figures.
	 */
	private Hashtable applicationElements = new Hashtable();
	
	/**
	 * The device registry that is used to observe devices.
	 */
	private DeviceRegistry registry = InvocationBroker.getInstance().getDeviceRegistry();
	
	/**
	 * Creates a new control using the specified manager and application.
	 * 
	 * @param manager The manager that is used for ui interaction.
	 * @param application The application that provides the model.
	 */
	public ApplicationControl(IElementManager manager, Application application) {
		super(manager);
		this.application = application;
	}
	
	/**
	 * Returns the application that is visualized by this control.
	 * 
	 * @return The application visualized by the control.
	 */
	protected Application getApplication() {
		return application;
	}
	
	/**
	 * Returns the image of the control.
	 * 
	 * @return The image of the control.
	 */
	public Image getImage() {
		return GDAssemblerUI.getImage(GDAssemblerUI.IMAGE_GD);
	}

	/**
	 * Returns the name of the control.
	 * 
	 * @return The name of the control.
	 */
	public String getName() {
		return GDAssemblerUI.getText(UI_TEXT);
	}
	
	/**
	 * Creates the application control on the specified parent pane.
	 * 
	 * @param parent The parent of the control.
	 */
	public void showControl(Composite parent) {
		// create the parent's content
		super.showControl(parent);
		// add myself as listener to the application
		application.addApplicationListener(Event.EVENT_EVERYTHING, this);
		synchronized (registry) {
			registry.addDeviceListener(Event.EVENT_EVERYTHING, this);
			SystemID[] ids = registry.getDevices();
			for (int i = 0; i < ids.length; i++) {
				addDevice(registry.getDeviceDescription(ids[i]));
			}
		}
		assemblerGraph.updateGraph();
		applicationGraph.updateGraph();
		update();
	}

	/**
	 * Called whenever an event must be visualized.
	 * 
	 * @param event The event to visualize.
	 */
	public void handleEvent(final Event event) {
		IElementManager manager = getManager();
		if (manager == null) return;
		if (event.getSource() == application) {
			manager.run(new Runnable() {
				public void run() {
					if (getDisplay() == null || getDisplay().isDisposed()) return;
					switch (event.getType()) {
						case Application.EVENT_DEVICE_ADDED: {
							SystemID systemID = (SystemID)event.getData();
							DeviceFigure assemblerDevice = (DeviceFigure)assemblerElements.get(systemID);
							if (assemblerDevice != null) {
								assemblerDevice.setResolved(true);	
							}
						}
						break;
						case Application.EVENT_DEVICE_REMOVED: {
							SystemID systemID = (SystemID)event.getData();
							DeviceFigure assemblerDevice = (DeviceFigure)assemblerElements.get(systemID);
							if (assemblerDevice != null) {
								assemblerDevice.setResolved(false);
								assemblerDevice.setAvailable(false);
							}
						}
						break;
						case Application.EVENT_REQEUST_ADDED: {
							addRequest((InstanceRequest)event.getData());
						}
						break;
						case Application.EVENT_REQUEST_CHANGED: {
							changeRequest((InstanceRequest)event.getData());
						}
						break;
						case Application.EVENT_REQUEST_REMOVED: {
							removeRequest((InstanceRequest)event.getData());
						}
						break;
						case Application.EVENT_BINDING_CHANGED: {
							changeBinding((InstanceBinding)event.getData());
						}
						break;
						case Application.EVENT_STATE_ADDED: {
							Object[] data = (Object[])event.getData();
							addState((Pointer)data[0], (AssemblyState)data[1]);
						}
						break;
						default:
							// nothing to be done here		
					}
					assemblerGraph.updateGraph();
					applicationGraph.updateGraph();
				}			
			});
			block();
		} else if (event.getSource() == registry) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getDisplay() == null || getDisplay().isDisposed()) return;
					switch (event.getType()) {
						case DeviceRegistry.EVENT_DEVICE_ADDED: {
							// add the device to the panel
							DeviceDescription description = 
								(DeviceDescription)event.getData();
							addDevice(description);
						}
						break;
						case DeviceRegistry.EVENT_DEVICE_REMOVED: {
							DeviceDescription description = 
								(DeviceDescription)event.getData();
							// invalidate the device
							removeDevice(description);
						}
						break;
						default:
							// nothing to be done here
					}
					assemblerGraph.updateGraph();
					applicationGraph.updateGraph();
				}
			});
		}
	}

	/**
	 * Adds the device with the specified system id to the graph panel.
	 * 
	 * @param description The description of the device to add.
	 */
	private void addDevice(DeviceDescription description) {		
		if (description == null) return;
		SystemID systemID = description.getSystemID();
		DeviceFigure applicationDevice = (DeviceFigure)applicationElements.get(systemID);
		if (applicationDevice == null) {
			applicationDevice = new DeviceFigure(description);
			applicationElements.put(systemID, applicationDevice);
			applicationGraph.addEntry(applicationDevice);
		} else {
			applicationDevice.setAvailable(true);
		}
		DeviceFigure assemblerDevice = (DeviceFigure)assemblerElements.get(systemID);
		if (assemblerDevice == null) {
			assemblerDevice = new DeviceFigure(description);
			assemblerElements.put(systemID, assemblerDevice);
			assemblerGraph.addEntry(assemblerDevice);
		}
		zoom();
	}

	/**
	 * Invalidates the device with the specified system id.
	 * 
	 * @param description The description of the device to add.
	 */
	private void removeDevice(DeviceDescription description) {
		SystemID systemID = description.getSystemID();
		DeviceFigure applicationDevice = (DeviceFigure)applicationElements.get(systemID);
		if (applicationDevice != null) {
			applicationDevice.setResolved(false);
			applicationDevice.setAvailable(false);
		}
	}
	
	/**
	 * Called whenever the configuration algorithm has been loaded
	 * with some additional state.
	 * 
	 * @param pointer The pointer that denotes the point in the
	 * 	graph.
	 * @param state The application state.
	 */
	private void addState(Pointer pointer, AssemblyState state) {
		// recurse and make sure that all parents exist already
		if (pointer.length() != 0) {
			Pointer parent = new Pointer(pointer, 1);
			if (! applicationElements.containsKey(parent)) {
				addState(parent, null);
			}
		}
		// now comes the actual implementation
		DeviceFigure device = null;
		if (state != null) {
			device = (DeviceFigure)applicationElements.get(state.getSystemID());
		}
		StateFigure figure = null;
		if (pointer.length() == 0) {
			figure = new StateFigure();
		} else {
			figure = new StateFigure(pointer.getName(pointer.length() -1), 
				pointer.isInstance(pointer.length() - 1));
		}
		figure.setAvailable(state != null);
		applicationElements.put(pointer, figure);
		if (device != null) {
			applicationGraph.insertNode(figure, device);	
			PolylineConnection edge = new PolylineConnection();
			edge.setSourceAnchor(new ChopboxAnchor(device));
			edge.setTargetAnchor(new ChopboxAnchor(figure));	
			edge.setLineStyle(Graphics.LINE_DOT);
			edge.setForegroundColor(ColorConstants.lightBlue);
			applicationGraph.add(edge);
		} else {
			applicationGraph.addNode(figure);
		}
		if (pointer.length() != 0) {
			// recurse in order to create all parent if that has not
			// been done so far
			Pointer parent = new Pointer(pointer, 1);
			Figure parentFigure = (Figure)applicationElements.get(parent);
			PolylineConnection edge = new PolylineConnection();
			edge.setSourceAnchor(new ChopboxAnchor(parentFigure));
			edge.setTargetAnchor(new ChopboxAnchor(figure));	
			applicationGraph.addEdge(edge);
		}
	}
	
	/**
	 * Add a new request to the graph. If this happens,
	 * we need to add the instance representing the
	 * request into the tree including its bindings. To add
	 * it we might have to construct its parents from the
	 * path.
	 * 
	 * @param request The request that needs to be added.
	 */
	private void addRequest(InstanceRequest request) {
		
	}
	
	/**
	 * Modifies an existing request since the instance of the
	 * request might have changed. If this happens, we need
	 * to remove all bindings that exist for the instance
	 * we need to create new ones for the new instance.
	 * 
	 * @param request The request that has changed.
	 */
	private void changeRequest(InstanceRequest request) {
		
	}
	
	/**
	 * Removes the specified request since it is no longer
	 * needed. If this happens, we need to remove all
	 * bindings and the instance bound to the request.
	 * We also should check whether the parent bindings
	 * have more children or reside on the same container.
	 * If non of the above is the case, the parent bindings
	 * and instances can be removed, too.
	 * 
	 * @param request The request to remove.
	 */
	private void removeRequest(InstanceRequest request) {
		
	}
	
	/**
	 * Changes the specified binding. If this happens,
	 * the visualization of the binding should be updated.
	 * 
	 * @param binding The binding to update.
	 */
	private void changeBinding(InstanceBinding binding) {
		
	}
	
}
