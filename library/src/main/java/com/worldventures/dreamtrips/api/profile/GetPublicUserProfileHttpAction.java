package com.worldventures.dreamtrips.api.profile;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.profile.model.PublicUserProfile;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/profiles/{id}")
public class GetPublicUserProfileHttpAction extends AuthorizedHttpAction {

    @Path("id")
    public final int id;

    @Response
    PublicUserProfile profile;

    public GetPublicUserProfileHttpAction(int id) {
        this.id = id;
    }

    public PublicUserProfile response() {
        return profile;
    }
}
