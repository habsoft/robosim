package pk.com.habsoft.robosim.filters.kalman;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JSpinner;

import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.utils.UIUtils;

public class KalmanFilterView extends RootView {

	private static final long serialVersionUID = 1L;

	final static String TOTAL_TIME_TAG = "TOTAL_TIME";
	final static String VARIANCE_TAG = "VARIANCE";
	final static String CAR_SPEED_TAG = "CAR_SPEED";

	static int DEFAULT_TOTAL_TIME = 20;
	final static int MIN_TOTAL_TIME = 10;
	final static int MAX_TOTAL_TIME = 100;

	static double DEFAULT_VARIANCE = 3;
	final static int MIN_VARIANCE = 0;
	final static int MAX_VARIANCE = 10;

	static double DEFAULT_CAR_SPEED = 0.5;
	final static int MIN_CAR_SPEED = 0;
	final static int MAX_CAR_SPEED = 2;

	LineChartPanel pnlPosition = null;
	LineChartPanel pnlVelocity = null;

	BarChartPanel pnlPositionError = null;
	BarChartPanel pnlVelocityError = null;

	RPanel pnlControls = null;
	JButton btnUpdate;

	JSpinner spnTotalTime = new JSpinner();

	JSpinner spnVariance = new JSpinner();

	JSpinner spnCarSpeed = new JSpinner();

	KalmanFilterSimulator anim = null;

	public KalmanFilterView() {
		super("Kalman Filter", "config/Kalman.properties");
		loadProperties();
		// setLayout(new GridLayout(2, 2));
		setLayout(null);
		setSize(screenSize.getWidth(), screenSize.getHeight() - 50);
	}

	@Override
	public void initGUI() {
		isInit = true;

		int pnlWidth = (int) screenSize.getWidth() / 2;
		int pnlHeight = (int) screenSize.getHeight() / 2 - 120;

		pnlPosition = new LineChartPanel("Time", "Car Position");
		pnlPosition.setSize(pnlWidth, pnlHeight);
		pnlPosition.setBounds(0, 0, pnlWidth, pnlHeight);

		pnlVelocity = new LineChartPanel("Time", "Car Velocity");
		pnlVelocity.setSize(pnlWidth, pnlHeight);
		pnlVelocity.setBounds(pnlWidth, 0, pnlWidth, pnlHeight);

		pnlPositionError = new BarChartPanel("Time", "Position Error", "Measurement Error", "Kalman Position");
		pnlPositionError.setSize(pnlWidth, pnlHeight);
		pnlPositionError.setBounds(0, pnlHeight, pnlWidth, pnlHeight);

		pnlVelocityError = new BarChartPanel("Time", "Velocity Error", "Measurement Error", "Kalman Position");
		pnlVelocityError.setSize(pnlWidth, pnlHeight);
		pnlVelocityError.setBounds(pnlWidth, pnlHeight, pnlWidth, pnlHeight);

		pnlControls = new RPanel(pnlWidth * 2, screenSize.height - (pnlHeight * 2), "Control Panel");
		pnlControls.setLayout(new FlowLayout(), true);

		pnlControls.add(UIUtils.createSpinnerPanel("Total Time", spnTotalTime, DEFAULT_TOTAL_TIME, MIN_TOTAL_TIME,
				MAX_TOTAL_TIME, 1));

		pnlControls.add(UIUtils.createSpinnerPanel("Measurement Variance", spnVariance, DEFAULT_VARIANCE, MIN_VARIANCE,
				MAX_VARIANCE, 0.1));

		pnlControls.add(UIUtils.createSpinnerPanel("Car Speed", spnCarSpeed, DEFAULT_CAR_SPEED, MIN_CAR_SPEED,
				MAX_CAR_SPEED, 0.1));

		btnUpdate = new JButton("Update");
		pnlControls.add(btnUpdate);
		// pnlControls.setBounds(0, pnlHeight * 2, pnlWidth * 2, (int)
		// (getSize().getHeight() - (pnlHeight * 2)));
		pnlControls.setLocation(0, pnlHeight * 2);
		btnUpdate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update();

			}
		});
		getContentPane().add(pnlPosition);
		getContentPane().add(pnlVelocity);
		getContentPane().add(pnlPositionError);
		getContentPane().add(pnlVelocityError);
		getContentPane().add(pnlControls);
		update();
	}

	private void update() {
		DEFAULT_TOTAL_TIME = Integer.parseInt(spnTotalTime.getValue().toString());
		DEFAULT_VARIANCE = Double.parseDouble(spnVariance.getValue().toString());
		DEFAULT_CAR_SPEED = Double.parseDouble(spnCarSpeed.getValue().toString());
		update(DEFAULT_TOTAL_TIME, DEFAULT_VARIANCE, DEFAULT_CAR_SPEED);

	}

	public void update(int total_time, double variance, double carSpeed) {
		anim = new KalmanFilterSimulator(total_time, variance, carSpeed);
		anim.simulate();
		pnlPosition.clearData();
		pnlPosition.addData("Measurement", anim.getPositionMeasurements());
		pnlPosition.addData("Car Position", anim.getCarPositions());
		pnlPosition.addData("Kalman Postion", anim.getPositionsKalman());

		pnlVelocity.clearData();
		pnlVelocity.addData("Measurement", anim.getVelocityMeasurements());
		pnlVelocity.addData("Car Velocity", anim.getCarVelocities());
		pnlVelocity.addData("Kalman Velocity", anim.getVelocitiesKalman());

		pnlPositionError.setData(anim.getPositionMeasurementError(), anim.getPositionKalmanError());
		pnlVelocityError.setData(anim.getVelocityMeasurementError(), anim.getVelocityKalmanError());

	}

	@Override
	public boolean loadProperties() {
		System.out.println("Property File = " + propertyFile);

		if (super.loadProperties()) {
			// Load Total Time
			if (prop.containsKey(TOTAL_TIME_TAG)) {
				try {
					int totalTime = Integer.parseInt(prop.getProperty(TOTAL_TIME_TAG));
					if (totalTime > MAX_TOTAL_TIME || totalTime < MIN_TOTAL_TIME) {
						System.out.println("Invalid value of tag " + TOTAL_TIME_TAG + " .Expedted : " + MIN_TOTAL_TIME
								+ "-" + MAX_TOTAL_TIME + ". Loading Default");
					} else {
						DEFAULT_TOTAL_TIME = totalTime;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + TOTAL_TIME_TAG);
				}
			}
			// Load Variance
			if (prop.containsKey(VARIANCE_TAG)) {
				try {
					double variance = Double.parseDouble(prop.getProperty(VARIANCE_TAG));
					if (variance > MAX_VARIANCE || variance < MIN_VARIANCE) {
						System.out.println("Invalid value of tag " + VARIANCE_TAG + " .Expedted : " + MIN_VARIANCE + "-"
								+ MAX_VARIANCE + ". Loading Default");
					} else {
						DEFAULT_VARIANCE = variance;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + VARIANCE_TAG);
				}
			}
			// Load Car Speed
			if (prop.containsKey(CAR_SPEED_TAG)) {
				try {
					double carSpeed = Double.parseDouble(prop.getProperty(CAR_SPEED_TAG));
					if (carSpeed > MAX_CAR_SPEED || carSpeed < MIN_CAR_SPEED) {
						System.out.println("Invalid value of tag " + CAR_SPEED_TAG + " .Expedted : " + MIN_CAR_SPEED
								+ "-" + MAX_CAR_SPEED + ". Loading Default");
					} else {
						DEFAULT_CAR_SPEED = carSpeed;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + CAR_SPEED_TAG);
				}
			}
		}
		return true;
	}

	@Override
	public void saveProperties() {
		prop.setProperty(TOTAL_TIME_TAG, String.valueOf(DEFAULT_TOTAL_TIME));
		prop.setProperty(VARIANCE_TAG, String.valueOf(DEFAULT_VARIANCE));
		prop.setProperty(CAR_SPEED_TAG, String.valueOf(DEFAULT_CAR_SPEED));

		super.saveProperties();

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		KalmanFilterView view1 = new KalmanFilterView();
		view1.initGUI();

		desk.add(view1);
		view1.setVisible(true);

		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

}
