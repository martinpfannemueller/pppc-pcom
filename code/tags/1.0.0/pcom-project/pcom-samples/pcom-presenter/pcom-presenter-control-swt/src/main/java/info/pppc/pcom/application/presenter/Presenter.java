/*
 * Revision: $Revision: 1.4 $
 * Author:   $Author: handtems $
 * Date:     $Date: 2007/08/29 14:03:26 $ 
 */

package info.pppc.pcom.application.presenter;

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
import info.pppc.pcom.capability.ir.IRAllocator;
import info.pppc.pcom.capability.swtui.SwtAllocator;
import info.pppc.pcom.component.presenter.IPresenter;
import info.pppc.pcom.component.presenter.PresenterFactory;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.system.application.ApplicationDescriptor;
import info.pppc.pcom.system.application.ApplicationManager;
import info.pppc.pcom.system.container.Container;
import info.pppc.pcom.system.contract.Contract;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcomx.assembler.gc.GCAssembler;
import info.pppc.pcomx.assembler.gd.GDAssembler;
import info.pppc.pcomx.container.FixedContainerStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * The bootstrapping code for the pcom presenter that is executed
 * on an ipaq. The local id of the ipaq will be 1.
 * 
 * @author Mac
 */
public class Presenter {

	/**
	 * Starts the pcom presenter with algorithms and user interface.
	 * 
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		Application application = Application.getInstance();
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

		// add another assembler
		info.pppc.pcomx.assembler.gd.swtui.GDAssemblerUI.registerAssemblerBrowser();
		GDAssembler.getInstance();

		// start a pcom assembler service
		GCAssembler.getInstance();
		ServiceRegistry.getInstance();
		ReferenceID assemblerID = new ReferenceID(SystemID.SYSTEM, GCAssembler.ASSEMBLER_ID);
		// start the pcom container service
		Container container = Container.getInstance();
		FixedContainerStrategy strategy = new FixedContainerStrategy(assemblerID);
		container.setStrategy(strategy);
		// start the application manager
		ApplicationManager manager = ApplicationManager.getInstance();
		// install factories and capabilities
		IRAllocator ir = new IRAllocator((short)1);
		container.addAllocator(ir);
		SwtAllocator ui = new SwtAllocator(application);
		container.addAllocator(ui);
		PresenterFactory pf = new PresenterFactory();
		container.addFactory(pf);
		// create the instance template and preferences
		ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
		applicationDescriptor.setAssemblerID(assemblerID);
		applicationDescriptor.setName("3PC Pervasive Presenter");
		Vector preferences = new Vector();
		// create a contract that will use the ir constraint
		Contract constraint = new Contract(Contract.TYPE_INSTANCE_TEMPLATE, "IR Constraint");
		Contract constraintInstance = new Contract(Contract.TYPE_INSTANCE_DEMAND, "Presenter");
		Contract constraintType = new Contract(Contract.TYPE_INTERFACE_DEMAND, IPresenter.class.getName());
		Contract constraintDimension = new Contract(Contract.TYPE_DIMENSION_DEMAND, "LOCATION");
		Contract constraintFeature = new Contract(Contract.TYPE_FEATURE_DEMAND, "CONSTRAINT");
		constraintFeature.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(IContract.IFEQ));
		constraintFeature.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, new Boolean(true));
		constraint.addContract(constraintInstance);
		constraintInstance.addContract(constraintType);
		constraintType.addContract(constraintDimension);
		constraintDimension.addContract(constraintFeature);
		// create a contract that will use the portrayer
		Contract portrayer = new Contract(Contract.TYPE_INSTANCE_TEMPLATE, "Portrayer (2)");
		Contract portrayerInstance = new Contract(Contract.TYPE_INSTANCE_DEMAND, "Presenter");
		Contract portrayerType = new Contract(Contract.TYPE_INTERFACE_DEMAND, IPresenter.class.getName());
		Contract portrayerDimension = new Contract(Contract.TYPE_DIMENSION_DEMAND, "LOCATION");
		Contract portrayerFeature = new Contract(Contract.TYPE_FEATURE_DEMAND, "POWERPOINT");
		portrayerFeature.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(IContract.IFEQ));
		portrayerFeature.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, new Integer(2));
		portrayer.addContract(portrayerInstance);
		portrayerInstance.addContract(portrayerType);
		portrayerType.addContract(portrayerDimension);
		portrayerDimension.addContract(portrayerFeature);
		// create a contract that will use the displayer
		Contract displayer = new Contract(Contract.TYPE_INSTANCE_TEMPLATE, "Displayer (3)");
		Contract displayerInstance = new Contract(Contract.TYPE_INSTANCE_DEMAND, "Presenter");
		Contract displayerType = new Contract(Contract.TYPE_INTERFACE_DEMAND, IPresenter.class.getName());
		Contract displayerDimension = new Contract(Contract.TYPE_DIMENSION_DEMAND, "LOCATION");
		Contract displayerFeature = new Contract(Contract.TYPE_FEATURE_DEMAND, "POWERPOINT");
		displayerFeature.setAttribute(Contract.ATTRIBUTE_FEATURE_COMPARATOR, new Integer(IContract.IFEQ));
		displayerFeature.setAttribute(Contract.ATTRIBUTE_FEATURE_VALUE, new Integer(3));
		displayer.addContract(displayerInstance);
		displayerInstance.addContract(displayerType);
		displayerType.addContract(displayerDimension);
		displayerDimension.addContract(displayerFeature);
		// create a contract that will use the any output
		Contract any = new Contract(Contract.TYPE_INSTANCE_TEMPLATE, "Any");
		Contract anyInstance = new Contract(Contract.TYPE_INSTANCE_DEMAND, "Presenter");
		Contract anyType = new Contract(Contract.TYPE_INTERFACE_DEMAND, IPresenter.class.getName());
		any.addContract(anyInstance);
		anyInstance.addContract(anyType);
		// read the presenter image
		try {
			InputStream stream = Presenter.class.getResourceAsStream("presenter.png");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = stream.read(buffer);
			while (read != -1) {
				bos.write(buffer, 0, read);
				read = stream.read(buffer);
			}
			applicationDescriptor.setImage(bos.toByteArray());
		} catch (IOException e) {
			applicationDescriptor.setImage(null);
			e.printStackTrace();
		}
		// finalize the application descriptor
		preferences.addElement(constraint);
		preferences.addElement(portrayer);
		preferences.addElement(displayer);
		preferences.addElement(any);
		applicationDescriptor.setPreferences(preferences);
		manager.addApplication(applicationDescriptor);
	}
}
