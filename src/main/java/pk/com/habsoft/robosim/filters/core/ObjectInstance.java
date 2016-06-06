package pk.com.habsoft.robosim.filters.core;

public interface ObjectInstance {

    public String getName();

    public String getClassName();

    public String getObjectDescription();

    public void setName(String newName);

    public ObjectInstance copy();

    public int getIntValForAttribute(String attx);

    ObjectInstance setValue(String attName, int v);

}
