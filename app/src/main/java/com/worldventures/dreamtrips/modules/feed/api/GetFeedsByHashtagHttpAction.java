package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    List<ParentFeedItem> responseItems;

    public List<ParentFeedItem> getResponseItems() {
        if (responseItems == null) responseItems = new ArrayList<>();
        return responseItems;
    }
}
