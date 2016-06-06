package pk.com.habsoft.robosim.filters.core.ui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import pk.com.habsoft.robosim.filters.core.Domain;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.actions.Action;

public class GridWorldExplorer extends JFrame {

    State baseState;
    Visualizer painter;

    protected Domain domain;
    protected Map<String, Action> keyActionMap;

    public GridWorldExplorer(Domain domain, Visualizer painter, State baseState) {
        this.domain = domain;
        this.painter = painter;
        this.baseState = baseState;
        this.keyActionMap = new HashMap<>();
    }

    public void initGUI() {
        painter.setPreferredSize(new Dimension(600, 600));

        getContentPane().add(painter);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 600));

        addKeyListener(new KeyListener() {
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

        pack();
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
            System.out.println("Could not parse GroundedAction string representation of " + actionStringRep + ".\n"
                    + "It is not being assigned to VisualExplorer key " + key + ".");
        } else {
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
