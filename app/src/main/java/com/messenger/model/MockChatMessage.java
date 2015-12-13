package com.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MockChatMessage implements ChatMessage {

    private String message;
    private ChatUser user;
    private Date date;
    private boolean isUnread;

    public MockChatMessage() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

    @Override public String getMessage() {
        return message;
    }

    @Override public void setMessage(String message) {
        this.message = message;
    }

    @Override public ChatUser getUser() {
        return user;
    }

    @Override public void setUser(ChatUser user) {
        this.user = user;
    }

    @Override public Date getDate() {
        return date;
    }

    @Override public void setDate(Date date) {
        this.date = date;
    }

    @Override public boolean isUnread() {
        return isUnread;
    }

    @Override public void setUnread(boolean isUnread) {
        this.isUnread = isUnread;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeParcelable(this.user, 0);
        dest.writeLong(date != null ? date.getTime() : -1);
        dest.writeInt(isUnread ? 1 : 0);
    }

    protected MockChatMessage(Parcel in) {
        this.message = in.readString();
        this.user = in.readParcelable(ChatUser.class.getClassLoader());
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.isUnread = in.readInt() == 1 ? true : false;
    }

    public static final Parcelable.Creator<MockChatMessage> CREATOR = new Parcelable.Creator<MockChatMessage>() {
        public MockChatMessage createFromParcel(Parcel source) {return new MockChatMessage(source);}

        public MockChatMessage[] newArray(int size) {return new MockChatMessage[size];}
    };
}
