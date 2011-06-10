package info.pppc.pcom.lcdui.application;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import info.pppc.base.lcdui.element.AbstractElement;
import info.pppc.base.lcdui.element.ElementAction;
import info.pppc.base.lcdui.element.IElementManager;
import info.pppc.base.lcdui.element.action.CloseAction;
import info.pppc.base.lcdui.form.FormComboItem;
import info.pppc.base.lcdui.form.FormItem;
import info.pppc.base.lcdui.form.FormStringItem;
import info.pppc.base.system.DeviceDescription;
import info.pppc.base.system.DeviceRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.lcdui.PcomUI;
import info.pppc.pcom.lcdui.application.action.UpdateAction;
import info.pppc.pcom.system.application.ApplicationDescriptor;

/**
 * The application editor lets the user select the assembler and
 * it lets the user weight his/her preferences.
 * 
 * @author Mac
 */
public class ApplicationEditor extends AbstractElement {

	/**
	 * The text that is shown in the title line.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.lcdui.application.ApplicationEditor.TEXT";
	
	/**
	 * The text of the label next to the application name.
	 */
	private static final String UI_NAME = "info.pppc.pcom.lcdui.application.ApplicationEditor.NAME";
	
	/**
	 * The text of the label next to the assembler.
	 */
	private static final String UI_ASSEMBLER = "info.pppc.pcom.lcdui.application.ApplicationEditor.ASSEMBLER";
	
	/**
	 * The text of the label next to the preferences.
	 */
	private static final String UI_CONTRACTS = "info.pppc.pcom.lcdui.application.ApplicationEditor.CONTRACTS";
	
	/**
	 * The ids of pcom assemblers used to fill the assembler editor dialog.
	 */
	private static final ObjectID[] assemblerIDs = new ObjectID[] {
		new ObjectID(5)
	};
	
	/**
	 * The names of pcom assemblers used to fill the assembler editor dialog.
	 */
	private static final String[] assemblerNames = new String[] {
		"GC Assembler"
	};
	
	/**
	 * The application descriptor that is configured by
	 * the editor.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * The items of the editor.
	 */
	private FormItem[] items;
	
	/**
	 * The actions of the editor.
	 */
	private ElementAction[] actions;

	/**
	 * The form combo that is used for the assemblers.
	 */
	private FormComboItem assembler;
	
	/**
	 * The application element that is used to update
	 * the application descriptor in cases where the
	 * user saves the changes.
	 */
	private ApplicationElement element;
	
	/**
	 * The assembler that are available for selection.
	 */
	private Vector assemblers = new Vector();
	
	/**
	 * Creates a new application editor for the specified
	 * manager using the specified descriptor.
	 * 
	 * @param manager The element manager of the element.
	 * @param element The element that created the editor.
	 * @param descriptor The application descriptor.
	 */
	public ApplicationEditor(IElementManager manager, ApplicationElement element, ApplicationDescriptor descriptor) {
		super(manager);
		this.descriptor = descriptor;
		this.element = element;
		actions = new ElementAction[] {
			new UpdateAction(this),
			new CloseAction(this)
		};
		FormStringItem nameLabel = new FormStringItem("", PcomUI.getText(UI_NAME));
		FormStringItem name = new FormStringItem("", descriptor.getName());
		name.setLayout(FormItem.LAYOUT_LINE_AFTER | FormItem.LAYOUT_CENTER);
		FormStringItem assemblerLabel = new FormStringItem("", PcomUI.getText(UI_ASSEMBLER));
		assemblerLabel.setLayout(FormItem.LAYOUT_LINE_AFTER);
		assembler = new FormComboItem(null);
		assembler.setLayout(FormItem.LAYOUT_EXPAND | FormItem.LAYOUT_LINE_AFTER);
		assembler.setEnabled(true);
		FormStringItem contractsLabel = new FormStringItem("", PcomUI.getText(UI_CONTRACTS));
		updateAssemblers();
		items = new FormItem[] {
			nameLabel, name, assemblerLabel, assembler, contractsLabel
		};
	}

	/**
	 * Updates the assembler list with the assemblers that are currently
	 * available.
	 */
	private void updateAssemblers() {
		DeviceRegistry registry = InvocationBroker.getInstance().getDeviceRegistry();
		for (int i = 0; i < assemblerIDs.length; i++) {
			SystemID[] systems = registry.getDevices(assemblerIDs[i]);
			String assemblerName = assemblerNames[i];
			for (int j = 0; j < systems.length; j++) {
				DeviceDescription description = registry.getDeviceDescription(systems[j]);
				if (description != null) {
					String deviceName = description.getName();
					ReferenceID id = new ReferenceID(systems[j], assemblerIDs[i]);
					assemblers.addElement(id);
					assembler.append(assemblerName + " " + deviceName);	
				}
			}
		}
		int index = assemblers.indexOf(descriptor.getAssemblerID());
		if (index != -1) {
			assembler.select(index);
		}
	}
	
	/**
	 * Returns the application descriptor of the editor.
	 * 
	 * @return The application descriptor of the editor.
	 */
	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Returns the name of the application editor.
	 * 
	 * @return The name of the application editor.
	 */
	public String getName() {
		return PcomUI.getText(UI_TEXT);
	}

	/**
	 * Returns the image of the application editor.
	 * 
	 * @return The image of the application editor.
	 */
	public Image getImage() {
		return PcomUI.getImage(PcomUI.IMAGE_BUILDER);
	}

	/**
	 * Returns the items of the application editor.
	 * 
	 * @return The editors items.
	 */
	public FormItem[] getItems() {
		return items;
	}

	/**
	 * Returns the menu actions of the application editor.
	 * 
	 * @return The menu actions of the editor.
	 */
	public ElementAction[] getActions() {
		return actions;
	}
	
	/**
	 * Called whenever the changes need to be stored.
	 */
	public void update() {
		int index = assembler.getSelection();
		if (index != -1) {
			descriptor.setAssemblerID
				((ReferenceID)assemblers.elementAt(index));
		}
		element.updateApplication(descriptor);
		getManager().focusElement(element);
		getManager().removeElement(this);
	}
}
