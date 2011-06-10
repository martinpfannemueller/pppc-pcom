package info.pppc.pcom.swtui.tree;

import info.pppc.base.swtui.tree.TreeNode;

/**
 * The dirty tree node is a tree node that can be set to dirty.
 * Using a dirty tree node provider, this tree node can be used
 * to create partially updateable trees.
 * 
 * @author Mac
 */
public class DirtyTreeNode extends TreeNode {

	/**
	 * A flag that indicates whether the node is marked
	 * dirty.
	 */
	private boolean dirty;
	
	/**
	 * Creates a dirty tree node with the specified type.
	 * 
	 * @param type The type of the tree node.
	 */
	public DirtyTreeNode(int type) {
		super(type);
	}

	/**
	 * Creates a new dirty tree node with the specified type
	 * and data object.
	 * 
	 * @param type The type of the tree node.
	 * @param data The data object contained in the node.
	 */
	public DirtyTreeNode(int type, Object data) {
		super(type, data);
	}
	
	/**
	 * Marks the tree node as dirty.
	 */
	public void dirty() {
		this.dirty = true;
	}
	
	/**
	 * Determines whether the node is dirty.
	 * 
	 * @return True if the node is dirty, false otherwise.
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * Marks the tree node as no longer dirty.
	 */
	protected void clean() {
		this.dirty = false;
	}

}
