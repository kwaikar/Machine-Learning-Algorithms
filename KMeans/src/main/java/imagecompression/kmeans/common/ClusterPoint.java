package imagecompression.kmeans.common;

/**
 * This pojo is used for associating neighbour and its distance
 * 
 * @author Kanchan Waikar Date Created : Mar 26, 2016 - 2:01:44 PM
 *
 */
public class ClusterPoint {

	private Double distance;
	private int index;

	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}

	public ClusterPoint(int index, Double distance) {
		super();
		this.distance = distance;
		this.index = index;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClusterPoint [distance=" + distance + ", index=" + index + "]";
	}

}
