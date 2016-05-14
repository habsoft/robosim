package pk.com.habsoft.robosim.filters.particles;

import pk.com.habsoft.robosim.filters.particles.internal.IRobot;
import pk.com.habsoft.robosim.utils.RoboMathUtils;

public class BigRobot extends Robot {

    double MAX_STEERING_ANGLE = Math.PI / 4.0;
    
	public BigRobot() {
		super();
	}

	public BigRobot(double length) {
		super(length);
	}

	public BigRobot(double length, RobotType type) {
		super(length, type);

	}

	public BigRobot(IRobot r) {
		super(r);

	}

	@Override
	public double[] sense(boolean addNoise) {
		double[] z = new double[World.getLandmark().size()];
		for (int i = 0; i < z.length; i++) {
			LandMark landmark = World.getLandmark().get(i);
			double dx = landmark.getX() - this.x;
			double dy = landmark.getY() - this.y;
			double bearing = Math.atan2(dy, dx) - this.orientation;
			if (addNoise)
				bearing += RoboMathUtils.nextGaussian(0, senseNoise);
			bearing = RoboMathUtils.modulus(bearing, 2 * Math.PI);
			z[i] = bearing;
		}
		return z;
	}

	@Override
	public double measurementProb(double[] measurements) {
		// calculate the correct measurement
		double[] predictedMeasurements = sense(false);

		// compute errors
		double error = 1.0;
		for (int j = 0; j < predictedMeasurements.length; j++) {
			double errorBearing = Math.abs(measurements[j] - predictedMeasurements[j]);
			errorBearing = RoboMathUtils.modulus(errorBearing + Math.PI, 2.0 * Math.PI) - Math.PI;

			// update Gaussian
			double e1 = RoboMathUtils.gaussian(0, Math.pow(senseNoise, 2), Math.pow(errorBearing, 2));
			error *= e1;
			// System.out.println("Error bearing " + error_bearing +
			// " ; ERROR = " + error);
		}
		return error;
	}

	@Override
	public void move(double steering, double speed) {
		move(new double[] { steering, speed });
	}

	@Override
	public void move(double[] motions) {
		double stearing = motions[0];
		double forward = motions[1];
		if (stearing > MAX_STEERING_ANGLE) {
			// System.err.println("Turning angle is greater than maximum");
			stearing = MAX_STEERING_ANGLE;
		}
		if (stearing < -MAX_STEERING_ANGLE) {
			// System.err.println("Turning angle is greater than maximum");
			stearing = -MAX_STEERING_ANGLE;
		}
		// if (forward < 0) {
		// // raise ValueError, 'Robot cant move backwards'
		// System.err.println("Robot cant move backwards");
		// }
		double stearing2 = RoboMathUtils.nextGaussian(stearing, steeringNoise);
		double distance2 = RoboMathUtils.nextGaussian(forward, forwardNoise);
		// # move, and add randomness to the motion command
		// # calculate beeta

		// add steering drift
		stearing2 += steeringDrift;

		double beeta = Math.tan(stearing2) * distance2 / length;
		// # calculate global coordinates
		if (Math.abs(beeta) < 0.001) {
			x = x + (distance2 * Math.cos(orientation));
			y = y + (distance2 * Math.sin(orientation));
			orientation = orientation + beeta;
			setOrientation(orientation);
		} else {
			// Apply by-cycle modal
			double turningRadius = distance2 / beeta;
			double cx = x - (Math.sin(orientation) * turningRadius);
			double cy = y + (Math.cos(orientation) * turningRadius);

			// # calculate the local coordinates
			x = cx + (Math.sin(orientation + beeta) * turningRadius);
			y = cy - (Math.cos(orientation + beeta) * turningRadius);

			setOrientation(orientation + beeta);
		}

		setX(x);// # cyclic truncate
		setY(y);

		// # turn, and add randomness to the turning command
	}

}
