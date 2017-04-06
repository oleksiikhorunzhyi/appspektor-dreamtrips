package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/location_suggestions/popular")
public class GetBucketListPopularLocationsHttpAction extends AuthorizedHttpAction {

    @Query("name")
    public final String nameQuery;

    @Response
    List<BucketListLocation> locations;

    public GetBucketListPopularLocationsHttpAction(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public List<BucketListLocation> response() {
        return locations;
    }
}
