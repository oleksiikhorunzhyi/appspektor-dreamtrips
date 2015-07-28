package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class NewPostCommand extends Command<JSONObject> {

    private String text;

    public NewPostCommand(String text) {
        super(JSONObject.class);
        this.text = text;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return getService().post(text);
    }
}
