package com.decisiontree.common;

import java.util.List;

/**
 * This node represents
 * 
 * @author Kanchan Waikar Date Created : 12:01:20 AM
 *
 */
public class DecisionNode<T> {

	private List<FeatureCounts<T>> children;
	private FeatureCounts<T> node;

	/**
	 * @return the children
	 */
	public List<FeatureCounts<T>> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<FeatureCounts<T>> children) {
		this.children = children;
	}

	/**
	 * @return the node
	 */
	public FeatureCounts<T> getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(FeatureCounts<T> node) {
		this.node = node;
	}

}
