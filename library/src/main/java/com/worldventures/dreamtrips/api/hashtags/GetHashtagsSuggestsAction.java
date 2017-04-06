package com.worldventures.dreamtrips.api.hashtags;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.hashtags.model.HashTagExtended;
import com.worldventures.dreamtrips.api.hashtags.model.HashtagsSuggestsParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("/api/hashtags/suggestions")
public class GetHashtagsSuggestsAction extends PaginatedHttpAction {

    @Query("query")
    public final String query;

    @Response
    protected List<HashTagExtended> suggests;

    public GetHashtagsSuggestsAction(HashtagsSuggestsParams params) {
        super(params);
        this.query = params.query();
    }

    public List<HashTagExtended> response() {
        return suggests;
    }
}
