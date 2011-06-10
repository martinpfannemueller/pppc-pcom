package info.pppc.pcom.swtui.application.builder;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.swtui.widget.ImageButton;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The name builder generates a dialog that lets a user edit
 * the name of the application. The input must be a node of
 * type descriptor.
 * 
 * @author Mac
 */
public class NameBuilder extends AbstractBuilder {

	/**
	 * The ok button of the builder.
	 */
	private ImageButton okButton;
	
	/**
	 * The name label of the builder.
	 */
	private Label nameLabel;
	
	/**
	 * The name text of the builder.
	 */
	private Text nameText;
	
	/**
	 * Creates the name builder using the specified composite.
	 * 
	 * @param composite The composite used by the builder.
	 */
	public NameBuilder(Composite composite) {
		super(composite);
	}

	/**
	 * Creates the control using the specified tree node
	 * as input.
	 * 
	 * @param node The tree node. The data object contained in
	 * 	the node must be an application descriptor.
	 */
	public void createControl(TreeNode node) {
		final ApplicationDescriptor descriptor = (ApplicationDescriptor)node.getData();
		Composite builderComposite = getComposite();
		builderComposite.setLayout(new GridLayout(3, false));
		nameLabel = new Label(builderComposite, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		nameText = new Text(builderComposite, SWT.SINGLE | SWT.BORDER);
		if (descriptor.getName() != null) {
			nameText.setText(descriptor.getName());	
		}
		nameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		okButton = new ImageButton(builderComposite, SWT.NONE);
		okButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_OK));
		okButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		builderComposite.layout(true);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				descriptor.setName(nameText.getText().trim());
				done();
			}
		});
	}
	
	/**
	 * Disposes the widgets created by the builder.
	 */
	public void dispose() {
		okButton.dispose();
		nameLabel.dispose();
		nameText.dispose();
		okButton = null;
		nameLabel = null;
		nameText = null;
		super.dispose();
	}

}
