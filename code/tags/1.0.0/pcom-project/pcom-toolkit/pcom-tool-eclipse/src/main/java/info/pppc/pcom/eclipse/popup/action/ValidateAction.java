package info.pppc.pcom.eclipse.popup.action;

import info.pppc.pcom.eclipse.Plugin;
import info.pppc.pcom.eclipse.markers.MarkerFactory;
import info.pppc.pcom.eclipse.parser.IParserHandler;
import info.pppc.pcom.eclipse.parser.Parser;
import info.pppc.pcom.eclipse.parser.ParserException;
import info.pppc.pcom.eclipse.parser.ParserMarker;
import info.pppc.pcom.eclipse.parser.validator.FeatureValidator;
import info.pppc.pcom.eclipse.parser.validator.ReferenceValidator;
import info.pppc.pcom.eclipse.parser.validator.TypeValidator;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * The validation action is used to validate a specified component
 * descriptor. The validation process adds markers to the component
 * description that describe the failure in more detail.
 * 
 * @author Mac
 */
public class ValidateAction implements IObjectActionDelegate {

	/**
	 * The selected file that was selected last time the
	 * selection changed.
	 */
	private IFile selectedFile;
	
	/**
	 * Constructor for Action1.
	 */
	public ValidateAction() {
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
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						try {
							IJavaProject project = JavaCore.create(selectedFile.getProject());
							if (project == null) throw new RuntimeException("Cannot create Java Project.");
							// remove all error markers and warnings
							MarkerFactory.delete(selectedFile);
							Parser parser = new Parser();
							parser.addValidator(new FeatureValidator());
							parser.addValidator(new ReferenceValidator());
							parser.addValidator(new TypeValidator(project));
							// create handler that signals the completion stage
							parser.setHandler(new IParserHandler() {
								public boolean begin(String name, int total) {
									monitor.beginTask(name, total + 1);
									return (! monitor.isCanceled());
								}
								public boolean step(String name, int work) {
									monitor.setTaskName(name);
									monitor.worked(work);
									return (! monitor.isCanceled());
								}
								public void finish() {};
							});
							// parse document
							parser.parse(selectedFile.getContents());
						} catch (ParserException e) {
							// create warnings and errors
							MarkerFactory.createWarning(selectedFile, e.getMessage());
							ParserMarker[] markers = e.getMarkers();
							for (int i = 0; i < markers.length; i++) {
								ParserMarker m = markers[i];
								MarkerFactory.createError(selectedFile, m.getMessage(), m.getLine());
							}
							selectedFile.touch(null);
						}
					} catch (Throwable t) {
						throw new InvocationTargetException(t);		
					}					
				}
			});			
		} catch (InvocationTargetException t) {
			Throwable throwable = t.getTargetException();
			if (throwable instanceof JavaModelException) {
				JavaModelException exception = (JavaModelException)throwable;
				ErrorDialog.openError(shell, "3PC Pcom Tools", "Validate component failed.", exception.getStatus());
			} else {
				Plugin plugin = Plugin.getDefault();
				plugin.error("Component action failed.", throwable);
				String id = plugin.getBundle().getSymbolicName();
				IStatus status = new Status(IStatus.ERROR, id, 0, "Unknown exception caught.", throwable);
				ErrorDialog.openError(shell, "3PC Pcom Tools", "Validate component failed.", status);				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Plugin plugin = Plugin.getDefault();
			String id = plugin.getBundle().getSymbolicName();
			IStatus status = new Status(IStatus.ERROR, id, 0, "Thread failure (Interrupted Exception).", e);
			ErrorDialog.openError(shell, "3PC Pcom Tools", "Validation status unknown.", status);			
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
