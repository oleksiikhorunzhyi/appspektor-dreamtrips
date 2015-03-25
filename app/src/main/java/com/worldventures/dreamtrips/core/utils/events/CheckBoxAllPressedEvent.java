package com.worldventures.dreamtrips.core.utils.events;


public class CheckBoxAllPressedEvent {
    private boolean isChecked;

    public CheckBoxAllPressedEvent(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
