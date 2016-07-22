package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/social/feed", method = HttpAction.Method.GET)
public class GeAccountFeedsQueryHttpAction extends AuthorizedHttpAction {

    @Query("circle_id") String circleId;
    @Query("per_page") int perPage;
    @Query("before") String before;

    @Response ArrayList<ParentFeedItem> responseItems;

    public GeAccountFeedsQueryHttpAction(String circleId, int perPage, String before) {
        this.circleId = circleId;
        this.perPage = perPage;
        this.before = before;
    }

    public ArrayList<ParentFeedItem> getResponseItems() {
        if (responseItems == null) responseItems = new ArrayList<>();
        return responseItems;
    }
}
