package pk.com.habsoft.robosim.filters.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import pk.com.habsoft.robosim.filters.particles.internal.IRobot;
import pk.com.habsoft.robosim.utils.RoboMathUtils;

public class Robot implements IRobot {
	// private BufferedImage image = null;
	private boolean checkBoundaries = true;
	protected double x;
	protected double y;
	protected double orientation;
	protected double length;
	protected double sense_noise = 0.0;// for sense function.
	protected double steering_noise = 0.0; // for move function.
	protected double forward_noise = 0.0;// for move function.
	protected double steering_drift = 0.0;// for PID controller

	protected RobotType robot_type = RobotType.PARTICLE;

	protected boolean boundedVision = false;
	protected int laserRange = 100;
	protected int laserAngleRange = 60;// in degrees

	private Random r = new Random();

	public Robot() {
		this(3);
	}

	public Robot(double length) {
		this.setLength(length);
		this.random();
	}

	public Robot(double length, RobotType type) {
		this(length);
		this.setRobotType(type);
	}

	public Robot(IRobot r) {
		this.setLength(r.getLength());
		this.setX(r.getX());
		this.setY(r.getY());
		this.setOrientation(r.getOrientation());
		this.setSenseNoise(r.getSenseNoise());
		this.setSteeringNoise(r.getSteeringNoise());
		this.setForwardNoise(r.getForwardNoise());

	}

	@Override
	public IRobot clone() throws CloneNotSupportedException {
		return (IRobot) super.clone();
	}

	/**
	 * Randomize the robot location within the world boundary
	 */
	@Override
	public void random() {
		this.setX(r.nextInt((int) (World.getWidth() - length)));
		this.setY(r.nextInt((int) (World.getHeight() - length)));
		this.setOrientation(r.nextDouble() * 2 * Math.PI);
	}

	@Override
	public void setLocation(double x, double y, double orientation) {
		this.setX(x);
		this.setY(y);
		this.setOrientation(orientation);
	}

	@Override
	public void setLocation(double[] measurements) {
		this.setLocation(measurements[0], measurements[1], measurements[2]);
	}

	@Override
	public void setNoise(double sense_noise, double steering_noise, double forward_noise) {
		this.setSenseNoise(sense_noise);
		this.setSteeringNoise(steering_noise);
		this.setForwardNoise(forward_noise);
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public void setX(double xx) {
		if (checkBoundaries)
			this.x = RoboMathUtils.modulus(xx, World.getWidth());
		else
			this.x = xx;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setY(double yy) {
		if (checkBoundaries)
			this.y = RoboMathUtils.modulus(yy, World.getHeight());
		else
			this.y = yy;
	}

	@Override
	public double getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(double orientation) {
		this.orientation = RoboMathUtils.modulus(orientation, 2 * Math.PI);
	}

	@Override
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	@Override
	public double getSenseNoise() {
		return sense_noise;
	}

	public void setSenseNoise(double sense_noise) {
		this.sense_noise = sense_noise;
	}

	@Override
	public double getSteeringNoise() {
		return steering_noise;
	}

	public void setSteeringNoise(double steering_noise) {
		this.steering_noise = steering_noise;
	}

	@Override
	public double getForwardNoise() {
		return forward_noise;
	}

	public void setForwardNoise(double forward_noise) {
		this.forward_noise = forward_noise;
	}

	@Override
	public void setSteeringDrift(double steering_drift) {
		this.steering_drift = steering_drift;
	}

	@Override
	public double getSteeringDrift() {
		return steering_drift;
	}

	public void setRobotType(RobotType robot_type) {
		this.robot_type = robot_type;
	}

	@Override
	public RobotType getRobotType() {
		return robot_type;
	}

	@Override
	public void setLaserRange(int laser_range) {
		this.laserRange = laser_range;
	}

	public int getLaserRange() {
		return laserRange;
	}

	@Override
	public void setLaserAngle(int laser_angle) {
		this.laserAngleRange = laser_angle;
	}

	public int getLaserAngle() {
		return laserAngleRange;
	}

	@Override
	public void setBoundedVision(boolean boundedVision) {
		this.boundedVision = boundedVision;
	}

	public boolean isBoundedVision() {
		return boundedVision;
	}

	@Override
	public String toString() {
		// return getRobot_type() + " [x=" + Util.round(x, 4) + ", y=" +
		// Util.round(y, 4) + ", orientation=" +
		// Util.round(Math.toDegrees(Math.abs(orientation)), 4) + "]";
		return getRobotType() + " [x=" + RoboMathUtils.round(x, 4) + ", y=" + RoboMathUtils.round(y, 4)
				+ ", orientation=" + RoboMathUtils.round(orientation, 4) + "]";
	}

	@Override
	public void update(IRobot obj) {
		this.setLocation(obj.getX(), obj.getY(), obj.getOrientation());
		this.setNoise(obj.getSenseNoise(), obj.getSteeringNoise(), obj.getForwardNoise());
	}

	// //Source
	// JSONObject source = step.getJSONObject("start_location");
	// double lat1 = Double.parseDouble(source.getString("lat"));
	// double lng1 = Double.parseDouble(source.getString("lng"));
	//
	// // destination
	// JSONObject destination = step.getJSONObject("end_location");
	// double lat2 = Double.parseDouble(destination.getString("lat"));
	// double lng2 = Double.parseDouble(destination.getString("lng"));
	//
	// double dLon = (lng2-lng1);
	// double y = Math.sin(dLon) * Math.cos(lat2);
	// double x = Math.cos(lat1)*Math.sin(lat2) -
	// Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
	// double brng = Math.toDegrees((Math.atan2(y, x)));
	// brng = (360 - ((brng + 360) % 360));

	@Override
	public void move(double steering, double speed) {
		move(new double[] { steering, speed });
	}

	@Override
	public void move(double[] motions) {
		double stearing = motions[0];
		double distance = motions[1];

		// # turn, and add randomness to the turning command
		orientation = orientation - stearing + RoboMathUtils.nextGaussian(0, steering_noise);
		setOrientation(orientation);

		// # move, and add randomness to the motion command
		double dist = distance + RoboMathUtils.nextGaussian(0, forward_noise);
		x = x + (dist * Math.cos(orientation));
		y = y + (dist * Math.sin(orientation));

		setX(x);// # cyclic truncate
		setY(y);

	}

	@Override
	public double measurementProb(double[] measurements) {
		double prob = 1.0;
		int c = 0;
		double[] myMeasurements = sense(false);
		for (int j = 0; j < measurements.length; j++) {
			if (measurements[j] != 0) {
				prob *= RoboMathUtils.gaussian(myMeasurements[j], sense_noise, measurements[j]);
				c++;
			}
		}
		if (isBoundedVision()) {
			if (c > 0) {
				// increase the probability if this particle can see more
				// landmarks
				prob = Math.pow(prob, 1.0 / c);
			} else {
				prob = 0.0;
			}
		}
		return prob;
	}

	/**
	 * 
	 * @return Calculates the Euclidean Distance of Landmarks from robot
	 */
	@Override
	public double[] sense(boolean addNoise) {
		double[] z = new double[World.getLandmark().size()];
		for (int i = 0; i < z.length; i++) {
			LandMark lm = World.getLandmark().get(i);
			double dx = (lm.getX() + World.LANDMARK_SIZE / 2) - (x + getLength() / 2);
			double dy = (lm.getY() + World.LANDMARK_SIZE / 2) - (y + getLength() / 2);
			double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			if (addNoise) {
				dist += RoboMathUtils.nextGaussian(0, sense_noise);
			}
			if (isBoundedVision()) {
				// TODO Limited angle vision
				// if robot can see within 60 degree angle w.r.t its orientation

				int ort = (int) ((Math.toDegrees(orientation) - 180) % 360) + 180;
				// in Degree
				double bearing = Math.toDegrees(Math.atan2(dy, dx));
				if (bearing < 0)
					bearing += 360;
				double diff = Math.abs(ort - bearing);
				lm.setText(Double.toString(bearing));
				// if (ort > 180)
				// bearing += 360;

				// bearing = Util.modulus((bearing), 2 * Math.PI);
				if (dist <= laserRange) {
					if (robot_type == RobotType.ROBOT) {
						System.out.println(i + "  ********************");
						System.out.println("Dx = " + dx + " , Dy = " + dy);
						System.out.println("O=" + ort + " , B=" + RoboMathUtils.round(bearing, 2) + " , D="
								+ RoboMathUtils.round(diff, 2));
						if (diff <= laserAngleRange / 2 || (360 - diff <= laserAngleRange / 2)) {
							lm.blink();
							System.out.println("LandMark " + lm.x + " : " + lm.y);
							// System.out.println("Robot " + x + " : " + y);
							z[i] = dist;
						} else {
							lm.unblink();
						}
					}
				}
				// else {
				// lm.unblink();
				// }
			} else {
				z[i] = dist;
				lm.unblink();
			}
		}
		return z;
	}

	public void setCheckBoundaries(boolean checkBoundaries) {
		this.checkBoundaries = checkBoundaries;
	}

	@Override
	public void onPaint(Graphics2D g) {
		// System.out.println(this);

		// if (getRobot_type() != RobotType.PARTICLE) {
		// AffineTransform at = new AffineTransform(); // 4. translate it to
		// // the
		// // center of the component
		// at.translate(x, y);
		// at.rotate(orientation);
		// // at.scale(0.5, 0.5);
		// at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
		// // draw the image
		// Graphics2D g2d = (Graphics2D) g;
		// g2d.drawImage(image, at, null);
		// // g2d.drawString((int) x + ":" + (int) y, (int) x, (int) y);
		// }
		int r = (int) length / 2;
		int cx = (int) x + r;
		int cy = (int) y + r;
		if (getRobotType() == RobotType.ROBOT) {
			g.setColor(Color.GREEN);
			g.fillOval((int) x, (int) y, (int) length, (int) length);

			if (isBoundedVision()) {
				int incr = 10;
				int lines = laserAngleRange / incr;
				int j = 0;
				double[] thetas = new double[lines + 1];
				for (double i = -lines / 2.0; i <= lines / 2.0; i = i + 1) {
					thetas[j] = Math.toDegrees(orientation) + (incr * i);
					thetas[j] = RoboMathUtils.modulus(Math.toRadians(thetas[j]), 2 * Math.PI);
					j++;
				}
				for (int i = 0; i < thetas.length; i++) {
					g.setColor(Color.RED);
					// int temp = i * 3;
					g.drawLine(cx, cy, (int) (cx + laserRange * Math.cos(thetas[i])),
							(int) (cy + laserRange * Math.sin(thetas[i])));
				}
			}
			g.setStroke(new BasicStroke(2));
		} else if (getRobotType() == RobotType.GHOST) {
			g.setColor(Color.RED);
			g.fillOval((int) x, (int) y, (int) length, (int) length);
			g.setStroke(new BasicStroke(2));
		} else if (getRobotType() == RobotType.PARTICLE) {
			g.setColor(Color.BLACK);
			g.fillOval((int) x, (int) y, (int) length, (int) length);
			g.setStroke(new BasicStroke(1));
		}
		// draw orientation line
		g.setColor(Color.WHITE);
		g.drawLine(cx, cy, (int) (cx + r * Math.cos(orientation)), (int) (cy + r * Math.sin(orientation)));

	}
}