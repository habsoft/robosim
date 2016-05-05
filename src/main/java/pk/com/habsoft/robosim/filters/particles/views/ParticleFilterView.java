package pk.com.habsoft.robosim.filters.particles.views;


import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pk.com.habsoft.robosim.filters.particles.ParticleSimulator;
import pk.com.habsoft.robosim.filters.particles.World;
import pk.com.habsoft.robosim.internal.RPanel;
import pk.com.habsoft.robosim.internal.RootView;

public class ParticleFilterView extends RootView {

	private static final long serialVersionUID = 1L;

	RPanel pnlOutput;
	SimulationPanel pnlWorld;
	ControlPanel pnlControls;
	JTextArea ta;

	final static int PNL_CONTROL_SIZE = 180;
	final static double PNL_WORLD_RATIO = 0.6;

	public static int PNL_WORLD_WIDTH;
	public static int PNL_WORLD_HEIGHT;

	static int PNL_OUTPUT_WIDTH;
	static int PNL_OUTPUT_HEIGHT;

	static int PNL_CONTROL_WIDTH;
	static int PNL_CONTROL_HEIGHT;

	ParticleSimulator sim;

	public ParticleFilterView() {
		super("Particle Filter", "config/Particle.properties");
		setLayout(new GridLayout(2, 2));
		setLayout(null);
		loadProperties();
	}

	@Override
	public void initGUI() {
		isInit = true;

		pnlOutput = new RPanel(PNL_OUTPUT_WIDTH, PNL_OUTPUT_HEIGHT, "Output Panel");
		pnlOutput.setLocation(PNL_WORLD_WIDTH, 0);
		pnlOutput.setLayout(new BorderLayout(), true);
		ta = new JTextArea();
		// ta.setLineWrap(false);
		// ta.setWrapStyleWord(false);
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setAutoscrolls(false);
		pnlOutput.add(scroll, BorderLayout.CENTER);

		add(pnlOutput);

		World.setMaxWidth(PNL_WORLD_WIDTH);
		World.setMaxHeight(PNL_WORLD_HEIGHT);
		sim = new ParticleSimulator();
		pnlWorld = new SimulationPanel(sim);
		pnlWorld.setBounds(0, 0, PNL_WORLD_WIDTH, PNL_WORLD_HEIGHT);
		pnlWorld.setBorder(lineBorder);
		sim.setSimulationPanel(pnlWorld);
		sim.setOutPutPanel(this);
		// sim.paint();
		add(pnlWorld);

		pnlControls = new ControlPanel(sim, prop, PNL_CONTROL_WIDTH, PNL_CONTROL_HEIGHT);
		pnlControls.setBounds(0, PNL_WORLD_HEIGHT, PNL_CONTROL_WIDTH, PNL_CONTROL_HEIGHT);
		add(pnlControls);

		setBounds(0, 0, screenSize.getWidth(), screenSize.getHeight());

	}

	public void showOutPut(String txt) {
		ta.append(" " + txt + "\n");
		ta.setCaretPosition(ta.getDocument().getLength());
	}

	@Override
	public boolean loadProperties() {
		System.out.println("Property File = " + propertyFile);
		PNL_WORLD_WIDTH = (int) (screenSize.getWidth() * PNL_WORLD_RATIO) - 15;
		PNL_WORLD_HEIGHT = (int) (screenSize.getHeight() - PNL_CONTROL_SIZE - 150);

		PNL_OUTPUT_HEIGHT = PNL_WORLD_HEIGHT;
		PNL_OUTPUT_WIDTH = (int) (screenSize.getWidth() * (1 - PNL_WORLD_RATIO)) - 15;

		PNL_CONTROL_HEIGHT = PNL_CONTROL_SIZE;
		PNL_CONTROL_WIDTH = (int) screenSize.getWidth() - 30;
		return super.loadProperties();
	}

	@Override
	public void saveProperties() {
		prop.clear();
		pnlControls.saveProperties();
		super.saveProperties();
	}

	@Override
	public void dispose() {
		if (sim != null) {
			sim.setRunning(false);
		}
		super.dispose();
	}

	@Override
	public void hide() {
		if (sim != null) {
			sim.setRunning(false);
		}
		super.hide();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JDesktopPane desk = new JDesktopPane();
		frame.setContentPane(desk);

		ParticleFilterView view1 = new ParticleFilterView();
		view1.initGUI();

		desk.add(view1);
		view1.setVisible(true);
		// Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		// frame.setLocation((int) size.getWidth() - 600, (int) size.getHeight()
		// - 800);
		// frame.setSize(600, 700);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

}
