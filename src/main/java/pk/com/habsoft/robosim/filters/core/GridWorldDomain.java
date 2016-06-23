package pk.com.habsoft.robosim.filters.core;

import pk.com.habsoft.robosim.filters.core.actions.Action;
import pk.com.habsoft.robosim.filters.core.actions.SADomain;
import pk.com.habsoft.robosim.filters.core.objects.GridRobot;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;
import pk.com.habsoft.robosim.filters.sensors.MotionControllerModule;
import pk.com.habsoft.robosim.filters.sensors.SonarRangeModule;

// TODO: Auto-generated Javadoc
/**
 * The Class GridWorldDomain.
 */
public class GridWorldDomain {

	/** Constant for the name of the x attribute. */
	public static final String ATTX = "x";

	/** Constant for the name of the y attribute. */
	public static final String ATTY = "y";

	/** Constant for the name of the sensor action. */
	public static final String ACTION_SENSE = "sense";

	/** The Constant CLASS_ROBOT. */
	public static final String CLASS_ROBOT = "robot";

	/** The Constant CLASS_BELIEF. */
	public static final String CLASS_BELIEF = "belief";

	public static final String CLASS_RANGE_SENSORS = "range_sensors";

	public static final String CLASS_MOTION_SENSORS = "motion_sensors";

	/** The Constant OPEN. Robot can navigate in open cells. */
	public static final int OPEN = 0;

	/** The Constant BLOCK. Occupied space in world */
	public static final int BLOCK = 1;

	/** The width. */
	private int width;

	/** The height. */
	private int height;

	/** The map. */
	private int[][] map;

	/**
	 * Instantiates a new grid world domain.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public GridWorldDomain(int width, int height) {
		this.width = width;
		this.height = height;
		this.makeEmptyMap();
	}

	/**
	 * Instantiates a new grid world domain.
	 *
	 * @param map
	 *            the map
	 */
	public GridWorldDomain(int[][] map) {
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
	}

	/**
	 * Make empty map.
	 */
	public void makeEmptyMap() {
		this.map = new int[this.width][this.height];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				this.map[i][j] = 0;
			}
		}
	}

	/**
	 * Sets the map.
	 *
	 * @param map
	 *            the new map
	 */
	public void setMap(int[][] map) {
		this.width = map.length;
		this.height = map[0].length;
		this.map = map.clone();
	}

	/**
	 * Returns a deep copy of the map being used for the domain.
	 *
	 * @return a deep copy of the map being used in the domain
	 */
	public int[][] getMap() {
		int[][] cmap = new int[this.map.length][this.map[0].length];
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[0].length; j++) {
				cmap[i][j] = this.map[i][j];
			}
		}
		return cmap;
	}

	/**
	 * Returns this grid world's width.
	 *
	 * @return this grid world's width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns this grid world's height.
	 *
	 * @return this grid world's height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Will set the map of the world to the classic Four Rooms map used the
	 * original options work (Sutton, R.S. and Precup, D. and Singh, S., 1999).
	 */
	public void initDefaultWorl() {
		// this.width = 5;
		// this.height = 5;
		// this.makeEmptyMap();
		// //
		// horizontalWall(0, 2, 4);
		// horizontalWall(0, 0, 1);
		// horizontalWall(2, 2, 2);
		// //
		// verticalWall(1, 3, 4);
		// verticalWall(3, 3, 4);

		this.width = 3;
		this.height = 4;
		makeEmptyMap();

		// Add Actions
	}

	public void initializeSensors(State s, Domain d, double motionNoise) {

		ObjectInstance sensorModule = new SonarRangeModule(d.getObjectClass(CLASS_RANGE_SENSORS), CLASS_RANGE_SENSORS + 0);
		s.addObject(sensorModule);

		MotionControllerModule motionControllerModule = new MotionControllerModule(d.getObjectClass(CLASS_MOTION_SENSORS), CLASS_MOTION_SENSORS + 0, d, map);
		motionControllerModule.setProbSucceedTransitionDynamics(1 - motionNoise);
		s.addObject(motionControllerModule);

	}

	/**
	 * Creates a sequence of complete cell walls spanning the specified start
	 * and end x coordinates.
	 * 
	 * @param xi
	 *            The starting x coordinate of the wall
	 * @param xf
	 *            The ending x coordinate of the wall
	 * @param y
	 *            The y coordinate of the wall
	 */
	public void horizontalWall(int xi, int xf, int y) {
		for (int x = xi; x <= xf; x++) {
			this.map[x][y] = BLOCK;
		}
	}

	/**
	 * Creates a sequence of complete cell walls spanning the specified start
	 * and end y coordinates.
	 *
	 * @param yi
	 *            The stating y coordinate of the wall
	 * @param yf
	 *            The ending y coordinate of the wall
	 * @param x
	 *            The x coordinate of the wall
	 */
	public void verticalWall(int yi, int yf, int x) {
		for (int y = yi; y <= yf; y++) {
			this.map[x][y] = BLOCK;
		}
	}

	/**
	 * Sets the cell status.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param status
	 *            the status
	 */
	public void setCellStatus(int x, int y, int status) {
		this.map[x][y] = status;
	}

	/**
	 * Check whether x,y location of open or occupied or wall.
	 *
	 * @param nx
	 *            the nx
	 * @param ny
	 *            the ny
	 * @param map
	 *            the map
	 * @return true if cell is not occupied
	 */
	public static boolean isOpen(int nx, int ny, int[][] map) {
		// hit wall, or cell is occupied
		if (nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length) {
			return false;
		}
		return map[nx][ny] == OPEN;
	}

	/**
	 * Gets the one robot belief state.
	 *
	 * @param d
	 *            the d
	 * @return the one robot belief state
	 */
	public State getOneRobotBeliefState(Domain d) {

		State s = new MutableState();

		ObjectInstance obj = new GridRobot(d.getObjectClass(CLASS_ROBOT), CLASS_ROBOT + 0);
		s.addObject(obj);

		ObjectInstance belief = new GridRobotBelief(d.getObjectClass(CLASS_BELIEF), CLASS_BELIEF + 0);
		s.addObject(belief);

		return s;

	}

	/**
	 * Sets the first agent object in s to the specified x and y position.
	 * 
	 * @param s
	 *            the state with the agent whose position to set
	 * @param x
	 *            the x position of the agent
	 * @param y
	 *            the y position of the agent
	 */
	public void setAgent(State s, int x, int y) {
		GridRobot o = (GridRobot) s.getObjectsOfClass(CLASS_ROBOT).get(0);

		o.setValue(ATTX, x);
		o.setValue(ATTY, y);
	}

	/**
	 * Sets the uniform belief w.r.t open spaces.
	 *
	 * @param s
	 *            the state
	 */
	public void setUniformBelief(State s) {

		GridRobotBelief o = (GridRobotBelief) s.getObjectsOfClass(CLASS_BELIEF).get(0);

		// Count open spaces
		int openCells = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (isOpen(i, j, map)) {
					openCells++;
				}
			}
		}

		// Initialize uniform belief
		double p = 1. / openCells;
		double[][] belief = new double[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// belief[i][j] = p;
			}
		}
		belief[1][1] = 0.5;
		belief[0][3] = 0.5;

		o.setBeliefMap(belief);

	}

	/**
	 * Generate domain.
	 *
	 * @return the domain
	 */
	public Domain generateDomain() {

		Domain domain = new SADomain();

		// Creates a new Attribute object
		Attribute xatt = new Attribute(domain, ATTX, Attribute.AttributeType.INT);
		xatt.setLims(0, this.width - 1);

		Attribute yatt = new Attribute(domain, ATTY, Attribute.AttributeType.INT);
		yatt.setLims(0., this.height - 1);

		ObjectClass agentClass = new ObjectClass(domain, CLASS_ROBOT);
		agentClass.addAttribute(xatt);
		agentClass.addAttribute(yatt);

		ObjectClass beliefClass = new ObjectClass(domain, CLASS_BELIEF);

		ObjectClass sensorClass = new ObjectClass(domain, CLASS_RANGE_SENSORS);

		ObjectClass controllerClass = new ObjectClass(domain, CLASS_MOTION_SENSORS);

		int[][] cmap = this.getMap();

		// Add sensor action
		new SensorAction(ACTION_SENSE, domain, cmap);

		return domain;
	}

	/**
	 * Action class for sensor measurement in grid world.
	 * 
	 * @author Faisal Hameed
	 *
	 */
	public class SensorAction extends Action {

		/** The map of the world. */
		protected int[][] map;

		/**
		 * Initializes for the given name, domain and actually direction
		 * probabilities the agent will go.
		 *
		 * @param name
		 *            name of the action
		 * @param domain
		 *            the domain of the action
		 * @param map
		 *            the map of the world
		 */
		public SensorAction(String name, Domain domain, int[][] map) {
			super(name, domain);
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
			SonarRangeModule sensorModule = (SonarRangeModule) s.getFirstObjectOfClass(CLASS_RANGE_SENSORS);
			sensorModule.sense(s, map);

			return s;
		}

	}

}
