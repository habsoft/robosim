package pk.com.habsoft.robosim.filters.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.ObjectClass;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.actions.ActionObserver;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;

public class SonarRangeModule implements ObjectInstance, ActionObserver {

	ObjectClass objectClass;
	String objectName;

	List<RangeSensor> sensors = new ArrayList<>();
	private double pSuccess = 0.8;

	public SonarRangeModule(ObjectClass objectClass, String objectName) {
		this.objectClass = objectClass;
		this.objectName = objectName;
		initDefaultSensors();
	}

	public SonarRangeModule(SonarRangeModule other) {
		this.objectClass = other.objectClass;
		this.objectName = other.objectName;
		this.sensors = new ArrayList<>(other.getSensors());
	}

	protected void initDefaultSensors() {
		// TODO config
		int range = 2;
		double noise = 0.1;
		this.sensors.add(new SonarRangeSensor(RobotDirection.EAST, 1, noise));
		this.sensors.add(new SonarRangeSensor(RobotDirection.NORTH, 1, noise));
		this.sensors.add(new SonarRangeSensor(RobotDirection.WEST, 1, noise));
		this.sensors.add(new SonarRangeSensor(RobotDirection.SOUTH, 1, noise));
	}

	@Override
	public String getName() {
		return objectName;
	}

	@Override
	public String getClassName() {
		return objectClass.name;
	}

	@Override
	public String getObjectDescription() {
		return this.toString();
	}

	@Override
	public void setName(String newName) {
		this.objectName = newName;
	}

	@Override
	public ObjectInstance copy() {
		return new SonarRangeModule(this);
	}

	@Override
	public int getIntValForAttribute(String attx) {
		return 0;
	}

	@Override
	public ObjectInstance setValue(String attName, int v) {
		return null;
	}

	public List<RangeSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<RangeSensor> sensors) {
		this.sensors = new ArrayList<>(sensors);
	}

	public void addNewSensor(RangeSensor sensor) {
		if (sensors.contains(sensor)) {
			throw new RuntimeException("RangeSensor already found : " + sensor);
		}
		if (this.sensors == null) {
			this.sensors = new ArrayList<>();
		}
		this.sensors.add(sensor);
	}

	public void sense(State s, int[][] map) {

		ObjectInstance robot = s.getFirstObjectOfClass(GridWorldDomain.CLASS_ROBOT);
		int rx = robot.getIntValForAttribute(GridWorldDomain.ATTX);
		int ry = robot.getIntValForAttribute(GridWorldDomain.ATTY);
		int td = robot.getIntValForAttribute(GridWorldDomain.ATT_THETA);

		// Robot Belief
		GridRobotBelief robotBelief = (GridRobotBelief) s.getFirstObjectOfClass(GridWorldDomain.CLASS_BELIEF);
		double[][][] belief = robotBelief.getBeliefMap();
		int rows = belief.length;
		int cols = belief[0].length;
		int dirs = belief[0][0].length;
		double[][][] nb = new double[rows][cols][dirs];

		// Get Actual measurement of Robot
		double[] z = getActualMeasurement(s, rx, ry, td, dirs, map);
		print("Robot ", z);

		// ////////////////////////////////////////////////

		double total = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {

				if (!GridWorldDomain.INSTANCE.isOpen(r, c)) {
					continue;
				}

				for (RobotDirection direction : RobotDirection.values()) {
					int angle = direction.getAngle();
					int d = angle / 90;

					double[] cellMeasurements = getActualMeasurement(s, r, c, angle, dirs, map);

					boolean hit = match(z, cellMeasurements);

					double prior = belief[r][c][d];

					nb[r][c][d] = prior * (hit ? pSuccess : (1 - pSuccess));
					total += nb[r][c][d];
				}

			}
		}

		double tp = 0;
		int count = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				for (int d = 0; d < dirs; d++) {
					nb[r][c][d] /= total;
					tp += nb[r][c][d];
					count++;
				}
			}
		}
		System.out.println("Total cell count : " + count);
		System.out.println("Sense : Total Prob  : " + tp);
		// Workaround
		getActualMeasurement(s, rx, ry, td, dirs, map);

		robotBelief.setBeliefMap(nb);

	}

	private void print(String msg, double[] m) {
		System.out.println(msg + " : " + Arrays.toString(m));
	}

	/**
	 * Match sensor reading with actual robot reading.
	 * 
	 * @param exp
	 *            Expected measurements
	 * @param measured
	 *            Measurement at particular cell
	 * @return True if reading exacly matched
	 */
	public boolean match(double[] exp, double[] measured) {
		return Arrays.equals(exp, measured);
	}

	private double[] getActualMeasurement(State s, int x, int y, int theta, int totalZ, int[][] map) {
		double[] z = new double[totalZ];
		for (RangeSensor rs : sensors) {
			// Rotate sensor at angle w.r.t robot angle.
			int nd = GridWorldDomain.INSTANCE.trimValue(GridWorldDomain.ATT_THETA, rs.getDirection().getAngle() + theta, true);
			rs.sense(s, x, y, theta, map);

			z[rs.getDirection().getAngle() / 90] = rs.getMeasurement();
		}

		// z = rotateAtAngle(z, theta);
		// System.out.println(String.format("x:%d, y:%d, d:%d", x, y, theta));
		// System.out.println(Arrays.toString(z));

		return z;
	}

	// private static double[] rotateAtAngle(double[] z, int theta) {
	// theta = theta / 90;
	// double[] zz = new double[z.length];
	// for (int i = 0; i < z.length; i++) {
	// zz[RoboMathUtils.modulus((theta - i), z.length, true)] = z[i];
	//
	// }
	// return zz;
	// }

	// public static void main(String[] args) {
	// double[] z = new double[] { 30, 40, 50, 60 };
	// double[] r = SonarRangeModule.rotateAtAngle(z, 90);
	//
	// System.out.println(Arrays.toString(z));
	// System.out.println(Arrays.toString(r));
	// }

	@Override
	public void actionEvent(State s, State sp, String actionName) {
		System.out.println("ActionEvent : " + actionName);
		if (!actionName.equals(GridWorldDomain.ACTION_SENSE)) {
			resetMeasurements();
		}
	}

	private void resetMeasurements() {
		for (RangeSensor rangeSensor : sensors) {
			rangeSensor.setMeasurement(0);
		}
	}

	public void setSuccessProbability(double pSuccess) {
		this.pSuccess = pSuccess;

	}

}
