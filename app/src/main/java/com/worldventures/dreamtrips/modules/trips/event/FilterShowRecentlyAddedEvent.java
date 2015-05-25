package com.worldventures.dreamtrips.modules.trips.event;

public class FilterShowRecentlyAddedEvent {
    boolean showRecentlyAdded;

    public FilterShowRecentlyAddedEvent(boolean showFavorites) {
        this.showRecentlyAdded = showFavorites;
    }

    public boolean isShowRecentlyAdded() {
        return showRecentlyAdded;
    }
}
