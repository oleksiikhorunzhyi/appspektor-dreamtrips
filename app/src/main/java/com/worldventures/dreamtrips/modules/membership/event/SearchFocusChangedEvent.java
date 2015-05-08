package com.worldventures.dreamtrips.modules.membership.event;

public class SearchFocusChangedEvent {

    private boolean focused;

    public SearchFocusChangedEvent(boolean focused) {
        this.focused = focused;
    }

    public boolean hasFocus() {
        return focused;
    }
}
