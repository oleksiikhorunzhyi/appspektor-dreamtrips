package com.worldventures.dreamtrips.api.inspirations;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.inspirations.model.InspireMePhoto;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/inspirations")
public class GetInspireMePhotosHttpAction extends PaginatedHttpAction {

    @Query("random_seed")
    double randomSeed;

    @Response
    List<InspireMePhoto> photos;

    public GetInspireMePhotosHttpAction(double randomSeed, int page, int perPage) {
        super(page, perPage);
        this.randomSeed = randomSeed;
    }

    public List<InspireMePhoto> response() {
        return photos;
    }
}
