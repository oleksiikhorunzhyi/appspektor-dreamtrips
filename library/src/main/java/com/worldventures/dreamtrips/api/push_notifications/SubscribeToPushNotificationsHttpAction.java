package com.worldventures.dreamtrips.api.push_notifications;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.push_notifications.model.PushSubscriptionParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/social/push_subscriptions", method = POST)
public class SubscribeToPushNotificationsHttpAction extends AuthorizedHttpAction {

    @Body
    public final PushSubscriptionParams params;

    public SubscribeToPushNotificationsHttpAction(PushSubscriptionParams params) {
        this.params = params;
    }
}
