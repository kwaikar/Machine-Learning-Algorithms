package logisticregression.textclassification;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import logisticregression.textclassification.common.Token;

/**
 * MCAP Logistic Regression algorithm with L2 regularization
 * 
 * This class is implementation of Text Classifier that uses Logistic Regression
 * approach. This approach involves calculation of P(y|x) based on trained
 * model. We use gradient ascent approach in order to reach the global maxima.
 * 
 * @author Kanchan Waikar Date Created : Mar 15, 2016 - 6:44:13 PM
 *
 */
public class LogisticRegressionClassifier {
	private static final String SIMPLE_TOKENIZER_REGEX = "( |\t|\r|\n|\f)";
	private Set<String> stopWordsList = new HashSet<String>();
	private int tokenCounter = Integer.MIN_VALUE;
	Map<String, Token> tokens = null;

	/**
	 * This method initializes classifier with an optional stopwords file
	 * 
	 * @param stopWordsFile
	 * @throws IOException
	 */
	private void initialize(File stopWordsFile) throws IOException {
		tokenCounter = 0;
		tokens = new LinkedHashMap<String, Token>();
		if (stopWordsFile != null && stopWordsFile.exists()) {
			stopWordsList = new HashSet<String>(
					Arrays.asList(FileUtils.readFileToString(stopWordsFile).split(SIMPLE_TOKENIZER_REGEX)));
		//	System.out.println("Stopwords File found: Loaded " + stopWordsList.size() + " stop words in memory");
		}
	}

	/**
	 * Accepts input Directory path and trains a Naive Bayes In memory model for
	 * doing Text classification.
	 * 
	 * @param path
	 * @throws IOException
	 */
	private double[] trainModel(String path, int numIterations, double lambda, double learningRate) throws IOException {
		double[] weights = new double[0];
		File parentFolder = new File(path);
		if (parentFolder.exists()) {
			//System.out.println("Training on");
			File[] classes = parentFolder.listFiles();
			for (File classDirectory : classes) {

				if (classDirectory.isDirectory()) {
					String classOfData = classDirectory.getName();
				//	System.out.println("\t" + classDirectory.getAbsolutePath());

					for (File classifiedFile : classDirectory.listFiles()) {
						/**
						 * Read each token, and build vocabulary of the class.
						 */
						String[] input = FileUtils.readFileToString(classifiedFile).replaceAll("[^\\x20-\\x7e]", "")
								.split(SIMPLE_TOKENIZER_REGEX);
						for (String token : input) {
							if (StringUtils.isNotBlank(token) && !stopWordsList.contains(token)) {
								/**
								 * Build token counts Map.
								 */
								buildVocabulary(token.trim(), classOfData);
							}
						}
					}
					/**
					 * Maintain aggregated Class level counts.
					 */

				} else {
					System.out.println("Invalid directory structure. Please provide path of the directory that contains"
							+ " one level directory structure for each of the class.");
					System.exit(0);
				}
			}

			/**
			 * Now that data pre-processing has happened, lets calculate all ws
			 * for logistic regression.
			 */

			weights = applyLogisticRegression(classes, numIterations, learningRate, lambda);
			return weights;
		} else {
			System.out.println("Invalid directory structure. Please provide path of the directory that contains"
					+ " one level directory structure for each of the class.");
			System.exit(0);
			return null;
		}
	}

	/**
	 * This method loops through all the files from given path and prints the
	 * accuracy of the match. The directory structure expected is same as that
	 * 
	 * @param path
	 *            - Meta Path Input is expected to have the directory structure
	 *            {path}/{className}/file.txt
	 * @return - Accuracy of the data on which prediction is being tested.
	 * @throws IOException
	 */
	public double calculateAccuracy(String path, double[] weights) throws IOException {
		double accuracy = 0;
		File parentFolder = new File(path);
		if (parentFolder.exists()) {
			File[] classes = parentFolder.listFiles();
			int totalDocs = 0;
			int correctPredictions = 0;
			/**
			 * Loop through all the available directories - here each directory
			 * name is expected to be class name.
			 */
			for (File classDirectory : classes) {
				//System.out.println("\t" + classDirectory.getAbsolutePath());
				if (classDirectory.isDirectory()) {

					/**
					 * get Class Name, predict class on each of the file.
					 */
					int yL = 0;
					if (classDirectory.getName().contains("ham")) {
						yL = 1;
					}

					for (File classifiedFile : classDirectory.listFiles()) {
						totalDocs++;

						int[] inputFeatureVectorX = getInputFeatureMatrix(classifiedFile);
						int predictedYl = isClassHam(weights, inputFeatureVectorX);
						/**
						 * Verify if both the classes are matching or not.
						 */
						if (predictedYl == yL) {
							correctPredictions++;
						}
					}
				}
			}
			accuracy = 100 * (double) correctPredictions / totalDocs;
	//		System.out.println("Total accuracy found : " + accuracy);
		}

		return (accuracy);
	}

	/**
	 * This function accepts set of folders (ham/spam), number ofiterations,
	 * learningRate and lambda and returns weights of the calculated logistic
	 * regression function
	 * 
	 * @param folders
	 * @param numIterations
	 * @param learningRate
	 * @param lambda
	 * @return
	 * @throws IOException
	 */
	private double[] applyLogisticRegression(File[] folders, int numIterations, double learningRate, double lambda)
			throws IOException {
		double[] weights = new double[tokens.size() + 1];
		double[] subtractionMatrix = new double[tokens.size()];

		for (int numIteration = 0; numIteration < numIterations; numIteration++) {

			for (File classDirectory : folders) {
				int yL = classDirectory.getName().contains("ham") ? 1 : 0;
				if (classDirectory.isDirectory()) {

					for (File classifiedFile : classDirectory.listFiles()) {

						/**
						 * Read each token, and build vocabulary of the class.
						 */
						int[] inputFeatureVectorX = getInputFeatureMatrix(classifiedFile);
						int predictedYl = isClassHam(weights, inputFeatureVectorX);

						for (int l = 0; l < inputFeatureVectorX.length; l++) {
							/**
							 * This is l'th training sample, start accumulating
							 * incremental sum for each
							 */
							subtractionMatrix[l] += inputFeatureVectorX[l] * (yL - predictedYl);

						}
					}
				}
			}
			int[] featureVectorWithAllOnes = new int[tokens.size()];
			for (int i = 0; i < featureVectorWithAllOnes.length; i++) {
				featureVectorWithAllOnes[i] = 1;
			}
			/**
			 * Use MCAP regularization rule!
			 * 
			 * w[i]=w[i]+learningRate*(dw[i]-Lambda* w[i])
			 */

			// If an input has all words, its probably spam.
			weights[0] = weights[0]
					+ learningRate * ((0 - isClassHam(weights, featureVectorWithAllOnes)) - lambda * weights[0]);
			for (int i = 0; i < subtractionMatrix.length; i++) {
				weights[i + 1] = weights[i + 1] + learningRate * (subtractionMatrix[i] - lambda * weights[i + 1]);
			}
		}

		return weights;
	}

	/**
	 * This function reads input document, converts it into a input feature
	 * matrix and returns the same.
	 * 
	 * @param document
	 *            - Input document which needs to be converted into feature
	 *            matrix.
	 * @return
	 * @throws IOException
	 */
	public int[] getInputFeatureMatrix(File document) throws IOException {
		int[] inputFeatureMatrix = new int[tokens.size()];

		if (tokens.size() > 0) {
			String[] input = FileUtils.readFileToString(document).replaceAll("[^\\x20-\\x7e]", "")
					.split(SIMPLE_TOKENIZER_REGEX);

			for (String token : input) {
				if (StringUtils.isNotBlank(token) && !stopWordsList.contains(token)) {
					Token tokenFromMap = tokens.get(token);
					if (tokenFromMap != null) {
						inputFeatureMatrix[tokenFromMap
								.getPositionInMatrix()] = inputFeatureMatrix[tokenFromMap.getPositionInMatrix()] + 1;
					}
				}
			}
		} else {
			new NullPointerException("Null Trained Model : Please Train Model Before predicting class for document.");
		}
		return inputFeatureMatrix;
	}

	/**
	 * This method multiplies weights with inputs and identifies the class of
	 * the input data. Xi input vector will be of size one less than the weights
	 * vector
	 * 
	 * @param weights
	 * @param xi
	 * @return
	 */
	private int isClassHam(double[] weights, int[] xi) {
		/**
		 * e(W0+sum(Wi*Xi))/(1+e(W0+sum(Wi*Xi))) => Is the probability of Y=1.
		 * When we take log of same formula, we get following interpretation
		 * W0+Sum(WiXi) > 0 then Class of Y predicted is 1.
		 */
		Double total = weights[0];
		for (int i = 0; i < xi.length; i++) {
			total += ((double) xi[i] * weights[i + 1]);
		}
		if (total > 0) {
			return 1;
		} else {
			return 0;
		}

	}

	public double log(double n) {
		if (n == 0) {
			return 0;
		}
		return (Math.log(n));
	}

	/**
	 * This method builds vocabulary for given class.
	 * 
	 * @param text
	 * @param classOfToken
	 */
	private void buildVocabulary(String text, String classOfToken) {
		if (text != null && StringUtils.isNotBlank(text)) {
			/**
			 * Get the Token
			 */
			Token token = tokens.get(text);
			if (token == null) {
				token = new Token(text, tokenCounter++);
			}
			Integer count = token.getClassOccuranceCounts().get(classOfToken);
			/**
			 * Increment the occurance count of the token for given class.
			 */
			token.getClassOccuranceCounts().put(classOfToken, count == null ? 1 : ++count);
			/**
			 * Reinstate the token in the map.
			 */
			tokens.put(text, token);
		}
	}

	public static void main(String[] args) throws Exception {

		double[] learningRates = {0.1};
		
		String trainPath = "S:\\ML\\Source_code\\Machine_learning\\PerceptronClassification\\src\\main\\resources\\train\\";
		String testPath = "S:\\ML\\Source_code\\Machine_learning\\PerceptronClassification\\src\\main\\resources\\test\\";
		String stopWordsPath = "S:\\ML\\Source_code\\Machine_learning\\PerceptronClassification\\src\\main\\resources\\stopWords.txt";
		for(int i=1;i<200;i=i+5)
		{
		trainTestAndPrintAccuracy(i, learningRates, trainPath, testPath, stopWordsPath);
		}

	}
	/**
	 * @param numIterations
	 * @param learningRates
	 * @param trainPath
	 * @param testPath
	 * @param stopWordsPath
	 * @throws IOException
	 */
	public static void trainTestAndPrintAccuracy(int numIterations, double[] learningRates, String trainPath,
			String testPath, String stopWordsPath) throws IOException {
		for (double learningRate : learningRates) {
			System.out.print(numIterations + "\t" + new Double(learningRate).toString()+"\t");
			LogisticRegressionClassifier classifier = new LogisticRegressionClassifier ();
			classifier.initialize(null);
			double[] weights = classifier.trainModel(trainPath, numIterations,0.1, learningRate);
			System.out.print(classifier.calculateAccuracy(testPath, weights));
			  classifier = new LogisticRegressionClassifier ();
			classifier.initialize(new File(stopWordsPath));
			weights = classifier.trainModel(trainPath, numIterations,0.1, learningRate);
			System.out.print("\t" + classifier.calculateAccuracy(testPath, weights));
			System.out.println();
		}
	}
	public static void main2(String[] args) throws IOException {
		LogisticRegressionClassifier classifier = new LogisticRegressionClassifier();
		System.out.println("Please provide path to the training folder(should contain two subfolders ham & spam with input files):");
		classifier.initialize(null);
		Scanner sc = new Scanner(System.in);
		String path = sc.next();
		if (new File(path).isDirectory()) {

			System.out.println("number of Iterations:");
			int numIterations = sc.nextInt();

			System.out.println("lambda:");
			double lambda = sc.nextDouble();
			System.out.println("learningRate/Step:");
			double learningRate = sc.nextDouble();
			double[] weights = classifier.trainModel(path,numIterations, lambda, learningRate); 
			System.out.println("Prediction on Training data: ");
			classifier.calculateAccuracy(path, weights);
			System.out.println("Please provide path to Test folder on which prediction needs to be made:");
			String testPath = sc.next();
			System.out.println("Prediction on Test data: ");
			classifier.calculateAccuracy(testPath, weights);

			System.out.println("Please provide path to stopWords file:");
			String stopWordsFile = sc.next();
			classifier.initialize(new File(stopWordsFile));
			weights = classifier.trainModel(path, numIterations, lambda, learningRate);
			System.out.println("Prediction on Training data: ");
			classifier.calculateAccuracy(path, weights);
			System.out.println("Prediction on Test data: ");
			classifier.calculateAccuracy(testPath, weights);
		} else {
			System.out.println(
					"Invalid folder path. Please make sure that you share path to the directory in which ham and spam folder exist");
		}
		sc.close();
	}
}
