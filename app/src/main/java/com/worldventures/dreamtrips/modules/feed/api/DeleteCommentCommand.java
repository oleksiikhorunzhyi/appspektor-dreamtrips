package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class DeleteCommentCommand extends Command<JSONObject> {

    private String id;

    public DeleteCommentCommand(String id) {
        super(JSONObject.class);
        this.id = id;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().deleteComment(id);
    }
}
