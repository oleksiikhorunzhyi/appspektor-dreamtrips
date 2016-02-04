package com.messenger.entities;

import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;

import com.messenger.model.ChatUser;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@TableEndpoint(name = DataUser.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = DataUser.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class DataUser extends BaseProviderModel<DataUser> implements ChatUser {
    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_NAME = "userName";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @PrimaryKey
    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @Column(name = BaseColumns._ID) String id;
    @Column int socialId;
    @Column String userName;
    @Column boolean online;
    @Column String userAvatarUrl = "http://www.skivecore.com/members/0/Default.jpg";
    @Column Boolean friend;

    public DataUser() {
    }

    public DataUser(com.messenger.messengerservers.model.User user) {
        this(user.getName());
        setOnline(user.isOnline());
    }

    public DataUser(String userId) {
        this.id = userId;
        this.userName = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
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

        DataUser user = (DataUser) o;

        return !(id != null ? !id.equals(user.id) : user.id != null);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + id + '\'' +
                '}';
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.socialId);
        dest.writeString(this.userName);
        dest.writeByte(online ? (byte) 1 : (byte) 0);
        dest.writeString(this.userAvatarUrl);
        dest.writeValue(this.friend);
    }

    protected DataUser(Parcel in) {
        this.id = in.readString();
        this.socialId = in.readInt();
        this.userName = in.readString();
        this.online = in.readByte() != 0;
        this.userAvatarUrl = in.readString();
        this.friend = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<DataUser> CREATOR = new Creator<DataUser>() {
        public DataUser createFromParcel(Parcel source) {
            return new DataUser(source);
        }

        public DataUser[] newArray(int size) {
            return new DataUser[size];
        }
    };
}

