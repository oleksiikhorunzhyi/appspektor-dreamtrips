package com.worldventures.dreamtrips.api.comment;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.comment.model.Comment;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.PUT;

@HttpAction(value = "/api/social/comments/{uid}", method = PUT)
public class UpdateCommentHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String commentId;

    @Body
    public final ActionBody body;

    @Response
    Comment updatedComment;

    public UpdateCommentHttpAction(String commentId, String text) {
        this.commentId = commentId;
        this.body = new ActionBody(text);
    }

    public Comment response() {
        return updatedComment;
    }

    private static class ActionBody {
        @SerializedName("text")
        public final String text;

        private ActionBody(String text) {
            this.text = text;
        }
    }
}
