package info.pppc.pcom.eclipse.generator.template;

import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import info.pppc.pcom.eclipse.Plugin;
import info.pppc.pcom.eclipse.generator.model.FactoryModel;

/**
 * The factory template is used to generate instance factories using
 * a factory model as input.
 * 
 * @author Mac
 */
public class FactoryTemplate extends AbstractTemplate {

	/**
	 * The pcom factory interface.
	 */
	public static final String CLASS_FACTORY = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.factory");
	
	/**
	 * The pcom factory context interface.
	 */
	public static final String CLASS_FACTORY_CONTEXT = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.factorycontext");
	
	/**
	 * The pcom instance interface.
	 */
	public static final String CLASS_INSTANCE = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.instance");
	
	/**
	 * The pcom proxy interface.
	 */
	public static final String CLASS_PROXY = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.proxy");
	
	/**
	 * The pcom skeleton class.
	 */
	public static final String CLASS_SKELETON = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.skeleton");

	/**
	 * The interface name for instance setups.
	 */
	public static final String CLASS_SETUP = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.setup");
		
	/**
	 * The interface name for demand readers.
	 */
	public static final String CLASS_DEMAND = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.demand");
	
	/**
	 * Creates a new uninitialized factory template.
	 */
	public FactoryTemplate() {
		super();
	}
	
	/**
	 * Returns the factory model that is used to retrieve the
	 * factory's structure.
	 * 
	 * @return The factory model used during generation.
	 */
	protected FactoryModel getModel() {
		return (FactoryModel)source;
	}

	/**
	 * Called whenever the template is initialized.
	 * 
	 * @param project The project that is used to perform type lookups.
	 * @param source The source data model used during generation.
	 * @param classname The fully qualified class name of the artefact.
	 * @param out The print writer used to write the templates contents.
	 * @param monitor The progress monitor used to report progress.
	 * @throws JavaModelException Thrown if some type lookup failed.
	 */
	public void init(IJavaProject project, Object source, String classname, PrintWriter out, IProgressMonitor monitor) throws JavaModelException {
		super.init(project, source, classname, out, monitor);
		interfaces = new String[] { CLASS_FACTORY };
		abstracted = false;
	}
	
	/**
	 * Writes the class comment. Overwrites the method in abstract template.
	 * 
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	protected void writeClassComment() throws JavaModelException {
		out.println("/**");
		out.println(" * This class file has been generated. It can be freely modified.");
		out.println(" * It will not be regenerated unless it is removed.");
		out.println(" *");
		out.println(" * @author 3PC Pcom Tools");
		out.println(" */");		
	}
	
	/**
	 * Writes the contents of the template using the current configuration.
	 * 
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	protected void writeBody() throws JavaModelException {
		// write factory context field
		out.println("/**");
		out.println(" * The context used to gain access to the container.");
		out.println(" */");
		out.print("private ");
		out.print(CLASS_FACTORY_CONTEXT);
		out.println(" context;");
		out.println();
		// write constructor
		writeDefaultConstructor();
		out.println();
		// write name method
		out.println("/**");
		out.println(" * Returns the human readable name of the factory.");
		out.println(" *");
		out.println(" * @return The human readable name of the factory.");
		out.println(" */");
		out.println("public String getName() {");
		out.print("return \"");
		out.print(getModel().getName());
		out.println("\";");
		out.println("}");
		out.println();
		// write factory context methods
		out.println("/**");
		out.println(" * Called to set the context which can be used to access the container.");
		out.println(" *");
		out.println(" * @param context The context used to gain access to the container.");
		out.println(" */");
		out.print("public void setContext(");
		out.print(CLASS_FACTORY_CONTEXT);
		out.println(" context) {");
		out.println("this.context = context;");
		out.println("}");
		out.println();
		out.println("/**");
		out.println(" * Called to unset the context that has been set earlier.");
		out.println(" */");
		out.println("public void unsetContext() {");
		out.println("this.context = null;");
		out.println("}");
		out.println();
		// write start and stop methods
		out.println("/**");
		out.println(" * Called to start the factory.");
		out.println(" */");		
		out.println("public void start() {");
		out.println("// TODO: implement");
		out.println("}");
		out.println();
		out.println("/**");
		out.println(" * Called to stop the factory.");
		out.println(" */");		
		out.println("public void stop() {");
		out.println("// TODO: implement");
		out.println("}");
		out.println();
		// write create instance method
		out.println("/**");
		out.println(" * Creates and returns a new uninitialized instance.");
		out.println(" *");
		out.println(" * @return The newly created instance.");
		out.println(" */");
		out.print("public ");
		out.print(CLASS_INSTANCE);
		out.println(" createInstance() {");
		out.print("return new ");
		out.print(getModel().getInstance());
		out.println("(this);");
		out.println("}");
		out.println();
		// write create proxy method
		out.println("/**");
		out.println(" * Returns a new uninitialized proxy for the specified demand.");
		out.println(" *");
		out.println(" * @param name The name of the demand whose proxy should be retrieved.");
		out.println(" * @return The new proxy for the demand or null if the name is illegal.");
		out.println(" */");
		out.print("public ");
		out.print(CLASS_PROXY);
		out.println(" createProxy(String name) {");
		String[] proxies = getModel().getProxies();
		for (int i = 0; i < proxies.length; i++) {
			out.print("if (name.equals(\"");
			out.print(proxies[i]);
			out.println("\")) {");
			out.print("return new ");
			out.print(getModel().getProxy(proxies[i]));
			out.println("();");
			out.println("}");
		}
		out.println("return null;");
		out.println("}");
		out.println();
		// write create skeleton method
		out.println("/**");
		out.println(" * Returns a new uninitialized skeleton for the instance.");
		out.println(" *");
		out.println(" * @return The newly created skeleton for the instance.");
		out.println(" */");
		out.print("public ");
		out.print(CLASS_SKELETON);
		out.println(" createSkeleton() {");
		out.print("return new ");
		out.print(getModel().getSkeleton());
		out.println("();");
		out.println("}");
		out.println();
		// write derive setups method
		out.println("/**");
		out.println(" * Returns the setups that can be derived for the demand or null");
		out.println(" * if the demand cannot be met by instances of the factory.");
		out.println(" *");
		out.println(" * @param demand The demand that needs to be met.");
		out.println(" * @return The setups for instances or null.");
		out.println(" */");
		out.print("public ");
		out.print(CLASS_SETUP);
		out.print("[] deriveSetups(");
		out.print(CLASS_DEMAND);
		out.println(" demand) {");
		out.println("// TODO: implement");
		out.println("return null;");
		out.println("}");
	}
	
	/**
	 * Writes the factory using the current initialization.
	 * 
	 * @throws JavaModelException Thrown if the factory could
	 * 	not be generated due to some java problem.
	 */
	public void write() throws JavaModelException {
		writeClass();
	}

}
