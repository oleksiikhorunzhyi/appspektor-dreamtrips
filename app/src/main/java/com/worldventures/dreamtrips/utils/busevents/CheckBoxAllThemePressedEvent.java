package com.worldventures.dreamtrips.utils.busevents;

/**
 * Created by 1 on 23.01.15.
 */
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