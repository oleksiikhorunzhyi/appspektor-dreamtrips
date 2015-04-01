package com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.File;

public class NavigationHeader {
    protected Uri userCover;
    protected Uri userPhoto;
    protected String userName;
    protected String userEmail;

    public NavigationHeader(User user) {
        userEmail = user.getEmail();
        userName = user.getUsername();
        userCover = Uri.fromFile(new File(user.getCoverPath()));
        userPhoto = Uri.parse(user.getAvatar().getMedium());
    }

    public Uri getUserCover() {
        return userCover;
    }

    public Uri getUserPhoto() {
        return userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
