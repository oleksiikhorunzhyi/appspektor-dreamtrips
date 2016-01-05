package com.messenger.messengerservers.entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.model.ChatUser;
import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@TableEndpoint(name = User.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = User.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class User extends BaseProviderModel<User> implements ChatUser {
    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_NAME = "userName";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @PrimaryKey
    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @Column String _id;
    @Column int socialId;
    @Column String userName;
    @Column boolean online;
    @Column String userAvatarUrl = "http://www.skivecore.com/members/0/Default.jpg";
    @Column Boolean friend;

    public User() {
    }

    public User(String userId) {
        this._id = userId;
        this.userName = userId;
    }

    public User(Parcel in) {
        this.userName = in.readString();
        this._id = this.userName;
        this.socialId = in.readInt();
        this.userAvatarUrl = in.readString();

        boolean[] booleans = new boolean[2];
        in.readBooleanArray(booleans);
        this.online = booleans[0];
        this.friend = booleans[1];
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String getId() {
        return _id;
    }

    public int getSocialId() {
        return socialId;
    }

    public void setSocialId(int socialId) {
        this.socialId = socialId;
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

    public boolean isFriend() {
        return friend != null ? friend : false;
    }

    public void setFriend(Boolean friend) {
        this.friend = friend;
    }

    public boolean isFriendSet() {
        return friend != null;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return !(_id != null ? !_id.equals(user._id) : user._id != null);
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
        dest.writeInt(this.socialId);
        dest.writeString(this.userAvatarUrl);
        dest.writeBooleanArray(new boolean[]{online, friend});
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

