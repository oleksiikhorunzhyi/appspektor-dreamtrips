package com.worldventures.dreamtrips.modules.dtl.event;

public class FilterAttributesSelectAllEvent {

    private final boolean checked;

    public FilterAttributesSelectAllEvent(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
