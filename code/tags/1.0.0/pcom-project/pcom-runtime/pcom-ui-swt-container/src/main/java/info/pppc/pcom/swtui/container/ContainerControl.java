package info.pppc.pcom.swtui.container;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.ElementListener;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.element.IRefreshable;
import info.pppc.base.swtui.element.action.RefreshAction;
import info.pppc.base.swtui.element.action.RemoveAction;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.swtui.tree.TreeProvider;
import info.pppc.base.system.SystemID;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.container.action.TreeCollapseAction;
import info.pppc.pcom.swtui.container.action.TreeExpandAction;
import info.pppc.pcom.swtui.tree.DirtyTreeNode;
import info.pppc.pcom.swtui.tree.DirtyTreeProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * The container control is a browser for a remote or local pcom container.
 * It can be used to start and stop components and to browse the current
 * state of a pcom container.
 * 
 * @author Mac
 */
public class ContainerControl extends AbstractElementControl implements IRefreshable, IContainerModel {

	/**
	 * The resource identifier of the container browser label.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.container.ContainerControl.TEXT";

	/**
	 * The system id of the container browser.
	 */
	protected SystemID systemID;

	/**
	 * The human readable name of the system.
	 */
	protected String name;

	/**
	 * The tree viewer that shows the contents of the container
	 * browser.
	 */
	protected TreeViewer tree;

	/**
	 * The generic label provider for contents of the container view.
	 */
	protected ContainerLabelProvider labelProvider = new ContainerLabelProvider();

	/**
	 * The action provider that provides action depending on the selection.
	 */
	protected ContainerActionProvider actionProvider = new ContainerActionProvider(this);

	/**
	 * The model provider that creates the model of the tree view.
	 */
	protected ContainerModelProvider modelProvider;

	/**
	 * The content provider of the tree viewer. This provider is generic
	 * for tree nodes and it can be reused.
	 */
	protected TreeProvider contentProvider;

	/**
	 * Creates a new container control for the specified container
	 * that operates on the specified manager.
	 * 
	 * @param systemID The system to manager by this control.
	 * @param manager The manager of the view.
	 * @param name The name of the system (human readable).
	 */
	public ContainerControl(IElementManager manager, SystemID systemID, String name) {
		super(manager);
		this.systemID = systemID;
		this.name = name;
		modelProvider = new ContainerModelProvider(manager, systemID);
		contentProvider = new DirtyTreeProvider(modelProvider);
	}

	/**
	 * Returns the name of the container browser.
	 * 
	 * @return The name of the container browser.
	 */
	public String getName() {
		return PcomUI.getText(UI_TEXT) + " (" + name + ")";
	}

	/**
	 * Returns the image of the container browser.
	 * 
	 * @return The image of the container browser.
	 */
	public Image getImage() {
		return PcomUI.getImage(PcomUI.IMAGE_CONTAINER);
	}
	
	/**
	 * Returns the system id of the system that is browsed
	 * by this container control.
	 * 
	 * @return The system id of the system that is browsed.
	 */
	public SystemID getSystemID() {
		return systemID;
	}

	/**
	 * Creates the tree view control and refreshes the
	 * contents.
	 * 
	 * @param parent The parent composite of the view.
	 */
	public void showControl(Composite parent) {
		tree = new TreeViewer(parent);
		final MenuManager manager = new MenuManager();
		tree.getControl().setMenu(manager.createContextMenu(tree.getControl()));
		tree.addSelectionChangedListener(new ElementListener(this, manager));
		tree.setContentProvider(contentProvider);
		tree.setLabelProvider(labelProvider);
		setControl(tree.getControl());
		refresh();	
	}

	/**
	 * Returns the actions of the element. This element always shows
	 * a refresh and a close action and other actions depending on
	 * the current selection of the tree.
	 * 
	 * @return The actions for the current selection.
	 */
	public Action[] getMenuActions() {
		Action[] contextActions = new Action[0];
		if (tree != null && !tree.getControl().isDisposed()) {
			IStructuredSelection sel = (IStructuredSelection)tree.getSelection();
			Object element = sel.getFirstElement();
			if (element != null) {
				contextActions = actionProvider.getMenuActions(element);	
			}
		}
		if (contextActions.length > 0) {
			Action[] result = new Action[contextActions.length + 6];
			for (int i = 0; i < contextActions.length; i++) {
				result[i] = contextActions[i];
			}
			result[result.length - 6] = null;
			result[result.length - 5] = new TreeExpandAction(tree);
			result[result.length - 4] = new TreeCollapseAction(tree);
			result[result.length - 3] = null;
			result[result.length - 2] = new RefreshAction(this);
			result[result.length - 1] = new RemoveAction(this, getManager());
			return result;
		} else {
			return new Action[] {
				new TreeExpandAction(tree),
				new TreeCollapseAction(tree),
				null,
				new RefreshAction(this),
				new RemoveAction(this, getManager())
			};			
		}
	}


	/**
	 * Refreshes the model of the container browser and
	 * displays the refreshed model in the view if it is
	 * currently displayed.
	 */
	public void refresh() {
		Display display = getDisplay();
		if (display != null) {
			display.syncExec(new Runnable() {
				public void run() {
					if (tree != null && ! tree.getControl().isDisposed()) {
						ISelection selection = tree.getSelection();
						if (! selection.isEmpty() && selection instanceof IStructuredSelection) {
							IStructuredSelection s = (IStructuredSelection)selection;
							Object o = s.getFirstElement();
							TreeNode n = (o instanceof TreeNode)?(TreeNode)o:null;
							while (n != null && !(n instanceof DirtyTreeNode)) {
								n = n.getParent();
							}
							if (n != null) {
								DirtyTreeNode dn = (DirtyTreeNode)n;
								dn.dirty();
								tree.refresh(dn, true);
								return;
							}
						}
						tree.setInput(modelProvider.getModel());
					}
				}
			});
		}
	}
	
	/**
	 * Called whenever the control is disposed. This method
	 * releases the internal reference to the view element.
	 */
	public void disposeControl() {
		tree = null;
		super.disposeControl();
	}

}
