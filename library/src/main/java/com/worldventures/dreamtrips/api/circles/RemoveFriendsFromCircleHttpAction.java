package com.worldventures.dreamtrips.api.circles;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/social/circles/{circle_id}/users", method = DELETE)
public class RemoveFriendsFromCircleHttpAction extends CircleFriendsModificationHttpAction {

    public RemoveFriendsFromCircleHttpAction(String circleId, List<Integer> userIds) {
        super(circleId, userIds);
    }
}
