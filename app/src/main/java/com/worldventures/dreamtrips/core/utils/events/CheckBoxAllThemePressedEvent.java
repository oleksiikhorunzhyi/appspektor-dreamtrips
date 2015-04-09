package com.worldventures.dreamtrips.core.utils.events;

public class CheckBoxAllThemePressedEvent {
    private boolean checked;

    public CheckBoxAllThemePressedEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}