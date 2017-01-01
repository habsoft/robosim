package pk.com.habsoft.robosim.main;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.DefaultDesktopManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import pk.com.habsoft.robosim.filters.core.ui.HistogramMain;
import pk.com.habsoft.robosim.filters.histogram.HistogramFilterView;
import pk.com.habsoft.robosim.filters.kalman.KalmanFilterView;
import pk.com.habsoft.robosim.filters.particles.views.ParticleFilterView;
import pk.com.habsoft.robosim.internal.RootView;
import pk.com.habsoft.robosim.planning.pathplanning.views.PathPlannerView;
import pk.com.habsoft.robosim.planning.pathsmoother.views.PathSmoothingView;
import pk.com.habsoft.robosim.smoothing.views.PIDControllerView;

public class RoboSim extends JFrame implements ActionListener {
	// Numbus, Napkin
	private final static String APPLICATION_TITLE = "RoboSim (Robot Simulator)";
	// First version
	// final static String version = "1.0.3.2012-07-12";
	// final static String version = "1.1.0.2012-09-26";
	// final static String version = "1.1.1.2012-10-16";
	// Stochastic motion in DP
	// final static String version = "1.1.2(2012-11-13)";
	// Bounded Vision
	// final static String version = "1.1.3(2013-1-1)";
	// Path Smoother View
	// final static String version = "1.2.0(2013-4-1)";
	// UI enhancements
	// final static String version = " 1.3.0 (2013-6-20)";
	// Bounded angle in Particle Filter
	// final static String version = " 1.3.1 (2014-1-28)";
	// PID controller
	// final static String version = " 1.3.2 (2014-4-1)";
	// Some code cleanup.
	// final static String version = "  1.6.0 (2016-05-05)";
	// Histogram Filter with SonarRangeFinder
	final static String version = "  2.0.0 (2016-08-14)";
	private static final long serialVersionUID = 1L;

	private JDesktopPane desk;

	JMenuItem miExit;
	JMenuItem miAbout;

	public RoboSim(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		String os = System.getProperty("os.name");
		try {
			if (os != null && os.contains("Windows")) {
				// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else {

				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				// UIManager.setLookAndFeel("Napkin");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// Create a desktop and set it as the content pane. Don't set the
		// layered
		// pane, since it needs to hold the menu bar too.
		desk = new JDesktopPane();
		setContentPane(desk);

		// Install our custom desktop manager.
		desk.setDesktopManager(new SampleDesktopMgr());

		createMenuBar();
		loadBackgroundImage();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (e.getID() == WindowEvent.WINDOW_CLOSING) {
					JInternalFrame[] frms = desk.getAllFrames();
					for (int i = 0; i < frms.length; i++) {
						try {
							if (frms[i] instanceof RootView) {
								((RootView) frms[i]).setClosed(true);
								// ((RootView) frms[i]).saveProperties();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}
		});
	}

	// Create a menu bar to show off a few things.
	protected void createMenuBar() {
		JMenuBar mb = new JMenuBar();

		JMenu mnFile = new JMenu("File");

		mnFile.add(new TileAction(desk)); // add tiling capability

		miExit = new JMenuItem("Exit");
		miExit.addActionListener(this);
		mnFile.add(miExit);

		// //////////////////////////////////////////////////

		// Localization Menu
		JMenu mnLocalization = new JMenu("Localization");

		// Histogram Filters
		JInternalFrame histogramFilter = new HistogramFilterView();
		mnLocalization.add(new AddFrameAction("Histogram Filter(Color Sensor)", histogramFilter));
		desk.add(histogramFilter);

		// Histogram Filters
		JInternalFrame histogramFilter2 = new HistogramMain();
		mnLocalization.add(new AddFrameAction("Histogram Filter(Sonar Range Finder)", histogramFilter2));
		desk.add(histogramFilter2);

		// Kalman Filters
		JInternalFrame kalmanFilter = new KalmanFilterView();
		mnLocalization.add(new AddFrameAction("Kalman Filter", kalmanFilter));
		desk.add(kalmanFilter);

		// Particle Filters
		JInternalFrame particleFilter = new ParticleFilterView();
		mnLocalization.add(new AddFrameAction("Particle Filter", particleFilter));
		desk.add(particleFilter);

		// ////////////////////////////////////////////////////////////

		// Planning Menu
		JMenu mnPlanning = new JMenu("Planning");

		// Path Planning
		JInternalFrame pathPlanning = new PathPlannerView();
		mnPlanning.add(new AddFrameAction("Path Planning", pathPlanning));
		desk.add(pathPlanning);

		// ///////////////////////////////////////////////////////////////

		// Optimization Menu
		JMenu mnOptimization = new JMenu("Optimization");

		JInternalFrame gradientDescent = new PathSmoothingView();
		mnOptimization.add(new AddFrameAction("Gradient Descent", gradientDescent));
		desk.add(gradientDescent);

		// Control Menu
		JMenu mnControl = new JMenu("Controller");
		JInternalFrame pidController = new PIDControllerView();
		mnControl.add(new AddFrameAction("PID Controller", pidController));
		desk.add(pidController);

		// //////////////////////////////////////////////////////////////

		JMenu mnHelp = new JMenu("Help");

		miAbout = new JMenuItem("About");
		miAbout.addActionListener(this);
		mnHelp.add(miAbout);

		// ////////////////////////////////////////////////////////////

		setJMenuBar(mb);
		mb.add(mnFile);
		mb.add(mnLocalization);
		mb.add(mnPlanning);
		mb.add(mnOptimization);
		mb.add(mnControl);
		mb.add(mnHelp);
	}

	// Here we load a background image for our desktop.
	protected void loadBackgroundImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/bg2.jpg"));
		JLabel l = new JLabel(icon);
		l.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());

		// Place the image in the lowest possible layer so nothing
		// can ever be painted under it.
		desk.add(l, new Integer(Integer.MIN_VALUE));
	}

	// This class adds a new JInternalFrame when requested.
	class AddFrameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		JInternalFrame frame = null;

		public AddFrameAction(String name, JInternalFrame frame) {
			super(name);
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			RootView view = (RootView) frame;
			view.setVisible(true); // Needed since 1.3
			try {
				view.setSelected(true);
				if (!view.isInit)
					view.initGUI();
			} catch (PropertyVetoException e) {
			}
		}

		// private Integer layer;
		// private String name;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof JMenuItem) {
			if (obj.equals(miExit)) {
				System.exit(0);
			} else if (obj.equals(miAbout)) {
				showContactDetail();

			}
		}
	}

	private void showContactDetail() {
		// URL url = RoboSim.class.getResource("images/about.jpg");
		Icon ico = new ImageIcon(getClass().getResource("/images/about.jpg"));
		JOptionPane.showOptionDialog(null, "                RoboSim(Robot Simulator)\nVersion = " + version
				+ "\nIf you need any help regarding this software, I am just an email away.\nEmail = faisal.hameed.pk@gmail.com\nSkype = faisal.hameed.pk", "About Me", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, ico, new Object[] {}, null);

	}

	public static void main(String[] args) {

		RoboSim robosim = new RoboSim(APPLICATION_TITLE + version);

		Toolkit tool = Toolkit.getDefaultToolkit();
		robosim.setSize(tool.getScreenSize());
		robosim.setVisible(true);

		// robosim.showContactDetail();

	}
}

class TileAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JDesktopPane desk; // the desktop to work with

	public TileAction(JDesktopPane desk) {
		super("Show All Frames");
		this.desk = desk;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		// How many frames do we have?
		JInternalFrame[] allframes = desk.getAllFrames();
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = desk.getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (!f.isClosed() && f.isIcon()) {
					try {
						f.setIcon(false);
					} catch (PropertyVetoException ignored) {
					}
				}

				desk.getDesktopManager().resizeFrame(f, x, y, w, h);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
	}

}

// SampleDesktopMgr.java
// A DesktopManager that keeps its frames inside the desktop.

class SampleDesktopMgr extends DefaultDesktopManager {

	private static final long serialVersionUID = 1L;

	// This is called anytime a frame is moved. This
	// implementation keeps the frame from leaving the desktop.
	@Override
	public void dragFrame(JComponent f, int x, int y) {
		if (f instanceof JInternalFrame) { // Deal only w/internal frames
			JInternalFrame frame = (JInternalFrame) f;
			JDesktopPane desk = frame.getDesktopPane();
			Dimension d = desk.getSize();

			// Nothing all that fancy below, just figuring out how to adjust
			// to keep the frame on the desktop.
			if (x < 0) { // too far left?
				x = 0; // flush against the left side
			} else {
				if (x + frame.getWidth() > d.width) { // too far right?
					x = d.width - frame.getWidth(); // flush against right side
				}
			}
			if (y < 0) { // too high?
				y = 0; // flush against the top
			} else {
				if (y + frame.getHeight() > d.height) { // too low?
					y = d.height - frame.getHeight(); // flush against the
					// bottom
				}
			}
		}

		// Pass along the (possibly cropped) values to the normal drag handler.
		super.dragFrame(f, x, y);
	}
}