package pk.com.habsoft.robosim.filters.core;

public interface ObjectInstance {

	public String getName();

	public void setName(String newName);

	public String getClassName();

	public String getObjectDescription();

	public ObjectInstance copy();

	public ObjectInstance setValue(String attrib, int val);

	public int getIntValForAttribute(String attrib);

}
