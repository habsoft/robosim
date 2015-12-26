package pk.com.habsoft.robosim.filters.particles;

import java.awt.Color;
import java.awt.Graphics2D;

import pk.com.habsoft.robosim.filters.particles.internal.SimulationObject;

public class LandMark implements SimulationObject {

	int x, y;
	String text = "";

	private boolean blink = false;

	public LandMark(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void blink() {
		blink = true;
	}

	public void unblink() {
		blink = false;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "\nLandMark [x=" + x + ", y=" + y + "]";
	}

	@Override
	public void onPaint(Graphics2D g) {

		if (blink)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);
		g.fillRect(x, y, World.LANDMARK_SIZE, World.LANDMARK_SIZE);

	}

}
