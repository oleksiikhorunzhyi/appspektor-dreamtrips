package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/users/{user_id}/bucket_list_items", method = HttpAction.Method.GET)
public class LoadBucketListFullHttpAction extends AuthorizedHttpAction {
    @Path("user_id")
    Integer userId;

    @Response
    List<BucketItem> response;

    public LoadBucketListFullHttpAction(int userId) {
        this.userId = userId;
    }

    public List<BucketItem> getResponse() {
        return this.response;
    }
}