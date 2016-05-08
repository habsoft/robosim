package pk.com.habsoft.robosim.planning.pathplanning.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.utils.Util;
import pk.com.habsoft.robosim.utils.UIUtils;

public class WorldBuilder extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	DiscreteWorld world;
	int[][] newGrid;
	Color[] colors = { Color.WHITE, Color.BLACK };
	int totalColors = colors.length;
	private JButton[][] btnArray;
	final static int FRAME_WIDTH = 500, FRAME_HEIGHT = 600;
	final static int PNL_NORTH_HEIGHT = 400;
	int startX, startY, goalX, goalY;

	JLabel lblText;
	private JButton btnOk, btnCancel;
	private static int WORLD = 0, START = 1, GOAL = 2;
	int mode = WORLD;
	JRadioButton chkWorld, chkStart, chkGoal;

	JPanel pnlNorth, pnlSouth;

	boolean worldChanged = false;
	JSpinner spnRows, spnCols;

	public WorldBuilder(DiscreteWorld w) {
		this.setTitle("World Builder");
		this.world = w;
		startX = w.getStart().getxLoc();
		startY = w.getStart().getyLoc();
		goalX = w.getGoal().getxLoc();
		goalY = w.getGoal().getyLoc();
		newGrid = new int[w.getRows()][w.getColumns()];
		for (int i = 0; i < w.getRows(); i++) {
			for (int j = 0; j < w.getColumns(); j++) {
				newGrid[i][j] = w.getGrid()[i][j];
				if (newGrid[i][j] > 1) {
					newGrid[i][j] = 1;
				}
			}
		}
		initGUI();
	}

	void initGUI() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();
		setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// North panel
		pnlNorth = new JPanel();
		pnlNorth.setLocation(0, 0);
		pnlNorth.setSize(FRAME_WIDTH, PNL_NORTH_HEIGHT);

		// South Panel
		pnlSouth = new JPanel();
		pnlSouth.setBounds(0, PNL_NORTH_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT - PNL_NORTH_HEIGHT);

		pnlSouth.add(chkStart = new JRadioButton("Specify Start State"));
		chkStart.addActionListener(this);
		pnlSouth.add(chkGoal = new JRadioButton("Specify Gaol State"));
		chkGoal.addActionListener(this);
		pnlSouth.add(chkWorld = new JRadioButton("Modify World"));
		chkWorld.addActionListener(this);
		chkWorld.setSelected(true);
		ButtonGroup gp = new ButtonGroup();
		gp.add(chkGoal);
		gp.add(chkStart);
		gp.add(chkWorld);

		spnRows = new JSpinner();
		pnlSouth.add(UIUtils.createSpinnerPanel("No of Rows", spnRows, world.getRows(), PathPlannerView.MIN_NO_OF_ROWS,
				PathPlannerView.MAX_NO_OF_ROWS, 1));
		spnRows.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				createComponents();
			}
		});

		spnCols = new JSpinner();
		pnlSouth.add(UIUtils.createSpinnerPanel("No of Columns", spnCols, world.getColumns(),
				PathPlannerView.MIN_NO_OF_COLUMNS, PathPlannerView.MAX_NO_OF_COLUMNS, 1));
		spnCols.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				createComponents();
			}
		});

		pnlSouth.add(lblText = new JLabel("Click on any cell to change its status."));
		pnlSouth.add(btnOk = new JButton("Ok"));
		btnOk.addActionListener(this);
		pnlSouth.add(btnCancel = new JButton("Cancel"));
		btnCancel.addActionListener(this);

		// ////////////////////////////////////////////

		setModalityType(ModalityType.APPLICATION_MODAL);

		add(pnlNorth);
		add(pnlSouth);
		setLocation(width / 2 - FRAME_WIDTH / 2, height / 2 - FRAME_HEIGHT / 2);
		setSize(FRAME_WIDTH + 20, FRAME_HEIGHT + 10);
		setMinimumSize(new Dimension(FRAME_WIDTH + 20, FRAME_HEIGHT + 10));
		createComponents();
		setStart(world.getStart().getxLoc(), world.getStart().getyLoc());
		setGoal(world.getGoal().getxLoc(), world.getGoal().getyLoc());
	}

	private void createComponents() {
		worldChanged = true;
		int rows = Integer.parseInt(spnRows.getValue().toString());
		int cols = Integer.parseInt(spnCols.getValue().toString());
		// pnlNorth = new JPanel();
		pnlNorth.removeAll();
		pnlNorth.setLayout(new GridLayout(rows, cols));
		btnArray = new JButton[rows][cols];
		newGrid = new int[rows][cols];
		for (int i = 0; i < Math.min(rows, world.getRows()); i++) {
			for (int j = 0; j < Math.min(cols, world.getColumns()); j++) {
				if (this.world.getGrid()[i][j] > 1) {
					newGrid[i][j] = 1;
				} else {
					newGrid[i][j] = this.world.getGrid()[i][j];
				}
			}
		}

		btnArray = new JButton[newGrid.length][newGrid[0].length];
		for (int i = 0; i < newGrid.length; i++) {
			for (int j = 0; j < newGrid[i].length; j++) {
				btnArray[i][j] = new JButton();
				btnArray[i][j].setBackground(colors[newGrid[i][j]]);
				btnArray[i][j].setActionCommand(Integer.toString(newGrid[i][j]));
				btnArray[i][j].addActionListener(this);

				pnlNorth.add(btnArray[i][j]);
			}
		}

		setStart(0, 0);
		setGoal(rows - 1, cols - 1);

		pnlNorth.doLayout();
	}

	public boolean isWorldChanged() {
		return worldChanged;
	}

	private void setStart(int xLoc, int yLoc) {
		if (startX < newGrid.length && startY < newGrid[0].length) {
			// Reset the old cell
			btnArray[startX][startY].setBackground(Color.WHITE);
		}
		// this.world.setStatus(startX, startY, World.OPEN);
		// Set the new cell to start state
		btnArray[xLoc][yLoc].setBackground(Color.RED);
		newGrid[xLoc][yLoc] = DiscreteWorld.OPEN;
		this.startX = xLoc;
		this.startY = yLoc;
		pnlNorth.repaint();

	}

	private void setGoal(int xLoc, int yLoc) {
		// Reset the old cell
		if (goalX < newGrid.length && goalY < newGrid[0].length) {
			// Reset the old cell
			btnArray[goalX][goalY].setBackground(Color.WHITE);
		}
		// Set the new cell to Goal state
		btnArray[xLoc][yLoc].setBackground(Color.GREEN);
		newGrid[xLoc][yLoc] = DiscreteWorld.OPEN;
		this.goalX = xLoc;
		this.goalY = yLoc;

	}

	public DiscreteWorld getNewWorld() {
		return this.world;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(btnOk)) {
			// world = new World(newGrid);
			world.setGrid(newGrid);
			world.setStartNode(startX, startY);
			world.setGoalNode(goalX, goalY);
			dispose();
		} else if (o.equals(btnCancel)) {
			worldChanged = false;
			dispose();
		} else if (o.equals(chkStart)) {
			mode = START;
		} else if (o.equals(chkGoal)) {
			mode = GOAL;
		} else if (o.equals(chkWorld)) {
			mode = WORLD;
		} else {

			outer: for (int i = 0; i < newGrid.length; i++) {
				for (int j = 0; j < newGrid[i].length; j++) {
					if (o.equals(btnArray[i][j])) {
						worldChanged = true;
						JButton btn = (JButton) o;
						if (mode == START && !(i == goalX && j == goalY)) {
							setStart(i, j);
							break outer;
						} else if (mode == GOAL && !(i == startX && j == startY)) {
							setGoal(i, j);
							break outer;
						} else if (!(i == startX && j == startY) && !(i == goalX && j == goalY)) {
							int newColor = Integer.parseInt(btn.getActionCommand());
							newColor = (newColor + 1) % totalColors;
							btn.setActionCommand("" + newColor);
							newGrid[i][j] = newColor;
							btn.setBackground(colors[newColor]);
							break outer;
						}
					}
				}
			}

		}
	}

	public static void main(String[] args) {
		int[][] map = { { 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 1, 0 }, { 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 1, 0 } };
		DiscreteWorld world = new DiscreteWorld(map);
		// world.setStartNode(3, 1);
		WorldBuilder gui = new WorldBuilder(world);
		gui.setVisible(true);
		System.out.println("Old World ");
		Util.printArrayP(map);
		// Util.printArrayP(gui.getNewWorld().getGrid());
		if (gui.isWorldChanged()) {
			System.out.println("New World ");
			System.out.println(gui.getNewWorld().printWorld());
			System.out.println("Start Loc" + gui.getNewWorld().getStart());
			System.out.println("Goal Loc" + gui.getNewWorld().getGoal());
		}
		// System.out.println(gui.getDefaultCloseOperation());

	}

}
