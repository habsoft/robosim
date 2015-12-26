package pk.com.habsoft.robosim.planning.pathplanning.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.planning.algos.Algorithm;
import pk.com.habsoft.robosim.planning.common.AlgorithmPanel;
import pk.com.habsoft.robosim.planning.common.StatisticsPanel;
import pk.com.habsoft.robosim.planning.internal.AlgorithmListener;
import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.utils.ImageUtil;
import pk.com.habsoft.robosim.utils.Util;

public class PathPlannerView extends RootView implements AlgorithmListener {

	private static final long serialVersionUID = 1L;

	private static final String NO_OF_ROWS_TAG = "NO_OF_ROWS";
	private static final String NO_OF_COLUMNS_TAG = "NO_OF_COLUMNS";
	private static final String MAP_ROW_TAG = "MAP_ROW_";
	private static final String START_NODE = "START_NODE_TAG";
	private static final String GOAL_NODE = "GOAL_NODE_TAG";

	DiscreteWorld world;
	// int startX, startY, goalX, goalY;
	ArrayList<int[]> path = new ArrayList<int[]>();

	JButton btnModifyWorld, btnApplyCostSettings;

	public PathPlannerView() {
		super("Path Planner", "PathPlanner.properties");
		setLayout(null);
		loadProperties();
		setLocation(0, 0);
		setSize(screenSize);
	}

	static int MAX_NO_OF_ROWS = 10;
	static int MIN_NO_OF_ROWS = 2;
	static int DEF_NO_OF_ROWS = 5;
	static int MAX_NO_OF_COLUMNS = 10;
	static int MIN_NO_OF_COLUMNS = 2;
	static int DEF_NO_OF_COLUMNS = 4;

	int PANEL_WORLD_WIDTH;
	int PANEL_WORLD_HEIGHT;
	int PANEL_CONTROL_WIDTH;
	int PANEL_CONTROL_HEIGHT;
	int PANEL_OUTPUT_WIDTH;
	int PANEL_OUTPUT_HEIGHT;
	int PANEL_SOUTH_HEIGHT;
	int PANEL_SOUTH_WIDTH;

	JLabel[][] lblLocationMap;

	RPanel pnlLocationMap;
	AlgorithmPanel pnlAlgorithm;
	StatisticsPanel pnlStatistics;
	RPanel pnlSouth;

	public void initGUI() {
		isInit = true;

		// Robot Location Panel
		pnlLocationMap = new RPanel(PANEL_WORLD_WIDTH, PANEL_WORLD_HEIGHT, "World Map");
		pnlLocationMap.setLocation(0, 0);
		createWorldMap();

		// Algorithm Panel
		pnlAlgorithm = new AlgorithmPanel(world, prop, PANEL_CONTROL_WIDTH, PANEL_CONTROL_HEIGHT, "Algorithms");
		pnlAlgorithm.setLocation(PANEL_WORLD_WIDTH, 0);
		pnlAlgorithm.addAlgorithmObserver(this);

		// Output Panel
		pnlStatistics = new StatisticsPanel(PANEL_OUTPUT_WIDTH, PANEL_OUTPUT_HEIGHT, "Statistics Panel");
		pnlStatistics.setLocation(PANEL_WORLD_WIDTH, PANEL_CONTROL_HEIGHT);

		// South Panel
		pnlSouth = new RPanel(PANEL_SOUTH_WIDTH, PANEL_SOUTH_HEIGHT, "World Configuration");
		pnlSouth.setLocation(0, PANEL_WORLD_HEIGHT);
		createSouthPanelContents(pnlSouth);
		// Add panels to frame
		getContentPane().add(pnlLocationMap);
		getContentPane().add(pnlAlgorithm);
		getContentPane().add(pnlStatistics);
		getContentPane().add(pnlSouth);

		modifyWorld(100);

		pnlAlgorithm.addAlgorithmObserver(pnlStatistics);
		pnlAlgorithm.initAlgorithm();
	}

	private void createWorldMap() {
		pnlLocationMap.removeAll();
		lblLocationMap = new JLabel[DEF_NO_OF_ROWS][DEF_NO_OF_COLUMNS];
		pnlLocationMap.setLayout(new GridLayout(DEF_NO_OF_ROWS, DEF_NO_OF_COLUMNS, 1, 1), true);

		for (int i = 0; i < DEF_NO_OF_ROWS; i++) {
			for (int j = 0; j < DEF_NO_OF_COLUMNS; j++) {
				lblLocationMap[i][j] = new JLabel();
				lblLocationMap[i][j].setOpaque(true);
				lblLocationMap[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				pnlLocationMap.add(lblLocationMap[i][j]);

			}
		}
		pnlLocationMap.doLayout();
		pnlLocationMap.repaint();
	}

	private void createSouthPanelContents(RPanel pnlSouth) {
		pnlSouth.setLayout(new FlowLayout(), false);
		pnlSouth.add(btnModifyWorld = new JButton("Modify World"));
		btnModifyWorld.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = e.getSource();
				if (o.equals(btnModifyWorld)) {
					WorldBuilder b = new WorldBuilder(world);
					b.setVisible(true);
					if (b.isWorldChanged()) {
						world = b.getNewWorld();
						DEF_NO_OF_ROWS = world.getRows();
						DEF_NO_OF_COLUMNS = world.getColumns();
						createWorldMap();
						modifyWorld(100);
						saveProperties();
						pnlAlgorithm.initAlgorithm();
					}
				}

			}
		});

		Dimension lblTempSize = new Dimension(100, 30);
		JLabel lblTemp = new JLabel(" Start");
		lblTemp.setOpaque(true);
		lblTemp.setBackground(Algorithm.colors[Algorithm.START]);
		lblTemp.setPreferredSize(lblTempSize);
		pnlSouth.add(lblTemp);

		lblTemp = new JLabel(" Goal");
		lblTemp.setOpaque(true);
		lblTemp.setBackground(Algorithm.colors[Algorithm.GOAL]);
		lblTemp.setPreferredSize(lblTempSize);
		pnlSouth.add(lblTemp);

		lblTemp = new JLabel(" Path");
		lblTemp.setOpaque(true);
		lblTemp.setBackground(Algorithm.colors[Algorithm.UP]);
		lblTemp.setForeground(Color.WHITE);
		lblTemp.setPreferredSize(lblTempSize);
		pnlSouth.add(lblTemp);

		lblTemp = new JLabel(" Explored");
		lblTemp.setOpaque(true);
		lblTemp.setBackground(Algorithm.colors[Algorithm.EXPLORED]);
		lblTemp.setPreferredSize(lblTempSize);
		pnlSouth.add(lblTemp);

		lblTemp = new JLabel(" Not Explored");
		lblTemp.setOpaque(true);
		lblTemp.setBackground(Algorithm.colors[Algorithm.NOT_EXPLORED]);
		lblTemp.setPreferredSize(lblTempSize);
		pnlSouth.add(lblTemp);

		lblTemp = new JLabel(" Blocks");
		lblTemp.setOpaque(true);
		lblTemp.setBackground(Algorithm.colors[Algorithm.BLOCK]);
		lblTemp.setForeground(Color.WHITE);
		lblTemp.setPreferredSize(lblTempSize);
		pnlSouth.add(lblTemp);

	}

	public void saveProperties() {

		prop.clear();
		// save world
		prop.setProperty(NO_OF_ROWS_TAG, "" + DEF_NO_OF_ROWS);
		prop.setProperty(NO_OF_COLUMNS_TAG, "" + DEF_NO_OF_COLUMNS);

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

		super.saveProperties();

	}

	@SuppressWarnings("unused")
	public boolean loadProperties() {
		System.out.println("Property File = " + propertyFile);

		PANEL_SOUTH_HEIGHT = 120;
		PANEL_SOUTH_WIDTH = (int) screenSize.getWidth() - 50;

		PANEL_CONTROL_WIDTH = 500;
		PANEL_CONTROL_HEIGHT = 350;
		PANEL_OUTPUT_WIDTH = PANEL_CONTROL_WIDTH;
		PANEL_OUTPUT_HEIGHT = (int) screenSize.getHeight() - PANEL_CONTROL_HEIGHT - PANEL_SOUTH_HEIGHT - 100;

		PANEL_WORLD_WIDTH = (int) screenSize.getWidth() - PANEL_OUTPUT_WIDTH - 50;
		PANEL_WORLD_HEIGHT = (int) screenSize.getHeight() - PANEL_SOUTH_HEIGHT - 100;

		boolean trueWorld = true;
		int startX = 0, startY = 0, goalX = 0, goalY = 0;
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
			int[][] temp = { { 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 1, 0, 1, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 1, 0 } };
			DEF_NO_OF_ROWS = temp.length;
			DEF_NO_OF_COLUMNS = temp[0].length;
			map = new int[DEF_NO_OF_ROWS][DEF_NO_OF_COLUMNS];
			int i = 0;
			for (int[] a : temp) {
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

		return true;
	}

	private boolean isPath(int x, int y) {
		for (int i = 0; i < path.size(); i++) {
			int[] arr = path.get(i);
			if (arr[0] == x && arr[1] == y) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		PathPlannerView view1 = new PathPlannerView();
		view1.initGUI();

		desk.add(view1);
		view1.setVisible(true);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();

		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
						lblLocationMap[i][j].setBackground(Color.BLACK);
						lblLocationMap[i][j].setIcon(null);
					}
				}
			}
		}
	}

	@Override
	public void algorithmUpdate(Algorithm algorithm) {
		if (algorithm != null) {
			int[][] policy = algorithm.getPolicy();
			double[][] expand = algorithm.getExpand();

			// Show out put
			for (int i = 0; i < world.getRows(); i++) {
				for (int j = 0; j < world.getColumns(); j++) {
					if (!isPath(i, j)) {
						lblLocationMap[i][j].setBackground(Algorithm.colors[policy[i][j]]);
						int resizeImage = 40 - Math.max(DEF_NO_OF_COLUMNS, DEF_NO_OF_ROWS) * 2;
						if (policy[i][j] >= Algorithm.UP && policy[i][j] <= Algorithm.UP_RIGHT) {
							File f = new File(
									"images" + File.separatorChar + Algorithm.DELTA_NAMES[policy[i][j]] + ".png");
							BufferedImage image = null;
							try {
								image = ImageIO.read(f);
								image = ImageUtil.resize(image, resizeImage, resizeImage);
								lblLocationMap[i][j].setIcon(new ImageIcon(image));
							} catch (IOException e) {
							}
						} else {
							lblLocationMap[i][j].setIcon(null);
						}
						lblLocationMap[i][j].setToolTipText(i + " : " + j);
						if (pnlAlgorithm.isShowHeuristicValue()) {
							lblLocationMap[i][j].setText("" + Util.round(algorithm.getHeuristic()[i][j], 2));
						} else {
							lblLocationMap[i][j].setText("" + Util.round(expand[i][j], 2));
						}
					}
				}
			}

		} else {
			for (int i = 0; i < world.getRows(); i++) {
				for (int j = 0; j < world.getColumns(); j++) {
					lblLocationMap[i][j].setBackground(Algorithm.colors[world.getGrid()[i][j] + 9]);
					lblLocationMap[i][j].setIcon(null);
				}
			}

		}
		lblLocationMap[world.getStart().getxLoc()][world.getStart().getyLoc()].setBackground(Color.RED);
		lblLocationMap[world.getGoal().getxLoc()][world.getGoal().getyLoc()].setBackground(Color.GREEN);
	}

}
