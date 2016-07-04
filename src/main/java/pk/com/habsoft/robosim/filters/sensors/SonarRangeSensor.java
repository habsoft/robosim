package pk.com.habsoft.robosim.filters.sensors;

import pk.com.habsoft.robosim.filters.core.Direction;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.State;

public class SonarRangeSensor implements RangeSensor {

	Direction direction;
	int maxRange;
	double noise;
	/*
	 * Will be calculated on sense()
	 */
	int measurement;

	public SonarRangeSensor(Direction direction, int maxRange, double noise) {
		super();
		this.direction = direction;
		this.maxRange = maxRange;
		this.noise = noise;
	}

	@Override
	public void sense(State s, int cx, int cy, int[][] map) {
		measurement = 0;

		int nx = cx + direction.getDcomp()[0];
		int ny = cy + direction.getDcomp()[1];

		while (GridWorldDomain.INSTANCE.isOpen(nx, ny) && measurement < getMaxRange()) {
			nx += direction.getDcomp()[0];
			ny += direction.getDcomp()[1];
			measurement++;
		}
		// System.out.println(this);

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
	public Direction getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "Reading [Dir=" + direction.getAngle() + ", Dist=" + measurement + "]";
	}

}
