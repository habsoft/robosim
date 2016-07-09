package pk.com.habsoft.robosim.filters.core.ui;

import java.util.List;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.KeyActionBinding;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.actions.Action;
import pk.com.habsoft.robosim.filters.sensors.RobotDirection;

public class HistogramMain {

	public static void main(String[] args) {
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

		GridWorldExplorer exp = new GridWorldExplorer(d, v, s);

		// TODO
		List<KeyActionBinding> act = domain.getKeyActionsBindings();
		for (KeyActionBinding action : act) {
			exp.addKeyAction(action.getKey(), action.getActionName());
		}
		// set control keys to use w-s-a-d

		exp.addKeyAction("l", GridWorldDomain.ACTION_SENSE);

		exp.initGUI();
	}
}
