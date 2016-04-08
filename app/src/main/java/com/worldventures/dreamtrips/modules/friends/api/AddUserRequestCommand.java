package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import org.json.JSONObject;

public class AddUserRequestCommand extends Command<JSONObject> {
    private int userId;
    private Circle circle;

    public AddUserRequestCommand(int userId, Circle circle) {
        super(JSONObject.class);
        this.userId = userId;
        this.circle = circle;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().addFriend(userId, circle.getId());
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_send_friend_request;
    }
}
