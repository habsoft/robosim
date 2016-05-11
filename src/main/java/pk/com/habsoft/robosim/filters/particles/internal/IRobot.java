package pk.com.habsoft.robosim.filters.particles.internal;

import pk.com.habsoft.robosim.filters.particles.RobotType;

public interface IRobot extends Cloneable, SimulationObject {

	IRobot clone() throws CloneNotSupportedException;

	public void random();

	public double[] sense(boolean addNoise);

	public void move(double[] motions);

	public void move(double steering, double speed);

	public void setLocation(double x, double y, double orientation);

	public void setLocation(double[] measurements);

	public void setNoise(double senseNoise, double steeringNoise, double forwardNoise);

	public double measurementProb(double[] measurements);

	public double getX();

	public void setX(double x);

	public double getY();

	public void setY(double y);

	public double getOrientation();

	public void setOrientation(double orientation);

	public double getLength();

	public double getSenseNoise();

	public double getSteeringNoise();

	public double getForwardNoise();

	public RobotType getRobotType();

	public void update(IRobot obj);

	public void setLaserAngle(int laserAngle);

	public void setLaserRange(int laserRange);

	public void setBoundedVision(boolean boundedVision);

	public void setCheckBoundaries(boolean checkBoundaries);

	public double getSteeringDrift();

	public void setSteeringDrift(double steeringDrift);

}
