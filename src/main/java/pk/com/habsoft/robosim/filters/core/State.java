package pk.com.habsoft.robosim.filters.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface State {

	/**
	 * Returns a copy of this state, if mutable the copy should be deep.
	 * 
	 * @return a copy of this state.
	 */
	State copy();

	/**
	 * Adds object instance o to this state.
	 * 
	 * @param o
	 *            the object instance to be added to this state.
	 * @return the modified state
	 */
	State addObject(ObjectInstance o);

	/**
	 * Adds the collection of objects to the state
	 * 
	 * @param objects
	 * @return the modified state
	 */
	State addAllObjects(Collection<ObjectInstance> objects);

	/**
	 * Removes the object instance with the name oname from this state.
	 * 
	 * @param oname
	 *            the name of the object instance to remove.
	 * @return the modified state
	 */
	State removeObject(String oname);

	/**
	 * Removes the object instance o from this state.
	 * 
	 * @param o
	 *            the object instance to remove from this state.
	 * @return the modified state
	 */
	State removeObject(ObjectInstance o);

	/**
	 * Removes the collection of objects from the state
	 * 
	 * @param objects
	 * @return the modified state
	 */
	State removeAllObjects(Collection<ObjectInstance> objects);

	/**
	 * Renames the identifier for object instance o in this state to newName.
	 * 
	 * @param o
	 *            the object instance to rename in this state
	 * @param newName
	 *            the new name of the object instance
	 * @return the modified state
	 */
	State renameObject(ObjectInstance o, String newName);

	/**
	 * Returns the number of object instances in this state.
	 * 
	 * @return the number of object instances in this state.
	 */
	int numTotalObjects();

	/**
	 * Returns the object in this state with the name oname
	 * 
	 * @param oname
	 *            the name of the object instance to return
	 * @return the object instance with the name oname or null if there is no
	 *         object in this state named oname
	 */
	ObjectInstance getObject(String oname);

	/**
	 * Returns the list of observable and hidden object instances in this state.
	 * 
	 * @return the list of observable and hidden object instances in this state.
	 */
	List<ObjectInstance> getAllObjects();

	/**
	 * Returns all objects that belong to the object class named oclass
	 * 
	 * @param oclass
	 *            the name of the object class for which objects should be
	 *            returned
	 * @return all objects that belong to the object class named oclass
	 */
	List<ObjectInstance> getObjectsOfClass(String oclass);

	/**
	 * Returns the first indexed object of the object class named oclass
	 * 
	 * @param oclass
	 *            the name of the object class for which the first indexed
	 *            object should be returned.
	 * @return the first indexed object of the object class named oclass
	 */
	ObjectInstance getFirstObjectOfClass(String oclass);

	/**
	 * Returns a set of of the object class names for all object classes that
	 * have instantiated objects in this state.
	 * 
	 * @return a set of of the object class names for all object classes that
	 *         have instantiated objects in this state.
	 */
	Set<String> getObjectClassesPresent();

	/**
	 * Returns a list of list of object instances, grouped by object class
	 * 
	 * @return a list of list of object instances, grouped by object class
	 */
	List<List<ObjectInstance>> getAllObjectsByClass();

	/**
	 * Returns a string representation of this state using observable and hidden
	 * object instances.
	 * 
	 * @return a string representation of this state using observable and hidden
	 *         object instances.
	 */
	String getCompleteStateDescription();

	State setObjectsValue(String name, String attrib, int val);

}
