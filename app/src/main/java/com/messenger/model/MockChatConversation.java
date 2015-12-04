package com.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MockChatConversation implements ChatConversation, Parcelable {

    private String conversationName;
    private ChatUser owner;
    private List<ChatUser> chatUsers;
    private List<ChatMessage> messages = new ArrayList<>();

    public MockChatConversation() {
    }

    @Override public String getConversationName() {
        return conversationName;
    }

    @Override public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    @Override public ChatUser getConversationOwner() {
        return owner;
    }

    @Override
    public List<ChatMessage> getMessages() {
        return messages;
    }

    @Override public void setConversationOwner(ChatUser owner) {
        this.owner = owner;
    }

    @Override public void setChatMessages(List<ChatMessage> chatMessages) {
        this.messages = chatMessages;
    }

    @Override public List<ChatUser> getChatUsers() {
        return chatUsers;
    }

    @Override public void setChatUsers(List<ChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationName);
        dest.writeParcelable(this.owner, 0);
        dest.writeList(this.messages);
        dest.writeList(this.chatUsers);
    }

    public MockChatConversation(Parcel in) {
        this.conversationName = in.readString();
        this.owner = in.readParcelable(MockChatUser.class.getClassLoader());
        this.messages = new ArrayList<>();
        in.readList(this.messages, MockChatMessage.class.getClassLoader());
        this.chatUsers = new ArrayList<>();
        in.readList(this.chatUsers, MockChatMessage.class.getClassLoader());
    }

    public static final Creator<MockChatConversation> CREATOR = new Creator<MockChatConversation>() {
        public MockChatConversation createFromParcel(Parcel source) {return new MockChatConversation(source);}

        public MockChatConversation[] newArray(int size) {return new MockChatConversation[size];}
    };
}
