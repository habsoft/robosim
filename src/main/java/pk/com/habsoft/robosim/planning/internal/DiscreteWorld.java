package pk.com.habsoft.robosim.planning.internal;

public class DiscreteWorld {
	public final static int OPEN = 0, HIDDEN = 1, BLOCK = 2;

	private int columns, rows;
	private int[][] grid;
	WorldNode start, tempStart, goal;

	public DiscreteWorld(int columns, int rows) {
		this.rows = rows;
		this.columns = columns;
		grid = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			grid[i] = new int[columns];
		}

		start = new WorldNode(0, 0);
		tempStart = new WorldNode(0, 0);
		// grid[0][0] = OPEN;
		goal = new WorldNode(rows - 1, columns - 1);
		// grid[height - 1][width - 1] = OPEN;
	}

	public DiscreteWorld(int[][] world) {
		grid = world.clone();
		rows = grid.length;
		columns = grid[0].length;

		start = new WorldNode(0, 0);
		tempStart = new WorldNode(0, 0);
		// grid[0][0] = OPEN;
		goal = new WorldNode(rows - 1, columns - 1);
		// grid[height - 1][width - 1] = OPEN;
	}

	public String printWorld() {
		String temp = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (i == start.getxLoc() && j == start.getyLoc()) {
					temp += " S ";
				} else if (i == goal.getxLoc() && j == goal.getyLoc()) {
					temp += " G ";
				} else if (grid[i][j] == OPEN) {
					temp += " 0 ";
				} else if (grid[i][j] == BLOCK) {
					temp += " 1 ";
				} else if (grid[i][j] == HIDDEN) {
					temp += " 2 ";
				}
			}
			temp += "\n";
		}
		return temp;
	}

	public void setStatus(int xLoc, int yLoc, int status) {
		if (xLoc >= 0 && xLoc < rows && yLoc >= 0 && yLoc < columns) {
			grid[xLoc][yLoc] = status;
		} else {
			System.out.println("SetStatus = " + xLoc + " : " + yLoc + "  is out of bound.");
		}
	}

	public void setStartNode(int xLoc, int yLoc) {
		if (xLoc >= 0 && xLoc < rows && yLoc >= 0 && yLoc < columns) {
			start.setxLoc(xLoc);
			start.setyLoc(yLoc);
			tempStart.setxLoc(xLoc);
			tempStart.setyLoc(yLoc);
			// grid[xLoc][yLoc] = OPEN;
		} else {
			System.out.println(xLoc + " : " + yLoc + "  is out of bound.Using default start location.");
			System.out.println(start);
		}
	}

	public void setTempStartNode(int xLoc, int yLoc) {
		if (xLoc >= 0 && xLoc < rows && yLoc >= 0 && yLoc < columns) {
			tempStart.setxLoc(xLoc);
			tempStart.setyLoc(yLoc);
			// grid[xLoc][yLoc] = OPEN;
		} else {
			System.out.println(xLoc + " : " + yLoc + "  is out of bound.Using default start location.");
			System.out.println(start);
		}
	}

	public void setGoalNode(int xLoc, int yLoc) {
		if (xLoc >= 0 && xLoc < rows && yLoc >= 0 && yLoc < columns) {
			goal.setxLoc(xLoc);
			goal.setyLoc(yLoc);
			// grid[xLoc][yLoc] = OPEN;
		} else {
			System.out.println(xLoc + " : " + yLoc + "  is out of bound.Using default goal location.");
			System.out.println(goal);
		}
	}

	// public boolean isStart(WorldState node) {
	// return start.equals(node);
	// }
	//
	// public boolean isStart(int xLoc, int yLoc) {
	// return (start.getxLoc() == xLoc && start.getyLoc() == yLoc);
	// }

	public boolean isGoal(WorldNode node) {
		return goal.equals(node);
	}

	public boolean isGoal(int xLoc, int yLoc) {
		return (goal.getxLoc() == xLoc && goal.getyLoc() == yLoc);
	}

	public boolean isStart(WorldNode node) {
		return start.equals(node);
	}

	public boolean isStart(int xLoc, int yLoc) {
		return (start.getxLoc() == xLoc && start.getyLoc() == yLoc);
	}

	public boolean isOpen(WorldNode node) {
		try {
			return (grid[node.getxLoc()][node.getyLoc()] == OPEN || grid[node.getxLoc()][node.getyLoc()] == HIDDEN);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isOpen(int x, int y) {
		try {
			return (grid[x][y] == OPEN || grid[x][y] == HIDDEN);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isHidden(int x, int y) {
		try {
			return (grid[x][y] == HIDDEN);
		} catch (Exception e) {
			return false;
		}
	}

	public WorldNode getStart() {
		return start;
	}

	public WorldNode getTempStart() {
		return tempStart;
	}

	public WorldNode getGoal() {
		return goal;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public int[][] getGrid() {
		return grid;
	}

	public void setGrid(int[][] world) {
		try {
			grid = world.clone();
			rows = grid.length;
			columns = grid[0].length;

			start = new WorldNode(0, 0);
			tempStart = new WorldNode(0, 0);
			goal = new WorldNode(rows - 1, columns - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// int[][] world = { { 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 1, 0, 0 }, { 0, 0,
		// 0, 1, 0, 0 }, { 0, 0, 1, 1, 0, 0 }, { 0, 0, 1, 0, 0, 0 } };

		DiscreteWorld w = new DiscreteWorld(5, 4);
		// World w = new World(world);
		System.out.println(w.printWorld());
		// System.out.println(w.g);

	}
}
