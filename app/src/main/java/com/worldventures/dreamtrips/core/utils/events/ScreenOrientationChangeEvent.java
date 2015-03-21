package com.worldventures.dreamtrips.core.utils.events;

public class ScreenOrientationChangeEvent {
    boolean isLandscape;

    public ScreenOrientationChangeEvent(boolean isLandscape) {
        this.isLandscape = isLandscape;
    }

    public boolean isLandscape() {
        return isLandscape;
    }
}
