package com.worldventures.dreamtrips.core.utils.events;


public class CheckBoxAllPressedEvent {
    private boolean checked;

    public CheckBoxAllPressedEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean isChecked) {
        this.checked = isChecked;
    }
}
