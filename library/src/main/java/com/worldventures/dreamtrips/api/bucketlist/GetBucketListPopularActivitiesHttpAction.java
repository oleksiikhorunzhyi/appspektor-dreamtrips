package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListActivity;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/activity_suggestions/popular")
public class GetBucketListPopularActivitiesHttpAction extends AuthorizedHttpAction {

    @Query("name")
    public final String nameQuery;

    @Response
    List<BucketListActivity> activities;

    public GetBucketListPopularActivitiesHttpAction(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public List<BucketListActivity> response() {
        return activities;
    }
}
