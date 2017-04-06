package com.worldventures.dreamtrips.api.friends;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/social/friends/requests", method = POST)
public class SendFriendRequestHttpAction extends AuthorizedHttpAction {

    @Body
    public final ActionBody body;

    public SendFriendRequestHttpAction(int userId, String circleId) {
        this.body = new ActionBody(userId, circleId);
    }

    static class ActionBody {
        @SerializedName("user_id")
        public final int userId;

        @SerializedName("circle_id")
        public final String circleId;

        public ActionBody(int userId, String circleId) {
            this.userId = userId;
            this.circleId = circleId;
        }
    }


}
