package com.worldventures.dreamtrips.modules.dtl.event;

public class DtlSearchMerchantRequestEvent {

    String searchQuery;

    public DtlSearchMerchantRequestEvent(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}
