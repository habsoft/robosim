package pk.com.habsoft.robosim.filters.core;

public class KeyActionBinding {

	String key;
	String actionName;

	public KeyActionBinding(String key, String actionName) {
		super();
		this.key = key;
		this.actionName = actionName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

}
