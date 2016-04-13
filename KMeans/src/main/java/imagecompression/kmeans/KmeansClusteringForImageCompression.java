package imagecompression.kmeans;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import imagecompression.kmeans.common.ClusterPoint;

/**
 * Kmeans Algorithm Implementation with Image compression implementation
 * 
 * @author Kanchan Waikar Date Created : Mar 26, 2016 - 12:09:07 PM
 *
 */
public class KmeansClusteringForImageCompression {

	/**
	 * Update the array rgb by assigning each entry in the rgb array to its
	 * cluster center.
	 * 
	 * @param rgb
	 *            - rbg value array for each pixed
	 * @param k
	 *            - number of neighbours to be considered
	 */
	public static int[] applyKmeans(int[] rgb, int k, int numIterations) {

		int[] kcentroids = new int[k];
		for (int i = 0; i < kcentroids.length; i++) {
			kcentroids[i] = (new Random().nextInt(555555));
		}
		for (int j : kcentroids) {
			System.out.print(j + " ");
		}
		Map<Integer, List<Integer>> clusterMap = new HashMap<Integer, List<Integer>>();

		for (int iter = 0; iter < numIterations; iter++) {
			clusterMap = new HashMap<Integer, List<Integer>>();
			for (int i = 0; i < rgb.length; i++) {
				/**
				 * Minimum distance priority queue
				 */
				Queue<ClusterPoint> queueOfDistances = new PriorityQueue<ClusterPoint>(new Comparator<ClusterPoint>() {

					public int compare(ClusterPoint o1, ClusterPoint o2) {
						return o2.getDistance().compareTo(o1.getDistance());
					}
				});
				Color current = new Color(rgb[i]);

				for (int j = 0; j < k; j++) {
					Color centroid = new Color(kcentroids[j]);

					double distance = (double) Math.sqrt(Math
							.abs((centroid.getRed() - current.getRed()) ^ 2 + (centroid.getGreen() - current.getGreen())
									^ 2 + (centroid.getBlue() - current.getBlue()) ^ 2));
					queueOfDistances.add(new ClusterPoint(j, distance));
				}

				ClusterPoint minDistance = queueOfDistances.remove();
				// minDistance contains the cluster that point "i" belongs to,
				// put it in that cluster's map.

				List<Integer> list = clusterMap.get(minDistance.getIndex());
				if (list == null || list.size() == 0) {
					list = new ArrayList<Integer>();
				}
				list.add(i);
				clusterMap.put(minDistance.getIndex(), list);
			}

			
			for (Map.Entry<Integer, List<Integer>> entry : clusterMap.entrySet()) {
				int size = entry.getValue().size();
				long sumR = 0L;
				long sumG = 0L;
				long sumB = 0L;
				long sumAlpha = 0L;
				for (int clusterEntry : entry.getValue()) {
					Color currentPoint = new Color(rgb[clusterEntry]);

					sumAlpha += currentPoint.getAlpha();
					sumR += currentPoint.getRed();
					sumG += currentPoint.getGreen();
					sumB += currentPoint.getBlue();
				}
				int avgAlpha = (int) (sumAlpha / size);
				int avgR = (int) (sumR / size);
				int avgG = (int) (sumG / size);
				int avgB = (int) (sumB / size);

			//	System.out.println(avgR + ": " + avgG + " : " + avgB);
				kcentroids[entry.getKey()] = new Color(avgR, avgG, avgB, avgAlpha).getRGB();
				/*System.out.println(
						avgR + ": " + avgG + " : " + avgB + ": " + new Color(avgR, avgG, avgB, avgAlpha).getRGB());
*/
			}

			// now that clusterMap has all values present int the cluster, loop,
			// take average of their values and set kcentroid[i]=average
			// Iteration ends here.
		}

		
		for (Map.Entry<Integer, List<Integer>> entry : clusterMap.entrySet()) {
			List<Integer> list = clusterMap.get(entry.getKey());
			int centroidValue = kcentroids[entry.getKey()];
			for (Integer integer : list) {
				rgb[integer] = centroidValue;
			}
		}
		System.out.println("centroids :");
		for (int j : kcentroids) {
			System.out.print(j + " ");
		}

		return rgb;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 3; j++) {
				System.out.println(111);
			}
		System.out.println("22");
	}
}
