package pk.com.habsoft.robosim.filters.histogram;

import java.text.DecimalFormat;

/**
 * The Class HistogramFilter.
 */
public class HistogramFilter {

	/** The date format. */
	private DecimalFormat df = new DecimalFormat("####0.0000");

	/** The world map represents the map of the robot in 2D space. */
	private int[][] worldMap;

	/** The belief map of robot. */
	private double[][] beliefMap;

	/** The p hit. */
	private double pHit = .8;

	/** The p miss. */
	private double sensorNoise = 1 - pHit;

	/** The p move. */
	private double pMove = .8;

	/** The p stay. */
	private double motionNoise = 1 - pMove;

	/** The cyclic. */
	private boolean cyclic = true;
	// /////////////////////////////////

	/** The delta. */
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

	/**
	 * Instantiates a new histogram filter.
	 *
	 * @param world
	 *            the world
	 */
	public HistogramFilter(int[][] world) {
		this.worldMap = world;
		resetBelief();
	}

	/**
	 * Sets the motion noise.
	 *
	 * @param motionNoise
	 *            the new motion noise
	 */
	public void setMotionNoise(double motionNoise) {
		this.pMove = 1 - motionNoise;
		this.motionNoise = motionNoise;
	}

	/**
	 * Sets the sensor noise.
	 *
	 * @param sensorNoise
	 *            the new sensor noise
	 */
	public void setSensorNoise(double sensorNoise) {
		this.pHit = 1 - sensorNoise;
		this.sensorNoise = sensorNoise;
	}

	/**
	 * Sets the cyclic.
	 *
	 * @param cyclic
	 *            the new cyclic
	 */
	public void setCyclic(boolean cyclic) {
		this.cyclic = cyclic;
	}

	/**
	 * Sets the world.
	 *
	 * @param world
	 *            the new world
	 */
	public void setWorld(int[][] world) {
		this.worldMap = world;
		resetBelief();
	}

	/**
	 * Reset robot belief map with uniform probability.
	 */
	public void resetBelief() {
		beliefMap = new double[worldMap.length][worldMap[0].length];

		int length = worldMap.length * worldMap[0].length;

		for (int y = 0; y < beliefMap.length; y++) {
			for (int x = 0; x < beliefMap[y].length; x++) {
				beliefMap[y][x] = 1.0 / length;
			}
		}
	}

	/**
	 * Perform Sensor update by applying Bayesian Theorem.
	 *
	 * @param measurement
	 *            the measurement of robot sensor (color value)
	 */
	public void sense(int measurement) {
		double sum = 0;
		for (int i = 0; i < worldMap.length; i++) {
			for (int j = 0; j < worldMap[i].length; j++) {
				if (worldMap[i][j] == measurement)
					beliefMap[i][j] *= pHit;
				else
					beliefMap[i][j] *= sensorNoise;
				sum += beliefMap[i][j];
			}
		}

		// Perform probability normalization
		if (Double.doubleToRawLongBits(sum) != 0) {
			for (int y = 0; y < beliefMap.length; y++) {
				for (int x = 0; x < beliefMap[y].length; x++) {
					beliefMap[y][x] /= sum;
				}
			}
		} else {
			// if the sensor contradicts then load intial belief
			// FIXME it should not get here.
			resetBelief();
		}
	}

	/**
	 * Perform move action by applying Total Probability theorem..
	 *
	 * @param action
	 *            This will update the Robot motion belief matrix.
	 */
	public void move(int action) {
		double[][] q = new double[beliefMap.length][beliefMap[0].length];
		for (int i = 0; i < q.length; i++) {
			for (int j = 0; j < q[i].length; j++) {

				// FIXME formulae need to be reviewed
				if (cyclic) {

					// Compute previous location of robot in cyclic word. Since
					// world is cyclic so we need to trim the coordinates.
					int xx = trim(i - delta[action][0], q.length);
					int yy = trim(j - delta[action][1], q[i].length);
					q[i][j] = pMove * beliefMap[xx][yy];// Prob of success
					q[i][j] += motionNoise * beliefMap[i][j];// Prob of failure

				} else {
					// Previous location of robot in bounded word
					int xx = Math.max(0, Math.min(i + delta[action][0], q.length - 1));
					int yy = Math.max(0, Math.min(j + delta[action][1], q[0].length - 1));

					double currentProbability = beliefMap[i][j];
					// Probability of staying in the same point
					q[i][j] += motionNoise * currentProbability;
					// Probability of moving to a new point
					q[xx][yy] += pMove * currentProbability;
				}
			}
		}
		beliefMap = q;

	}

	/**
	 * Circle.
	 *
	 * @param num
	 *            the num
	 * @param length
	 *            the length
	 * @return modulus (equivalent to python modulus)
	 */
	private int trim(int num, int length) {
		int newNum = num % length;
		if (newNum < 0)
			newNum += length;
		return newNum;
	}

	/**
	 * Gets the probability at.
	 *
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @return the probability at
	 */
	public String getProbabilityAt(int x, int y) {
		double num = 0.00;
		if (x >= 0 && x < beliefMap.length && y >= 0 && y < beliefMap[0].length) {
			num = beliefMap[x][y];
		}
		return df.format(num);
	}
}
