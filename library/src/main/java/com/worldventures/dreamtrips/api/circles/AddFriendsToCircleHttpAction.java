package com.worldventures.dreamtrips.api.circles;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/social/circles/{circle_id}/users", method = POST)
public class AddFriendsToCircleHttpAction extends CircleFriendsModificationHttpAction {

    public AddFriendsToCircleHttpAction(String circleId, List<Integer> userIds) {
        super(circleId, userIds);
    }
}
