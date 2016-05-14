package pk.com.habsoft.robosim.filters.histogram;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import pk.com.habsoft.robosim.internal.RPanel;

public class RobotBeliefMap extends RPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default cellSize. It will be computed with width,height
	 */
	private int cellWidth = 50;// pixel
	private int cellHeight = 50;// pixel
	private static int spacing = 1; // pixel
	int rows;
	int cols;

	private Image image;
	HistogramFilterAdvView view;

	public RobotBeliefMap(HistogramFilterAdvView view, double width, double height, String label, int rows, int cols) {
		super(width, height, label);
		this.view = view;
		this.rows = rows;
		this.cols = cols;
		this.cellWidth = (int) width / cols;
		this.cellHeight = (int) height / rows;

		image = new BufferedImage((int) width, (int) height, BufferedImage.BITMASK);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graphics = (Graphics2D) image.getGraphics();

		graphics.setBackground(Color.red);
		graphics.setPaint(Color.RED);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				graphics.setPaint(Color.WHITE);
				graphics.fillRect(j * cellHeight, i * cellWidth, cellHeight, cellWidth);

				double intensity = Double.parseDouble(view.filter.getProbabilityAt(i, j));
				// Paint p = new Color(0, 0, 0, (int) (255 * intensity));
				// graphics.setPaint(p);
				Color p = view.SENSORS[view.world[i][j]];
				graphics.setPaint(new Color(p.getRed(), p.getGreen(), p.getBlue(), 55 + (int) (200 * intensity)));

				graphics.fillRect(j * cellWidth + spacing, i * cellWidth + spacing, cellWidth - spacing, cellWidth - spacing);

				graphics.setColor(Color.BLACK);
				graphics.setFont(new Font("Arial", Font.BOLD, Math.min(cellWidth, cellWidth) / 4));
				String str = String.valueOf(view.filter.getProbabilityAt(i, j));

				FontMetrics matrix = graphics.getFontMetrics();
				int ht = matrix.getAscent();
				int wd = matrix.stringWidth(str);

				// Draw Image in center of the cell
				graphics.drawString(str, (j * (cellWidth) + cellWidth / 2 - wd / 2), (i * (cellWidth) + cellWidth / 2 + ht / 2));

			}
		}

		g.drawImage(image, 0, LABEL_HEIGHT, this);
	}

}
