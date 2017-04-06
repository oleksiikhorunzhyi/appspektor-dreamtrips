package com.worldventures.dreamtrips.api.post;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.PostSimple;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/social/posts", method = HttpAction.Method.POST)
public class CreatePostHttpAction extends AuthorizedHttpAction {

    @Body
    public final PostData data;

    @Response
    PostSimple post;

    public CreatePostHttpAction(PostData data) {
        this.data = data;
    }

    public PostSimple response() {
        return post;
    }
}
