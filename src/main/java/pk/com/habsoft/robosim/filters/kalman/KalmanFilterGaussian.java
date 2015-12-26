package pk.com.habsoft.robosim.filters.kalman;

import pk.com.habsoft.robosim.utils.Util;

public class KalmanFilterGaussian {

	/**
	 * 
	 * @param mean1
	 * @param var1
	 * @param mean2
	 * @param var2
	 * @return measurement update
	 */
	public static Mu_Sigma update(double mean1, double var1, double mean2, double var2) {
		double new_mean = (mean1 * var2 + mean2 * var1) / (var1 + var2);
		double new_var = 1 / (1 / var1 + 1 / var2);
		return new Mu_Sigma(new_mean, new_var);
	}

	/**
	 * 
	 * @param mean1
	 * @param var1
	 * @param mean2
	 * @param var2
	 * @return Motion/Prediction Update(Correction)
	 */
	public static Mu_Sigma predict(double mean1, double var1, double mean2, double var2) {
		double new_mean = mean1 + mean2;
		double new_var = var1 + var2;
		return new Mu_Sigma(new_mean, new_var);
	}

	public static void main(String[] args) {

		double[] measurements = { 5.0, 6.0, 7.0, 9.0, 10.0 };
		double measurement_sig = 4.0;
		double[] motions = { 1.0, 1.0, 2.0, 1.0, 1.0 };
		double motion_sig = 2.0;

		// initial parameters
		double mu = 0;
		double var = 10000.0;

		Mu_Sigma newVal = new Mu_Sigma(mu, var);
		for (int j = 0; j < measurements.length; j++) {
			// System.out.println("Actual "+(newVal.mu));
			newVal = update(newVal.mu, newVal.var, measurements[j], measurement_sig);
			System.out.println("Update  " + newVal);
			newVal = predict(newVal.mu, newVal.var, motions[j], motion_sig);
			System.out.println("Predict " + newVal);
		}

	}
}

class Mu_Sigma {
	double mu;
	double var;

	public Mu_Sigma(double mu, double var) {
		this.mu = mu;
		this.var = var;
	}

	@Override
	public String toString() {
		return "Mu_Sigma [mu=" + Util.df.format(mu) + ", sigma=" + Util.df.format(var) + "]";
	}

}
