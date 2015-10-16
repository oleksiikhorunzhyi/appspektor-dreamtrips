package com.worldventures.dreamtrips.core.utils.events;

public class ActionBarHideEvent {

    private boolean hidden;

    public ActionBarHideEvent(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }
}
