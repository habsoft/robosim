package pk.com.habsoft.robosim.filters.sensors;

public enum RobotDirection {

	NORTH("MoveNorth", "w", 0, new int[] { 0, 1 }), // North
	SOUTH("MoveSouth", "s", 1, new int[] { 0, -1 }), // South
	EAST("MoveEast", "d", 2, new int[] { 1, 0 }), // East
	WEST("MoveWest", "a", 3, new int[] { -1, 0 }); // West

	private String actionName;
	private String shortKey;
	private int index;
	private int[] dcomp;

	private RobotDirection(String actionName, String shortKey, int index, int[] dcomp) {
		this.actionName = actionName;
		this.shortKey = shortKey;
		this.index = index;
		this.dcomp = dcomp;
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

	public int[] getDcomp() {
		return dcomp;
	}

	public void setDcomp(int[] dcomp) {
		this.dcomp = dcomp;
	}

}
