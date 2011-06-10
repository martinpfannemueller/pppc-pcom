package info.pppc.pcom.eclipse.generator.util;

import info.pppc.pcom.eclipse.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * This utility class contains helper methods for handling the eclipse reflection
 * api.
 * 
 * @author Mac, batorfc
 */
public class JavaUtility {

	/**
	 * Returns the fully qualified class name that is denoted by the specified 
	 * unqualified, eclipse internal name. The type parameter specifies the 
	 * context that is used to resolve the class name. If the name denotes a
	 * primitive type, this method returns null.
	 * 
	 * @param context The context used to resolve the type.
	 * @param name The name of the type to resolve.
	 * @return the given name, if we have an internal type like int,long, etc. or
	 * the fully qualified class name of a normal class
	 * @throws JavaModelException Thrown if the type cannot be resolved.
	 */
	public static String getQualifiedType(IType context, String name) throws JavaModelException {
		int count = Signature.getArrayCount(name);
		if (count > 0) {
			name = Signature.getElementType(name);
		}
		Stack stack = new Stack();
		stack.push(context);
		String result = name; // need non qualified name for boxing/unboxing	
		stack: while (!stack.empty()) {
			IType curType = (IType) stack.pop();
			String[][] options = curType.resolveType(getJavaType(name));
			if (options != null) {
				result = options[0][0] + "." + options[0][1];
				break stack;
			} else {
				String[] interfaceNames = curType.getSuperInterfaceNames();
				for (int i = 0; i < interfaceNames.length; i++) {
					options = curType.resolveType(getJavaType(interfaceNames[i]));
					if (options != null) {
						String parentQName = options[0][0] + "." + options[0][1];
						IType parent = curType.getJavaProject().findType(parentQName);
						if (!stack.contains(parent))
							stack.push(parent);
						break stack;
					}
				}
			}
		}
		if (count > 0) {
			result = Signature.createArraySignature(result, count);
		}
		return result;
	}

	/**
	 * Box generic types like int, long etc. to Wrapper classes Integer, Long etc.
	 * The boxing is needed, because we pass an Object[] array as argument
	 * for our RPC call.
	 * 
	 * @param type internal name of type, generated by eclipse reflection
	 * @param name variable name
	 * @return boxed variable
	 */
	public static String boxVariable(String type, String name) {
		String result = name;
		int rank = 0;
		for (rank = 0; type.charAt(rank) == '['; rank++);

		if (rank == 0) {
			if ((type.length() - rank) == 1) {
				boolean found = true;
				switch (type.charAt(0)) {
					case 'I' :
						result = "new Integer";
						break;
					case 'Z' :
						result = "new Boolean";
						break;
					case 'D' :
						result = "new Double";
						break;
					case 'J' :
						result = "new Long";
						break;
					case 'B' :
						result = "new Byte";
						break;
					case 'C' :
						result = "new Character";
						break;
					case 'F' :
						result = "new Float";
						break;
					case 'S' :
						result = "new Short";
						break;
					default :
						found = false;
						break;
				}
				if (found) {
					result += "(" + (name) + ")";
				}
			}
		}
		return result;
	}

	/**
	 * Unbox variables of type int, long, etc.
	 * The boxing is needed, because we pass an Object[] array as argument
	 * for our RPC call.
	 * 
	 * @param type internal name of type, generated by eclipse reflection
	 * @param name variable name The name of the variable to unbox.
	 * @return The unboxed variable.
	 */
	public static String unboxVariable(String type, String name) {
		String result = null;
		int rank = 0;
		for (rank = 0; type.charAt(rank) == '['; rank++);

		if (rank == 0) {
			if ((type.length() - rank) == 1) {
				switch (type.charAt(rank)) {
					case 'I' :
						result = "((Integer)" + name + ").intValue()";
						break;
					case 'Z' :
						result = "((Boolean)" + name + ").booleanValue()";
						break;
					case 'D' :
						result = "((Double)" + name + ").doubleValue()";
						break;
					case 'J' :
						result = "((Long)" + name + ").longValue()";
						break;
					case 'B' :
						result = "((Byte)" + name + ").byteValue()";
						break;
					case 'C' :
						result = "((Character)" + name + ").charValue()";
						break;
					case 'F' :
						result = "((Float)" + name + ").floatValue()";
						break;
					case 'S' :
						result = "((Short)" + name + ").shortValue()";
						break;
					case 'V' :
						result = "";
						break;
					default :
						result = "ERROR;)";
				}
			} else {
				result = "(" + JavaUtility.getJavaType(type) + ")" + name;
			}
		} else {
			result = "(" + JavaUtility.getJavaType(type) + ")" + name;
		}
		return result;
	}
	
	/**
	 * Generates a string representation for the method declaration of a given eclipse 
	 * reflection object. The name can be changed by adding an appendix.
	 * 
	 * @param appendix The appendix that should be added to the method name.
	 * @param method The eclipse reflection method to generate a string representation.
	 * @param names True to include the names of the parameters, false to remove them.
	 * @param value A flag that indicates whether the return value should be contained.
	 * @return generated method signature.
	 * @throws JavaModelException Thrown by the java model.
	 */
	public static String generateMethodSignature(IMethod method, String appendix, boolean names, boolean value) throws JavaModelException {
		StringBuffer buf = new StringBuffer();
		String methodName = method.getElementName();
		String[] parTypes = method.getParameterTypes();
		String[] parNames = method.getParameterNames();
		if (value) {
			String returnType = getJavaType(getQualifiedType(method.getDeclaringType(), method.getReturnType()));
			buf.append(returnType);
			buf.append(" ");
		}
		buf.append(methodName + appendix);
		buf.append("(");
		for (int i = 0; i < parTypes.length; i++) {
			String parType = getJavaType(getQualifiedType(method.getDeclaringType(), parTypes[i]));
			buf.append(parType);
			if (names) {
				buf.append(" ");
				buf.append(parNames[i]);
			}
			if (i != parTypes.length - 1)
				buf.append(", ");
		}
		buf.append(")");
		return buf.toString();	
	}

	/**
	 * Converts the internal type names of eclipse reflection to names used in java 
	 * source code. This conversion is needed for generic types like int, long, etc
	 * and for object types. If a fully qualified type is passed to this method, this
	 * method will return a fully qualified name. If the unqualified name is passed,
	 * the method will return an unqualified name.
	 *  
	 * @param type Internal name of type, generated by eclipse reflection.
	 * @return The source type name of the specified eclipse type name.
	 * @throws IllegalArgumentException Thrown if the string does not represent a
	 * 	primitive java type.
	 */
	private static String getJavaType(String type) throws IllegalArgumentException {
		int rank = 0;
		for (rank = 0; type.charAt(rank) == '['; rank++);
		String result = type.substring(rank, type.length());
		if ((result.length()) == 1) {
			switch (type.charAt(rank)) {
				case 'I' :
					result = "int";
					break;
				case 'Z' :
					result = "boolean";
					break;
				case 'D' :
					result = "double";
					break;
				case 'J' :
					result = "long";
					break;
				case 'B' :
					result = "byte";
					break;
				case 'C' :
					result = "char";
					break;
				case 'F' :
					result = "float";
					break;
				case 'S' :
					result = "short";
					break;
				case 'V' :
					result = "void";
					break;
				default :
					throw new IllegalArgumentException("This is not a valid type.");
			}
		} else if (result.startsWith("Q") && result.endsWith(";")) {
			result = result.substring(1, result.length() - 1);
		}
		for (int i = 0; i < rank; i++) {
			result += "[]";
		}
		return result;
	}

	/**
	 * This method returns a list of all member function, including the ones
	 * declared in super interfaces.
	 * 
	 * @param type The interface to use.
	 * @return An array of methods declared by the type.
	 * @throws JavaModelException Thrown by eclipse if the reflection fails.
	 */
	public static IMethod[] getAllMethods(IType type) throws JavaModelException {
		Vector interfaces = new Vector();
		Vector result = new Vector();
		interfaces.addElement(type);
		while (!interfaces.isEmpty()) {
			IType curInterface = (IType) interfaces.remove(0);
			String[] superInterfaces = curInterface.getSuperInterfaceNames();
			for (int i = 0; i < superInterfaces.length; i++) {
				IType superInterface =
					type.getJavaProject().findType(JavaUtility.getQualifiedType(curInterface, superInterfaces[i]));
				if (superInterface != null && !interfaces.contains(superInterface)) {
					interfaces.addElement(superInterface);
				}
			}
			IMethod[] methods = curInterface.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (!result.contains(methods[i])) {
					result.add(methods[i]);
				}
			}
		}
		IMethod[] returnValue = new IMethod[result.size()];
		result.copyInto(returnValue);
		return returnValue;
	}

	/**
	 * Merges all methods of the given interfaces. The result has
	 * no duplicate methods.
	 * 
	 * @param type The types to merge.
	 * @return The unique methods of all interfaces.
	 * @throws JavaModelException Thrwon by the java model.
	 */
	public static IMethod[] mergeMethods(IType[] type) throws JavaModelException {
		HashMap map = new HashMap();
		for (int i = 0; i < type.length; i++) {
			IMethod[] methods = getAllMethods(type[i]);
			for (int j = 0; j < methods.length; j++) {
				String signature = generateMethodSignature(methods[j], "", false, true);
				map.put(signature, methods[j]);
			}
		}
		IMethod[] result = new IMethod[map.values().size()];
		map.values().toArray(result);
		return result;
	}

	/**
	 * Determines whether the specified eclipse internal name denotes a primitve
	 * type such as int, boolean, etc. Note that boolean, int, etc. arrays are
	 * not considered primitive types.
	 * 
	 * @param name The eclipse internal name of the type.
	 * @return True if the type denotes a primitive type.
	 */
	public static boolean isPrimitveType(String name) {
		if (name.length() == 1) {
			switch (name.charAt(0)) {
				case 'I' :
				case 'Z' :
				case 'D' :
				case 'J' :
				case 'B' :
				case 'C' :
				case 'F' :
				case 'S' :
				case 'V' :
					return true;
				default :
					break;
			}
		}
		return false;
	}

	/**
	 * Determines whether the specified eclipse internal name denotes a primitive
	 * array such as int[], boolean[], etc. Note that primitive types such as
	 * boolean, int, etc. that might be passed to this method will evaluate to
	 * false.
	 * 
	 * @param name The eclipse internal name of the type.
	 * @return True if the type denotes a primitive array.
	 */
	public static boolean isPrimitiveArray(String name) {
		int rank;
		for (rank = 0; name.charAt(rank) == '['; rank++);
		return (rank > 0) && JavaUtility.isPrimitveType(name.substring(rank));
	}

	/**
	 * Returns the package of a given file.
	 * 
	 * @param project A reference to a project.
	 * @param fileName A fully qualified name of a file (dot separated like a class name).
	 * @return The package of the file.
	 * @throws JavaModelException Thrown by the underlying java model.
	 */
	public static IPackageFragment getPackage(IJavaProject project, String fileName) throws JavaModelException {
		int index = fileName.lastIndexOf('.');
		String packageName = "";
		if (index != -1) {
			packageName = fileName.substring(0, index);
			packageName = packageName.replace('.', '/');	
		}
		return (IPackageFragment) project.findElement(new Path(packageName));
	}

	/**
	 * Creates the java file fileName in the java project project.
	 * 
	 * @param project the java project were the file must created.
	 * @param fileName the fully qualified name of the java class (dot separated like a class name)
	 * @param content the content of the file.
	 * @return the created compilation unit.
	 * @throws JavaModelException Thrown by the underlying java model.
	 */
	public static ICompilationUnit createJavaFile(IJavaProject project, String fileName, String content)
		throws JavaModelException {
		IPackageFragment packageFragment = getPackage(project, fileName);
		// maybe we should create non-existent packages ?
		if (packageFragment == null) {
			// create package name
			int index = fileName.lastIndexOf('.');
			String packageName = "";
			if (index != -1) {
				packageName = fileName.substring(0, index);
				packageName = packageName.replace('.', '/');	
			}
			// create status
			String pluginName = Plugin.getDefault().getBundle().getSymbolicName();
			Status status = new Status(IStatus.ERROR, pluginName, 0, 
				"Cannot find Java package \"" + packageName + "\" in project \"" + project.getElementName() + "\".", null);
			// throw exception
			throw new JavaModelException(new CoreException(status));
		}
		String genName = fileName.substring(fileName.lastIndexOf('.') + 1);
		return packageFragment.createCompilationUnit(genName + ".java", content, true, null);
	}

	/**
	 * Returns the types that are referenced by the specified interface through parameters,
	 * return values and exceptions that are declared through methods. The return value
	 * is an array of fully qualified java class names.
	 *  
	 * @param type The interface whose referenced types should be returned. 
	 * @return An array of strings that denote the full qualified class
	 * 	names of classes that are referenced through the specified interface.
	 * @throws JavaModelException Thrown if the lookup fails.
	 */
	public static String[] getReferencedTypes(IType type) throws JavaModelException {
		Vector types = new Vector();
		HashSet result = new HashSet();
		types.addElement(type);
		// recursive lookup through super types
		while (!types.isEmpty()) {
			type = (IType) types.remove(0);
			String[] sifs = type.getSuperInterfaceNames();
			for (int i = 0; i < sifs.length; i++) {
				String ifname = getQualifiedType(type, sifs[i]);
				if (ifname != null) {
					IType iftype = type.getJavaProject().findType(ifname);
					if (iftype != null) {
						types.addElement(iftype);
					}
				}
			}
			// for each type, all all return values, parameters and exceptions
			IMethod[] methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				IMethod method = methods[i];
				String returnname = method.getReturnType();
				String returntype = getQualifiedType(type, returnname);
				if (returntype != null) {
					result.add(returntype);
				}
				String[] paramnames = method.getParameterTypes();
				for (int j = 0; j < paramnames.length; j++) {
					String paramtype = getQualifiedType(type, paramnames[j]);
					if (paramtype != null) {
						result.add(paramtype);
					}
				}
				String[] exceptionnames = method.getExceptionTypes();
				for (int j = 0; j < exceptionnames.length; j++) {
					String exceptiontype = getQualifiedType(type, exceptionnames[j]);
					if (exceptiontype != null) {
						result.add(exceptiontype);
					}
				}
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}
}