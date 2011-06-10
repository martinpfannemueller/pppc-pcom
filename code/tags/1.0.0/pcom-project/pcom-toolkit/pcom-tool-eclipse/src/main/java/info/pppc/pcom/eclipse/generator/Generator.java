package info.pppc.pcom.eclipse.generator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

import info.pppc.pcom.eclipse.generator.io.FormattingWriter;
import info.pppc.pcom.eclipse.generator.model.FactoryModel;
import info.pppc.pcom.eclipse.generator.model.InstanceModel;
import info.pppc.pcom.eclipse.generator.model.Model;
import info.pppc.pcom.eclipse.generator.model.ProxyModel;
import info.pppc.pcom.eclipse.generator.model.SkeletonModel;
import info.pppc.pcom.eclipse.generator.template.AbstractTemplate;
import info.pppc.pcom.eclipse.generator.template.FactoryTemplate;
import info.pppc.pcom.eclipse.generator.template.InstanceTemplate;
import info.pppc.pcom.eclipse.generator.template.ProxyTemplate;
import info.pppc.pcom.eclipse.generator.template.SkeletonTemplate;
import info.pppc.pcom.eclipse.generator.util.JavaUtility;
import info.pppc.pcom.eclipse.parser.util.XMLUtility;
import info.pppc.pcom.eclipse.parser.xerces.LocatorDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Node;

/**
 * The generator is used to generate a component template from an 
 * xml description. 
 * 
 * @author Mac
 */
public class Generator {

	/**
	 * The name of the deployment descriptor node.
	 */
	private static final String NODE_DEPLOYMENT = "deployment";
	
	/**
	 * The name of the skeleton node within the deployment descriptor.
	 */
	private static final String NODE_SKELETON = "skeleton";
	
	/**
	 * The name of the factory node within the deployment descriptor.
	 */
	private static final String NODE_FACTORY = "factory";
	
	/**
	 * The name of the instance node within the deployment descriptor.
	 */
	private static final String NODE_INSTANCE = "instance";
	
	/**
	 * The name of the instance demand nodes.
	 */
	private static final String NODE_INSTANCE_DEMAND = "instance-demand";
	
	/**
	 * The name of the instance provision nodes.
	 */
	private static final String NODE_INSTANCE_PROVISION = "instance-provision";
	
	/**
	 * The name of the interface nodes.
	 */
	private static final String NODE_INTERFACE = "interface";
	
	/**
	 * The name of stateful nodes.
	 */
	private static final String NODE_STATEFUL = "stateful";
	
	/**
	 * The name of nodes that denote a name.
	 */
	private static final String NODE_NAME = "name";
	
	/**
	 * The name of proxy nodes that point to proxy class names.
	 */
	private static final String NODE_PROXY = "proxy";
	
	/**
	 * The value used as content for boolean nodes that denote true.
	 */
	private static final String VALUE_TRUE = "true";
	
	/**
	 * The java project that is used to retrieve types and to generate
	 * the new java files.
	 */
	private IJavaProject project;
	
	/**
	 * The monitor that is used to report updates.
	 */
	private IProgressMonitor monitor;
	
	/**
	 * The document that is used as input model.
	 */
	private LocatorDocument document;
	
	/**
	 * Creates a new unitialized generator. 
	 */
	public Generator() {
		super();
	}
	
	/**
	 * Initializes the service generator with the specified java
	 * project, locator document and progress monitor.
	 * 
	 * @param project The java project used for reflection.
	 * @param document The document used to generate the 
	 * @param monitor A progress monitor to display status messages.
	 */
	public void init(IJavaProject project, LocatorDocument document, IProgressMonitor monitor) {
		this.monitor = monitor;
		this.document = document;
		this.project = project;
	}
	
	/**
	 * Runs the generator using the setup that has been passed in the
	 * init method.
	 * 
	 * @throws JavaModelException Thrown if the generation failed for some
	 * 	reason.
	 */
	public void generate() throws JavaModelException {
		monitor.setTaskName("Generating Component Templates");
		// gemerate proxies from document
		ProxyTemplate proxyTemplate = new ProxyTemplate();
		ProxyModel[] proxyModels = getProxies();
		for (int i = 0; i < proxyModels.length; i++) {
			if (monitor.isCanceled()) return;
			String name = proxyModels[i].getClassname();
			if (name.length() > 10) name = name.substring(0, 7) + "...";
			monitor.setTaskName("Generating Proxy " + name);
			generate(proxyModels[i], proxyTemplate);
		}
		// generate skeleton from document
		if (monitor.isCanceled()) return;
		SkeletonModel skeletonModel = getSkeleton();
		String name = skeletonModel.getClassname();
		if (name.length() > 10) name = name.substring(0, 7) + "...";
		monitor.setTaskName("Generating Skeleton " + name);
		generate(skeletonModel, new SkeletonTemplate());
		// generate factory from document
		if (monitor.isCanceled()) return;
		FactoryModel factoryModel = getFactory();
		IType factory = project.findType(factoryModel.getClassname());
		if (factory == null) {
			monitor.setTaskName("Generating Factory Template");
			generate(factoryModel, new FactoryTemplate());	
		}
		// generate instance from document
		if (monitor.isCanceled()) return;
		InstanceModel instanceModel = getInstance();
		IType instance = project.findType(instanceModel.getClassname());
		if (instance == null) {
			monitor.setTaskName("Generating Instance Template");
			generate(instanceModel, new InstanceTemplate());
		}
	}

	/**
	 * Generates the output for a certain model using the specified template.
	 * 
	 * @param source The source that is used as source for the template.
	 * @param template The template that is used as template for the generation.
	 * @throws JavaModelException Thrown if the output could not be generated.
	 */
	private void generate(Model source, AbstractTemplate template) throws JavaModelException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bos);
		FormattingWriter fow = new FormattingWriter(osw);
		PrintWriter pw = new PrintWriter(fow);
		// generate contents
		template.init(project, source, source.getClassname(), pw, monitor);
		template.write();
		pw.flush();
		pw.close();
		byte[] bytes = bos.toByteArray();
		String content = new String(bytes);
		// create java file
		JavaUtility.createJavaFile(project, source.getClassname(), content);
	}
	
	/**
	 * Returns the proxy models defined by the locator document.
	 * 
	 * @return The proxy models defined by the locator document.
	 * @throws JavaModelException Thrown if the types cannot be
	 * 	resolved.
	 */
	private ProxyModel[] getProxies() throws JavaModelException {
		Vector proxies = new Vector();
		Node deployment = getDeployment();
		Vector children = XMLUtility.getChildren(deployment, NODE_INSTANCE_DEMAND);
		for (int i = 0; i < children.size(); i++) {
			Node child = (Node)children.elementAt(i);
			Node proxy = XMLUtility.getChild(child, NODE_PROXY);
			ProxyModel model = new ProxyModel(XMLUtility.getContent(proxy));
			Node stateful = XMLUtility.getChild(child, NODE_STATEFUL);
			model.setStateful(XMLUtility.getContent(stateful).equals(VALUE_TRUE));
			Vector ifaces = XMLUtility.getChildren(child, NODE_INTERFACE);
			for (int j = 0; j < ifaces.size(); j++) {
				Node iface = (Node)ifaces.get(j);
				IType itype = project.findType(XMLUtility.getContent(iface));
				model.addInterface(itype);
			}
			proxies.add(model);			
		}
		return (ProxyModel[])proxies.toArray(new ProxyModel[0]);
	}
	
	/**
	 * Returns the instance model contained in the locator document.
	 * 
	 * @return The instance model contained in the locator document.
	 * @throws JavaModelException Thrown if the interfaces cannot
	 * 	be resolved.
	 */
	private InstanceModel getInstance() throws JavaModelException {
		Node deployment = getDeployment();
		Node factory = XMLUtility.getChild(deployment, NODE_FACTORY);
		Node instance = XMLUtility.getChild(deployment, NODE_INSTANCE);
		Node provision = XMLUtility.getChild(deployment, NODE_INSTANCE_PROVISION);
		Node stateful = XMLUtility.getChild(provision, NODE_STATEFUL);
		InstanceModel model = new InstanceModel(XMLUtility.getContent(instance));
		model.setFactory(XMLUtility.getContent(factory));
		model.setStateful(XMLUtility.getContent(stateful).equals(VALUE_TRUE));
		Vector ifaces = XMLUtility.getChildren(provision, NODE_INTERFACE);
		for (int i = 0; i < ifaces.size(); i++) {
			Node iface = (Node)ifaces.get(i);
			IType itype = project.findType(XMLUtility.getContent(iface));
			model.addInterface(itype);			
		}
		return model;
	}
	
	/**
	 * Returns the factory model contained in the locator document.
	 * 
	 * @return The factory model contained in the locator document.
	 */
	private FactoryModel getFactory() {
		Node deployment = getDeployment();
		Node factory = XMLUtility.getChild(deployment, NODE_FACTORY);
		Node instance = XMLUtility.getChild(deployment, NODE_INSTANCE);
		Node skeleton = XMLUtility.getChild(deployment, NODE_SKELETON);
		Node name = XMLUtility.getChild(deployment, NODE_NAME);
		FactoryModel model = new FactoryModel(XMLUtility.getContent(factory));
		model.setInstance(XMLUtility.getContent(instance));
		model.setName(XMLUtility.getContent(name));
		model.setSkeleton(XMLUtility.getContent(skeleton));
		Vector demands = XMLUtility.getChildren(deployment, NODE_INSTANCE_DEMAND);
		for (int i = 0; i < demands.size(); i++) {
			Node demand = (Node)demands.get(i);
			Node proxy = XMLUtility.getChild(demand, NODE_PROXY);
			Node pname = XMLUtility.getChild(demand, NODE_NAME);
			model.addProxy(XMLUtility.getContent(pname), XMLUtility.getContent(proxy));
		}
		return model;
	}
	
	/**
	 * Returns the skeleton model contained in the locator document.
	 * 
	 * @return The skeleton model contained in the locator document.
	 * @throws JavaModelException Thrown if the interface types 
	 * 	could not be resolved.
	 */
	private SkeletonModel getSkeleton() throws JavaModelException {
		Node deployment = getDeployment();
		Node skeleton = XMLUtility.getChild(deployment, NODE_SKELETON);
		Node instance = XMLUtility.getChild(deployment, NODE_INSTANCE);
		Node provision = XMLUtility.getChild(deployment, NODE_INSTANCE_PROVISION);
		Node stateful = XMLUtility.getChild(provision, NODE_STATEFUL);
		SkeletonModel model = new SkeletonModel(XMLUtility.getContent(skeleton));
		model.setStateful(XMLUtility.getContent(stateful).equals(VALUE_TRUE));
		model.setTarget(XMLUtility.getContent(instance));
		Vector ifaces = XMLUtility.getChildren(provision, NODE_INTERFACE);
		for (int i = 0; i < ifaces.size(); i++) {
			Node iface = (Node)ifaces.get(i);
			IType itype = project.findType(XMLUtility.getContent(iface));
			model.addInterface(itype);			
		}
		return model;
	}
	
	/**
	 * Returns the deployment descriptor of the component descriptor
	 * or null if it does not exist.
	 * 
	 * @return The node of the descriptor or null if it does not
	 * 	exist.
	 */
	private Node getDeployment() {
		Node root = document.getDocument().getFirstChild();
		if (root != null) {
			return XMLUtility.getChild(root, NODE_DEPLOYMENT);
		} else {
			return null;
		}
	}

}
