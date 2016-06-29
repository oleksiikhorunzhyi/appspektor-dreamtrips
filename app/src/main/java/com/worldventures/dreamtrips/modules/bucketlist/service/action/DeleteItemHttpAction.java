package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items/{uid}", method = HttpAction.Method.DELETE)
public class DeleteItemHttpAction extends AuthorizedHttpAction {
    @Path("uid")
    String uid;

    @Response
    JsonObject response;

    public DeleteItemHttpAction(String uid) {
        this.uid = uid;
    }

    public JsonObject getResponse() {
        return this.response;
    }

    public String getBucketItemUid() {
        return uid;
    }
}