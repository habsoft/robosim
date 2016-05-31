package pk.com.habsoft.robosim.filters.histogram;

import java.io.IOException;

import pk.com.habsoft.robosim.internal.BaseJsonConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HistogramConfig extends BaseJsonConfig<HistogramConfig> {

	public static final String FILE_NAME = "config/HistogramFilter.json";
	public static final int MAX_NO_OF_ROWS = 6;
	public static final int MIN_NO_OF_ROWS = 1;
	public static final int MAX_NO_OF_COLUMNS = 6;
	public static final int MIN_NO_OF_COLUMNS = 1;

	private int[][] world;
	private double sensorNoise;
	private double motionNoise;
	private boolean cyclicWorld;

	public HistogramConfig() {
		// default constructor for Json mapper
		super(FILE_NAME, HistogramConfig.class);
	}

	private HistogramConfig(int[][] world, double sensorNoise, double motionNoise, boolean cyclicWorld) {
		super(FILE_NAME, HistogramConfig.class);
		this.world = world;
		this.sensorNoise = sensorNoise;
		this.motionNoise = motionNoise;
		this.cyclicWorld = cyclicWorld;
	}

	public int[][] getWorld() {
		return world;
	}

	public void setWorld(int[][] world) {
		this.world = world.clone();
	}

	public double getSensorNoise() {
		return sensorNoise;
	}

	public void setSensorNoise(double sensorNoise) {
		this.sensorNoise = sensorNoise;
	}

	public double getMotionNoise() {
		return motionNoise;
	}

	public void setMotionNoise(double motionNoise) {
		this.motionNoise = motionNoise;
	}

	public boolean isCyclicWorld() {
		return cyclicWorld;
	}

	public void setCyclicWorld(boolean cyclicWorld) {
		this.cyclicWorld = cyclicWorld;
	}

	@Override
	public String toString() {
		return "HistogramConfig [world=" + world.length + ", sensorNoise=" + sensorNoise + ", motionNoise=" + motionNoise
				+ ", cyclicWorld=" + cyclicWorld + "]";
	}

	@Override
	public HistogramConfig loadDefault() {

		// TODO
		int[][] world = new int[][] { { 1 }, { 0 }, { 1 }, { 0 }, { 1 } };

		HistogramConfig con = new HistogramConfig(world, 0.2, 0.1, false);

		return con;
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		int[][] world = new int[][] { { 0, 0, 0, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 1, 0, 0, 0 } };

		// HistogramConfig con = new HistogramConfig(world, 0.2, 0.1, false);
		HistogramConfig con = new HistogramConfig();

		System.out.println(con.loadConfiguration().getWorld().length);
		System.out.println("file saved.");

	}

}
