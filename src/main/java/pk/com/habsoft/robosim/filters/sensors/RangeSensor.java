package pk.com.habsoft.robosim.filters.sensors;

import pk.com.habsoft.robosim.filters.core.Direction;
import pk.com.habsoft.robosim.filters.core.State;

public interface RangeSensor {

	void sense(State s, int cx, int cy, int[][] map);

	int getMaxRange();

	void setMaxRange(int maxRange);

	double getNoise();

	void setNoise(double noise);

	int getMeasurement();

	void setMeasurement(int measurement);

	Direction getDirection();

}
