package pk.com.habsoft.robosim.filters.particles;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pk.com.habsoft.robosim.filters.particles.internal.IRobot;
import pk.com.habsoft.robosim.filters.particles.internal.SimulationObject;
import pk.com.habsoft.robosim.filters.particles.views.ParticleFilterView;
import pk.com.habsoft.robosim.filters.particles.views.SimulationPanel;

public class ParticleSimulator implements Runnable, Iterable<SimulationObject> {
	private IRobot robot;
	private IRobot ghost;
	// private World world;
	private boolean run = false;
	private int timeDelay = 300;
	private int count = Integer.MAX_VALUE;
	private Thread currentThread;
	private int paricles;
	IRobot[] particleList;
	double sense_noise;// For sense function.
	double steering_noise;// For move function.
	double forward_noise;// For move function
	double[][] motions;
	double newParticleRatio = 0;
	double unSampledRatio = 0;
	int landMarkSize = 0;
	boolean showGhost = false;

	ParticleFilter filter = new ParticleFilter();
	private SimulationPanel gui;
	private ParticleFilterView output;
	private List<SimulationObject> objects;

	public ParticleSimulator() {
		// world = new World();
		new World();
	}

	public void reset(int particles, double distanceNoise, double steeringNoise, double forwardNoise, int robotSize,
			int ghostSize, int particleSize, double[][] motions, double newParticles, double unSampled,
			int landMarkSize) {
		this.paricles = particles;
		particleList = new IRobot[paricles];
		this.sense_noise = distanceNoise;
		this.steering_noise = steeringNoise;
		this.forward_noise = forwardNoise;
		this.motions = motions;
		this.newParticleRatio = newParticles;
		this.unSampledRatio = unSampled;
		this.landMarkSize = landMarkSize;
		filter.setNoise(distanceNoise, steeringNoise, forwardNoise);

		objects = new LinkedList<SimulationObject>();

		// objects.add(world);
		World.LANDMARK_SIZE = landMarkSize;
		for (int i = 0; i < World.getLandmark().size(); i++) {
			objects.add((SimulationObject) World.getLandmark().get(i));
		}

		// IRobot temp = new Robot(particleSize);
		IRobot temp = new Robot(particleSize);
		// add particles
		for (int i = 0; i < paricles; i++) {
			try {
				IRobot r = (IRobot) temp.clone();
				r.setNoise(sense_noise, steering_noise, forward_noise);
				r.random();
				objects.add(r);
				particleList[i] = r;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		IRobot car = new Robot(robotSize, RobotType.ROBOT);
		objects.add((SimulationObject) car);
		this.robot = car;

		IRobot gh = new Robot(ghostSize, RobotType.GHOST);
		gh.setLocation(filter.getAveragePosition(particleList));
		this.ghost = gh;
		showGhost(this.showGhost);

	}

	public void setMotions(double[][] motions, double newParticles, double unSampledRatio) {
		this.motions = motions;
		this.newParticleRatio = newParticles;
		this.unSampledRatio = unSampledRatio;
	}

	public void setLaserRange(boolean boundedVision, int laserRange, int laserAngle) {
		for (Iterator<SimulationObject> iter = objects.iterator(); iter.hasNext();) {
			SimulationObject obj = iter.next();
			if (obj instanceof IRobot) {
				IRobot temp = (IRobot) obj;
				temp.setBoundedVision(boundedVision);
				temp.setLaserRange(laserRange);
				temp.setLaserAngle(laserAngle);
			}
		}
		paint();
	}

	public void showGhost(boolean show) {
		if (show)
			objects.add(ghost);
		else
			objects.remove(ghost);
		paint();

	}

	private boolean start() {
		if (currentThread == null) {
			currentThread = new Thread(this);
			currentThread.start();
			return true;
		}
		return false;
	}

	public void simulate() {
		this.count = Integer.MAX_VALUE;
		start();
	}

	public void nextStep() {
		this.count = 1;
		start();
	}

	public void kidnapRobot() {
		robot.random();
		paint();
		output.showOutPut("-----------------------");
		output.showOutPut("OOppsssssssss Someone Kidnaped Me");
		output.showOutPut("" + robot);
		output.showOutPut("-----------------------");
	}

	@Override
	public void run() {
		setRunning(true);
		int i = 0;
		while (isRunning() && i < count) {
			i++;
			long time = System.currentTimeMillis();

			// for (SimulationObject o : objects)
			// o.update(0.02f);

			double error = filter.filter(robot, particleList, ghost, motions, particleList.length, newParticleRatio,
					unSampledRatio);
			output.showOutPut("Iteration = " + i);
			output.showOutPut("" + robot);
			output.showOutPut("" + ghost);
			output.showOutPut("Error        = " + error);
			output.showOutPut("--------------------------------------------");

			if (gui != null) {
				paint();
			}

			try {
				time = System.currentTimeMillis() - time;
				time = timeDelay - time;
				if (time > 0) {
					Thread.sleep(time); // bad, fix it
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		currentThread = null;
	}

	@Override
	public Iterator<SimulationObject> iterator() {
		return objects.iterator();
	}

	public static boolean runa = true;

	/**
	 * This will draw new objects on Canvas
	 */
	public void paint() {
		if (output != null) {
			gui.canvas.invalidate();
			gui.canvas.repaint();
		}
	}

	public synchronized void setRunning(boolean b) {
		run = b;
	}

	public synchronized boolean isRunning() {
		return run;
	}

	public void setSimulationPanel(SimulationPanel p) {
		gui = p;
	}

	public void setOutPutPanel(ParticleFilterView p) {
		output = p;
	}

	public void setTimeDelay(int i) {
		timeDelay = i;
	}

}
