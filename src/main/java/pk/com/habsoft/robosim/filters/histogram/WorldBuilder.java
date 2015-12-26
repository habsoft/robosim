package pk.com.habsoft.robosim.filters.histogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pk.com.habsoft.robosim.utils.UIUtils;

public class WorldBuilder extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	int[][] world;
	int[][] newWorld;
	int totalColors;
	Color[] colors;
	private JButton[][] btnArray;
	final static int FRAME_WIDTH = 400, FRAME_HEIGHT = 500;
	final static int PNL_NORTH_HEIGHT = 400;

	private JButton btnOk, btnCancel;
	boolean worldChanged = false;
	JSpinner spnRows, spnCols;

	JPanel pnlNorth, pnlSouth;

	public WorldBuilder(int[][] worldMap, int totalColors, Color[] colors) {

		setTitle("World Builder");
		this.world = worldMap;
		// newWorld = worldMap.clone();
		newWorld = new int[worldMap.length][worldMap[0].length];
		for (int i = 0; i < worldMap.length; i++) {
			for (int j = 0; j < worldMap[i].length; j++) {
				newWorld[i][j] = worldMap[i][j];
			}
		}
		this.totalColors = Math.min(totalColors, colors.length);
		this.colors = colors;
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

		spnRows = new JSpinner();
		pnlSouth.add(UIUtils.createSpinnerPanel("No of Rows", spnRows, world.length, HistogramFilterView.MIN_NO_OF_ROWS,
				HistogramFilterView.MAX_NO_OF_ROWS, 1));
		spnRows.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				createComponents();
			}
		});

		spnCols = new JSpinner();
		pnlSouth.add(UIUtils.createSpinnerPanel("No of Columns", spnCols, world[0].length,
				HistogramFilterView.MIN_NO_OF_COLUMNS, HistogramFilterView.MAX_NO_OF_COLUMNS, 1));
		spnCols.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				createComponents();
			}
		});

		pnlSouth.add(new JLabel("Click on any cell to change its color"));
		pnlSouth.add(btnOk = new JButton("Ok"));
		btnOk.addActionListener(this);
		pnlSouth.add(btnCancel = new JButton("Cancel"));
		btnCancel.addActionListener(this);

		createComponents();

		// ////////////////////////////////////////////

		setModalityType(ModalityType.APPLICATION_MODAL);

		add(pnlNorth);
		add(pnlSouth);
		setLocation(width / 2 - FRAME_WIDTH / 2, height / 2 - FRAME_HEIGHT / 2);
		setSize(FRAME_WIDTH + 20, FRAME_HEIGHT + 10);
		setMinimumSize(new Dimension(FRAME_WIDTH + 20, FRAME_HEIGHT + 10));
	}

	private void createComponents() {
		worldChanged = true;
		int rows = Integer.parseInt(spnRows.getValue().toString());
		int cols = Integer.parseInt(spnCols.getValue().toString());
		// pnlNorth = new JPanel();
		pnlNorth.removeAll();
		pnlNorth.setLayout(new GridLayout(rows, cols));
		btnArray = new JButton[rows][cols];
		newWorld = new int[rows][cols];
		for (int i = 0; i < Math.min(world.length, newWorld.length); i++) {
			System.arraycopy(world[i], 0, newWorld[i], 0, Math.min(world[i].length, newWorld[i].length));
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				btnArray[i][j] = new JButton();
				int newColor = newWorld[i][j] % totalColors;
				btnArray[i][j].setBackground(colors[newColor]);
				btnArray[i][j].setActionCommand("" + newColor);
				btnArray[i][j].addActionListener(this);
				// btnArray[i][j].setOpaque(true);
				// btnArray[i][j].setBorderPainted(false);

				pnlNorth.add(btnArray[i][j]);
			}
		}
		pnlNorth.doLayout();
	}

	public int[][] getNewWorld() {
		return this.newWorld;
	}

	public boolean isWorldChanged() {
		return worldChanged;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton o = (JButton) e.getSource();
		if (o.equals(btnOk)) {
			dispose();
		} else if (o.equals(btnCancel)) {
			worldChanged = false;
			this.newWorld = world;
			dispose();
		} else {
			for (int i = 0; i < newWorld.length; i++) {
				for (int j = 0; j < newWorld[i].length; j++) {
					if (o.equals(btnArray[i][j])) {
						int newColor = Integer.parseInt(o.getActionCommand());
						newColor = (newColor + 1) % totalColors;
						o.setActionCommand("" + newColor);
						newWorld[i][j] = newColor;
						o.setBackground(colors[newColor]);
						worldChanged = true;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA };
		int[][] worldMap = { { 0, 1, 2 }, { 2, 2, 1 } };
		WorldBuilder gui = new WorldBuilder(worldMap, 3, colors);
		gui.setVisible(true);
		if (gui.isWorldChanged()) {
			System.out.println("world changed");
		} else {
			System.out.println("Not changed");
		}
		// System.out.println("Old World ");
		// Util.printArrayP(worldMap);
		// System.out.println("New World ");
		// Util.printArrayP(gui.getNewWorld());
		// System.out.println(gui.getDefaultCloseOperation());

	}

}
