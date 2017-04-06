package com.worldventures.dreamtrips.api.profile;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.profile.model.PrivateUserProfile;

import java.io.File;
import java.io.IOException;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;
import static io.techery.janet.http.annotations.HttpAction.Type.MULTIPART;

@HttpAction(value = "/api/profile/avatar", method = POST, type = MULTIPART)
public class UpdateProfileAvatarHttpAction extends AuthorizedHttpAction {

    @Part("avatar")
    final FileBody avatar;

    @Response
    PrivateUserProfile userProfile;

    public UpdateProfileAvatarHttpAction(File avatarFile) throws IOException {
        this.avatar = new FileBody("", avatarFile);
    }

    public PrivateUserProfile response() {
        return userProfile;
    }
}
