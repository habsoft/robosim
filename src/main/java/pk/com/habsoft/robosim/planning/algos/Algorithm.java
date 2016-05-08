package pk.com.habsoft.robosim.planning.algos;

import java.awt.Color;

import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.planning.internal.Path;

public abstract class Algorithm {

	int[][] policy;
	double[][] expand;
	Path path;
	int exploredSize;
	int pathSize = 1;
	int unExploredSize;
	int blocked;
	int instances = 1;
	String result = "Goal Not Found";

	public final static int A_STAR = 0, DFS = 1, BFS = 2, DP = 3;
	DiscreteWorld world;

	public static int UP = 0, UP_LEFT = 1, LEFT = 2, DOWN_LEFT = 3, DOWN = 4, DOWN_RIGHT = 5, RIGHT = 6, UP_RIGHT = 7,
			EXPLORED = 8, NOT_EXPLORED = 9, BLOCK = 10, HIDDEN = 11, START = 12, GOAL = 13;
	int[] actionArray = { UP, UP_LEFT, LEFT, DOWN_LEFT, DOWN, DOWN_RIGHT, RIGHT, UP_RIGHT, EXPLORED, NOT_EXPLORED,
			BLOCK, HIDDEN, START, GOAL };
	public static Color pathColor = Color.ORANGE;
	public static Color[] colors = { pathColor, pathColor, pathColor, pathColor, pathColor, pathColor, pathColor,
			pathColor, Color.MAGENTA, Color.WHITE, Color.BLACK, Color.LIGHT_GRAY, Color.RED, Color.GREEN };

	// Used for images(up,left,down,right)
	public static final String[] DELTA_NAMES = { "Up", "Up-Left", "Left", "Down-Left", "Down", "Down-Right", "Right",
			"Up-Right" };
	// public static final String[] DELTA_NAMES = { "^", "<", "v",
	// ">","Up-Left", "Up-Right", "Down-Left", "Down-Right" };;
	int[][] deltas = { { -1, 0 }, // up
			{ -1, -1 }, // up-left
			{ 0, -1 }, // left
			{ 1, -1 }, // down-left
			{ 1, 0 }, // down
			{ 1, 1 }, // down-right
			{ 0, 1 }, // right
			{ -1, 1 } // up-right
	};

	protected static final String[] DELTA_NAMES2 = { "^", "", "<", "", "v", "", ">", "" };;
	int[][] deltas2 = { { -1, 0 }, // go up
			{ 0, -1 }, // go left
			{ 1, 0 }, // go down
			{ 0, 1 } };// go right

	abstract void solve();

	public abstract int[][] getPolicy();

	public abstract double[][] getExpand();

	public abstract double[][] getHeuristic();

	public abstract int getExploredSize();

	public abstract int getUnExploredSize();

	public abstract int getBlockedSize();

	public abstract int getTotalSize();

	public abstract int getPathSize();

	public abstract int getTotalInstances();

	public abstract String getResult();

	public abstract Path getPath();

}
