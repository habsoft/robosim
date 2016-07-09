package pk.com.habsoft.robosim.filters.core.objects;

import java.util.Arrays;

import pk.com.habsoft.robosim.filters.core.ObjectClass;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;

public class GridRobotBelief implements ObjectInstance {

	ObjectClass objectClass;
	String objectName;

	double[][][] beliefMap;

	public GridRobotBelief(ObjectClass objectClass, String objectName) {
		super();
		this.objectClass = objectClass;
		this.objectName = objectName;
	}

	public GridRobotBelief(GridRobotBelief ob) {
		super();
		this.objectClass = ob.objectClass;
		this.objectName = ob.objectName;
		this.beliefMap = ob.getBeliefMap().clone();

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
		return new GridRobotBelief(this);
	}

	public double[][][] getBeliefMap() {
		return beliefMap;
	}

	public void setBeliefMap(double[][][] beliefMap) {
		this.beliefMap = beliefMap;
	}

	@Override
	public String toString() {
		return "GridRobotBelief [objectClass=" + objectClass + ", objectName=" + objectName + ", beliefMap=" + Arrays.toString(beliefMap) + "]";
	}

	@Override
	public int getIntValForAttribute(String attx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ObjectInstance setValue(String attName, int v) {
		// TODO Auto-generated method stub
		return null;
	}

}
