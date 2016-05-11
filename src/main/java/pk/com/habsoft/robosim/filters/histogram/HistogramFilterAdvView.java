package pk.com.habsoft.robosim.filters.histogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.utils.UIUtils;

public class HistogramFilterAdvView extends RootView {

	private static final long serialVersionUID = 1L;

	private static final String NO_OF_ROWS_TAG = "NO_OF_ROWS";
	private static final String NO_OF_COLUMNS_TAG = "NO_OF_COLUMNS";
	private static final String NO_OF_COLORS_TAG = "NO_OF_COLORS";
	private static final String CYCLIC_WORLD_TAG = "CYCLIC_WORLD";
	private static final String MOTION_NOISE_TAG = "MOTION_NOISE";
	private static final String SENSOR_NOISE_TAG = "SENSOR_NOISE";
	private static final String MAP_ROW_TAG = "MAP_ROW_";

	static int MAX_NO_OF_ROWS = 6;
	static int MIN_NO_OF_ROWS = 1;
	static int DEF_NO_OF_ROWS = 3;
	static int MAX_NO_OF_COLUMNS = 6;
	static int MIN_NO_OF_COLUMNS = 1;
	static int DEF_NO_OF_COLUMNS = 4;

	final static int MAX_NO_OF_COLORS = 5;
	final static int MIN_NO_OF_COLORS = 1;
	static int DEF_NO_OF_COLORS = 3;

	protected final static String[] sensorNames = { "Red", "Green", "Blue", "Cyan", "Magenta" };
	protected final static Color[] sensors = { Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA };

	JLabel[][] lblBeliefMap;

	int[][] world;
	HistogramFilter filter = null;

	// Robot Motions Controls
	JSpinner spnMotionNoise;
	SpinnerNumberModel spnMotionNoiseModal = new SpinnerNumberModel(0, 0, 1, 0.01);
	JCheckBox chkCyclic;
	public final static String[] btnNames = { "Up-Left", "Up", "Up-Right", "Left", "No_Move", "Right", "Down-Left", "Down", "Down-Right" };
	JButton[] btnMotions = { new JButton(""), new JButton(""), new JButton(""), new JButton(""), new JButton(""), new JButton(""),
			new JButton(""), new JButton(""), new JButton("") };

	// Robot Sensor Controls
	JSpinner spnSensorNoise;
	SpinnerNumberModel spnSensorNoiseModal = new SpinnerNumberModel(0, 0, 1, 0.01);
	JButton[] btnSensors = { new JButton(""), new JButton(""), new JButton(""), new JButton(""), new JButton("") };
	JSpinner spnNoOfColors;
	SpinnerNumberModel spnNoOfColorsModal = new SpinnerNumberModel(DEF_NO_OF_COLORS, MIN_NO_OF_COLORS, MAX_NO_OF_COLORS, 1);
	JButton btnReset;
	JButton btnApply;
	JButton btnWorldConfiguration;
	JButton btnResetSimulation;

	static double DEFAULT_SENSOR_NOISE = 0.20;
	static double DEFAULT_MOTION_NOISE = 0.20;
	static boolean DEFAULT_CYCLIC_WORLD = false;

	static int PANEL_WIDTH = 300;
	static int PANEL_HEIGHT = 300;

	RPanel pnlRobotMotions;
	RPanel pnlRobotSettings;
	RPanel pnlBeliefMap;

	public HistogramFilterAdvView() {
		super("Histogram Filter (Markov Localization)", "config/Histogram.properties");
		setLayout(null);
		loadProperties();

		setSize(screenSize);
		setVisible(false);
	}

	public void initGUI() {
		isInit = true;
		// Set this world to Localizer
		filter = new HistogramFilter(world);
		// Setting the default noise
		filter.setMotionNoise(DEFAULT_MOTION_NOISE);
		filter.setSensorNoise(DEFAULT_SENSOR_NOISE);
		filter.setCyclic(DEFAULT_CYCLIC_WORLD);

		// //////////// Setting Panel

		pnlRobotSettings = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Robot Setting");
		createSensorsComponents(pnlRobotSettings);

		// ////////// Robot Belief Map
		pnlBeliefMap = new RobotBeliefMap(this, PANEL_WIDTH * 2, PANEL_HEIGHT * 2, "Robot Belief Map", DEF_NO_OF_ROWS, DEF_NO_OF_COLUMNS);
		pnlBeliefMap.setLocation(PANEL_WIDTH, 0);
		add(pnlBeliefMap);

		// ////////// Controls Panel

		pnlRobotMotions = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Motion Controls");
		pnlRobotMotions.setLocation(0, PANEL_HEIGHT);
		createMotionComponents();

		// Add panels to frame
		getContentPane().add(pnlRobotMotions);
		getContentPane().add(pnlRobotSettings);

	}

	private void createMotionComponents() {
		// Add Motion buttons
		pnlRobotMotions.setLayout(new GridLayout(3, 3, 5, 5), true);

		RobotMotionListener motionList = new RobotMotionListener();
		for (int i = 0; i < btnMotions.length; i++) {
			btnMotions[i].setActionCommand(String.valueOf(i));
			btnMotions[i].setIcon(new ImageIcon(getClass().getResource("/images" + File.separatorChar + btnNames[i] + ".png")));
			btnMotions[i].setToolTipText(btnNames[i]);
			pnlRobotMotions.add(btnMotions[i]);
			btnMotions[i].addActionListener(motionList);
		}

	}

	private void createSensorsComponents(JPanel pnlRobotSetting) {
		int spacing = 3;

		int xLoc = 0;
		int yLoc = 0;
		int width = PANEL_WIDTH / 2 - 1;
		int height = Math.min(PANEL_HEIGHT / 8, 30);
		JLabel lblMotion = new JLabel("Motion Noise");
		lblMotion.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(lblMotion);

		spnMotionNoise = new JSpinner();
		spnMotionNoise.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		spnMotionNoise.setToolTipText("Set the ROBOT motion noise.It should be (0-1)");
		spnMotionNoise.setModel(spnMotionNoiseModal);
		spnMotionNoise.setValue(DEFAULT_MOTION_NOISE);
		pnlRobotSetting.add(spnMotionNoise);

		lblMotion = new JLabel("Cyclic World");
		yLoc += height;
		lblMotion.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(lblMotion);

		chkCyclic = new JCheckBox("");
		chkCyclic.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		chkCyclic.setToolTipText("UnCheck it if the ROBOT world is not cyclic");
		chkCyclic.setSelected(DEFAULT_CYCLIC_WORLD);
		pnlRobotSetting.add(chkCyclic);

		lblMotion = new JLabel("Sensor Noise");
		yLoc += height;
		lblMotion.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(lblMotion);

		spnSensorNoise = new JSpinner();
		spnSensorNoise.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		spnSensorNoise.setToolTipText("Set the ROBOT sensor noise.It should be (0-1)");
		spnSensorNoise.setModel(spnSensorNoiseModal);
		spnSensorNoise.setValue(DEFAULT_SENSOR_NOISE);
		pnlRobotSetting.add(spnSensorNoise);

		lblMotion = new JLabel("No Of Sensors");
		yLoc += height;
		lblMotion.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(lblMotion);

		spnNoOfColors = new JSpinner();
		spnNoOfColors.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		spnNoOfColors.setToolTipText("");
		spnNoOfColors.setModel(spnNoOfColorsModal);
		spnNoOfColors.setValue(DEF_NO_OF_COLORS);
		pnlRobotSetting.add(spnNoOfColors);

		btnApply = new JButton("Apply Setting");
		yLoc += height;
		btnApply.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(btnApply);
		RobotControlListener controlListener = new RobotControlListener();
		btnApply.addActionListener(controlListener);

		btnReset = new JButton("Reset Belief");
		btnReset.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(btnReset);
		btnReset.addActionListener(controlListener);

		btnWorldConfiguration = new JButton("Configure World");
		yLoc += height;
		btnWorldConfiguration.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlRobotSetting.add(btnWorldConfiguration);
		btnWorldConfiguration.addActionListener(controlListener);

		// //////////////////////////////////////////
		JLabel header = UIUtils.createLabel(PANEL_WIDTH, LABEL_HEIGHT, "Robot Sensors");
		yLoc += height;
		header.setBounds(xLoc + spacing, yLoc + spacing, 2 * width - spacing, height - spacing);
		pnlRobotSetting.add(header);

		yLoc += height;
		JPanel pnlSouth = new JPanel(new GridLayout(1, 5, 10, 10));
		pnlSouth.setBounds(xLoc + spacing, yLoc + spacing, 2 * width - spacing, height - spacing);
		RobotSensorListener sensorListener = new RobotSensorListener();
		for (int i = 0; i < MAX_NO_OF_COLORS; i++) {
			btnSensors[i].setActionCommand(Integer.toString(i));
			btnSensors[i].setBackground(sensors[i]);
			btnSensors[i].addActionListener(sensorListener);
			pnlSouth.add(btnSensors[i]);
		}
		enableSensors();
		pnlRobotSetting.add(pnlSouth);
	}

	private void enableSensors() {
		for (int i = 0; i < MAX_NO_OF_COLORS; i++) {
			if (i >= DEF_NO_OF_COLORS) {
				btnSensors[i].setEnabled(false);
				btnSensors[i].setText("D");
				btnSensors[i].setToolTipText("Disabled");
			} else {
				btnSensors[i].setEnabled(true);
				btnSensors[i].setText("E");
				btnSensors[i].setToolTipText("Enabled");
			}
		}
	}

	private class RobotControlListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JButton o = (JButton) e.getSource();
			if (o.equals(btnReset)) {
				filter.resetBelief();
				pnlBeliefMap.repaint();
			} else if (o.equals(btnApply)) {
				filter.setMotionNoise(Double.parseDouble(spnMotionNoise.getValue().toString()));
				filter.setSensorNoise(Double.parseDouble(spnSensorNoise.getValue().toString()));
				filter.setCyclic(chkCyclic.isSelected());
				int newSensors = Integer.parseInt(spnNoOfColors.getValue().toString());
				// review colors of world due to change in no of sensors
				if (newSensors < DEF_NO_OF_COLORS) {
					for (int i = 0; i < world.length; i++) {
						for (int j = 0; j < world[i].length; j++) {
							world[i][j] %= newSensors;
						}
					}
				}
				DEF_NO_OF_COLORS = newSensors;
				enableSensors();

				filter.setWorld(world);
				pnlBeliefMap.repaint();
			} else if (o.equals(btnWorldConfiguration)) {
				WorldBuilder gui = new WorldBuilder(world, DEF_NO_OF_COLORS, sensors);
				gui.setVisible(true);
				if (gui.isWorldChanged()) {
					world = gui.getNewWorld();
					DEF_NO_OF_ROWS = world.length;
					DEF_NO_OF_COLUMNS = world[0].length;
					filter.setWorld(world);
					pnlBeliefMap.repaint();
				}
			}
		}

	}

	private class RobotMotionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			try {
				int actionIdx = Integer.parseInt(action);
				filter.move(actionIdx);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	private class RobotSensorListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			try {
				int actionIdx = Integer.parseInt(action);
				filter.sense(actionIdx);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public boolean loadProperties() {

		PANEL_HEIGHT = (int) (screenSize.getHeight() / 2 - 80);
		PANEL_WIDTH = PANEL_HEIGHT - LABEL_HEIGHT;

		System.out.println("Property File = " + propertyFile);

		if (super.loadProperties()) {
			// No of rows
			if (prop.containsKey(NO_OF_ROWS_TAG)) {
				try {
					int noOfRows = Integer.parseInt(prop.getProperty(NO_OF_ROWS_TAG));
					if (noOfRows > MAX_NO_OF_ROWS || noOfRows < MIN_NO_OF_ROWS) {
						System.out.println("Invalid value of tag " + NO_OF_ROWS_TAG + " .Expedted : " + MIN_NO_OF_ROWS + "-"
								+ MAX_NO_OF_ROWS + ".Loading Default");
					} else {
						DEF_NO_OF_ROWS = noOfRows;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + NO_OF_ROWS_TAG + ".Loading Default");
				}
			}
			// No of columns
			if (prop.containsKey(NO_OF_COLUMNS_TAG)) {
				try {
					int noOfColumns = Integer.parseInt(prop.getProperty(NO_OF_COLUMNS_TAG));
					if (noOfColumns > MAX_NO_OF_COLUMNS || noOfColumns < MIN_NO_OF_COLUMNS) {
						System.out.println("Invalid value of tag " + NO_OF_COLUMNS_TAG + " .Expedted : " + MIN_NO_OF_COLUMNS + "-"
								+ MAX_NO_OF_COLUMNS + ".Loading Default");
					} else {
						DEF_NO_OF_COLUMNS = noOfColumns;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + NO_OF_COLUMNS_TAG + ".Loading Default");
				}
			}
			// No of colors
			if (prop.containsKey(NO_OF_COLORS_TAG)) {
				try {
					int noOfColors = Integer.parseInt(prop.getProperty(NO_OF_COLORS_TAG));
					if (noOfColors > MAX_NO_OF_COLORS || noOfColors < MIN_NO_OF_COLORS) {
						System.out.println("Invalid value of tag " + NO_OF_COLORS_TAG + " .Expedted : " + MIN_NO_OF_COLORS + "-"
								+ MAX_NO_OF_COLORS + ".Loading Default");
					} else {
						DEF_NO_OF_COLORS = noOfColors;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + NO_OF_COLORS_TAG + ".Loading Default");
				}
			}
			// Cyclic world or not
			if (prop.containsKey(CYCLIC_WORLD_TAG)) {
				try {
					DEFAULT_CYCLIC_WORLD = prop.getProperty(CYCLIC_WORLD_TAG).equals("true");
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + CYCLIC_WORLD_TAG + ". Loading Default");
				}
			}
			// Load motion noise
			if (prop.containsKey(MOTION_NOISE_TAG)) {
				try {
					double motionNoise = Double.parseDouble(prop.getProperty(MOTION_NOISE_TAG));
					if (motionNoise > 1 || motionNoise < 0) {
						System.out.println("Invalid value of tag " + MOTION_NOISE_TAG + " .Expedted : 0-1. Loading Default");
					} else {
						DEFAULT_MOTION_NOISE = motionNoise;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + MOTION_NOISE_TAG);
				}
			}
			// Load sensor noise
			if (prop.containsKey(SENSOR_NOISE_TAG)) {
				try {
					double sensorNoise = Double.parseDouble(prop.getProperty(SENSOR_NOISE_TAG));
					if (sensorNoise > 1 || sensorNoise < 0) {
						System.out.println("Invalid value of tag " + SENSOR_NOISE_TAG + " .Expedted : 0-1. Loading Default");
					} else {
						DEFAULT_SENSOR_NOISE = sensorNoise;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + SENSOR_NOISE_TAG);
				}
			}
			// Load world
			boolean trueWorld = true;
			world = new int[DEF_NO_OF_ROWS][DEF_NO_OF_COLUMNS];
			for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
				String rowTag = MAP_ROW_TAG + (i + 1);
				try {
					String[] row = prop.getProperty(rowTag).split(",");
					for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {
						try {
							world[i][j] = Integer.parseInt(row[j]);
							if (world[i][j] >= DEF_NO_OF_COLORS) {
								System.out.println("Invalid value at " + i + "," + j + "  Expecting 0 - " + DEF_NO_OF_COLORS);
								trueWorld = false;
							}
						} catch (Exception e) {
							System.out.println("Invalid value at " + i + "," + j + "  Expecting 0 - " + DEF_NO_OF_COLORS);
							trueWorld = false;
						}
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + rowTag + " ." + e.getMessage());
					trueWorld = false;
				}
			}
			// if world is invalid then initialize random world
			if (!trueWorld) {
				System.out.println("Loading Random Robot Map");
				Random r = new Random();
				for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
					for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {
						world[i][j] = r.nextInt(DEF_NO_OF_COLORS);
					}
				}
			}
		}
		return true;
	}

	public void saveProperties() {

		prop.setProperty(CYCLIC_WORLD_TAG, Boolean.toString(chkCyclic.isSelected()));
		prop.setProperty(MOTION_NOISE_TAG, spnMotionNoise.getValue().toString());
		prop.setProperty(SENSOR_NOISE_TAG, spnSensorNoise.getValue().toString());

		// save world
		prop.setProperty(NO_OF_ROWS_TAG, Integer.toString(DEF_NO_OF_ROWS));
		prop.setProperty(NO_OF_COLUMNS_TAG, Integer.toString(DEF_NO_OF_COLUMNS));
		prop.setProperty(NO_OF_COLORS_TAG, Integer.toString(DEF_NO_OF_COLORS));

		for (int i = 0; i < world.length; i++) {
			String row = "";
			int j = 0;
			for (; j < world[i].length - 1; j++) {
				row += world[i][j] + ",";
			}
			row += Integer.toString(world[i][j]);
			prop.setProperty(MAP_ROW_TAG + (i + 1), row);
		}

		super.saveProperties();

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		HistogramFilterAdvView view1 = new HistogramFilterAdvView();
		view1.initGUI();

		desk.add(view1);
		view1.setVisible(true);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();

		frame.setSize(width, height);
		frame.setVisible(true);
	}

}
