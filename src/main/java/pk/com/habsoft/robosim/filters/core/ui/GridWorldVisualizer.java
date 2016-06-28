package pk.com.habsoft.robosim.filters.core.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.List;

import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;
import pk.com.habsoft.robosim.filters.sensors.RangeSensor;
import pk.com.habsoft.robosim.filters.sensors.RobotDirection;
import pk.com.habsoft.robosim.filters.sensors.SonarRangeModule;

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

		r.addObjectClassPainter(GridWorldDomain.CLASS_RANGE_SENSORS, new RangeSensorPainter(1, Color.BLUE, map));

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
			g2.setStroke(new BasicStroke(1));

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
			int theta = ob.getIntValForAttribute(GridWorldDomain.ATT_THETA);

			if (this.shape == 0) {
				g2.fill(new Rectangle2D.Float(rx, ry, width, height));
			} else {
				g2.drawOval((int) rx, (int) ry, (int) width, (int) height);
				// g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				// draw orientation line
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.GREEN);
				int r1 = (int) width / 2;
				int r2 = (int) height / 2;
				int cx = (int) rx + r1;
				int cy = (int) ry + r2;
				int or = theta;
				// System.out.println(String.format("cx:%d, cy:%d, r1:%d, r2:%d",
				// cx, cy, r1, r2));
				// System.out.println(String.format("Theta : %d, Or : %d",
				// theta, or));

				g2.drawLine(cx, cy, (int) (cx + r1 * Math.cos(Math.toRadians(or))), (int) (cy - r2 * Math.sin(Math.toRadians(or))));
			}

		}

		private int getOrientation(int theta) {
			switch (theta) {
			case 0:
				return 0;
			case 1:
				return 90;
			case 2:
				return 180;
			case 3:
				return 270;

			default:
				break;
			}
			return 0;
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
			double[][][] beliefMap = ob.getBeliefMap();

			// set the color of the object
			g2.setColor(this.col);

			float domainXScale = this.dwidth;
			float domainYScale = this.dheight;

			// determine then normalized width
			float width = (1.0f / domainXScale) * cWidth;
			float height = (1.0f / domainYScale) * cHeight;

			final DecimalFormat df = new DecimalFormat("##0.####");

			int xoffset = (int) width / 3;
			int yoffset = (int) height / 3;
			// pass through each cell of the map and if it is a wall, draw it
			Color p = new Color(200, 0, 0);

			for (int i = 0; i < this.dwidth; i++) {
				for (int j = 0; j < this.dheight; j++) {

					if (this.map[i][j] == GridWorldDomain.OPEN) {
						for (RobotDirection dir : RobotDirection.values()) {
							int pose = dir.getIndex();
							double probability = beliefMap[i][j][pose];

							float rx = i * width;
							float ry = cHeight - j * height;
							if (dir.equals(RobotDirection.NORTH)) {
								rx += xoffset;
								ry = ry - height + yoffset;
							} else if (dir.equals(RobotDirection.SOUTH)) {
								rx += xoffset;
								// ry = ry + yoffset;
							} else if (dir.equals(RobotDirection.EAST)) {
								rx += xoffset + (xoffset);
								ry = ry - yoffset;
							} else if (dir.equals(RobotDirection.WEST)) {
								// rx += xoffset;
								ry = ry - yoffset;
							}

							// Probability
							g2.setFont(new Font("Arial", Font.BOLD, (int) Math.min(width, height) / 8));
							g2.setColor(Color.BLACK);
							// g2.drawString(i + " : " + j, rx, ry + 30);
							g2.drawString(df.format(probability), rx, ry);

							// Cell Background
							g2.setPaint(new Color(p.getRed(), p.getGreen(), p.getBlue(), 55 + (int) (150 * probability)));
							// g2.setPaint(Color.CYAN);
							rx = i * width;
							ry = cHeight - height - j * height;
							// g2.fill(new Rectangle2D.Float(rx, ry, width,
							// height));
						}

					}

				}
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
	public static class RangeSensorPainter implements ObjectPainter {

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
		public RangeSensorPainter(Color col, int[][] map) {
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
		public RangeSensorPainter(int shape, Color col, int[][] map) {
			this.col = col;
			this.dwidth = map.length;
			this.dheight = map[0].length;
			this.map = map;
			this.shape = shape;
		}

		@Override
		public void paintObject(Graphics2D g2, State s, ObjectInstance oi, float cWidth, float cHeight) {
			if (!(oi instanceof SonarRangeModule))
				return;

			// set the color of the object
			g2.setColor(this.col);
			g2.setStroke(new BasicStroke(5));

			float domainXScale = this.dwidth;
			float domainYScale = this.dheight;

			// determine then normalized width
			float width = (1.0f / domainXScale) * cWidth;
			float height = (1.0f / domainYScale) * cHeight;

			// ///////////////////////////////////////////////

			ObjectInstance robot = s.getFirstObjectOfClass(GridWorldDomain.CLASS_ROBOT);
			int rx = robot.getIntValForAttribute(GridWorldDomain.ATTX);
			int ry = robot.getIntValForAttribute(GridWorldDomain.ATTY);
			// Calculate Robot Center points
			float cx = ((rx * width) + (width / 2));
			float cy = (cHeight - (ry * height) - (height / 2));

			SonarRangeModule ob = (SonarRangeModule) oi;
			List<RangeSensor> sensors = ob.getSensors();

			for (RangeSensor sonar : sensors) {
				// Draw range sensor range
				int[] dir = sonar.getDir();
				int range = sonar.getMeasurement();

				float sx = cx + (dir[0] * width * range) - (dir[0] * height / 2);
				float sy = cy - ((dir[1] * height * range) + (dir[1] * height / 2));

				// g2.drawLine((int) cx, (int) cy, (int) sx, (int) sy);

			}
		}
	}

}
