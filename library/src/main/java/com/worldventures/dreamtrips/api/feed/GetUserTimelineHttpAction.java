package com.worldventures.dreamtrips.api.feed;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.feed.model.FeedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("/api/social/users/{user_id}/timeline")
public class GetUserTimelineHttpAction extends GetFeedHttpAction {

    @Path("user_id")
    public final int userId;

    public GetUserTimelineHttpAction(Params params) {
        super(params);
        userId = params.userId();
    }

    @Gson.TypeAdapters
    @Value.Immutable
    public interface Params extends FeedParams {

        @Value.Parameter
        @SerializedName("user_id")
        int userId();

        @Value.Parameter
        @SerializedName("page_size")
        @Override
        int pageSize();
    }
}
