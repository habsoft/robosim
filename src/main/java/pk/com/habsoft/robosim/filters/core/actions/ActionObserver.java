package pk.com.habsoft.robosim.filters.core.actions;

import pk.com.habsoft.robosim.filters.core.State;

public interface ActionObserver {

	/**
	 * 
	 * @param oldState
	 * @param newState
	 * @param actionName TODO
	 */
	public void actionEvent(State oldState, State newState, String actionName);
}
