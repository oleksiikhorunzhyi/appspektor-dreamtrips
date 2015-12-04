package com.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class  MockChatContacts implements ChatContacts, Parcelable {

    private List<ChatUser> users = new ArrayList<>();

    public MockChatContacts(List<ChatUser> users) {
        this.users = users;
    }

    @Override
    public List<ChatUser> getUsers() {
        return users;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeList(this.users);}

    public MockChatContacts(Parcel in) {
        this.users = new ArrayList<>();
        in.readList(this.users, MockChatUser.class.getClassLoader());
    }

    public static final Creator<MockChatContacts> CREATOR = new Creator<MockChatContacts>() {
        public MockChatContacts createFromParcel(Parcel source) {return new MockChatContacts(source);}

        public MockChatContacts[] newArray(int size) {return new MockChatContacts[size];}
    };
}
