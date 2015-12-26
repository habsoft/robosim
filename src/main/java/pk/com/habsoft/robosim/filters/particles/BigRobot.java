package pk.com.habsoft.robosim.filters.particles;

import pk.com.habsoft.robosim.filters.particles.internal.IRobot;
import pk.com.habsoft.robosim.utils.Util;

public class BigRobot extends Robot {

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
				bearing += Util.nextGaussian(0, sense_noise);
			bearing = Util.modulus(bearing, 2 * Math.PI);
			z[i] = bearing;
		}
		return z;
	}

	@Override
	public double measurement_prob(double[] measurements) {
		// calculate the correct measurement
		double[] predicted_measurements = sense(false);

		// compute errors
		double error = 1.0;
		for (int j = 0; j < predicted_measurements.length; j++) {
			double error_bearing = Math.abs(measurements[j] - predicted_measurements[j]);
			error_bearing = Util.modulus(error_bearing + Math.PI, 2.0 * Math.PI) - Math.PI;

			// update Gaussian
			double e1 = Util.gaussian(0, Math.pow(sense_noise, 2), Math.pow(error_bearing, 2));
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
		double stearing2 = Util.nextGaussian(stearing, steering_noise);
		double distance2 = Util.nextGaussian(forward, forward_noise);
		// # move, and add randomness to the motion command
		// # calculate beeta

		// add steering drift
		stearing2 += steering_drift;

		double beeta = Math.tan(stearing2) * distance2 / length;
		// # calculate global coordinates
		if (Math.abs(beeta) < 0.001) {
			x = x + (distance2 * Math.cos(orientation));
			y = y + (distance2 * Math.sin(orientation));
			orientation = orientation + beeta;
			setOrientation(orientation);
		} else {
			// Apply by-cycle modal
			double turning_radius = distance2 / beeta;
			double cx = x - (Math.sin(orientation) * turning_radius);
			double cy = y + (Math.cos(orientation) * turning_radius);

			// # calculate the local coordinates
			x = cx + (Math.sin(orientation + beeta) * turning_radius);
			y = cy - (Math.cos(orientation + beeta) * turning_radius);

			setOrientation(orientation + beeta);
		}

		setX(x);// # cyclic truncate
		setY(y);

		// # turn, and add randomness to the turning command
	}

	// tolerance=0.001
	// public void move(double[] motions, double tolerance) {
	// double stearing = motions[0];
	// double distance = motions[1];
	//
	// if (Math.abs(stearing) > MAX_STEERING_ANGLE) {
	// System.err.print("Exceeding maximum stearing angle");
	// }
	//
	// if (distance < 0) {
	// System.err.println("Robot cant move backwards");
	// }
	//
	// // apply noise
	// double stearing2 = Util.nextGaussian(stearing, steering_noise);
	// double distance2 = Util.nextGaussian(distance, forward_noise);
	//
	// // Execute motion
	// double turn = Math.tan(stearing2) * distance2 / length;
	//
	// // calculate global coordinates
	// if (Math.abs(turn) < tolerance) {
	//
	// // approximate by straight line motion

	// setX(x + (distance2 * Math.cos(orientation)));
	// setY(y + (distance2 * Math.sin(orientation)));
	// setOrientation(Util.modulus(orientation + turn, 2.0 * Math.PI));
	// } else {
	//
	// // approximate bicycle model for motion
	// double radius = distance2 / turn;
	// double cx = x - (Math.sin(orientation) * radius);
	// double cy = y + (Math.cos(orientation) * radius);
	// setOrientation(Util.modulus(orientation + turn, 2.0 * Math.PI));
	//
	// setX(cx + (Math.sin(orientation) * radius));
	// setY(cy - (Math.cos(orientation) * radius));
	// setX(Util.modulus(this.x, World.getWidth()));
	// setY(Util.modulus(this.y, World.getHeight()));
	// }
	//
	// }

}
