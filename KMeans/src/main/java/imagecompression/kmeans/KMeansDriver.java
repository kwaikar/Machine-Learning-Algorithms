package imagecompression.kmeans;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Kmeans skeleton Driver Program - Imported from Assignment description as
 * instructued
 * 
 * Author :Vibhav Gogate The University of Texas at Dallas
 * 
 * Modified by: Kanchan Waikar - 26th March, 2016 Modifications : Very small
 * customizations related to input/ouput.
 * 
 */
public class KMeansDriver {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
			return;
		}
		try {
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			int k = Integer.parseInt(args[1]);
			BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
			File outputFile = new File(args[2]);
			ImageIO.write(kmeansJpg, "jpg", outputFile);
			System.out.println("Output file has been written to following path: " + outputFile.getAbsolutePath());

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);
		// Read rgb values from the image
		int[] rgb = new int[w * h];
		int count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[count++] = kmeansImage.getRGB(i, j);
			}
		}
		// Call kmeans algorithm: update the rgb values
		int[] compressedRGB = KmeansClusteringForImageCompression.applyKmeans(rgb, 20,128);

		// Write the new rgb values to the image
		count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				kmeansImage.setRGB(i, j, compressedRGB[count++]);
			}
		}
		return kmeansImage;
	}
}