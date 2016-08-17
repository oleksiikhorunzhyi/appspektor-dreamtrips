package com.worldventures.dreamtrips.modules.feed.service.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

public abstract class GetFeedHttpAction extends AuthorizedHttpAction {

    @Query("per_page") int perPage;
    @Query("before") String before;

    @Response ArrayList<ParentFeedItem> responseItems;

    public GetFeedHttpAction(int perPage, String before) {
        this.perPage = perPage;
        this.before = before;
    }

    public ArrayList<ParentFeedItem> getResponseItems() {
        if (responseItems == null) responseItems = new ArrayList<>();
        return responseItems;
    }
}
