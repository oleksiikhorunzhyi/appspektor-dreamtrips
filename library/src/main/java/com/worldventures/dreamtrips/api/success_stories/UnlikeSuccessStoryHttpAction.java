package com.worldventures.dreamtrips.api.success_stories;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.success_stories.model.SuccessStory;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

@HttpAction(value = "/api/success_stories/{id}/like", method = HttpAction.Method.DELETE)
public class UnlikeSuccessStoryHttpAction extends AuthorizedHttpAction {

    @Path("id")
    public final int id;

    public UnlikeSuccessStoryHttpAction(int id) {
        this.id = id;
    }
}