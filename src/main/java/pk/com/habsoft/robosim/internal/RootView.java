/**
 * M Faisal Hameed
 */
package pk.com.habsoft.robosim.internal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

abstract public class RootView extends JInternalFrame implements PropertiesListener {

	private static final long serialVersionUID = 1L;
	protected String viewName = "";
	public int LABEL_HEIGHT = 30;
	public Border lineBorder;
	public boolean isInit = false;
	// TODO subtract 100 pixels
	public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	protected String propertyFile = "";
	protected Properties prop = new Properties();

	public RootView(String title, String propertyFile) {
		super(title, true, // resizable
				true, // closable
				true, // maximizable
				true);// iconifiable
		this.propertyFile = propertyFile;
		viewName = title;

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean loadProperties() {
		if (propertyFile != null && propertyFile.trim().length() > 0) {
			File f = new File(propertyFile);
			// If property file not exists then copy from default config folder.
			if (!f.exists()) {
				File ff = new File(getClass().getClassLoader().getResource(propertyFile).getFile());
				System.out.println(ff);
				f = ff;
			}
			System.out.println(f.getPath());
			if (f.exists()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					prop.load(fis);
					fis.close();
				} catch (Exception e) {
					System.out.println("Unable to read property file " + f.getAbsolutePath() + " ." + e.getMessage());
					return false;
				}
			} else {

				System.out.println("Property file not exists : " + f.getAbsolutePath());
				return false;
			}
		}
		return true;
	}

	@Override
	public void saveProperties() {
		try {
			// File f = new
			// File(getClass().getClassLoader().getResource(propertyFile).getFile());
			File f = new File(propertyFile);
			FileOutputStream out = new FileOutputStream(f);
			prop.store(out, "");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("** Saving properties file. " + propertyFile);
	}

	public void setBounds(double x, double y, double width, double height) {
		super.setBounds((int) x, (int) y, (int) width, (int) height);
	}

	public void setSize(double width, double height) {
		super.setSize((int) width, (int) height);
	}

	private void jbInit() throws Exception {
		this.setFont(new java.awt.Font("Dialog", 0, 10));
		lineBorder = BorderFactory.createLineBorder(Color.BLACK);

		this.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				this_internalFrameActivated(e);
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				this_internalFrameClosing(e);
			}
		});
	}

	public void this_internalFrameActivated(InternalFrameEvent e) {
	}

	public void this_internalFrameClosing(InternalFrameEvent e) {
		System.out.println("saving");
		if (isInit) {
			saveProperties();
		}
	}

	public abstract void initGUI();

	public void showView() {
		setVisible(true);
		try {
			setSelected(true);
			setIcon(false);
		} catch (java.beans.PropertyVetoException ex) {
			ex.printStackTrace();
		}
	}

}
