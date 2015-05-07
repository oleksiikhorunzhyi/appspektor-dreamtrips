package com.worldventures.dreamtrips.modules.membership.event;

public class MemberCellSelectedEvent {
    private boolean selected;
    private int from;
    private int to;

    public MemberCellSelectedEvent(boolean selected, int from, int to) {
        this.selected = selected;
        this.from = from;
        this.to = to;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
