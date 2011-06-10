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
import info.pppc.pcom.eclipse.generator.model.ProxyModel;
import info.pppc.pcom.eclipse.generator.util.JavaUtility;

/**
 * The proxy template is used to generate instance proxies using
 * a proxy model as input.
 * 
 * @author Mac
 */
public class ProxyTemplate extends AbstractTemplate {

	/**
	 * The pcom proxy class.
	 */
	public static final String CLASS_PROXY = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.proxyimpl");
	
	/**
	 * The methods that must be implemented by the proxy.
	 */
	protected IMethod[] methods = null;
	
	/**
	 * Creates a new uninitialized proxy template.
	 */
	public ProxyTemplate() {
		super();
	}

	/**
	 * Returns the proxy model that has been passed as source
	 * during the last initialization. This model is used
	 * to generate the proxy.
	 * 
	 * @return The proxy model that has been set during the
	 * 	last initialization.
	 */
	protected ProxyModel getModel() {
		return (ProxyModel)source;
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
		baseclass = CLASS_PROXY;
		// initialize interfaces and methods
		IType[] types = getModel().getInterfaces();
		interfaces = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			interfaces[i] = types[i].getFullyQualifiedName();
		}
		methods = JavaUtility.mergeMethods(types);
	}
	
	/**
	 * Writes the class comment. Overwrites the method in abstract template.
	 * 
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	protected void writeClassComment() throws JavaModelException {
		out.println("/**");
		out.println(" * Do not modify this file. This class has been generated.");
		out.println(" * Use inheritance or composition to add functionality.");
		out.println(" *");
		out.println(" * @author 3PC Pcom Tools");
		out.println(" */");		
	}
	
	/**
	 * Writes the body of the proxy.
	 * 
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	protected void writeBody() throws JavaModelException {
		// write constructor
		writeDefaultConstructor();
		// write method proxies
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				out.println();
				writeMethod(methods[i]);
			}		
		}
	}

	/**
	 * Writes a single synchronous method call for the specified method.
	 * 
	 * @param method The method generated.
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	public void writeMethod(IMethod method) throws JavaModelException {
		// generate javadoc comment that meets compiler checks
		out.println("/**");
		out.println(" * Proxy method that creates and transfers an invocation for the interface method.");
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
		// generate method body
		String[] parNames = method.getParameterNames();
		String[] parTypes = method.getParameterTypes();
		out.println("Object[] __args = new Object[" + parNames.length + "];");
		for(int i = 0; i < parNames.length; i++){
			out.print("__args["+ i + "] = ");
			String qualified = JavaUtility.getQualifiedType(interfaceType, parTypes[i]);
			out.print(JavaUtility.boxVariable(qualified, parNames[i]));
			out.println(";");
		}
		out.println("String __method = \"" + JavaUtility.generateMethodSignature(method, "", false, true) + "\";");
		out.println(CLASS_INVOCATION + " __invocation = __create(__method, __args);");
		out.println(CLASS_RESULT + " __result = __invoke(__invocation);");
		out.println("if (__result.hasException()) {");
		for(int i = 0; i < exceptions.length; i++){
			out.println("if (__result.getException() instanceof " + JavaUtility.getQualifiedType(interfaceType, exceptions[i]) + ") {"); 
			out.println("throw (" + JavaUtility.getQualifiedType(interfaceType,exceptions[i]) + ")__result.getException();");	
			out.println("}");								
		}
		out.println("throw (RuntimeException)__result.getException();");
		out.println("}");
		String returnType = JavaUtility.getQualifiedType(interfaceType, method.getReturnType());
		out.println("return " + JavaUtility.unboxVariable(returnType, "__result.getValue()") + ";");
		// generate method footer
		out.println("}");
	}
	
	/**
	 * Writes the proxy using the model that has been
	 * passed to the template during the last initialization.
	 * 
	 * @throws JavaModelException Thrown if some java problem
	 * 	occured during generation.
	 */
	public void write() throws JavaModelException {
		writeClass();
	}

}
