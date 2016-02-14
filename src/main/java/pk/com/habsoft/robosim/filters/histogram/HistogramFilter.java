package pk.com.habsoft.robosim.filters.histogram;

import java.text.DecimalFormat;

import pk.com.habsoft.robosim.utils.Util;

public class HistogramFilter {

	DecimalFormat df = new DecimalFormat("####0.0000");
	private double[][] p;
	private int[][] world;
	private double pHit = .8;
	private double pMiss = 1 - pHit;
	private double pMove = .8;
	private double pStay = 1 - pMove;
	private boolean cyclic = true;
	// /////////////////////////////////

	int[][] delta = { { -1, -1 }, // go up-left
			{ -1, 0 }, // go up
			{ -1, 1 }, // go up-right
			{ 0, -1 }, // go left
			{ 0, 0 }, // no move
			{ 0, 1 }, // go right
			{ 1, -1 }, // go down-left
			{ 1, 0 }, // go down
			{ 1, 1 } // go down-right
	};

	public HistogramFilter(int[][] world) {
		this.world = world;
		reset();
	}

	public void setMotionNoise(double noise) {
		this.pMove = 1 - noise;
		this.pStay = noise;
	}

	public void setSensorNoise(double noise) {
		this.pHit = 1 - noise;
		this.pMiss = noise;
	}

	public void setCyclic(boolean cyclic) {
		this.cyclic = cyclic;
	}

	public void setWorld(int[][] world) {
		this.world = world;
		reset();
	}

	public void reset() {
		p = new double[world.length][world[0].length];

		int length = world.length * world[0].length;

		for (int y = 0; y < p.length; y++) {
			for (int x = 0; x < p[y].length; x++) {
				p[y][x] = 1.0 / length;
			}
		}
	}

	public void sense(int z) {
		double sum = 0;
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[i][j] == z)
					p[i][j] *= pHit;
				else
					p[i][j] *= pMiss;
				sum += p[i][j];
			}
		}
		// if the sensor contradicts then load intial belief
		if (sum == 0) {
			reset();
		} else {
			for (int y = 0; y < p.length; y++) {
				for (int x = 0; x < p[y].length; x++) {
					p[y][x] /= sum;
				}
			}
		}
	}

	/**
	 * 
	 * @param action
	 *            ActionIndex : (0:Up , 1:Left , 2:Down , 3,Right)
	 * 
	 * 
	 *            This will update the Robot motion belief matrix.
	 */
	public void move(int action) {
		double[][] q = new double[p.length][p[0].length];
		for (int i = 0; i < q.length; i++) {
			for (int j = 0; j < q[i].length; j++) {

				if (!cyclic) {

					int xx = Math.max(0, Math.min(i + delta[action][0], q.length - 1));
					int yy = Math.max(0, Math.min(j + delta[action][1], q[0].length - 1));

					double p_total = p[i][j];
					q[i][j] += pStay * p_total;
					q[xx][yy] += pMove * p_total;

				} else {
					int xx = circle(i - delta[action][0], q.length);
					int yy = circle(j - delta[action][1], q[i].length);
					q[i][j] = pMove * p[xx][yy];// Prob of success
					q[i][j] += pStay * p[i][j];// Prob of failure
				}
			}
		}
		p = q;
		// printP();

	}

	/**
	 * 
	 * @param num
	 * @param length
	 * @return modulus (equivalent to python modulus)
	 */
	private int circle(int num, int length) {
		int newNum = num % length;
		if (newNum < 0)
			newNum += length;
		return newNum;
	}

	public String getP(int i, int j) {
		double num = 0.00;
		if (i >= 0 && i < p.length && j >= 0 && j < p[0].length) {
			num = p[i][j];
		}
		return df.format(num);
	}

	public void printP() {
		printP(p);
	}

	public void printP(double[][] p) {
		Util.printArrayP(p);
		double sum = 0;
		for (double[] d : p) {
			for (double i : d) {
				sum += i;
			}
		}
		System.out.println("   sum " + df.format(sum));

	}
}
