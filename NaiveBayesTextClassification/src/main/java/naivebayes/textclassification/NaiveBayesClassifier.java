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
 * Hello world!
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
			stopWordsList = new HashSet(
					Arrays.asList(FileUtils.readFileToString(stopWordsFile).split(SIMPLE_TOKENIZER_REGEX)));
			System.out.println("Stopwords File found: Loaded " + stopWordsList.size() + " stop words in memory");
		}
	}

	private void trainModel(String path) throws IOException {
		File parentFolder = new File(path);
		if (parentFolder.exists()) {
			System.out.println("Training on");
			File[] classes = parentFolder.listFiles();
			for (File classDirectory : classes) {

				if (classDirectory.isDirectory()) {
					String classOfData = classDirectory.getName();
					ClassificationClass cls = new ClassificationClass(classOfData);
					System.out.println(classDirectory.getAbsolutePath());

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
								buildVocabulary(token.trim(), classOfData);
							}
						}
					}
					cls.setTokenCount(termCount);
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

			for (Token token : tokens.values()) {
				Map<String, Integer> counts = token.getClassOccuranceCounts();
				for (Map.Entry<String, Integer> entry : counts.entrySet()) {
					String classOfToken = entry.getKey();
					ClassificationClass cls = classMap.get(classOfToken);
					/**
					 * Since the exercise asks for laplace smoothing, we must
					 * add vocabulary size in the denominator
					 */
					int nonClassCounts = tokens.size() + cls.getTokenCount();
					int addOneTermCount = entry.getValue() + 1;
					token.setConditionalProbability(classOfToken, ((double) (addOneTermCount) / nonClassCounts));
				}
			}

		} else {
			System.out.println("Invalid directory structure. Please provide path of the directory that contains"
					+ " one level directory structure for each of the class.");
			System.exit(0);
		}
	}

	/**
	 * This method loops through all the files from given path and prints the
	 * accuracy of the match. The directory structure expected is same as that
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public double calculateAccuracy(String path) throws IOException {
		double accuracy = 0;
		File parentFolder = new File(path);
		System.out.println("Calculating accuracy on : " + path);
		if (parentFolder.exists()) {
			File[] classes = parentFolder.listFiles();
			int totalDocs = 0;
			int correctPredictions = 0;
			for (File classDirectory : classes) {
				System.out.println(classDirectory.getAbsolutePath());
				if (classDirectory.isDirectory()) {

					String classOfData = classDirectory.getName();

					for (File classifiedFile : classDirectory.listFiles()) {
						totalDocs++;
						String classPredicted = predictedClass(classifiedFile);
						if (classPredicted.equals(classOfData)) {
							correctPredictions++;
						} else {
							// System.out.println(classifiedFile.getName()+ " :
							// "+classPredicted);
						}
					}
				}
			}
			accuracy = 100 * (double) correctPredictions / totalDocs;
			System.out.println("Total accuracy found : " + accuracy);
		}

		return (accuracy);
	}

	public String predictedClass(File document) throws IOException {
		String[] input = FileUtils.readFileToString(document).split(SIMPLE_TOKENIZER_REGEX);
		String maximizedClass = null;
		Double maximizedScore = null;
		for (ClassificationClass cls : classMap.values()) {

			double tempScore = log(cls.getPrior());
			for (String token : input) {
				if (StringUtils.isNotBlank(token) && !stopWordsList.contains(token)) {
					Token tokenFromMap = tokens.get(token);
					if (tokenFromMap != null && tokenFromMap.getConditionalProbabilities().get(cls.getName()) != null) {
						/**
						 * Known word. use precalculated conditional
						 * probability.
						 */
						tempScore += log(tokenFromMap.getConditionalProbabilities().get(cls.getName()));
					} else {
						/**
						 * unknown word - calculate the conditional probability.
						 */
						int nonClassCounts = tokens.size() + cls.getTokenCount();
						tempScore += log((double) (1) / nonClassCounts);
					}
			

				}
			}		if (maximizedScore == null) {
				maximizedScore = tempScore;
				maximizedClass = cls.getName();
			}
			if (tempScore > maximizedScore) {
				maximizedClass = cls.getName();
				maximizedScore = tempScore;
			}
		}
		return maximizedClass;

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
			Token token = tokens.get(text);
			if (token == null) {
				token = new Token(text);
			}
			Integer count = token.getClassOccuranceCounts().get(classOfToken);
			token.getClassOccuranceCounts().put(classOfToken, count == null ? 1 : ++count);
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
