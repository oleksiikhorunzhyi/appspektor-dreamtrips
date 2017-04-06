package com.worldventures.dreamtrips.api.friends;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/social/friends/{user_id}", method = DELETE)
public class RemoveFromFriendsHttpAction extends AuthorizedHttpAction {

    @Path("user_id")
    public final int userId;

    public RemoveFromFriendsHttpAction(int userId) {
        this.userId = userId;
    }
}
