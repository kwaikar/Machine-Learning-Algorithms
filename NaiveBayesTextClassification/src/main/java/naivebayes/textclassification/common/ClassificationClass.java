package naivebayes.textclassification.common;

public class ClassificationClass {
	private String name;
	private int documentCount = 0;
	private int tokenCount = 0;
	private double prior = 0.0;

	/**
	 * @param name
	 */
	public ClassificationClass(String name) {
		super();
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClassificationClass [name=" + name + ", documentCount=" + documentCount + ", tokenCount=" + tokenCount
				+ ", prior=" + prior + "]";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the prior
	 */
	public double getPrior() {
		return prior;
	}

	/**
	 * @param prior
	 *            the prior to set
	 */
	public void setPrior(double prior) {
		this.prior = prior;
	}

	/**
	 * @return the documentCount
	 */
	public int getDocumentCount() {
		return documentCount;
	}

	/**
	 * @param documentCount
	 *            the documentCount to set
	 */
	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	/**
	 * @return the tokenCount
	 */
	public int getTokenCount() {
		return tokenCount;
	}

	/**
	 * @param tokenCount
	 *            the tokenCount to set
	 */
	public void setTokenCount(int tokenCount) {
		this.tokenCount = tokenCount;
	}

}
