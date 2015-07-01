package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.common.model.User;

import retrofit.mime.TypedFile;

public class UploadCoverCommand extends Command<User> {
    private TypedFile type;

    public UploadCoverCommand(TypedFile type) {
        super(User.class);
        this.type = type;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        return getService().uploadBackgroundPhoto(type);
    }
}
