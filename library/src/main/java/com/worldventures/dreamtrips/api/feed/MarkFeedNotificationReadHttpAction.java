package com.worldventures.dreamtrips.api.feed;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.PUT;
import static io.techery.janet.http.annotations.HttpAction.Type.FORM_URL_ENCODED;

@HttpAction(value = "api/social/notifications/{id}", method = PUT, type = FORM_URL_ENCODED)
public class MarkFeedNotificationReadHttpAction extends AuthorizedHttpAction {

    @Path("id")
    public final int id;

    /**
     * Mark notification entity as read
     *
     * @param id of push notification. Note, this is not id of {@link FeedItem#entity()}
     */
    public MarkFeedNotificationReadHttpAction(int id) {
        this.id = id;
    }
}
