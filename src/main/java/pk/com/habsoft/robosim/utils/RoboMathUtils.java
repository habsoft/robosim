package pk.com.habsoft.robosim.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * 
 * @author faisal.hameed
 *
 */
public class RoboMathUtils {

	private RoboMathUtils() {

	}

	/**
	 * 
	 * @param mean
	 * @param variance
	 * @return Gaussian with uniformally distributed
	 */
	public static double nextGaussian(double mean, double variance) {
		Random r = new Random();
		return mean + r.nextGaussian() * variance;
	}

	public static double gaussian(double mu, double sigma, double x) {
		return Math.exp(-0.5 * Math.pow((x - mu), 2) / Math.pow(sigma, 2)) / (Math.sqrt(2 * Math.PI * Math.pow(sigma, 2)));
	}

	public static double euclideanDistance(double[] m, double[] z) {
		double dist = 0;
		for (int i = 0; i < z.length; i++) {
			dist += Math.pow(z[i] - m[i], 2);
		}

		return Math.sqrt(dist);
	}

	public static double manhattanDistance(double[] m, double[] z) {
		double dist = 0;
		for (int i = 0; i < z.length; i++) {
			dist += Math.pow(z[i] - m[i], 2);
		}

		return Math.sqrt(dist);
	}

	public static String round(double unrounded, int precision) {
		DecimalFormat df = new DecimalFormat("####0.##########");
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
		return df.format(rounded.doubleValue());
	}

	/**
	 * 
	 * @param value
	 * @param truncate
	 * @return modulus value equivalent to python modulus
	 */
	public static double modulus(double value, double truncate) {
		double newValue = value % truncate;
		// to get the same result as python (%) gives
		if (newValue < 0) {
			newValue += truncate;
		}

		return newValue;
	}

	/**
	 * 
	 * @param value
	 * @param truncate
	 * @return modulus value equivalent to python modulus
	 */
	public static int modulus(int value, int truncate, boolean flag) {
		System.out.println("val ; "+value);
		int newValue = value % truncate;
		// to get the same result as python (%) gives
		while (newValue < 0) {
			newValue += truncate;
		}

		return newValue;
	}

	public static void main(String[] args) {
//		System.out.println(RoboMathUtils.gaussian(5, 0.1, 4));
		System.out.println(modulus(1_2, 360, true));
		
		System.out.println((5%5));
		System.out.println((-6%5));
		System.out.println((17%5));
		System.out.println((6%5));
		
		System.out.println(modulus(-6, 5, false));
	}

}
