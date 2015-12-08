package com.worldventures.dreamtrips.modules.dtl.event;

public class DtlSearchPlaceRequestEvent {

    String searchQuery;

    public DtlSearchPlaceRequestEvent(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}
