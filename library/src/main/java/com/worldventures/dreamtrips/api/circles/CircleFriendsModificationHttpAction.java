package com.worldventures.dreamtrips.api.circles;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.Path;

public abstract class CircleFriendsModificationHttpAction extends AuthorizedHttpAction {

    @Path("circle_id")
    public final String circleId;

    @Body
    public final ActionBody body;

    public CircleFriendsModificationHttpAction(String circleId, List<Integer> userIds) {
        this.circleId = circleId;
        this.body = new ActionBody(userIds);
    }

    public static class ActionBody {
        @SerializedName("user_ids")
        public final List<Integer> userIds;

        private ActionBody(List<Integer> userIds) {
            this.userIds = userIds;
        }
    }
}
