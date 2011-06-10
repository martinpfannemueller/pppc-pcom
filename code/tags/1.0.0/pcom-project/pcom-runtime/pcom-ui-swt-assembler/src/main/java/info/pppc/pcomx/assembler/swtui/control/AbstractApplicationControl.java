package info.pppc.pcomx.assembler.swtui.control;

import info.pppc.base.swtui.element.AbstractElementControl;
import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.element.action.RemoveAction;
import info.pppc.base.system.util.Logging;
import info.pppc.pcomx.assembler.swtui.AssemblerUI;
import info.pppc.pcomx.assembler.swtui.graph.GraphEntryFigure;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * The application control is used to visualize a single application
 * that is configured by an assembler.
 * 
 * @author Mac
 */
public abstract class AbstractApplicationControl extends AbstractElementControl {
	
	/**
	 * The graph that displays the configuration algorithm.
	 */
	protected GraphEntryFigure assemblerGraph;
	
	/**
	 * The graph that displays the current running configuration.
	 */
	protected GraphEntryFigure applicationGraph;
	
	/**
	 * The scalable pane that contains the graph.
	 */
	protected ScalableLayeredPane assemblerPane;
	
	/**
	 * The scalable pane that contains the graph.
	 */
	protected ScalableLayeredPane applicationPane;
	
	/**
	 * The canvas that contains the light-weight system.
	 */
	protected Canvas assemblerCanvas;
	
	/**
	 * The canvas that contains the application light-weight system.
	 */
	protected Canvas applicationCanvas;
	
	/**
	 * The step button that enables automatic stepping.
	 */
	protected ToolItem stepButton;
	
	/**
	 * The stepping combo that enables the selection of stepping speeds.
	 */
	protected Combo stepCombo;
	
	/**
	 * The step next button that enables a user to run all waiting threads.
	 */
	protected ToolItem stepNextButton;
	
	/**
	 * The zoom button that enables automatic zooming.
	 */
	protected ToolItem zoomButton;
	
	/**
	 * The zoom combo that enables a user to select a zooming factor.
	 */
	protected Combo zoomCombo;
	
	/**
	 * The animation button that enables a user to disable the animations.
	 */
	protected ToolItem animationButton;
	
	/**
	 * The animation combo that enables a user to select an animation speed.
	 */
	protected Combo animationCombo;
	
	/**
	 * The blocking time or -1 for manual blocked.
	 */
	protected int[] blockTime = new int[] { 0 };
	
	/**
	 * This is the flag that indicates whether auto-zooming is enabled.
	 */
	protected boolean autoZoom = false;
	
	/**
	 * Creates a new manager for the specified application on the 
	 * specified element manager. 
	 * 
	 * @param manager The manager used to show the control.
	 */
	public AbstractApplicationControl(IElementManager manager) {
		super(manager);
	}
	
	/**
	 * Creates the application control on the specified parent pane.
	 * 
	 * @param parent The parent of the control.
	 */
	public void showControl(Composite parent) {
		final Composite control = new Composite(parent, SWT.NONE);
		setControl(control);
		// create the toolbar to control the animation
		final CoolBar coolBar = new CoolBar(control, SWT.NONE);
		// create the step bar
		ToolBar stepBar = new ToolBar(coolBar, SWT.FLAT);
		ToolItem stepLabelSeparator = new ToolItem(stepBar, SWT.SEPARATOR);
		Label stepLabel = new Label(stepBar, SWT.CENTER);
		stepLabel.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_STEP));
		stepLabelSeparator.setControl(stepLabel);
		stepLabelSeparator.setWidth(stepLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 4);
		stepButton = new ToolItem(stepBar, SWT.CHECK);
		stepButton.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_AUTO));
		ToolItem stepComboSeparator = new ToolItem(stepBar, SWT.SEPARATOR);
		stepCombo = new Combo(stepBar, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < AbstractAssemblerControl.STEPS.length; i++) {
			stepCombo.add(AbstractAssemblerControl.STEPS[i] + " ms");
		}
		stepCombo.select(3);
		stepComboSeparator.setControl(stepCombo);
		stepComboSeparator.setWidth(stepCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 4);
		stepNextButton = new ToolItem(stepBar, SWT.PUSH);
		stepNextButton.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_NEXT));
		stepBar.pack();
		Point stepSize = stepBar.getSize();
		CoolItem coolStep = new CoolItem(coolBar, SWT.NONE);
		coolStep.setControl(stepBar);
		coolStep.setPreferredSize(coolStep.computeSize(stepSize.x + 4, stepSize.y));
		// create the zoom toolbar
		ToolBar zoomBar = new ToolBar(coolBar, SWT.FLAT);
		ToolItem zoomLabelSeparator = new ToolItem(zoomBar, SWT.SEPARATOR);
		Label zoomLabel = new Label(zoomBar, SWT.CENTER);
		zoomLabel.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_ZOOM));
		zoomLabelSeparator.setControl(zoomLabel);
		zoomLabelSeparator.setWidth(zoomLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 4);
		zoomButton = new ToolItem(zoomBar, SWT.CHECK);
		zoomButton.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_AUTO));
		ToolItem zoomComboSeparator = new ToolItem(zoomBar, SWT.SEPARATOR);
		zoomCombo = new Combo(zoomBar, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < AbstractAssemblerControl.ZOOMS.length; i++) {
			zoomCombo.add(AbstractAssemblerControl.ZOOMS[i] + " %");
		}
		zoomCombo.select(9);
		zoomComboSeparator.setControl(zoomCombo);
		zoomComboSeparator.setWidth(zoomCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 4);
		zoomBar.pack();
		Point zoomSize = zoomBar.getSize();
		CoolItem coolZoom = new CoolItem(coolBar, SWT.NONE);
		coolZoom.setControl(zoomBar);
		coolZoom.setPreferredSize(coolZoom.computeSize(zoomSize.x + 4, zoomSize.y));
		// create the animation toolbar
		ToolBar animateBar = new ToolBar(coolBar, SWT.FLAT);
		ToolItem animateLabelSeparator = new ToolItem(animateBar, SWT.SEPARATOR);
		Label animateLabel = new Label(animateBar, SWT.CENTER);
		animateLabel.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_ANIMATE));
		animateLabelSeparator.setControl(animateLabel);
		animateLabelSeparator.setWidth(animateLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 4);
		animationButton = new ToolItem(animateBar, SWT.CHECK);
		animationButton.setImage(AssemblerUI.getImage(AssemblerUI.IMAGE_STOP));
		ToolItem animateComboSeparator = new ToolItem(animateBar, SWT.SEPARATOR);
		animationCombo = new Combo(animateBar, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < AbstractAssemblerControl.ANIMATIONS.length; i++) {
			animationCombo.add(AssemblerUI.getText(AbstractAssemblerControl.UI_ANIMATION + "." + (i + 1)));
		}
		animationCombo.select(1);
		animateComboSeparator.setControl(animationCombo);
		animateComboSeparator.setWidth(animationCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 4);
		animateBar.pack();
		Point animateSize = animateBar.getSize();
		CoolItem coolAnimate = new CoolItem(coolBar, SWT.NONE);
		coolAnimate.setControl(animateBar);
		coolAnimate.setPreferredSize(coolAnimate.computeSize(animateSize.x + 4, animateSize.y));
		// resize the toolbar upon resize
		FormLayout layout = new FormLayout();
	    control.setLayout(layout);
	    FormData coolData = new FormData();
	    coolData.left = new FormAttachment(0);
	    coolData.right = new FormAttachment(100);
	    coolData.top = new FormAttachment(0);
	    coolBar.setLayoutData(coolData);
		coolBar.addListener(SWT.Resize, new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				control.layout();
			}
		});
		Composite canvasPane = new Composite(control, SWT.NONE);
		canvasPane.setLayout(new GridLayout(2, false));
		FormData canvasData = new FormData();
		canvasData.left = new FormAttachment(0);
		canvasData.right = new FormAttachment(100);
		canvasData.top = new FormAttachment(coolBar);
		canvasData.bottom = new FormAttachment(100);
		canvasPane.setLayoutData(canvasData);
		// create the application draw 2d canvas	
		applicationCanvas = new Canvas(canvasPane, SWT.BORDER);
		applicationCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		applicationCanvas.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		LightweightSystem lws2 = new LightweightSystem(applicationCanvas);
		// add a scaleable pane that contains the graph
		applicationPane = new ScalableLayeredPane();
		lws2.setContents(applicationPane);
		Layer layer2 = new Layer();
		applicationPane.add(layer2, new Integer(0), 0);
		BorderLayout bl2 = new BorderLayout();
		layer2.setLayoutManager(bl2);
		// add the application graph
		applicationGraph = new GraphEntryFigure(parent.getDisplay());
		applicationGraph.addFigureListener(new FigureListener() {
			public void figureMoved(IFigure source) {
				zoom();
			};
		});
		layer2.add(applicationGraph, BorderLayout.CENTER);
		// create another draw 2d canvas
		assemblerCanvas = new Canvas(canvasPane, SWT.BORDER);
		assemblerCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		assemblerCanvas.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		LightweightSystem lws = new LightweightSystem(assemblerCanvas);
		// add a scaleable pane that contains the graph
		assemblerPane = new ScalableLayeredPane();
		lws.setContents(assemblerPane);
		Layer layer = new Layer();
		assemblerPane.add(layer, new Integer(0), 0);
		BorderLayout bl = new BorderLayout();
		layer.setLayoutManager(bl);
		// add the assembler graph
		assemblerGraph = new GraphEntryFigure(parent.getDisplay());
		assemblerGraph.addFigureListener(new FigureListener() {
			public void figureMoved(IFigure source) {
				zoom();
			};
		});
		layer.add(assemblerGraph, BorderLayout.CENTER);
		// add the button and combobox listeners
		stepNextButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				unblock();
			}
		});
		SelectionListener updater = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				update();
			};
			public void widgetSelected(SelectionEvent e) {
				update();
			};
		};
		stepButton.addSelectionListener(updater);
		stepCombo.addSelectionListener(updater);
		zoomButton.addSelectionListener(updater);
		zoomCombo.addSelectionListener(updater);
		animationButton.addSelectionListener(updater);
		animationCombo.addSelectionListener(updater);
		assemblerGraph.updateGraph();
		applicationGraph.updateGraph();
		update();
	}

	/**
	 * Returns the menu actions that are available for the control.
	 * 
	 * @return The menu actions available for the control.
	 */
	public Action[] getMenuActions() {
		return new Action[] {
			new RemoveAction(this, getManager())
		};
	}
	
	/**
	 * Called whenever the control is disposed. This will unregister
	 * the application listener from the application before it 
	 * disposes the parent control.
	 */
	public void disposeControl() {
		assemblerGraph.getUpdateManager().dispose();
		applicationGraph.getUpdateManager().dispose();
		super.disposeControl();
		synchronized (blockTime) {
			blockTime[0] = 0;
			blockTime.notifyAll();
		}
	}
	
	/**
	 * Sets the zoom to the specified values.
	 * 
	 * @param auto A boolean that indicates auto-zoom.
	 * @param index The selected zoom in the zoom combo.
	 */
	public void setZoom(boolean auto, int index) {
		zoomButton.setSelection(auto);
		if (index != -1) {
			zoomCombo.select(index);	
		}
		update();
	}
	
	/**
	 * Sets the stepping to the specified values.
	 * 
	 * @param auto A boolean that indicates auto stepping.
	 * @param index The selected stepping index in the combo.
	 */
	public void setStep(boolean auto, int index) {
		stepButton.setSelection(auto);
		if (index != -1) {
			stepCombo.select(index);	
		}
		update();
	}

	/**
	 * Enables or disables the animation and sets the animation
	 * combo to the specified selection index.
	 * 
	 * @param enabled The enabled or disabled flag of the animation.
	 * @param index The selected index in the animation combo.
	 */
	public void setAnimation(boolean enabled, int index) {
		animationButton.setSelection(enabled);
		if (index != -1) {
			animationCombo.select(index);	
		}
		update();
	}
	
	/**
	 * Called whenever a configuration thread has entered
	 * the control and wants to return to its original task.
	 */
	protected void block() {
		synchronized (blockTime) {
			try {
				if (blockTime[0] == 0) {
					// just continue, no wait
				} else if (blockTime[0] == -1) {
					blockTime.wait();
				} else {
					blockTime.wait(blockTime[0]);
				}							
			} catch (InterruptedException e) {
				Logging.error(getClass(), "Thread got interrupted.", e);
			}
		}
	}
	
	/**
	 * Unblocks all threads that are currently waiting.
	 */
	protected void unblock() {
		synchronized (blockTime) {
			blockTime.notifyAll();
		}
	}
	
	
	/**
	 * Called whenever a button or a combobox selection changes.
	 */
	protected void update() {
		// enable or disable the stepping time setup according to 
		stepCombo.setEnabled(stepButton.getSelection());
		stepNextButton.setEnabled(!stepButton.getSelection());
		if (stepButton.getSelection()) {
			int index = stepCombo.getSelectionIndex();
			if (index != -1) {
				int step = AbstractAssemblerControl.STEPS[index];
				synchronized (blockTime) {
					blockTime[0] = step;
					unblock();
				}
			}
		} else {
			synchronized (blockTime) {
				blockTime[0] = -1;
			}
		}
		// enable or disable the zoom selection based on the auto-zoom function
		zoomCombo.setEnabled(! zoomButton.getSelection());
		if (zoomButton.getSelection()) {
			autoZoom = true;
			zoom();
		} else {
			autoZoom = false;
			int index = zoomCombo.getSelectionIndex();
			if (index != -1) {
				int zoom = AbstractAssemblerControl.ZOOMS[index];
				double factor = (double)zoom / (double)100;
				assemblerPane.setScale(factor);
				assemblerPane.revalidate();
				applicationPane.setScale(factor);
				applicationPane.revalidate();
			}
		}
		// enable or disable animation speed selection based on animations
		animationCombo.setEnabled(animationButton.getSelection());
		// change animation speed according to setup.
		if (animationButton.getSelection()) {
			int index = animationCombo.getSelectionIndex();
			if (index != -1) {
				int step = AbstractAssemblerControl.ANIMATIONS[index];
				assemblerGraph.setStep(step);
				applicationGraph.setStep(step);
			}
		} else {
			assemblerGraph.setStep(Integer.MAX_VALUE);
			applicationGraph.setStep(Integer.MAX_VALUE);
		}
	}
	
	/**
	 * Called whenever the graph structure has changed.
	 */
	public void zoom() {
		if (autoZoom) {
			Dimension asSize = assemblerGraph.getPreferredSize();
			Dimension apSize = applicationGraph.getPreferredSize();
			Point asPoint = assemblerCanvas.getSize();
			Point apPoint = applicationCanvas.getSize();
			
			for (int i = AbstractAssemblerControl.ZOOMS.length - 1; i > 0; i--) {
				int zoom = AbstractAssemblerControl.ZOOMS[i];
				double factor = (double)zoom / (double)100;
				if (asSize.width * factor <= asPoint.x &&
						asSize.height * factor <= asPoint.y &&
							apSize.width * factor <= apPoint.x &&
								apSize.height * factor <= apPoint.y) {
					assemblerPane.setScale(factor);
					assemblerPane.revalidate();
					applicationPane.setScale(factor);
					applicationPane.revalidate();
					return;
				}
			}
			int zoom = AbstractAssemblerControl.ZOOMS[0];
			double factor = (double)zoom / (double)100;
			assemblerPane.setScale(factor);
			assemblerPane.revalidate();
			applicationPane.setScale(factor);
			applicationPane.revalidate();
		}
	}
}
