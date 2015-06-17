package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class ActOnRequestCommand extends Command<JSONObject> {
    private int userId;
    private String action;

    public ActOnRequestCommand(int userId, String action) {
        super(JSONObject.class);
        this.userId = userId;
        this.action = action;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().actOnRequest(userId, action);
    }

    public enum Action {
        CONFIRM , REJECT , HIDE , CANCEL
    }
}
