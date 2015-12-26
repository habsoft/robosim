package pk.com.habsoft.robosim.smoothing.views;

import pk.com.habsoft.robosim.filters.particles.BigRobot;
import pk.com.habsoft.robosim.filters.particles.internal.IRobot;
import pk.com.habsoft.robosim.smoothing.controller.Controller;
import pk.com.habsoft.robosim.smoothing.controller.PIDController;

public class PIDSimulator {

	public double tauP = 0.2, tauD = 3.0, tauI = 0.004;
	public int steerDrift = 10;// in degrees
	private double speed = 1;
	private int iter = 100;

	public double getTauP() {
		return tauP;
	}

	public void setTauP(double tauP) {
		this.tauP = tauP;
	}

	public double getTauD() {
		return tauD;
	}

	public void setTauD(double tauD) {
		this.tauD = tauD;
	}

	public double getTauI() {
		return tauI;
	}

	public void setTauI(double tauI) {
		this.tauI = tauI;
	}

	public void setSteerDrift(int steerDrift) {
		this.steerDrift = steerDrift;
	}

	public int getSteerDrift() {
		return steerDrift;
	}

	public double[][] getRefData() {
		double[][] data = new double[getIter()][2];
		for (int i = 0; i < getIter(); i++) {
			data[i][0] = i * getSpeed();
			data[i][1] = 0;
		}
		return data;
	}

	// public double[][] getPData(boolean drift) {
	//
	// IRobot robot = getRobot(drift);
	// Controller c = new PController(getTauP());
	// double[][] data = new double[iter][2];
	// for (int i = 0; i < iter; i++) {
	// double steering = c.getCommand(robot.getY());
	// robot.move(steering, speed);
	// data[i][0] = robot.getX();
	// data[i][1] = robot.getY();
	// }
	//
	// return data;
	// }
	//
	// public double[][] getPDData(boolean drift) {
	//
	// IRobot robot = getRobot(drift);
	// Controller c = new PDController(getTauP(), getTauD());
	// double[][] data = new double[iter][2];
	// for (int i = 0; i < iter; i++) {
	// double steering = c.getCommand(robot.getY());
	// robot.move(steering, speed);
	// data[i][0] = robot.getX();
	// data[i][1] = robot.getY();
	// }
	// return data;
	// }
	//
	// public double[][] getPIData(boolean drift) {
	//
	// IRobot robot = getRobot(drift);
	// Controller c = new PIController(getTauP(), getTauI());
	// double[][] data = new double[iter][2];
	// for (int i = 0; i < iter; i++) {
	// double steering = c.getCommand(robot.getY());
	// robot.move(steering, speed);
	// data[i][0] = robot.getX();
	// data[i][1] = robot.getY();
	// }
	// return data;
	// }
	//
	// public double[][] getPIDData(boolean drift) {
	//
	// IRobot robot = getRobot(drift);
	// Controller c = new PIDController(getTauP(), getTauI(), getTauD());
	// double[][] data = new double[iter][2];
	// for (int i = 0; i < iter; i++) {
	// double steering = c.getCommand(robot.getY());
	// robot.move(steering, speed);
	// data[i][0] = robot.getX();
	// data[i][1] = robot.getY();
	// }
	// return data;
	// }

	public double[][] getPData(boolean drift) {
		Controller c = new PIDController(getTauP(), 0, 0);
		return getData(c, drift);

	}

	public double[][] getDData(boolean drift) {
		Controller c = new PIDController(0, 0, getTauD());
		return getData(c, drift);

	}

	public double[][] getIData(boolean drift) {
		Controller c = new PIDController(0, getTauI(), 0);
		return getData(c, drift);

	}

	public double[][] getPDData(boolean drift) {
		Controller c = new PIDController(getTauP(), 0, getTauD());
		return getData(c, drift);

	}

	public double[][] getPIData(boolean drift) {
		Controller c = new PIDController(getTauP(), getTauI(), 0);
		return getData(c, drift);

	}

	public double[][] getPIDData(boolean drift) {
		Controller c = new PIDController(getTauP(), getTauI(), getTauD());
		return getData(c, drift);

	}

	public double[][] getData(Controller c, boolean drift) {

		IRobot robot = getRobot(drift);
		double[][] data = new double[getIter()][2];
		for (int i = 0; i < getIter(); i++) {
			double steering = c.getCommand(robot.getY());
			robot.move(steering, getSpeed());
			data[i][0] = robot.getX();
			data[i][1] = robot.getY();
		}
		return data;
	}

	private IRobot getRobot(boolean drift) {
		IRobot robot = new BigRobot(20);
		robot.setLocation(0, 1, 0);
		robot.setCheckBoundaries(false);
		if (drift)
			robot.setSteering_drift(Math.toRadians(steerDrift));		
		return robot;
	}

	public void setIter(int iter) {
		this.iter = iter;
	}

	public int getIter() {
		return iter;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getSpeed() {
		return speed;
	}

}
