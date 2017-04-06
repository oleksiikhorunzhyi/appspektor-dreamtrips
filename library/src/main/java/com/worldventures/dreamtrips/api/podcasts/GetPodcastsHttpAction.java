package com.worldventures.dreamtrips.api.podcasts;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.podcasts.model.Podcast;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/podcasts")
public class GetPodcastsHttpAction extends PaginatedHttpAction {

    @Response
    List<Podcast> podcasts;

    public GetPodcastsHttpAction(int page, int perPage) {
        super(page, perPage);
    }

    public List<Podcast> response() {
        return podcasts;
    }
}
