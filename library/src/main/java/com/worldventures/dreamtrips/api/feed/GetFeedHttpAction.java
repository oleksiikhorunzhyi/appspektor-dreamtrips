package com.worldventures.dreamtrips.api.feed;

import com.worldventures.dreamtrips.api.api_common.PaginatedFeedHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.feed.model.FeedItemWrapper;
import com.worldventures.dreamtrips.api.feed.model.FeedParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.techery.janet.http.annotations.Response;

public abstract class GetFeedHttpAction extends PaginatedFeedHttpAction {

    @Response
    List<FeedItemWrapper> wrappedItems;

    public GetFeedHttpAction(FeedParams params) {
        super(params);
    }

    public List<FeedItem> response() {
        if (wrappedItems == null) return null;
        else if (wrappedItems.isEmpty()) return Collections.emptyList();
        //
        List<FeedItem> feedItems = new ArrayList<FeedItem>(wrappedItems.size());
        for (FeedItemWrapper wrappedItem : wrappedItems) {
            feedItems.addAll(wrappedItem.items());
        }
        return feedItems;
    }
}
