package pk.com.habsoft.robosim.filters.core.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;

/**
 * Returns a visualizer for grid worlds in which walls are rendered as black
 * squares or black lines, the agent is a gray circle and the location objects
 * are colored squares. The size of the cells scales to the size of the domain
 * and the size of the canvas.
 * 
 * @author James MacGlashan
 *
 */
public class GridWorldVisualizer {

    private GridWorldVisualizer() {
        // do nothing
    }

    /**
     * Returns visualizer for a grid world domain with the provided wall map.
     * 
     * @param map
     *            the wall map matrix where 0s indicate it is clear of walls, 1s
     *            indicate a full cell wall in that cell, 2s indicate a 1D north
     *            wall, 3s indicate a 1D east wall, and 4s indicate a 1D north
     *            and east wall.
     * @return a grid world domain visualizer
     */
    public static Visualizer getVisualizer(int[][] map) {

        StateRenderLayer r = getRenderLayer(map);
        Visualizer v = new Visualizer(r);

        return v;
    }

    /**
     * Returns state render layer for a gird world domain with the provided wall
     * map.
     * 
     * @param map
     *            the wall map matrix where 0s indicate it is clear of walls, 1s
     *            indicate a full cell wall in that cell, 2s indicate a 1D north
     *            wall, 3s indicate a 1D east wall, and 4s indicate a 1D north
     *            and east wall.
     * @return a grid world domain state render layer
     */
    public static StateRenderLayer getRenderLayer(int[][] map) {

        StateRenderLayer r = new StateRenderLayer();

        r.addStaticPainter(new MapPainter(map, true));
        // TODO
        r.addObjectClassPainter(GridWorldDomain.CLASS_ROBOT, new CellPainter(1, Color.gray, map));

        r.addObjectClassPainter(GridWorldDomain.CLASS_BELIEF, new BeliefPainter(1, Color.red, map));

        return r;

    }

    /**
     * A static painter class for rendering the walls of the grid world as black
     * squares or black lines for 1D walls.
     * 
     * @author James MacGlashan
     *
     */
    public static class MapPainter implements StaticPainter {

        protected int dwidth;
        protected int dheight;
        protected int[][] map;
        protected boolean drawGrid;

        /**
         * Initializes for the domain and wall map
         * 
         * @param map
         *            the wall map matrix where 1s indicate a wall in that cell
         *            and 0s indicate it is clear of walls
         */
        public MapPainter(int[][] map, boolean drawGrid) {
            this.dwidth = map.length;
            this.dheight = map[0].length;
            this.map = map;
            this.drawGrid = drawGrid;
        }

        @Override
        public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
            // draw the walls; make them black
            g2.setColor(Color.black);

            // set stroke for 1d walls
            // g2.setStroke(new BasicStroke(4));

            float domainXScale = this.dwidth;
            float domainYScale = this.dheight;

            // determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            // pass through each cell of the map and if it is a wall, draw it
            for (int i = 0; i < this.dwidth; i++) {
                for (int j = 0; j < this.dheight; j++) {

                    float rx = i * width;
                    float ry = cHeight - height - j * height;
                    if (this.map[i][j] == 1) {

                        // g2.drawString(i + " : " + j, rx, ry);
                        g2.fill(new Rectangle2D.Float(rx, ry, width, height));
                    }
                    if (drawGrid) {
                        // Draw Grid
                        g2.drawLine((int) rx, (int) ry, (int) (rx + width), (int) ry);
                        g2.drawLine((int) (rx + width), (int) ry, (int) (rx + width), (int) (ry + height));
                    }

                }
            }

        }

    }

    /**
     * A painter for a grid world cell which will fill the cell with a given
     * color and where the cell position is indicated by the x and y attribute
     * for the mapped object instance
     * 
     * @author James MacGlashan
     *
     */
    public static class CellPainter implements ObjectPainter {

        protected Color col;
        protected int dwidth;
        protected int dheight;
        protected int[][] map;
        protected int shape = 0; // 0 for rectangle 1 for ellipse

        /**
         * Initializes painter for a rectangle shape cell
         * 
         * @param col
         *            the color to paint the cell
         * @param map
         *            the wall map matrix where 1s indicate a wall in that cell
         *            and 0s indicate it is clear of walls
         */
        public CellPainter(Color col, int[][] map) {
            this.col = col;
            this.dwidth = map.length;
            this.dheight = map[0].length;
            this.map = map;
        }

        /**
         * Initializes painter with filling the cell with the given shape
         * 
         * @param shape
         *            the shape with which to fill the cell. 0 for a rectangle,
         *            1 for an ellipse.
         * @param col
         *            the color to paint the cell
         * @param map
         *            the wall map matrix where 1s indicate a wall in that cell
         *            and 0s indicate it is clear of walls
         */
        public CellPainter(int shape, Color col, int[][] map) {
            this.col = col;
            this.dwidth = map.length;
            this.dheight = map[0].length;
            this.map = map;
            this.shape = shape;
        }

        @Override
        public void paintObject(Graphics2D g2, State s, ObjectInstance ob, float cWidth, float cHeight) {

            // set the color of the object
            g2.setColor(this.col);

            float domainXScale = this.dwidth;
            float domainYScale = this.dheight;

            // determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            float rx = ob.getIntValForAttribute(GridWorldDomain.ATTX) * width;
            float ry = cHeight - height - ob.getIntValForAttribute(GridWorldDomain.ATTY) * height;

            if (this.shape == 0) {
                g2.fill(new Rectangle2D.Float(rx, ry, width, height));
            } else {
                g2.fill(new Ellipse2D.Float(rx, ry, width, height));
            }

        }

    }

    /**
     * A painter for a grid world which will fill the cell with a given color
     * and corresponding belief
     * 
     * @author Faisal Hameed
     *
     */
    public static class BeliefPainter implements ObjectPainter {

        protected Color col;
        protected int dwidth;
        protected int dheight;
        protected int[][] map;
        protected int shape = 0; // 0 for rectangle 1 for ellipse

        /**
         * Initializes painter for a rectangle shape cell
         * 
         * @param col
         *            the color to paint the cell
         * @param map
         *            the wall map matrix where 1s indicate a wall in that cell
         *            and 0s indicate it is clear of walls
         */
        public BeliefPainter(Color col, int[][] map) {
            this.col = col;
            this.dwidth = map.length;
            this.dheight = map[0].length;
            this.map = map;
        }

        /**
         * Initializes painter with filling the cell with the given shape
         * 
         * @param shape
         *            the shape with which to fill the cell. 0 for a rectangle,
         *            1 for an ellipse.
         * @param col
         *            the color to paint the cell
         * @param map
         *            the wall map matrix where 1s indicate a wall in that cell
         *            and 0s indicate it is clear of walls
         */
        public BeliefPainter(int shape, Color col, int[][] map) {
            this.col = col;
            this.dwidth = map.length;
            this.dheight = map[0].length;
            this.map = map;
            this.shape = shape;
        }

        @Override
        public void paintObject(Graphics2D g2, State s, ObjectInstance oi, float cWidth, float cHeight) {

            if (!(oi instanceof GridRobotBelief))
                return;

            GridRobotBelief ob = (GridRobotBelief) oi;
            double[][] beliefMap = ob.getBeliefMap();

            // set the color of the object
            g2.setColor(this.col);

            float domainXScale = this.dwidth;
            float domainYScale = this.dheight;

            // determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            final DecimalFormat df = new DecimalFormat("##0.####");

            int xoffset = (int) width / 3;
            int yoffset = (int) height / 2;
            // pass through each cell of the map and if it is a wall, draw it
            for (int i = 0; i < this.dwidth; i++) {
                for (int j = 0; j < this.dheight; j++) {

                    if (this.map[i][j] == 0) {

                        float rx = xoffset + i * width;
                        float ry = cHeight - yoffset - j * height;

                        g2.drawString(df.format(beliefMap[i][j]), rx, ry);

                    }

                }
            }

        }

    }

}
