package pk.com.habsoft.robosim.filters.core;

public class GridLocation {

	private GridLocation() {

	}

	public static int[] getGridLocation(int theta) {
		return getGridLocation(1, theta);
	}

	public static int[] getGridLocation(int distance, int theta) {
		if (distance == 0) {
			return new int[] { 0, 0 };
		} else {
			switch (theta) {
			case 0:
				return new int[] { distance * 1, 0 };
			case 45:
				return new int[] { distance * 1, distance * 1 };
			case 90:
				return new int[] { 0, distance * 1 };
			case 135:
				return new int[] { distance * (-1), distance * 1 };
			case 180:
				return new int[] { distance * (-1), 0 };
			case 225:
				return new int[] { distance * (-1), distance * (-1) };
			case 270:
				return new int[] { 0, distance * (-1) };
			case 315:
				return new int[] { distance * 1, distance * (-1) };
			}
		}
		throw new RuntimeException(String.format("Invalid motion command.[s:%d, T:%d]", distance, theta));
	}
}
