package info.pppc.pcom.application.presenter;

import info.pppc.base.swtui.Application;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.PluginManager;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.util.Logging;
import info.pppc.basex.plugin.discovery.ProactiveDiscovery;
import info.pppc.basex.plugin.semantic.RmiSemantic;
import info.pppc.basex.plugin.serializer.ObjectSerializer;
import info.pppc.basex.plugin.transceiver.MxBluetoothTransceiver;
import info.pppc.basex.plugin.transceiver.MxIPMulticastTransceiver;
import info.pppc.basex.plugin.transceiver.bt.TimedDiscoveryStrategy;
import info.pppc.pcom.capability.com.COMAllocator;
import info.pppc.pcom.capability.ir.IRAllocator;
import info.pppc.pcom.capability.swtui.SwtAllocator;
import info.pppc.pcom.component.filesystem.FilesystemFactory;
import info.pppc.pcom.component.powerpoint.converter.ConverterFactory;
import info.pppc.pcom.component.powerpoint.displayer.DisplayerFactory;
import info.pppc.pcom.component.powerpoint.forwarder.ForwarderFactory;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.application.ApplicationManager;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcomx.assembler.gc.GCAssembler;
import info.pppc.pcomx.assembler.gc.swtui.GCAssemblerUI;
import info.pppc.pcomx.assembler.gd.GDAssembler;
import info.pppc.pcomx.container.FixedContainerStrategy;

/**
 * The bootstrapping code for the pcom filesystem/portrayer/forwarder/
 * displayer/converter that is executed on a windows pc. The local id 
 * of the ipaq will be 3.
 * 
 * @author Mac
 */
public class Displayer {

	/**
	 * Starts the pcom filesystem/portrayer/forwarder/
     * displayer/converter with algorithms and user interface.
	 * 
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		// create the ui and register the ui components for the device
		Application application = Application.getInstance();
		PcomUI.registerSystemBrowser();
		// register the assembler ui for the gc assembler
		GCAssemblerUI.registerAssemblerBrowser();
		// set logging verbosity
		Logging.setVerbosity(Logging.MAXIMUM_VERBOSITY);
		// add default set of base plugins
		InvocationBroker b = InvocationBroker.getInstance();
		PluginManager m = b.getPluginManager();
		m.addPlugin(new MxIPMulticastTransceiver());
		m.addPlugin(new MxBluetoothTransceiver(new TimedDiscoveryStrategy(20000), false));
		m.addPlugin(new ProactiveDiscovery());
		m.addPlugin(new RmiSemantic());
		m.addPlugin(new ObjectSerializer());
		// add another assembler
		info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI.registerAssemblerBrowser();
		GDAssembler.getInstance();
		// start a pcom assembler service
		GCAssembler.getInstance();
		ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM, GCAssembler.ASSEMBLER_ID);
		// start the pcom container service
		Container container = Container.getInstance();
		FixedContainerStrategy strategy = new FixedContainerStrategy(assemblerID);
		container.setStrategy(strategy);
		// start the application manager
		ApplicationManager.getInstance();
		// install test factories and capabilities
		COMAllocator com = new COMAllocator();
		container.addAllocator(com);
		IRAllocator ir = new IRAllocator((short)3);
		container.addAllocator(ir);
		SwtAllocator ui = new SwtAllocator(application);
		container.addAllocator(ui);
		FilesystemFactory fs = new FilesystemFactory();
		container.addFactory(fs);
		DisplayerFactory dp = new DisplayerFactory();
		container.addFactory(dp);
		ForwarderFactory fw = new ForwarderFactory();
		container.addFactory(fw);
		ConverterFactory cv = new ConverterFactory();
		container.addFactory(cv);
	}

}
