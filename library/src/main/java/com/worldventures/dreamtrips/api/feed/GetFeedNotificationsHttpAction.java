package com.worldventures.dreamtrips.api.feed;

import com.worldventures.dreamtrips.api.feed.model.FeedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import io.techery.janet.http.annotations.HttpAction;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("api/social/notifications")
public class GetFeedNotificationsHttpAction extends GetFeedHttpAction {

    public GetFeedNotificationsHttpAction(Params params) {
        super(params);
    }

    @Gson.TypeAdapters
    @Value.Immutable
    public interface Params extends FeedParams {}
}
