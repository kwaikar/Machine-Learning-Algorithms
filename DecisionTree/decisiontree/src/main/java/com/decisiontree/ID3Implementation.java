package com.decisiontree;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

		// predictor.extractStatistics(predictor.getClass().getClassLoader().getResource("Data_Set_1/training_set.csv").getFile(),predictor.getClass().getClassLoader().getResource("Data_Set_1/validation_set.csv").getFile(),predictor.getClass().getClassLoader().getResource("Data_Set_1/test_set.csv").getFile());
		// System.out.println();
		predictor.extractStatistics(
				predictor.getClass().getClassLoader().getResource("Data_Set_2/training_set.csv").getFile(),
				predictor.getClass().getClassLoader().getResource("Data_Set_2/validation_set.csv").getFile(),
				predictor.getClass().getClassLoader().getResource("Data_Set_2/test_set.csv").getFile());
	}

	/**
	 * @param predictor
	 * @param treeRootWithVarianceImpurity
	 * @param treeRootUsingInfoGain
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void extractStatistics(String trainingSetPath, String testSetPath, String validationSetPath)
			throws Exception {
		List<FeatureVector> inputFeatureVectors = readInputFeatures(trainingSetPath);

		DecisionNode treeRootWithVarianceImpurity = getTreeUsingID3InformationGain(inputFeatureVectors, null,
				new HashMap<String, String>(), false);

		DecisionNode treeRootUsingInfoGain = getTreeUsingID3InformationGain(inputFeatureVectors, null,
				new HashMap<String, String>(), false);
		// printTree("-", treeRootUsingInfoGain);
		getPrunedTree(treeRootUsingInfoGain, 100, 10, validationSetPath);
		// printAccuracy(treeRootWithVarianceImpurity, treeRootUsingInfoGain,
		// testSetPath);
		// printAccuracy(treeRootWithVarianceImpurity, treeRootUsingInfoGain,
		// validationSetPath);
		// printAccuracy(treeRootWithVarianceImpurity, treeRootUsingInfoGain,
		// trainingSetPath);
	}

	/**
	 * @param treeRootWithVarianceImpurity
	 * @param treeRootUsingInfoGain
	 * @param fileName
	 * @throws Exception
	 */
	public <T> void printAccuracy(DecisionNode<T> treeRootWithVarianceImpurity, DecisionNode<T> treeRootUsingInfoGain,
			String fileName) throws Exception {
		double accuracy;
		accuracy = getDataSetAccuracy(treeRootUsingInfoGain, fileName);
		System.out.println(fileName + ":Accuracy (information gain): " + accuracy);
		accuracy = getDataSetAccuracy(treeRootWithVarianceImpurity, fileName);
		System.out.println(fileName + ":Accuracy (Variance Impurity) : " + accuracy);
	}

	/**
	 * @param predictor
	 * @param treeRootUsingEntropy
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private double getDataSetAccuracy(DecisionNode treeRootUsingEntropy, String fileName) throws Exception {
		List<FeatureVector> testSet = readInputFeatures(fileName);
		int correctCounter = 0;
		for (FeatureVector featureVector : testSet) {
			if (isPredictedClassSameAsActual(featureVector, treeRootUsingEntropy)) {
				correctCounter++;
			}
		}
		double accuracy = (100 * (double) correctCounter / testSet.size());
		return accuracy;
	}

	/**
	 * This method Prunes the Tree Provided for given value of L and K.
	 * 
	 * @param dTree
	 * @param l
	 * @param k
	 * @return
	 */
	public <T> DecisionNode<T> getPrunedTree(DecisionNode<T> dTree, int l, int k, String validationSetPath)
			throws Exception {

		Double originalTreeAccuracy = getDataSetAccuracy(dTree, validationSetPath);
		System.out.println("Accuracy before Pruning:" + originalTreeAccuracy);
		DecisionNode<T> dBest = dTree;
		for (int i = 1; i <= l; i++) {
			DecisionNode<T> dHat = dTree;

			int m = (int) (Math.random() * k);
			for (int j = 1; j <= m; j++) {
				int maxIndex = assignIndexToNonLeafNodesAndReturnMaxIndex(dHat, 0);
				int p = (int) (Math.random() * maxIndex);
				DecisionNode<T> pthNode = getNodeByIndex(dHat, p);
				pthNode.setChildren(null);
				pthNode.setResult(pthNode.getFeatureCount().getOverallCounts().getPositiveProportionCount() > pthNode
						.getFeatureCount().getOverallCounts().getNegativeProportionCount());
				Double prunedTreeAccuracy = getDataSetAccuracy(dHat, validationSetPath);
				if (prunedTreeAccuracy > originalTreeAccuracy) {
					dBest = dHat;
					originalTreeAccuracy = prunedTreeAccuracy;
				}
			}
		}
		System.out.println("Accuracy after Pruning:" + originalTreeAccuracy);
		return dBest;
	}

	/**
	 * This method assigns a number to each of the non-Leaf node.
	 * 
	 * @param root
	 * @param number
	 * @return
	 */
	public <T> int assignIndexToNonLeafNodesAndReturnMaxIndex(DecisionNode<T> root, Integer number) {
		if (!CollectionUtils.isEmpty(root.getChildren())) {
			root.setIndex(number);
			number = number + 1;
			for (DecisionNode<T> child : root.getChildren()) {
				number = assignIndexToNonLeafNodesAndReturnMaxIndex(child, number);
			}
			return number;
		}
		return number;

	}

	/**
	 * This method returns the node by index
	 * 
	 * @param root
	 * @param index
	 * @return
	 */
	public <T> DecisionNode<T> getNodeByIndex(DecisionNode<T> root, int index) {

		DecisionNode<T> nodeToBeReturned = root;
		Queue<DecisionNode<T>> queue = new ArrayDeque<DecisionNode<T>>();
		queue.add(nodeToBeReturned);
		while (queue.peek() != null && nodeToBeReturned.getIndex() != index) {
			nodeToBeReturned = queue.remove();
			if (!CollectionUtils.isEmpty(nodeToBeReturned.getChildren())) {
				for (DecisionNode<T> child : nodeToBeReturned.getChildren()) {
					queue.add(child);
				}
			}
		}
		if (queue.isEmpty()) {
			return null;
		} else {
			return nodeToBeReturned;
		}
	}

	/**
	 * This method sets node in the tree at given index
	 * 
	 * @param root
	 * @param index
	 * @param node
	 * @return
	 */
	public <T> DecisionNode<T> setNodeByIndex(DecisionNode<T> root, int index, DecisionNode<T> node) {

		DecisionNode<T> nodeToBeReturned = root;
		Queue<DecisionNode<T>> queue = new ArrayDeque<DecisionNode<T>>();
		queue.add(nodeToBeReturned);
		while (queue.peek() != null && nodeToBeReturned.getIndex() != index) {
			nodeToBeReturned = queue.remove();
			if (!CollectionUtils.isEmpty(nodeToBeReturned.getChildren())) {
				for (DecisionNode<T> child : nodeToBeReturned.getChildren()) {
					queue.add(child);
				}
			}
		}
		if (queue.isEmpty()) {
			return null;
		} else {
			return nodeToBeReturned = node;
		}
	}

	/**
	 * This function returns number of Non-Leaf nodes from the tree
	 * 
	 * @param root
	 * @return
	 */
	public <T> int getNumNonLeafNodes(DecisionNode<T> root) {
		int total = 0;
		if (!CollectionUtils.isEmpty(root.getChildren())) {
			total++;
			for (DecisionNode<T> child : root.getChildren()) {
				total += getNumNonLeafNodes(child);
			}
			return total;
		}
		return total;

	}

	/**
	 * This method validates whether the Prediction and actual values are
	 * exactly same or not.
	 * 
	 * @param vector
	 * @param node
	 * @return
	 */
	private <T> Boolean isPredictedClassSameAsActual(FeatureVector vector, DecisionNode<T> node) {
		DecisionNode<T> temp = node;
		while (CollectionUtils.isNotEmpty(temp.getChildren())) {
			for (DecisionNode<T> current : temp.getChildren()) {
				logger.debug(current.getName() + " :" + current.getChildren());
				if (current.getValueChosen().equals(vector.getInputMap().get(current.getName()))) {
					temp = current;
				}
			}
		}
		return (temp.getResult().equals(vector.isClass() == 1 ? true : false));
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
				? node.getResult() : "" + "(" + node.getIndex() + ")" + node.getFeatureCount().getOverallCounts()));
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
		List<FeatureCounts<T>> featureCounts = calculateInformationGain(inputFeatureVectors, map,
				useVarianceImpurityForGain);
		if (!featureCounts.isEmpty()) {
			FeatureCounts<T> bestAttribute = getBestAttribute(featureCounts);
			if (bestAttribute.getInformationGain() != 0.0) {

				/**
				 * Create Branches for the Selected Best Attribute.
				 */
				for (Map.Entry<T, OccuranceCounts> entry : bestAttribute.getValueStatistics().entrySet()) {

					DecisionNode<T> child = new DecisionNode<T>(bestAttribute);
					child.setValueChosen(entry.getKey());
					map.put(bestAttribute.getName(), entry.getKey());

					if (entry.getValue().getNegativeProportionCount() != 0
							&& entry.getValue().getPositiveProportionCount() != 0) {
						/**
						 * Expand the tree for which there is no
						 * "Pure set available"
						 */
						parent.getChildren().add(getTreeUsingID3InformationGain(inputFeatureVectors, child,
								new HashMap<String, T>(map), useVarianceImpurityForGain));
					} else if (entry.getValue().getNegativeProportionCount() == 0) {
						/**
						 * Assign Class to Pure Nodes.
						 */
						child.setResult(true);
						parent.getChildren().add(child);
					} else if (entry.getValue().getPositiveProportionCount() == 0) {
						child.setResult(false);
						parent.getChildren().add(child);
					}
				}
			} else {
				parent.setResult(false);
			}
		}
		return parent;
	}

	/**
	 * This method sets entropy in featureVectors sent.
	 * 
	 * @param inputFeatureVectors
	 * @param map
	 * @return
	 */
	public <T> List<FeatureCounts<T>> calculateInformationGain(List<FeatureVector> inputFeatureVectors, Map map,
			boolean useVarianceImpurityForGain) {
		List<FeatureCounts<T>> featureCounts = extractFeatureCounts(inputFeatureVectors, map);
		for (FeatureCounts<T> feature : featureCounts) {
			Utils.setEntropy(feature.getOverallCounts(), useVarianceImpurityForGain);
			double gain = feature.getOverallCounts().getEntropy();
			double modS = feature.getOverallCounts().getNegativeProportionCount()
					+ feature.getOverallCounts().getPositiveProportionCount();
			for (OccuranceCounts count : feature.getValueStatistics().values()) {
				Utils.setEntropy(count, useVarianceImpurityForGain);
				gain -= ((count.getPositiveProportionCount() + count.getNegativeProportionCount()) / modS)
						* count.getEntropy();
			}
			feature.setInformationGain(gain);

		}
		return featureCounts;
	}
}
