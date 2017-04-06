package com.worldventures.dreamtrips.api.success_stories;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.success_stories.model.SuccessStory;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/success_stories")
public class GetSuccessStoriesHttpAction extends AuthorizedHttpAction {

    @Response
    List<SuccessStory> successStories;

    public List<SuccessStory> response() {
        return successStories;
    }
}
