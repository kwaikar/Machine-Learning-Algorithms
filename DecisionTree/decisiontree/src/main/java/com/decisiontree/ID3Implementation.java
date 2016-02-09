package com.decisiontree;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.decisiontree.common.DecisionNode;
import com.decisiontree.common.FeatureCounts;
import com.decisiontree.common.FeatureVector;
import com.decisiontree.common.OccuranceCounts;
import com.decisiontree.util.Utils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

/**
 * This class implements decision tree.
 * 
 * @author Kanchan Waikar Date Created : 8:44:06 PM
 *
 */
public class ID3Implementation {

	/**
	 * This function accepts filePath and returns the Feature Vector identified.
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private List<FeatureVector> readInputFeatures(String filePath) throws Exception {
		List<FeatureVector> inputFeaturesList = null;
		File inputFile = new File(filePath);

		if (inputFile.exists()) {
			HeaderColumnNameMappingStrategy<FeatureVector> strategy = new HeaderColumnNameMappingStrategy<FeatureVector>();
			strategy.setType(FeatureVector.class);
			CsvToBean<FeatureVector> csvToBean = new CsvToBean<FeatureVector>();
			inputFeaturesList = csvToBean.parse(strategy,
					new CSVReader(new StringReader(FileUtils.readFileToString(inputFile))));
		}
		return inputFeaturesList;

	}

	private List<FeatureCounts<Integer>> extractFeatureCounts(List<FeatureVector> inputFeatures,
			Map featuresToBeExcluded) {
		List<FeatureCounts<Integer>> featureCounts = new ArrayList<FeatureCounts<Integer>>();

		/**
		 * Load all headers in Counts.
		 */
		for (String featureKey : inputFeatures.get(0).getInputMap().keySet()) {
			if (!featuresToBeExcluded.containsKey(featureKey)) {
				featureCounts.add(new FeatureCounts<Integer>(featureKey));
			}
		}

		/**
		 * featureCounts now has one entry for each feature. Populate all Counts
		 * for each feature.
		 */

		for (FeatureCounts<Integer> countEntry : featureCounts) {
			for (FeatureVector featureVector : inputFeatures) {
				if (!featuresToBeExcluded.containsKey(countEntry.name)) {
					Map<String, Integer> map = featureVector.getInputMap();
					countEntry.incrementFeatureCounts(map.get(countEntry.name), featureVector.isClass());
				}
			}
			countEntry.populateOverallCounts();
		}
		return featureCounts;
	}

	public static void main(String[] args) throws Exception {
		ID3Implementation predictor = new ID3Implementation();
		List<FeatureVector> inputFeatureVectors = predictor.readInputFeatures(
				predictor.getClass().getClassLoader().getResource("data_sets1/test_set.csv").getFile());

		Map map = new HashMap();

		List<FeatureCounts<Integer>> featureCounts = predictor.setEntropy(inputFeatureVectors, map);
		System.out.println(featureCounts);

	}
	
	public void getBestAttribute(FeatureCounts count)
	{
		
	}

	/*public DecisionNode growTree(List<FeatureCounts> counts, FeatureCounts target,FeatureCounts attributes)
	{
		
	}*/
	/**
	 * This method sets entropy in featureVectors sent.
	 * @param inputFeatureVectors
	 * @param map
	 * @return
	 */
	public List<FeatureCounts<Integer>> setEntropy(List<FeatureVector> inputFeatureVectors, Map map) {
		List<FeatureCounts<Integer>> featureCounts = extractFeatureCounts(inputFeatureVectors, map);
		for (FeatureCounts<Integer> feature : featureCounts) {
			Utils.setEntropy(feature.getOverallCounts());
			double gain = feature.getOverallCounts().getEntropy();
			double modS = feature.getOverallCounts().getNegativeProportionCount()+feature.getOverallCounts().getPositiveProportionCount();
			for (OccuranceCounts count : feature.getValueStatistics().values()) {
				Utils.setEntropy(count);
				gain-=((count.getPositiveProportionCount()+count.getNegativeProportionCount())/modS)*count.getEntropy();
			}
			feature.setInformationGain(gain);
			
			
		}
		return featureCounts;
	}
}
