package pk.com.habsoft.robosim.filters.core.ui;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.sensors.RobotDirection;

public class HistogramMain {

	public static void main(String[] args) {
		GridWorldDomain domain = new GridWorldDomain(11, 11);
		domain.initDefaultWorl();

		Domain d = domain.generateDomain();

		// setup initial state
		State s = domain.getOneRobotBeliefState(d);
		domain.setAgent(s, 1, 1);
		domain.setUniformBelief(s);
		domain.initializeSensors(s, d, 0.4);

		Visualizer v = GridWorldVisualizer.getVisualizer(domain.getMap());
		v.updateState(s);

		GridWorldExplorer exp = new GridWorldExplorer(d, v, s);

		// TODO
		// d.getActionKeyBindings();
		// set control keys to use w-s-a-d
		exp.addKeyAction(RobotDirection.NORTH.getShortKey(), RobotDirection.NORTH.getActionName());
		exp.addKeyAction(RobotDirection.SOUTH.getShortKey(), RobotDirection.SOUTH.getActionName());
		exp.addKeyAction(RobotDirection.EAST.getShortKey(), RobotDirection.EAST.getActionName());
		exp.addKeyAction(RobotDirection.WEST.getShortKey(), RobotDirection.WEST.getActionName());

		exp.addKeyAction("l", GridWorldDomain.ACTION_SENSE);

		exp.initGUI();
	}
}
