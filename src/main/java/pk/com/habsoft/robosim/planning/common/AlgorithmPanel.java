/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pk.com.habsoft.robosim.planning.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pk.com.habsoft.robosim.internal.PropertiesListener;
import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.planning.algos.AStarAlgorithm;
import pk.com.habsoft.robosim.planning.algos.Algorithm;
import pk.com.habsoft.robosim.planning.algos.BFSAlgorithm;
import pk.com.habsoft.robosim.planning.algos.DFSAlgorithm;
import pk.com.habsoft.robosim.planning.algos.DynamicPrograming2D;
import pk.com.habsoft.robosim.planning.algos.DynamicPrograming3D;
import pk.com.habsoft.robosim.planning.algos.Heuristic;
import pk.com.habsoft.robosim.planning.internal.AlgorithmListener;
import pk.com.habsoft.robosim.planning.internal.DiscreteWorld;
import pk.com.habsoft.robosim.planning.internal.WorldListener;
import pk.com.habsoft.robosim.utils.UIUtils;

public class AlgorithmPanel extends RPanel implements ActionListener, PropertiesListener, WorldListener {

	private static final long serialVersionUID = 1L;

	ArrayList<AlgorithmListener> observers = new ArrayList<AlgorithmListener>();

	private static final String ALGORITHM = "ALGORITHM";
	private static final String LEFT_TURN_COST = "LEFT_TURN_COST";
	private static final String RIGHT_TURN_COST = "RIGHT_TURN_COST";
	private static final String GO_STRAIGHT_COST = "GO_STRAIGHT_COST";
	private static final String USE_HEURISTICS = "USE_HEURISTICS";
	private static final String SHOW_HEURISTICS = "SHOW_HEURISTICS";
	private static final String DIAGONAL_MOTION = "DIAGONAL_MOTION";
	private static final String USE_ORIENTATION = "USE_ORIENTATION";
	private static final String HEURISTICS = "HEURISTICS";
	private static final String USE_STOCHASTIC_MOTION = "USE_STOCHASTIC_MOTION";
	private static final String P_SUCCESS = "P_SUCCESS";

	private int algorithm = Algorithm.A_STAR;
	private int heuristic = Heuristic.MANHATTAN;
	private boolean useHeuristics, showHeuristics, diagonalMotion, useOrientation, useStochasticMotion;
	int leftTurnCost, rightTurnCost, goStraightCost;
	double successProb = 0.7;
	Algorithm algo;

	JRadioButton rbDFS, rbBFS, rbAStar, rbDP;

	JCheckBox cbUseHeuristic, cbShowHuristicVal, cbAllowDiagonalMotion, cbConsiderOrientation, cbStochasticMotion;
	JComboBox<String> cmbHeuristics;
	JLabel lblHeuristic;
	JSpinner spRightTurnCost, spLeftTurnCost, spGoStraightCost, spSuccessProb;
	JButton btnApplyCostSettings;

	Properties prop;
	DiscreteWorld world = null;

	public AlgorithmPanel(DiscreteWorld world, Properties props, int width, int height, String label) {
		super(width, height, label);
		this.world = world;
		this.prop = props;
		loadProperties();

		setLayoutMgr(new BorderLayout());

		JPanel pnl = new JPanel();
		createControlPanelContents(pnl);

		add(pnl, BorderLayout.NORTH);
	}

	private void createControlPanelContents(JPanel pnlNorth) {

		pnlNorth.setLayout(new GridLayout(9, 2, 10, 3));

		cbShowHuristicVal = new JCheckBox("Show Heuristic Value");
		cbShowHuristicVal.setBackground(Color.ORANGE);
		pnlNorth.add(cbShowHuristicVal);
		cbShowHuristicVal.setSelected(showHeuristics);
		cbShowHuristicVal.addActionListener(this);
		// cbShowHuristicVal.setEnabled(false);

		cbAllowDiagonalMotion = new JCheckBox("Allow Diagonal Motion");
		cbAllowDiagonalMotion.setBackground(Color.ORANGE);
		pnlNorth.add(cbAllowDiagonalMotion);
		cbAllowDiagonalMotion.setSelected(diagonalMotion);
		cbAllowDiagonalMotion.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		rbDFS = new JRadioButton("Depth First Algorithm");
		rbDFS.setBackground(Color.GREEN);
		pnlNorth.add(rbDFS);
		gp.add(rbDFS);
		rbDFS.setSelected(algorithm == Algorithm.DFS);
		rbDFS.addActionListener(this);

		rbBFS = new JRadioButton("Breadth First Algorithm");
		rbBFS.setBackground(Color.GREEN);
		pnlNorth.add(rbBFS);
		gp.add(rbBFS);
		rbBFS.setSelected(algorithm == Algorithm.BFS);
		rbBFS.addActionListener(this);

		rbAStar = new JRadioButton("A* Algorithm");
		rbAStar.setBackground(Color.GREEN);
		pnlNorth.add(rbAStar);
		gp.add(rbAStar);
		rbAStar.setSelected(algorithm == Algorithm.A_STAR);
		rbAStar.addActionListener(this);

		// pnlNorth.add(new JLabel());

		cbUseHeuristic = new JCheckBox("Use Heuristic Function");
		pnlNorth.add(cbUseHeuristic);
		cbUseHeuristic.setSelected(useHeuristics);
		cbUseHeuristic.addActionListener(this);

		// pnlNorth.add(new JLabel());

		cmbHeuristics = new JComboBox<String>(Heuristic.HURISTIC_NAMES);
		pnlNorth.add(cmbHeuristics);
		cmbHeuristics.setSelectedIndex(heuristic);
		cmbHeuristics.setEnabled(cbUseHeuristic.isSelected());
		cmbHeuristics.addActionListener(this);

		lblHeuristic = new JLabel(Heuristic.HURISTIC_FUNCTIONS[heuristic]);
		pnlNorth.add(lblHeuristic);

		// Dynamic Programming
		rbDP = new JRadioButton("Dynamic Programming");
		rbDP.setBackground(Color.GREEN);
		pnlNorth.add(rbDP);
		rbDP.setSelected(algorithm == Algorithm.DP);
		gp.add(rbDP);
		rbDP.addActionListener(this);

		pnlNorth.add(new JLabel());

		// pnlNorth.add(new JLabel());

		cbStochasticMotion = new JCheckBox();
		pnlNorth.add(cbStochasticMotion);
		cbStochasticMotion.setText(" Stochastic Motion");
		cbStochasticMotion.setSelected(useStochasticMotion);
		cbStochasticMotion.addActionListener(this);
		cbStochasticMotion.setEnabled(rbDP.isSelected());

		spSuccessProb = new JSpinner();
		pnlNorth.add(UIUtils.createSpinnerPanel(" Success Probability", spSuccessProb, successProb, 0.5, 1, 0.1));
		spSuccessProb.setEnabled(cbStochasticMotion.isSelected() && rbDP.isSelected());
		spSuccessProb.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				successProb = Double.parseDouble(spSuccessProb.getValue().toString());
				initAlgorithm();
			}
		});

		cbConsiderOrientation = new JCheckBox("Consider Orientation");
		pnlNorth.add(cbConsiderOrientation);
		cbConsiderOrientation.setSelected(useOrientation);
		cbConsiderOrientation.setEnabled(rbDP.isSelected());
		cbConsiderOrientation.addActionListener(this);

		spLeftTurnCost = new JSpinner();
		pnlNorth.add(UIUtils.createSpinnerPanel(" Left Turn Cost", spLeftTurnCost, leftTurnCost, 0, 50, 1));
		spLeftTurnCost.setEnabled(cbConsiderOrientation.isSelected() && rbDP.isSelected());

		spRightTurnCost = new JSpinner();
		pnlNorth.add(UIUtils.createSpinnerPanel(" Right Turn Cost", spRightTurnCost, rightTurnCost, 0, 50, 1));
		spRightTurnCost.setEnabled(cbConsiderOrientation.isSelected() && rbDP.isSelected());

		spGoStraightCost = new JSpinner();
		pnlNorth.add(UIUtils.createSpinnerPanel(" Go Straight Cost", spGoStraightCost, goStraightCost, 0, 50, 1));
		spGoStraightCost.setEnabled(cbConsiderOrientation.isSelected() && rbDP.isSelected());

		btnApplyCostSettings = new JButton("Apply Cost Settings");
		pnlNorth.add(btnApplyCostSettings);
		btnApplyCostSettings.addActionListener(this);
		// btnApplyCostSettings.setEnabled(cbConsiderOrientation.isSelected());

	}

	@Override
	public boolean loadProperties() {
		if (prop != null) {
			if (prop.containsKey(ALGORITHM)) {
				try {
					algorithm = Integer.parseInt(prop.getProperty(ALGORITHM));
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(HEURISTICS)) {
				try {
					heuristic = Integer.parseInt(prop.getProperty(HEURISTICS));
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(USE_HEURISTICS)) {
				try {
					useHeuristics = prop.getProperty(USE_HEURISTICS).equalsIgnoreCase("true");
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(SHOW_HEURISTICS)) {
				try {
					showHeuristics = prop.getProperty(SHOW_HEURISTICS).equalsIgnoreCase("true");
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(DIAGONAL_MOTION)) {
				try {
					diagonalMotion = prop.getProperty(DIAGONAL_MOTION).equalsIgnoreCase("true");
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(USE_ORIENTATION)) {
				try {
					useOrientation = prop.getProperty(USE_ORIENTATION).equalsIgnoreCase("true");
				} catch (Exception e) {
				}
			}
			// Load cost for dynamic programming
			if (prop.containsKey(LEFT_TURN_COST)) {
				try {
					leftTurnCost = Integer.parseInt(prop.getProperty(LEFT_TURN_COST));
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(RIGHT_TURN_COST)) {
				try {
					rightTurnCost = Integer.parseInt(prop.getProperty(RIGHT_TURN_COST));
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(GO_STRAIGHT_COST)) {
				try {
					goStraightCost = Integer.parseInt(prop.getProperty(GO_STRAIGHT_COST));
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(USE_STOCHASTIC_MOTION)) {
				try {
					useStochasticMotion = prop.getProperty(USE_STOCHASTIC_MOTION).equalsIgnoreCase("true");
				} catch (Exception e) {
				}
			}
			if (prop.containsKey(P_SUCCESS)) {
				try {
					successProb = Double.parseDouble(prop.getProperty(P_SUCCESS));
				} catch (Exception e) {
				}
			}
		} else {
			System.out.println("Loading Null properties in Drawing Panel");
		}
		return true;
	}

	@Override
	public void saveProperties() {
		if (prop != null) {
			prop.setProperty(LEFT_TURN_COST, Integer.toString(leftTurnCost));
			prop.setProperty(RIGHT_TURN_COST, Integer.toString(rightTurnCost));
			prop.setProperty(GO_STRAIGHT_COST, Integer.toString(goStraightCost));

			prop.setProperty(USE_STOCHASTIC_MOTION, Boolean.toString(cbStochasticMotion.isSelected()));
			prop.setProperty(P_SUCCESS, Double.toString(successProb));

			prop.setProperty(ALGORITHM, Integer.toString(algorithm));
			prop.setProperty(USE_HEURISTICS, Boolean.toString(cbUseHeuristic.isSelected()));
			prop.setProperty(SHOW_HEURISTICS, Boolean.toString(cbShowHuristicVal.isSelected()));
			prop.setProperty(DIAGONAL_MOTION, Boolean.toString(cbAllowDiagonalMotion.isSelected()));
			prop.setProperty(USE_ORIENTATION, Boolean.toString(cbConsiderOrientation.isSelected()));
			prop.setProperty(HEURISTICS, Integer.toString(cmbHeuristics.getSelectedIndex()));
		} else {
			System.out.println("Saving Null properties in Drawing Panel");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(rbDFS)) {
			algorithm = Algorithm.DFS;
		} else if (o.equals(rbBFS)) {
			algorithm = Algorithm.BFS;
		} else if (o.equals(rbAStar)) {
			algorithm = Algorithm.A_STAR;
		} else if (o.equals(cbUseHeuristic)) {
			cmbHeuristics.setEnabled(cbUseHeuristic.isSelected());
		} else if (o.equals(cmbHeuristics)) {
			heuristic = cmbHeuristics.getSelectedIndex();
			lblHeuristic.setText(Heuristic.HURISTIC_FUNCTIONS[heuristic]);

		} else if (o.equals(rbDP)) {
			algorithm = Algorithm.DP;
		} else if (o.equals(btnApplyCostSettings)) {
			try {
				leftTurnCost = Integer.parseInt(spLeftTurnCost.getValue().toString());
				rightTurnCost = Integer.parseInt(spRightTurnCost.getValue().toString());
				goStraightCost = Integer.parseInt(spGoStraightCost.getValue().toString());
				successProb = Double.parseDouble(spSuccessProb.getValue().toString());
			} catch (Exception e2) {
			}
		} else if (o.equals(cbStochasticMotion)) {
			successProb = Double.parseDouble(spSuccessProb.getValue().toString());
		} else if (o.equals(cbConsiderOrientation)) {
		}
		spSuccessProb.setEnabled(cbStochasticMotion.isSelected() && rbDP.isSelected());
		cbStochasticMotion.setEnabled(rbDP.isSelected());
		cbConsiderOrientation.setEnabled(rbDP.isSelected());
		spLeftTurnCost.setEnabled(cbConsiderOrientation.isSelected() && rbDP.isSelected());
		spRightTurnCost.setEnabled(cbConsiderOrientation.isSelected() && rbDP.isSelected());
		spGoStraightCost.setEnabled(cbConsiderOrientation.isSelected() && rbDP.isSelected());
		btnApplyCostSettings.setEnabled(rbDP.isSelected());

		initAlgorithm();

	}

	public void initAlgorithm() {
		// Create algorithm to solve world

		try {
			if (cbStochasticMotion.isSelected())
				successProb = Double.parseDouble(spSuccessProb.getValue().toString());
			else
				successProb = 1;

			if (algorithm == Algorithm.DFS) {
				algo = new DFSAlgorithm(world, cbAllowDiagonalMotion.isSelected());
			} else if (algorithm == Algorithm.BFS) {
				algo = new BFSAlgorithm(world, cbAllowDiagonalMotion.isSelected());
			} else if (algorithm == Algorithm.A_STAR) {
				if (cbUseHeuristic.isSelected()) {
					heuristic = cmbHeuristics.getSelectedIndex();
				} else {
					heuristic = Heuristic.NONE;
				}
				algo = new AStarAlgorithm(world, heuristic, cbAllowDiagonalMotion.isSelected());
			} else if (algorithm == Algorithm.DP) {
				if (cbConsiderOrientation.isSelected()) {
					int[] cost = { rightTurnCost, goStraightCost, leftTurnCost };
					algo = new DynamicPrograming3D(world, cbAllowDiagonalMotion.isSelected(), cost, successProb);
				} else {
					algo = new DynamicPrograming2D(world, cbAllowDiagonalMotion.isSelected(), successProb);
				}
			}

			for (Iterator<AlgorithmListener> iter = observers.iterator(); iter.hasNext();) {
				AlgorithmListener type = iter.next();
				type.algorithmUpdate(algo);
			}
		} catch (Exception e) {
			if (e.getCause() instanceof OutOfMemoryError) {

			} else
				e.printStackTrace();
		}

	}

	public boolean isShowHeuristicValue() {
		return cbShowHuristicVal.isSelected();
	}

	// public void setWorld(World world) {
	// this.world = world;
	// }

	public void addAlgorithmObserver(AlgorithmListener listener) {
		observers.add(listener);
	}

	@Override
	public void worldUpdate(DiscreteWorld world) {
		this.world = world;
		initAlgorithm();
	}

}
