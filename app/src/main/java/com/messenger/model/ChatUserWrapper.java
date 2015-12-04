package com.messenger.model;

import android.os.Parcel;

import com.messenger.messengerservers.entities.User;

public class ChatUserWrapper implements ChatUser {
    private final User user;

    public ChatUserWrapper(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getUserName();
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getAvatarUrl() {
        return null;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public void setOnline(boolean online) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
