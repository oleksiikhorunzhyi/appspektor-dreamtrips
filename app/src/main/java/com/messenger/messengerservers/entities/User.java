package com.messenger.messengerservers.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.model.ChatUser;

public class User implements ChatUser {
    private String userName;
    private String userAvatarUrl =  "http://www.skivecore.com/members/0/Default.jpg";
    private boolean online;

    public User(String userName) {
        this.userName = userName;
    }

    public User (Parcel in){
        this.userName = in.readString();
        this.userAvatarUrl = in.readString();
        this.online = in.readInt() == 1;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public void setId(long id) {

    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public String getAvatarUrl() {
        return userAvatarUrl;
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
        this.online = online;
    }

    @Override
    public boolean isCloseFriend() {
        return false;
    }

    @Override
    public void setCloseFriend(boolean isCloseFriend) {

    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {return new User(source);}

        public User[] newArray(int size) {return new User[size];}
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userAvatarUrl);
        dest.writeInt(online ? 1 : 0);
    }


}
