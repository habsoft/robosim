/*
 *  Player Java Client 2 - HeadingControl.java
 *  Copyright (C) 2002-2006 Radu Bogdan Rusu, Maxim Batalin
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: HeadingControl.java,v 1.3 2006/03/06 08:33:31 veedee Exp $
 *
 */
package pk.com.habsoft.robosim.smoothing;

import pk.com.habsoft.robosim.filters.particles.BigRobot;
import pk.com.habsoft.robosim.filters.particles.internal.IRobot;
import pk.com.habsoft.robosim.smoothing.controller.Controller;
import pk.com.habsoft.robosim.smoothing.controller.PIDController;

/**
 * Heading control interface for Position, Position2D and Position3D Player
 * interfaces. Uses methods from both player interfaces and PIDController.
 * 
 * @author Radu Bogdan Rusu & Marius Borodi
 */
public class HeadingControl extends PIDController {

	private IRobot device;

	/* PID coefficients */
	private int Kp = 1;
	private int Ki = 0;
	private int Kd = 0;

	private boolean stop = false;

	/* minimum and maximum admissible commands */
	private double minCommand = 1;
	private double maxCommand = 45;
	/* maximum allowed error */
	private double maxError = 0;

	public static void main(String[] args) {
		// myrobot = robot()
		// myrobot.set(0.0, 1.0, 0.0)
		// speed = 1.0 # motion distance is equal to speed (we assume time = 1)
		// N = 100
		// #
		// # Add Code Here
		// #
		// for i in range(N):
		// crosstrack_error = myrobot.y
		// steering = -param * crosstrack_error
		// myrobot = myrobot.move(steering, speed)
		// print i,myrobot,steering

		IRobot robot = new BigRobot(20);
		robot.setLocation(0, 1, 0);
		robot.setSteering_drift(Math.toRadians(10));
		robot.setCheckBoundaries(false);
		// Controller c = new PController(0.5);
		double tau_p = 0.2, tau_d = 3.0, tau_i = 0.004;
		Controller c = new PIDController(tau_p, tau_i, tau_d);
		double speed = 1;
		int iter = 10;
		for (int i = 0; i < iter; i++) {
			double steering = c.getCommand(robot.getY());
			robot.move(steering, speed);
			// System.out.println(i + " " + robot + " , " + steering);
		}

		// double D_CTE = robot.getY();
		// double I_CTE = 0;
		// for (int i = 0; i < iter; i++) {
		// double P_CTE = robot.getY();
		// I_CTE += P_CTE;
		// double diff_CTE = P_CTE - D_CTE;
		// double steering = -tau_p * P_CTE - tau_d * diff_CTE - tau_i * I_CTE;
		// robot.move(steering, speed);
		//
		// System.out.println("P " + P_CTE + " , D " + diff_CTE + " , I " +
		// I_CTE);
		// D_CTE = P_CTE;
		//
		// // System.out.println(i + " " + robot + " , " + steering);
		// }

	}

	/**
	 * Constructor for HeadingControl.
	 * 
	 * @param pd
	 *            a reference to a PlayerDevice interface (Position, Position2D
	 *            or Position3D).
	 */
	public HeadingControl(IRobot pd) {
		super(1, 0, 0);
		this.device = pd;
	}

	/**
	 * Constructor for HeadingControl.
	 * 
	 * @param pd
	 *            a reference to a PlayerDevice interface (Position, Position2D
	 *            or Position3D).
	 * @param kp
	 *            the proportional constant
	 * @param ki
	 *            the integral constant
	 * @param kd
	 *            the derivative constant
	 */
	public HeadingControl(IRobot pd, int kp, int ki, int kd) {
		super(kp, ki, kd);
		this.Kp = kp;
		this.Ki = ki;
		this.Kd = kd;
		this.device = pd;
	}

	/**
	 * Constructor for HeadingControl.
	 * 
	 * @param pd
	 *            a reference to a PlayerDevice interface (Position, Position2D
	 *            or Position3D).
	 * @param minC
	 *            minimum admissible command for the robot's motors
	 * @param maxC
	 *            maximum admissible command for the robot's motors
	 */
	public HeadingControl(IRobot pd, int minC, int maxC) {
		super(1, 0, 0);
		this.minCommand = minC;
		this.maxCommand = maxC;
		this.device = pd;
	}

	/**
	 * Constructor for HeadingControl.
	 * 
	 * @param pd
	 *            a reference to a PlayerDevice interface (Position, Position2D
	 *            or Position3D).
	 * @param minC
	 *            minimum admissible command for the robot's motors
	 * @param maxC
	 *            maximum admissible command for the robot's motors
	 * @param kp
	 *            the proportional constant
	 * @param ki
	 *            the integral constant
	 * @param kd
	 *            the derivative constant
	 */
	public HeadingControl(IRobot pd, int minC, int maxC, int kp, int ki, int kd) {
		super(kp, ki, kd);
		this.minCommand = minC;
		this.maxCommand = maxC;
		this.Kp = kp;
		this.Ki = ki;
		this.Kd = kd;
		this.device = pd;
	}

	/**
	 * Set the minimum admissible command for the robot's motors.
	 * 
	 * @param minC
	 *            minimum admissible command as an integer
	 */
	public void setMinimumCommand(double minC) {
		this.minCommand = minC;
	}

	/**
	 * Set the maximum admissible command for the robot's motors.
	 * 
	 * @param maxC
	 *            maximum admissible command as an integer
	 */
	public void setMaximumCommand(double maxC) {
		this.maxCommand = maxC;
	}

	/**
	 * Stop the robot from moving.
	 */
	public void stopRobot() {
		this.stop = true;
	}

	/**
	 * Set the maximum allowed error between the final goal and the current
	 * position. (default error is 0)
	 * 
	 * @param err
	 *            maximum allowed error as an integer
	 */
	public void setAllowedError(double err) {
		this.maxError = err;
	}

	/**
	 * Calculate and return the controller's command for the controlled system.
	 * 
	 * @param currentOutput
	 *            the current output of the system
	 * @return the new calculated command for the system
	 */
	public double getCommand(double currentOutput) {
		this.currE = this.goal - currentOutput;

		/* Angle adjustments */
		if (currE <= -180)
			currE = 360 + currE;
		else if (currE >= 180 && currE <= 360)
			currE = currE - 360;
		else if (currE > 360)
			currE = currE - 360;

		eSum += currE;

		lastE = currE;

		double Pgain = this.Kp * currE;
		double Igain = this.Ki * eSum;
		double Dgain = this.Kd * deltaE();

		return Pgain + Igain + Dgain;
	}

	/**
	 * Bound the output command to the minimum and maximum admissible commands.
	 * 
	 * @param command
	 *            command to bound
	 * @return new bounded command
	 */
	private double boundCommand(double command) {
		if (command == 0)
			return 0;
		if (command < 0) {
			if (command > -minCommand)
				command = -minCommand;
			if (command < -maxCommand)
				command = -maxCommand;
		} else {
			if (command < minCommand)
				command = minCommand;
			if (command > maxCommand)
				command = maxCommand;
		}
		return command;
	}

	/**
	 * Angle transformations, used internally.
	 * 
	 * @param angle
	 *            angle to transform
	 * @return new transformed angle
	 */
	private double transformAngle(double angle) {
		angle = angle % 360;
		if (angle < 0)
			angle = 360 + angle;
		return angle;
	}

	/**
	 * Rotate the robot on spot (differential heading) with a desired heading.
	 * 
	 * @param angle
	 *            angle for rotation
	 * @return false in case the rotation was interrupted, true otherwise
	 */
	public boolean setDiffHeading(double angle) {
		if (angle == 0)
			return true;

		stop = false;
		boolean ret = true;
		/* get the current heading */
		double currentHead = transformAngle((double) device.getOrientation());
		/* calculate the goal heading */
		double newGoal = transformAngle(currentHead + angle);

		setGoal(newGoal);

		double now = transformAngle((double) device.getOrientation());

		/* keep rotating while the goal was not reached */
		while (now != newGoal) {
			if (stop == true) {
				ret = false;
				break;
			}

			/* no point in rotating at all if we're at +/-180 */
			if (Math.abs(now - newGoal) <= 1 && newGoal == 180)
				break; /* exit if we reached our destination */

			/*
			 * in case a diff. of maxError (default 0) between angles is
			 * acceptable
			 */
			if (Math.abs(now - newGoal) <= maxError)
				break; /* exit if we reached our destination */

			/* get the current heading */
			now = transformAngle((double) device.getOrientation());

			/* get the motor command and check if within the desired limits */
			double command = getCommand(now);
			command = boundCommand(command);
			device.move(0, command);

			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
		device.move(0, 0);

		return ret;
	}

	/**
	 * Rotate the robot on spot (absolute heading) to the desired heading.
	 * 
	 * @param angle
	 *            goal angle
	 * @return false in case the rotation was interrupted, true otherwise
	 */
	public boolean setHeading(double angle) {
		/* get the current heading */
		double currentAngle = transformAngle((double) device.getOrientation());

		/* difference between the current heading and the goal heading */
		double deltaAngle = (angle - currentAngle);

		if (deltaAngle != 0) {
			if (deltaAngle <= 180 && deltaAngle > 0)
				return setDiffHeading(deltaAngle);
			else if (deltaAngle > -180)
				return setDiffHeading(-360 + deltaAngle);
			else
				return setDiffHeading(360 + deltaAngle);
		}
		return true;
	}
}
