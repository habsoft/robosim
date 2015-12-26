package pk.com.habsoft.robosim.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

	public static double[] loadImageData(String name) {
		BufferedImage bi = null;
		double[] arr = null;
		try {
			File f = new File(name);
			bi = ImageIO.read(f);
			int w = bi.getWidth(null);
			int h = bi.getHeight(null);
			arr = new double[w * h];
			System.out.println("w " + w + " : h " + h);
			bi.getRaster().getPixels(0, 0, w, h, arr);
		} catch (IOException e) {
			System.out.println("Image could not be read");
			System.exit(1);
		}
		return arr;
	}

	public static Image createImageFromArray(double[] pixels, int oldWidth, int oldHeight, int newWidth, int newHeight,
			int imgType) throws Exception {
		BufferedImage image = new BufferedImage(oldWidth, oldHeight, imgType);
		WritableRaster raster = (WritableRaster) image.getData();
		raster.setPixels(0, 0, oldWidth, oldHeight, pixels);
		image.setData(raster);
		return resize(image, newWidth, newHeight);
	}

	public static BufferedImage resize(BufferedImage image, int w, int h) {
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(w, h, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		return resizedImage;
	}
}
