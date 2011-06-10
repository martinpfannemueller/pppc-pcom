package info.pppc.pcom.swtui.application.builder;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.swtui.widget.ImageButton;
import info.pppc.base.system.util.Comparator;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.IContract;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The contract builder is used to manipulate a single contract.
 * 
 * @author Mac
 */
public class ContractBuilder extends AbstractBuilder {

	/**
	 * The resource key for the name label text.
	 */
	private static final String UI_NAME = "info.pppc.pcom.swtui.application.builder.ContractBuilder.NAME";
	
	/**
	 * The resource key for the type label text.
	 */
	private static final String UI_TYPE = "info.pppc.pcom.swtui.application.builder.ContractBuilder.TYPE";
	
	/**
	 * The resource key fo the value label text.
	 */
	private static final String UI_VALUE = "info.pppc.pcom.swtui.application.builder.ContractBuilder.VALUE";
	
	/**
	 * The types of comparators.
	 */
	private static final int[] comparatorTypes = new int[] {
		IContract.IFLT, 
		IContract.IFLE, 
		IContract.IFEQ, 
		IContract.IFGE, 
		IContract.IFGT, 
		IContract.IFIR, 
		IContract.IFOR
	};
	
	/**
	 * The names of comparators as shown in the ui.
	 */
	private static final String[] comparatorNames = new String[] {
		"<",
		"<=",
		"==",
		">=",
		">",
		"[,]",
		"],["
	};
	
	/**
	 * Flags that indicate whether the comparator at the same
	 * position is a range comparator.
	 */
	private static final boolean[] comparatorRanges = new boolean[] {
		false,
		false,
		false,
		false,
		false,
		true,
		true
	};
	
	/**
	 * The type names as shown in the user interface.
	 */
	private static final String[] typeNames = new String[] {
		"String",
		"Boolean",
		"Integer",
		"Long"
	};
	
	/**
	 * The name label of the builder.
	 */
	private Label nameLabel;
	
	/**
	 * The name text of the builder.
	 */
	private Text nameText;
	
	/**
	 * The ok button of the builder.
	 */
	private ImageButton okButton;
	
	/**
	 * The type label of the builder (only for features).
	 */
	private Label typeLabel;
	
	/**
	 * The type combo of the builder (only for features).
	 */
	private Combo typeCombo;
	
	/**
	 * The comparator combo of the builder (only for features).
	 */
	private Combo comparatorCombo;
	
	/**
	 * The value label of the builder (only for features).
	 */
	private Label valueLabel;
	
	/**
	 * The value text of the builder (only for features).
	 */
	private Text valueText;
	
	/**
	 * The range text of the builder (only for features).
	 */
	private Text rangeText;
	
	/**
	 * The type label for spacing (only for features).
	 */
	private Label typeSpace;
	
	/**
	 * The value label for spacing (only for features).
	 */
	private Label valueSpace;
	
	/**
	 * The contract configured by the builder.
	 */
	private Contract contract;
	
	/**
	 * The parent contract if any. This is used to check references.
	 */
	private Contract parent;
	
	/**
	 * Creates a new contract builder using the specified composite.
	 * 
	 * @param composite The composite used to display the builder.
	 */
	public ContractBuilder(Composite composite) {
		super(composite);
	}

	/**
	 * Creates the contract builder for the specified tree node.
	 * The node must contain a contract as data object.
	 * 
	 * @param node The tree node that contains the contract that
	 * 	should be edited.
	 */
	public void createControl(TreeNode node) {
		contract = (Contract)node.getData();
		if (node.getParent() != null && node.getParent().getData() instanceof Contract) {
			parent = (Contract)node.getParent().getData();
		}
		Composite builderComposite = getComposite();
		builderComposite.setLayout(new GridLayout(4, false));
		// create name fields and ok button
		nameLabel = new Label(builderComposite, SWT.NONE);
		nameLabel.setText(PcomUI.getText(UI_NAME));
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		nameText = new Text(builderComposite, SWT.SINGLE | SWT.BORDER);
		if (contract.getName() != null) {
			nameText.setText(contract.getName());	
		}
		nameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				update();
			}
		});
		okButton = new ImageButton(builderComposite, SWT.NONE);
		okButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_OK));
		okButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		if (contract.getType() == Contract.TYPE_FEATURE_DEMAND) {
			typeLabel = new Label(builderComposite, SWT.NONE);
			typeLabel.setText(PcomUI.getText(UI_TYPE));
			typeLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
			comparatorCombo = new Combo(builderComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			comparatorCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
			comparatorCombo.setItems(comparatorNames);
			comparatorCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					update();
				}
			});
			// select the comparator contained in the contract
			int compIndex = 0;
			Integer comparator = (Integer)contract.getAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR);
			if (comparator != null) {
				for (int i = 0; i < comparatorTypes.length; i++) {
					if (comparatorTypes[i] == comparator.intValue()) {
						compIndex = i;
						break;
					}
				}
			}
			comparatorCombo.select(compIndex);
			typeCombo = new Combo(builderComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			typeCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
			typeCombo.setItems(typeNames);
			typeCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					update();
				}
			});
			// select the type contained in the contract
			Object value = null;
			if (comparatorRanges[compIndex]) {
				value = contract.getAttribute(Contract.ATTRIBUTE_FEATURE_MINIMUM);
			} else {
				value = contract.getAttribute(Contract.ATTRIBUTE_FEATURE_VALUE);
			}
			if (value != null) {
				if (value instanceof String) {
					typeCombo.select(0);
				} else if (value instanceof Long) {
					typeCombo.select(3);
				} else if (value instanceof Boolean) {
					typeCombo.select(1);
				} else if (value instanceof Integer) {
					typeCombo.select(2);
				}
			}
			typeSpace = new Label(builderComposite, SWT.NONE);
			typeSpace.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 1, 1));
			valueLabel = new Label(builderComposite, SWT.NONE);
			valueLabel.setText(PcomUI.getText(UI_VALUE));
			valueLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
			valueText = new Text(builderComposite, SWT.SINGLE | SWT.BORDER);
			valueText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			valueText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					update();
				}
			});
			rangeText = new Text(builderComposite, SWT.SINGLE | SWT.BORDER);
			rangeText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));			
			rangeText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					update();
				}
			});
			valueSpace = new Label(builderComposite, SWT.NONE);
			valueSpace.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 1, 1));
			// update the value and range values according to the contract
			if (comparatorRanges[compIndex]) {
				Object max = contract.getAttribute(Contract.ATTRIBUTE_FEATURE_MAXIMUM);
				Object min = contract.getAttribute(Contract.ATTRIBUTE_FEATURE_MINIMUM);
				if (max != null) rangeText.setText(max.toString());
				if (min != null) valueText.setText(min.toString());
			} else {
				Object val = contract.getAttribute(Contract.ATTRIBUTE_FEATURE_VALUE);
				if (val != null) valueText.setText(val.toString());
			}
		}
		// add the listeners
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// apply the current configuration
				contract.setName(nameText.getText().trim());
				if (contract.getType() == Contract.TYPE_FEATURE_DEMAND) {
					int comp = comparatorCombo.getSelectionIndex();
					int type = typeCombo.getSelectionIndex();	
					contract.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, 
							new Integer(comparatorTypes[comp]));
					if (comparatorRanges[comp]) {
						contract.removeAttribute(Contract.ATTRIBUTE_FEATURE_VALUE);
						contract.setAttribute(Contract.ATTRIBUTE_FEATURE_MAXIMUM, 
								getTypedValue(valueText.getText().trim(), type));
						contract.setAttribute(Contract.ATTRIBUTE_FEATURE_MINIMUM,
								getTypedValue(rangeText.getText().trim(), type));
					} else {
						contract.removeAttribute(Contract.ATTRIBUTE_FEATURE_MAXIMUM);
						contract.removeAttribute(Contract.ATTRIBUTE_FEATURE_MINIMUM);
						contract.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE,
								getTypedValue(valueText.getText().trim(), type));
					}					
				}
				done();
			}
		});
		// update the view
		builderComposite.layout(true);
		update();
	}
	
	/**
	 * Called to update the state of the contract builder. This
	 * should be called whenever the state changes, ie. if the
	 * user has made some input.
	 */
	protected void update() {
		// only enable if parent does not have a child with the same name
		Contract c = null;
		if (parent != null) {
			c = parent.getContract(contract.getType(), nameText.getText().trim());
		} 
		okButton.setEnabled(c == contract || c == null);
		if (contract.getType() == Contract.TYPE_FEATURE_DEMAND) {
			// check whether a range comparator is selected
			int comp = comparatorCombo.getSelectionIndex();
			int type = typeCombo.getSelectionIndex();
			if (comp == -1 || type == -1) {
				okButton.setEnabled(false);
				return;
			} else {
				if (comparatorRanges[comp]) {
					rangeText.setEnabled(true);
					boolean min = validateType(valueText.getText().trim(), type);
					boolean max = validateType(rangeText.getText().trim(), type);
					okButton.setEnabled(okButton.isEnabled() && min && max);
				} else {
					rangeText.setEnabled(false);
					boolean val = validateType(valueText.getText().trim(), type);
					okButton.setEnabled(okButton.isEnabled() && val);
				}				
			}
		}
	}
	
	/**
	 * Determines whether the type in the string matches the type
	 * selected in the type combobox.
	 * 
	 * @param string The string that needs to be checked.
	 * @param type The selection of the type combobox.
	 * @return True if there is a match, false otherwise.
	 */
	private boolean validateType(String string, int type) {
		switch (type) {
			case 0:
				return true;
			case 1:
				try {
					Comparator.getBoolean(string);
					return true;
				} catch (IllegalArgumentException t) {
					return false;
				}
			case 2:
				try {
					Integer.parseInt(string);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			case 3:
				try {
					Long.parseLong(string);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			default:
				// nothing to be done here
		}
		return false;
	}
	
	/**
	 * Returns the typed value of the string according to the
	 * type index passed to the method.
	 * 
	 * @param string The string to convert.
	 * @param type The type index as defined by the type name array.
	 * @return The converted string or null if the conversion failed.
	 */
	private Object getTypedValue(String string, int type) {
		switch (type) {
			case 0:
				return string;
			case 1:
				try {
					return new Boolean(Comparator.getBoolean(string));
				} catch (IllegalArgumentException t) {
					return null;
				}
			case 2:
				try {
					return new Integer(Integer.parseInt(string));
				} catch (NumberFormatException e) {
					return null;
				}
			case 3:
				try {
					return new Long(Long.parseLong(string));
				} catch (NumberFormatException e) {
					return null;
				}
			default:
				// nothing to be done here
		}
		return null;
	}

	/**
	 * Disposes the controls created by the builder.
	 */
	public void dispose() {
		nameLabel.dispose();
		nameText.dispose();
		okButton.dispose();
		if (contract.getType() == Contract.TYPE_FEATURE_DEMAND) {
			typeLabel.dispose();
			typeCombo.dispose();
			comparatorCombo.dispose();
			valueLabel.dispose();
			valueText.dispose();
			rangeText.dispose();
			typeSpace.dispose();
			valueSpace.dispose();
		}
		nameLabel = null;
		nameText = null;
		okButton = null;
		typeLabel = null;
		typeCombo = null;
		comparatorCombo = null;
		valueLabel = null;
		valueText = null;
		rangeText = null;
		typeSpace = null;
		valueSpace = null;
		contract = null;
		parent = null;
		super.dispose();
	}
}
