package pk.com.habsoft.robosim.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import org.apache.commons.math3.linear.RealMatrix;

public class Util {
	public static DecimalFormat df = new DecimalFormat("####0.##########");

	public static void printMatrix(RealMatrix m) {
		printArrayP(m.getData());
	}

	public static void printArrayP(double[][] p) {

		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p[i].length; j++) {
				System.out.print(df.format(p[i][j]));
				// System.out.print(df.format(p[i][j])+" , ");
				if (!(j + 1 == p[i].length)) {
					System.out.print(" , ");
				}
			}
			System.out.println("");
		}
		System.out.println("");

	}

	public static void printArrayP(int[][] p) {

		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p[i].length; j++) {
				System.out.print(df.format(p[i][j]));
				// System.out.print(df.format(p[i][j])+" , ");
				if (!(j + 1 == p[i].length)) {
					System.out.print(" , ");
				}
			}
			System.out.println("");
		}
		System.out.println("");

	}

	public static String round(double unrounded, int precision) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
		return df.format(rounded.doubleValue());
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
		int newValue = value % truncate;
		// to get the same result as python (%) gives
		if (newValue < 0) {
			newValue += truncate;
		}

		return newValue;
	}

	public static double findMax(double[] arr) {
		double max = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (max < arr[i]) {
				max = arr[i];
			}
		}
		return max;
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$#" + n + "s", s);
	}

	public boolean isValidValue(double value, double min, double max) {
		return value >= min && value <= max;
	}

	/**
	 * 
	 * @param mu
	 * @param sigma
	 * @param x
	 * @return
	 */
	public static double gaussian(double mu, double sigma, double x) {
		return Math.exp(-0.5 * Math.pow((x - mu), 2) / Math.pow(sigma, 2))
				/ (Math.sqrt(2 * Math.PI * Math.pow(sigma, 2)));
		// return 1 / (Math.sqrt(2. * Math.PI) * var) * Math.exp(-0.5 *
		// Math.pow((x - mu), 2) / var);
		// return Math.exp(-(Math.pow(mu - x, 2) / var / 2.0) / Math.sqrt(2.0 *
		// Math.PI * var));
	}

	public static void main(String[] args) {
		// System.out.println(findMax(arr));
		// System.out.println(Util.gaussian(10, 2, 8));
		// System.out.println(Util.gaussian(65.68305138286316, 0.01,
		// 32.240609683393075));

		// System.out.println(Util.round(34234233.42342352352352352354444, 10));
		System.out.println(Util.modulus(-9, 4, false));
		System.out.println(Util.modulus(0, 4, false));
		System.out.println(Util.modulus(1, 4, false));

	}
}
