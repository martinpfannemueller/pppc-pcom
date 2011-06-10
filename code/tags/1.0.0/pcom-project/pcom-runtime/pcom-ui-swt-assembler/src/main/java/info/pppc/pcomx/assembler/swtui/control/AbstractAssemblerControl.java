package info.pppc.pcomx.assembler.swtui.control;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.element.action.RemoveAction;
import info.pppc.pcomx.assembler.swtui.AssemblerUI;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;


/**
 * The abstract assembler control is used to visualize the
 * inner workings of an assembler. It allows the configuration
 * af an application controls settings.
 * 
 * @author Mac
 */
public abstract class AbstractAssemblerControl extends AbstractElementControl {
	
	/**
	 * The resource key used to retrieve the text of the visualize section.
	 */
	private static final String UI_VISUAL = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.VISUAL";
	
	/**
	 * The resource key used to retrieve the text of a button that enables and disables.
	 */
	private static final String UI_ENABLE = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.ENABLE";
	
	/**
	 * The resource key used to retrieve the text of the settings section.
	 */	
	private static final String UI_GROUP = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.GROUP";
	
	/**
	 * The resource key used to retrieve the text of the zooming section.
	 */
	private static final String UI_ZOOM = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.ZOOM";
	
	/**
	 * The resource key used to retrieve the text of the stepping section.
	 */
	private static final String UI_STEP = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.STEP";

	/**
	 * The resource key used to retrieve the text of a button that selects an automatic action.
	 */
	private static final String UI_AUTO = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.AUTO";
	
	/**
	 * The resource key use to retrieve the text for the remove button.
	 */
	private static final String UI_REMOVE = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.REMOVE";
	
	/**
	 * The resource key used to retrieve the text of the animation section.
	 */
	protected static final String UI_ANIMATION = "info.pppc.pcomx.assembler.swtui.control.AbstractAssemblerControl.ANIMATION";

	/**
	 * The animation speeds that are available in pixels per animation step.
	 */
	protected static final int[] ANIMATIONS = { 5, 10, 50 };
	
	/**
	 * The zooming factors that are available.
	 */
	protected static final int[] ZOOMS = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 150, 200, 300, 500 };
	
	/**
	 * The stepping factors that are available.
	 */
	protected static final int[] STEPS = { 0, 100, 500, 1000, 2000, 5000 };
	
	/**
	 * The scrolled pane that contains all other controls.
	 */
	protected ScrolledComposite pane;

	/**
	 * The content pane within the scrolled pane.
	 */
	protected Composite content;
	
	/**
	 * The visualization enable/disable button.
	 */
	protected Button visButton;
	
	/**
	 * The zoom automation button.
	 */
	protected Button zoomButton;
	
	/**
	 * The zoom combobox.
	 */
	protected Combo zoomCombo;
	
	/**
	 * The stepping automation button.
	 */
	protected Button stepButton;
	
	/**
	 * The stepping combo.
	 */
	protected Combo stepCombo;
	
	/**
	 * The animation enabling button.
	 */
	protected Button animationButton;
	
	/**
	 * The animation combo.
	 */
	protected Combo animationCombo;
	
	/**
	 * The flag that indicates whether open visualizations should be removed.
	 */
	protected boolean removeOnExit = false;
	
	/**
	 * Creates a new visualization control for the gc assembler.
	 * 
	 * @param manager The manager that will display the element.
	 */
	public AbstractAssemblerControl(IElementManager manager) {
		super(manager);
	}

	/**
	 * Called whenver the control is displayed.
	 * 
	 * @param parent The parent to draw the control.
	 */
	public void showControl(Composite parent) {
		// create the control contents
		pane = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		setControl(pane);
		content = new Composite(pane, SWT.NONE);
		pane.setContent(content);
		content.setLayout(new GridLayout(1, false));
		// create the group of controls
		Group defaultGroup = new Group(content, SWT.NONE);
		defaultGroup.setText(AssemblerUI.getText(UI_GROUP));
		defaultGroup.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, true, 1, 1));
		defaultGroup.setLayout(new GridLayout(3, true));
		// create the visualization part
		final Label visLabel = new Label(defaultGroup, SWT.NONE);
		visLabel.setText(AssemblerUI.getText(UI_VISUAL));
		visLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 2));
		final Button removeButton = new Button(defaultGroup, SWT.CHECK);
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		removeButton.setText(AssemblerUI.getText(UI_REMOVE));
		removeButton.setSelection(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeOnExit = removeButton.getSelection();
			}
		});
		visButton = new Button(defaultGroup, SWT.CHECK);
		visButton.setText(AssemblerUI.getText(UI_ENABLE));
		visButton.setSelection(false);
		visButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		/// create the zooming part
		final Label zoomLabel = new Label(defaultGroup, SWT.NONE);
		zoomLabel.setText(AssemblerUI.getText(UI_ZOOM));
		zoomLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 2));
		zoomButton = new Button(defaultGroup, SWT.CHECK);
		zoomButton.setText(AssemblerUI.getText(UI_AUTO));
		zoomButton.setSelection(true);
		zoomButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		zoomCombo = new Combo(defaultGroup, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		zoomCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		for (int i = 0; i < ZOOMS.length; i++) {
			zoomCombo.add(ZOOMS[i] + " %");
		}
		zoomCombo.select(9);
		// create the stepping part
		final Label stepLabel = new Label(defaultGroup, SWT.NONE);
		stepLabel.setText(AssemblerUI.getText(UI_STEP));
		stepLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 2));
		stepButton = new Button(defaultGroup, SWT.CHECK);
		stepButton.setText(AssemblerUI.getText(UI_AUTO));
		stepButton.setSelection(true);
		stepButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		stepCombo = new Combo(defaultGroup, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		stepCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		for (int i = 0; i < STEPS.length; i++) {
			stepCombo.add(STEPS[i] + " ms");
		}
		stepCombo.select(2);
		// create the animation part
		final Label animationLabel = new Label(defaultGroup, SWT.NONE);
		animationLabel.setText(AssemblerUI.getText(UI_ANIMATION));
		animationLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 2));
		animationButton = new Button(defaultGroup, SWT.CHECK);
		animationButton.setText(AssemblerUI.getText(UI_ENABLE));
		animationButton.setSelection(true);
		animationButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		animationCombo = new Combo(defaultGroup, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		animationCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		for (int i = 0; i < ANIMATIONS.length; i++) {
			animationCombo.add(AssemblerUI.getText(UI_ANIMATION + "." + (i + 1)));
		}
		animationCombo.select(2);
		// add the selection listener that changes the states according to the buttons
		SelectionListener updater = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				updateStates();
			};
			public void widgetSelected(SelectionEvent e) {
				updateStates();
			};
		};
		visButton.addSelectionListener(updater);
		zoomButton.addSelectionListener(updater);
		stepButton.addSelectionListener(updater);
		animationButton.addSelectionListener(updater);
		pane.addListener(SWT.Resize, new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				updateSize();
			}
		});
		// update the enables states
		updateStates();
		// updte the size of the buttons
		updateSize();
	}
	
	/**
	 * Returns the menu actions that are available for this control.
	 * 
	 * @return The menu actions for the control.
	 */
	public Action[] getMenuActions() {
		return new Action[] {
			new RemoveAction(this, getManager())
		};
	}
	
	/**
	 * Updates the enabled states of all buttons that change their state.
	 */
	protected void updateStates() {
		if (visButton.getSelection()) {
			zoomButton.setEnabled(true);
			zoomCombo.setEnabled(! zoomButton.getSelection());
			stepButton.setEnabled(true);
			stepCombo.setEnabled(stepButton.getSelection());
			animationButton.setEnabled(true);
			animationCombo.setEnabled(animationButton.getSelection());
		} else {
			zoomButton.setEnabled(false);
			zoomCombo.setEnabled(false);
			stepButton.setEnabled(false);
			stepCombo.setEnabled(false);
			animationButton.setEnabled(false);
			animationCombo.setEnabled(false);
		}
	}
	
	/**
	 * Updates the size of the content pane according to the dimensions
	 * of it and the scrollable pane.
	 */
	protected void updateSize() {
		if (content != null && pane != null) {
			Point p1 = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Rectangle r = pane.getClientArea();
			Point p2 = new Point(r.width, r.height);
			Point p = new Point(p2.x, p2.y);
			p.x = Math.max(p.x, p1.x);
			p.y = Math.max(p.y, p1.y);
			content.setSize(p);
		}		
	}
}
