package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

public class DeleteCommentRequestEvent {

    private Comment comment;

    public DeleteCommentRequestEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
