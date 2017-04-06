package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListActivity;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/bucket_list/activities")
public class GetBucketListActivitiesHttpAction extends AuthorizedHttpAction {

    @Response
    List<BucketListActivity> activities;

    public List<BucketListActivity> response() {
        return activities;
    }
}
