package info.pppc.pcomx.contract;

import java.util.Vector;

import info.pppc.base.system.util.Comparator;
import info.pppc.pcom.system.model.contract.IContract;
import info.pppc.pcom.system.model.contract.reader.IDimensionDemandReader;
import info.pppc.pcom.system.model.contract.reader.IFeatureDemandReader;
import info.pppc.pcom.system.model.contract.reader.ITypeDemandReader;

/**
 * The simple checker is a helper class that checks an input contract
 * for the existence of dimensions or features that are unknown. This
 * can be used to determine whether a certain demand can be fulfilled
 * by some element without writing the checking code all the time.
 * 
 * @author Mac
 */
public class SimpleValidator {

	/**
	 * The rules that are checked by this validator.
	 */
	private Vector rules = new Vector();
	
	/**
	 * Creates a new simple validator without any validation rules.
	 */
	public SimpleValidator() {
		super();
	}
	
	/**
	 * Adds the specified dimension to the set of allowed dimensions.
	 * Adding a dimension means that all attributes within the 
	 * dimension are understood and not checked.
	 * 
	 * @param dimension The name of the dimension to add.
	 */
	public void addDimension(String dimension) {
		Object[] rule = new Object[] { dimension };
		rules.addElement(rule);
	}
	
	/**
	 * Removes a previously added dimension from the set of allowed
	 * dimensions.
	 * 
	 * @param dimension The name of the dimension to remove.
	 * @return True if the dimension has been removed, false otherwise.
	 */
	public boolean removeDimension(String dimension) {
		return remove(new Object[] { dimension });
	}
	
	/**
	 * Adds the specified feature to the set of features that are 
	 * understood.
	 * 
	 * @param dimension The dimension name of the feature.
	 * @param feature The feature name.
	 */
	public void addFeature(String dimension, String feature) {
		Object[] rule = new Object[] { dimension, feature };
		rules.addElement(rule);
	}
	
	/**
	 * Removes the specified feature from the set of features that
	 * are understood.
	 * 
	 * @param dimension The dimension name of the feature.
	 * @param feature The feature name.
	 * @return True if the feature has been removed, false if the
	 * 	feature was not specified as rule.
	 */
	public boolean removeFeature(String dimension, String feature) {
		return remove(new Object[] { dimension, feature });
	}
	
	/**
	 * Adds the specified feature and feature value to the set of
	 * understood features.
	 * 
	 * @param dimension The dimension name of the feature.
	 * @param value The value of the feature.
	 * @param feature The feature name.
	 */
	public void addFeature(String dimension, String feature, Object value) {
		Object[] rule = new Object[] { dimension, feature, value };
		rules.addElement(rule);
	}
	
	/**
	 * Removes the specified feature and feature value from the set
	 * of understood features.
	 * 
	 * @param dimension The dimension name of the feature.
	 * @param feature The feature name.
	 * @param value The value of the feature.
	 * @return True if the feature has been found and removed, false otherwise.
	 */
	public boolean removeFeature(String dimension, String feature, Object value) {
		return remove(new Object[] { dimension, feature, value });
	}
	
	
	/**
	 * Removes the specified rule from the set of rules.
	 * 
	 * @param rule The rule to remove.
	 * @return True if the rule has been removed, false otherwise.
	 */
	private boolean remove(Object[] rule) {
		rules: for (int i = 0; i < rules.size(); i++) {
			Object[] r = (Object[])rules.elementAt(i);
			if (r.length == rule.length) {
				for (int j = 0; j < r.length; j++) {
					if (! r[j].equals(rule[j])) {
						continue rules;
					}
				}
				rules.removeElementAt(i);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Validates the specified type demand against the rules.
	 * 
	 * @param demand The type demand to check.
	 * @return True if the demand suffices the rules. This means that
	 * 	all features specified in the demand are either covered through
	 * 	dimensions, features with names or features with names and values.
	 */
	public boolean validate(ITypeDemandReader demand) {
		IDimensionDemandReader[] dimensions = demand.getDimensions();
		for (int i = 0; i < dimensions.length; i++) {
			if (! validate(dimensions[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validates the specified dimension demand against the rules.
	 * 
	 * @param demand The dimension demand to check.
	 * @return True if the demand suffices the rules. This means that
	 * 	all features specified in the demand are either covered through
	 * 	dimensions, features with names or features with names and values.
	 */
	public boolean validate(IDimensionDemandReader demand) {
		String name = demand.getName();
		Vector applying = new Vector();
		for (int i = 0; i < rules.size(); i++) {
			Object[] rule = (Object[]) rules.elementAt(i);
			if (name.equals(rule[0])) {
				if (rule.length == 1) return true;
				else applying.addElement(rule);
			}
		}
		IFeatureDemandReader[] features = demand.getFeatures();
		if (features.length != 0) {
			for (int i = 0; i < features.length; i++) {
				if (! validate(features[i], applying)) {
					return false;
				}
			}
			return true;
		} else {
			return applying.size() > 0;
		}
	}
	
	/**
	 * Determines whether the specified demand can be matched using
	 * the passed set of applying rules.
	 * 
	 * @param demand The feature demand to validate.
	 * @param applying The set of applying rules
	 * @return True if the feature is covered by the set of applying
	 * 	rules.
	 */
	private boolean validate(IFeatureDemandReader demand, Vector applying) {
		String name = demand.getName();
		for (int i = applying.size() - 1; i >= 0; i--) {
			Object[] rule = (Object[])applying.elementAt(i);
			String ruleName = (String)rule[1];
			if (ruleName.equals(name)) {
				// this rule applies to the demand, we can remove it
				// from the vector to speed up other features
				applying.removeElementAt(i);
				if (rule.length == 2) {
					return true;
				} else {
					Object pvalue = rule[2];
					Object dvalue = demand.getValue();
					Object dmin = demand.getMinimum();
					Object dmax = demand.getMaximum();
					switch (demand.getComparator()) {
						case IContract.IFEQ:
							if (pvalue.equals(dvalue)) 
								return true;
							break;
						case IContract.IFGE:
							if (Comparator.isMoreOrEqual(pvalue, dvalue)) 
								return true;
							break;
						case IContract.IFGT:
							if (Comparator.isMore(pvalue, dvalue)) 
								return true;
							break;
						case IContract.IFLE:
							if (Comparator.isLessOrEqual(pvalue, dvalue)) 
								return true;
							break;
						case IContract.IFLT:
							if (Comparator.isLess(pvalue, dvalue)) 
								return true;
							break;
						case IContract.IFIR:
							if (Comparator.isMoreOrEqual(pvalue, dmin) 
									&& Comparator.isLessOrEqual(pvalue, dmax)) 
										return true;
							break;
						case IContract.IFOR:
							if (Comparator.isLess(pvalue, dmin) 
									|| Comparator.isMore(pvalue, dmax)) 
										return true;
							break;
						default:
							// will never happen
					}
				}
			}			
		}
		return false;
	}

}
