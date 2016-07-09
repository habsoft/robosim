package pk.com.habsoft.robosim.filters.sensors;

import pk.com.habsoft.robosim.filters.core.GridLocation;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.State;

public class SonarRangeSensor implements RangeSensor {

	RobotDirection direction;
	int maxRange;
	double noise;
	/*
	 * Will be calculated on sense()
	 */
	int measurement;

	public SonarRangeSensor(RobotDirection direction, int maxRange, double noise) {
		super();
		this.direction = direction;
		this.maxRange = maxRange;
		this.noise = noise;
	}

	@Override
	public void sense(State s, int cx, int cy, int theta, int[][] map) {
		measurement = 0;
		int nd = GridWorldDomain.INSTANCE.trimValue(GridWorldDomain.ATT_THETA, direction.getAngle() + theta, true);
		int[] dcomp = GridLocation.getGridLocation(nd);
		int nx = cx + dcomp[0];
		int ny = cy + dcomp[1];

		while (GridWorldDomain.INSTANCE.isOpen(nx, ny) && measurement < getMaxRange()) {
			nx += GridLocation.getGridLocation(direction.getAngle())[0];
			ny += GridLocation.getGridLocation(direction.getAngle())[1];
			measurement++;
		}

	}

	@Override
	public int getMaxRange() {
		return maxRange;
	}

	@Override
	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	@Override
	public double getNoise() {
		return noise;
	}

	@Override
	public void setNoise(double noise) {
		this.noise = noise;
	}

	@Override
	public int getMeasurement() {
		return measurement;
	}

	@Override
	public void setMeasurement(int measurement) {
		this.measurement = measurement;
	}

	@Override
	public RobotDirection getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "Reading [Dir=" + direction.getAngle() + ", Dist=" + measurement + "]";
	}

}
