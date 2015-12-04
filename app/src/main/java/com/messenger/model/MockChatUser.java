package com.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MockChatUser implements ChatUser {

    private String name;
    private String avatarUrl;
    private boolean isOnline;

    public MockChatUser(String name, String avatarUrl) {
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

    ///////////////////////////////////////////////////////////////////////////
    // Object
    ///////////////////////////////////////////////////////////////////////////

    @Override public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof MockChatUser)) return false;
        MockChatUser user = (MockChatUser) o;
        return user.name.equals(name);
    }

    @Override public int hashCode() {
        return name.hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.avatarUrl);
        dest.writeInt(isOnline ? 1 : 0);
    }

    private MockChatUser(Parcel in) {
        this.name = in.readString();
        this.avatarUrl = in.readString();
        this.isOnline = in.readInt() == 1 ? true : false;
    }

    public static final Parcelable.Creator<MockChatUser> CREATOR = new Parcelable.Creator<MockChatUser>() {
        public MockChatUser createFromParcel(Parcel source) {return new MockChatUser(source);}

        public MockChatUser[] newArray(int size) {return new MockChatUser[size];}
    };
}
