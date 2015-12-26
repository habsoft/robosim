package pk.com.habsoft.robosim.filters.particles.internal;

import java.awt.Graphics2D;

public interface SimulationListener {
	// public void init(CarController crtl, Background background);
	public void onUpdate(float dt);

	// public void onGPS(float[] pos);
	// public void onCamera(int[][] img);
	public void onPaint(Graphics2D g);
	// public void onScanner(int[][] dots);
}
