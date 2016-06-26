package pk.com.habsoft.robosim.filters.core.objects;

import java.util.ArrayList;
import java.util.List;

import pk.com.habsoft.robosim.filters.core.Attribute;
import pk.com.habsoft.robosim.filters.core.ObjectClass;
import pk.com.habsoft.robosim.filters.core.ObjectInstance;
import pk.com.habsoft.robosim.filters.core.values.Value;

public class GridRobot implements ObjectInstance {

	ObjectClass obClass;
	String objectName;
	protected List<Value> values;

	public GridRobot(ObjectClass objectClass, String objectName) {
		super();
		this.obClass = objectClass;
		this.objectName = objectName;
		this.values = this.initializeValueObjects();
	}

	/**
	 * Creates a new object instance that is a deep copy of the specified object
	 * instance's values. The object class and name is a shallow copy.
	 * 
	 * @param o
	 *            the source object instance from which this will object will
	 *            copy.
	 */
	public GridRobot(GridRobot o) {

		this.obClass = o.obClass;
		this.objectName = o.objectName;

		this.values = new ArrayList<Value>(o.values);

	}

	/**
	 * Creates new value object assignments for each of this object instance
	 * class's attributes.
	 */
	public List<Value> initializeValueObjects() {

		List<Value> values = new ArrayList<Value>(obClass.numAttributes());
		for (Attribute att : obClass.attributeList) {
			values.add(att.valueConstructor());
		}
		return values;
	}

	@Override
	public String getName() {
		return objectName;
	}

	@Override
	public String getClassName() {
		return obClass.name;
	}

	@Override
	public String getObjectDescription() {
		return this.toString();
	}

	@Override
	public void setName(String newName) {
		objectName = newName;
	}

	@Override
	public ObjectInstance copy() {
		return new GridRobot(this);
	}

	/**
	 * Sets the value of the attribute named attName for this object instance.
	 * 
	 * @param attName
	 *            the name of the attribute whose value is to be set.
	 * @param v
	 *            the int rep value to which the attribute of this object
	 *            instance should be set.
	 */
	@Override
	public ObjectInstance setValue(String attName, int v) {
		int ind = obClass.attributeIndex(attName);
		Value value = values.get(ind);
		Value newValue = value.setValue(v);
		values.set(ind, newValue);
		return this;
	}

	/**
	 * Returns the int value assignment for the discrete-valued attribute named
	 * attName. Will throw a runtime exception is the attribute named attName is
	 * not of type DISC
	 * 
	 * @param attName
	 *            the name of the attribute whose value should be returned
	 * @return the int value assignment for the discrete-valued attribute named
	 *         attName.
	 */
	public int getIntValForAttribute(String attName) {
		int ind = obClass.attributeIndex(attName);
		return values.get(ind).getDiscVal();
	}

}
