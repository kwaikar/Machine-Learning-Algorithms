package naivebayes.textclassification.common;

import java.util.HashMap;
import java.util.Map;
/**
 * This class maintains two Maps for each instance.
 * 		1.  How many times a word has appeared for given class.
 * 		2.  Conditional Probabilities for each of the Class.
 * 
 * @author Kanchan Waikar
 * Date Created : Mar 15, 2016 - 4:08:25 PM
 *
 */
public class Token {

	private String token;
	private Map<String, Integer> classOccuranceCounts = new HashMap<String, Integer>();

	private Map<String, Double> conditionalProbabilities = new HashMap<String, Double>();

	public Token(String token) {
		super();
		this.token = token;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the classOccuranceCounts
	 */
	public Map<String, Integer> getClassOccuranceCounts() {
		return classOccuranceCounts;
	}

	/**
	 * @param classOccuranceCounts
	 *            the classOccuranceCounts to set
	 */
	public void setClassOccuranceCounts(Map<String, Integer> classOccuranceCounts) {
		this.classOccuranceCounts = classOccuranceCounts;
	}

	/**
	 * @return the conditionalProbabilities
	 */
	public Map<String, Double> getConditionalProbabilities() {
		return conditionalProbabilities;
	}

	/**
	 * @param conditionalProbabilities the conditionalProbabilities to set
	 */
	public void setConditionalProbabilities(Map<String, Double> conditionalProbabilities) {
		this.conditionalProbabilities = conditionalProbabilities;
	}

	public void setConditionalProbability(String classOfToken,Double probability)
	{
		this.conditionalProbabilities.put(classOfToken, probability);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Token [token=" + token + ", classOccuranceCounts=" + classOccuranceCounts
				+ ", conditionalProbabilities=" + conditionalProbabilities + "]";
	}

	  

}
