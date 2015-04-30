package com.worldventures.dreamtrips.modules.membership.api;

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
}
