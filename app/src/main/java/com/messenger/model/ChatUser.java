package com.messenger.model;

import android.os.Parcelable;

public interface ChatUser extends Parcelable {
    String getId();

    String getName();

    void setName(String name);

    String getAvatarUrl();

    void setAvatarUrl(String avatarUrl);

    boolean isOnline();

    void setOnline(boolean online);

    boolean isCloseFriend();

    void setCloseFriend(boolean isCloseFriend);
}
