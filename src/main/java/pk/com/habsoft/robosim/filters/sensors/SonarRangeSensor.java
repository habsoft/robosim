package pk.com.habsoft.robosim.filters.sensors;

import pk.com.habsoft.robosim.filters.core.GridWorldDomain;
import pk.com.habsoft.robosim.filters.core.State;

public class SonarRangeSensor implements RangeSensor {

    String dirName;
    int maxRange;
    double noise;
    int[] dir;
    int measurement;

    public SonarRangeSensor(String dirName, int maxRange, double noise, int[] dir) {
        super();
        this.dirName = dirName;
        this.maxRange = maxRange;
        this.noise = noise;
        this.dir = dir;
    }

    @Override
    public void sense(State s, int cx, int cy, int[][] map) {
        measurement = 0;

        int nx = cx + dir[0];
        int ny = cy + dir[1];

        while (GridWorldDomain.isOpen(nx, ny, map) && measurement < getMaxRange()) {
            nx += dir[0];
            ny += dir[1];
            measurement++;
        }
        System.out.println(this);

    }

    @Override
    public int getMaxRange() {
        return maxRange;
    }

    @Override
    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    @Override
    public double getNoise() {
        return noise;
    }

    @Override
    public void setNoise(double noise) {
        this.noise = noise;
    }

    @Override
    public int[] getDir() {
        return dir;
    }

    public void setDir(int[] dir) {
        this.dir = dir;
    }

    @Override
    public int getMeasurement() {
        return measurement;
    }

    @Override
    public void setMeasurement(int measurement) {
        this.measurement = measurement;
    }

    @Override
    public String toString() {
        return "Reading [Dir=" + dirName + ", Dist=" + measurement + "]";
    }

}
