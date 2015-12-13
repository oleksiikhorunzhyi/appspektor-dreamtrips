package com.messenger.messengerservers.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.model.ChatUser;
import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

@ModelContainer
@Table(databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class User extends BaseModel implements ChatUser{
    @PrimaryKey
    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @Column
    String userName;
    @Column boolean online;
    private String userAvatarUrl =  "http://www.skivecore.com/members/0/Default.jpg";

    public User() {}
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

    public void setName(String name) {
        this.userName = name;
    }

    public String getAvatarUrl() {
        return userAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.userAvatarUrl = avatarUrl;
    }

    public boolean isOnline() {
        return online;
    }

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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userAvatarUrl);
        dest.writeInt(online ? 1 : 0);
    }


}

