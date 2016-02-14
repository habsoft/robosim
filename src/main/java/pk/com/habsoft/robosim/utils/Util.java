package pk.com.habsoft.robosim.utils;

import java.text.DecimalFormat;

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
}
