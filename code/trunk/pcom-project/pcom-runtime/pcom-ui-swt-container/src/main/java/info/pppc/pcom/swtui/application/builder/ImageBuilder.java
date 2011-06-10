package info.pppc.pcom.swtui.application.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.swtui.widget.ImageButton;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.application.ApplicationDescriptor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * The image builder lets a user select an application image from
 * the set of predefined images.
 * 
 * @author Mac
 */
public class ImageBuilder extends AbstractBuilder {

	/**
	 * The resource key for the text that is shown when the images load.
	 */
	private static final String UI_LOAD = "info.pppc.pcom.swtui.application.builder.ImageBuilder.LOAD";
	
	/**
	 * The default images of the image builder.
	 */
	private static final String[] IMAGE_NAMES = new String[] {
		"camera.gif", "cd.gif", "cdrom.gif", "computer.gif", "document.gif",
		"empty.gif", "floppy.gif", "full.gif", "game.gif", "hdd.gif",
		"internet.gif", "mail.gif", "media.gif", "music.gif","network.gif",
		"office.gif", "paint.gif", "search.gif", "service.gif",	"tool.gif"
	};
	
	/**
	 * The default images.
	 */
	private Image[] images = null;
	
	/**
	 * The root composite of the image builder.
	 */
	private ScrolledComposite composite;
	
	/**
	 * The image composite that holds the images.
	 */
	private Composite imageComposite;
	
	/**
	 * The application descriptor to configure.
	 */
	private ApplicationDescriptor descriptor;
	
	/**
	 * The element manager used to show a progess bar whenever
	 * the images are loaded.
	 */
	private IElementManager manager;
	
	/**
	 * Creates a new image builder on the specified composite.
	 * 
	 * @param composite The composite used by the builder.
	 * @param manager The elment manager that is needed to load
	 * 	the images asynchronously.
	 */
	public ImageBuilder(Composite composite, IElementManager manager) {
		super(composite);
		this.manager = manager;
	}

	/**
	 * Creates the builder on the composite.
	 * 
	 * @param node The node containing the application descriptor.
	 */
	public void createControl(TreeNode node) {
		descriptor = (ApplicationDescriptor)node.getData();
		final Composite builderComposite = getComposite();
		builderComposite.setLayout(new FillLayout());
		composite = new ScrolledComposite(builderComposite, SWT.H_SCROLL);
		composite.setBackground(builderComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		imageComposite = new Composite(composite, SWT.NONE);
		imageComposite.setBackground(builderComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		composite.setContent(imageComposite);
		GridLayout layout = new GridLayout(IMAGE_NAMES.length + 2, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 3;
		layout.marginWidth = 0;
		imageComposite.setLayout(layout);
		// load images asychronously
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_LOAD), IMAGE_NAMES.length);
					images = new Image[IMAGE_NAMES.length];
					for (int i = 0; i < images.length; i++) {
						final int index = i;
						ImageDescriptor descriptor = ImageDescriptor.createFromURL
							(getClass().getResource(IMAGE_NAMES[i]));
						images[i] = descriptor.createImage(true, builderComposite.getDisplay());
						manager.run(new Runnable() {
							public void run() {
								ImageButton button = new ImageButton(imageComposite, SWT.NONE);
								button.setImage(images[index]);
								button.addSelectionListener(new SelectionAdapter() {
									public void widgetSelected(SelectionEvent e) {
										select(index);
									}
								});
								button.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 1, 1));															
							}
						});
						monitor.worked(1);
					}
					monitor.done();
				}
			}, false);			
		} catch (Throwable t) {
			Logging.error(getClass(), "Could not load images.", t);
			return;
		} 
		Label vspace1 = new Label(imageComposite, SWT.NONE);
		vspace1.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false, 1, 1));
		Label vspace2 = new Label(imageComposite, SWT.NONE);
		vspace2.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false, 1, 1));
		composite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				update();
			}
		});
		update();
		
	}
	
	/**
	 * Selects the i-th image as selected image.
	 * 
	 * @param i The index of the image.
	 */
	protected void select(int i) {
		try {
			InputStream stream = getClass().getResourceAsStream(IMAGE_NAMES[i]);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = stream.read(buffer);
			while (read != -1) {
				bos.write(buffer, 0, read);
				read = stream.read(buffer);
			}
			descriptor.setImage(bos.toByteArray());
		} catch (IOException e) {
			descriptor.setImage(null);
		}
		done();
	}

	/**
	 * Called to dispose the builder. This will free all image data
	 * and all controls.
	 */
	public void dispose() {
		imageComposite.dispose();
		composite.dispose();
		for (int i = 0; i < images.length; i++) {
			if (images[i] != null) images[i].dispose();
		}
		composite = null;
		images = null;
	}
	
	/**
	 * Updates the size of the content pane according to the dimensions
	 * of it and the scrollable pane.
	 */
	private void update() {
		imageComposite.setSize(imageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		if (imageComposite != null && composite != null) {
			Point p1 = imageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Rectangle r = composite.getClientArea();
			Point p2 = new Point(r.width, r.height);
			Point p = new Point(p2.x, p2.y);
			p.x = Math.max(p.x, p1.x);
			p.y = Math.max(p.y + 5, p1.y + 5);
			imageComposite.setSize(p);
		}		
	}
}
