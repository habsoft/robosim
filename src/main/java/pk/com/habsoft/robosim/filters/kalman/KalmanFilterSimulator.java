package pk.com.habsoft.robosim.filters.kalman;

import java.text.DecimalFormat;

import pk.com.habsoft.robosim.utils.RoboMathUtils;

public class KalmanFilterSimulator {

	// simulation parameters
	int totalTime;
	double measurementVariance;

	// #Data Arrays for plotting
	double[] positionMeasurements;
	double[] carPositions;
	double[] positionKalman;

	double[] velocMeasurement;
	double[] carVeloc;
	double[] velocKalman;

	double[] measurementError;
	double[] kalmanPositionError;
	double[] measurementVelocError;
	double[] kalmanVelocError;

	double[][] xx = { { 0 }, { 0 } };
	double[][] pp = { { 1000.0, 0.0 }, { 0.0, 1000.0 } };
	double[][] uu = { { 0.0 }, { 0.0 } };
	double[][] ff = { { 1.0, 1.0 }, { 0.0, 1.0 } };
	double[][] hh = { { 1.0, 0.0 } };
	double[][] rr = { { 1.0 } };

	double carSpeed = 0.5;

	KalmanFilter filter = new KalmanFilter(xx, pp, uu, ff, hh, rr);

	public static final DecimalFormat df = new DecimalFormat("####0.000");

	public KalmanFilterSimulator(int totalTime, double variance, double carSpeed) {
		this.totalTime = totalTime;
		this.measurementVariance = variance;
		this.carSpeed = carSpeed;

		positionMeasurements = new double[totalTime];
		carPositions = new double[totalTime];
		positionKalman = new double[totalTime];

		velocMeasurement = new double[totalTime];
		carVeloc = new double[totalTime];
		velocKalman = new double[totalTime];

		measurementError = new double[totalTime];
		kalmanPositionError = new double[totalTime];

		measurementVelocError = new double[totalTime];
		kalmanVelocError = new double[totalTime];
	}

	// #Returns car distance given of time(t)
	// #our car drives with constant speed
	public double carPos(double t) {
		return carSpeed * t;
	}

	public void simulate() {
		double lastPos = 0;
		double lastMes = 0;
		for (int t = 0; t < totalTime; t++) {
			// #generate measurement and carpos
			// actual position
			double carPos = carPos(t);
			// measured position
			double measuredPos = RoboMathUtils.nextGaussian(carPos, measurementVariance);

			// actual velocity
			double v = carPos - lastPos;
			// measured velocity
			double mv = measuredPos - lastMes;

			// #run Kalman Filter
			// x, P, u, F, H, R, I = kalman(t, m, x, P, u, F, H, R, I)
			double[] mm = { measuredPos };
			filter.filter(mm);

			// predicted next position
			double[][] x = filter.getX().getData();

			velocMeasurement[t] = mv;
			carVeloc[t] = v;
			velocKalman[t] = x[1][0];

			positionMeasurements[t] = measuredPos;
			carPositions[t] = carPos;
			positionKalman[t] = x[0][0];

			measurementError[t] = measuredPos - carPos;
			measurementVelocError[t] = mv - v;

			kalmanPositionError[t] = x[0][0] - carPos;
			kalmanVelocError[t] = x[1][0] - v;

			lastPos = carPos;// #assume dPos/dT ^= veloc
			lastMes = measuredPos;// #so no need for extra veloc func
			// System.out.println(car_positions[t] + " : " +
			// position_kalman[t]);
			// System.out.println(car_veloc[t] + " : " + veloc_kalman[t] + " : "
			// + veloc_measurement[t]);

		}

	}

	public static void main(String[] args) {
		KalmanFilterSimulator anim = new KalmanFilterSimulator(10, 0, 0.5);
		anim.simulate();
		System.out.println(anim.filter.getX());
	}

	public double[] getPositionMeasurements() {
		return this.positionMeasurements;
	}

	public double[] getCarPositions() {
		return this.carPositions;
	}

	public double[] getPositionsKalman() {
		return this.positionKalman;
	}

	public double[] getVelocityMeasurements() {
		return this.velocMeasurement;
	}

	public double[] getCarVelocities() {
		return this.carVeloc;
	}

	public double[] getVelocitiesKalman() {
		return this.velocKalman;
	}

	public double[] getPositionKalmanError() {
		return this.kalmanPositionError;
	}

	public double[] getPositionMeasurementError() {
		return this.measurementError;
	}

	public double[] getVelocityKalmanError() {
		return this.kalmanVelocError;
	}

	public double[] getVelocityMeasurementError() {
		return this.measurementVelocError;
	}

}
