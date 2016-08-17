package com.worldventures.dreamtrips.modules.feed.service.api;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction(value = "/api/social/timeline", method = HttpAction.Method.GET)
public class GetAccountTimelineHttpAction extends GetFeedHttpAction {

    public GetAccountTimelineHttpAction(int perPage, String before) {
        super(perPage, before);
    }
}
