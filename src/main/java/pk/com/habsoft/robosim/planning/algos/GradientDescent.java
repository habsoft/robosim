package pk.com.habsoft.robosim.planning.algos;

import java.util.ArrayList;
import java.util.List;

import pk.com.habsoft.robosim.planning.internal.PathNode;

public class GradientDescent {
    
    private GradientDescent() {
        
    }

	public static void main(String[] args) {
		List<PathNode> path = new ArrayList<PathNode>();
		path.add(new PathNode(0, 0));
		path.add(new PathNode(0, 1));
		path.add(new PathNode(0, 2));
		path.add(new PathNode(1, 2));
		path.add(new PathNode(2, 2));
		path.add(new PathNode(3, 2));
		path.add(new PathNode(4, 2));
		path.add(new PathNode(4, 3));
		path.add(new PathNode(4, 4));

		// List<Point> newPath = smooth(path, 0.5, 0.1, 0.000001);
		List<PathNode> newPath = smooth(path, 0.5, 0.1, 0.0000001, 10000, false);

		for (int i = 0; i < path.size(); i++) {
			System.out.println(path.get(i) + " : " + newPath.get(i));
		}

	}

	public static List<PathNode> smooth(List<PathNode> pathList, double weightData, double weightSmooth,
			double tolerance, int timeout, boolean smoothBoundries) {
		double[][] path = new double[pathList.size()][2];
		double[][] newpath = new double[pathList.size()][2];
		for (int i = 0; i < pathList.size(); i++) {
			PathNode node = pathList.get(i);
			newpath[i][0] = node.getX();
			newpath[i][1] = node.getY();
			path[i][0] = node.getX();
			path[i][1] = node.getY();
		}

		double change = tolerance;
		int turns = 0;
		while (change >= tolerance) {
			turns++;
			if (turns > timeout) {
				System.out.println("Timeout");
				break;
			}
			change = 0.0;

			// do not smooth 1st and last index
			for (int i = smoothBoundries ? 0 : 1; i < (smoothBoundries ? newpath.length : newpath.length - 1); i++) {
				for (int j = 0; j < newpath[i].length; j++) {
					double aux = newpath[i][j];
					// here we should apply simultaneous update
					double alphaValue = weightData * ((path[i][j]) - newpath[i][j]);
					double beetaValue = 0;
					if (smoothBoundries) {
						if (i < newpath.length - 1)
							beetaValue = weightSmooth * (newpath[i + 1][j] - newpath[i][j]);
					} else {
						beetaValue = weightSmooth * (newpath[i - 1][j] + newpath[i + 1][j] - (2.0 * newpath[i][j]));
					}
					newpath[i][j] += alphaValue + beetaValue;
					change += Math.abs(aux - newpath[i][j]);
				}
			}
		}

		// System.out.println("Path smoothed in Turns = " + turns);

		List<PathNode> newPathList = new ArrayList<PathNode>(pathList.size());
		for (int i = 0; i < newpath.length; i++) {
			newPathList.add(new PathNode(newpath[i][0], newpath[i][1]));
			// System.out.println(i + " " + newPathList.get(i));
		}

		return newPathList;
	}
}
