package pk.com.habsoft.robosim.filters.kalman;

import java.text.DecimalFormat;

import pk.com.habsoft.robosim.utils.RoboMathUtils;

public class KalmanFilterSimulator {

	// simulation parameters
	int total_time;
	double measurement_variance;

	// #Data Arrays for plotting
	double[] position_measurements;
	double[] car_positions;
	double[] position_kalman;

	double[] veloc_measurement;
	double[] car_veloc;
	double[] veloc_kalman;

	double[] measurement_error;
	double[] kalman_position_error;
	double[] measurement_verror;
	double[] kalman_veloc_error;

	double[][] xx = { { 0 }, { 0 } };
	double[][] pp = { { 1000.0, 0.0 }, { 0.0, 1000.0 } };
	double[][] uu = { { 0.0 }, { 0.0 } };
	double[][] ff = { { 1.0, 1.0 }, { 0.0, 1.0 } };
	double[][] hh = { { 1.0, 0.0 } };
	double[][] rr = { { 1.0 } };

	double carSpeed = 0.5;

	KalmanFilter filter = new KalmanFilter(xx, pp, uu, ff, hh, rr);

	public static DecimalFormat df = new DecimalFormat("####0.000");

	public KalmanFilterSimulator(int total_time, double variance, double carSpeed) {
		this.total_time = total_time;
		this.measurement_variance = variance;
		this.carSpeed = carSpeed;

		position_measurements = new double[total_time];
		car_positions = new double[total_time];
		position_kalman = new double[total_time];

		veloc_measurement = new double[total_time];
		car_veloc = new double[total_time];
		veloc_kalman = new double[total_time];

		measurement_error = new double[total_time];
		kalman_position_error = new double[total_time];

		measurement_verror = new double[total_time];
		kalman_veloc_error = new double[total_time];
	}

	// #Returns car distance given of time(t)
	// #our car drives with constant speed
	public double carPos(double t) {
		return carSpeed * t;
	}

	public void simulate() {
		double lastPos = 0;
		double lastMes = 0;
		for (int t = 0; t < total_time; t++) {
			// #generate measurement and carpos
			// actual position
			double carPos = carPos(t);
			// measured position
			double measuredPos = RoboMathUtils.nextGaussian(carPos, measurement_variance);

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

			veloc_measurement[t] = mv;
			car_veloc[t] = v;
			veloc_kalman[t] = x[1][0];

			position_measurements[t] = measuredPos;
			car_positions[t] = carPos;
			position_kalman[t] = x[0][0];

			measurement_error[t] = measuredPos - carPos;
			measurement_verror[t] = mv - v;

			kalman_position_error[t] = x[0][0] - carPos;
			kalman_veloc_error[t] = x[1][0] - v;

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
		return this.position_measurements;
	}

	public double[] getCarPositions() {
		return this.car_positions;
	}

	public double[] getPositionsKalman() {
		return this.position_kalman;
	}

	public double[] getVelocityMeasurements() {
		return this.veloc_measurement;
	}

	public double[] getCarVelocities() {
		return this.car_veloc;
	}

	public double[] getVelocitiesKalman() {
		return this.veloc_kalman;
	}

	public double[] getPositionKalmanError() {
		return this.kalman_position_error;
	}

	public double[] getPositionMeasurementError() {
		return this.measurement_error;
	}

	public double[] getVelocityKalmanError() {
		return this.kalman_veloc_error;
	}

	public double[] getVelocityMeasurementError() {
		return this.measurement_verror;
	}

}
