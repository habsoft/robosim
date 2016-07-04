package pk.com.habsoft.robosim.filters.core;

import pk.com.habsoft.robosim.filters.core.actions.Action;
import pk.com.habsoft.robosim.filters.core.actions.SADomain;
import pk.com.habsoft.robosim.filters.core.objects.GridRobot;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;
import pk.com.habsoft.robosim.filters.sensors.MotionControllerModule;
import pk.com.habsoft.robosim.filters.sensors.RobotDirection;
import pk.com.habsoft.robosim.filters.sensors.SonarRangeModule;
import pk.com.habsoft.robosim.utils.RoboMathUtils;

/**
 * The Class GridWorldDomain.
 */
public class GridWorldDomain {

	public static GridWorldDomain INSTANCE;

	/** Constant for the name of the x attribute. */
	public static final String ATTX = "x";

	/** Constant for the name of the y attribute. */
	public static final String ATTY = "y";

	/** Constant for the name of the orientation attribute. */
	public static final String ATT_THETA = "theta";

	/** Constant for the name of the sensor action. */
	public static final String ACTION_SENSE = "sense";

	/** The Constant CLASS_ROBOT. */
	public static final String CLASS_ROBOT = "robot";

	/** The Constant CLASS_BELIEF. */
	public static final String CLASS_BELIEF = "belief";

	/** The Constant CLASS_RANGE_SENSORS. */
	public static final String CLASS_RANGE_SENSORS = "range_sensors";

	/** The Constant CLASS_MOTION_SENSORS. */
	public static final String CLASS_MOTION_SENSORS = "motion_sensors";

	/** The Constant OPEN. Robot can navigate in open cells. */
	public static final int OPEN = 0;

	/** The Constant BLOCK. Occupied space in world */
	public static final int BLOCK = 1;

	private static Domain domain = null;

	/** The width. */
	private int width;

	/** The height. */
	private int height;

	/** The map. */
	private int[][] map;

	/** The cyclic world. */
	private boolean isCyclicWorld = false;

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
		INSTANCE = this;
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
	public void initDefaultWorld() {

		this.width = 3;
		this.height = 3;
		makeEmptyMap();

		// this.width = 5;
		// this.height = 5;
		// this.makeEmptyMap();
		// //
		// horizontalWall(0, 2, 4);
//		horizontalWall(1, 1, 2);
		horizontalWall(0, 0, 1);
		// //
		// verticalWall(1, 3, 4);
		// verticalWall(3, 3, 4);

		// Add Actions
	}

	/**
	 * Sets the uniform belief w.r.t open spaces.
	 *
	 * @param s
	 *            the state
	 */
	public void initUniformBelief(State s) {

		GridRobotBelief o = (GridRobotBelief) s.getObjectsOfClass(CLASS_BELIEF).get(0);

		// Count open spaces
		int openCells = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (isOpen(i, j)) {
					openCells++;
				}
			}
		}

		// Initialize uniform belief
		int numOfDirs = RobotDirection.values().length;
		double p = 1. / (openCells * numOfDirs);
		double[][][] belief = new double[width][height][RobotDirection.values().length];

		double tp = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < numOfDirs; k++) {
					belief[i][j][k] = p;
					tp += p;
				}
			}
		}
		System.out.println("Total probability : " + tp);

		// belief[0][0][0] = 1;

		// belief[0][1][0] =0.4;
		// belief[1][0][1] = 0.3;
		// belief[2][1][2] = 0.1;
		// belief[1][2][3] = 0.2;

		o.setBeliefMap(belief);

	}

	public void setCyclicWorld(boolean isCyclicWorld) {
		this.isCyclicWorld = isCyclicWorld;
	}

	/**
	 * Initialize sensors.
	 *
	 * @param s
	 *            the s
	 * @param d
	 *            the d
	 * @param motionNoise
	 *            the motion noise
	 */
	public void initializeSensors(State s, Domain d, double motionNoise, double sensorNoise) {

		SonarRangeModule sensorModule = new SonarRangeModule(d.getObjectClass(CLASS_RANGE_SENSORS), CLASS_RANGE_SENSORS + 0);
		sensorModule.setSuccessProbability(1 - sensorNoise);
		s.addObject(sensorModule);

		MotionControllerModule motionControllerModule = new MotionControllerModule(d.getObjectClass(CLASS_MOTION_SENSORS), CLASS_MOTION_SENSORS + 0, d, map);
		motionControllerModule.setSuccessProbability(1 - motionNoise);
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
	public boolean isOpen(int nx, int ny) {
		// hit wall, or cell is occupied
		Attribute attx = domain.getAttribute(ATTX);
		Attribute atty = domain.getAttribute(ATTY);
		if (!isCyclicWorld) {
			if (nx < attx.lowerLim || nx >= attx.upperLim || ny < atty.lowerLim || ny >= atty.upperLim) {
				return false;
			}
		} else {
			// Trim (x,y) if world is cyclic
			nx = RoboMathUtils.modulus(nx, (int) attx.upperLim, false);
			ny = RoboMathUtils.modulus(ny, (int) atty.upperLim, false);
		}

		return map[nx][ny] == OPEN;
	}

	public int trimValue(String attrib, int val, boolean forceTrim) {
		int newVal = val;
		Attribute att = domain.getAttribute(attrib);
		if (isCyclicWorld || forceTrim) {
			// TODO while loop in modulus is missing.
			newVal = RoboMathUtils.modulus(val, (int) att.upperLim, false);
		}
		return newVal;
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
	 * @param theta
	 *            the theta
	 */
	public void setAgent(State s, int x, int y, int theta) {
		GridRobot o = (GridRobot) s.getObjectsOfClass(CLASS_ROBOT).get(0);

		o.setValue(ATTX, x);
		o.setValue(ATTY, y);
		o.setValue(ATT_THETA, theta);
	}

	/**
	 * Generate domain.
	 *
	 * @return the domain
	 */
	public Domain generateDomain() {

		domain = new SADomain();

		// Creates a new Attribute object
		Attribute xatt = new Attribute(domain, ATTX, Attribute.AttributeType.INT);
		xatt.setLims(0, this.width);

		Attribute yatt = new Attribute(domain, ATTY, Attribute.AttributeType.INT);
		yatt.setLims(0, this.height);

		Attribute theta = new Attribute(domain, ATT_THETA, Attribute.AttributeType.INT);
		theta.setLims(0, 360);

		ObjectClass agentClass = new ObjectClass(domain, CLASS_ROBOT);
		agentClass.addAttribute(xatt);
		agentClass.addAttribute(yatt);
		agentClass.addAttribute(theta);

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
