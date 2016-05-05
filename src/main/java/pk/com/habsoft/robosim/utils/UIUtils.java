package pk.com.habsoft.robosim.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class UIUtils {
    
    private UIUtils() {
        
    }

	public static Font labelFont = new Font("Comic", Font.BOLD, 16);

	public static JPanel createSpinnerPanel(String text, JSpinner spn, int value, int min, int max, int step) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridLayout(1, 2));
		pnl.add(new JLabel(" " + text + " "));
		spn.setModel(new SpinnerNumberModel(value, min, max, step));
		pnl.add(spn);
		return pnl;
	}

	public static JPanel createSpinnerPanel(String text, JSpinner spn, double value, double min, double max,
			double step) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridLayout(1, 2));
		pnl.add(new JLabel(" " + text + " "));
		spn.setModel(new SpinnerNumberModel(value, min, max, step));
		pnl.add(spn);
		return pnl;
	}

	public static JLabel createLabel(int width, int height, String text) {
		JLabel header = new JLabel();
		// header.setSize(width, height);
		header.setVerticalTextPosition(SwingConstants.CENTER);
		header.setHorizontalTextPosition(SwingConstants.CENTER);
		header.setPreferredSize(new Dimension(width, height));
		header.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setBounds(0, 0, width, height);
		header.setFont(labelFont);
		header.setText(text);
		return header;

	}
}
