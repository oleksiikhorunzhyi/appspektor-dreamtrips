package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class EditPostCommand extends Command<TextualPost> {

    private String uid;
    private String text;

    public EditPostCommand(String uid, String text) {
        super(TextualPost.class);
        this.uid = uid;
        this.text = text;
    }

    @Override
    public TextualPost loadDataFromNetwork() throws Exception {
        return getService().editPost(uid, text);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_update_post;
    }
}
