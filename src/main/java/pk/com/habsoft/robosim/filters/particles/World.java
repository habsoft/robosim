package pk.com.habsoft.robosim.filters.particles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;

import pk.com.habsoft.robosim.utils.RobotLogger;

public class World {
	private static String propertyFile = "config/Particle.properties";
	final static String TAG_WALL_THICKNESS = "WALL_THICKNESS";

	final static String DEF_WORLD_WALL = "10";

	final static String DEF_LANDMARK_FILE = "config/landmarks.properties";
	final static int NUM_OF_LANDMARKS = 6;
	public static int LANDMARK_SIZE = 10;
	private Random r = new Random();
	static List<LandMark> landmarks = new LinkedList<LandMark>();

	Logger logger = RobotLogger.getLogger(this.getClass().getName());
	// final static World _instance = new World();

	private static int maxWidth = 500;
	private static int maxHeight = 500;
	private static int thikness = 10;

	public World() {

		Properties p = new Properties();
		try {
			FileInputStream fis = new FileInputStream(propertyFile);
			p.load(fis);
			loadFromProperties(p);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			loadFromFile(World.DEF_LANDMARK_FILE);
		} catch (Exception e) {
			logger.error("Unable to load Land Marks File " + e.getMessage());
			loadDefault();
		}
	}

	private static void loadFromProperties(Properties p) {
		thikness = Integer.parseInt(p.getProperty(TAG_WALL_THICKNESS, DEF_WORLD_WALL));

	}

	private void loadDefault() {
		logger.info("Initializing random landmarks");
		for (int i = 0; i < NUM_OF_LANDMARKS; i++) {
			System.out.println(World.getMaxWidth() - World.getWallSize() * 2 - LANDMARK_SIZE);
			int x = World.getWallSize() + r.nextInt(World.getMaxWidth() - World.getWallSize() * 2 - LANDMARK_SIZE);
			int y = World.getWallSize() + r.nextInt(World.getMaxHeight() - World.getWallSize() * 2 - LANDMARK_SIZE);
			landmarks.add(new LandMark(x, y));
		}
		saveToFile(World.DEF_LANDMARK_FILE);
	}

	public void loadFromFile(String file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.trim().length() > 0) {
				String[] args = line.split(",");
				int x = Integer.parseInt(args[0]);
				int y = Integer.parseInt(args[1]);
				if (x >= thikness && x <= (maxWidth - thikness * 2 - LANDMARK_SIZE) && y >= thikness
						&& y <= (maxHeight - thikness * 2 - LANDMARK_SIZE)) {
					// landmark is within the world boundary
				} else {
					// load random landmark
					logger.error("Value out of range " + x + " : " + y + " expecting :  " + (maxWidth - thikness * 2 - LANDMARK_SIZE)
							+ " - " + (maxHeight - thikness * 2 - LANDMARK_SIZE));
					x = thikness + r.nextInt(maxWidth - thikness * 2 - LANDMARK_SIZE);
					y = thikness + r.nextInt(maxHeight - thikness * 2 - LANDMARK_SIZE);
				}
				landmarks.add(new LandMark(x, y));
			}
		}
		in.close();
		// if (landmarks.size() != NUM_OF_LANDMARKS) {
		// landmarks.clear();
		// loadDefault();
		// }
	}

	public void saveToFile(String file) {
		if (landmarks != null && !landmarks.isEmpty()) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)));
				for (int i = 0; i < landmarks.size(); i++) {
					LandMark p = landmarks.get(i);
					bw.write(p.getX() + "," + p.getY() + "\n");
				}
				bw.flush();
				bw.close();
			} catch (IOException e) {
			}
		}
	}

	public static int getWidth() {
		return maxWidth;
	}

	public static int getHeight() {
		return maxHeight;
	}

	public static int getMaxWidth() {
		return maxWidth;
	}

	public static void setMaxWidth(int maxWidth) {
		World.maxWidth = maxWidth;
	}

	public static int getMaxHeight() {
		return maxHeight;
	}

	public static void setMaxHeight(int maxHeight) {
		World.maxHeight = maxHeight;
	}

	public static List<LandMark> getLandmark() {
		return landmarks;
	}

	public static int getWallSize() {
		return thikness;
	}

	public List<LandMark> getLandmarks() {
		return new LinkedList<LandMark>(landmarks);
	}

	// public void setLandmarks(List<LandMark> landmarks) {
	// this.landmarks = landmarks;
	// }

	public void setLandMarkSize(int size) {
		LANDMARK_SIZE = size;
	}

	@Override
	public String toString() {
		return "World [maxWidth=" + maxWidth + ", maxHeight=" + maxHeight + ", WallThickness=" + thikness + landmarks;
	}

}
