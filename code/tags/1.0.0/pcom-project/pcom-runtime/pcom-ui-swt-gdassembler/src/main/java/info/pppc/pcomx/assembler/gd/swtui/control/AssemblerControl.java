package info.pppc.pcomx.assembler.gd.swtui.control;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.pcomx.assembler.gd.GDAssembler;
import info.pppc.pcomx.assembler.gd.internal.Application;
import info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI;
import info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl;

/**
 * The assembler control is used to configure the gd assembler visualization.
 * 
 * @author Mac
 */
public class AssemblerControl extends AbstractAssemblerControl implements IListener {

	/**
	 * The text displayed in the tab.
	 */
	private static final String UI_TEXT = "info.pppc.pcomx.assembler.gd.swtui.control.AssemblerControl.TEXT";
	
	/**
	 * The assembler that is visualized by this control.
	 */
	private GDAssembler assembler = GDAssembler.getInstance();
	
	/**
	 * Creates a new assembler control with the specified manager.
	 * 
	 * @param manager The manager used for ui interaction.
	 */
	public AssemblerControl(IElementManager manager) {
		super(manager);
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
	 * Returns the text displayed in the tab.
	 * 
	 * @return The text displayed in the tab.
	 */
	public String getName() {
		return GDAssemblerUI.getText(UI_TEXT);
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
			case GDAssembler.EVENT_APPLICATION_ADDED: {
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
			case GDAssembler.EVENT_APPLICATION_REMOVED: {
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
				// should never happen as long as the gd assembler does not
				// declare any additonal evens
		}
	}

}
