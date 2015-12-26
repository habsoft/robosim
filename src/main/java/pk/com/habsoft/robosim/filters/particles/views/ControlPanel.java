package pk.com.habsoft.robosim.filters.particles.views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import pk.com.habsoft.robosim.filters.particles.ParticleSimulator;
import pk.com.habsoft.robosim.filters.particles.World;
import pk.com.habsoft.robosim.internal.PropertiesListener;
import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.utils.RobotLogger;
import pk.com.habsoft.robosim.utils.UIUtils;

public class ControlPanel extends JPanel implements ActionListener, ChangeListener, PropertiesListener {
	private static final long serialVersionUID = 1L;
	private static char degree = '\u00B0';
	private ParticleSimulator simulation;
	private Properties prop;
	private Logger log = RobotLogger.getLogger(ControlPanel.class.getName());

	final static String TAG_PARTICLES = "PARTICLES";
	int DEF_PARTICLES = 1000;
	private JSpinner spnParticles = new JSpinner();

	final static String TAG_SENSE_NOISE = "SENSE_NOISE";
	double DEF_SENSE_NOISE = 10;
	private JSpinner spnSenseNoise = new JSpinner();

	final static String TAG_STEERING_NOISE = "STEERING_NOISE";
	int DEF_STEERING_NOISE = 1;
	private JSpinner spnSteeringNoise = new JSpinner();

	final static String TAG_FORWARD_NOISE = "FORWARD_NOISE";
	double DEF_FORWARD_NOISE = 0.05;
	private JSpinner spnForwardNoise = new JSpinner();

	final static String TAG_ROBOT_SIZE = "ROBOT_SIZE";
	int DEF_ROBOT_SIZE = 10;
	private JSpinner spnRobotSize = new JSpinner();

	final static String TAG_GHOST_SIZE = "GHOST_SIZE";
	int DEF_GHOST_SIZE = 10;
	private JSpinner spnGhostSize = new JSpinner();

	final static String TAG_PARTICLE_SIZE = "PARTICLE_SIZE";
	int DEF_PARTICLE_SIZE = 4;
	private JSpinner spnParticleSize = new JSpinner();

	final static String TAG_LANDMARK_SIZE = "LANDMARK_SIZE";
	int DEF_LANDMARK_SIZE = 10;
	private JSpinner spnLandMarkSize = new JSpinner();

	final static String TAG_MOTION_ANGLE = "MOTION_ANGLE";
	int DEF_MOTION_ANGLE = 3;
	private JSpinner spnMotionAngle = new JSpinner();

	final static String TAG_MOTIONS_SPEED = "MOTIONS_SPEED";
	double DEF_MOTIONS_SPEED = 5;
	private JSpinner spnMotionSpeed = new JSpinner();

	final static String TAG_NEW_PARTICLES_RATIO = "NEW_PARTICLES_RATIO";
	double DEF_NEW_PARTICLES_RATIO = 0.01;
	private JSpinner spnNewParticlesRatio = new JSpinner();

	final static String TAG_UNSAMPLED_PARTICLES_RATIO = "UNSAMPLED_PARTICLES_RATIO";
	double DEF_UNSAMPLED_PARTICLES_RATIO = 0.01;
	private JSpinner spnUnsampledRatio = new JSpinner();

	final static String TAG_BOUNDED_VISION = "BOUNDED_VISION";
	boolean DEF_BOUNDED_VISION = false;
	private JCheckBox chkBoundedVision;

	final static String TAG_SHOW_GHOST = "SHOW_GHOST";
	boolean DEF_SHOW_GHOST = true;
	private JCheckBox chkShowGhost;

	final static String TAG_LASER_RANGE = "LASER_RANGE";
	int DEF_LASER_RANGE = 200;
	private JSpinner spnLaserRange = new JSpinner();

	final static String TAG_LASER_ANGLE = "LASER_ANGLE";
	int DEF_LASER_ANGLE = 60;
	private JSpinner spnLaserAngle = new JSpinner();

	final static String TAG_SIMULATION_SPEED = "SIMULATION_SPEED";
	int DEF_SIMULATION_SPEED = 300;

	private JButton btnApplySetting;
	private JButton btnApplyMotion;
	private JButton btnKidnapRobot;

	private JSlider slSpeed;
	private JButton btnNext;
	private JRadioButton rbStart, rbStop;

	private JPanel pnlNorth, pnlSouth;

	public ControlPanel(ParticleSimulator s, Properties prop, int width, int height) {
		this.prop = prop;
		this.simulation = s;
		loadProperties();
		initGUI(width, height);
	}

	private void initGUI(int width, int height) {

		this.setLayout(null);
		double pnlWestRatio = ParticleFilterView.PNL_WORLD_RATIO;

		// //////////////// Filter Setting ////////////////////////////
		RPanel pnlWest = new RPanel(width * pnlWestRatio, height, "Filter Settings");
		pnlWest.setLayout(new BorderLayout(), true);
		pnlWest.setBounds(0, 0, (int) (width * pnlWestRatio), height);

		// Create North Panel
		pnlNorth = new JPanel();
		pnlNorth.setLayout(new GridLayout(3, 3, 5, 1));
		int hypotenous = (int) (Math.sqrt(Math.pow(World.getHeight(), 2) + Math.pow(World.getWidth(), 2)));

		pnlNorth.add(UIUtils.createSpinnerPanel("Sense Noise", spnSenseNoise, DEF_SENSE_NOISE, 0, hypotenous, 0.01));
		pnlNorth.add(UIUtils.createSpinnerPanel("Steering Noise(" + degree + ")", spnSteeringNoise, DEF_STEERING_NOISE, -360, 360, 1));
		pnlNorth.add(UIUtils.createSpinnerPanel("Forward Noise", spnForwardNoise, DEF_FORWARD_NOISE, 0, hypotenous, 0.01));

		pnlNorth.add(UIUtils.createSpinnerPanel("Robot Size", spnRobotSize, DEF_ROBOT_SIZE, 1, 100, 1));
		pnlNorth.add(UIUtils.createSpinnerPanel("Ghost Size", spnGhostSize, DEF_GHOST_SIZE, 1, 100, 1));
		pnlNorth.add(UIUtils.createSpinnerPanel("Particle Size", spnParticleSize, DEF_PARTICLE_SIZE, 1, 100, 1));

		pnlNorth.add(UIUtils.createSpinnerPanel("No of Particles", spnParticles, DEF_PARTICLES, 0, Integer.MAX_VALUE, 1));
		pnlNorth.add(UIUtils.createSpinnerPanel("Land Mark Size", spnLandMarkSize, DEF_LANDMARK_SIZE, 1, 50, 1));
		btnApplySetting = new JButton("Apply Settings");
		btnApplySetting.addActionListener(this);
		pnlNorth.add(btnApplySetting);

		// /////////// Create South Panel ////////////////////
		pnlSouth = new JPanel();

		pnlSouth.add(new JLabel("Simulation Speed"));
		rbStart = new JRadioButton("Start");
		rbStart.addActionListener(this);

		rbStop = new JRadioButton("Stop");
		rbStop.addActionListener(this);
		rbStop.setSelected(true);

		ButtonGroup gp = new ButtonGroup();
		gp.add(rbStart);
		gp.add(rbStop);

		btnNext = new JButton("Next Step");
		btnNext.addActionListener(this);

		slSpeed = new JSlider(0, 490);
		slSpeed.addChangeListener(this);
		slSpeed.setValue(DEF_SIMULATION_SPEED);

		pnlSouth.add(slSpeed);
		pnlSouth.add(rbStart);
		pnlSouth.add(rbStop);
		pnlSouth.add(btnNext);

		pnlWest.add(pnlNorth, BorderLayout.NORTH);
		pnlWest.add(pnlSouth, BorderLayout.SOUTH);

		// ///////////////// Runtime Setting ////////////////////////////

		RPanel pnlEast = new RPanel(width * (1 - pnlWestRatio), height, "Runtime Settings");
		pnlEast.setLocation((int) (width * pnlWestRatio), 0);
		pnlEast.setLayout(new GridLayout(5, 2, 10, 2), true);

		// Motion Controls
		pnlEast.add(UIUtils.createSpinnerPanel("Turning Angle(" + degree + ")", spnMotionAngle, DEF_MOTION_ANGLE, -360, 360, 1));

		pnlEast.add(UIUtils.createSpinnerPanel("Speed", spnMotionSpeed, DEF_MOTIONS_SPEED, 0, 100, 1));

		pnlEast.add(UIUtils.createSpinnerPanel("New Particles %", spnNewParticlesRatio, DEF_NEW_PARTICLES_RATIO, 0, 1, 0.01));

		pnlEast.add(UIUtils.createSpinnerPanel("UnSampled %", spnUnsampledRatio, DEF_UNSAMPLED_PARTICLES_RATIO, 0, 1, 0.01));

		pnlEast.add(chkBoundedVision = new JCheckBox("Bounded Vision"));
		chkBoundedVision.setSelected(DEF_BOUNDED_VISION);

		pnlEast.add(chkShowGhost = new JCheckBox("Show Ghost"));
		chkShowGhost.setSelected(DEF_SHOW_GHOST);

		pnlEast.add(UIUtils.createSpinnerPanel("Laser Range", spnLaserRange, DEF_LASER_RANGE, 0, hypotenous, 1));

		pnlEast.add(UIUtils.createSpinnerPanel("Laser Angle", spnLaserAngle, DEF_LASER_ANGLE, 0, 360, 1));

		btnApplyMotion = new JButton("Apply Motions");
		btnApplyMotion.addActionListener(this);
		pnlEast.add(btnApplyMotion);
		btnKidnapRobot = new JButton("Kidnap Robot");
		btnKidnapRobot.addActionListener(this);
		pnlEast.add(btnKidnapRobot);

		// ////////////////////////////////////

		// Add panels to Main Panel

		add(pnlWest);
		add(pnlEast);

		simulation.reset(DEF_PARTICLES, DEF_SENSE_NOISE, Math.toRadians(DEF_STEERING_NOISE), DEF_FORWARD_NOISE, DEF_ROBOT_SIZE, DEF_GHOST_SIZE, DEF_PARTICLE_SIZE,
				new double[][] { { Math.toRadians(DEF_MOTION_ANGLE), DEF_MOTIONS_SPEED } }, DEF_NEW_PARTICLES_RATIO, DEF_UNSAMPLED_PARTICLES_RATIO, DEF_LANDMARK_SIZE);
		simulation.setLaserRange(DEF_BOUNDED_VISION, DEF_LASER_RANGE, DEF_LASER_ANGLE);
		simulation.showGhost(DEF_SHOW_GHOST);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object o = arg0.getSource();
		if (o.equals(btnNext)) {
			simulation.nextStep();
		} else if (o.equals(rbStart)) {
			simulation.simulate();
			btnNext.setEnabled(false);
			btnApplySetting.setEnabled(false);
		} else if (o.equals(rbStop)) {
			simulation.setRunning(false);
			btnNext.setEnabled(true);
			btnApplySetting.setEnabled(true);
		} else if (o.equals(btnApplySetting)) {
			DEF_PARTICLES = Integer.parseInt(spnParticles.getValue().toString());
			DEF_SENSE_NOISE = Double.parseDouble(spnSenseNoise.getValue().toString());
			DEF_STEERING_NOISE = Integer.parseInt(spnSteeringNoise.getValue().toString());
			DEF_FORWARD_NOISE = Double.parseDouble(spnForwardNoise.getValue().toString());
			DEF_ROBOT_SIZE = Integer.parseInt(spnRobotSize.getValue().toString());
			DEF_GHOST_SIZE = Integer.parseInt(spnGhostSize.getValue().toString());
			DEF_PARTICLE_SIZE = Integer.parseInt(spnParticleSize.getValue().toString());
			DEF_LANDMARK_SIZE = Integer.parseInt(spnLandMarkSize.getValue().toString());
			DEF_MOTION_ANGLE = Integer.parseInt(spnMotionAngle.getValue().toString());
			DEF_MOTIONS_SPEED = Double.parseDouble(spnMotionSpeed.getValue().toString());
			DEF_NEW_PARTICLES_RATIO = Double.parseDouble(spnNewParticlesRatio.getValue().toString());
			DEF_UNSAMPLED_PARTICLES_RATIO = Double.parseDouble(spnUnsampledRatio.getValue().toString());

			DEF_LASER_RANGE = Integer.parseInt(spnLaserRange.getValue().toString());
			DEF_LASER_ANGLE = Integer.parseInt(spnLaserAngle.getValue().toString());
			DEF_BOUNDED_VISION = chkBoundedVision.isSelected();
			DEF_SHOW_GHOST = chkShowGhost.isSelected();

			simulation.reset(DEF_PARTICLES, DEF_SENSE_NOISE, Math.toRadians(DEF_STEERING_NOISE), DEF_FORWARD_NOISE, DEF_ROBOT_SIZE, DEF_GHOST_SIZE, DEF_PARTICLE_SIZE,
					new double[][] { { Math.toRadians(DEF_MOTION_ANGLE), DEF_MOTIONS_SPEED } }, DEF_NEW_PARTICLES_RATIO, DEF_UNSAMPLED_PARTICLES_RATIO, DEF_LANDMARK_SIZE);
			simulation.setLaserRange(DEF_BOUNDED_VISION, DEF_LASER_RANGE, DEF_LASER_ANGLE);
			simulation.showGhost(DEF_SHOW_GHOST);

		} else if (o.equals(btnApplyMotion)) {
			DEF_MOTION_ANGLE = Integer.parseInt(spnMotionAngle.getValue().toString());
			DEF_MOTIONS_SPEED = Double.parseDouble(spnMotionSpeed.getValue().toString());
			DEF_NEW_PARTICLES_RATIO = Double.parseDouble(spnNewParticlesRatio.getValue().toString());
			DEF_UNSAMPLED_PARTICLES_RATIO = Double.parseDouble(spnUnsampledRatio.getValue().toString());
			DEF_LASER_RANGE = Integer.parseInt(spnLaserRange.getValue().toString());
			DEF_LASER_ANGLE = Integer.parseInt(spnLaserAngle.getValue().toString());
			DEF_BOUNDED_VISION = chkBoundedVision.isSelected();
			DEF_SHOW_GHOST = chkShowGhost.isSelected();

			simulation.setMotions(new double[][] { { Math.toRadians(DEF_MOTION_ANGLE), DEF_MOTIONS_SPEED } }, DEF_NEW_PARTICLES_RATIO, DEF_UNSAMPLED_PARTICLES_RATIO);
			simulation.setLaserRange(DEF_BOUNDED_VISION, DEF_LASER_RANGE, DEF_LASER_ANGLE);
			simulation.showGhost(DEF_SHOW_GHOST);

		} else if (o.equals(btnKidnapRobot)) {
			simulation.kidnapRobot();
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		DEF_SIMULATION_SPEED = slSpeed.getValue();
		simulation.setTimeDelay(500 - DEF_SIMULATION_SPEED);
	}

	@Override
	public boolean loadProperties() {
		try {
			DEF_PARTICLES = Integer.parseInt(prop.getProperty(TAG_PARTICLES, "" + DEF_PARTICLES));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_PARTICLES);
		}
		try {
			DEF_SENSE_NOISE = Double.parseDouble(prop.getProperty(TAG_SENSE_NOISE, "" + DEF_SENSE_NOISE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_SENSE_NOISE);
		}
		try {
			DEF_STEERING_NOISE = Integer.parseInt(prop.getProperty(TAG_STEERING_NOISE, "" + DEF_STEERING_NOISE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_STEERING_NOISE);
		}
		try {
			DEF_FORWARD_NOISE = Double.parseDouble(prop.getProperty(TAG_FORWARD_NOISE, "" + DEF_FORWARD_NOISE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_FORWARD_NOISE);
		}
		try {
			DEF_ROBOT_SIZE = Integer.parseInt(prop.getProperty(TAG_ROBOT_SIZE, "" + DEF_ROBOT_SIZE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_ROBOT_SIZE);
		}
		try {
			DEF_GHOST_SIZE = Integer.parseInt(prop.getProperty(TAG_GHOST_SIZE, "" + DEF_GHOST_SIZE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_GHOST_SIZE);
		}
		try {
			DEF_PARTICLE_SIZE = Integer.parseInt(prop.getProperty(TAG_PARTICLE_SIZE, "" + DEF_PARTICLE_SIZE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_PARTICLE_SIZE);
		}
		try {
			DEF_LANDMARK_SIZE = Integer.parseInt(prop.getProperty(TAG_LANDMARK_SIZE, "" + DEF_LANDMARK_SIZE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_LANDMARK_SIZE);
		}

		try {
			DEF_MOTION_ANGLE = Integer.parseInt(prop.getProperty(TAG_MOTION_ANGLE, "" + DEF_MOTION_ANGLE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_MOTION_ANGLE);
		}
		try {
			DEF_MOTIONS_SPEED = Double.parseDouble(prop.getProperty(TAG_MOTIONS_SPEED, "" + DEF_MOTIONS_SPEED));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_MOTIONS_SPEED);
		}
		try {
			DEF_NEW_PARTICLES_RATIO = Double.parseDouble(prop.getProperty(TAG_NEW_PARTICLES_RATIO, "" + DEF_NEW_PARTICLES_RATIO));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_NEW_PARTICLES_RATIO);
		}
		try {
			DEF_UNSAMPLED_PARTICLES_RATIO = Double.parseDouble(prop.getProperty(TAG_UNSAMPLED_PARTICLES_RATIO, "" + DEF_UNSAMPLED_PARTICLES_RATIO));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_UNSAMPLED_PARTICLES_RATIO);
		}
		try {
			DEF_SIMULATION_SPEED = Integer.parseInt(prop.getProperty(TAG_SIMULATION_SPEED, "" + DEF_SIMULATION_SPEED));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_SIMULATION_SPEED);
		}
		try {
			if (prop.containsKey(TAG_BOUNDED_VISION)) {
				DEF_BOUNDED_VISION = prop.getProperty(TAG_BOUNDED_VISION).equalsIgnoreCase("true");
			}
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_BOUNDED_VISION);
		}
		try {
			DEF_LASER_RANGE = Integer.parseInt(prop.getProperty(TAG_LASER_RANGE, "" + DEF_LASER_RANGE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_LASER_RANGE);
		}
		try {
			DEF_LASER_ANGLE = Integer.parseInt(prop.getProperty(TAG_LASER_ANGLE, "" + DEF_LASER_ANGLE));
		} catch (NumberFormatException e) {
			log.error("Invalid value of tag " + TAG_LASER_ANGLE);
		}

		return true;
	}

	@Override
	public void saveProperties() {

		// Save these properties
		prop.setProperty(TAG_PARTICLES, "" + DEF_PARTICLES);
		prop.setProperty(TAG_SENSE_NOISE, "" + DEF_SENSE_NOISE);
		prop.setProperty(TAG_STEERING_NOISE, "" + DEF_STEERING_NOISE);
		prop.setProperty(TAG_FORWARD_NOISE, "" + DEF_FORWARD_NOISE);
		prop.setProperty(TAG_ROBOT_SIZE, "" + DEF_ROBOT_SIZE);
		prop.setProperty(TAG_GHOST_SIZE, "" + DEF_GHOST_SIZE);
		prop.setProperty(TAG_PARTICLE_SIZE, "" + DEF_PARTICLE_SIZE);
		prop.setProperty(TAG_LANDMARK_SIZE, "" + DEF_LANDMARK_SIZE);

		prop.setProperty(TAG_MOTION_ANGLE, "" + DEF_MOTION_ANGLE);
		prop.setProperty(TAG_MOTIONS_SPEED, "" + DEF_MOTIONS_SPEED);
		prop.setProperty(TAG_NEW_PARTICLES_RATIO, "" + DEF_NEW_PARTICLES_RATIO);
		prop.setProperty(TAG_UNSAMPLED_PARTICLES_RATIO, "" + DEF_UNSAMPLED_PARTICLES_RATIO);
		prop.setProperty(TAG_SIMULATION_SPEED, "" + DEF_SIMULATION_SPEED);
		prop.setProperty(TAG_LASER_RANGE, "" + DEF_LASER_RANGE);
		prop.setProperty(TAG_LASER_ANGLE, "" + DEF_LASER_ANGLE);
		prop.setProperty(TAG_BOUNDED_VISION, "" + DEF_BOUNDED_VISION);
	}

}
