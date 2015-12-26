package pk.com.habsoft.robosim.planning.algos;

public class Heuristic {
	public final static int NONE = 0, EUCLIDEAN = 1, EUCLIDEAN_MULTIPLY = 2, EUCLIDEAN_SQUARED = 3, MANHATTAN = 4,
			CHEBYSHEV = 5;
	public final static String[] HURISTIC_NAMES = { "None", "Euclidean Diatance(+)", "Euclidean Diatance(*)",
			"Euclidean Diatance Squared", "Manhattan Distance", "Chebyshev Distance" };
	public final static String[] HURISTIC_FUNCTIONS = { "h(n)=0", "h(n)=sqrt((x - gX)^2 + (y - gY)^2))",
			"h(n)=sqrt((x - gX)^2 * (y - gY)^2))", "h(n)=(x - gX)^2 + (y - gY)^2)", "h(n)=abs(x - gX) + abs(y - gY))",
			"h(n)=max(abs(x - gX),abs(y - gY)))" };
	double[][] heuristic;
	private int goalX, goalY;
	private int heuristicType;

	public Heuristic(double[][] heuristics) {
		this.heuristic = heuristics.clone();
	}

	public Heuristic(int rows, int cols, int goalX, int goalY, int heuristicType) {
		heuristic = new double[rows][cols];
		this.goalX = goalX;
		this.goalY = goalY;
		this.heuristicType = heuristicType;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				heuristic[i][j] = getHeuristic(i, j, true);
			}
		}
		// System.out.println("Euclidean Distance Heuristics");
		// for (int[] a : heuristic) {
		// System.out.println(Arrays.toString(a));
		// }
	}

	/**
	 * Used to build array of heuristic
	 */
	private double getHeuristic(int x, int y, boolean flag) {
		double h = 0;
		if (heuristicType == NONE) {
			h = 0;
		} else if (heuristicType == EUCLIDEAN) {
			h = Math.sqrt((Math.pow(x - goalX, 2) + Math.pow(y - goalY, 2)));
		} else if (heuristicType == EUCLIDEAN_MULTIPLY) {
			h = Math.sqrt((Math.pow(x - goalX, 2) * Math.pow(y - goalY, 2)));
		} else if (heuristicType == EUCLIDEAN_SQUARED) {
			h = (Math.pow(x - goalX, 2) + Math.pow(y - goalY, 2));
		} else if (heuristicType == MANHATTAN) {
			h = Math.abs(x - goalX) + Math.abs(y - goalY);
		} else if (heuristicType == CHEBYSHEV) {
			h = Math.max(Math.abs(x - goalX), Math.abs(y - goalY));
		}
		return h;
	}

	public double getHeuristic(int x, int y) {
		return heuristic[x][y];
	}

}
