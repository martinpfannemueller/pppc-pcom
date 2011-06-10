package info.pppc.pcom.swtui.container.action;

import info.pppc.base.swtui.BaseUI;
import info.pppc.pcom.swtui.PcomUI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * This action completely expands the viewers contents.
 * 
 * @author Mac
 */
public class TreeExpandAction extends Action {

	/**
	 * The resource id of the action label.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.action.TreeExpandAction.TEXT";

	/**
	 * The viewer that will be expaned.
	 */
	private TreeViewer viewer;
	
	/**
	 * Creates a new expand action for the specified viewer.
	 * 
	 * @param viewer The viewer to expand.
	 */
	public TreeExpandAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText(PcomUI.getText(UI_TEXT));
		setImageDescriptor(BaseUI.getDescriptor(BaseUI.IMAGE_RIGHT));
	}

	/**
	 * Called by jface whenever the action is executed. This
	 * will completely expand the viewer.
	 */
	public void run() {
		ISelection selection = viewer.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection s = (IStructuredSelection)selection;
			if (! s.isEmpty()) {
				viewer.expandToLevel(s.getFirstElement(), AbstractTreeViewer.ALL_LEVELS);
				return;
			}
		}
		viewer.expandAll();
	}


}
