package pk.com.habsoft.robosim.smoothing.views;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

import pk.com.habsoft.robosim.internal.PropertiesListener;
import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.smoothing.LineChartPanel;
import pk.com.habsoft.robosim.utils.UIUtils;


public class PIDControlPanel extends JPanel implements PropertiesListener, ActionListener {

	private static final long serialVersionUID = 1L;

	final static String TAU_P = "TAU_P";
	final static String TAU_D = "TAU_D";
	final static String TAU_I = "TAU_I";
	final static String ITERATIONS = "ITERATIONS";
	final static String SPEED = "SPEED";
	final static String SHOWREF = "SHOWREF", SHOWP = "SHOWP", SHOWPDRIFT = "SHOWPDRIFT", SHOWD = "SHOWD", SHOWDDRIFT = "SHOWDDRIFT", SHOWI = "SHOWI", SHOWIDRIFT = "SHOWIDRIFT", SHOWPD = "SHOWPD",
			SHOWPDDRIFT = "SHOWPDDRIFT", SHOWPI = "SHOWPI", SHOWPIDRIFT = "SHOWPIDRIFT", SHOWPID = "SHOWPID", SHOWPIDDRIFT = "SHOWPIDDRIFT";

	LineChartPanel chart;
	PIDSimulator simulator;

	JSpinner spProportional, spDifferenctial, spIntegral;
	JSpinner spSteeringDrift, spIterations, spSpeed;
	JButton btnSetting;

	Properties props;
	JCheckBox cbRef, cbP, cbPDrift, cbD, cbDDrift, cbI, cbIDrift, cbPD, cbPDDrift, cbPI, cbPIDrift, cbPID, cbPIDDrift;
	boolean showRef = true, showP = true, showPDrift, showD, showDDrift, showI, showIDrift, showPD, showPDDrift, showPI, showPIDrift, showPID, showPIDDrift;

	// Simulator Variables
	private static char degree = '\u00B0';
	double tauP = 0.2, tauD = 3.0, tauI = 0.004;
	int drift = 10;// in degrees
	int iterations = 100;
	int speed = 1;

	public PIDControlPanel(Properties props, int width, int height, LineChartPanel chart) {
		this.props = props;
		this.chart = chart;
		setLayout(new GridLayout(3, 1));
		setSize(width, height);

		loadProperties();
		initGUI();
	}

	private ActionListener displayListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			showRef = cbRef.isSelected();
			showP = cbP.isSelected();
			showPDrift = cbPDrift.isSelected();
			showD = cbD.isSelected();
			showDDrift = cbDDrift.isSelected();
			showI = cbI.isSelected();
			showIDrift = cbIDrift.isSelected();
			showPD = cbPD.isSelected();
			showPDDrift = cbPDDrift.isSelected();
			showPI = cbPI.isSelected();
			showPIDrift = cbPIDrift.isSelected();
			showPID = cbPID.isSelected();
			showPIDDrift = cbPIDDrift.isSelected();

			updateChartData();
		}
	};

	private void initGUI() {

		RPanel pnlController = new RPanel(getWidth() - 30, 220, "Controller Setting");
		pnlController.setLayoutMgr(new GridLayout(7, 1));

		spProportional = new JSpinner();
		pnlController.add(UIUtils.createSpinnerPanel("P (Proportional)", spProportional, tauP, -10, 10, 0.1));
		spDifferenctial = new JSpinner();
		pnlController.add(UIUtils.createSpinnerPanel("D (Differential)", spDifferenctial, tauD, -100, 100, 1));
		spIntegral = new JSpinner();
		pnlController.add(UIUtils.createSpinnerPanel("I (Integral)", spIntegral, tauI, -10, 10, 0.001));

		spSteeringDrift = new JSpinner();
		pnlController.add(UIUtils.createSpinnerPanel("Drift(" + degree + ")", spSteeringDrift, drift, -30, 30, 1));
		spIterations = new JSpinner();
		pnlController.add(UIUtils.createSpinnerPanel("Iterations", spIterations, iterations, 0, 500, 1));
		spSpeed = new JSpinner();
		pnlController.add(UIUtils.createSpinnerPanel("Speed", spSpeed, speed, 0, 50, 1));

		btnSetting = new JButton("Apply Setting");
		btnSetting.addActionListener(this);
		pnlController.add(btnSetting);

		// ////////////////////////////////////////////////////////////////////////////////////

		RPanel pnlGrapth = new RPanel(getWidth() - 30, 220, "Chart Setting");
		pnlGrapth.setLayoutMgr(new GridLayout(7, 1));

		cbRef = new JCheckBox("Show Reference");
		cbRef.setSelected(showRef);
		cbRef.addActionListener(displayListener);
		pnlGrapth.add(cbRef);
		pnlGrapth.add(new Label());

		cbP = new JCheckBox("P");
		cbP.setSelected(showP);
		cbP.addActionListener(displayListener);
		pnlGrapth.add(cbP);
		cbPDrift = new JCheckBox("P(Drift)");
		cbPDrift.setSelected(showPDrift);
		cbPDrift.addActionListener(displayListener);
		pnlGrapth.add(cbPDrift);

		cbD = new JCheckBox("D");
		cbD.setSelected(showD);
		cbD.addActionListener(displayListener);
		pnlGrapth.add(cbD);
		cbDDrift = new JCheckBox("D(Drift)");
		cbDDrift.setSelected(showDDrift);
		cbDDrift.addActionListener(displayListener);
		pnlGrapth.add(cbDDrift);

		cbI = new JCheckBox("I");
		cbI.setSelected(showI);
		cbI.addActionListener(displayListener);
		pnlGrapth.add(cbI);
		cbIDrift = new JCheckBox("I(Drift)");
		cbIDrift.setSelected(showIDrift);
		cbIDrift.addActionListener(displayListener);
		pnlGrapth.add(cbIDrift);

		cbPD = new JCheckBox("PD");
		cbPD.setSelected(showPD);
		cbPD.addActionListener(displayListener);
		pnlGrapth.add(cbPD);
		cbPDDrift = new JCheckBox("PD(Drift)");
		cbPDDrift.setSelected(showPDDrift);
		cbPDDrift.addActionListener(displayListener);
		pnlGrapth.add(cbPDDrift);

		cbPI = new JCheckBox("PI");
		cbPI.setSelected(showPI);
		cbPI.addActionListener(displayListener);
		pnlGrapth.add(cbPI);
		cbPIDrift = new JCheckBox("PI(Drift)");
		cbPIDrift.setSelected(showPIDrift);
		cbPIDrift.addActionListener(displayListener);
		pnlGrapth.add(cbPIDrift);

		cbPID = new JCheckBox("PID");
		cbPID.setSelected(showPID);
		cbPID.addActionListener(displayListener);
		pnlGrapth.add(cbPID);
		cbPIDDrift = new JCheckBox("PID(Drift)");
		cbPIDDrift.setSelected(showPIDDrift);
		cbPIDDrift.addActionListener(displayListener);
		pnlGrapth.add(cbPIDDrift);

		// /////////////

		RPanel pnlScenario = new RPanel(getWidth() - 30, 220, "Scenario");
		pnlScenario.setLayoutMgr(new BorderLayout());
		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setEditable(false);
		area.setText("A robotic car is at point (0,1) and want to travell along x-axis. The steering of this car is based on P.I.D controller and it will turns the car towards x-axis based on different values of P.I.D controller."
				+ " The graph will show the results of (P.I.D) controller with the given parameters.");
		area.setText("A robotic car is at point (0,1) and want to travell along x-axis.\n" + " The steering of this car is based on P.I.D controller and it will \n "
				+ "turns the car towards x-axis based on different values of \nP.I.D controller." + " The graph will show the results of \n(P.I.D) controller with the given parameters.");
		pnlScenario.add(area, BorderLayout.CENTER);

		add(pnlController);
		add(pnlGrapth);
		add(pnlScenario);

		updateChartData();

	}

	@Override
	public boolean loadProperties() {
		if (props != null) {
			tauP = Double.parseDouble(props.getProperty(TAU_P, "0.2"));
			tauD = Double.parseDouble(props.getProperty(TAU_D, "3"));
			tauI = Double.parseDouble(props.getProperty(TAU_I, "0.004"));
			iterations = Integer.parseInt(props.getProperty(ITERATIONS, "100"));
			speed = Integer.parseInt(props.getProperty(SPEED, "1"));

			showRef = props.getProperty(SHOWREF, "true").equals("true");
			showP = props.getProperty(SHOWP, "true").equals("true");
			showPDrift = props.getProperty(SHOWPDRIFT, "false").equals("true");
			showD = props.getProperty(SHOWD, "false").equals("true");
			showDDrift = props.getProperty(SHOWDDRIFT, "false").equals("true");
			showI = props.getProperty(SHOWI, "false").equals("true");
			showIDrift = props.getProperty(SHOWIDRIFT, "false").equals("true");
			showPD = props.getProperty(SHOWPD, "false").equals("true");
			showPDDrift = props.getProperty(SHOWPDDRIFT, "false").equals("true");
			showPI = props.getProperty(SHOWPI, "false").equals("true");
			showPIDrift = props.getProperty(SHOWPIDRIFT, "false").equals("true");
			showPID = props.getProperty(SHOWPID, "true").equals("true");
			showPIDDrift = props.getProperty(SHOWPIDDRIFT, "false").equals("true");

		}

		simulator = new PIDSimulator();
		simulator.setTauP(tauP);
		simulator.setTauD(tauD);
		simulator.setTauI(tauI);
		simulator.setSteerDrift(drift);
		simulator.setIter(iterations);
		simulator.setSpeed(speed);

		return true;
	}

	@Override
	public void saveProperties() {
		props.setProperty(TAU_P, String.valueOf(tauP));
		props.setProperty(TAU_D, String.valueOf(tauD));
		props.setProperty(TAU_I, String.valueOf(tauI));
		props.setProperty(ITERATIONS, String.valueOf(iterations));
		props.setProperty(SPEED, String.valueOf(speed));

		props.setProperty(SHOWREF, Boolean.toString(showRef));

		props.setProperty(SHOWP, Boolean.toString(showP));
		props.setProperty(SHOWD, Boolean.toString(showD));
		props.setProperty(SHOWI, Boolean.toString(showI));
		props.setProperty(SHOWPD, Boolean.toString(showPD));
		props.setProperty(SHOWPI, Boolean.toString(showPI));
		props.setProperty(SHOWPID, Boolean.toString(showPID));

		props.setProperty(SHOWPDRIFT, Boolean.toString(showPDrift));
		props.setProperty(SHOWDDRIFT, Boolean.toString(showDDrift));
		props.setProperty(SHOWIDRIFT, Boolean.toString(showIDrift));
		props.setProperty(SHOWPDDRIFT, Boolean.toString(showPDDrift));
		props.setProperty(SHOWPIDRIFT, Boolean.toString(showPIDrift));
		props.setProperty(SHOWPIDDRIFT, Boolean.toString(showPIDDrift));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		tauP = Double.parseDouble(spProportional.getValue().toString());
		tauD = Double.parseDouble(spDifferenctial.getValue().toString());
		tauI = Double.parseDouble(spIntegral.getValue().toString());

		drift = Integer.parseInt(spSteeringDrift.getValue().toString());
		iterations = Integer.parseInt(spIterations.getValue().toString());
		speed = Integer.parseInt(spSpeed.getValue().toString());

		// simulator = new PIDSimulator();
		simulator.setTauP(tauP);
		simulator.setTauD(tauD);
		simulator.setTauI(tauI);
		simulator.setSteerDrift(drift);
		simulator.setIter(iterations);
		simulator.setSpeed(speed);

		updateChartData();
	}

	private void updateChartData() {
		chart.clearData();
		if (showRef)
			chart.addData("Reference", simulator.getRefData());
		if (showP)
			chart.addData("P-Controller", simulator.getPData(false));
		if (showPDrift)
			chart.addData("P-Controller(Drift)", simulator.getPData(true));
		if (showPD)
			chart.addData("PD-Controller", simulator.getPDData(false));
		if (showPDDrift)
			chart.addData("PD-Controller(Drift)", simulator.getPDData(true));
		if (showPI)
			chart.addData("PI-Controller", simulator.getPIData(false));
		if (showPIDrift)
			chart.addData("PI-Controller(Drift)", simulator.getPIData(true));
		if (showPID)
			chart.addData("PID-Controller", simulator.getPIDData(false));
		if (showPIDDrift)
			chart.addData("PID-Controller(Drift)", simulator.getPIDData(true));
	}

}
