package com.worldventures.dreamtrips.api.post;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/social/posts/{post_id}", method = HttpAction.Method.PUT)
public class UpdatePostHttpAction extends AuthorizedHttpAction {

    @Path("post_id")
    public final String postId;

    @Body
    public final PostData data;

    @Response
    PostSocialized updatedPost;

    public UpdatePostHttpAction(String postId, PostData data) {
        this.postId = postId;
        this.data = data;
    }

    public PostSocialized response() {
        return updatedPost;
    }
}
