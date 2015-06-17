package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class AddUserRequestCommand extends Command<JSONObject> {
    private int userId;

    public AddUserRequestCommand(int userId) {
        super(JSONObject.class);
        this.userId = userId;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().addFriend(userId);
    }
}
