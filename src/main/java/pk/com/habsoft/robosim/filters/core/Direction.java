package pk.com.habsoft.robosim.filters.core;

public enum Direction {

	NORTH("North", 90, new int[] { 0, 1 }), // North
	SOUTH("South", 270, new int[] { 0, -1 }), // South
	EAST("East", 0, new int[] { 1, 0 }), // East
	WEST("West", 180, new int[] { -1, 0 }); // West

	private String name;
	private int angle;
	private int[] dcomp;

	Direction(String name, int angle, int[] dcomp) {
		this.name = name;
		this.angle = angle;
		this.dcomp = dcomp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int[] getDcomp() {
		return dcomp;
	}

	public void setDcomp(int[] dcomp) {
		this.dcomp = dcomp;
	}

}
