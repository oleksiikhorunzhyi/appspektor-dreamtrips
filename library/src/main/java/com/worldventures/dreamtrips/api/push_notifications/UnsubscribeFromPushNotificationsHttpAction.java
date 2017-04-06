package com.worldventures.dreamtrips.api.push_notifications;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/social/push_subscriptions/{token}", method = DELETE)
public class UnsubscribeFromPushNotificationsHttpAction extends AuthorizedHttpAction {

    @Path("token")
    public final String token;

    public UnsubscribeFromPushNotificationsHttpAction(String token) {
        this.token = token;
    }
}
