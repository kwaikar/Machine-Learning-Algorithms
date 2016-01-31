package com.decisiontree;

import java.io.Externalizable;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.decisiontree.common.FeatureCounts;
import com.decisiontree.common.FeatureVector;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

/**
 * This class implements decision tree.
 * 
 * @author Kanchan Waikar Date Created : 8:44:06 PM
 *
 */
public class App {

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

	private List<FeatureCounts<Integer>> extractFeatureCounts(List<FeatureVector> inputFeatures) {
		List<FeatureCounts<Integer>> featureCounts = new ArrayList<FeatureCounts<Integer>>();

		/**
		 * Load all headers in Counts.
		 */
		for (String featureKey : inputFeatures.get(0).getInputMap().keySet()) {
			featureCounts.add(new FeatureCounts<Integer>(featureKey));
		}
		;

		/**
		 * featureCounts now has one entry for each feature. Populate all Counts
		 * for each feature.
		 */

		for (FeatureCounts<Integer> countEntry : featureCounts) {
			for (FeatureVector featureVector : inputFeatures) {
				Map<String, Integer> map = featureVector.getInputMap();
				countEntry.incrementFeatureCounts(map.get(countEntry.name), featureVector.isClass());
			}
			countEntry.populateOverallCounts();
		}
		return featureCounts;
	}

	public static void main(String[] args) throws Exception {
		App predictor = new App();
		List<FeatureVector> inputFeatureVectors = predictor.readInputFeatures(
				predictor.getClass().getClassLoader().getResource("data_sets1/test_set.csv").getFile());
		List<FeatureCounts<Integer>> featureCounts = predictor.extractFeatureCounts(inputFeatureVectors);
		System.out.println(featureCounts);

	}
}
