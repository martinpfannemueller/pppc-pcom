package info.pppc.pcom.swtui.application.builder;

import java.util.Vector;

import info.pppc.base.swtui.BaseUI;
import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.ElementListener;
import info.pppc.base.swtui.element.action.RemoveAction;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.swtui.widget.ImageButton;
import info.pppc.base.system.event.Event;
import info.pppc.base.system.event.IListener;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.application.ApplicationControl;
import info.pppc.pcom.swtui.tree.DirtyTreeNode;
import info.pppc.pcom.swtui.tree.DirtyTreeProvider;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.IContract;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * The application builder enables users to create and manipulate
 * application descriptors that are later on used to start 
 * applications.
 * 
 * @author Mac
 */
public class ApplicationBuilderControl extends AbstractElementControl implements IApplicationModel {

	/**
	 * The text shown for the builder.
	 */
	private static final String UI_TEXT = "info.pppc.pcom.swtui.application.builder.ApplicationBuilderControl.TEXT";
	
	/**
	 * The builder composite where the builder elements will be
	 * shown according to the selection within the viewer.
	 */
	private Composite builderComposite;
	
	/**
	 * The tree viewer that will display the preferences.
	 */
	private TreeViewer treeViewer;
	
	/**
	 * The composite that holds the tree viewer.
	 */
	private Composite treeComposite;
	
	/**
	 * The application descriptor that is configured using this
	 * application builder.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * The model provider of the descriptor.
	 */
	private ApplicationModelProvider provider;
	
	/**
	 * The builder that is currently active.
	 */
	private AbstractBuilder builder;
	
	/**
	 * The sash form that provides the control.
	 */
	private SashForm sash;
	
	/**
	 * The ok button to commit the descriptor.
	 */
	private ImageButton okButton;
	
	/**
	 * The up button to shift preferences.
	 */
	private ImageButton upButton;
	
	/**
	 * The down button to shift preferences.
	 */
	private ImageButton downButton;
	
	/**
	 * The add button to add contracts.
	 */
	private ImageButton addButton;
	
	/**
	 * The remove button to remove contracts.
	 */
	private ImageButton removeButton;
	
	/**
	 * The application control that needs to be informed
	 * if the state changes.
	 */
	private ApplicationControl control;
	
	/**
	 * A counter that is used to speed up the unique string
	 * computation.
	 */
	private int count = 0;
	
	/**
	 * Creates a new application builder using the specified
	 * application control.
	 * 
	 * @param control The application control used by the
	 * 	builder.
	 * @param descriptor The application descriptor configured
	 * 	by the builder.
	 */
	public ApplicationBuilderControl(ApplicationControl control, ApplicationDescriptor descriptor) {
		super(control.getManager());
		this.descriptor = descriptor;
		this.control = control;
	}

	/**
	 * Returns the name of the application builder.
	 * 
	 * @return The name of the builder.
	 */
	public String getName() {
		return PcomUI.getText(UI_TEXT);
	}

	/**
	 * Returns the application builder image.
	 * 
	 * @return The image of the application builder.
	 */
	public Image getImage() {
		return PcomUI.getImage(PcomUI.IMAGE_BUILDER);
	}

	/**
	 * Creates the application builder control on the specified
	 * composite.
	 * 
	 * @param parent The parent composite of the builder.
	 */
	public void showControl(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL);
		setControl(sash);
		// create the tree
		treeComposite = new Composite(sash, SWT.BORDER);
		// create the control for the builders
		builderComposite = new Composite(sash, SWT.BORDER);
		// create the contents of the tree control
		treeComposite.setLayout(new GridLayout(2, false));
		treeViewer = new TreeViewer(treeComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		MenuManager manager = new MenuManager();
		treeViewer.getControl().setMenu
			(manager.createContextMenu(treeViewer.getControl()));
		treeViewer.addSelectionChangedListener
			(new ElementListener(this, manager));
		treeViewer.setLabelProvider(new ApplicationLabelProvider());
		provider = new ApplicationModelProvider();
		treeViewer.setContentProvider(new DirtyTreeProvider(provider));
		treeViewer.setInput(provider.getModel(descriptor));
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));
		// add the buttons
		addButton = new ImageButton(treeComposite, SWT.NONE);
		addButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_ADD));
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		removeButton = new ImageButton(treeComposite, SWT.NONE);
		removeButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_REMOVE));
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		upButton = new ImageButton(treeComposite, SWT.NONE);
		upButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_UP));
		upButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		downButton = new ImageButton(treeComposite, SWT.NONE);
		downButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_DOWN));
		downButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		okButton = new ImageButton(treeComposite, SWT.NONE);
		okButton.setImage(BaseUI.getImage(BaseUI.IMAGE_BUTTON_OK));
		okButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		Label treeSpace = new Label(treeComposite, SWT.NONE);
		treeSpace.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 1));
		// add all listeners
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection s = (IStructuredSelection)selection;
					if (! s.isEmpty()) {
						TreeNode node = (TreeNode)s.getFirstElement();
						update(node);
						return;
					}
				} 
				update(null);
			}
		});
		// add the ok listener
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getManager().removeElement(ApplicationBuilderControl.this);
				control.updateApplication(descriptor);
				AbstractElementControl[] controls = getManager().getElements();
				for (int i = 0; i < controls.length; i++) {
					if (controls[i] == control) {
						getManager().focusElement(controls[i]);
						return;
					}
				}
			}
		});
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection s = (IStructuredSelection)selection;
					if (! s.isEmpty()) {
						TreeNode node = (TreeNode)s.getFirstElement();
						Contract c = (Contract)node.getData();
						DirtyTreeNode parent = (DirtyTreeNode)node.getParent();
						if (parent.getType() == TYPE_CONTRACT) {
							Contract p = (Contract)parent.getData();
							p.removeContract(c);
						}
						if (parent.getType() == TYPE_CONTRACTS) {
							Vector v = (Vector)parent.getData();
							v.removeElement(c);
						}
						parent.dirty();
						treeViewer.refresh(parent, true);
					}
				} 
			}
		});
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection s = (IStructuredSelection)selection;
					if (! s.isEmpty()) {
						DirtyTreeNode node = (DirtyTreeNode)s.getFirstElement();
						if (node.getType() == TYPE_CONTRACTS) {
							Vector v = (Vector)node.getData();
							Vector names = new Vector();
							for (int i = 0; i < v.size(); i++) {
								Contract c = (Contract)v.elementAt(i);
								names.addElement(c.getName());
							}
							String name = createString(names);
							v.addElement(new Contract(Contract.TYPE_INSTANCE_TEMPLATE, name));
						} else if (node.getType() == TYPE_CONTRACT) {
							Contract c = (Contract)node.getData();
							if (c.getType() == Contract.TYPE_FEATURE_DEMAND) {
								node = (DirtyTreeNode)node.getParent();
								c = (Contract)node.getData();
							}
							Contract[] children = c.getContracts();
							Vector names = new Vector();
							for (int i = 0; i < children.length; i++) {
								names.addElement(children[i].getName());
							}
							String name = createString(names);
							byte type = 0;
							switch (c.getType()) {
								case Contract.TYPE_INSTANCE_TEMPLATE:
									type = Contract.TYPE_INSTANCE_DEMAND;
									break;
								case Contract.TYPE_INSTANCE_DEMAND:
									type = Contract.TYPE_INTERFACE_DEMAND;
									break;
								case Contract.TYPE_INTERFACE_DEMAND:
									type = Contract.TYPE_DIMENSION_DEMAND;
									break;
								case Contract.TYPE_DIMENSION_DEMAND:
									type = Contract.TYPE_FEATURE_DEMAND;
									break;
								default:
									// will never happen, due parent swap
							}
							Contract child = new Contract(type, name);
							if (type == Contract.TYPE_FEATURE_DEMAND) {
								child.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, 
										new Integer(IContract.IFEQ));
								child.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, "");
							}
							c.addContract(child);
						}
						node.dirty();
						treeViewer.refresh(node, true);
						update(node);
					}
				} 
			}
		});
		upButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection s = (IStructuredSelection)selection;
					if (! s.isEmpty()) {
						TreeNode node = (TreeNode)s.getFirstElement();
						Contract c = (Contract)node.getData();
						DirtyTreeNode parent = (DirtyTreeNode)node.getParent();
						Vector v = (Vector)parent.getData();
						for (int i = 0; i < v.size(); i++) {
							Contract child = (Contract)v.elementAt(i);
							if (child == c) {
								v.removeElementAt(i);
								v.insertElementAt(c, i - 1);
								break;
							}
						}
						parent.dirty();
						treeViewer.refresh(parent, true);
					}
				} 
			}
		});
		downButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection s = (IStructuredSelection)selection;
					if (! s.isEmpty()) {
						TreeNode node = (TreeNode)s.getFirstElement();
						Contract c = (Contract)node.getData();
						DirtyTreeNode parent = (DirtyTreeNode)node.getParent();
						Vector v = (Vector)parent.getData();
						for (int i = 0; i < v.size(); i++) {
							Contract child = (Contract)v.elementAt(i);
							if (child == c) {
								v.removeElementAt(i);
								v.insertElementAt(c, i + 1);
								break;
							}
						}
						parent.dirty();
						treeViewer.refresh(parent, true);
					}
				} 			
			}
		});
		update(null);
	}
	
	/**
	 * Returns the menu actions for the specified selection.
	 * 
	 * @return The menu actions for the current selection.
	 */
	public Action[] getMenuActions() {
		return new Action[] {
				new RemoveAction(this, getManager())
		};
	}
	
	/**
	 * Returns the application descriptor that is modified by this
	 * application builder.
	 * 
	 * @return The descriptor modified by the builder.
	 */
	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Removes all content from the builder pane.
	 */
	private void removeBuilder() {
		if (builder != null) {
			builder.dispose();
			builder = null;
		}
	}
	
	/**
	 * Disposes the control and the builder if any.
	 */
	public void disposeControl() {
		removeBuilder();
		super.disposeControl();
	}
	
	/**
	 * Creates the builder that lets the user select a name.
	 * 
	 * @param node The tree node that contains the descriptor.
	 */
	private void createNameBuilder(TreeNode node) {
		builder = new NameBuilder(builderComposite);
		builder.createControl(node);
		builder.addBuilderListener(AbstractBuilder.EVENT_DONE, new IListener() {
			public void handleEvent(Event event) {
				treeViewer.setInput(provider.getModel(descriptor));					
			}
		});
	}
	
	/**
	 * Creates a new contract builder for the current selection.
	 * 
	 *  @param node The tree node that contains the contract.
	 */
	private void createContractBuilder(final TreeNode node) {
		builder = new ContractBuilder(builderComposite);
		builder.createControl(node);
		builder.addBuilderListener(AbstractBuilder.EVENT_DONE, new IListener() {
			public void handleEvent(Event event) {
				TreeNode n = node;
				while (n != null && !(n instanceof DirtyTreeNode)) {
					n = node.getParent();
				}
				if (n == null) {
					treeViewer.setInput(provider.getModel(descriptor));	
				} else {
					DirtyTreeNode dtn = (DirtyTreeNode)n;
					dtn.dirty();
					treeViewer.refresh(dtn, true);
				}
			}
		});
	}
	
	/**
	 * Creates an assembler builder on the builder composite.
	 * 
	 * @param node The tree node containing the application descriptor.
	 */
	private void createAssemblerBuilder(TreeNode node) {
		builder = new AssemblerBuilder(builderComposite);
		builder.createControl(node);
		builder.addBuilderListener(AbstractBuilder.EVENT_DONE, new IListener() {
			public void handleEvent(Event event) {
				treeViewer.setInput(provider.getModel(descriptor));	
			}
		});
	}
	
	/**
	 * Creates a new image builder on the builder composite.
	 * 
	 * @param node The tree node containing the application descriptor.
	 */
	private void createImageBuilder(TreeNode node) {
		builder = new ImageBuilder(builderComposite, getManager());
		builder.createControl(node);
		builder.addBuilderListener(AbstractBuilder.EVENT_DONE, new IListener() {
			public void handleEvent(Event event) {
				treeViewer.setInput(provider.getModel(descriptor));
			}
		});
	}
	
	/**
	 * Called when another tree node is selected. Updates the
	 * ui according to the node.
	 * 
	 * @param node The selected node.
	 */
	private void update(TreeNode node) {
		removeBuilder();
		Vector preferences = descriptor.getPreferences();
		okButton.setEnabled(preferences != null && preferences.size() > 0);
		if (node == null) {
			addButton.setEnabled(false);
			removeButton.setEnabled(false);
			upButton.setEnabled(false);
			downButton.setEnabled(false);
			sash.setMaximizedControl(treeComposite);
		} else {
			boolean isContract = (node.getType() == TYPE_CONTRACT);
			addButton.setEnabled(isContract || node.getType() == TYPE_CONTRACTS);
			removeButton.setEnabled(isContract);
			if (isContract) {
				boolean isTemplate = node.getParent().getType() == TYPE_CONTRACTS;
				TreeNode[] nodes = node.getParent().getChildren();
				boolean isFirstChild = (nodes[0] == node);
				boolean isLastChild = (nodes[nodes.length - 1] == node);
				upButton.setEnabled(isTemplate && !isFirstChild);
				downButton.setEnabled(isTemplate && !isLastChild);
			} else {
				upButton.setEnabled(false);
				downButton.setEnabled(false);
			}
			switch (node.getType()) {
				case TYPE_ASSEMBLER:
					createAssemblerBuilder(node.getParent());
					break;
				case TYPE_ATTRIBUTE:
					break;
				case TYPE_CONTRACT:
					createContractBuilder(node);			
					break;
				case TYPE_CONTRACTS:
					break;
				case TYPE_DESCRIPTOR:
					break;
				case TYPE_IDENTIFIER:
					break;
				case TYPE_IMAGE:
					createImageBuilder(node.getParent());
					break;
				case TYPE_NAME:
					createNameBuilder(node.getParent());
					break;
				default:
					// will never happen due to model
			}
		}
		if (builder == null) {
			sash.setMaximizedControl(treeComposite);	
		} else {
			sash.setMaximizedControl(null);
			int builder = builderComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			Rectangle r = sash.getClientArea();
			int total = r.height - r.y;
			int tree = total - builder;
			sash.setWeights(new int[] { tree, builder});				
		}
	}
	
	
	/**
	 * Creates a unique string considering the specified strings.
	 * 
	 * @param strings A vector that contains strings that cannot be
	 * 	used.
	 * @return A unique string considering the specified strings.
	 */
	private String createString(Vector strings) {
		String s = "UNSET";
		while (strings.contains(s + count)) {
			count += 1;
		}
		return s + count;
	}
	
}
