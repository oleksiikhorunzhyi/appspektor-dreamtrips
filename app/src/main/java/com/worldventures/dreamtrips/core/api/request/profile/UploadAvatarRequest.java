package com.worldventures.dreamtrips.core.api.request.profile;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.User;

import retrofit.mime.TypedFile;

public class UploadAvatarRequest extends DreamTripsRequest<User> {
    private retrofit.mime.TypedFile type;

    public UploadAvatarRequest(TypedFile type) {
        super(User.class);
        this.type = type;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        return getService().uploadAvatar(type);
    }
}
