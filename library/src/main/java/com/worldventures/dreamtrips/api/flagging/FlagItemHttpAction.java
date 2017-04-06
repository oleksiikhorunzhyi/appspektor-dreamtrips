package com.worldventures.dreamtrips.api.flagging;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/{uid}/flags", method = POST)
public class FlagItemHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    @Body
    public final ActionBody body;

    public FlagItemHttpAction(String uid, int flagReasonId, String reason) {
        this.uid = uid;
        this.body = new ActionBody(flagReasonId, reason);
    }

    private static class ActionBody {
        @SerializedName("flag_reason_id")
        public final int reasonId;

        @SerializedName("reason")
        public final String reason;

        private ActionBody(int reasonId, String reason) {
            this.reasonId = reasonId;
            this.reason = reason;
        }
    }
}
