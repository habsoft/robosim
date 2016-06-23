package pk.com.habsoft.robosim.filters.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.ObjectClass;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;
import pk.com.habsoft.robosim.filters.core.State;
import pk.com.habsoft.robosim.filters.core.actions.ActionObserver;
import pk.com.habsoft.robosim.filters.core.objects.GridRobotBelief;
import pk.com.habsoft.robosim.utils.RoboMathUtils;

public class SonarRangeModule implements ObjectInstance, ActionObserver {

    ObjectClass objectClass;
    String objectName;

    List<RangeSensor> sensors = new ArrayList<>();

    public SonarRangeModule(ObjectClass objectClass, String objectName) {
        this.objectClass = objectClass;
        this.objectName = objectName;
        initDefaultSensors();
    }

    public SonarRangeModule(SonarRangeModule other) {
        this.objectClass = other.objectClass;
        this.objectName = other.objectName;
        this.sensors = new ArrayList<>(other.getSensors());
    }

    protected void initDefaultSensors() {
        // TODO config
        int range = 2;
        double noise = 0.1;
        this.sensors.add(new SonarRangeSensor("Up", 1, noise, new int[] { 0, 1 }));
        this.sensors.add(new SonarRangeSensor("Down", 1, noise, new int[] { 0, -1 }));
        // this.sensors.add(new SonarRangeSensor("Right", 2, noise, new int[] {
        // 1, 0 }));
        // this.sensors.add(new SonarRangeSensor("Left", 2, noise, new int[] {
        // -1, 0 }));
    }

    @Override
    public String getName() {
        return objectName;
    }

    @Override
    public String getClassName() {
        return objectClass.name;
    }

    @Override
    public String getObjectDescription() {
        return this.toString();
    }

    @Override
    public void setName(String newName) {
        this.objectName = newName;
    }

    @Override
    public ObjectInstance copy() {
        return new SonarRangeModule(this);
    }

    @Override
    public int getIntValForAttribute(String attx) {
        return 0;
    }

    @Override
    public ObjectInstance setValue(String attName, int v) {
        return null;
    }

    public List<RangeSensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<RangeSensor> sensors) {
        this.sensors = new ArrayList<>(sensors);
    }

    public void addNewSensor(RangeSensor sensor) {
        if (sensors.contains(sensor)) {
            throw new RuntimeException("RangeSensor already found : " + sensor);
        }
        if (this.sensors == null) {
            this.sensors = new ArrayList<>();
        }
        this.sensors.add(sensor);
    }

    public void sense(State s, int[][] map) {

        ObjectInstance robot = s.getFirstObjectOfClass(GridWorldDomain.CLASS_ROBOT);
        int rx = robot.getIntValForAttribute(GridWorldDomain.ATTX);
        int ry = robot.getIntValForAttribute(GridWorldDomain.ATTY);

        // Get Actual measurement of Robot
        double[] z = getActualMeasurement(s, rx, ry, map);
        System.out.println("Robot Measures : " + Arrays.toString(z));

        // Robot Belief
        GridRobotBelief robotBelief = (GridRobotBelief) s.getFirstObjectOfClass(GridWorldDomain.CLASS_BELIEF);
        double[][] belief = robotBelief.getBeliefMap();
        double[][] sensorBelief = new double[belief.length][belief[0].length];

        double total = 0;
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[0].length; c++) {
                double[] cellMeasurements = getActualMeasurement(s, r, c, map);
                //double dist = 10 - RoboMathUtils.euclideanDistance(cellMeasurements, z);
                double dist = RoboMathUtils.euclideanDistance(cellMeasurements, z);
                sensorBelief[r][c] = dist;
                total += sensorBelief[r][c];
            }
        }

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[0].length; c++) {
                sensorBelief[r][c] /= total;
            }
        }

        // Workaround
        getActualMeasurement(s, rx, ry, map);

        robotBelief.setBeliefMap(sensorBelief);

    }

    private double[] getActualMeasurement(State s, int x, int y, int[][] map) {
        double[] z = new double[sensors.size()];
        for (int i = 0; i < z.length; i++) {
            RangeSensor rs = sensors.get(i);
            rs.sense(s, x, y, map);

            z[i] = rs.getMeasurement();
        }

        return z;
    }

    @Override
    public void actionEvent(State s, State sp, String actionName) {
        System.out.println("ActionEvent : " + actionName);
        if (!actionName.equals(GridWorldDomain.ACTION_SENSE)) {
            // resetMeasurements();
        }
    }

    private void resetMeasurements() {
        for (RangeSensor rangeSensor : sensors) {
            rangeSensor.setMeasurement(0);
        }
    }

}
