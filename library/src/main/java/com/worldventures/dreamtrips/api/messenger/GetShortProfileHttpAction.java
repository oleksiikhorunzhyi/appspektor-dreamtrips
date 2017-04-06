package com.worldventures.dreamtrips.api.messenger;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.request.GetShortUserProfileBody;
import com.worldventures.dreamtrips.api.messenger.model.request.ImmutableGetShortUserProfileBody;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.SIMPLE;

@HttpAction(value = "/api/users/profiles/short", method = POST, type = SIMPLE)
public class GetShortProfileHttpAction extends AuthorizedHttpAction {

    @Body
    public final GetShortUserProfileBody body;

    @Response
    List<ShortUserProfile> shortUsers;

    public GetShortProfileHttpAction(List<String> usernames) {
        this.body = ImmutableGetShortUserProfileBody.of(usernames);
    }

    public List<ShortUserProfile> getShortUsers() {
        return shortUsers;
    }

}
