package pk.com.habsoft.robosim.filters.sensors;

public enum RobotDirection {

	EAST(0, 0), // East
	NORTH(1, 90), // North
	WEST(2, 180), // West
	SOUTH(3, 270); // South

	private int index;
	private int angle;

	private RobotDirection(int index, int angle) {
		this.index = index;
		this.angle = angle;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

}
