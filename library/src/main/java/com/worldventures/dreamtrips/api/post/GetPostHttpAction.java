package com.worldventures.dreamtrips.api.post;

import com.worldventures.dreamtrips.api.entity.GetEntityHttpAction;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction("/api/{uid}")
public class GetPostHttpAction extends GetEntityHttpAction<PostSocialized> {

    public GetPostHttpAction(String uid) {
        super(uid);
    }
}
