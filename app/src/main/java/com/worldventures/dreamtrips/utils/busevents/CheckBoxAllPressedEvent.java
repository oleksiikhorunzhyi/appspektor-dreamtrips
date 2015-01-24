package com.worldventures.dreamtrips.utils.busevents;

/**
 * Created by 1 on 22.01.15.
 */
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
