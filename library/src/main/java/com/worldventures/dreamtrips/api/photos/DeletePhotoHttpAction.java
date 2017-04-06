package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/photos/{uid}", method = DELETE)
public class DeletePhotoHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    public DeletePhotoHttpAction(String uid) {
        this.uid = uid;
    }
}
