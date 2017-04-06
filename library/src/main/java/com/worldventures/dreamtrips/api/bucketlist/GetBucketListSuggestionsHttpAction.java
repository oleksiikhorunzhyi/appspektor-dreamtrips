package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListSuggestion;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/{type}_suggestions")
public class GetBucketListSuggestionsHttpAction extends AuthorizedHttpAction {

    @Path("type")
    public final String type;

    @Query("name")
    public final String nameQuery;

    @Response
    List<BucketListSuggestion> activities;

    public GetBucketListSuggestionsHttpAction(BucketType type, String nameQuery) {
        this.type = type.toString().toLowerCase();
        this.nameQuery = nameQuery;
    }

    public List<BucketListSuggestion> response() {
        return activities;
    }
}
