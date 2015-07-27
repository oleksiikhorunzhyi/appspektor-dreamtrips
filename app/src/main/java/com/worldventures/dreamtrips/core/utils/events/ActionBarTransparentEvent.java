package com.worldventures.dreamtrips.core.utils.events;

public class ActionBarTransparentEvent {

    private boolean isTransparent;

    public ActionBarTransparentEvent(boolean transparent) {
        this.isTransparent = transparent;
    }

    public boolean isTransparent() {
        return isTransparent;
    }
}
