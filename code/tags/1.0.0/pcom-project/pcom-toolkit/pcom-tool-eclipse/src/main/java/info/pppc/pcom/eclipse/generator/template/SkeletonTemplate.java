package info.pppc.pcom.eclipse.generator.template;

import java.io.PrintWriter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import info.pppc.pcom.eclipse.Plugin;
import info.pppc.pcom.eclipse.generator.model.SkeletonModel;
import info.pppc.pcom.eclipse.generator.util.JavaUtility;

/**
 * The skeleton template is used to generate an instance skeleton using
 * a skeleton model as input.
 * 
 * @author Mac
 */
public class SkeletonTemplate extends AbstractTemplate {

	/**
	 * The pcom skeleton class.
	 */
	public static final String CLASS_SKELETON = Plugin.getDefault().getResourceString("info.pppc.pcom.eclipse.class.skeletonimpl");
	
	/**
	 * The methods that must be dispatched by the skeleton.
	 */
	protected IMethod[] methods = null;
	
	/**
	 * Creates a new uninitialized skeleton template.
	 */
	public SkeletonTemplate() {
		super();
	}
	
	/**
	 * Returns the skeleton model that has been passed to
	 * the template during the last initialization. This
	 * model will be used during generation.
	 * 
	 * @return The skeleton model used for generation.
	 */
	protected SkeletonModel getModel() {
		return (SkeletonModel)source;
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
		baseclass = CLASS_SKELETON;
		IType[] types = getModel().getInterfaces();
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
	 * Writes the body of the skeleton. This will create the dispatch 
	 * method and the corresponding helper methods to initalize the 
	 * component instance.
	 * 
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	protected void writeBody() throws JavaModelException {
		// write default constructor
		writeDefaultConstructor();
		out.println();
		// write dispatch method
		writeDispatchMethod();
	}	
	
	/**
	 * Creates the dispatch method for all application methods that need
	 * to be supported.
	 * 
	 * @throws JavaModelException Thrown by eclipse if the operation fails.
	 */
	protected void writeDispatchMethod() throws JavaModelException {
		out.println("/**");
		out.println(" * Dispatch method that dispatches incoming invocations to the skeleton's implementation.");
		out.println(" *");
		out.println(" * @param method The signature of the method to call.");
		out.println(" * @param args The parameters of the method call.");
		out.println(" * @return The result of the method call.");
		out.println(" */");
		String cinterface = getModel().getTarget(); 
		// create method signature
		out.println("protected " + CLASS_RESULT + 
			" dispatch(String method, Object[] args) {");
		// create method body
		out.println(cinterface + " impl = (" + cinterface + ")getTarget();");
		// try for application exceptions
		out.println("try {");
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				writeDispatchMethod(methods[i], i == 0, i != methods.length - 1);
			}
		}
		// return unknown signature
		out.print("return new " + CLASS_RESULT + "(null, new ");
		out.println(CLASS_EXCEPTION + "(\"Illegal signature.\"));");
		// try catch block for application exceptions.
		out.println("} catch (Throwable t) {");
		out.println("return new " + CLASS_RESULT + "(null, t);");
		out.println("}");
		// create method footer
		out.println("}");		
	}
	
	/**
	 * Writes a fragment of the dispatch method that will dispatch the
	 * specified method.
	 * 
	 * @param method The method to dispatch.
	 * @param first A flag that indicates whether this is the first 
	 * 	method to generate.
	 * @param last A flag that indicates whether this is the last 
	 * 	method to generate.
	 * @throws JavaModelException Thrown by eclipse if the operation
	 * 	fails.
	 */
	protected void writeDispatchMethod(IMethod method, boolean first, 
			boolean last) throws JavaModelException{
		IType interfaceType = method.getDeclaringType();
		String signature = JavaUtility.generateMethodSignature(method, "", false, true);
		String returnType = JavaUtility.getQualifiedType(interfaceType, method.getReturnType());
		if (! first) {
			out.print("else ");
		}
		out.println("if (method.equals(\"" + signature + "\")) {");
		if (returnType.equals("V")) {
			out.println("Object result = null;");	
		} else {
			out.print("Object result = ");
		}
		// create method call
		String methodName = method.getElementName();
		String[] paramNames = method.getParameterNames();
		String[] paramTypes = method.getParameterTypes();
		String callName = "impl." + methodName + "(";		
		for(int i = 0; i < paramNames.length; i++){
			String qualified = JavaUtility.getQualifiedType(interfaceType, paramTypes[i]);
			callName += (JavaUtility.unboxVariable(qualified, "args["+i+"]"));
			if(i != paramNames.length - 1)
				callName += ", ";
		}
		callName+=")";
		out.println(JavaUtility.boxVariable(returnType, callName) + ";");		
		out.println("return new " + CLASS_RESULT + "(result, null);");
		out.print("}");
		if (last) {
			out.println();
		}
	}
	
	/**
	 * Writes the skeleton using the model that has been
	 * passed to the skeleton template during the last
	 * initialization.
	 * 
	 * @throws JavaModelException Thrown if some java 
	 * 	problem occurs during initialization.
	 */
	public void write() throws JavaModelException {
		writeClass();
	}

}
