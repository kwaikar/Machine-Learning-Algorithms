package com.decisiontree.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.util.MathUtils;

/**
 * This class accumulates Feature counts for each of the feature.
 * 
 * @author Kanchan
 *
 */
public class FeatureCounts<T> implements Comparable<FeatureCounts<T>>,   Cloneable {

	private String name;
	private Map<T, OccuranceCounts> valueStatistics = new HashMap<T, OccuranceCounts>();
	private OccuranceCounts overallCounts = new OccuranceCounts();
	private double informationGain;

	/**
	 * This method increments the occurrence counts based on value.
	 * 
	 * @param vector
	 */
	public void incrementFeatureCounts(T value, Integer outputValue) {
		OccuranceCounts counts = valueStatistics.get(value);
		if (counts == null) {
			counts = new OccuranceCounts();
			valueStatistics.put(value, counts);
		}
		if (outputValue == 0) {
			counts.incrementNegativeProportionCount();
		} else {
			counts.incrementPositiveProportionCount();
		}
	}

	/**
	 * Named constructor name field
	 * 
	 * @param name
	 */
	public FeatureCounts(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the valueStatistics
	 */
	public Map<T, OccuranceCounts> getValueStatistics() {
		return valueStatistics;
	}

	/**
	 * @param valueStatistics
	 *            the valueStatistics to set
	 */
	public void setValueStatistics(Map<T, OccuranceCounts> valueStatistics) {
		this.valueStatistics = valueStatistics;
	}

	/**
	 * This method aggregates overall counts at feature level.
	 */
	public void populateOverallCounts() {
		for (Map.Entry<T, OccuranceCounts> entry : valueStatistics.entrySet()) {
			this.overallCounts.setNegativeProportionCount(
					this.overallCounts.getNegativeProportionCount() + entry.getValue().getNegativeProportionCount());
			this.overallCounts.setPositiveProportionCount(
					this.overallCounts.getPositiveProportionCount() + entry.getValue().getPositiveProportionCount());
		}
	}

	/**
	 * @return the overallCounts
	 */
	public OccuranceCounts getOverallCounts() {
		return overallCounts;
	}

	/**
	 * @param overallCounts
	 *            the overallCounts to set
	 */
	public void setOverallCounts(OccuranceCounts overallCounts) {
		this.overallCounts = overallCounts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FeatureCounts [name=" + name + ", valueStatistics=" + valueStatistics + ", overallCounts="
				+ overallCounts + ", informationGain=" + informationGain + "]";
	}

	public int compareTo(FeatureCounts<T> o) {
		return (this.getInformationGain().compareTo(o.getInformationGain()));

	}

	/**
	 * @return the informationGain
	 */
	public Double getInformationGain() {
		return informationGain;
	}

	/**
	 * @param informationGain
	 *            the informationGain to set
	 */
	public void setInformationGain(double informationGain) {
		this.informationGain = MathUtils.round(informationGain,15);
	}

}
