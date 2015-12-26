package pk.com.habsoft.robosim.filters.kalman;

//http://www.udacity-forums.com/cs373/questions/10153/what-are-all-those-matrices-for-the-kalman-filter-part-i-x-f-p-h-r-u

public class KalmanFilterTest {

	public static void main(String[] args) {

		// 1D Kalman Filters
		double[][] measurement = { { 1 }, { 2 }, { 3 }, { 4 }, { 5 } };
		double[][] dataX = { { 0 }, { 0 } };
		double[][] dataP = { { 1000.0, 0.0 }, { 0.0, 1000.0 } };
		double[][] dataU = { { 0.0 }, { 0.0 } };
		double[][] dataF = { { 1.0, 1.0 }, { 0.0, 1.0 } };
		double[][] dataH = { { 1.0, 0.0 } };
		double[][] dataR = { { 1.0 } };

		// 2D kalman filter
		// double[][] measurement = { { 2.0, 17.0 }, { 0.0, 15.0 }, { 2.0, 13.0
		// }, { 0.0, 11.0 } };
		// double dt = 0.1;
		// double[] initial_xy = { 1.0, 19.0 };
		// double[][] dataX = { { initial_xy[0] }, { initial_xy[1] }, { 0 }, { 0
		// } };
		// double[][] dataP = { { 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0 },
		// { 0.0, 0.0, 1000.0, 0.0 }, { 0.0, 0.0, 0.0, 1000.0 } };
		// double[][] dataU = { { 0.0 }, { 0.0 }, { 0.0 }, { 0.0 } };
		// double[][] dataF = { { 1.0, 0.0, dt, 0.0 }, { 0.0, 1.0, 0.0, dt }, {
		// 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 } };
		// double[][] dataH = { { 1.0, 0.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0, 0.0 }
		// };
		// double[][] dataR = { { 0.1, 0.0 }, { 0.0, 0.1 } };

		KalmanFilter filter = new KalmanFilter(dataX, dataP, dataU, dataF, dataH, dataR);
		for (int i = 0; i < measurement.length; i++) {
			filter.filter(measurement[i]);
			System.out.println(filter.getX());

		}

	}
}