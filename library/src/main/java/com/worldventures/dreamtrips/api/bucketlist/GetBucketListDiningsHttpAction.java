package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListDining;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/bucket_list/dinings")
public class GetBucketListDiningsHttpAction extends AuthorizedHttpAction {

    @Response
    List<BucketListDining> dinings;

    public List<BucketListDining> response() {
        return dinings;
    }
}
