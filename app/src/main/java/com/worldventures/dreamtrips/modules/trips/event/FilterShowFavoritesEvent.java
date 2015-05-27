package com.worldventures.dreamtrips.modules.trips.event;

public class FilterShowFavoritesEvent {
    boolean showFavorites;

    public FilterShowFavoritesEvent(boolean showFavorites) {
        this.showFavorites = showFavorites;
    }

    public boolean isShowFavorites() {
        return showFavorites;
    }
}
