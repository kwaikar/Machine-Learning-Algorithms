package com.decisiontree.common;

import java.util.List;

/**
 * This node represents
 * 
 * @author Kanchan Waikar Date Created : 12:01:20 AM
 *
 */
public class DecisionNode<T> {

	private List<DecisionNode<T>> children;
	private T valueChosen;
	private String name;
	private T result;
	 
	public DecisionNode(FeatureCounts<T> counts) {
		this.name= counts.getName();
		
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
	public T getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(T result) {
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
 
	
 }
