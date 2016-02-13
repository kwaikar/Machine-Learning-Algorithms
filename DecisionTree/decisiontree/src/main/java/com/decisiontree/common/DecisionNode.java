package com.decisiontree.common;

import java.util.LinkedList;
import java.util.List;

/**
 * This node represents
 * 
 * @author Kanchan Waikar Date Created : 12:01:20 AM
 *
 */
public class DecisionNode<T> {

	private List<DecisionNode<T>> children = new LinkedList<DecisionNode<T>>();
	private T valueChosen;
	private String name;
	private Boolean  result;
	private FeatureCounts<T> featureCount;
	private int index;
	 
	public DecisionNode(FeatureCounts<T> featureCount) {
		this.name= featureCount.getName();
		this.featureCount = featureCount;
		
	}
	/**
	 * @return the children
	 */
	public List<DecisionNode<T>> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<DecisionNode<T>> children) {
		this.children = children;
	}
	/**
	 * @return the valueChosen
	 */
	public T getValueChosen() {
		return valueChosen;
	}
	/**
	 * @param valueChosen the valueChosen to set
	 */
	public void setValueChosen(T valueChosen) {
		this.valueChosen = valueChosen;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the result
	 */
	public Boolean getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Boolean  result) {
		this.result = result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DecisionNode [children=" + children + ", valueChosen=" + valueChosen + ", name=" + name + ", result="
				+ result + "]";
	}
	/**
	 * @return the featureCount
	 */
	public FeatureCounts<T> getFeatureCount() {
		return featureCount;
	}
	/**
	 * @param featureCount the featureCount to set
	 */
	public void setFeatureCount(FeatureCounts<T> featureCount) {
		this.featureCount = featureCount;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param number the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
 
	
	
 }
