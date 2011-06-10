package info.pppc.pcom.tutorial.tree;

import info.pppc.base.service.ServiceRegistry;
import info.pppc.base.swtui.Application;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.PluginManager;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.util.Logging;
import info.pppc.basex.plugin.discovery.ProactiveDiscovery;
import info.pppc.basex.plugin.semantic.RmiSemantic;
import info.pppc.basex.plugin.serializer.ObjectSerializer;
import info.pppc.basex.plugin.transceiver.MxIPMulticastTransceiver;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.application.ApplicationManager;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcomx.assembler.gc.GCAssembler;
import info.pppc.pcomx.assembler.gd.GDAssembler;
import info.pppc.pcomx.container.FixedContainerStrategy;

import java.util.Vector;

/**
 * This class contains the bootstrap code for a simple pcom hello world
 * application.
 * 
 * @author Mac
 */
public class TreeMain {

	/**
	 * Starts base/pcom and executes the hello world application on the container.
	 * 
	 * @param args The command line arguments. Ignored at the moment.
	 */
	public static void main(String[] args) {
		// start the pcom user interface
		Application.getInstance();
		PcomUI.registerSystemBrowser();
		PcomUI.registerApplicationBrowser();
		// set logging verbosity
		Logging.setVerbosity(Logging.MAXIMUM_VERBOSITY);
		// add default set of base plugins
		InvocationBroker b = InvocationBroker.getInstance();
		PluginManager m = b.getPluginManager();
		m.addPlugin(new MxIPMulticastTransceiver());
		m.addPlugin(new ProactiveDiscovery());
		m.addPlugin(new RmiSemantic());
		m.addPlugin(new ObjectSerializer());
		// start a pcom assembler service
		GCAssembler.getInstance();
		GDAssembler.getInstance();
		ServiceRegistry.getInstance();
		ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM, GDAssembler.ASSEMBLER_ID);
		// start the pcom container service
		Container container = Container.getInstance();
		FixedContainerStrategy strategy = new FixedContainerStrategy(assemblerID);
		container.setStrategy(strategy);
		// start the application manager
		ApplicationManager manager = ApplicationManager.getInstance();
		// install a factory into the container
		container.addFactory(new TreeFactory(true));
		container.addAllocator(new TreeAllocator(2));
		// start the pcom application manager service
		ApplicationDescriptor descriptor = new ApplicationDescriptor();
		descriptor.setAssemblerID(assemblerID);
		descriptor.setImage(null);
		descriptor.setName("Tree Application");
		descriptor.setPreferences(createPreferences(1, 1));
		manager.addApplication(descriptor);
		
	}
	
	
	/**
	 * Creates a new tree preference that will lead to a tree with the specified
	 * height and width.
	 * 
	 * @param width The width of the tree. This must be larger than 0.
	 * @param height The height of the tree. This must be larger than 0.
	 * @return The vector with the preference to create a tree with the specified
	 * 	width and height.
	 */
	private static Vector createPreferences(int width, int height) {
		if (width < 0 || height < 0) throw new IllegalArgumentException("Width and height must be postive.");
		Contract preference = new Contract(Contract.TYPE_INSTANCE_TEMPLATE, "<" + 0 + ", " + height + ">");
		for (int i = 0; i < width; i++) {
			// create instance demand
			String name = "<" + i + ", " + (height - 1) + ">";
			Contract instanceDemand = new Contract(Contract.TYPE_INSTANCE_DEMAND, name);
			preference.addContract(instanceDemand);
			// set interface
			Contract typeDemand = new Contract(Contract.TYPE_INTERFACE_DEMAND, ITree.class.getName());
			instanceDemand.addContract(typeDemand);
			// set width
			Contract widthDemand = new Contract(Contract.TYPE_DIMENSION_DEMAND, "WIDTH");
			typeDemand.addContract(widthDemand);
			Contract widthValue = new Contract(Contract.TYPE_FEATURE_DEMAND, "VALUE");
			widthValue.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(IContract.IFEQ));
			widthValue.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, new Integer(width));
			widthDemand.addContract(widthValue);
			// set height
			Contract heightDemand = new Contract(Contract.TYPE_DIMENSION_DEMAND, "HEIGHT");
			typeDemand.addContract(heightDemand);
			Contract heightValue = new Contract(Contract.TYPE_FEATURE_DEMAND, "VALUE");
			heightValue.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(IContract.IFEQ));
			heightValue.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, new Integer(height));
			heightDemand.addContract(heightValue);
		}
		Vector preferences = new Vector();
		preferences.addElement(preference);
		return preferences;
	}
	
}
