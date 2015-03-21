package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.User;

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
