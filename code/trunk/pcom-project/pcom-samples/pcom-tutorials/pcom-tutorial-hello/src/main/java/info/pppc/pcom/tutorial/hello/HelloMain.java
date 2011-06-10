package info.pppc.pcom.tutorial.hello;

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
import info.pppc.pcom.system.container.Container;
import info.pppc.pcomx.assembler.gc.GCAssembler;
import info.pppc.pcomx.container.FixedContainerStrategy;

/**
 * This class contains the bootstrap code for a simple pcom hello world
 * application.
 * 
 * @author Mac
 */
public class HelloMain {

	/**
	 * Starts base/pcom and executes the hello world application on the
	 * container.
	 * 
	 * @param args
	 *            The command line arguments. Ignored at the moment.
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
		ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM,
				GCAssembler.ASSEMBLER_ID);
		// start the pcom container service
		Container container = Container.getInstance();
		FixedContainerStrategy strategy = new FixedContainerStrategy(
				assemblerID);
		container.setStrategy(strategy);
		// install a factory into the container
		container.addFactory(new HelloFactory());
	}
}
