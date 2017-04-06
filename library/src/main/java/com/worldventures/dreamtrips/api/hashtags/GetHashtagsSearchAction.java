package com.worldventures.dreamtrips.api.hashtags;

import com.worldventures.dreamtrips.api.api_common.PaginatedFeedHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.feed.model.FeedItemWrapper;
import com.worldventures.dreamtrips.api.hashtags.model.HashtagsSearchParams;
import com.worldventures.dreamtrips.api.hashtags.model.HashtagsSearchResponse;
import com.worldventures.dreamtrips.api.hashtags.model.Metadata;

import org.immutables.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@Gson.TypeAdapters
@HttpAction("/api/hashtags/search")
public class GetHashtagsSearchAction extends PaginatedFeedHttpAction {

    @Query("query")
    public final String query;

    @Query("type")
    public final String type;

    @Response
    protected HashtagsSearchResponse result;

    public GetHashtagsSearchAction(HashtagsSearchParams params) {
        super(params);
        this.query = params.query();
        this.type = params.type().toString();
    }

    public List<FeedItem> response() {
        List<FeedItemWrapper> feedItemWrappers = this.result.data();
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        for (FeedItemWrapper wrapper : feedItemWrappers) {
            feedItems.addAll(wrapper.items());
        }
        return feedItems;
    }

    public Metadata metadata() {
        return this.result.metadata();
    }
}
