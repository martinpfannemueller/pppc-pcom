package info.pppc.pcom.swtui.tree;

/**
 * The cleaner is used by the dirty tree node interface.
 * 
 * @author Mac
 */
public interface ICleaner {

	/**
	 * This method should update the contents of the tree node.
	 * 
	 * @param node The tree node that needs to be refreshed.
	 */
	public void clean(DirtyTreeNode node);
	
}
