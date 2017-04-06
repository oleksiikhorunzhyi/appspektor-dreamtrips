package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListLocation;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/bucket_list/locations")
public class GetBucketListLocationsHttpAction extends AuthorizedHttpAction {

    @Response
    List<BucketListLocation> locations;

    public List<BucketListLocation> response() {
        return locations;
    }
}
