package com.decisiontree;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

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
	final static Logger logger = Logger.getLogger(ID3Implementation.class);

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

	private <T> List<FeatureCounts<T>> extractFeatureCounts(List<FeatureVector> inputFeatures,
			Map<String, String> featuresToBeExcluded) {
		List<FeatureCounts<T>> featureCounts = new ArrayList<FeatureCounts<T>>();

		/**
		 * Load all headers in Counts.
		 */
		for (String featureKey : inputFeatures.get(0).getInputMap().keySet()) {
			if (!featuresToBeExcluded.containsKey(featureKey)) {
				featureCounts.add(new FeatureCounts<T>(featureKey));
			}
		}

		/**
		 * featureCounts now has one entry for each feature. Populate all Counts
		 * for each feature. Makes sure that only the ones that are applicable
		 * for selected tree path are being considered in population of data.
		 */

		for (FeatureVector featureVector : inputFeatures) {
			boolean isFeatureVectorApplicable = true;
			for (Map.Entry<String, String> entry : featuresToBeExcluded.entrySet()) {
				if (!featureVector.getInputMap().get(entry.getKey()).equals(entry.getValue())) {
					isFeatureVectorApplicable = false;
				}
			}
			if (isFeatureVectorApplicable) {
				for (FeatureCounts<T> countEntry : featureCounts) {
					if (!featuresToBeExcluded.containsKey(countEntry.getName())) {
						Map<String, Integer> map = featureVector.getInputMap();
						countEntry.incrementFeatureCounts((T) map.get(countEntry.getName()), featureVector.isClass());
					}
				}
			}
		}

		for (FeatureCounts<T> countEntry : featureCounts) {
			countEntry.populateOverallCounts();
		}
		return featureCounts;
	}

	public static void main(String[] args) throws Exception {
		ID3Implementation predictor = new ID3Implementation();
		List<FeatureVector> inputFeatureVectors = predictor.readInputFeatures(
				predictor.getClass().getClassLoader().getResource("data_sets1/training_set.csv").getFile());/*
		predictor.printTree("",
				predictor.getTreeUsingID3InformationGain(inputFeatureVectors, null, new HashMap<String, String>(),false));*/
		predictor.printTree("",
				predictor.getTreeUsingID3InformationGain(inputFeatureVectors, null, new HashMap<String, String>(),true));
	}

	/**
	 * This method returns the best attribute that Tree should be split on.
	 * 
	 * @param counts
	 * @return
	 */
	public <T> FeatureCounts<T> getBestAttribute(List<FeatureCounts<T>> counts) {
		return Collections.max(counts);
	}

	/**
	 * This method prints Tree using DFS approach
	 * 
	 * @param tabsPrefix
	 * @param node
	 */
	public <T> void printTree(String tabsPrefix, DecisionNode<T> node) {
		logger.info(tabsPrefix + node.getName() + "= " + node.getValueChosen() + " = " + (node.getResult() != null
				? node.getResult() : "" + node.getFeatureCount().getOverallCounts().getEntropy()));
		if (CollectionUtils.isNotEmpty(node.getChildren())) {
			for (DecisionNode<T> child : node.getChildren()) {
				printTree(tabsPrefix + "-", child);
			}
		}
	}

	/**
	 * This method returns the Decision tree based on information gain
	 * attribute.
	 * 
	 * @param featureCounts
	 * @param parent
	 * @param map
	 * @return
	 */
	public <T> DecisionNode<T> getTreeUsingID3InformationGain(List<FeatureVector> inputFeatureVectors,
			DecisionNode<T> parent, Map<String, T> map, boolean useVarianceImpurityForGain) {
		if (parent == null) {
			parent = new DecisionNode<T>(new FeatureCounts<T>("ROOT"));
		}
		List<FeatureCounts<T>> featureCounts = calculateInformationGain(inputFeatureVectors, map,useVarianceImpurityForGain);
		if (!featureCounts.isEmpty()) {
			FeatureCounts<T> bestAttribute = null;
			bestAttribute = getBestAttribute(featureCounts);
			for (Map.Entry<T, OccuranceCounts> entry : bestAttribute.getValueStatistics().entrySet()) {

				DecisionNode<T> child = new DecisionNode<T>(bestAttribute);
				child.setValueChosen(entry.getKey());
				map.put(bestAttribute.getName(), entry.getKey());

				if (entry.getValue().getNegativeProportionCount() != 0
						&& entry.getValue().getPositiveProportionCount() != 0) {
					parent.getChildren().add(
							getTreeUsingID3InformationGain(inputFeatureVectors, child, new HashMap<String, T>(map),useVarianceImpurityForGain));
				} else if (entry.getValue().getNegativeProportionCount() == 0) {
					child.setResult(true);
					parent.getChildren().add(child);
				} else if (entry.getValue().getPositiveProportionCount() == 0) {
					child.setResult(false);
					parent.getChildren().add(child);
				}
			}
		}
		return parent;
	}

	/**
	 * This method sets entropy in featureVectors sent.
	 * 
	 * @param <T>
	 * 
	 * @param inputFeatureVectors
	 * @param map
	 * @return
	 */
	public <T> List<FeatureCounts<T>> calculateInformationGain(List<FeatureVector> inputFeatureVectors, Map map,
			boolean useVarianceImpurityForGain) {
		List<FeatureCounts<T>> featureCounts = extractFeatureCounts(inputFeatureVectors, map);
		for (FeatureCounts<T> feature : featureCounts) {
			Utils.setEntropy(feature.getOverallCounts(),useVarianceImpurityForGain);
			double gain = feature.getOverallCounts().getEntropy();
			double modS = feature.getOverallCounts().getNegativeProportionCount()
					+ feature.getOverallCounts().getPositiveProportionCount();
			for (OccuranceCounts count : feature.getValueStatistics().values()) {
				Utils.setEntropy(count,useVarianceImpurityForGain);
					gain -= ((count.getPositiveProportionCount() + count.getNegativeProportionCount()) / modS)
							* count.getEntropy();
			}
			feature.setInformationGain(gain);

		}
		return featureCounts;
	}
}
