package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

public class DeletePostCommand extends Command<Void> {

    private String id;

    public DeletePostCommand(String id) {
        super(Void.class);
        this.id = id;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().deletePost(id);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_delete_post;
    }
}
