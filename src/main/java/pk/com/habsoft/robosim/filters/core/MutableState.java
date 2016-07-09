package pk.com.habsoft.robosim.filters.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * State objects are a collection of Object Instances.
 * 
 * @author James MacGlashan
 *
 */
public class MutableState implements State {

	/**
	 * List of object instances that define the state
	 */
	protected List<ObjectInstance> objectInstances;

	/**
	 * Map from object names to their instances
	 */
	protected Map<String, ObjectInstance> objectMap;

	/**
	 * Map of object instances organized by class name
	 */
	protected Map<String, List<ObjectInstance>> objectIndexByClass;

	public MutableState() {
		super();
		this.initDataStructures();
	}

	/**
	 * Initializes this state as a deep copy of the object instances in the
	 * provided source state s
	 * 
	 * @param s
	 *            the source state from which this state will be initialized.
	 */
	public MutableState(MutableState s) {
		super();

		this.initDataStructures();

		for (ObjectInstance o : s.objectInstances) {
			this.addObject(o.copy());
		}

	}

	/**
	 * Returns a deep copy of this state.
	 * 
	 * @return a deep copy of this state.
	 */
	@Override
	public MutableState copy() {
		return new MutableState(this);
	}

	/**
	 * Performs a semi-deep copy of the state in which only the objects with the
	 * names in deepCopyObjectNames are deep copied and the rest of the objects
	 * are shallowed copied.
	 * 
	 * @param deepCopyObjectNames
	 *            the names of the objects to be deep copied.
	 * @return a new state that is a mix of a shallow and deep copy of this
	 *         state.
	 */
	public MutableState semiDeepCopy(String... deepCopyObjectNames) {
		Set<ObjectInstance> deepCopyObjectSet = new HashSet<ObjectInstance>(deepCopyObjectNames.length);
		for (String n : deepCopyObjectNames) {
			deepCopyObjectSet.add(this.getObject(n));
		}
		return this.semiDeepCopy(deepCopyObjectSet);
	}

	/**
	 * Performs a semi-deep copy of the state in which only the objects in
	 * deepCopyObjects are deep copied and the rest of the objects are shallowed
	 * copied.
	 * 
	 * @param deepCopyObjects
	 *            the objects to be deep copied
	 * @return a new state that is a mix of a shallow and deep copy of this
	 *         state.
	 */
	public MutableState semiDeepCopy(ObjectInstance... deepCopyObjects) {

		Set<ObjectInstance> deepCopyObjectSet = new HashSet<ObjectInstance>(deepCopyObjects.length);
		for (ObjectInstance d : deepCopyObjects) {
			deepCopyObjectSet.add(d);
		}

		return this.semiDeepCopy(deepCopyObjectSet);
	}

	/**
	 * Performs a semi-deep copy of the state in which only the objects in
	 * deepCopyObjects are deep copied and the rest of the objects are shallowed
	 * copied.
	 * 
	 * @param deepCopyObjects
	 *            the objects to be deep copied
	 * @return a new state that is a mix of a shallow and deep copy of this
	 *         state.
	 */
	public MutableState semiDeepCopy(Set<ObjectInstance> deepCopyObjects) {

		MutableState s = new MutableState();
		for (ObjectInstance o : this.objectInstances) {
			if (deepCopyObjects.contains(o)) {
				s.addObject(o.copy());
			} else {
				s.addObject(o);
			}
		}

		return s;
	}

	protected void initDataStructures() {

		objectInstances = new ArrayList<ObjectInstance>();
		objectMap = new HashMap<String, ObjectInstance>();
		objectIndexByClass = new HashMap<String, List<ObjectInstance>>();
	}

	/**
	 * Adds object instance o to this state.
	 * 
	 * @param o
	 *            the object instance to be added to this state.
	 */
	public State addObject(ObjectInstance o) {

		String oname = o.getName();

		if (objectMap.containsKey(oname)) {
			return this; // don't add an object that conflicts with another
							// object of the same name
		}

		objectMap.put(oname, o);
		objectInstances.add(o);

		this.addObjectClassIndexing(o);

		return this;
	}

	public State addAllObjects(Collection<ObjectInstance> objects) {
		for (ObjectInstance object : objects) {
			this.addObject(object);
		}
		return this;
	}

	private void addObjectClassIndexing(ObjectInstance o) {

		String otclass = o.getClassName();

		// manage true indexing
		if (objectIndexByClass.containsKey(otclass)) {
			objectIndexByClass.get(otclass).add(o);
		} else {

			ArrayList<ObjectInstance> classList = new ArrayList<ObjectInstance>();
			classList.add(o);
			objectIndexByClass.put(otclass, classList);

		}

	}

	/**
	 * Removes the object instance with the name oname from this state.
	 * 
	 * @param oname
	 *            the name of the object instance to remove.
	 */
	public State removeObject(String oname) {
		this.removeObject(objectMap.get(oname));
		return this;
	}

	/**
	 * Removes the object instance o from this state.
	 * 
	 * @param o
	 *            the object instance to remove from this state.
	 */
	public State removeObject(ObjectInstance o) {
		if (o == null) {
			return this;
		}

		String oname = o.getName();

		if (!objectMap.containsKey(oname)) {
			return this; // make sure we're removing something that actually
							// exists in this state!
		}

		objectInstances.remove(o);

		objectMap.remove(oname);

		this.removeObjectClassIndexing(o);
		return this;
	}

	public State removeAllObjects(Collection<ObjectInstance> objects) {
		for (ObjectInstance object : objects) {
			this.removeObject(object);
		}
		return this;
	}

	private void removeObjectClassIndexing(ObjectInstance o) {

		String otclass = o.getClassName();
		List<ObjectInstance> classTList = objectIndexByClass.get(otclass);

		// if this index has more than one entry, then we can just remove from
		// it and be done
		if (classTList.size() > 1) {
			classTList.remove(o);
		} else {
			// otherwise we have to remove class entries for it
			objectIndexByClass.remove(otclass);
		}

	}

	/**
	 * Renames the identifier for object instance o in this state to newName.
	 * 
	 * @param o
	 *            the object instance to rename in this state
	 * @param newName
	 *            the new name of the object instance
	 */
	public State renameObject(ObjectInstance o, String newName) {
		String originalName = o.getName();
		o.setName(newName);
		objectMap.remove(originalName);
		objectMap.put(newName, o);
		return this;
	}

	/**
	 * Returns the number of object instances in this state.
	 * 
	 * @return the number of object instances in this state.
	 */
	public int numTotalObjects() {
		return objectInstances.size();
	}

	/**
	 * Returns the object in this state with the name oname
	 * 
	 * @param oname
	 *            the name of the object instance to return
	 * @return the object instance with the name oname or null if there is no
	 *         object in this state named oname
	 */
	public ObjectInstance getObject(String oname) {
		return objectMap.get(oname);
	}

	/**
	 * Returns the list of observable and hidden object instances in this state.
	 * 
	 * @return the list of observable and hidden object instances in this state.
	 */
	public List<ObjectInstance> getAllObjects() {
		List<ObjectInstance> objects = new ArrayList<ObjectInstance>(objectInstances);
		return objects;
	}

	/**
	 * Returns all objects that belong to the object class named oclass
	 * 
	 * @param oclass
	 *            the name of the object class for which objects should be
	 *            returned
	 * @return all objects that belong to the object class named oclass
	 */
	public List<ObjectInstance> getObjectsOfClass(String oclass) {
		List<ObjectInstance> tmp = objectIndexByClass.get(oclass);
		if (tmp == null) {
			return new ArrayList<ObjectInstance>();
		}
		return new ArrayList<ObjectInstance>(tmp);
	}

	/**
	 * Returns the first indexed object of the object class named oclass
	 * 
	 * @param oclass
	 *            the name of the object class for which the first indexed
	 *            object should be returned.
	 * @return the first indexed object of the object class named oclass
	 */
	public ObjectInstance getFirstObjectOfClass(String oclass) {
		List<ObjectInstance> obs = this.objectIndexByClass.get(oclass);
		if (obs != null && !obs.isEmpty()) {
			return obs.get(0);
		}
		return null;
	}

	/**
	 * Returns a set of of the object class names for all object classes that
	 * have instantiated objects in this state.
	 * 
	 * @return a set of of the object class names for all object classes that
	 *         have instantiated objects in this state.
	 */
	public Set<String> getObjectClassesPresent() {
		return new HashSet<String>(objectIndexByClass.keySet());
	}

	/**
	 * Returns a list of list of object instances, grouped by object class
	 * 
	 * @return a list of list of object instances, grouped by object class
	 */
	public List<List<ObjectInstance>> getAllObjectsByClass() {
		return new ArrayList<List<ObjectInstance>>(objectIndexByClass.values());
	}

	/**
	 * Returns a string representation of this state using observable and hidden
	 * object instances.
	 * 
	 * @return a string representation of this state using observable and hidden
	 *         object instances.
	 */
	public String getCompleteStateDescription() {

		String desc = "";
		for (ObjectInstance o : objectInstances) {
			desc = desc + o.getObjectDescription() + "\n";
		}

		return desc;

	}

	@Override
	public String toString() {
		return this.getCompleteStateDescription();
	}

	@Override
	public State setObjectsValue(String objectName, String attrib, int val) {
		ObjectInstance obj = this.objectMap.get(objectName);
		if (obj == null) {
			throw new RuntimeException("Object " + objectName + " does not exist in this state");
		}
		obj.setValue(attrib, val);
		return this;
	}

}
