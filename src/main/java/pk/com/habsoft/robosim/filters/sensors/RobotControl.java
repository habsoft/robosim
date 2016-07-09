package pk.com.habsoft.robosim.filters.sensors;

public enum RobotControl {

	MOVE_FORWARD("MoveForward", "w", 1, 0), // Forward
	MOVE_BACKWARD("MoveBackward", "s", -1, 0), // Backward
	TURN_RIGHT("TurnRight", "d", 0, -90), // Right
	TURN_LEFT("TurnLeft", "a", 0, 90); // Left

	private String actionName;
	private String shortKey;
	private int distance;
	private int angle;

	private RobotControl(String actionName, String shortKey, int distance, int angle) {
		this.actionName = actionName;
		this.shortKey = shortKey;
		this.distance = distance;
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

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

}
