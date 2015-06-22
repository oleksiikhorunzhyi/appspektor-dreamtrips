package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class ActOnRequestCommand extends Command<JSONObject> {
    private int userId;
    private String action;
    private String circleID;

    public ActOnRequestCommand(int userId, String action) {
        super(JSONObject.class);
        this.userId = userId;
        this.action = action;
    }

    public ActOnRequestCommand(int userId, String action, String circleID) {
        super(JSONObject.class);
        this.userId = userId;
        this.action = action;
        this.circleID = circleID;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().actOnRequest(userId, action, circleID);
    }

    public enum Action {
        CONFIRM , REJECT
    }
}
