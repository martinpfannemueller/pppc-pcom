package info.pppc.pcomx.assembler.gc.swtui.control;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.DeviceDescription;
import info.pppc.base.system.DeviceRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.pcom.system.assembler.AssemblyState;
import info.pppc.pcomx.assembler.gc.internal.AbstractBinding;
import info.pppc.pcomx.assembler.gc.internal.AbstractElement;
import info.pppc.pcomx.assembler.gc.internal.AbstractItem;
import info.pppc.pcomx.assembler.gc.internal.Application;
import info.pppc.pcomx.assembler.gc.internal.Device;
import info.pppc.pcomx.assembler.gc.internal.Pointer;
import info.pppc.pcomx.assembler.gc.swtui.GCAssemblerUI;
import info.pppc.pcomx.assembler.gc.swtui.figure.BindingFigure;
import info.pppc.pcomx.assembler.gc.swtui.figure.ElementFigure;
import info.pppc.pcomx.assembler.gc.swtui.figure.ItemFigure;
import info.pppc.pcomx.assembler.swtui.control.AbstractApplicationControl;
import info.pppc.pcomx.assembler.swtui.figure.DeviceFigure;
import info.pppc.pcomx.assembler.swtui.figure.StateFigure;

import java.util.Hashtable;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * The application control is used to visualize a single application
 * that is configured by the gc assembler.
 * 
 * @author Mac
 */
public class ApplicationControl extends AbstractApplicationControl implements IListener {

	/**
	 * The text that is shown in the top level selection.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gc.swtui.control.ApplicationControl.TEXT";
	
	/**
	 * The application that is visualized by this control.
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
	 * Creates a new manager for the specified application on the 
	 * specified element manager. 
	 * 
	 * @param manager The manager used to show the control.
	 * @param application The application that will be visualized by
	 * 	the control.
	 */
	public ApplicationControl(IElementManager manager, Application application) {
		super(manager);
		this.application = application;
	}

	/**
	 * Returns the application that is displayed by this control.
	 * 
	 * @return The application displayed by the control.
	 */
	protected Application getApplication() {
		return application;
	}
	
	/**
	 * Returns the name of the application control as shown in the ui.
	 * 
	 * @return The name of the application control.
	 */
	public String getName() {
		return GCAssemblerUI.getText(UI_TEXT);
	}

	/**
	 * Returns the image of the applicaiton control as shown in the ui.
	 * 
	 * @return The image of the application control.
	 */
	public Image getImage() {
		return GCAssemblerUI.getImage(GCAssemblerUI.IMAGE_GC);
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
	 * Called whenever the control is disposed. This will unregister
	 * the application listener from the application before it 
	 * disposes the parent control.
	 */
	public void disposeControl() {
		application.removeApplicationListener(Event.EVENT_EVERYTHING, this);
		registry.removeDeviceListener(Event.EVENT_EVERYTHING, this);
		super.disposeControl();
		synchronized (blockTime) {
			blockTime[0] = 0;
			blockTime.notifyAll();
		}
	}
	
	/**
	 * Called whenever the application performs some action.
	 * 
	 * @param event The event that describes the actions performed.
	 */
	public void handleEvent(final Event event) {
		IElementManager manager = getManager();
		if (manager == null) return;
		if (event.getSource() == application) {
			manager.run(new Runnable() {
				public void run() {
					if (getDisplay() == null || getDisplay().isDisposed()) return;
					switch (event.getType()) {
						case Application.EVENT_DEVICE_RESOLVED: {
							updateDevice((Device)event.getData());					
						}
						break;
						case Application.EVENT_ITEM_ADDED: {
							addItem((AbstractItem)event.getData());
						}
						break;
						case Application.EVENT_ITEM_CONFIGURED: {
							updateItem((AbstractItem)event.getData());
						}
						break;
						case Application.EVENT_ITEM_REMOVED: {
							removeItem((AbstractItem)event.getData());
						}
						break;
						case Application.EVENT_BINDING_RESOLVED: {
							resolveItem((AbstractBinding)event.getData());
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
	 * @param description The description of the device that has been added.
	 */
	private void addDevice(DeviceDescription description) {		
		if (description == null) return;
		SystemID systemID = description.getSystemID();
		DeviceFigure assemblerDevice = (DeviceFigure)assemblerElements.get(systemID);
		if (assemblerDevice == null) {
			assemblerDevice = new DeviceFigure(description);
			assemblerElements.put(systemID, assemblerDevice);
			assemblerGraph.addEntry(assemblerDevice);
		} else {
			assemblerDevice.setAvailable(true);
		}
		DeviceFigure applicationDevice = (DeviceFigure)applicationElements.get(systemID);
		if (applicationDevice == null) {
			applicationDevice = new DeviceFigure(description);
			applicationElements.put(systemID, applicationDevice);
			applicationGraph.addEntry(applicationDevice);
		} else {
			applicationDevice.setAvailable(true);
		}
		zoom();
	}

	/**
	 * Invalidates the device with the specified system id.
	 * 
	 * @param description The description of the device that has been
	 * 	removed.
	 */
	private void removeDevice(DeviceDescription description) {
		SystemID systemID = description.getSystemID();
		DeviceFigure assemblerDevice = (DeviceFigure)assemblerElements.get(systemID);
		if (assemblerDevice != null) {
			assemblerDevice.setResolved(false);
			assemblerDevice.setAvailable(false);
		}
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
		DeviceFigure device = (DeviceFigure)applicationElements.get(state.getSystemID());
		StateFigure figure = null;
		if (pointer.length() == 0) {
			figure = new StateFigure();
		} else {
			figure = new StateFigure(pointer.getName(pointer.length() -1), 
				pointer.isInstance(pointer.length() - 1));
		}
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
			Pointer parent = new Pointer(pointer, 1);
			Figure parentFigure = (Figure)applicationElements.get(parent);
			PolylineConnection edge = new PolylineConnection();
			edge.setSourceAnchor(new ChopboxAnchor(parentFigure));
			edge.setTargetAnchor(new ChopboxAnchor(figure));	
			applicationGraph.addEdge(edge);
		}
	}
	
	/**
	 * Called whenever a device is resolved.
	 * 
	 * @param device The device that has been resolved.
	 */
	private void updateDevice(Device device) {
		SystemID systemID = device.getSystemID();
		DeviceFigure figure = (DeviceFigure)assemblerElements.get(systemID);
		if (device == null) return;
		figure.setResolved(true);
		figure.setAvailable(true);
	}
	
	/**
	 * Called whenever an item of the algorithm has been created.
	 * 
	 * @param item The item that has been created.
	 */
	private void addItem(AbstractItem item) {
		// retrieve the parent for the configurable
		AbstractItem parent = item.getParent();
		// prepare the figure variable to support all future operations
		ItemFigure figure = null;
		if (item instanceof AbstractBinding) {
			AbstractBinding binding = (AbstractBinding)item;
			BindingFigure bindingFigure = new BindingFigure(binding);
			bindingFigure.updateItem();
			figure = bindingFigure;
		} else if (item instanceof AbstractElement) {
			AbstractElement element = (AbstractElement)item;
			ElementFigure elementFigure = new ElementFigure(element);
			elementFigure.updateItem();
			figure = elementFigure;
			Pointer pointer = element.getPointer();
			if (pointer != null) {
				StateFigure pfig = (StateFigure)applicationElements.get(pointer);
				if (pfig != null) pfig.setReused(true);
			}
		} 
		// add the figure and configurable to the elements table
		assemblerElements.put(item, figure);
		// add the figure to the graph
		Figure entry = (Figure)assemblerElements.get(item.getSystemID());
		assemblerGraph.insertNode(figure, entry);
		// create an edge to the parent, if there is one
		if (parent != null) {
			Figure parentFigure = (Figure)assemblerElements.get(parent);
			PolylineConnection edge = new PolylineConnection();
			edge.setSourceAnchor(new ChopboxAnchor(parentFigure));
			edge.setTargetAnchor(new ChopboxAnchor(figure));	
			assemblerGraph.addEdge(edge);
		}
		zoom();
	}
	
	/**
	 * Called whenever an item that has been created earlier is 
	 * removed.
	 * 
	 * @param item The item that has been removed.
	 */
	private void removeItem(AbstractItem item) {
		ItemFigure figure = (ItemFigure)assemblerElements.remove(item);
		if (figure == null) return;		
		Connection[] edges = assemblerGraph.getEdges();
		for (int i = 0; i < edges.length; i++) {
			Connection edge = edges[i];
			if (edge.getTargetAnchor().getOwner() == figure ||
					edge.getSourceAnchor().getOwner() == figure) {
				assemblerGraph.removeEdge(edge);
			}
		}
		assemblerGraph.removeNode(figure);
		if (item instanceof AbstractElement) {
			AbstractElement element = (AbstractElement)item;
			Pointer pointer = element.getPointer();
			if (pointer != null) {
				StateFigure pfig = (StateFigure)applicationElements.get(pointer);
				if (pfig != null) pfig.setReused(false);
			}
		}
		zoom();
	}
	
	/**
	 * Called whenever some computation has been completed.
	 * 
	 * @param item The item whose compuation has been completed.
	 */
	private void updateItem(AbstractItem item) {
		ItemFigure figure = (ItemFigure)assemblerElements.get(item);
		if (figure == null) return;	
		figure.updateItem();
		figure.repaint();
		zoom();
	}

	/**
	 * Called whenever a binding has been resolved.
	 * 
	 * @param binding The binding that has been resolved.
	 */
	private void resolveItem(AbstractBinding binding) {
		BindingFigure figure = (BindingFigure)assemblerElements.get(binding);
		if (figure == null) return;	
		figure.setResolved(true);
		figure.updateItem();
		figure.repaint();
		zoom();
	}
	
}
