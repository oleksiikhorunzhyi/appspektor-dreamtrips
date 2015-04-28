package com.worldventures.dreamtrips.modules.membership.event;

public class SelectAllEvent {

    boolean selectAll;

    public SelectAllEvent(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public boolean isSelectAll() {
        return selectAll;
    }
}
