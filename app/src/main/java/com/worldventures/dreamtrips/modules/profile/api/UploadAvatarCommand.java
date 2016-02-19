package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.common.model.User;

import retrofit.mime.TypedFile;

public class UploadAvatarCommand extends Command<User> {
    private retrofit.mime.TypedFile type;

    public UploadAvatarCommand(TypedFile type) {
        super(User.class);
        this.type = type;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        return getService().uploadAvatar(type);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_update_avatar;
    }
}
