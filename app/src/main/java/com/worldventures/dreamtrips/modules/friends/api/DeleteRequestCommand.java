package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class DeleteRequestCommand extends Command<JSONObject> {
    private int userId;
    private Action action;

    public DeleteRequestCommand(int userId, Action action) {
        super(JSONObject.class);
        this.userId = userId;
        this.action = action;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().deleteRequest(userId);
    }

    @Override
    public int getErrorMessage() {
        return action == Action.HIDE ?
                R.string.error_fail_to_hide_friend_request :
                R.string.error_fail_to_cancel_friend_request;
    }

    public enum Action {
        HIDE, CANCEL;
    }
}