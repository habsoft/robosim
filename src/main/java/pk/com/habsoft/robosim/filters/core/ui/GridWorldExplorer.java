package pk.com.habsoft.robosim.filters.core.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.actions.Action;
import pk.com.habsoft.robosim.internal.RootView;

public class GridWorldExplorer {

	private static final String HELP_TEXT = "Use keys (A,S,D,W) to move left, back, right and forward respectively. Use key (L) to scan environment using Sonar Range Sensors. ";
	RootView frame;
	State baseState;
	Visualizer painter;

	protected Domain domain;
	protected Map<String, Action> keyActionMap;
	protected JTextArea stateConsole;

	public GridWorldExplorer(RootView frame, Domain domain, Visualizer painter, State baseState) {
		this.frame = frame;
		this.domain = domain;
		this.painter = painter;
		this.baseState = baseState;
		this.keyActionMap = new HashMap<>();
	}

	public void initGUI() {
		painter.setPreferredSize(new Dimension(600, 600));

		this.stateConsole = new JTextArea(40, 40);
		this.stateConsole.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret) this.stateConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.stateConsole.setEditable(false);
		this.stateConsole.setMargin(new Insets(10, 5, 10, 5));
		this.stateConsole.setText(HELP_TEXT);

		JScrollPane shellScroll = new JScrollPane(this.stateConsole, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		shellScroll.setPreferredSize(new Dimension(600, 50));

		frame.add(shellScroll, BorderLayout.SOUTH);

		frame.add(painter);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 600));

		frame.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				handleKeyPressed(e);
			}

		});

		// also add key listener to the painter in case the focus is changed
		painter.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				handleKeyPressed(e);
			}

		});

		frame.pack();
	}

	/**
	 * Specifies a string representation of an action to execute when the
	 * specified key is pressed. The string representation should have the first
	 * word be the action name, with spaces separating the parameters of the
	 * string representation of each parameter value.
	 * 
	 * @param key
	 *            the key that is pressed by the user
	 * @param actionStringRep
	 *            the {@link burlap.oomdp.singleagent.GroundedAction} to take
	 *            when the key is pressed
	 */
	public void addKeyAction(String key, String actionStringRep) {
		Action ga = domain.getAction(actionStringRep);
		if (ga == null) {
			System.out.println("Could not parse GroundedAction string representation of " + actionStringRep + ".\n" + "It is not being assigned to VisualExplorer key " + key + ".");
		} else {
			System.out.println(key);
			this.keyActionMap.put(key, ga);
		}
	}

	protected void handleKeyPressed(KeyEvent e) {

		String key = String.valueOf(e.getKeyChar());

		// otherwise this could be an action, see if there is an action mapping
		Action mappedAction = keyActionMap.get(key);
		if (mappedAction != null) {

			this.executeAction(mappedAction);

		}

	}

	protected void executeAction(Action ga) {

		State newState = ga.performAction(this.baseState);
		updateState(newState);

	}

	/**
	 * Updates the currently visualized state to the input state.
	 * 
	 * @param s
	 *            the state to visualize.
	 */
	synchronized public void updateState(State s) {
		this.baseState = s;
		this.painter.updateState(s);
	}

}
