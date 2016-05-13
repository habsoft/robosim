/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pk.com.habsoft.robosim.planning.pathsmoother.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import pk.com.habsoft.robosim.internal.PropertiesListener;
import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.utils.UIUtils;

public class SmoothingControlPanel extends RPanel implements ActionListener, PropertiesListener {

	private static final long serialVersionUID = 1L;

	private final static char ALPHA = '\u03B1';
	private final static char BEETA = '\u03B2';

	private static final String TAG_SHOW_ACTUAL_PATH = "SHOW_ACTUAL_PATH";
	private static final String TAG_SHOW_SMOOTH_PATH = "SHOW_SMOOTH_PATH";
	private static final String TAG_SHOW_GRID = "SHOW_GRID";
	private static final String TAG_SMOOTH_BOUNDRIES = "SMOOTH_BOUNDRIES";
	private static final String TAG_WEIGHT_DATA = "WEIGHT_DATA";
	private static final String TAG_WEIGHT_SMOOTH = "WEIGHT_SMOOTH";
	private static final String TAG_SMOOTHING_TIMEOUT = "SMOOTHING_TIMEOUT";
	private static final String TAG_CELL_DIVISIONS = "CELL_DIVISIONS";

	private boolean showActualPath = true;
	private boolean showSmoothPath = true;
	private boolean showGrid = true;
	private boolean smoothBoundryPoints = false;
	private double weightData = 0.1;
	private double weightSmooth = 0.9;
	private int smoothingTimeout = 1000;
	private int cellDivisions = 5;

	Properties props;

	DrawingPanel drawingPanel = null;

	JCheckBox cbShowActualPath, cbShowSmoothPath, cbShowGrid, cbSmoothBoundryPoints;
	JSpinner spWeightData, spWeightSmooth, spCellDivisions, spSmoothingTimeout;
	JButton btnApply;

	public void setDrawingPanel(DrawingPanel observer) {

		this.drawingPanel = observer;
		this.drawingPanel.setShowActualPath(showActualPath);
		this.drawingPanel.setShowSmoothPath(showSmoothPath);
		this.drawingPanel.setShowGrid(showGrid);
		this.drawingPanel.setSmoothBoundryPoints(smoothBoundryPoints);
		this.drawingPanel.setWeightData(weightData);
		this.drawingPanel.setWeightSmooth(weightSmooth);
		this.drawingPanel.setCellDivisions(cellDivisions);
		this.drawingPanel.setSmotthingTimeout(smoothingTimeout);
		drawingPanel.smooth();
		this.drawingPanel.repaint();

	}

	public SmoothingControlPanel(Properties props, int width, int height, String label) {
		super(width, height, label);
		this.props = props;
		loadProperties();

		setLayoutMgr(new BorderLayout());

		JPanel pnl = new JPanel();
		pnl.setLayout(new GridLayout(5, 2, 10, 3));

		cbShowActualPath = new JCheckBox("Show Actual Path");
		pnl.add(cbShowActualPath);
		cbShowActualPath.setBackground(Color.RED);
		cbShowActualPath.addActionListener(this);
		cbShowActualPath.setSelected(showActualPath);

		cbShowSmoothPath = new JCheckBox("Show Smooth Path");
		pnl.add(cbShowSmoothPath);
		cbShowSmoothPath.setBackground(Color.GREEN);
		cbShowSmoothPath.addActionListener(this);
		cbShowSmoothPath.setSelected(showSmoothPath);

		cbShowGrid = new JCheckBox(" Show Grid");
		pnl.add(cbShowGrid);
		cbShowGrid.addActionListener(this);
		cbShowGrid.setSelected(showGrid);

		cbSmoothBoundryPoints = new JCheckBox(" Smooth Boundry Points");
		pnl.add(cbSmoothBoundryPoints);
		cbSmoothBoundryPoints.addActionListener(this);
		cbSmoothBoundryPoints.setSelected(smoothBoundryPoints);

		spCellDivisions = new JSpinner();
		pnl.add(UIUtils.createSpinnerPanel(" Cell Division", spCellDivisions, cellDivisions, 0, 50, 1));

		spSmoothingTimeout = new JSpinner();
		pnl.add(UIUtils.createSpinnerPanel(" Smoothing Timeout", spSmoothingTimeout, smoothingTimeout, 0,
				Integer.MAX_VALUE, 1));

		spWeightData = new JSpinner();
		pnl.add(UIUtils.createSpinnerPanel(" Weight Data( " + ALPHA + " )", spWeightData, weightData, 0, 1, 0.01));

		spWeightSmooth = new JSpinner();
		pnl.add(UIUtils.createSpinnerPanel(" Weight Smooth( " + BEETA + " )", spWeightSmooth, weightSmooth, 0, 1,
				0.01));

		btnApply = new JButton("Apply Setting");
		pnl.add(btnApply);
		btnApply.addActionListener(this);

		add(pnl, BorderLayout.NORTH);
	}

	@Override
	public boolean loadProperties() {
		if (props != null) {
			if (props.containsKey(TAG_SHOW_ACTUAL_PATH)) {
				showActualPath = props.getProperty(TAG_SHOW_ACTUAL_PATH, "true").equalsIgnoreCase("true");
			}
			if (props.containsKey(TAG_SHOW_SMOOTH_PATH)) {
				showSmoothPath = props.getProperty(TAG_SHOW_SMOOTH_PATH, "true").equalsIgnoreCase("true");
			}
			if (props.containsKey(TAG_SHOW_GRID)) {
				showGrid = props.getProperty(TAG_SHOW_GRID, "true").equalsIgnoreCase("true");
			}
			if (props.containsKey(TAG_SMOOTH_BOUNDRIES)) {
				smoothBoundryPoints = props.getProperty(TAG_SMOOTH_BOUNDRIES, "true").equalsIgnoreCase("true");
			}
			if (props.containsKey(TAG_WEIGHT_DATA)) {
				weightData = Double.parseDouble(props.getProperty(TAG_WEIGHT_DATA));
			}
			if (props.containsKey(TAG_WEIGHT_SMOOTH)) {
				weightSmooth = Double.parseDouble(props.getProperty(TAG_WEIGHT_SMOOTH));
			}
			if (props.containsKey(TAG_CELL_DIVISIONS)) {
				cellDivisions = Integer.parseInt(props.getProperty(TAG_CELL_DIVISIONS));
			}
			if (props.containsKey(TAG_SMOOTHING_TIMEOUT)) {
				smoothingTimeout = Integer.parseInt(props.getProperty(TAG_SMOOTHING_TIMEOUT));
			}
		} else {
			System.out.println("Loading Null properties in Drawing Panel");
		}
		return true;
	}

	@Override
	public void saveProperties() {
		if (props != null) {
			props.setProperty(TAG_SHOW_ACTUAL_PATH, String.valueOf(showActualPath));
			props.setProperty(TAG_SHOW_SMOOTH_PATH, String.valueOf(showSmoothPath));
			props.setProperty(TAG_SHOW_GRID, String.valueOf(showGrid));
			props.setProperty(TAG_SMOOTH_BOUNDRIES, String.valueOf(smoothBoundryPoints));
			props.setProperty(TAG_WEIGHT_DATA, String.valueOf(weightData));
			props.setProperty(TAG_WEIGHT_SMOOTH, String.valueOf(weightSmooth));
			props.setProperty(TAG_CELL_DIVISIONS, String.valueOf(cellDivisions));
			props.setProperty(TAG_SMOOTHING_TIMEOUT, String.valueOf(smoothingTimeout));
		} else {
			System.out.println("Saving Null properties in Drawing Panel");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (obj.equals(cbShowGrid)) {
			showGrid = cbShowGrid.isSelected();
			drawingPanel.setShowGrid(showGrid);
		} else if (obj.equals(cbShowActualPath)) {
			showActualPath = cbShowActualPath.isSelected();
			drawingPanel.setShowActualPath(showActualPath);
		} else if (obj.equals(cbShowSmoothPath)) {
			showSmoothPath = cbShowSmoothPath.isSelected();
			drawingPanel.setShowSmoothPath(showSmoothPath);
		} else if (obj.equals(btnApply)) {
			cellDivisions = Integer.parseInt(spCellDivisions.getValue().toString());
			smoothingTimeout = Integer.parseInt(spSmoothingTimeout.getValue().toString());
			weightData = Double.parseDouble(spWeightData.getValue().toString());
			weightSmooth = Double.parseDouble(spWeightSmooth.getValue().toString());
			this.drawingPanel.setWeightData(weightData);
			this.drawingPanel.setWeightSmooth(weightSmooth);
			this.drawingPanel.setCellDivisions(cellDivisions);
			this.drawingPanel.setSmotthingTimeout(smoothingTimeout);
			drawingPanel.smooth();
		} else if (obj.equals(cbSmoothBoundryPoints)) {
			smoothBoundryPoints = cbSmoothBoundryPoints.isSelected();
			this.drawingPanel.setSmoothBoundryPoints(smoothBoundryPoints);
		}
		this.drawingPanel.repaint();
	}

}
