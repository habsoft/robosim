package pk.com.habsoft.robosim.filters.sensors;

public enum RobotDirection {

	EAST("MoveEast", "d", 0, 0), // East
	NORTH("MoveNorth", "w", 1, 90), // North
	WEST("MoveWest", "a", 2, 180), // West
	SOUTH("MoveSouth", "s", 3, 270); // South

	private String actionName;
	private String shortKey;
	private int index;
	private int angle;

	private RobotDirection(String actionName, String shortKey, int index, int angle) {
		this.actionName = actionName;
		this.shortKey = shortKey;
		this.index = index;
		this.angle = angle;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getShortKey() {
		return shortKey;
	}

	public void setShortKey(String shortKey) {
		this.shortKey = shortKey;
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
