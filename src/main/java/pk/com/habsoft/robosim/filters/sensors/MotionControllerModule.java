package pk.com.habsoft.robosim.filters.sensors;

import static pk.com.habsoft.robosim.utils.Util.debug;

import java.util.ArrayList;
import java.util.List;

import pk.com.habsoft.robosim.filters.core.Domain;
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
	List<RobotDirection> controllers = new ArrayList<>();

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

		controllers.add(RobotDirection.NORTH);
		controllers.add(RobotDirection.SOUTH);
		controllers.add(RobotDirection.EAST);
		controllers.add(RobotDirection.WEST);

		// Register Actions
		for (RobotDirection action : controllers) {
			new MovementAction(action, domain, map);
		}

		setProbSucceedTransitionDynamics(1);

	}

	public void setProbSucceedTransitionDynamics(double probSuccess) {
		// Total number of actions.
		int na = controllers.size();

		this.probSuccess = probSuccess;
		this.probAlt = (1. - probSuccess) / na;
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
	protected State move(State s, RobotDirection dir) {

		ObjectInstance agent = s.getObjectsOfClass(GridWorldDomain.CLASS_ROBOT).get(0);
		int ax = agent.getIntValForAttribute(GridWorldDomain.ATTX);
		int ay = agent.getIntValForAttribute(GridWorldDomain.ATTY);

		int nx = ax + dir.getDcomp()[0];
		int ny = ay + dir.getDcomp()[1];

		// hit wall, so do not change position
		if (!GridWorldDomain.isOpen(nx, ny, map)) {
			nx = ax;
			ny = ay;
		}

		s = s.setObjectsValue(agent.getName(), GridWorldDomain.ATTX, nx);
		s = s.setObjectsValue(agent.getName(), GridWorldDomain.ATTY, ny);

		// Update robot belief
		updateBeliefMap(s, dir);

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
	private void updateBeliefMap(State s, RobotDirection targetDir) {
		int width = map.length;
		int height = map[0].length;

		GridRobotBelief belief = (GridRobotBelief) s.getObjectsOfClass(GridWorldDomain.CLASS_BELIEF).get(0);
		// New belief
		double[][] nb = new double[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Cell is not occupied.
				if (!GridWorldDomain.isOpen(i, j, map)) {
					continue;
				}

				// Calculate Target location (x,y)
				int tx = i + targetDir.getDcomp()[0];
				int ty = j + targetDir.getDcomp()[1];
				// hit wall, so do not change position
				if (!GridWorldDomain.isOpen(tx, ty, map)) {
					tx = i;
					ty = j;
				}

				// Success probability
				double prior = belief.getBeliefMap()[i][j];
				nb[tx][ty] += probSuccess * prior;

				debug(String.format("Move %d:%d >> %d:%d", i, j, tx, ty));

				// Probability calculations due to noise in motion. Because
				// robot can move in any direction around its target location
				// (tx,ty) with probAlt
				for (RobotDirection dir : controllers) {

					// nx,ny are locations around target location.
					int nx = tx + dir.getDcomp()[0];
					int ny = ty + dir.getDcomp()[1];
					// hit wall, so do not change position
					if (!GridWorldDomain.isOpen(nx, ny, map)) {
						nx = tx;
						ny = ty;
					}

					// Prior probability
					prior = belief.getBeliefMap()[i][j];
					// Probability of moving to a new point direction
					debug(String.format("Move %s : %d >>  %f", dir.getActionName(), dir.getIndex(), probAlt));
					nb[nx][ny] += probAlt * prior;
				}

			}
		}
		belief.setBeliefMap(nb);
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

	public List<RobotDirection> getControllers() {
		return controllers;
	}

	public void setControllers(List<RobotDirection> controllers) {
		this.controllers = controllers;
	}

	/**
	 * The Class MovementAction.
	 */
	public class MovementAction extends Action {

		/** The action. */
		RobotDirection action;

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
		public MovementAction(RobotDirection action, Domain domain, int[][] map) {
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
