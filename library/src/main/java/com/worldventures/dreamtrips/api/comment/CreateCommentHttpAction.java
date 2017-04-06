package com.worldventures.dreamtrips.api.comment;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.comment.model.Comment;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/social/comments", method = POST)
public class CreateCommentHttpAction extends AuthorizedHttpAction {

    @Body
    public final ActionBody body;

    @Response
    Comment createdComment;

    public CreateCommentHttpAction(String originId, String text) {
        this.body = new ActionBody(originId, text);
    }

    public Comment response() {
        return createdComment;
    }

    private static class ActionBody {
        @SerializedName("origin_id")
        public final String originId;

        @SerializedName("text")
        public final String text;

        private ActionBody(String originId, String text) {
            this.originId = originId;
            this.text = text;
        }
    }
}
