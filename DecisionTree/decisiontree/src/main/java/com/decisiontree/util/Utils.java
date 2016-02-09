package com.decisiontree.util;

import com.decisiontree.common.OccuranceCounts;

public class Utils {

	/**
	 * Returns entropy based on counts available.
	 * @param counts
	 * @return
	 */
	public static void setEntropy(OccuranceCounts counts)
	{
		int total = counts.getPositiveProportionCount()+counts.getNegativeProportionCount();
		double positiveProb = (double)counts.getPositiveProportionCount()/total;
		double negativeProb =(double) counts.getNegativeProportionCount()/total;
		counts.setEntropy((double) (-positiveProb * log2(positiveProb)+ (-negativeProb * log2(negativeProb))));
		System.out.println(counts);
	}

	
	/**
	 * Accepts probabilities and calculates entropy on the same.
	 * @param positiveProb - positive probability
	 * @param negativeProb - Negative probability
	 * @return
	 */
	public static double getEntropy(double positiveProb, double negativeProb)
	{
		return (-positiveProb * log2(positiveProb)+ (-negativeProb * log2(negativeProb)));
	}

	/**
	 * This method returns log to the base 2 value of the number required
	 * @param n
	 * @return
	 */
	public static double log2(double n)
	{
	    return (Math.log(n) / Math.log(2));
	}
}
