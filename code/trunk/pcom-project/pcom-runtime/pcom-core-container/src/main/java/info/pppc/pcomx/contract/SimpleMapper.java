package info.pppc.pcomx.contract;

import java.util.Vector;

import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IDimensionProvisionReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureDemandReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureProvisionReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeProvisionReader;
import info.pppc.pcom.system.model.contract.writer.IDimensionDemandWriter;
import info.pppc.pcom.system.model.contract.writer.IDimensionProvisionWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeDemandWriter;
import info.pppc.pcom.system.model.contract.writer.ITypeProvisionWriter;

/**
 * The mapper is a pcom extension that enables users to map dimensions
 * and features from one demand to another or from one provision to
 * another.
 * 
 * @author Mac
 */
public class SimpleMapper {

	/**
	 * The mappings that have been defined for the mapper.
	 */
	private Vector mappings = new Vector();
	
	/**
	 * Creates a new demand mapper without any mapping information.
	 */
	public SimpleMapper() {
		super();
	}
	
	/**
	 * Adds a dimension mapping that maps the dimension and all features of
	 * the dimension to the specified target dimension.
	 * 
	 * @param sourceDimension The source dimension name.
	 * @param targetDimension The target dimension name.
	 */
	public void addDimension(String sourceDimension, String targetDimension) {
		String[] mapping = new String[] { sourceDimension, targetDimension };
		mappings.addElement(mapping);
	}
	
	/**
	 * Removes a dimension mapping that has been specified using the add
	 * dimension mapping method.
	 * 
	 * @param sourceDimension The source dimension name.
	 * @param targetDimension The target dimension name.
	 * @return True if the dimension has been found, false otherwise.
	 */
	public boolean removeDimension(String sourceDimension, String targetDimension) {
		return remove(new String[] { sourceDimension, targetDimension });
	}
	
	/**
	 * Adds a feature mapping from one feature to another to the set of
	 * mappings contained in the mapper.
	 * 
	 * @param sourceDimension The source dimension.
	 * @param sourceFeature The source feature within the source dimension.
	 * @param targetDimension The target dimension.
	 * @param targetFeature The target feature within the target dimension.
	 */
	public void addFeature(String sourceDimension, String sourceFeature, String targetDimension, String targetFeature) {
		String[] mapping = new String[] { sourceDimension, sourceFeature, targetDimension, targetFeature };
		mappings.addElement(mapping);
	}
	
	/**
	 * Removes a previously added mapping from one feature to another from
	 * the set of mappings container in the mapper.
	 * 
	 * @param sourceDimension The source dimension.
	 * @param sourceFeature The source feature within the source dimension.
	 * @param targetDimension The target dimension.
	 * @param targetFeature The target feature within the target dimension.
	 * @return True if the mapping has been found and removed, false otherwise.
	 */
	public boolean removeFeature(String sourceDimension, String sourceFeature, String targetDimension, String targetFeature) {
		return remove(new String[] { sourceDimension, sourceFeature, targetDimension, targetFeature });
	}
	
	/**
	 * Removes the specified mapping from the set of mappings.
	 * 
	 * @param mapping The mappings to remove.
	 * @return True if the mappings has been removed, false otherwise.
	 */
	private boolean remove(String[] mapping) {
		rules: for (int i = 0; i < mappings.size(); i++) {
			String[] m = (String[])mappings.elementAt(i);
			if (m.length == mapping.length) {
				for (int j = 0; j < m.length; j++) {
					if (! m[j].equals(mapping[j])) {
						continue rules;
					}
				}
				mappings.removeElementAt(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Maps all mappings contained in the mapper from the source to the
	 * target. If the source or the target is null, this method will
	 * simply return.
	 * 
	 * @param source The source contract that provides the input.
	 * @param target The target contract that will be adjusted.
	 */
	public void map(ITypeDemandReader source, ITypeDemandWriter target) {
		if (source == null || target == null) return;
		for (int i = 0; i < mappings.size(); i++) {
			String[] mapping = (String[])mappings.elementAt(i);
			if (mapping.length == 2) {
				String sourceName = mapping[0];
				String targetName = mapping[1];
				IDimensionDemandReader sourceDimension = source.getDimension(sourceName);
				if (sourceDimension != null) {
					IDimensionDemandWriter targetDimension = target.getDimension(targetName);
					if (targetDimension == null) {
						target.createDimension(targetName);
						targetDimension = target.getDimension(targetName);
					}
					IFeatureDemandReader[] sourceFeatures = sourceDimension.getFeatures();
					for (int j = 0; j < sourceFeatures.length; j++) {
						IFeatureDemandReader sourceFeature = sourceFeatures[j];
						if (sourceFeature.getValue() == null) {
							targetDimension.createFeature(sourceFeature.getName(), 
								sourceFeature.getComparator(),	sourceFeature.getMinimum(), 
										sourceFeature.getMaximum());
						} else {
							targetDimension.createFeature(sourceFeature.getName(), 
								sourceFeature.getComparator(), sourceFeature.getValue());
						}
					}
				}
			} else if (mapping.length == 4) {
				String sourceDimensionName = mapping[0];
				String sourceFeatureName = mapping[1];
				String targetDimensionName = mapping[2];
				String targetFeatureName = mapping[3];
				IDimensionDemandReader sourceDimension = source.getDimension(sourceDimensionName);
				if (sourceDimension != null) {
					IDimensionDemandWriter targetDimension = target.getDimension(targetDimensionName);
					if (targetDimension == null) {
						target.createDimension(targetDimensionName);
						targetDimension = target.getDimension(targetDimensionName);
					}
					IFeatureDemandReader sourceFeature = sourceDimension.getFeature(sourceFeatureName);
					if (sourceFeature != null) {
						if (sourceFeature.getValue() == null) {
							targetDimension.createFeature(targetFeatureName, 
								sourceFeature.getComparator(),	sourceFeature.getMinimum(), 
										sourceFeature.getMaximum());
						} else {
							targetDimension.createFeature(targetFeatureName, 
								sourceFeature.getComparator(), sourceFeature.getValue());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Maps all mappings contained in the mapper from the source to the
	 * target. If the source or the target is null, this method will
	 * simply return.
	 * 
	 * @param source The source contract that provides the input.
	 * @param target The target contract that will be adjusted.
	 */
	public void map(ITypeProvisionReader source, ITypeProvisionWriter target) {
		if (source == null || target == null) return;
		for (int i = 0; i < mappings.size(); i++) {
			String[] mapping = (String[])mappings.elementAt(i);
			if (mapping.length == 2) {
				String sourceName = mapping[0];
				String targetName = mapping[1];
				IDimensionProvisionReader sourceDimension = source.getDimension(sourceName);
				if (sourceDimension != null) {
					IDimensionProvisionWriter targetDimension = target.getDimension(targetName);
					if (targetDimension == null) {
						target.createDimension(targetName);
						targetDimension = target.getDimension(targetName);
					}
					IFeatureProvisionReader[] sourceFeatures = sourceDimension.getFeatures();
					for (int j = 0; j < sourceFeatures.length; j++) {
						IFeatureProvisionReader sourceFeature = sourceFeatures[j];
						targetDimension.createFeature(sourceFeature.getName(), sourceFeature.getValue());
					}
				}
			} else if (mapping.length == 4) {
				String sourceDimensionName = mapping[0];
				String sourceFeatureName = mapping[1];
				String targetDimensionName = mapping[2];
				String targetFeatureName = mapping[3];
				IDimensionProvisionReader sourceDimension = source.getDimension(sourceDimensionName);
				if (sourceDimension != null) {
					IDimensionProvisionWriter targetDimension = target.getDimension(targetDimensionName);
					if (targetDimension == null) {
						target.createDimension(targetDimensionName);
						targetDimension = target.getDimension(targetDimensionName);
					}
					IFeatureProvisionReader sourceFeature = sourceDimension.getFeature(sourceFeatureName);
					targetDimension.createFeature(targetFeatureName, sourceFeature.getValue());
				}
			}
		}
	}	

}
