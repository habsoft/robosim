package pk.com.habsoft.robosim.filters.core.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.KeyActionBinding;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.internal.RootView;

public class HistogramMain extends RootView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static GridWorldExplorer exp;

	public HistogramMain() {
		super("Histogram Filter(Sonar Range Finder)", "");
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		HistogramMain view1 = new HistogramMain();
		view1.initGUI();

		view1.setVisible(true);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();

		frame.setSize(width, height);
		frame.setVisible(true);

	}

	@Override
	public void initGUI() {
		GridWorldDomain domain = new GridWorldDomain(11, 11);
		domain.initDefaultWorld();

		Domain d = domain.generateDomain();

		// setup initial state
		State s = domain.getOneRobotBeliefState(d);
		domain.setAgent(s, 0, 0, 0);
		domain.setCyclicWorld(false);
		domain.initUniformBelief(s);
		domain.initializeSensors(s, d, 0.3, 0.2);

		Visualizer v = GridWorldVisualizer.getVisualizer(domain.getMap());
		v.updateState(s);

		exp = new GridWorldExplorer(this, d, v, s);

		// TODO
		List<KeyActionBinding> act = domain.getKeyActionsBindings();
		for (KeyActionBinding action : act) {
			exp.addKeyAction(action.getKey(), action.getActionName());
		}
		// set control keys to use w-s-a-d

		exp.addKeyAction("l", GridWorldDomain.ACTION_SENSE);

		exp.initGUI();

		super.add(this);

		setSize(500, 500);
		setVisible(true);
	}
}
