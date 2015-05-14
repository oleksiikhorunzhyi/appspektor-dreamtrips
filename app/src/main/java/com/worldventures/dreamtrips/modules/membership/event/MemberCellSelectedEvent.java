package com.worldventures.dreamtrips.modules.membership.event;

public class MemberCellSelectedEvent {
    private boolean selected;

    public MemberCellSelectedEvent(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

}
