package com.worldventures.dreamtrips.modules.friends.events;

public class QueryStickyEvent {

    private String query;

    public QueryStickyEvent(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
