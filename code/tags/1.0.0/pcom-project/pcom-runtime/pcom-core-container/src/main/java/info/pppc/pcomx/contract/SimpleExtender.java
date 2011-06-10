package info.pppc.pcomx.contract;

import java.util.Vector;

import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;

/**
 * The simple extender can be used to automatically generate dynamic
 * attributes for dimensions in a provision according to the specified
 * demand.
 * 
 * @author Mac
 */
public class SimpleExtender {

	/**
	 * The dimensions and features that will be extended by the extender.
	 */
	private Vector extensions = new Vector();
	
	/**
	 * Creates a new simple extender without any extensions.
	 */
	public SimpleExtender() {
		super();
	}
	
	/**
	 * Adds a dimension to the extender whose features will be enriched
	 * with dynamic attributes according to the specification contained
	 * in the demand.
	 * 
	 * @param dimension The dimension to extend with dynamic attributes.
	 */
	public void addDimension(String dimension) {
		extensions.addElement(new String[] { dimension });
	}
	
	/**
	 * Removes the specified dimension from the set of dimensions
	 * to extend.
	 * 
	 * @param dimension The dimension to remove.
	 * @return True if the dimension has been removed, false if it
	 * 	was not present in the first place.
	 */
	public boolean removeDimension(String dimension) {
		return remove(new String[] { dimension });
	}
	
	/**
	 * Adds the specified feature to the set of features that will
	 * be extended.
	 * 
	 * @param dimension The dimension name of the feature.
	 * @param feature The feature name.
	 */
	public void addFeature(String dimension, String feature) {
		extensions.addElement(new String[] { dimension, feature});
	}
	
	/**
	 * Removes a previously added feature from the set of features
	 * that will be extended.
	 * 
	 * @param dimension The dimension name of the feature.
	 * @param feature The name of the feature.
	 * @return True if the feature has been removed, false if the
	 * 	feature was not present.
	 */
	public boolean removeFeature(String dimension, String feature) {
		return remove(new String[] { dimension, feature });
	}
	
	/**
	 * Remove a certain extension and return whether the removal
	 * has happened.
	 * 
	 * @param extension The extension to remove.
	 * @return True if the extension has been removed, false otherwise.
	 */
	private boolean remove(String[] extension) {
		extension: for (int i = 0; i < extensions.size(); i++) {
			String[] e = (String[])extensions.elementAt(i);
			if (e.length == extension.length) {
				for (int j = 0; j < e.length; j++) {
					if (! e[j].equals(extension[j])) {
						continue extension;
					}
				}
				extensions.removeElementAt(i);
				return true;
			}
		}
		return false;
	}


	
	/**
	 * Creates the dynamic attributes within the target that are neccessary
	 * to fulfill the source.
	 * 
	 * @param source The source that specifies the demand.
	 * @param target The target that will receive the dynamic attributes.
	 */
	public void extend(ITypeDemandReader source, ITypeProvisionWriter target) {
		for (int i = 0; i < extensions.size(); i++) {
			String[] extension = (String[])extensions.elementAt(i);
			String name = extension[0];
			// retrieve source dimension
			IDimensionDemandReader sourceDimension = source.getDimension(name);
			if (sourceDimension != null) {
				// get or create target dimension
				IDimensionProvisionWriter targetDimension = target.getDimension(name);
				if (targetDimension == null) {
					target.createDimension(name);
					targetDimension = target.getDimension(name);
				} 
				// see whether we copy the whole target or a single feature
				if (extension.length == 1) {
					// copy the whole dimension
					IFeatureDemandReader[] features = sourceDimension.getFeatures();
					for (int j = 0; j < features.length; j++) {
						String fname = features[j].getName();
						if (targetDimension.getFeature(fname) == null) {
							targetDimension.createFeature(fname);
						}
					}
				} else if (extension.length == 2){
					// create only a single feature
					String fname = extension[1];
					IFeatureDemandReader feature = sourceDimension.getFeature(fname);
					if (feature != null && targetDimension.getFeature(fname) == null) {
						targetDimension.createFeature(fname);
					}
				}
			}	
		}
	}
	
}
