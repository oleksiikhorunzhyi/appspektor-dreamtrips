package com.messenger.ui.model;

import android.os.Parcelable;

public interface ChatUser extends Parcelable {
    String getId();

    String getName();

    String getAvatarUrl();

    boolean isOnline();

    boolean isCloseFriend();
}
