package pk.com.habsoft.robosim.filters.kalman;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class KalmanFilter {
	RealMatrix x;// initial
	// state(location,velocit)

	RealMatrix p;// initial uncertainty

	private RealMatrix u;// external motion

	private RealMatrix f;// next state function

	private RealMatrix h;// measurement function

	private RealMatrix r;// measurement uncertainty

	private RealMatrix identity;// identity matrix

	public KalmanFilter(double[][] xx, double[][] pp, double[][] uu, double[][] ff, double[][] hh, double[][] rr) {

		x = new Array2DRowRealMatrix(xx);
		p = new Array2DRowRealMatrix(pp);
		u = new Array2DRowRealMatrix(uu);
		f = new Array2DRowRealMatrix(ff);
		h = new Array2DRowRealMatrix(hh);
		r = new Array2DRowRealMatrix(rr);
		identity = MatrixUtils.createRealIdentityMatrix(pp[0].length);
	}

	/**
	 * 
	 * @param measurement
	 */
	public void filter(double[] measurement) {
		double[][] dataZ = { measurement };

		// Measurement
		RealMatrix z = new Array2DRowRealMatrix(dataZ);

		// MOTION UPDATE(PREDICT) /////////////////
		// The time update projects the current state estimate ahead in time.
		// We are predicting the next state here
		// x = F(2x2) * x(2x1) + u(2x1)
		x = (f.multiply(x)).add(u);
		// P(2x2) = F(2x2) * P (2x2)* F.transpose()(2x2)
		p = f.multiply(p.multiply(f.transpose()));

		// MEASUREMENT UPDATE //////////////////////
		// The measurement update adjusts the projected estimate by an actual
		// measurement at that time.
		// Y(1x1) = Z(1x1) - H(1x2) * x(2x1)
		// y = Measurement Error
		RealMatrix y = (z.transpose()).subtract(h.multiply(x));
		// S(1x1) = H(1x2) * P(2x2) * H.transpose()(2x1) + R(1x1)
		RealMatrix s = (h.multiply(p)).multiply(h.transpose()).add(r);
		RealMatrix sInverse = new LUDecomposition(s).getSolver().getInverse();
		// K(2x1) = P(2x2) * H.transpose()(2x1) * S.invers()(1x1)
		// k = Kalman Gain
		RealMatrix k = (p.multiply(h.transpose())).multiply(sInverse);
		// x(2x1) = x(2x1) + K(2x1) * Y(1x1)
		x = x.add(k.multiply(y));
		// P(2x2) =(I(2x2) - K(2x1) * H(1x2)) * P(2x2)
		p = (identity.subtract(k.multiply(h))).multiply(p);

		// System.out.println("X = ");
		// Util.printMatrix(x);
		// System.out.println("P = ");
		// Util.printMatrix(p);

	}

	/**
	 * 
	 * @param xx
	 *            (initial state)
	 * @param pp
	 *            (initial uncertainty)
	 * @param uu
	 *            (external motion)
	 * @param ff
	 *            (next state function)
	 * @param hh
	 *            (measurement function)
	 * @param rr
	 *            (measurement uncertainty)
	 */
	public void setData(double[][] xx, double[][] pp, double[][] uu, double[][] ff, double[][] hh, double[][] rr) {
		x = new Array2DRowRealMatrix(xx);
		p = new Array2DRowRealMatrix(pp);
		u = new Array2DRowRealMatrix(uu);
		f = new Array2DRowRealMatrix(ff);
		h = new Array2DRowRealMatrix(hh);
		r = new Array2DRowRealMatrix(rr);
		identity = MatrixUtils.createRealIdentityMatrix(pp[0].length);
	}

	public RealMatrix getX() {
		return new Array2DRowRealMatrix(x.getData());
	}

	public RealMatrix getP() {
		return new Array2DRowRealMatrix(p.getData());
	}

}
