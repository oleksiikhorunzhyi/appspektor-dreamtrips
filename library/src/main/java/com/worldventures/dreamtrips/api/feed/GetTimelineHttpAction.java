package com.worldventures.dreamtrips.api.feed;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.feed.model.FeedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import io.techery.janet.http.annotations.HttpAction;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("/api/social/timeline")
public class GetTimelineHttpAction extends GetFeedHttpAction {

    public GetTimelineHttpAction(Params params) {
        super(params);
    }

    @Gson.TypeAdapters
    @Value.Immutable
    public interface Params extends FeedParams {
        @Value.Parameter
        @SerializedName("page_size")
        @Override
        int pageSize();
    }
}
