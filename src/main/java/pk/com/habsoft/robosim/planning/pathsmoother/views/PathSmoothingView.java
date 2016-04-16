package pk.com.habsoft.robosim.planning.pathsmoother.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.planning.common.AlgorithmPanel;
import pk.com.habsoft.robosim.planning.common.StatisticsPanel;
import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.planning.internal.WorldListener;

public class PathSmoothingView extends RootView implements WorldListener {

	private static final long serialVersionUID = 1L;

	private static final String NO_OF_ROWS_TAG = "NO_OF_ROWS";
	private static final String NO_OF_COLUMNS_TAG = "NO_OF_COLUMNS";
	private static final String MAP_ROW_TAG = "MAP_ROW_";
	private static final String START_NODE = "START_NODE_TAG";
	private static final String GOAL_NODE = "GOAL_NODE_TAG";

	DiscreteWorld world;

	RPanel pnlLocationMap;
	AlgorithmPanel pnlAlgorithms;
	StatisticsPanel pnlStatistics;
	SmoothingControlPanel pnlSmoothControl;
	DrawingPanel drawingPanel = null;
	DrawingControlPanel pnlDrawingControl;

	public PathSmoothingView() {
		super("Path Smoother", "config/PathSmoother.properties");
		setLayout(null);
		loadProperties();
		setSize(screenSize);
	}

	static int MAX_NO_OF_ROWS = 100;
	static int MIN_NO_OF_ROWS = 2;
	static int DEF_NO_OF_ROWS = 15;
	static int MAX_NO_OF_COLUMNS = 100;
	static int MIN_NO_OF_COLUMNS = 2;
	static int DEF_NO_OF_COLUMNS = 15;

	int PANEL_WORLD_WIDTH;
	int PANEL_WORLD_HEIGHT;
	int PANEL_CONTROL_WIDTH;
	int PANEL_CONTROL_HEIGHT;
	int PANEL_SMOOTH_WIDTH;
	int PANEL_SMOOTH_HEIGHT;
	int PANEL_OUTPUT_WIDTH;
	int PANEL_OUTPUT_HEIGHT;
	int PANEL_SOUTH_HEIGHT;
	int PANEL_SOUTH_WIDTH;

	@Override
	public void initGUI() {
		isInit = true;
		// Robot Location Panel
		pnlLocationMap = new RPanel(PANEL_WORLD_WIDTH, PANEL_WORLD_HEIGHT, "World Map");
		pnlLocationMap.setLocation(0, 0);

		drawingPanel = new DrawingPanel(world, PANEL_WORLD_WIDTH, PANEL_WORLD_HEIGHT - 30);
		pnlLocationMap.add(drawingPanel, BorderLayout.CENTER);

		// Controls Panel
		pnlAlgorithms = new AlgorithmPanel(world, prop, PANEL_CONTROL_WIDTH, PANEL_CONTROL_HEIGHT, "Algorithms Panel");
		pnlAlgorithms.setLocation(PANEL_WORLD_WIDTH, 0);

		// Smoothing Panel
		pnlSmoothControl = new SmoothingControlPanel(prop, PANEL_SMOOTH_WIDTH, PANEL_SMOOTH_HEIGHT, "Smothing Panel");
		pnlSmoothControl.setLocation(PANEL_WORLD_WIDTH, PANEL_CONTROL_HEIGHT);
		pnlSmoothControl.setDrawingPanel(drawingPanel);
		// createOutputPanelContents(pnlOutput);

		// Statistics Panel
		pnlStatistics = new StatisticsPanel(PANEL_OUTPUT_WIDTH, PANEL_OUTPUT_HEIGHT, "Output Panel");
		pnlStatistics.setLocation(PANEL_WORLD_WIDTH, PANEL_CONTROL_HEIGHT + PANEL_SMOOTH_HEIGHT);

		// South Control Panel
		pnlDrawingControl = new DrawingControlPanel(world.getRows(), world.getColumns());
		pnlDrawingControl.setBounds(0, PANEL_WORLD_HEIGHT, PANEL_SOUTH_WIDTH, PANEL_SOUTH_HEIGHT);
		pnlDrawingControl.setDrawingPanel(drawingPanel);

		// createSouthPanelContents(pnlSouth);
		// Add panels to frame
		getContentPane().add(pnlLocationMap);
		getContentPane().add(pnlAlgorithms);
		getContentPane().add(pnlSmoothControl);
		getContentPane().add(pnlStatistics);
		getContentPane().add(pnlDrawingControl);

		// add world observers
		drawingPanel.addWorldObserver(pnlAlgorithms);
		drawingPanel.addWorldObserver(this);
		// add algorithm observers
		pnlAlgorithms.addAlgorithmObserver(pnlStatistics);
		pnlAlgorithms.addAlgorithmObserver(drawingPanel);

		pnlAlgorithms.initAlgorithm();
	}

	@Override
	public void saveProperties() {

		prop.clear();
		// save world

		prop.setProperty(NO_OF_ROWS_TAG, "" + world.getRows());
		prop.setProperty(NO_OF_COLUMNS_TAG, "" + world.getColumns());

		for (int i = 0; i < world.getRows(); i++) {
			String row = "";
			int value = 0;
			for (int j = 0; j < world.getColumns(); j++) {
				value = world.getGrid()[i][j];
				if (value > 1) {
					value = 1;
				}
				row += value + ",";
			}
			prop.setProperty(MAP_ROW_TAG + (i + 1), row);
		}
		prop.setProperty(START_NODE, world.getStart().getxLoc() + "," + world.getStart().getyLoc());
		prop.setProperty(GOAL_NODE, world.getGoal().getxLoc() + "," + world.getGoal().getyLoc());

		pnlSmoothControl.saveProperties();
		pnlAlgorithms.saveProperties();

		// Save others properties

		try {
			FileOutputStream out = new FileOutputStream(propertyFile);
			prop.store(out, "");
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	@Override
	public boolean loadProperties() {
		System.out.println("Property File = " + propertyFile);

		// Algorithms
		PANEL_CONTROL_WIDTH = 500;
		PANEL_CONTROL_HEIGHT = 300;

		// Smoothing
		PANEL_SMOOTH_WIDTH = PANEL_CONTROL_WIDTH;
		PANEL_SMOOTH_HEIGHT = 180;

		// Output (Statistics)
		PANEL_OUTPUT_WIDTH = PANEL_CONTROL_WIDTH;
		PANEL_OUTPUT_HEIGHT = (int) screenSize.getHeight() - PANEL_CONTROL_HEIGHT - PANEL_SMOOTH_HEIGHT - 150;

		// Bottom panel (drawing control)
		PANEL_SOUTH_WIDTH = (int) screenSize.getWidth() - PANEL_SMOOTH_WIDTH - 50;
		PANEL_SOUTH_HEIGHT = 100;

		PANEL_WORLD_WIDTH = PANEL_SOUTH_WIDTH;
		PANEL_WORLD_HEIGHT = (int) screenSize.getHeight() - PANEL_SOUTH_HEIGHT - 100;

		int startX = 0, startY = 0, goalX = 0, goalY = 0;

		boolean trueWorld = true;
		int[][] map = null;
		if (super.loadProperties()) {
			// No of rows
			if (prop.containsKey(NO_OF_ROWS_TAG)) {
				try {
					int noOfRows = Integer.parseInt(prop.getProperty(NO_OF_ROWS_TAG));
					if (noOfRows > MAX_NO_OF_ROWS || noOfRows < MIN_NO_OF_ROWS) {
						System.out.println("Invalid value of tag " + NO_OF_ROWS_TAG + " .Expedted : " + MIN_NO_OF_ROWS
								+ "-" + MAX_NO_OF_ROWS + ".Loading Default");
						trueWorld = false;
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
						System.out.println("Invalid value of tag " + NO_OF_COLUMNS_TAG + " .Expedted : "
								+ MIN_NO_OF_COLUMNS + "-" + MAX_NO_OF_COLUMNS + ".Loading Default");
						trueWorld = false;
					} else {
						DEF_NO_OF_COLUMNS = noOfColumns;
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + NO_OF_COLUMNS_TAG + ".Loading Default");
				}
			}

			if (prop.containsKey(START_NODE)) {
				try {
					String[] tokens = prop.getProperty(START_NODE).split(",");
					startX = Integer.parseInt(tokens[0]);
					startY = Integer.parseInt(tokens[1]);
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + START_NODE + ".Loading Default");
				}
			}
			if (prop.containsKey(GOAL_NODE)) {
				try {
					String[] tokens = prop.getProperty(GOAL_NODE).split(",");
					goalX = Integer.parseInt(tokens[0]);
					goalY = Integer.parseInt(tokens[1]);
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + GOAL_NODE + ".Loading Default");
				}
			}

			// Load world
			map = new int[DEF_NO_OF_ROWS][DEF_NO_OF_COLUMNS];
			outer: for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
				String row_tag = MAP_ROW_TAG + (i + 1);
				try {
					String[] row = prop.getProperty(row_tag).split(",");
					// System.out.println("world " + i + " " +
					// Arrays.toString(row));
					for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {
						try {
							map[i][j] = Integer.parseInt(row[j]);
							if (map[i][j] > 2) {
								System.out.println("Invalid value at " + i + "," + j + "  Expecting 0|1 ");
								map[i][j] = 0;
							}
						} catch (Exception e) {
							System.out.println("e Invalid value at " + i + "," + j + "  Expecting 0|1 ");
							map[i][j] = 0;
						}
					}
				} catch (Exception e) {
					System.out.println("Invalid value of tag " + row_tag + " ." + e.getMessage());
					// trueWorld = false;
				}
			}

		} else {
			trueWorld = false;
			System.out.println("Invalid property file " + propertyFile + " .");
		}

		// if world is invalid then initialize random world
		if (!trueWorld) {
			System.out.println("Loading Default world");
			int[][] defMap = { { 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 1, 0, 1, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 1, 0 } };
			DEF_NO_OF_ROWS = defMap.length;
			DEF_NO_OF_COLUMNS = defMap[0].length;
			map = new int[DEF_NO_OF_ROWS][DEF_NO_OF_COLUMNS];
			int i = 0;
			for (int[] a : defMap) {
				System.arraycopy(a, 0, map[i], 0, a.length);
				i++;
			}
			goalX = DEF_NO_OF_ROWS - 1;
			goalY = DEF_NO_OF_COLUMNS - 1;
		}

		// Create world
		world = new DiscreteWorld(map);
		world.setStartNode(startX, startY);
		world.setGoalNode(goalX, goalY);

		modifyWorld(1000);

		return true;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (e.getID() == WindowEvent.WINDOW_CLOSING) {
					JInternalFrame frms[] = desk.getAllFrames();
					for (int i = 0; i < frms.length; i++) {
						try {
							if (frms[i] instanceof RootView) {
								((RootView) frms[i]).setClosed(true);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}
		});

		PathSmoothingView view1 = new PathSmoothingView();
		view1.initGUI();

		desk.add(view1);
		view1.setVisible(true);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();

		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	public void modifyWorld(int range) {
		int x = world.getTempStart().getxLoc();
		int y = world.getTempStart().getyLoc();
		for (int i = 0; i < world.getRows(); i++) {
			for (int j = 0; j < world.getColumns(); j++) {
				int d = (int) Math.sqrt(Math.pow(x - i, 2) + Math.pow(y - j, 2));
				if (d <= range) {
					if (world.isHidden(i, j)) {
						world.setStatus(i, j, DiscreteWorld.BLOCK);
						// lblLocationMap[i][j].setBackground(Algorithm.colors[Algorithm.BLOCK]);
						// lblLocationMap[i][j].setBackground(Color.BLACK);
						// lblLocationMap[i][j].setIcon(null);
					}
				}
			}
		}
	}

	@Override
	public void worldUpdate(DiscreteWorld world) {
		this.world = world;
	}

}
