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
	private static int spacing = 1; // pixel

	private int width;
	private int height;

	private Image image;
	// TODO replace with environment;
	HistogramFilterAdvView view;

	public RobotBeliefMap(HistogramFilterAdvView view, int width, int height, String label) {
		super(width, height, label);
		this.view = view;
		this.width = width;
		this.height = height - LABEL_HEIGHT;

		image = new BufferedImage(width, height, BufferedImage.BITMASK);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graphics = (Graphics2D) image.getGraphics();

		int rows = view.world.length;
		int cols = view.world[0].length;

		// Cell width, height
		int cWidth = width / cols;// pixel
		int cHeight = height / rows;// pixel

		graphics.setBackground(Color.red);
		graphics.setPaint(Color.RED);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				graphics.setPaint(Color.WHITE);
				graphics.fillRect(j * cWidth, i * cHeight, cWidth, cHeight);

				double intensity = Double.parseDouble(view.filter.getProbabilityAt(i, j));
				// Paint p = new Color(0, 0, 0, (int) (255 * intensity));
				// graphics.setPaint(p);
				Color p = view.SENSORS[view.world[i][j]];
				graphics.setPaint(new Color(p.getRed(), p.getGreen(), p.getBlue(), 55 + (int) (200 * intensity)));

				graphics.fillRect(j * cWidth + spacing, i * cHeight + spacing, cWidth - spacing, cHeight - spacing);

				graphics.setColor(Color.BLACK);
				graphics.setFont(new Font("Arial", Font.BOLD, Math.min(cWidth, cHeight) / 4));
				String str = String.valueOf(view.filter.getProbabilityAt(i, j));

				FontMetrics matrix = graphics.getFontMetrics();
				int ht = matrix.getAscent();
				int wd = matrix.stringWidth(str);

				// Draw Image in center of the cell
				graphics.drawString(str, (j * (cWidth) + cWidth / 2 - wd / 2), (i * (cHeight) + cHeight / 2 + ht / 2));

			}
		}

		g.drawImage(image, 0, LABEL_HEIGHT, this);
	}

}
