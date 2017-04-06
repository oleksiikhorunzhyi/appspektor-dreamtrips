package com.worldventures.dreamtrips.api.profile;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.profile.model.UserAvatar;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/profile/{username}/avatar")
public class GetUserAvatarHttpAction extends AuthorizedHttpAction {

    @Path("username")
    public final String username;

    @Response
    UserAvatar avatar;

    public GetUserAvatarHttpAction(String username) {
        this.username = username;
    }

    public UserAvatar response() {
        return avatar;
    }
}
