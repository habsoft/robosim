package pk.com.habsoft.robosim.filters.core.actions;

import pk.com.habsoft.robosim.filters.core.State;

public interface ActionObserver {
    public void actionEvent(State s, State sp);
}
