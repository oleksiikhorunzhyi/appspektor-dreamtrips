package com.worldventures.dreamtrips.api.feed;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.feed.model.FeedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("/api/social/feed")
public class GetAccountFeedHttpAction extends GetFeedHttpAction {

    @Query("circle_id")
    public final String circleId;

    public GetAccountFeedHttpAction(Params params) {
        super(params);
        circleId = params.circleId();
    }

    @Gson.TypeAdapters
    @Value.Immutable
    public interface Params extends FeedParams {
        @Nullable
        @SerializedName("circle_id")
        String circleId();
    }
}
