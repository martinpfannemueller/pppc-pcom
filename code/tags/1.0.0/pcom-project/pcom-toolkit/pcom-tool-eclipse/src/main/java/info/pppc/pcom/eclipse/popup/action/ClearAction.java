package info.pppc.pcom.eclipse.popup.action;

import info.pppc.pcom.eclipse.Plugin;
import info.pppc.pcom.eclipse.markers.MarkerFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The clear action is used to remove all persistent markers
 * from the selected file. This is used to remove the problem
 * markers in cases where users do not want to see them all
 * the time.
 * 
 * @author Mac
 */
public class ClearAction implements IObjectActionDelegate {

	/**
	 * The selected file that was selected last time the
	 * selection changed.
	 */
	private IFile selectedFile;
	
	/**
	 * Constructor for Action1.
	 */
	public ClearAction() {
		super();
	}

	/**
	 * Called when the active part changes.
	 * 
	 * @param action The action.
	 * @param targetPart The target part.
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// nothing to be done here
	}

	/**
	 * Starts the action.
	 * 
	 * @param action The action to start.
	 */
	public void run(IAction action) {
		try {
			MarkerFactory.delete(selectedFile);
		} catch (Throwable t) {
			Plugin plugin = Plugin.getDefault();
			plugin.error("Clear action failed.", t);
			String id = plugin.getBundle().getSymbolicName();
			IStatus status = new Status(IStatus.ERROR, id, 0, "Unknown exception caught.", t);
			ErrorDialog.openError(new Shell(), "3PC Pcom Tools", "Clear component failed.", status);				
		}			
	}
	
	
	/**
	 * Called whenever the selection changes for the action.
	 * 
	 * @param action The action.
	 * @param selection The selection.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection s = (IStructuredSelection)selection;
			if (! s.isEmpty()) {
				Object o = s.getFirstElement();
				if (o != null && o instanceof IFile) {
					selectedFile = (IFile)o;
					action.setEnabled(true);
					return;
				}
			}
		}
		selectedFile = null;
		action.setEnabled(false);
	}

}
