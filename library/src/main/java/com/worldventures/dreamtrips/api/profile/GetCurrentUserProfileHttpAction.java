package com.worldventures.dreamtrips.api.profile;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.profile.model.PrivateUserProfile;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/profile")
public class GetCurrentUserProfileHttpAction extends AuthorizedHttpAction {

    @Response
    PrivateUserProfile profile;

    public PrivateUserProfile response() {
        return profile;
    }
}
