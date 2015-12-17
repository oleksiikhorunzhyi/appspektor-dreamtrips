package com.messenger.messengerservers.entities;

import android.net.Uri;
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
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@ModelContainer
@TableEndpoint(name = User.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = User.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class User extends BaseProviderModel<User> implements ChatUser {
    public static final String TABLE_NAME = "Users";

    public static final String COLUMN_USER_NAME = "userName";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @PrimaryKey
    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @Column
    String _id;
    @Column(name = COLUMN_USER_NAME)
    String userName;
    @Column
    boolean online;
    private String userAvatarUrl = "http://www.skivecore.com/members/0/Default.jpg";

    public User() {
    }

    public User(String userName) {
        this._id = userName;
        this.userName = userName;
    }

    public User(Parcel in) {
        this.userName = in.readString();
        this._id = this.userName;
        this.userAvatarUrl = in.readString();
        this.online = in.readInt() == 1;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String getId() {
        return _id;
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

    @Override
    public boolean equals(Object o) {
        User anotherUser = (User) o;
        return _id.equals(anotherUser._id);
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userAvatarUrl);
        dest.writeInt(online ? 1 : 0);
    }


    @Override
    public Uri getDeleteUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return CONTENT_URI;
    }
}

