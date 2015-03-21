package com.worldventures.dreamtrips.core.utils.events;

public class CheckBoxAllThemePressedEvent {
    private boolean isChecked;

    public CheckBoxAllThemePressedEvent(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}