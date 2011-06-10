package info.pppc.pcom.swtui.tree;

import info.pppc.base.swtui.tree.TreeProvider;

/**
 * The dirty tree provider is a tree provider that can also work
 * with dirty tree nodes. It will use a cleaner to update the
 * dirty tree nodes whenever necessary.
 * 
 * @author Mac
 */
public class DirtyTreeProvider extends TreeProvider {

	/**
	 * The cleaner used to clean dirty tree nodes.
	 */
	private ICleaner cleaner;
	
	/**
	 * Creates a new dirty tree provider using the
	 * specified cleaner.
	 * 
	 * @param cleaner The cleaner that will be used to
	 * 	clean dirty nodes.
	 */
	public DirtyTreeProvider(ICleaner cleaner) {
		if (cleaner == null) throw new NullPointerException("Cleaner is null.");
		this.cleaner = cleaner;
	}
	
	/**
	 * Retrieves the children of the node. If the node is a
	 * dirty node that is marked as dirty, the node will be
	 * updated using the cleaner of the provider.
	 * 
	 * @param parentElement The parent whose children should
	 * 	be retrieved.
	 * @return The children of the parent.
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DirtyTreeNode) {
			DirtyTreeNode node = (DirtyTreeNode)parentElement;
			if (node.isDirty()) {
				cleaner.clean(node);
				node.clean();
			}
		}
		return super.getChildren(parentElement);
	}
	

	
}
