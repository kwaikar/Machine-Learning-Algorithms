/**
 * 
 */
package com.decisiontree.common;

/**
 * @author Kanchan Waikar Date Created : 12:04:50 AM
 *
 */
public class OccuranceCounts {

	private int positiveProportionCount=0;
	private int negativeProportionCount=0;
	private double entropy;

	/**
	 * @return the positiveProportionCount
	 */
	public int getPositiveProportionCount() {
		return positiveProportionCount;
	}

	/**
	 * @param positiveProportionCount
	 *            the positiveProportionCount to set
	 */
	public void setPositiveProportionCount(int positiveProportionCount) {
		this.positiveProportionCount = positiveProportionCount;
	}

	/**
	 * @return the negativeProportionCount
	 */
	public int getNegativeProportionCount() {
		return negativeProportionCount;
	}

	/**
	 * @param negativeProportionCount
	 *            the negativeProportionCount to set
	 */
	public void setNegativeProportionCount(int negativeProportionCount) {
		this.negativeProportionCount = negativeProportionCount;
	}
	
	public void incrementNegativeProportionCount() {
		this.negativeProportionCount +=1;
	}

	public void incrementPositiveProportionCount() {
		this.positiveProportionCount+=1;
	}
	

	/**
	 * @return the entropy
	 */
	public Double getEntropy() {
		return entropy;
	}

	/**
	 * @param entropy the entropy to set
	 */
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[+ve=" + positiveProportionCount + ", -ve="
				+ negativeProportionCount + ", E=" + entropy + "]";
	}

	 

}
