package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

public class EditCommentRequestEvent {

    private Comment comment;

    public EditCommentRequestEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
