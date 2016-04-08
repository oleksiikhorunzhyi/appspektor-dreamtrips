package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;

import org.json.JSONObject;

public class SendInvitationsQuery extends Query<JSONObject> {

    private InviteBody body;

    public SendInvitationsQuery(InviteBody body) {
        super(JSONObject.class);
        this.body = body;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().sendInvitations(body);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_send_invitation;
    }
}
