package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

public class CreateCommentCommand extends Command<Comment> {

    private long objectId;
    private String text;

    public CreateCommentCommand(long objectId, String text) {
        super(Comment.class);
        this.objectId = objectId;
        this.text = text;
    }

    @Override
    public Comment loadDataFromNetwork() throws Exception {
        return getService().createComment(objectId, text);
    }
}
