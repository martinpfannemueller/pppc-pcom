package info.pppc.pcom.capability.ir;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Vector;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import info.pppc.base.system.operation.IMonitor;
import info.pppc.base.system.operation.IOperation;
import info.pppc.base.system.operation.NullMonitor;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.system.model.capability.IAllocator;
import info.pppc.pcom.system.model.capability.IAllocatorContext;
import info.pppc.pcom.system.model.capability.IResourceContext;
import info.pppc.pcom.system.model.capability.IResourceSetup;
import info.pppc.pcom.system.model.capability.IResourceTemplate;
import info.pppc.pcom.system.model.contract.reader.IResourceDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IFeatureProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.IResourceProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;
import info.pppc.pcom.utility.LibraryLoader;
import info.pppc.pcomx.contract.SimpleValidator;

/**
 * The ir allocator is a pcom allocator that enables applications to 
 * receive signals of ir beacons over jcl. Using the assignments,
 * elements can for instance ensure that they are in visible range of
 * another device. Since the ir port of windows xp-based pcs is no longer
 * exported as com port, the ir allocator can also be run without binding
 * to a specific com port. In that case, the only function that will be
 * provided is the binding to a certain local id.
 * 
 * @author Mac
 */
public class IRAllocator implements IAllocator, IOperation {

	/**
	 * The name of the ir allocator as shown in the ui.
	 */
	private static final String CAPABILITY_NAME = "IR Beacon";
	
	/**
	 * Sets the timeout for read operations. The thread will read
	 * until the period has expired before it will send its local
	 * id.
	 */
	private static final long READ_PERIOD = 750;
	
	/**
	 * Sets the maximum random wait timeout for which the writer
	 * will wait before it starts to write.
	 */
	private static final int WRITE_PERIOD = 250;
	
	/**
	 * Sets the number of repetitive writes. This is the number
	 * of times which the local id will be repeated.
	 */
	private static final int WRITE_REPEAT = 1;
	
	/**
	 * Sets the number of repetitive reads that are required in
	 * order to consider the incoming data to be an id.
	 */
	private static final int READ_REPEAT = 1;
	
	/**
	 * The port identifier used to bootstrap the allocator.
	 */
	protected String port;
	
	/**
	 * The local id of the allocator.
	 */
	protected short localID;
	
	/**
	 * The remote id that has been heard the last time.
	 */
	protected short remoteID;
	
	/**
	 * The time at which the remote id has been heard the last time.
	 */
	protected long remoteLast;
	
	/**
	 * The first point in time at which the remote id has been heard.
	 */
	protected long remoteFirst;
	
	/**
	 * The context of the allocator after the set context method
	 * has been called.
	 */
	private IAllocatorContext context;
	
	/**
	 * The monitor that the allocator uses during start and stop.
	 */
	private NullMonitor monitor;
	
	/**
	 * The resources that are currently started.
	 */
	private Vector resources = new Vector();
	
	/**
	 * Creates a new ir allocator that has the specified local id.
	 * The ir allocator will not try to send or receive any data.
	 * This constructor can be used on windows xp-based pcs to
	 * mimic at least the local behavior of the ir allocator.
	 * 
	 * @param localID The local id of the ir allocator the value must
	 * 	not be null.
	 */
	public IRAllocator(short localID) {
		this(null, localID);
	}
	
	/**
	 * Creates a new ir allocator that has the specified local id and that sends 
	 * and receives data on the specified port. The string required as port 
	 * description depends heavily on the type of device used. Using JCL 2.0 from 
	 * sun, you can use COMx (e.g. COM1, COM2) as description. Using the windows 
	 * ce-based JCL implementation, you should must use COMx: (e.g. COM1:, COM2:). 
	 * For an IPAQ H5550, the port specification to use is COM2: (at least last
	 * time I checked).
	 * 
	 * @param port The port description to use or null if the allocator should
	 * 	not bind to a port.
	 * @param localID The local id that the device should have the value must not
	 * 	be null.
	 */
	public IRAllocator(String port, short localID) {
		if (localID == 0) throw new IllegalArgumentException("Illegal local id (0).");
		this.port = port;
		this.localID = localID;
	}

	/**
	 * Returns the name of the allocator as shown in the ui.
	 * 
	 * @return The name of the allocator as shown in the ui.
	 */
	public String getName() {
		return CAPABILITY_NAME;
	}

	/**
	 * Called to set the context of the allocator.
	 * 
	 * @param context The context of the allocator.
	 */
	public void setContext(IAllocatorContext context) {
		this.context = context;
	}
	
	/**
	 * Called to unset the context of the allocator.
	 */
	public void unsetContext() {
		context = null;
	}
	
	/**
	 * Called to start the allocator. If the allocator has a port
	 * description, this will start the send/receive operation.
	 */
	public void start() {
		if (port != null) {
			monitor = new NullMonitor();
			context.performOperation(this, monitor);			
		}
	}
	
	/**
	 * Called to stop the allocator. This will stop the
	 * thread created in the start method.
	 */
	public void stop() {
		if (monitor != null) {
			synchronized (monitor) {
				monitor.cancel();
				try {
					while (! monitor.isDone()) {
						monitor.join();	
					}	
				} catch (InterruptedException e) {
					Logging.error(getClass(), "Thread got interrupted.", e);
				}				
			}
		}
	}

	
	/**
	 * Derives the possible setups for the specified demand.
	 * 
	 * @param demand The demand for which a setup should be
	 * 	derived.
	 * @return The setups for the demand.
	 */
	public IResourceSetup[] deriveSetups(IResourceDemandReader demand) {
		// only accept demands with an interface demand towards the com accessor
		if (demand.getInterfaces().length != 1) return null;
		ITypeDemandReader iface = demand.getInterface(IIRAccessor.class.getName());
		if (iface == null) return null;
		// only accept one dimension called ir with local and remote features
		SimpleValidator validator = new SimpleValidator();
		validator.addFeature("IR", "LOCAL", new Integer(localID));
		validator.addFeature("IR", "REMOTE", new Integer(remoteID));
		if (! validator.validate(iface)) return null;
		// create a setup that provides an interface provision of the accessor
		IResourceSetup setup = context.createSetup();
		IResourceProvisionWriter provision = setup.getResource();
		provision.createInterface(IIRAccessor.class.getName());
		ITypeProvisionWriter ipro = provision.getInterface(IIRAccessor.class.getName());
		ipro.createDimension("IR");
		IDimensionProvisionWriter dpro = ipro.getDimension("IR");
		dpro.createFeature("LOCAL", new Integer(localID));
		dpro.createFeature("REMOTE", new Integer(remoteID));
		return new IResourceSetup[] { setup };
	}
	
	/**
	 * Estimates the resource usage of the template. Since the
	 * number of users of this allocator is unlimited, each
	 * template has a total cost of 0.
	 * 
	 * @param template The template that should be estimated.
	 * @return The estimate for the template, always 0.
	 */
	public int[] estimateTemplate(IResourceTemplate template) {
		return new int[] { 0 };
	}
	
	/**
	 * Returns the amount of free resources of the allocator.
	 * Since each assignment does not cost anything the available
	 * resources are also set to 0.
	 * 
	 * @return The free resources of the allocator, i.e. 0.
	 */
	public int[] freeResources() {
		return new int[] { 0 };
	}
	
	/**
	 * Returns the total amount of resources of the allocator.
	 * Since the allocator's resource assignments are free,
	 * the total amount of available resources is 0.
	 * 
	 * @return The total resources of the allocator, i.e. 0.
	 */
	public int[] totalResources() {
		return new int[] { 0 };
	}


	/**
	 * Starts the specified resource assignment. This will setup the accessor 
	 * object. 
	 * 
	 * @param context The context object that needs to be
	 * 	reserved.
	 * @return Always true.
	 */
	public synchronized boolean startResource(IResourceContext context) {
		IResourceTemplate template = context.getTemplate();
		IResourceProvisionWriter resource = template.getResource();
		ITypeProvisionWriter type = resource.getInterface(IIRAccessor.class.getName());
		IDimensionProvisionWriter dimension = type.getDimension("IR");
		IFeatureProvisionWriter local = dimension.getFeature("LOCAL");
		IFeatureProvisionWriter remote = dimension.getFeature("REMOTE");
		if (((Integer)local.getValue()).intValue() != localID ||
				((Integer)remote.getValue()).intValue() != remoteID) {
			return false;
		}
		resources.addElement(context);
		IRAccessor accessor = (IRAccessor)context.getAccessor();
		if (accessor == null) {
			accessor = new IRAccessor(localID);
			context.setAccessor(accessor);
		}
		accessor.update(remoteID, remoteFirst, remoteLast, false);
		return true;
	}
	
	/**
	 * Pauses the specified resource assignment. For this
	 * allocator, this method does nothing.
	 * 
	 * @param context The context to pause.
	 */
	public synchronized void pauseResource(IResourceContext context) { 
		resources.removeElement(context);
	}

	/**
	 * Stops the specified resource assignment. For this
	 * allocator, this method does nothing.
	 * 
	 * @param context The context to stop.
	 */
	public synchronized void stopResource(IResourceContext context) { 
		resources.removeElement(context);
	}
	

	/**
	 * Updates the current remote id to the specified value.
	 * 
	 * @param remoteID The remote id that should be updated.
	 */
	protected synchronized void updateRemoteID(short remoteID) {
		boolean changed = (remoteID != this.remoteID);
		long time = System.currentTimeMillis();
		this.remoteID = remoteID;	
		remoteLast = time;
		if (changed) {
			remoteFirst = time;
		} 
		for (int i = 0; i < resources.size(); i++) {
			// get resource context and accessor
			IResourceContext context = (IResourceContext)resources.elementAt(i);
			IRAccessor accessor = null;
			synchronized (context) {
				accessor = (IRAccessor)context.getAccessor();				
			}
			// update accessor
			accessor.update(remoteID, remoteFirst, remoteLast, true);
			// update contract if neccessary
			if (changed) {
				IResourceTemplate template = context.getTemplate();
				IResourceProvisionWriter resource = template.getResource();
				ITypeProvisionWriter type = resource.getInterface(IIRAccessor.class.getName());
				IDimensionProvisionWriter dimension = type.getDimension("IR");
				dimension.createFeature("REMOTE", new Integer(remoteID));
				template.commitTemplate();
			}  
		}
	}
	
	
	/**
	 * This method runs the thread that executes the commands issued through 
	 * the resource assignments.
	 * 
	 * @param monitor The monitor that is used to cancel the operation.
	 * @throws Exception Thrown if an exception occurs while reading and
	 * 	sending ids.
	 */
	public void perform(IMonitor monitor) throws Exception {
		// load the library for jacob
		String library;
		switch (LibraryLoader.getOS()) {
			case LibraryLoader.OS_WINDOWS_X64:
				library = "rxtxSerial-x64";
				break;
			case LibraryLoader.OS_WINDOWS_X86:
				library = "rxtxSerial-x86";
				break;
			default:
				library = null;
		}
		if (library != null) {
			LibraryLoader.load(library, false);
		}
		Random random = new Random();
		CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(port);
		SerialPort serial = (SerialPort)cpi.open(getClass().getName(), 0);
		serial.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		serial.enableReceiveTimeout(250);
		serial.setInputBufferSize(4 * (READ_REPEAT + 1));
		serial.setOutputBufferSize(4 * (READ_REPEAT + 1));
		InputStream input = serial.getInputStream();
		OutputStream output = serial.getOutputStream();
		byte[] read = new byte[4 * READ_REPEAT];
		byte[] copy = new byte[4 * READ_REPEAT];
		operation: while (! monitor.isCanceled()) {
			// read data for max 2 seconds, check if read_repeat times id is ok
			long start = System.currentTimeMillis();
			long time = System.currentTimeMillis();
			int readPosition = 0;
			input.skip(input.available());
			detect: while (time - start < READ_PERIOD) {
				int result = input.read();
				if (result == -1) {
					readPosition = 0;
				} else {
					read[readPosition] = (byte)result;
					readPosition += 1;
					if (readPosition == read.length) {
						if (read[0] == (byte)(~read[2]) && read[1] == (byte)(~read[3])) {
							boolean equal = true;
							compare: for (int i = 1; i < READ_REPEAT - 1; i++) {
								if (! (read[0] == read[4 * i] &&
									read[1] == read[4 * i + 1] &&
									read[2] == read[4 * i + 2] &&
									read[3] == read[4 * i + 3])) {
									equal = false;
									break compare;
								}
							}
							if (equal) {
								updateRemoteID((short)((read[0] & 0xFF) << 8 | (read[1] & 0xFF)));
								synchronized (monitor) {
									// wait some time to reduce the sending speed
									time = System.currentTimeMillis();
									if (time - start < READ_PERIOD) {
										monitor.wait(READ_PERIOD - (time - start));
									}
									break detect;										
								}
							} 
						}
						System.arraycopy(read, 1, copy, 0, read.length - 1);
						byte[] temp = read;
						read = copy;
						copy = temp;
						readPosition -= 1;
					}				
				}
				time = System.currentTimeMillis();	
			}
			// wait a randomized time
			synchronized (monitor) {
				if (monitor.isCanceled()) break operation;
				monitor.wait(random.nextInt(WRITE_PERIOD) + 1);
			}
			// write data n times 			
			byte[] write = new byte[4];
			write[0] = (byte)(localID >> 8);
			write[1] = (byte)(localID);
			write[2] = (byte)(~write[0]);
			write[3] = (byte)(~write[1]);
			for (int i = 0; i < WRITE_REPEAT; i++) {
				output.write(write);
			}
		}
		input.close();
		output.close();
		serial.close();
	}
	
}
