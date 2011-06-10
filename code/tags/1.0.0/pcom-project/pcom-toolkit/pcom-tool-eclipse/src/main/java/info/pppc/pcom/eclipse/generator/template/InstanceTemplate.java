package info.pppc.pcom.eclipse.generator.template;

import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import info.pppc.pcom.eclipse.Plugin;
import info.pppc.pcom.eclipse.generator.model.InstanceModel;
import info.pppc.pcom.eclipse.generator.util.JavaUtility;

/**
 * The instance template is used to generate an instance using
 * a instance model as input.
 * 
 * @author Mac
 */
public class InstanceTemplate extends AbstractTemplate {

	/**
	 * The pcom instance interface.
	 */
	public static final String CLASS_INSTANCE = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.instance");
	
	/**
	 * The pcom instance context interface.
	 */
	public static final String CLASS_INSTANCE_CONTEXT = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.instancecontext");
	
	/**
	 * The application methods that must be implemented by the instance.
	 */
	protected IMethod[] methods = null;
	
	/**
	 * Creates a new uninitialized instance template.
	 */
	public InstanceTemplate() {
		super();
	}
	
	/**
	 * Returns the instance model that has been set during
	 * the initialization. This model is used to create the
	 * factory.
	 * 
	 * @return The model used to create the factory.
	 */
	protected InstanceModel getModel() {
		return (InstanceModel)source;
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
		IType[] ifaces = getModel().getInterfaces();
		interfaces = new String[ifaces.length + 1];
		for (int i = 0; i < ifaces.length; i++) {
			interfaces[i] = ifaces[i].getFullyQualifiedName();
		}
		interfaces[ifaces.length] = CLASS_INSTANCE;
		methods = JavaUtility.mergeMethods(ifaces);
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
		// write factory field
		out.println("/**");
		out.println(" * The factory that has created the instance.");
		out.println(" */");
		out.print("protected ");
		out.print(getModel().getFactory());
		out.println(" factory;");
		out.println();
		// write context field
		out.println("/**");
		out.println(" * The context used to gain access to the container.");
		out.println(" */");
		out.print("protected ");
		out.print(CLASS_INSTANCE_CONTEXT);
		out.println(" context;");
		out.println();
		// write constructor
		out.println("/**");
		out.println(" * Creates a new instance using the specified factory.");
		out.println(" *");
		out.println(" * @param factory The factory that created the instance.");
		out.println(" */");
		out.print("public ");
		out.print(getClassName());
		out.print("(");
		out.print(getModel().getFactory());
		out.println(" factory) {");
		out.println("this.factory = factory;");
		out.println("}");
		out.println();
		// write instance context methods
		out.println("/**");
		out.println(" * Called to set the context of the instance.");
		out.println(" *");
		out.println(" * @param context The context of the instance.");
		out.println(" */");
		out.print("public void setContext(");
		out.print(CLASS_INSTANCE_CONTEXT);
		out.println(" context) {");
		out.println("this.context = context;");
		out.println("}");
		out.println();
		out.println("/**");
		out.println(" * Called to unset the context of the instance.");
		out.println(" */");
		out.println("public void unsetContext() {");
		out.println("this.context = null;");
		out.println("}");
		out.println();
		// write start, pause and stop methods
		out.println("/**");
		out.println(" * Called to start the instance.");
		out.println(" */");
		out.println("public void start() {");
		out.println("// TODO: implement");
		out.println("}");
		out.println();
		out.println("/**");
		out.println(" * Called to pause the instance.");
		out.println(" */");
		out.println("public void pause() {");
		out.println("// TODO: implement");
		out.println("}");
		out.println();
		out.println("/**");
		out.println(" * Called to stop the instance.");
		out.println(" */");
		out.println("public void stop() {");
		out.println("// TODO: implement");
		out.println("}");
		out.println();
		// write application method stubs
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				out.println();
				writeMethod(methods[i]);
			}		
		}
	}
	
	/**
	 * Writes a stub for a certain application method.
	 * 
	 * @param method The method generated.
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	public void writeMethod(IMethod method) throws JavaModelException {
		// generate javadoc comment that meets compiler checks
		out.println("/**");
		out.println(" * Application method that must be implemented by the instance.");
		out.println(" *");
		for (int i = 0; i < method.getParameterNames().length; i++) {
			out.print(" * @param ");
			out.print(method.getParameterNames()[i]);
			out.print(" see ");
			out.println(method.getDeclaringType().getFullyQualifiedName());
		}
		if (! method.getReturnType().equals(Signature.SIG_VOID)) {
			out.print(" * @return see");
			out.println(method.getDeclaringType().getFullyQualifiedName());
		}
		for (int i = 0; i < method.getExceptionTypes().length; i++) {
			out.print(" * @throws ");
			out.print(JavaUtility.getQualifiedType(method.getDeclaringType(), method.getExceptionTypes()[i]));
			out.print(" see ");
			out.println(method.getDeclaringType().getFullyQualifiedName());
		}
		out.println(" * @see " + method.getDeclaringType().getFullyQualifiedName());
		out.println(" */");
		IType interfaceType = method.getDeclaringType();
		// needed for method name in invokeSynchronious
		String signature = JavaUtility.generateMethodSignature(method, "", true, true);
		// generate method header
		out.print(Flags.toString(method.getFlags()) + " " + signature + " ");
		String[] exceptions = method.getExceptionTypes();				
		if(exceptions != null && exceptions.length > 0){
			out.print("throws ");	
			for(int i = 0; i < exceptions.length - 1; i++){				
				out.print(JavaUtility.getQualifiedType(interfaceType, exceptions[i]) + ", ");
			}
			out.print(JavaUtility.getQualifiedType(interfaceType, exceptions[exceptions.length - 1]));	
		}
		out.println(" {");
		out.println("// TODO: implement");
		String returnType = JavaUtility.getQualifiedType(interfaceType, method.getReturnType());
		if (returnType.equals("Z")) {
			out.println("return false;");
		} else if (returnType.equals("V")) {
			// nothing to be done, returns void
		} else if (returnType.length() == 1) {
			out.println("return 0;");
		} else {
			out.println("return null;");
		}
		// generate method footer
		out.println("}");
	}
	
	
	/**
	 * Writes the instance using the model that has been
	 * passed to the template during the last initialization.
	 * 
	 * @throws JavaModelException Thrown if some java 
	 * 	problem occured during generation.
	 */
	public void write() throws JavaModelException {
		writeClass();
	}

}
