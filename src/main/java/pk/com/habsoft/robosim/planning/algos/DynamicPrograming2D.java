package pk.com.habsoft.robosim.planning.algos;

import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.planning.internal.Path;
import pk.com.habsoft.robosim.utils.Util;

/*
 * 2D dynamic programming stochastic
 */
public class DynamicPrograming2D extends Algorithm {
	// static int[][] map = { { 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0,
	// 0, 1, 1, 0, 0 }, { 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1, 0 } };
	static int[][] map = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };

	// int[][] policy;
	// double[][] expand;
	int[][] actions;

	DiscreteWorld world;
	int initCost = 1000000;
	int collisionCost = 100;
	double sProb = 0.8;
	double fProb = (1 - sProb) / 2;

	public static void main(String[] args) {
		DiscreteWorld w = new DiscreteWorld(map);
		w.setGoalNode(map.length - 1, map[0].length - 1);
		new DynamicPrograming2D(w, false, 0.8);
	}

	private boolean allowDiagonalMotion;

	public DynamicPrograming2D(DiscreteWorld world, boolean diagonalMotion, double successProb) {
		this.world = world;
		this.allowDiagonalMotion = diagonalMotion;
		sProb = successProb;
		fProb = (1 - sProb) / 2;

		if (allowDiagonalMotion) {
			actions = deltas;
		} else {
			actions = deltas2;
		}

		policy = new int[world.getRows()][world.getColumns()];
		expand = new double[world.getRows()][world.getColumns()];

		solve();
	}

	@Override
	void solve() {
		for (int i = 0; i < world.getRows(); i++) {
			for (int j = 0; j < world.getColumns(); j++) {
				expand[i][j] = initCost;
				// if (world.isHidden(i, j)) {
				// policy[i][j] = HIDDEN;
				// } else
				if (world.isOpen(i, j))
					policy[i][j] = NOT_EXPLORED;
				else {
					policy[i][j] = BLOCK;
					blocked++;
				}
			}
		}
		// Cost of each node
		int cost = 1;
		// int incr = allowDiagonalMotion ? 1 : 2;

		boolean change = true;
		while (change) {
			change = false;
			for (int x = 0; x < world.getRows(); x++) {
				for (int y = 0; y < world.getColumns(); y++) {
					if (world.isGoal(x, y)) {
						result = "Goal Found";
						if (expand[x][y] > 0) {
							expand[x][y] = 0;
							change = true;
							if (world.isHidden(x, y)) {
								policy[x][y] = HIDDEN;
							} else if (policy[x][y] >= deltas.length) {
								policy[x][y] = Algorithm.EXPLORED;
								exploredSize++;
							}

						}
					} else if (world.isOpen(x, y)) {

						for (int k = 0; k < actions.length; k = k + 1) {
							instances++;
							double v2 = cost;
							v2 += sProb * getCost(x, y, k);
							v2 += fProb * getCost(x, y, Util.modulus(k - 1, actions.length, true));
							v2 += fProb * getCost(x, y, Util.modulus(k + 1, actions.length, true));
							if (v2 < expand[x][y]) {
								change = true;
								expand[x][y] = v2;
								if (world.isHidden(x, y)) {
									policy[x][y] = HIDDEN;
								} else if (policy[x][y] >= deltas.length) {
									exploredSize++;
									pathSize++;
								}
								policy[x][y] = allowDiagonalMotion ? k : k * 2;

							}
						}
					}
				}
			}
		}
		unExploredSize = world.getRows() * world.getColumns() - exploredSize - blocked;

		// for (double[] strings : expand) {
		// System.out.println(Arrays.toString(strings));
		// }
		// System.out.println(world.printWorld());

		// for (int i = 0; i < world.getHeight(); i++) {
		// for (int j = 0; j < world.getWidth(); j++) {
		// if (policy[i][j] < 8) {
		// System.out.print(DELTA_NAME[policy[i][j]] + " ");
		// }
		// }
		// System.out.println();
		// }

	}

	private double getCost(int x, int y, int i) {
		int x2 = x + actions[i][0];
		int y2 = y + actions[i][1];
		if (world.isOpen(x2, y2))
			return expand[x2][y2];
		return collisionCost;
	}

	@Override
	public int[][] getPolicy() {
		return policy;
	}

	@Override
	public double[][] getExpand() {
		return expand;
	}

	@Override
	public int getExploredSize() {
		return exploredSize;
	}

	@Override
	public int getUnExploredSize() {
		return unExploredSize;
	}

	@Override
	public int getPathSize() {
		return pathSize;
	}

	@Override
	public double[][] getHeuristic() {
		return new double[world.getRows()][world.getColumns()];
	}

	@Override
	public int getBlockedSize() {
		return blocked;
	}

	@Override
	public int getTotalSize() {
		return exploredSize + unExploredSize + blocked;
	}

	@Override
	public String getResult() {
		return result;
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public int getTotalInstances() {
		return instances;
	}
}
