package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import org.json.JSONObject;

public class NewPostCommand extends Command<TextualPost> {

    private String text;

    public NewPostCommand(String text) {
        super(TextualPost.class);
        this.text = text;
    }

    @Override
    public TextualPost loadDataFromNetwork() throws Exception {
        return getService().post(text);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_create_post;
    }
}
