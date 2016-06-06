package pk.com.habsoft.robosim.filters.core;

import java.util.Random;

import pk.com.habsoft.robosim.filters.core.actions.Action;
import pk.com.habsoft.robosim.filters.core.actions.SADomain;
import pk.com.habsoft.robosim.filters.core.objects.GridRobot;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;

public class GridWorldDomain {

    /**
     * Constant for the name of the x attribute
     */
    public static final String ATTX = "x";

    /**
     * Constant for the name of the y attribute
     */
    public static final String ATTY = "y";

    /**
     * Constant for the name of the north action
     */
    public static final String ACTIONNORTH = "north";

    /**
     * Constant for the name of the south action
     */
    public static final String ACTIONSOUTH = "south";

    /**
     * Constant for the name of the east action
     */
    public static final String ACTIONEAST = "east";

    /**
     * Constant for the name of the west action
     */
    public static final String ACTIONWEST = "west";

    public static final String CLASS_ROBOT = "robot";
    public static final String CLASS_BELIEF = "belief";
    private int width;
    private int height;

    private int[][] map;

    protected double[][] transitionDynamics;

    public GridWorldDomain(int width, int height) {
        this.width = width;
        this.height = height;
        this.makeEmptyMap();
    }

    public GridWorldDomain(int[][] map) {
        this.map = map;
        this.width = map.length;
        this.height = map[0].length;
    }

    public void makeEmptyMap() {
        this.map = new int[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.map[i][j] = 0;
            }
        }
    }

    /**
     * Sets the domain to use probabilistic transitions. Agent will move in the
     * intended direction with probability probSucceed. Agent will move in a
     * random direction with probability 1 - probSucceed
     * 
     * @param probSucceed
     *            probability to move the in intended direction
     */
    public void setProbSucceedTransitionDynamics(double probSucceed) {
        int na = 4;
        double pAlt = (1. - probSucceed) / 3.;
        transitionDynamics = new double[na][na];
        for (int i = 0; i < na; i++) {
            for (int j = 0; j < na; j++) {
                if (i != j) {
                    transitionDynamics[i][j] = pAlt;
                } else {
                    transitionDynamics[i][j] = probSucceed;
                }
            }
        }
    }

    public void setMap(int[][] map) {
        this.width = map.length;
        this.height = map[0].length;
        this.map = map.clone();
    }

    /**
     * Returns a deep copy of the map being used for the domain
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
     * Returns this grid world's width
     * 
     * @return this grid world's width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns this grid world's height
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
        this.width = 8;
        this.height = 5;
        this.makeEmptyMap();

        horizontalWall(0, 0, 3);

        verticalWall(0, 0, 4);
        verticalWall(3, 3, 4);

        // Add Actions
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
            this.map[x][y] = 1;
        }
    }

    /**
     * Creates a sequence of complete cell walls spanning the specified start
     * and end y coordinates
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
            this.map[x][y] = 1;
        }
    }

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

    public void setUniformBelief(State s) {

        GridRobotBelief o = (GridRobotBelief) s.getObjectsOfClass(CLASS_BELIEF).get(0);

        double p = 1. / (width * height);
        double[][] belief = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                belief[i][j] = p;
            }
        }

        o.setBeliefMap(belief);

    }

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

        int[][] cmap = this.getMap();

        new MovementAction(ACTIONNORTH, domain, this.transitionDynamics[0], cmap);
        new MovementAction(ACTIONSOUTH, domain, this.transitionDynamics[1], cmap);
        new MovementAction(ACTIONEAST, domain, this.transitionDynamics[2], cmap);
        new MovementAction(ACTIONWEST, domain, this.transitionDynamics[3], cmap);

        return domain;
    }

    /**
     * Attempts to move the agent into the given position, taking into account
     * walls and blocks
     * 
     * @param s
     *            the current state
     * @param xd
     *            the attempted new X position of the agent
     * @param yd
     *            the attempted new Y position of the agent
     */
    protected State move(State s, int xd, int yd, int[][] map, double[] directionProbs) {

        ObjectInstance agent = s.getObjectsOfClass(CLASS_ROBOT).get(0);
        int ax = agent.getIntValForAttribute(ATTX);
        int ay = agent.getIntValForAttribute(ATTY);

        int nx = ax + xd;
        int ny = ay + yd;

        // hit wall, so do not change position
        if (nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length || map[nx][ny] == 1) {
            nx = ax;
            ny = ay;
        }

        s = s.setObjectsValue(agent.getName(), ATTX, nx);
        s = s.setObjectsValue(agent.getName(), ATTY, ny);
        updateBeliefMap(s, directionProbs);

        return s;
    }

    private void updateBeliefMap(State s, double[] directionProbs) {
        // TODO dynamic
        GridRobotBelief belief = (GridRobotBelief) s.getObjectsOfClass(CLASS_BELIEF).get(0);
        double[][] nb = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                for (int dir = 0; dir < directionProbs.length; dir++) {
                    int[] decomp = this.movementDirectionFromIndex(dir);
                    int nx = i + decomp[0];
                    int ny = j + decomp[1];

                    // hit wall, so do not change position
                    if (nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length || map[nx][ny] == 1) {
                        nx = i;
                        ny = j;
                    }

                    double currentProbability = belief.getBeliefMap()[i][j];
                    // Probability of moving to a new point direction
                    nb[nx][ny] += directionProbs[dir] * currentProbability;
                }

            }
        }
        belief.setBeliefMap(nb);
    }

    protected int[] movementDirectionFromIndex(int i) {

        int[] result = null;

        switch (i) {
        case 0:
            result = new int[] { 0, 1 };
            break;

        case 1:
            result = new int[] { 0, -1 };
            break;

        case 2:
            result = new int[] { 1, 0 };
            break;

        case 3:
            result = new int[] { -1, 0 };

        default:
            break;
        }

        return result;
    }

    /**
     * Action class for movement actions in grid world.
     * 
     * @author James MacGlashan
     *
     */
    public class MovementAction extends Action {

        /**
         * Probabilities of the actual direction the agent will go
         */
        protected double[] directionProbs;

        /**
         * Random object for sampling distribution
         */
        protected Random rand;

        /**
         * The map of the world
         */
        protected int[][] map;

        /**
         * Initializes for the given name, domain and actually direction
         * probabilities the agent will go
         * 
         * @param name
         *            name of the action
         * @param domain
         *            the domain of the action
         * @param directions
         *            the probability for each direction (index 0,1,2,3
         *            corresponds to north,south,east,west, respectively).
         * @param map
         *            the map of the world
         */
        public MovementAction(String name, Domain domain, double[] directions, int[][] map) {
            super(name, domain);
            this.directionProbs = directions.clone();
            this.rand = new Random();
            this.map = map;
        }

        @Override
        protected State performActionHelper(State s) {
            double roll = rand.nextDouble();
            double curSum = 0.;
            int dir = 0;
            for (int i = 0; i < directionProbs.length; i++) {
                curSum += directionProbs[i];
                if (roll < curSum) {
                    dir = i;
                    break;
                }
            }

            int[] dcomps = GridWorldDomain.this.movementDirectionFromIndex(dir);
            return GridWorldDomain.this.move(s, dcomps[0], dcomps[1], this.map, directionProbs);
        }

    }
}
