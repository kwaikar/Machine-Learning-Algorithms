package naivebayes.textclassification;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import naivebayes.textclassification.common.ClassificationClass;
import naivebayes.textclassification.common.Token;

/**
 * This implementation of Naive Bayes Text Classifier Uses laplace smoothing and
 * also uses stop words list in order see whether prediction is improving or
 * not.
 * 
 * @author Kanchan Waikar Date Created : Mar 15, 2016 - 6:09:12 PM
 *
 */
public class NaiveBayesClassifier {
	private static final String SIMPLE_TOKENIZER_REGEX = "( |\t|\r|\n|\f)";
	private Set<String> stopWordsList = new HashSet<String>();

	Map<String, Token> tokens = null;
	private Map<String, ClassificationClass> classMap = null;
	private Integer totalNumDocs = 0;

	/**
	 * This method initializes Naive Bayes classifier with an optional stopwords
	 * file
	 * 
	 * @param stopWordsFile
	 * @throws IOException
	 */
	private void initialize(File stopWordsFile) throws IOException {
		totalNumDocs = 0;
		tokens = new HashMap<String, Token>();
		classMap = new HashMap<String, ClassificationClass>();
		if (stopWordsFile != null && stopWordsFile.exists()) {
			stopWordsList = new HashSet<String>(
					Arrays.asList(FileUtils.readFileToString(stopWordsFile).split(SIMPLE_TOKENIZER_REGEX)));
			System.out.println("Stopwords File found: Loaded " + stopWordsList.size() + " stop words in memory");
		}
	}

	/**
	 * Accepts input Directory path and trains a Naive Bayes In memory model for
	 * doing Text classification.
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void trainModel(String path) throws IOException {
		File parentFolder = new File(path);
		if (parentFolder.exists()) {
			System.out.println("Training on");
			File[] classes = parentFolder.listFiles();
			for (File classDirectory : classes) {

				if (classDirectory.isDirectory()) {
					String classOfData = classDirectory.getName();
					ClassificationClass cls = new ClassificationClass(classOfData);
					System.out.println("\t" + classDirectory.getAbsolutePath());

					totalNumDocs += classDirectory.listFiles().length;
					cls.setDocumentCount(classDirectory.listFiles().length);
					int termCount = 0;
					for (File classifiedFile : classDirectory.listFiles()) {
						/**
						 * Read each token, and build vocabulary of the class.
						 */
						String[] input = FileUtils.readFileToString(classifiedFile).split(SIMPLE_TOKENIZER_REGEX);
						for (String token : input) {
							if (StringUtils.isNotBlank(token) && !stopWordsList.contains(token)) {
								termCount++;
								/**
								 * Build token counts Map.
								 */
								buildVocabulary(token.trim(), classOfData);
							}
						}
					}
					cls.setTokenCount(termCount);
					/**
					 * Maintain aggregated Class level counts.
					 */
					classMap.put(classOfData, cls);

				} else {
					System.out.println("Invalid directory structure. Please provide path of the directory that contains"
							+ " one level directory structure for each of the class.");
					System.exit(0);
				}
			}

			/**
			 * Calculate Priors for each of the classes.
			 */

			for (ClassificationClass cls : classMap.values()) {
				cls.setPrior(((double) cls.getDocumentCount() / totalNumDocs));
			}
			/**
			 * Now that data pre-processing has happened, lets compute
			 * conditional probability for each of the Token found.
			 */

			setConditionalProbabilitiesForTokens(tokens);

		} else {
			System.out.println("Invalid directory structure. Please provide path of the directory that contains"
					+ " one level directory structure for each of the class.");
			System.exit(0);
		}
	}

	/**
	 * This method sets conditional probabilities on input token map.
	 * 
	 * @param tokens
	 */
	public void setConditionalProbabilitiesForTokens(Map<String, Token> tokens) {
		for (Token token : tokens.values()) {
			Map<String, Integer> counts = token.getClassOccuranceCounts();
			for (Map.Entry<String, Integer> entry : counts.entrySet()) {
				String classOfToken = entry.getKey();
				ClassificationClass cls = classMap.get(classOfToken);

				/**
				 * Numerator is number of occurrences of the token and one added
				 * to account for laplace smoonthing.
				 */
				int addOneTermCount = entry.getValue() + 1;
				/**
				 * Since the exercise asks for Laplace smoothing, we must add
				 * vocabulary size in the denominator
				 */

				int nonClassCounts = tokens.size() + cls.getTokenCount();
				token.setConditionalProbability(classOfToken, ((double) (addOneTermCount) / nonClassCounts));
			}
		}
	}

	/**
	 * This method loops through all the files from given path and prints the
	 * accuracy of the match. The directory structure expected is same as that
	 * 
	 * @param path - Meta Path
	 * 		Input is expected to have the directory structure  {path}/{className}/file.txt
	 * @return - Accuracy of the data on which prediction is being tested.
	 * @throws IOException
	 */
	public double calculateAccuracy(String path) throws IOException {
		double accuracy = 0;
		File parentFolder = new File(path);
		if (parentFolder.exists()) {
			File[] classes = parentFolder.listFiles();
			int totalDocs = 0;
			int correctPredictions = 0;
			/**
			 * Loop through all the available directories - here each directory name is expected to be class name.
			 */
			for (File classDirectory : classes) {
				System.out.println("\t" + classDirectory.getAbsolutePath());
				if (classDirectory.isDirectory()) {

					/**
					 * get Class Name, predict class on each of the file.
					 */
					String classOfData = classDirectory.getName();
					for (File classifiedFile : classDirectory.listFiles()) {
						totalDocs++;
						String classPredicted = predictClass(classifiedFile);
						/**
						 * Verify if both the classes are matching or not.
						 */
						if (classPredicted.equals(classOfData)) {
							correctPredictions++;
						}
					}
				}
			}
			accuracy = 100 * (double) correctPredictions / totalDocs;
			System.out.println("Total accuracy found : " + accuracy);
		}

		return (accuracy);
	}

	/**
	 * This method needs to be called only on trained model. This method
	 * predicts the Class of input document.
	 * 
	 * @param document
	 * @return
	 * @throws IOException
	 */
	public String predictClass(File document) throws IOException {
		if (classMap.size() > 0 && tokens.size() > 0) {
			String[] input = FileUtils.readFileToString(document).split(SIMPLE_TOKENIZER_REGEX);
			String maximizedClass = null;
			Double maximizedScore = null;
			for (ClassificationClass cls : classMap.values()) {

				/**
				 * Initialize tempScore with Class Prior.
				 */
				double tempScore = log(cls.getPrior());
				for (String token : input) {
					if (StringUtils.isNotBlank(token) && !stopWordsList.contains(token)) {
						Token tokenFromMap = tokens.get(token);
						if (tokenFromMap != null
								&& tokenFromMap.getConditionalProbabilities().get(cls.getName()) != null) {
							/**
							 * Known word. use precalculated conditional
							 * probability - Likelihood probability
							 */
							tempScore += log(tokenFromMap.getConditionalProbabilities().get(cls.getName()));
						} else {
							/**
							 * unknown word - calculate the conditional
							 * probability.
							 */
							int nonClassCounts = tokens.size() + cls.getTokenCount();
							tempScore += log((double) (1) / nonClassCounts);
						}

					}
				}
				if (maximizedScore == null) {
					maximizedScore = tempScore;
					maximizedClass = cls.getName();
				}
				if (tempScore > maximizedScore) {
					maximizedClass = cls.getName();
					maximizedScore = tempScore;
				}
			}
			return maximizedClass;
		} else {
			new NullPointerException("Null Trained Model : Please Train Model Before predicting class for document.");
			return null;
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
				token = new Token(text);
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

	public static void main(String[] args) throws IOException {
		NaiveBayesClassifier classifier = new NaiveBayesClassifier();

		classifier.initialize(null);
		classifier.trainModel(classifier.getClass().getResource("/train").getFile());
		System.out.println("Prediction on Training data: ");
		classifier.calculateAccuracy(classifier.getClass().getResource("/train").getFile());
		System.out.println("Prediction on Test data: ");
		classifier.calculateAccuracy(classifier.getClass().getResource("/test").getFile());

		classifier.initialize(new File(classifier.getClass().getResource("/stopwords.txt").getFile()));
		classifier.trainModel(classifier.getClass().getResource("/train").getFile());
		System.out.println("Prediction on Training data: ");
		classifier.calculateAccuracy(classifier.getClass().getResource("/train").getFile());
		System.out.println("Prediction on Test data: ");
		classifier.calculateAccuracy(classifier.getClass().getResource("/test").getFile());

	}
}
