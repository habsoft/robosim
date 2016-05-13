package pk.com.habsoft.robosim.filters.histogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.utils.UIUtils;

public class HistogramFilterView extends RootView {

	private static final long serialVersionUID = 1L;

	private static final String NO_OF_ROWS_TAG = "NO_OF_ROWS";
	private static final String NO_OF_COLUMNS_TAG = "NO_OF_COLUMNS";
	private static final String NO_OF_COLORS_TAG = "NO_OF_COLORS";
	private static final String CYCLIC_WORLD_TAG = "CYCLIC_WORLD";
	private static final String MOTION_NOISE_TAG = "MOTION_NOISE";
	private static final String SENSOR_NOISE_TAG = "SENSOR_NOISE";
	private static final String MAP_ROW_TAG = "MAP_ROW_";
	private static final String ROBOT_COMMANDS_TAG = "ROBOT_COMMANDS";

	private Image image;
	private Graphics2D graphics;
	private static int cellSize = 50;
	private static final int spacing = 2;

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

	JLabel[][] lblLocationMap;
	JLabel[][] lblBeliefMap;

	int[][] world;
	HistogramFilter filter = null;
	HistogramSimulator simulator = null;

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

	RPanel pnlLocationMap;
	RPanel pnlRobotMotions;
	RPanel pnlRobotSettings;
	RPanel pnlOutput;
	RPanel pnlSimulation;
	private JTextArea ta;
	int[][] commands = new int[0][0];

	JRadioButton rbStart, rbStop;
	JButton btnNext, btnBuildSimulation;

	public HistogramFilterView() {
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
		simulator = new HistogramSimulator(this, filter);
		// Setting the default noise
		filter.setMotionNoise(DEFAULT_MOTION_NOISE);
		filter.setSensorNoise(DEFAULT_SENSOR_NOISE);
		filter.setCyclic(DEFAULT_CYCLIC_WORLD);

		// Robot Location Panel
		pnlLocationMap = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Robot Location Map");
		createWorldMapComponents();

		// ////////// Robot Belief Map
		JLabel header = UIUtils.createLabel(PANEL_WIDTH, LABEL_HEIGHT, "Robot Belief Map");
		header.setLocation(PANEL_WIDTH, 0);
		add(header);

		// ////////// Controls Panel

		pnlRobotMotions = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Motion Controls");
		pnlRobotMotions.setLocation(0, PANEL_HEIGHT);
		createMotionComponents();

		// //////////// Setting Panel

		pnlRobotSettings = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Robot Setting");
		pnlRobotSettings.setLocation(PANEL_WIDTH, PANEL_HEIGHT);
		createSensorsComponents(pnlRobotSettings);

		// Simulation panel

		pnlOutput = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Simulation Output");
		pnlOutput.setLocation(PANEL_WIDTH * 2, 0);
		createSimulationOutput();

		pnlSimulation = new RPanel(PANEL_WIDTH, PANEL_HEIGHT, "Simulation Control");
		pnlSimulation.setLocation(PANEL_WIDTH * 2, PANEL_HEIGHT);
		createSimulationComponents();

		// Add panels to frame
		getContentPane().add(pnlLocationMap);
		getContentPane().add(pnlRobotMotions);
		getContentPane().add(pnlRobotSettings);
		getContentPane().add(pnlOutput);
		getContentPane().add(pnlSimulation);

	}

	private void createWorldMapComponents() {

		// Calculate Suitable sell size
		int cols = (PANEL_WIDTH - 8) / DEF_NO_OF_COLUMNS;
		int rows = (PANEL_HEIGHT - LABEL_HEIGHT - 8) / DEF_NO_OF_ROWS;

		cellSize = Math.min(rows, cols);

		pnlLocationMap.pnlPublic.removeAll();
		lblLocationMap = new JLabel[DEF_NO_OF_ROWS][DEF_NO_OF_COLUMNS];
		pnlLocationMap.setLayoutMgr(null);
		for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
			for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {
				lblLocationMap[i][j] = new JLabel();
				lblLocationMap[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
				lblLocationMap[i][j].setPreferredSize(new Dimension(cellSize, cellSize));
				lblLocationMap[i][j].setBackground(sensors[world[i][j]]);
				lblLocationMap[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				lblLocationMap[i][j].setOpaque(true);
				pnlLocationMap.add(lblLocationMap[i][j]);
			}
		}
		pnlLocationMap.pnlPublic.doLayout();
		setWorldColors();

		image = new BufferedImage(DEF_NO_OF_COLUMNS * cellSize, DEF_NO_OF_ROWS * cellSize, BufferedImage.BITMASK);
		graphics = (Graphics2D) image.getGraphics();
	}

	private void createMotionComponents() {
		// Add Motion buttons
		pnlRobotMotions.setLayoutMgr(new GridLayout(3, 3, 5, 5));

		RobotMotionListener motionList = new RobotMotionListener();
		for (int i = 0; i < btnMotions.length; i++) {
			btnMotions[i].setActionCommand(String.valueOf(i));
			btnMotions[i].setIcon(new ImageIcon(getClass().getResource("/images/" + btnNames[i] + ".png")));
			btnMotions[i].setToolTipText(btnNames[i]);
			pnlRobotMotions.add(btnMotions[i]);
			btnMotions[i].addActionListener(motionList);
		}

	}

	private void createSimulationOutput() {
		pnlOutput.setLayoutMgr(new BorderLayout());
		ta = new JTextArea();
		ta.setLineWrap(false);
		ta.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setAutoscrolls(true);
		pnlOutput.add(scroll, BorderLayout.CENTER);
	}

	private void createSimulationComponents() {

		int cellSpacing = 3;

		int xLoc = 0;
		int yLoc = LABEL_HEIGHT;
		int width = PANEL_WIDTH / 2 - 1;
		int height = Math.min(PANEL_HEIGHT / 4, 30);

		RobotSimulationControlListener listener = new RobotSimulationControlListener();

		btnBuildSimulation = new JButton("Build Simulation");
		btnBuildSimulation.setBounds(xLoc + cellSpacing, yLoc + cellSpacing, width - cellSpacing, height - cellSpacing);
		btnBuildSimulation.addActionListener(listener);
		pnlSimulation.add(btnBuildSimulation);

		btnResetSimulation = new JButton("Reset Simulation");
		btnResetSimulation.setBounds(xLoc + width + cellSpacing, yLoc + cellSpacing, width - cellSpacing, height - cellSpacing);
		btnResetSimulation.addActionListener(listener);
		pnlSimulation.add(btnResetSimulation);

		rbStart = new JRadioButton("Start");
		yLoc += height;
		rbStart.setBounds(xLoc + cellSpacing, yLoc + cellSpacing, width - cellSpacing, height - cellSpacing);
		rbStart.addActionListener(listener);
		pnlSimulation.add(rbStart);
		rbStop = new JRadioButton("Stop");
		rbStop.setBounds(xLoc + width + cellSpacing, yLoc + cellSpacing, width - cellSpacing, height - cellSpacing);
		rbStop.addActionListener(listener);
		rbStop.setSelected(true);
		pnlSimulation.add(rbStop);
		ButtonGroup gp = new ButtonGroup();
		gp.add(rbStart);
		gp.add(rbStop);

		btnNext = new JButton("Next Step");
		yLoc += height;
		btnNext.setBounds(xLoc + cellSpacing, yLoc + cellSpacing, width - cellSpacing, height - cellSpacing);
		btnNext.addActionListener(listener);
		pnlSimulation.add(btnNext);

	}

	private void createSensorsComponents(JPanel pnlRobotSetting) {

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
			btnSensors[i].setActionCommand(String.valueOf(i));
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

	private void setWorldColors() {
		for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
			for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {
				lblLocationMap[i][j].setBackground(sensors[world[i][j] % DEF_NO_OF_COLORS]);
			}
		}
	}

	public void showOutPut(String txt) {
		ta.append(txt + "\n");
		ta.setCaretPosition(ta.getDocument().getLength());
	}

	private class RobotSimulationControlListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Object o = e.getSource();
			if (o.equals(btnBuildSimulation)) {
				SimulationBuilder sim = new SimulationBuilder(commands, DEF_NO_OF_COLORS, btnNames.length);
				commands = sim.getNewCommands();
				simulator.setCommands(commands);
				simulator.reset();
				repaint();
				ta.setText("");
			} else if (o.equals(btnResetSimulation)) {
				simulator.reset();
				ta.setText("");
			} else if (o.equals(btnNext)) {
				simulator.nextStep();
			} else if (o.equals(rbStart)) {
				simulator.simulate();
				btnNext.setEnabled(false);
				btnBuildSimulation.setEnabled(false);
				btnResetSimulation.setEnabled(false);
			} else if (o.equals(rbStop)) {
				simulator.setRunning(false);
				btnNext.setEnabled(true);
				btnBuildSimulation.setEnabled(true);
				btnResetSimulation.setEnabled(true);
				// btnApplySetting.setEnabled(true);
			}
		}

	}

	private class RobotControlListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JButton o = (JButton) e.getSource();
			if (o.equals(btnReset)) {
				filter.resetBelief();
				repaint();
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
				setWorldColors();

				filter.setWorld(world);
				// review motions commands
				for (int i = 0; i < commands.length; i++) {
					if (commands[i][1] == HistogramSimulator.SENSE) {
						commands[i][0] %= DEF_NO_OF_COLORS;
					} else if (commands[i][1] == HistogramSimulator.MOVE) {
						commands[i][0] %= btnNames.length;
					}
				}
				simulator.setCommands(commands);
				repaint();
			} else if (o.equals(btnWorldConfiguration)) {
				WorldBuilder gui = new WorldBuilder(world, DEF_NO_OF_COLORS, sensors);
				gui.setVisible(true);
				if (gui.isWorldChanged()) {
					world = gui.getNewWorld();
					DEF_NO_OF_ROWS = world.length;
					DEF_NO_OF_COLUMNS = world[0].length;
					filter.setWorld(world);
					createWorldMapComponents();
					// setWorldColors();
					repaint();
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
				repaint();
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
				repaint();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		graphics.setBackground(Color.red);
		graphics.setPaint(Color.RED);
		for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
			for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {

				graphics.setPaint(Color.WHITE);
				graphics.fillRect(j * (cellSize) + spacing, i * (cellSize) + spacing, cellSize - spacing, cellSize - spacing);

				Paint p = new Color(0, 0, 0, (int) (255 * Double.parseDouble(filter.getProbabilityAt(i, j))));
				graphics.setPaint(p);
				graphics.fillRect(j * (cellSize) + spacing, i * (cellSize) + spacing, cellSize - spacing, cellSize - spacing);

				graphics.setPaint(Color.RED);
				graphics.setFont(new Font("Arial", Font.BOLD, Math.min(cellSize, cellSize) / 4));
				String str = String.valueOf(filter.getProbabilityAt(i, j));

				FontMetrics matrix = graphics.getFontMetrics();
				int ht = matrix.getAscent();
				int wd = matrix.stringWidth(str);

				graphics.drawString(str, (j * (cellSize) + cellSize / 2 - wd / 2), (i * (cellSize) + cellSize / 2 + ht / 2));

			}
		}

		g.drawImage(image, PANEL_WIDTH + 10, 30 + LABEL_HEIGHT, this);
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
				String row_tag = MAP_ROW_TAG + (i + 1);
				try {
					String[] row = prop.getProperty(row_tag).split(",");
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
					System.out.println("Invalid value of tag " + row_tag + " ." + e.getMessage());
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
			// load robot commands
			String commands = prop.getProperty(ROBOT_COMMANDS_TAG);
			int[][] cmd;
			if (commands != null) {
				String[] arr = commands.split(";");
				cmd = new int[arr.length][];
				for (int i = 0; i < arr.length; i++) {
					String[] temp = arr[i].split(",");
					cmd[i] = new int[temp.length];

					int moveOrSense = Integer.parseInt(temp[1]) % 2;

					cmd[i][0] = Integer.parseInt(temp[0]);
					cmd[i][1] = moveOrSense;

					if (moveOrSense == HistogramSimulator.SENSE) {
						cmd[i][0] %= DEF_NO_OF_COLORS;
					} else if (moveOrSense == HistogramSimulator.MOVE) {
						cmd[i][0] %= btnMotions.length;
					}

				}
				this.commands = cmd;
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

		// Save Robot Commands
		StringBuilder buff = new StringBuilder(commands.length);
		int i = 0;
		for (; i < commands.length - 1; i++) {
			buff.append(commands[i][0] + "," + commands[i][1] + ";");
		}
		if (commands.length > 0) {
			buff.append(commands[i][0] + "," + commands[i][1]);
			prop.setProperty(ROBOT_COMMANDS_TAG, buff.toString());
		}

		super.saveProperties();

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		HistogramFilterView view1 = new HistogramFilterView();
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
