package info.pppc.pcom.swtui.container;

import info.pppc.base.swtui.element.IElementManager;
import info.pppc.base.swtui.tree.TreeNode;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ReferenceID;
import info.pppc.base.system.SystemID;
import info.pppc.base.system.util.Logging;
import info.pppc.pcom.swtui.PcomUI;
import info.pppc.pcom.swtui.tree.DirtyTreeNode;
import info.pppc.pcom.swtui.tree.ICleaner;
import info.pppc.pcom.system.container.ContainerProxy;
import info.pppc.pcom.system.container.IContainer;
import info.pppc.pcom.system.contract.Contract;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * The container model provider generates the model for the container browser.
 * 
 * @author Mac
 */
public class ContainerModelProvider implements IContainerModel, ICleaner {

	/**
	 * The resource identifier of the container browser label.
	 */
	private static final String UI_REFRESH = "info.pppc.pcom.swtui.container.ContainerModelProvider.REFRESH";
	
	/**
	 * The system of this model provider
	 */
	private SystemID systemID;

	/**
	 * The manager used to show a progress bar whenever nodes are cleaned.
	 */
	private IElementManager manager;
	
	/**
	 * Creates a new container model provider for the specified system using
	 * the specified manager.
	 * 
	 * @param manager The manager used to show a progress bar when remote
	 * 	calls are made.
	 * @param systemID The system that hosts the container.
	 */
	public ContainerModelProvider(IElementManager manager, SystemID systemID) {
		this.systemID = systemID;
		this.manager = manager;
	}

	/**
	 * Returns a newly created container model.
	 * 
	 * @return A newly created container model.
	 */
	public TreeNode getModel() {
		TreeNode root = new TreeNode(TYPE_NULL, systemID);
		DirtyTreeNode factories = new DirtyTreeNode(TYPE_FACTORIES);
		factories.dirty();
		root.addChild(factories);
		DirtyTreeNode allocators = new DirtyTreeNode(TYPE_ALLOCATORS);
		allocators.dirty();
		root.addChild(allocators);
		return root;
	}

	/**
	 * Cleans the tree node by retrieving the desired elements from the
	 * container.
	 * 
	 * @param node The tree node that should be cleaned.
	 */
	public void clean(final DirtyTreeNode node) {
		try {
			manager.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(PcomUI.getText(UI_REFRESH) + "...", 2);
					monitor.worked(1);
					node.removeAllChildren();
					ContainerProxy proxy = new ContainerProxy();
					proxy.setSourceID(new ReferenceID(SystemID.SYSTEM));
					proxy.setTargetID(new ReferenceID(systemID, IContainer.CONTAINER_ID));
					try {
						switch (node.getType()) {
						case TYPE_FACTORIES:
							retrieveFactories(proxy, node);
							break;
						case TYPE_ALLOCATORS:
							retrieveAllocators(proxy, node);
							break;
						case TYPE_INSTANCES:
							retrieveInstances(proxy, node);
							break;
						case TYPE_RESOURCES:
							retrieveResources(proxy, node);
							break;
						default:
							// will never happen, all other nodes are not dirty
						}			
					} catch (InvocationException e) {
						node.addChild(new TreeNode(TYPE_EXCEPTION, e));
					}
					monitor.worked(1);
					monitor.done();
				}
			}, false);
		} catch (Throwable t) {
			Logging.error(getClass(), "Exception while refreshing view.", t);
		}
	}
	
	/**
	 * Retrieves the factories from the container and adds them to the node.
	 * 
	 * @param container The container used for retrieval.
	 * @param node The node used to add the results.
	 * @throws InvocationException Thrown if the call fails.
	 */
	private void retrieveFactories(IContainer container, DirtyTreeNode node) throws InvocationException {
		Vector result = container.getFactoriesUI();
		for (int i = 0; i < result.size(); i++) {
			Object[] factory = (Object[])result.elementAt(i);
			ObjectID id = (ObjectID)factory[0];
			String name = (String)factory[1];
			Contract template = (Contract)factory[2];
			Contract status = (Contract)factory[3];
			TreeNode fnode = new TreeNode(TYPE_FACTORY);
			node.addChild(fnode);
			TreeNode fid = new TreeNode(TYPE_FACTORY_ID, id);
			fnode.addChild(fid);
			TreeNode fname = new TreeNode(TYPE_FACTORY_NAME, name);
			fnode.addChild(fname);
			TreeNode ftemplate = new TreeNode(TYPE_FACTORY_TEMPLATE, template);
			buildContract(template, ftemplate);
			fnode.addChild(ftemplate);
			TreeNode fstatus = new TreeNode(TYPE_FACTORY_STATUS, status);
			buildContract(status, fstatus);
			fnode.addChild(fstatus);
			DirtyTreeNode finstances = new DirtyTreeNode(TYPE_INSTANCES, id);
			finstances.dirty();
			fnode.addChild(finstances);
		}
	}

	/**
	 * Retrieves the allocators from the container and adds them to the node.
	 * 
	 * @param container The container used for retrieval.
	 * @param node The node used to add the results.
	 * @throws InvocationException Thrown if the call fails.
	 */
	private void retrieveAllocators(IContainer container, DirtyTreeNode node) throws InvocationException {
		Vector result = container.getAllocatorsUI();
		for (int i = 0; i < result.size(); i++) {
			Object[] allocator = (Object[])result.elementAt(i);
			ObjectID id = (ObjectID)allocator[0];
			String name = (String)allocator[1];
			int[] total = (int[])allocator[2];
			int[] free = (int[])allocator[3];
			Contract template = (Contract)allocator[4];
			Contract status = (Contract)allocator[5];
			TreeNode anode = new TreeNode(TYPE_ALLOCATOR);
			node.addChild(anode);
			TreeNode aid = new TreeNode(TYPE_ALLOCATOR_ID, id);
			anode.addChild(aid);
			TreeNode aname = new TreeNode(TYPE_ALLOCATOR_NAME, name);
			anode.addChild(aname);
			TreeNode atotal = new TreeNode(TYPE_ALLOCATOR_TOTAL, total);
			anode.addChild(atotal);
			TreeNode afree = new TreeNode(TYPE_ALLOCATOR_FREE, free);
			anode.addChild(afree);
			TreeNode atemplate = new TreeNode(TYPE_ALLOCATOR_TEMPLATE, template);
			buildContract(template, atemplate);
			anode.addChild(atemplate);
			TreeNode astatus = new TreeNode(TYPE_ALLOCATOR_STATUS, status);
			buildContract(status, astatus);
			anode.addChild(astatus);
			DirtyTreeNode aresources = new DirtyTreeNode(TYPE_RESOURCES, id);
			aresources.dirty();
			anode.addChild(aresources);
		}		
	}
	
	/**
	 * Retrieves the instances for a factory from the container and adds them to the node.
	 * 
	 * @param container The container used for retrieval.
	 * @param node The node used to add the results.
	 * @throws InvocationException Thrown if the call fails.
	 */
	private void retrieveInstances(IContainer container, DirtyTreeNode node) throws InvocationException {
		ObjectID fid = (ObjectID)node.getData();
		Vector results = container.getInstancesUI(fid);
		for (int i = 0; i < results.size(); i++) {
			Object[] instance = (Object[])results.elementAt(i);
			ObjectID id = (ObjectID)instance[0];
			String name = (String)instance[1];
			Contract template = (Contract)instance[2];
			Contract status = (Contract)instance[3];
			TreeNode inode = new TreeNode(TYPE_INSTANCE);
			node.addChild(inode);
			TreeNode iid = new TreeNode(TYPE_INSTANCE_ID, id);
			inode.addChild(iid);
			TreeNode iname = new TreeNode(TYPE_INSTANCE_NAME, name);
			inode.addChild(iname);
			TreeNode itemplate = new TreeNode(TYPE_INSTANCE_TEMPLATE, template);
			buildContract(template, itemplate);
			inode.addChild(itemplate);
			TreeNode istatus = new TreeNode(TYPE_INSTANCE_STATUS, status);
			buildContract(status, istatus);
			inode.addChild(istatus);
		}
	}

	/**
	 * Retrieves the resources for an allocator from the container and adds them to the node.
	 * 
	 * @param container The container used for retrieval.
	 * @param node The node used to add the results.
	 * @throws InvocationException Thrown if the call fails.
	 */
	private void retrieveResources(IContainer container, DirtyTreeNode node) throws InvocationException {
		ObjectID aid = (ObjectID)node.getData();
		Vector results = container.getResourcesUI(aid);
		for (int i = 0; i < results.size(); i++) {
			Object[] resource = (Object[])results.elementAt(i);
			ObjectID id = (ObjectID)resource[0];
			String name = (String)resource[1];
			int[] used = (int[])resource[2];
			Contract template = (Contract)resource[3];
			Contract status = (Contract)resource[4];
			TreeNode rnode = new TreeNode(TYPE_RESOURCE);
			node.addChild(rnode);
			TreeNode rid = new TreeNode(TYPE_RESOURCE_ID, id);
			rnode.addChild(rid);
			TreeNode rname = new TreeNode(TYPE_RESOURCE_NAME, name);
			rnode.addChild(rname);
			TreeNode ruse = new TreeNode(TYPE_RESOURCE_USE, used);
			rnode.addChild(ruse);
			TreeNode rtemplate = new TreeNode(TYPE_RESOURCE_TEMPLATE, template);
			buildContract(template, rtemplate);
			rnode.addChild(rtemplate);
			TreeNode rstatus = new TreeNode(TYPE_RESOURCE_STATUS, status);
			buildContract(status, rstatus);
			rnode.addChild(rstatus);			
		}
	}
	
	/**
	 * Creates the subtree structure for the specified contract and appends it 
	 * to the specified node.
	 * 
	 * @param contract The contract that needs to be attached.
	 * @param node The node to which the contract will be attached. 
	 */
	private void buildContract(Contract contract, TreeNode node) {
		byte[] attributes = contract.getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			TreeNode anode = new TreeNode
				(TYPE_CONTRACT_ATTRIBUTE, new Byte(attributes[i]));
			node.addChild(anode);
		}
		Contract[] contracts = contract.getContracts();
		for (int i = 0; i < contracts.length; i++) {
			TreeNode cnode = new TreeNode(TYPE_CONTRACT, contracts[i]);
			buildContract(contracts[i], cnode);
			node.addChild(cnode);
		}
	}

}
