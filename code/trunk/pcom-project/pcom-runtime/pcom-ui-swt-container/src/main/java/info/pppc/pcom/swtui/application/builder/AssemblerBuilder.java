package info.pppc.pcom.swtui.application.builder;

import java.util.Vector;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.swtui.widget.ImageButton;
import info.pppc.base.system.DeviceDescription;
import info.pppc.base.system.DeviceRegistry;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * The assembler builder lets the user select an assembler from
 * the set of assemblers that is currently available.
 * 
 * @author Mac
 */
public class AssemblerBuilder extends AbstractBuilder {

	/**
	 * The text for the label that is displayed with the assembler combobox.
	 */
	private static final String UI_ASSEMBLER = "info.pppc.pcom.swtui.application.builder.AssemblerBuilder.ASSEMBLER";
	
	/**
	 * The ids of pcom assemblers used to fill the assembler builder dialog.
	 */
	private static final ObjectID[] assemblerIDs = new ObjectID[] {
		new ObjectID(5), new ObjectID(6), new ObjectID(7)
	};
	
	/**
	 * The names of pcom assemblers used to fill the assembler builder dialog.
	 */
	private static final String[] assemblerNames = new String[] {
		"GC Assembler", "GD Assembler", "M Assembler"
	};
	
	/**
	 * The locality of an assembler used to determine whether remote assemblers
	 * should be shown. Set to true to show only local and to false to show
	 * local as well as remote assemblers. 
	 */
	private static final boolean[] assemblerLocalities = new boolean[] {
		false, true, false
	};
	
	
	/**
	 * The combo box that displays the assemblers.
	 */
	private Combo assemblerCombo;
	
	/**
	 * The label that shows the name.
	 */
	private Label nameLabel;
	
	/**
	 * The image button used to submit the changes.
	 */
	private ImageButton okButton;
	
	/**
	 * The assembler list that contains the assemblers in the same
	 * ordering as contained in the combo (reference ids).
	 */
	private Vector assemblerList;
	
	/**
	 * Creates a new assembler builder using the specified
	 * composite.
	 * 
	 * @param composite The composite that will contain
	 * 	the builder.
	 */
	public AssemblerBuilder(Composite composite) {
		super(composite);
	}

	/**
	 * Creates the assembler builder using the specified
	 * tree node. The node must contain a application 
	 * descriptor.
	 * 
	 * @param node The node containing the applicaiton descriptor.
	 */
	public void createControl(TreeNode node) {
		final ApplicationDescriptor descriptor = (ApplicationDescriptor)node.getData();
		Composite builderComposite = getComposite();
		builderComposite.setLayout(new GridLayout(3, false));
		nameLabel = new Label(builderComposite, SWT.NONE);
		nameLabel.setText(PcomUI.getText(UI_ASSEMBLER));
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		assemblerList = new Vector();
		assemblerCombo = new Combo(builderComposite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		// retrieve all assemblers from the local device registry
		InvocationBroker broker = InvocationBroker.getInstance();
		DeviceRegistry deviceRegistry = broker.getDeviceRegistry();
		ObjectRegistry objectRegistry = broker.getObjectRegistry();
		for (int i = 0; i < assemblerIDs.length; i++) {
			SystemID[] systems = new SystemID[0];
			if (assemblerLocalities[i]) {
				if (objectRegistry.isIdentifierRegistered(assemblerIDs[i])) {
					systems = new SystemID[] { SystemID.SYSTEM };
				} else {
					Logging.log(getClass(), "Noid");
				}
			} else {
				systems = deviceRegistry.getDevices(assemblerIDs[i]);
			}
			String assemblerName = assemblerNames[i];
			for (int j = 0; j < systems.length; j++) {
				DeviceDescription description = deviceRegistry.getDeviceDescription(systems[j]);
				if (description != null) {
					String deviceName = description.getName();
					ReferenceID id = new ReferenceID(systems[j], assemblerIDs[i]);
					assemblerList.addElement(id);
					assemblerCombo.add(assemblerName + " " + deviceName);	
				}
			}
		}
		if (assemblerCombo.getItemCount() > 0) {
			assemblerCombo.select(0);
		}
		assemblerCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		okButton = new ImageButton(builderComposite, SWT.NONE);
		okButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_OK));
		okButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		okButton.setEnabled(assemblerCombo.getSelectionIndex() != -1);
		builderComposite.layout(true);
		// add the listeners
		assemblerCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				okButton.setEnabled(assemblerCombo.getSelectionIndex() != -1);
			}
		});
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = assemblerCombo.getSelectionIndex();
				if (index != -1) {
					ReferenceID assembler = (ReferenceID)assemblerList.elementAt(index);
					descriptor.setAssemblerID(assembler);
				}
				done();
			}
		});
	}

	/**
	 * Disposes the control.
	 */
	public void dispose() {
		nameLabel.dispose();
		assemblerCombo.dispose();
		okButton.dispose();
		nameLabel = null;
		assemblerCombo = null;
		okButton = null;
		super.dispose();
	}
	
}
