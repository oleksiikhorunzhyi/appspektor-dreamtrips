package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCategory;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/categories")
public class GetBucketListCategoriesHttpAction extends AuthorizedHttpAction {
    @Response
    List<BucketCategory> response;

    public List<BucketCategory> response() {
        return response;
    }
}
