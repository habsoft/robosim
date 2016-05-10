package pk.com.habsoft.robosim.smoothing.views;


import java.awt.Frame;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.smoothing.LineChartPanel;


public class PIDControllerView extends RootView {

	private static final long serialVersionUID = 1L;

	static int PNL_CHART_WIDTH = 700;
	static int PNL_CHART_HEIGHT = 500;
	static int PNL_CONTROL_WIDTH = 300;
	static int PNL_CONTROL_HEIGHT = 250;

	LineChartPanel pnlChart;
	PIDControlPanel pnlControl;

	public PIDControllerView() {
		super("PID Controller", "config/PidController.properties");
		setLayout(null);
		loadProperties();
		initGUI();
	}

	@Override
	public void initGUI() {
		isInit = true;

		pnlChart = new LineChartPanel(PNL_CHART_WIDTH, PNL_CHART_HEIGHT, "X-Axis", "Y-Axis");
		pnlChart.setLocation(0, 0);
		pnlChart.setSize(PNL_CHART_WIDTH, PNL_CHART_HEIGHT);

		pnlControl = new PIDControlPanel(prop, PNL_CONTROL_WIDTH, PNL_CONTROL_HEIGHT, pnlChart);
		pnlControl.setLocation(PNL_CHART_WIDTH, 0);
		pnlControl.setSize(PNL_CONTROL_WIDTH, PNL_CONTROL_HEIGHT);

		add(pnlChart);
		add(pnlControl);

		setBounds(0, 0, screenSize.getWidth(), screenSize.getHeight());

	}

	@Override
	public boolean loadProperties() {
		System.out.println("Property File = " + propertyFile);

		double ratio = 0.70;
		PNL_CHART_WIDTH = (int) (screenSize.getWidth() * ratio);
		PNL_CONTROL_WIDTH = (int) (screenSize.getWidth() * (1 - ratio));
		PNL_CHART_HEIGHT = PNL_CONTROL_HEIGHT = (int) screenSize.getHeight() - 120;

		super.loadProperties();

		return true;
	}

	@Override
	public void saveProperties() {
		pnlControl.saveProperties();
		super.saveProperties();
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		PIDControllerView view1 = new PIDControllerView();

		desk.add(view1);
		view1.setVisible(true);
		// Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		// frame.setLocation((int) size.getWidth() - 600, (int) size.getHeight()
		// - 800);
		// frame.setSize(600, 700);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

}
