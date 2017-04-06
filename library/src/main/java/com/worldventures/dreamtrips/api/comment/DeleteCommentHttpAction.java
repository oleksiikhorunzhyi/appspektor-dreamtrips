package com.worldventures.dreamtrips.api.comment;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;


@HttpAction(value = "/api/social/comments/{uid}", method = DELETE)
public class DeleteCommentHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String commentId;

    public DeleteCommentHttpAction(String commentId) {
        this.commentId = commentId;
    }

}
