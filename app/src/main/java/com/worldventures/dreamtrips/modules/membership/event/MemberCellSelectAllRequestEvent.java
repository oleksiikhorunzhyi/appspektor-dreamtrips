package com.worldventures.dreamtrips.modules.membership.event;

public class MemberCellSelectAllRequestEvent {

    boolean selectAll;

    public MemberCellSelectAllRequestEvent(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public boolean isSelectAll() {
        return selectAll;
    }
}
