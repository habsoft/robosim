package pk.com.habsoft.robosim.planning.algos;

import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.planning.internal.Path;
import pk.com.habsoft.robosim.planning.internal.PathNode;
import pk.com.habsoft.robosim.utils.Util;

/*
 * 3D dynamic programming stochastic
 */
public class DynamicPrograming3D extends Algorithm {

	static int[][] map = { { 1, 1, 1, 0, 0, 0 }, { 1, 1, 1, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0 }, { 1, 1, 1, 0, 1, 1 },
			{ 1, 1, 1, 0, 1, 1 } };

	int[][] d;
	double[][] heuristic;
	// map to keep track actions
	int[][][] policy3D;
	// map to keep track cost
	double[][][] expand3D;
	// cost of turning
	int[] cost;
	// actions for with diagonal motion
	int[] action = { -2, -1, 0, 1, 2 };
	// actions for without diagonal motion
	int[] action2 = { -1, 0, 1 };

	int initCost = 50000000;
	int collisionCost = 5000;
	double sProb = 0.7;
	double fProb = (1 - sProb) / 2;

	// char[] action_names = { 'R', 'R', '#', 'L', 'L' };
	int g = -100;
	int orient;

	DiscreteWorld world;
	Heuristic h;

	public static void main(String[] args) {
		DiscreteWorld w = new DiscreteWorld(map);
		w.setStartNode(4, 3);
		w.setGoalNode(2, 0);
		int[] cost = { 1, 1, 15 };
		new DynamicPrograming3D(w, false, cost, 0.8);

	}

	private boolean allowDiagonalMotion;

	public DynamicPrograming3D(DiscreteWorld world, boolean diagonalMotion, int[] cost, double successProb) {
		this.world = world;
		this.allowDiagonalMotion = diagonalMotion;
		this.cost = cost;
		this.orient = 0;

		sProb = successProb;
		fProb = (1 - sProb) / 2;

		path = new Path();
		if (allowDiagonalMotion) {
			d = deltas;
			this.cost = new int[cost.length + 2];
			this.cost[0] = cost[0];
			this.cost[this.cost.length - 1] = cost[cost.length - 1];
			System.arraycopy(cost, 0, this.cost, 1, cost.length);
		} else {
			d = deltas2;
			this.action = action2;
		}

		policy = new int[world.getRows()][world.getColumns()];
		expand = new double[world.getRows()][world.getColumns()];
		heuristic = new double[world.getRows()][world.getColumns()];

		policy3D = new int[d.length][world.getRows()][world.getColumns()];
		expand3D = new double[d.length][world.getRows()][world.getColumns()];

		h = new Heuristic(heuristic);

		solve();
	}

	private double getCost(int x, int y, int i) {
		int o2 = Util.modulus(i, d.length, false);
		int x2 = x + d[o2][0];
		int y2 = y + d[o2][1];
		if (world.isOpen(x2, y2))
			return expand3D[o2][x2][y2];
		return collisionCost;
	}

	@Override
	void solve() {

		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < world.getRows(); j++) {
				for (int k = 0; k < world.getColumns(); k++) {
					expand3D[i][j][k] = initCost;
					policy3D[i][j][k] = -1;
				}
			}
		}
		for (int j = 0; j < world.getRows(); j++) {
			for (int k = 0; k < world.getColumns(); k++) {
				if (world.isHidden(j, k)) {
					policy[j][k] = HIDDEN;
				} else if (world.isOpen(j, k))
					policy[j][k] = NOT_EXPLORED;
				else {
					policy[j][k] = BLOCK;
					blocked++;
				}
			}
		}
		boolean change = true;
		while (change) {
			change = false;
			for (int x = 0; x < world.getRows(); x++) {
				for (int y = 0; y < world.getColumns(); y++) {
					for (int j = 0; j < d.length; j++) {
						if (world.isGoal(x, y)) {
							if (expand3D[j][x][y] > 0) {
								expand3D[j][x][y] = 0;
								policy3D[j][x][y] = g;
								if (world.isHidden(x, y)) {
									policy[x][y] = HIDDEN;
								} else if (policy[x][y] != EXPLORED) {
									policy[x][y] = EXPLORED;
									exploredSize++;
								}
								change = true;
							}
						} else if (world.isOpen(x, y)) {
							for (int i = 0; i < action.length; i++) {
								instances++;
								double v2 = cost[i];
								v2 += sProb * getCost(x, y, j + action[i]);
								v2 += fProb * getCost(x, y, j - 1 + action[i]);
								v2 += fProb * getCost(x, y, j + 1 + action[i]);

								if (v2 < expand3D[j][x][y]) {
									change = true;
									expand3D[j][x][y] = v2;
									policy3D[j][x][y] = i;
									if (world.isHidden(x, y)) {
										policy[x][y] = HIDDEN;
									} else if (policy[x][y] != EXPLORED) {
										policy[x][y] = EXPLORED;
										exploredSize++;
									}
								}
							}
						}
					}
				}
			}
		}

		unExploredSize = world.getRows() * world.getColumns() - exploredSize - blocked;

		int x = world.getTempStart().getxLoc();
		int y = world.getTempStart().getyLoc();
		int orientation = orient;
		int o2 = orientation;

		// for (int i = 0; i < policy3D.length; i++) {
		// for (int j = 0; j < policy3D[i].length; j++) {
		// System.out.println(Arrays.toString(expand3D[i][j]));
		// }
		// System.out.println("");
		// }

		int k = 0;
		try {
			if (expand3D[orientation][x][y] < collisionCost) {
				result = "Goal Found";
				path.addFirst(new PathNode(x, y));
				while (policy3D[orientation][x][y] != g) {
					pathSize++;
					o2 = Util.modulus((orientation + action[policy3D[orientation][x][y]]), d.length, false);
					policy[x][y] = allowDiagonalMotion ? o2 : o2 * 2;
					heuristic[x][y] = (int) expand3D[orientation][x][y];
					expand[x][y] = k++;

					x = x + d[o2][0];
					y = y + d[o2][1];
					path.addFirst(new PathNode(x, y));
					orientation = o2;
				}
				heuristic[x][y] = (int) expand3D[orientation][x][y];
				expand[x][y] = k;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result = "Goal not Found";
		}

		if (pathSize == 1) {
			pathSize = 0;
		}
		// System.out.println("-------------");
		// for (int[] arr : policy) {
		// System.out.println(Arrays.toString(arr));
		// }
		// System.out.println("-------------");
		// for (char[] arr : temp) {
		// System.out.println(Arrays.toString(arr));
		// }
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
		return h.heuristic;
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
