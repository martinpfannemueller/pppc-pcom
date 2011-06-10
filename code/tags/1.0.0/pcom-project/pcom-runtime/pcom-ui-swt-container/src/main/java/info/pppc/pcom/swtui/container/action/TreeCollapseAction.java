package info.pppc.pcom.swtui.container.action;

import info.pppc.base.swtui.BaseUI;
import info.pppc.pcom.swtui.PcomUI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * The collapse action collapses all contents shown in the viewer.
 * 
 * @author Mac
 */
public class TreeCollapseAction extends Action {

	/**
	 * The resource id of the action's label.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.TreeCollapseAction.TEXT";

	/**
	 * The tree viewer to collapse.
	 */
	private TreeViewer viewer;

	/**
	 * Creates a new collapse action that will collapse the
	 * specified viewer.
	 * 
	 * @param viewer The viewer to collapse.
	 */
	public TreeCollapseAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText(PcomUI.getText(UI_TEXT));
		setImageDescriptor(BaseUI.getDescriptor(BaseUI.IMAGE_LEFT));
	}

	/**
	 * Called by jface whenever the action is executed. 
	 * Collapses the specified viewer.
	 */
	public void run() {
		ISelection selection = viewer.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection s = (IStructuredSelection)selection;
			if (! s.isEmpty()) {
				viewer.collapseToLevel(s.getFirstElement(), AbstractTreeViewer.ALL_LEVELS);
				return;
			}
		}
		viewer.collapseAll();
	}


}
