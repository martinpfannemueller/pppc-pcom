package info.pppc.pcom.swtui.application.builder;

import java.util.Vector;

import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.pcom.swtui.tree.DirtyTreeNode;
import info.pppc.pcom.swtui.tree.ICleaner;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.contract.Contract;

/**
 * The application model provider provides a cleanable tree model
 * of an application descriptor.
 * 
 * @author Mac
 */
public class ApplicationModelProvider implements IApplicationModel, ICleaner {

	/**
	 * The application model provider used to create tree models
	 * from application descriptors.
	 */
	public ApplicationModelProvider() {
		super();
	}
	
	/**
	 * Returns the model for the specified application descriptor.
	 * 
	 * @param descriptor The descriptor that should be used to create
	 * 	the model.
	 * @return The tree node that can be used to bootstrap the tree.
	 */
	public TreeNode getModel(ApplicationDescriptor descriptor) {
		DirtyTreeNode result = new DirtyTreeNode(TYPE_DESCRIPTOR, descriptor);
		result.dirty();
		return result;
	}

	/**
	 * Cleans the dirty tree node by refreshing it with its immediate
	 * chldren.
	 * 
	 * @param node The node that should be cleaned.
	 */
	public void clean(DirtyTreeNode node) {
		node.removeAllChildren();
		switch (node.getType()) {
			case TYPE_DESCRIPTOR:
				ApplicationDescriptor d = (ApplicationDescriptor)node.getData();
				if (d.getApplicationID() != null) {
					node.addChild(new TreeNode(TYPE_IDENTIFIER, d.getApplicationID()));	
				}
				node.addChild(new TreeNode(TYPE_NAME, d.getName()));
				node.addChild(new TreeNode(TYPE_ASSEMBLER, d.getAssemblerID()));
				node.addChild(new TreeNode(TYPE_IMAGE, d.getImage()));
				if (d.getPreferences() == null) {
					d.setPreferences(new Vector());
				}
				DirtyTreeNode dtn = new DirtyTreeNode(TYPE_CONTRACTS, d.getPreferences());
				dtn.dirty();
				node.addChild(dtn);
				break;
			case TYPE_CONTRACTS:
				Vector v = (Vector)node.getData();
				if (v == null) return;
				for (int i = 0; i < v.size(); i++) {
					Contract c = (Contract)v.elementAt(i);
					DirtyTreeNode dirtyTemplate = new DirtyTreeNode(TYPE_CONTRACT, c);
					dirtyTemplate.dirty();
					node.addChild(dirtyTemplate);
				}
				break;
			case TYPE_CONTRACT:
				Contract c = (Contract)node.getData();
				if (c == null) return;
				byte[] attribs = c.getAttributes();
				for (int i = 0; i < attribs.length; i++) {
					node.addChild(new TreeNode(TYPE_ATTRIBUTE, new Byte(attribs[i])));
				}
				Contract[] cs = c.getContracts();
				for (int i = 0; i < cs.length; i++) {
					DirtyTreeNode dirtyContract = new DirtyTreeNode(TYPE_CONTRACT, cs[i]);
					dirtyContract.dirty();
					node.addChild(dirtyContract);
				}
				break;
			default:
				// will never happen
		}	
	}

}
