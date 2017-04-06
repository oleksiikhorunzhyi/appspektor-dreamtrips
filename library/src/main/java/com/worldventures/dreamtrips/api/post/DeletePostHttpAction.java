package com.worldventures.dreamtrips.api.post;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

@HttpAction(value = "/api/social/posts/{post_id}", method = HttpAction.Method.DELETE)
public class DeletePostHttpAction extends AuthorizedHttpAction {

    @Path("post_id")
    String postId;

    public DeletePostHttpAction(String postId) {
        this.postId = postId;
    }

}
