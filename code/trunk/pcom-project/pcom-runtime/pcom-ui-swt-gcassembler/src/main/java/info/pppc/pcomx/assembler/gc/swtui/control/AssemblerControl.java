package info.pppc.pcomx.assembler.gc.swtui.control;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.pcomx.assembler.gc.GCAssembler;
import info.pppc.pcomx.assembler.gc.internal.Application;
import info.pppc.pcomx.assembler.gc.swtui.GCAssemblerUI;
import info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;


/**
 * The gc control is used to visualize the gc assembler's inner workings.
 * 
 * @author Mac
 */
public class AssemblerControl extends AbstractAssemblerControl implements IListener {

	/**
	 * The resource key used to retrieve the text of the control.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gc.swtui.control.AssemblerControl.TEXT";
	
	/**
	 * The instance of the gc assembler that is controlled by this
	 * settings control.
	 */
	private GCAssembler assembler = GCAssembler.getInstance();
	
	/**
	 * Creates a new visualization control for the gc assembler.
	 * 
	 * @param manager The manager that will display the element.
	 */
	public AssemblerControl(IElementManager manager) {
		super(manager);
	}

	/**
	 * Returns the name of the control as shown in the ui.
	 * 
	 * @return The name of the control.
	 */
	public String getName() {
		return GCAssemblerUI.getText(UI_TEXT);
	}

	/**
	 * Returns the image of the control as shown in the ui.
	 * 
	 * @return The image of the control.
	 */
	public Image getImage() {
		return GCAssemblerUI.getImage(GCAssemblerUI.IMAGE_GC);
	}
	
	/**
	 * Called whenver the control is displayed.
	 * 
	 * @param parent The parent to draw the control.
	 */
	public void showControl(Composite parent) {
		super.showControl(parent);
		// add selection listeners that hooks up the visualization onto the gc assembler
		visButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (visButton.getSelection()) {
					assembler.addAssemblerListener(Event.EVENT_EVERYTHING, AssemblerControl.this);
				} else {
					assembler.removeAssemblerListener(Event.EVENT_EVERYTHING, AssemblerControl.this);
				}
			}
		});
		// update the enables states
		updateStates();
		// updte the size of the buttons
		updateSize();
	}
	
	/**
	 * Called whenever the control is disposed. This will automatically unregister
	 * the assembler listener at the local gc assembler.
	 */
	public void disposeControl() {
		assembler.removeAssemblerListener(Event.EVENT_EVERYTHING, this);
		super.disposeControl();
	}
	
	/**
	 * Called whenever the gc assembler that is visualized by this control
	 * starts or stops a new application. Note that this method will not be
	 * called if the assembler is no longer registered.
	 * 
	 * @param event An event that adheres to the description contained in the
	 * 	gc assembler.
	 */
	public void handleEvent(final Event event) {
		switch (event.getType()) {
			case GCAssembler.EVENT_APPLICATION_ADDED: {
				getManager().run(new Runnable() {
					public void run() {
						Application application = (Application)event.getData();
						ApplicationControl control = new ApplicationControl(getManager(), application);
						getManager().addElement(control);
						control.setZoom(zoomButton.getSelection(), zoomCombo.getSelectionIndex());
						control.setStep(stepButton.getSelection(), stepCombo.getSelectionIndex());
						control.setAnimation(animationButton.getSelection(), 
							animationCombo.getSelectionIndex());
						getManager().focusElement(control);				
					}
				});
			}
			break;
			case GCAssembler.EVENT_APPLICATION_REMOVED: {
				getManager().run(new Runnable() {
					public void run() {
						if (! removeOnExit) return;
						Application application = (Application)event.getData();
						AbstractElementControl[] controls = getManager().getElements();
						for (int i = 0; i < controls.length; i++) {
							if (controls[i] instanceof ApplicationControl) {
								ApplicationControl ac = (ApplicationControl)controls[i];
								if (ac.getApplication() == application) {
									getManager().removeElement(ac);
								}
							}
						}										
					};
				});
			}
			break;
			default:
				// should never happen as long as the gc assembler does not
				// declare any additonal evens
		}
	}
	
}
