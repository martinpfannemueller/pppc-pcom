package info.pppc.pcom.eclipse.markers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The marker factory provides common commands to create markers used 
 * by the pcom tool.
 * 
 * @author Mac
 */
public class MarkerFactory {

	/**
	 * The name of the error marker used by pcom tools.
	 */
	public static final String MARKER_ERROR = "info.pppc.pcom.eclipse.marker.error";
	
	/**
	 * The name of the warning marker used by pcom tools.
	 */
	public static final String MARKER_WARNING = "info.pppc.pcom.eclipse.marker.warning";
	
	/**
	 * Creates a persistent error marker with the specified error message at the
	 * specified line for the passed resource. 
	 * 
	 * @param resource The resource that should receive the marker.
	 * @param message The message that the marker will contain.
	 * @param line The line number that should be marked by the marker.
	 * @throws CoreException Thrown by eclipse if the marker could not be created.
	 */
	public static void createError(IResource resource, String message, int line) throws CoreException {
		HashMap map = new HashMap();
		map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
		map.put(IMarker.MESSAGE, message);
		map.put(IMarker.LINE_NUMBER, new Integer(line));
		createMarker(resource, map, MARKER_ERROR);
	}
	
	/**
	 * Creates a non-persistent warning marker with the specified warning message
	 * on the specified resource.
	 * 
	 * @param resource The resource that should receive the marker. Must not be null. 
	 * @param message The message that the marker will contain.
	 * @throws CoreException Thrown by eclipse if the marker could not be created.
	 */
	public static void createWarning(IResource resource, String message) throws CoreException {
		HashMap map = new HashMap();
		map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
		map.put(IMarker.MESSAGE, message);
		createMarker(resource, map, MARKER_WARNING);
	}

	/**
	 * Removes all error and warning markers from the specified resource.
	 * If the resource is a folder, all resources contained in the folder will
	 * be cleared, too.
	 * 
	 * @param resource The resource whose markers should be removed. Must not be null.
	 * @throws CoreException Thrown by eclipse if the markers could not be
	 * 	delected.
	 */
	public static void delete(IResource resource) throws CoreException {
		resource.deleteMarkers(MARKER_ERROR, false, IResource.DEPTH_INFINITE);
		resource.deleteMarkers(MARKER_WARNING, false, IResource.DEPTH_INFINITE);
	}
	
	/**
	 * Creates a new marker with the specified attributes of the specified type
	 * on the specified resource.
	 * 
	 * @param resource The resource that will receive the marker.
	 * @param attributes The attributes of the marker.
	 * @param markerType The type of marker to create.
	 * @throws CoreException Thrown by eclipse if the marker could not be created.
	 */
	public static void createMarker(final IResource resource, final Map attributes, final String markerType) throws CoreException {
		IWorkspaceRunnable r= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker= resource.createMarker(markerType);
				marker.setAttributes(attributes);
			}
		};
		resource.getWorkspace().run(r, null,IWorkspace.AVOID_UPDATE, null);
	}
	
}
