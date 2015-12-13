package com.messenger.model;

import android.os.Parcel;

public class MockChatUser implements ChatUser {

    private long id;
    private String name;
    private String avatarUrl;
    private boolean isOnline;
    private boolean isCloseFriend;

    public MockChatUser(long id, String name, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Override public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override public boolean isOnline() {
        return isOnline;
    }

    @Override public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    @Override public boolean isCloseFriend() {
        return isCloseFriend;
    }

    @Override public void setCloseFriend(boolean isCloseFriend) {
        this.isCloseFriend = isCloseFriend;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Object
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof MockChatUser)) return false;
        MockChatUser user = (MockChatUser) o;
        return user.id == id;
    }

    @Override public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.avatarUrl);
        dest.writeInt(isOnline ? 1 : 0);
        dest.writeInt(isCloseFriend ? 1 : 0);
    }

    private MockChatUser(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.avatarUrl = in.readString();
        this.isOnline = in.readInt() == 1 ? true : false;
        this.isCloseFriend = in.readInt() == 1 ? true : false;
    }

    public static final Creator<MockChatUser> CREATOR = new Creator<MockChatUser>() {
        public MockChatUser createFromParcel(Parcel source) {return new MockChatUser(source);}

        public MockChatUser[] newArray(int size) {return new MockChatUser[size];}
    };
}
