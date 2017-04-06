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

@HttpAction(value = "/api/profile/background_photo", method = POST, type = MULTIPART)
public class UpdateProfileBackgroundPhotoHttpAction extends AuthorizedHttpAction {

    @Part("background_photo")
    final FileBody backgroundPhoto;

    @Response
    PrivateUserProfile userProfile;

    public UpdateProfileBackgroundPhotoHttpAction(File backgroundPhotoFile) throws IOException {
        this.backgroundPhoto = new FileBody("", backgroundPhotoFile);
    }

    public PrivateUserProfile response() {
        return userProfile;
    }
}
