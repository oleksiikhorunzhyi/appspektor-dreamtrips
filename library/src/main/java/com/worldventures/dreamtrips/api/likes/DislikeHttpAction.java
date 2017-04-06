package com.worldventures.dreamtrips.api.likes;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/{uid}/likes", method = DELETE)
public class DislikeHttpAction extends AuthorizedHttpAction {
    @Path("uid")
    public final String uid;

    public DislikeHttpAction(String uid) {
        this.uid = uid;
    }
}
