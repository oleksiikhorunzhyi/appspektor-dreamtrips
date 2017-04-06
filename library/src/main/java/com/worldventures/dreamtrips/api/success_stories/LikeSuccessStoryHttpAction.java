package com.worldventures.dreamtrips.api.success_stories;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.success_stories.model.SuccessStory;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.FORM_URL_ENCODED;

@HttpAction(value = "/api/success_stories/{id}/like", method = POST, type = FORM_URL_ENCODED)
public class LikeSuccessStoryHttpAction extends AuthorizedHttpAction {

    @Path("id")
    public final int id;

    public LikeSuccessStoryHttpAction(int id) {
        this.id = id;
    }
}
