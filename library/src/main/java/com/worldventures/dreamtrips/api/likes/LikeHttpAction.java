package com.worldventures.dreamtrips.api.likes;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.FORM_URL_ENCODED;

@HttpAction(value = "/api/{uid}/likes", method = POST, type = FORM_URL_ENCODED)
public class LikeHttpAction extends AuthorizedHttpAction {
    @Path("uid")
    public final String uid;

    public LikeHttpAction(String uid) {
        this.uid = uid;
    }
}
