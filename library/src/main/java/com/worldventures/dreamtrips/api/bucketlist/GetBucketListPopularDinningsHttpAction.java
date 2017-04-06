package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListDining;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dining_suggestions/popular")
public class GetBucketListPopularDinningsHttpAction extends AuthorizedHttpAction {

    @Query("name")
    public final String nameQuery;

    @Response
    List<BucketListDining> dinings;

    public GetBucketListPopularDinningsHttpAction(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public List<BucketListDining> response() {
        return dinings;
    }
}
