package com.worldventures.dreamtrips.core.utils.events;


public class CheckBoxAllRegionsPressedEvent {
    private boolean checked;

    public CheckBoxAllRegionsPressedEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean isChecked) {
        this.checked = isChecked;
    }
}
