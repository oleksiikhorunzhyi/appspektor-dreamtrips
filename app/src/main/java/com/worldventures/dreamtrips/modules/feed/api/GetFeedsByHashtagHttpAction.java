package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.DataMetaData;

import java.util.Date;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/hashtags/search", method = HttpAction.Method.GET)
public class GetFeedsByHashtagHttpAction extends AuthorizedHttpAction{

    @Query("query")
    String query;
    @Query("per_page")
    int perPage;
    @Query("before")
    Date before;

    public GetFeedsByHashtagHttpAction(String query, int perPage, Date before) {
        this.query = query;
        this.perPage = perPage;
        this.before = before;
    }

    @Response
    DataMetaData responseItems;

    public DataMetaData getResponseItems() {
        if (responseItems == null) responseItems = new DataMetaData();
        return responseItems;
    }
}
