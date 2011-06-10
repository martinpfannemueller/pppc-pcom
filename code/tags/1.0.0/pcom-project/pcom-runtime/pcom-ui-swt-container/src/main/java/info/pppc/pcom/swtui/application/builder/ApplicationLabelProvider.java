package info.pppc.pcom.swtui.application.builder;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.contract.Contract;

/**
 * Returns the labels for the application tree model.
 * 
 * @author Mac
 */
public class ApplicationLabelProvider extends LabelProvider implements IApplicationModel {

	/**
	 * The description for the preferences node.
	 */
	private static final String UI_CONTRACTS = "info.pppc.pcom.swtui.application.builder.LabelProvider.CONTRACTS";
	
	/**
	 * The description for the image node.
	 */
	private static final String UI_IMAGE = "info.pppc.pcom.swtui.application.builder.LabelProvider.IMAGE";
	
	/**
	 * The description for the name node.
	 */
	private static final String UI_NAME = "info.pppc.pcom.swtui.application.builder.LabelProvider.NAME";
	
	/**
	 * The description for the assembler node.
	 */
	private static final String UI_ASSEMBLER = "info.pppc.pcom.swtui.application.builder.LabelProvider.ASSEMBLER";
	
	/**
	 * The description for the id node.
	 */
	private static final String UI_IDENTIFIER = "info.pppc.pcom.swtui.application.builder.LabelProvider.IDENTIFIER";
	
	/**
	 * Creates a new application label provider for the applicaton model.
	 */
	public ApplicationLabelProvider() {
		super();
	}
	
	/**
	 * Returns the image for the specified element.
	 * 
	 * @param element The element whose image should be retrieved.
	 * @return The image for the specified element.
	 */
	public Image getImage(Object element) {
		if (element != null && element instanceof TreeNode) {
			TreeNode node = (TreeNode)element;
			switch (node.getType()) {
				case TYPE_IDENTIFIER:
					return BaseUI.getImage(BaseUI.IMAGE_IDENTIFIER);
				case TYPE_ASSEMBLER:
					return BaseUI.getImage(BaseUI.IMAGE_IDENTIFIER);
				case TYPE_NAME:
					return BaseUI.getImage(BaseUI.IMAGE_NAME);
				case TYPE_IMAGE:
					return BaseUI.getImage(BaseUI.IMAGE_NAME);
				case TYPE_CONTRACTS:
					return PcomUI.getImage(PcomUI.IMAGE_BUILDER);
				case TYPE_CONTRACT:
					return PcomUI.getImage(PcomUI.IMAGE_CONTRACT);
				case TYPE_ATTRIBUTE:
					return BaseUI.getImage(BaseUI.IMAGE_PROPERTY);
				default:
					// will never happen due to model
			}
		}
		return null;
	}
	
	/**
	 * Returns the text for the specified element.
	 * 
	 * @param element The element whose text should be retrieved.
	 * @return The text for the specified element.
	 */
	public String getText(Object element) {
		if (element != null && element instanceof TreeNode) {
			TreeNode node = (TreeNode)element;
			switch (node.getType()) {
				case TYPE_DESCRIPTOR:
					return null;
				case TYPE_ASSEMBLER:
					return PcomUI.getText(UI_ASSEMBLER) + toText(node.getData());
				case TYPE_IDENTIFIER:
					return PcomUI.getText(UI_IDENTIFIER) + toText(node.getData());
				case TYPE_IMAGE:
					return PcomUI.getText(UI_IMAGE) + toText(node.getData());
				case TYPE_NAME:
					return PcomUI.getText(UI_NAME) + toText(node.getData());
				case TYPE_CONTRACTS:
					return PcomUI.getText(UI_CONTRACTS);
				case TYPE_CONTRACT:
					return toText((Contract)node.getData());
				case TYPE_ATTRIBUTE:
					return toText((Contract)node.getParent().getData(), 
								(Byte)node.getData());
				default:
					// will never happen due to model
			}
		}
		return null;
	}

	
	/**
	 * Returns the textual representation for the specified object.
	 * 
	 * @param o The object used to retrieve the representation.
	 * @return The text for the object.
	 */
	private String toText(Object o) {
		if (o == null) {
			return " ()";
		} if (o instanceof byte[]) {
			return " (" + ((byte[])o).length + ")";
		} else {
			return " (" + o.toString() + ")";
		}
	}
	
	/**
	 * Returns the text for the specified contract.
	 * 
	 * @param contract The contract used to create the string.
	 * @return The string for the contract.
	 */
	private String toText(Contract contract) {
		return " (" + Contract.TYPE_NAMES[contract.getType()] + "=" + contract.getName() + ")";
	}
	
	/**
	 * Creates the text representation for the attribute of the specified type
	 * from the specified contract.
	 * 
	 * @param contract The contract that contains the attribute.
	 * @param attribute The type of the attribute to retrieve.
	 * @return The string for the contract.
	 */
	private String toText(Contract contract, Byte attribute) {
		byte b = attribute.byteValue();
		return " (" + Contract.ATTRIBUTE_NAMES[b] + "=" + contract.getAttribute(b) + ")";
	}
}
