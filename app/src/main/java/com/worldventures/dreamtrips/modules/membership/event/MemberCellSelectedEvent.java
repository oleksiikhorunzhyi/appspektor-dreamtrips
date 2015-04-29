package com.worldventures.dreamtrips.modules.membership.event;

public class MemberCellSelectedEvent {
    boolean selected;

    public MemberCellSelectedEvent(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
