package pk.com.habsoft.robosim.filters.core.ui;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.State;

public class HistogramMain {

    public static void main(String[] args) {
        GridWorldDomain domain = new GridWorldDomain(11, 11);
        domain.initDefaultWorl();
        domain.setProbSucceedTransitionDynamics(0.9);

        Domain d = domain.generateDomain();

        // setup initial state
        State s = domain.getOneRobotBeliefState(d);
        domain.setAgent(s, 0, 0);
        domain.setUniformBelief(s);

        Visualizer v = GridWorldVisualizer.getVisualizer(domain.getMap());
        v.updateState(s);

        GridWorldExplorer exp = new GridWorldExplorer(d, v, s);
        // set control keys to use w-s-a-d
        exp.addKeyAction("w", GridWorldDomain.ACTIONNORTH);
        exp.addKeyAction("s", GridWorldDomain.ACTIONSOUTH);
        exp.addKeyAction("a", GridWorldDomain.ACTIONWEST);
        exp.addKeyAction("d", GridWorldDomain.ACTIONEAST);

        exp.initGUI();
    }
}
