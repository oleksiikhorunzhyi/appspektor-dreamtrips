package com.worldventures.dreamtrips.api.friends;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/social/friends/request_responses", method = DELETE)
public class HideFriendRequestHttpAction extends AuthorizedHttpAction {

    @Query("user_id")
    public final int userId;

    public HideFriendRequestHttpAction(int userId) {
        this.userId = userId;
    }
}
