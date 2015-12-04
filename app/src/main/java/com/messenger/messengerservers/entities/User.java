package com.messenger.messengerservers.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.model.ChatUser;

public class User implements ChatUser{

    private String userName;
    private String userAvatarUrl =  "http://www.skivecore.com/members/0/Default.jpg";
    private boolean online;

    public User(String userName) {
        this.userName = userName;
    }

    public User (Parcel parcel){

    }

    public String getUserName() {
        return userName;
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

    }


}
