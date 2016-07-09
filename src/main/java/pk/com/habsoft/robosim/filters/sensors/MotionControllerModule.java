package pk.com.habsoft.robosim.filters.sensors;

import static pk.com.habsoft.robosim.filters.core.GridWorldDomain.ATTX;
import static pk.com.habsoft.robosim.filters.core.GridWorldDomain.ATTY;
import static pk.com.habsoft.robosim.filters.core.GridWorldDomain.ATT_THETA;

import java.util.ArrayList;
import java.util.List;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.GridLocation;
import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.ObjectClass;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.actions.Action;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;

/**
 * The Class MotionControllerModule.
 */
public class MotionControllerModule implements ObjectInstance {

	/** The object class. */
	ObjectClass objectClass;

	/** The object name. */
	String objectName;

	/** The domain. */
	Domain domain;

	/** The map. */
	int[][] map;

	double probSuccess = 1;

	double probAlt;

	/** The controllers. */
	List<RobotControl> controllers = new ArrayList<>();

	/**
	 * Instantiates a new motion controller module.
	 *
	 * @param objectClass
	 *            the object class
	 * @param objectName
	 *            the object name
	 * @param domain
	 *            the domain
	 * @param map
	 *            the map
	 */
	public MotionControllerModule(ObjectClass objectClass, String objectName, Domain domain, int[][] map) {
		this.objectClass = objectClass;
		this.objectName = objectName;
		this.domain = domain;
		this.map = map;

		initDefaultSensors(domain, map);
	}

	/**
	 * Instantiates a new motion controller module.
	 *
	 * @param other
	 *            the other
	 */
	public MotionControllerModule(MotionControllerModule other) {
		this.objectClass = other.objectClass;
		this.objectName = other.objectName;

		this.controllers = new ArrayList<>(other.getControllers());
		this.map = other.map;
		this.domain = other.domain;
		this.probSuccess = other.probSuccess;
		this.probAlt = other.probAlt;

	}

	/**
	 * Inits the default sensors.
	 *
	 * @param domain
	 *            the domain
	 * @param map
	 *            the map
	 */
	protected void initDefaultSensors(Domain domain, int[][] map) {

		controllers.add(RobotControl.MOVE_FORWARD);
		controllers.add(RobotControl.MOVE_BACKWARD);
		controllers.add(RobotControl.TURN_RIGHT);
		controllers.add(RobotControl.TURN_LEFT);

		// Register Actions
		for (RobotControl action : controllers) {
			new MovementAction(action, domain, map);
		}

		setSuccessProbability(1);

	}

	public void setSuccessProbability(double probSuccess) {

		this.probSuccess = probSuccess;
		this.probAlt = 1. - probSuccess;
	}

	/**
	 * Attempts to move the agent into the given position, taking into account
	 * walls and blocks.
	 *
	 * @param s
	 *            the current state
	 * @param dir
	 *            the dir
	 * @return the state
	 */
	protected State move(State s, RobotControl action) {
		int dist = action.getDistance();
		int theta = action.getAngle();

		ObjectInstance agent = s.getObjectsOfClass(GridWorldDomain.CLASS_ROBOT).get(0);
		int ax = agent.getIntValForAttribute(GridWorldDomain.ATTX);
		int ay = agent.getIntValForAttribute(GridWorldDomain.ATTY);
		int ad = agent.getIntValForAttribute(GridWorldDomain.ATT_THETA);
		// TODO theta

		// Trim values if wold is cyclic
		int nd = GridWorldDomain.INSTANCE.trimValue(GridWorldDomain.ATT_THETA, ad + theta, true);
		int[] dcomp = GridLocation.getGridLocation(dist, nd);
		int nx = GridWorldDomain.INSTANCE.trimValue(GridWorldDomain.ATTX, ax + dcomp[0], false);
		int ny = GridWorldDomain.INSTANCE.trimValue(GridWorldDomain.ATTY, ay + dcomp[1], false);

		// System.out.println(String.format("Old:%d >> New:%d", ad, nd));

		// hit wall, so do not change position
		if (!GridWorldDomain.INSTANCE.isOpen(nx, ny)) {
			nx = ax;
			ny = ay;
		}

		s = s.setObjectsValue(agent.getName(), GridWorldDomain.ATTX, nx);
		s = s.setObjectsValue(agent.getName(), GridWorldDomain.ATTY, ny);
		s = s.setObjectsValue(agent.getName(), GridWorldDomain.ATT_THETA, nd);

		// Update robot belief
		updateBeliefMap(s, dist, theta);

		return s;
	}

	/**
	 * Update belief map.
	 *
	 * @param s
	 *            the state
	 * @param targetDir
	 *            the dir
	 */
	private void updateBeliefMap(State s, int dist, int theta) {
		int width = map.length;
		int height = map[0].length;
		int dirs = RobotDirection.values().length;
		double pSuccess = probSuccess;
		double pFail = probAlt;
		if (dist == 0) {
			pSuccess = 1;
			pFail = 0;
		}

		GridRobotBelief belief = (GridRobotBelief) s.getObjectsOfClass(GridWorldDomain.CLASS_BELIEF).get(0);
		// New belief
		double[][][] ob = belief.getBeliefMap();
		double[][][] nb = new double[width][height][dirs];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Cell is not occupied.
				if (!GridWorldDomain.INSTANCE.isOpen(i, j)) {
					continue;
				}

				for (RobotDirection dir : RobotDirection.values()) {
					int d = dir.getAngle() / 90;

					int pose = trimAttrib(ATT_THETA, dir.getAngle() + theta, true);
					int[] pdcomp = GridLocation.getGridLocation(dist, pose);
					int xx = trimAttrib(ATTX, i + pdcomp[0], false);
					int yy = trimAttrib(ATTY, j + pdcomp[1], false);
					int dd = pose / 90;

					if (!GridWorldDomain.INSTANCE.isOpen(xx, yy)) {
						xx = i;
						yy = j;
					}

					double currentProbability = ob[i][j][d];
					// Probability of staying in the same point
					nb[i][j][d] += pFail * currentProbability;
					// Probability of moving to a new point
					nb[xx][yy][dd] += pSuccess * currentProbability;

				}
			}
		}

		double tp = 0;
		int count = 0;
		for (int r = 0; r < width; r++) {
			for (int c = 0; c < height; c++) {
				for (int d = 0; d < dirs; d++) {
					tp += nb[r][c][d];
					count++;
				}
			}
		}
		System.out.println("Total cell count : " + count);
		System.out.println("Mov : Total Prob  : " + tp);
		belief.setBeliefMap(nb);
	}

	private int trimAttrib(String attrib, int val, boolean forceTrim) {
		return GridWorldDomain.INSTANCE.trimValue(attrib, val, forceTrim);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pk.com.habsoft.robosim.filters.core.ObjectInstance#getName()
	 */
	@Override
	public String getName() {
		return objectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pk.com.habsoft.robosim.filters.core.ObjectInstance#getClassName()
	 */
	@Override
	public String getClassName() {
		return objectClass.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pk.com.habsoft.robosim.filters.core.ObjectInstance#getObjectDescription()
	 */
	@Override
	public String getObjectDescription() {
		return this.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pk.com.habsoft.robosim.filters.core.ObjectInstance#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String newName) {
		this.objectName = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pk.com.habsoft.robosim.filters.core.ObjectInstance#copy()
	 */
	@Override
	public ObjectInstance copy() {
		return new MotionControllerModule(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pk.com.habsoft.robosim.filters.core.ObjectInstance#getIntValForAttribute
	 * (java.lang.String)
	 */
	@Override
	public int getIntValForAttribute(String attx) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pk.com.habsoft.robosim.filters.core.ObjectInstance#setValue(java.lang
	 * .String, int)
	 */
	@Override
	public ObjectInstance setValue(String attName, int v) {
		return null;
	}

	public List<RobotControl> getControllers() {
		return controllers;
	}

	public void setControllers(List<RobotControl> controllers) {
		this.controllers = controllers;
	}

	/**
	 * The Class MovementAction.
	 */
	public class MovementAction extends Action {

		/** The action. */
		RobotControl action;

		/** The map. */
		int[][] map;

		/**
		 * Instantiates a new movement action.
		 *
		 * @param action
		 *            the action
		 * @param domain
		 *            the domain
		 * @param map
		 *            the map
		 */
		public MovementAction(RobotControl action, Domain domain, int[][] map) {
			super(action.getActionName(), domain);
			this.action = action;
			this.map = map;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * pk.com.habsoft.robosim.filters.core.actions.Action#performActionHelper
		 * (pk.com.habsoft.robosim.filters.core.State)
		 */
		@Override
		protected State performActionHelper(State s) {
			State n = move(s, action);
			return n;
		}
	}

}
