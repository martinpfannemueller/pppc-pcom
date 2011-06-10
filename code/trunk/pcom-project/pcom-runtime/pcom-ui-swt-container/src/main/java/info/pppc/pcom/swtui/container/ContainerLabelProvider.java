package info.pppc.pcom.swtui.container;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.contract.Contract;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * The container label provider provides labels for the model created by
 * the container. This model is based on tree nodes with container content
 * objects.
 * 
 * @author Mac
 */
public class ContainerLabelProvider extends LabelProvider implements IContainerModel {

	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_EXCEPTION = "info.pppc.pcom.swtui.container.ContainerLabelProvider.EXCEPTION";

	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_FACTORIES = "info.pppc.pcom.swtui.container.ContainerLabelProvider.FACTORIES";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_FACTORY = "info.pppc.pcom.swtui.container.ContainerLabelProvider.FACTORY";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_FACTORY_ID = "info.pppc.pcom.swtui.container.ContainerLabelProvider.FACTORY_ID";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_FACTORY_NAME = "info.pppc.pcom.swtui.container.ContainerLabelProvider.FACTORY_NAME";

	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_FACTORY_TEMPLATE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.FACTORY_TEMPLATE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_FACTORY_STATUS = "info.pppc.pcom.swtui.container.ContainerLabelProvider.FACTORY_STATUS";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_INSTANCES = "info.pppc.pcom.swtui.container.ContainerLabelProvider.INSTANCES";

	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_INSTANCE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.INSTANCE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_INSTANCE_ID = "info.pppc.pcom.swtui.container.ContainerLabelProvider.INSTANCE_ID";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_INSTANCE_NAME = "info.pppc.pcom.swtui.container.ContainerLabelProvider.INSTANCE_NAME";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_INSTANCE_TEMPLATE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.INSTANCE_TEMPLATE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_INSTANCE_STATUS = "info.pppc.pcom.swtui.container.ContainerLabelProvider.INSTANCE_STATUS";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATORS = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATORS";

	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR_ID = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_ID";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR_NAME = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_NAME";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR_TOTAL = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_TOTAL";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR_FREE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_FREE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR_TEMPLATE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_TEMPLATE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_ALLOCATOR_STATUS = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_STATUS";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCES = "info.pppc.pcom.swtui.container.ContainerLabelProvider.ALLOCATOR_RESOURCES";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.RESOURCE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCE_ID = "info.pppc.pcom.swtui.container.ContainerLabelProvider.RESOURCE_ID";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCE_NAME = "info.pppc.pcom.swtui.container.ContainerLabelProvider.RESOURCE_NAME";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCE_USE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.RESOURCE_USE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCE_TEMPLATE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.RESOURCE_TEMPLATE";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_RESOURCE_STATUS = "info.pppc.pcom.swtui.container.ContainerLabelProvider.RESOURCE_STATUS";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_CONTRACT = "info.pppc.pcom.swtui.container.ContainerLabelProvider.CONTRACT";
	
	/**
	 * The resource key for the corresponding node in the properties file.
	 */
	public static final String UI_CONTRACT_ATTRIBUTE = "info.pppc.pcom.swtui.container.ContainerLabelProvider.CONTRACT_ATTRIBUTE";


	/**
	 * Creates a new container label provider.
	 */
	public ContainerLabelProvider() {
		super();
	}

	/**
	 * Returns the text for the specified model element.
	 * 
	 * @param element An element of the model. This will be a
	 * 	tree node with a type that is defined in the container
	 * 	model interface.
	 * @return The label for the specified element.
	 */
	public String getText(Object element) {
		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode)element;
			switch (node.getType()) {
				case TYPE_ALLOCATOR:
					{
						TreeNode[] nodes = node.getChildren(TYPE_ALLOCATOR_NAME, false);
						if (nodes.length == 1) return getString(nodes[0].getData());
						else return PcomUI.getText(UI_ALLOCATOR);
					}	
				case TYPE_ALLOCATOR_FREE:
					return PcomUI.getText(UI_ALLOCATOR_FREE) + toText(node.getData());
				case TYPE_ALLOCATOR_ID:
					return PcomUI.getText(UI_ALLOCATOR_ID) + toText(node.getData());
				case TYPE_ALLOCATOR_NAME:
					return PcomUI.getText(UI_ALLOCATOR_NAME) + toText(node.getData());
				case TYPE_ALLOCATOR_STATUS:
					return PcomUI.getText(UI_ALLOCATOR_STATUS);
				case TYPE_ALLOCATOR_TEMPLATE:
					return PcomUI.getText(UI_ALLOCATOR_TEMPLATE);
				case TYPE_ALLOCATOR_TOTAL:
					return PcomUI.getText(UI_ALLOCATOR_TOTAL) + toText(node.getData());
				case TYPE_ALLOCATORS:
					return PcomUI.getText(UI_ALLOCATORS);
				case TYPE_CONTRACT:
					return PcomUI.getText(UI_CONTRACT) + toText((Contract)node.getData());
				case TYPE_CONTRACT_ATTRIBUTE:
					return PcomUI.getText(UI_CONTRACT_ATTRIBUTE) 
						+ toText((Contract)node.getParent().getData(), (Byte)node.getData());
				case TYPE_EXCEPTION:
					return PcomUI.getText(UI_EXCEPTION) + toText(node.getData());
				case TYPE_FACTORIES:
					return PcomUI.getText(UI_FACTORIES);
				case TYPE_FACTORY:
					{
						TreeNode[] nodes = node.getChildren(TYPE_FACTORY_NAME, false);
						if (nodes.length == 1) return getString(nodes[0].getData());
						else return PcomUI.getText(UI_FACTORY);
					}	
				case TYPE_FACTORY_ID:
					return PcomUI.getText(UI_FACTORY_ID) + toText(node.getData());
				case TYPE_FACTORY_NAME:
					return PcomUI.getText(UI_FACTORY_NAME) + toText(node.getData());
				case TYPE_FACTORY_STATUS:
					return PcomUI.getText(UI_FACTORY_STATUS);
				case TYPE_FACTORY_TEMPLATE:
					return PcomUI.getText(UI_FACTORY_TEMPLATE);
				case TYPE_INSTANCE:
					{
						TreeNode[] nodes = node.getChildren(TYPE_INSTANCE_NAME, false);
						if (nodes.length == 1) return getString(nodes[0].getData());
						else return PcomUI.getText(UI_INSTANCE);
					}				
				case TYPE_INSTANCE_ID:
					return PcomUI.getText(UI_INSTANCE_ID) + toText(node.getData());
				case TYPE_INSTANCE_NAME:
					return PcomUI.getText(UI_INSTANCE_NAME) + toText(node.getData());
				case TYPE_INSTANCE_STATUS:
					return PcomUI.getText(UI_INSTANCE_STATUS);
				case TYPE_INSTANCE_TEMPLATE:
					return PcomUI.getText(UI_INSTANCE_TEMPLATE);
				case TYPE_INSTANCES:
					return PcomUI.getText(UI_INSTANCES);
				case TYPE_NULL:
					return "";
				case TYPE_RESOURCE:
					{
						TreeNode[] nodes = node.getChildren(TYPE_RESOURCE_NAME, false);
						if (nodes.length == 1) return getString(nodes[0].getData());
						else return PcomUI.getText(UI_RESOURCE);
					}	
				case TYPE_RESOURCE_ID:
					return PcomUI.getText(UI_RESOURCE_ID) + toText(node.getData());
				case TYPE_RESOURCE_NAME:
					return PcomUI.getText(UI_RESOURCE_NAME) + toText(node.getData());
				case TYPE_RESOURCE_STATUS:
					return PcomUI.getText(UI_RESOURCE_STATUS);
				case TYPE_RESOURCE_TEMPLATE:
					return PcomUI.getText(UI_RESOURCE_TEMPLATE);
				case TYPE_RESOURCE_USE:
					return PcomUI.getText(UI_RESOURCE_USE) + toText(node.getData());
				case TYPE_RESOURCES:
					return PcomUI.getText(UI_RESOURCES);
				default:
					// nothing to be done here
			}
		}
		return "";
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
		} else if (o instanceof Throwable) {
			return ((Throwable)o).getMessage();
		} else if (o instanceof int[]) {
			int[] ia = (int[])o;
			String s = " (";
			for (int i = 0; i < ia.length; i++) {
				s += ia[i];
				if (ia.length - 1 != i) {
					s += ", ";
				}
			}
			return s + ")";
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
	
	/**
	 * Returns a string representation of the specified object. This is
	 * the to string representation of the object or some other value for null.
	 * 
	 * @param o The object to convert to string.
	 * @return The string for the object or an empty string if null.
	 */
	private String getString(Object o) {
		if (o == null) return "";
		else return o.toString();
	}
	

	/**
	 * Returns an image for the specified model element.
	 * 
	 * @param element The model element to lookup.
	 * @return The image for the element.
	 */
	public Image getImage(Object element) {
		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode)element;
			switch (node.getType()) {
				case TYPE_NULL:
					return PcomUI.getImage(PcomUI.IMAGE_CONTAINER);
				case TYPE_EXCEPTION:
					return BaseUI.getImage(BaseUI.IMAGE_EXIT);
				case TYPE_FACTORIES:
				case TYPE_FACTORY:
					return PcomUI.getImage(PcomUI.IMAGE_FACTORY);
				case TYPE_INSTANCE:
				case TYPE_INSTANCES:
					return PcomUI.getImage(PcomUI.IMAGE_INSTANCE);
				case TYPE_RESOURCE:
				case TYPE_RESOURCES:
				case TYPE_RESOURCE_USE:
					return PcomUI.getImage(PcomUI.IMAGE_RESOURCE);
				case TYPE_ALLOCATORS:
				case TYPE_ALLOCATOR:
				case TYPE_ALLOCATOR_FREE:
				case TYPE_ALLOCATOR_TOTAL:
					return PcomUI.getImage(PcomUI.IMAGE_ALLOCATOR);
				case TYPE_INSTANCE_ID:
				case TYPE_RESOURCE_ID:
				case TYPE_ALLOCATOR_ID:
				case TYPE_FACTORY_ID:
					return BaseUI.getImage(BaseUI.IMAGE_IDENTIFIER);
				case TYPE_INSTANCE_NAME:
				case TYPE_RESOURCE_NAME:
				case TYPE_ALLOCATOR_NAME:
				case TYPE_FACTORY_NAME:
					return BaseUI.getImage(BaseUI.IMAGE_NAME);
				case TYPE_INSTANCE_STATUS:
				case TYPE_INSTANCE_TEMPLATE:
				case TYPE_FACTORY_STATUS:
				case TYPE_FACTORY_TEMPLATE:
				case TYPE_ALLOCATOR_STATUS:
				case TYPE_ALLOCATOR_TEMPLATE:
				case TYPE_RESOURCE_STATUS:
				case TYPE_RESOURCE_TEMPLATE:
				case TYPE_CONTRACT:
					return PcomUI.getImage(PcomUI.IMAGE_CONTRACT);
				case TYPE_CONTRACT_ATTRIBUTE:
					return BaseUI.getImage(BaseUI.IMAGE_PROPERTY);
				default:
					// fall through
			}
		} 
		return null;
	}


}
