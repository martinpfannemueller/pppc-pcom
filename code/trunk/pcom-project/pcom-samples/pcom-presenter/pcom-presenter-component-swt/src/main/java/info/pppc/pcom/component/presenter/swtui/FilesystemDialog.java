package info.pppc.pcom.component.presenter.swtui;

import info.pppc.base.system.event.IListener;
import info.pppc.base.system.event.ListenerBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The control file dialog lets a user select files from a tree
 * viewer. A selection change listener can be added in order to
 * receive selection events. The contents of the tree viewer can
 * be set with a content and a label provider.
 * 
 * @author Mac
 */
public class FilesystemDialog extends Dialog {

	/**
	 * Localized text for the help label.
	 */
	public static final String UI_HELP = "info.pppc.pcom.component.presenter.swtui.FilesystemDialog.HELP";

	/**
	 * Localized text for the dialog title.
	 */
	public static final String UI_TITLE = "info.pppc.pcom.component.presenter.swtui.FilesystemDialog.TITLE";

	/**
	 * This event is used to notify its receiver of a changed 
	 * selection. The user object will be either null to indicate
	 * that there is none or it will be the selected item of 
	 * the tree viewer.
	 */
	public static final int EVENT_SELECTION = 1;

	/**
	 * The listeners registered for events.
	 */
	private ListenerBundle listeners = new ListenerBundle(this);

	/**
	 * The tree viewer.
	 */
	private TreeViewer viewer;

	/**
	 * The label provider of the tree.
	 */
	private LabelProvider labelProvider;
	
	/**
	 * The content provider of the tree.
	 */
	private ITreeContentProvider contentProvider;
	
	/**
	 * The input object of the viewer.
	 */
	private Object input; 

	/**
	 * The last selected object of the dialog.
	 */
	private Object selection = null;

	/**
	 * A flag that determines whether the ok button
	 * is enabled.
	 */
	private boolean enabled = false;

	/**
	 * Createes a new control file dialog for the specified parent
	 * shell.
	 * 
	 * @param parentShell The parent shell of the dialog.
	 */
	public FilesystemDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Called when the window is created. This method will first create
	 * the window contents and thereafter it will set the text and the
	 * style of the window.
	 */
	public void create() {
		super.create();
		Shell shell = getShell();
		shell.setText(PresenterUI.getText(UI_TITLE));
		shell.setImage(PresenterUI.getImage
			(PresenterUI.IMAGE_PRESENTER));
	}


	/**
	 * Creates the viewer, sets the content and label provider as well
	 * as the input and registers the listener that informs the
	 * listeners of changes.
	 * 
	 * @param composite The parent of the control
	 * @return The control that has been created as base.
	 */
	protected Control createDialogArea(Composite composite) {
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		Label label = new Label(parent, SWT.LEFT);
		label.setText(PresenterUI.getText(UI_HELP));
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER);
		viewer.getControl().setLayoutData
			(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (contentProvider != null) {
			viewer.setContentProvider(contentProvider);
		}
		if (labelProvider != null) {
			viewer.setLabelProvider(labelProvider);
		}
		if (input != null) {
			viewer.setInput(input);
		}
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				selection = null;
				ISelection s = event.getSelection();
				if (s != null && s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection)s;
					if (! ss.isEmpty()) {
						selection = ss.getFirstElement();
					} 
				}
				listeners.fireEvent(EVENT_SELECTION, selection);
			}
		});
		return parent;		
	}

	/**
	 * Creeates the button bar and sets the enabled state of the
	 * ok button. Default is false.
	 * 
	 * @param parent The parent of the button bar.
	 * @return The control of the button bar.
	 */
	protected Control createButtonBar(Composite parent) {
		Control c = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
		return c;
	}


	/**
	 * Adds the specified listener to the set of registered listeners.
	 * At the present time the only event type supported is EVENT_SELECTION.
	 * 
	 * @param type The event type to register for.
	 * @param listener The listener to register.
	 */
	public void addControlListener(int type, IListener listener) {
		listeners.addListener(type, listener);
	}
	
	/**
	 * Removes the specified listener from the reception of the specified
	 * event types.
	 * 
	 * @param type The event types to unregister.
	 * @param listener The listener to unregister.
	 * @return True if the listener has been removed, false otherwise.
	 */
	public boolean removeControlListener(int type, IListener listener) {
		return listeners.removeListener(type, listener);
	}

	/**
	 * Returns the content provider of the dialogs tree viewer.
	 * 
	 * @return The content provider of the tree viewer.
	 */
	public ITreeContentProvider getContentProvider() {
		return contentProvider;
	}

	/**
	 * Returns the input object of the tree viewer.
	 * 
	 * @return The input object of the viewer.
	 */
	public Object getInput() {
		return input;
	}

	/**
	 * Returns the lable provider of the tree viewer.
	 * 
	 * @return The lable provider of the viewer.
	 */
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * Sets the content provider of the tree viewer. Note that
	 * this method must be called before the dialog is opened.
	 * 
	 * @param provider The content provider of the tree viewer.
	 */
	public void setContentProvider(ITreeContentProvider provider) {
		contentProvider = provider;
	}

	/**
	 * Sets the input object of the tree viewer. Note that this
	 * method must be called before the dialog is opened.
	 * 
	 * @param object The input object of the tree viewer.
	 */
	public void setInput(Object object) {
		input = object;
	}

	/**
	 * Sets the label provider of the tree viewer. Note that this
	 * method must be called before the dialog is opened.
	 * 
	 * @param provider The provider of the dialog.
	 */
	public void setLabelProvider(LabelProvider provider) {
		labelProvider = provider;
	}
	
	/**
	 * Returns the item that is currently selected.
	 * 
	 * @return The item that is selected.
	 */
	public Object getSelection() {
		return selection;
	}
	
	/**
	 * A flag that indicates whether the dialogs ok button is
	 * enabled.
	 *
	 * @return True if the dialog's ok button is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets a flag to determine whether the ok button of the
	 * dialog is enabled.
	 * 
	 * @param enabled True to enable the dialog's ok button,
	 * 	false to disable.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok != null) {
			ok.setEnabled(enabled);
		}
	}

	/**
	 * Returns the initial size, same as the parent shell.
	 * 
	 * @return The shell size.
	 */
	protected Point getInitialSize() {
		Shell shell = getParentShell();
		Point p = shell.getSize();
		int x = Math.min(Math.max(p.x - 50, 200), 300);
		int y = Math.min(Math.max(p.y - 50, 250), 450);
		return new Point(x, y);
	}

	

}
