package com.messenger.model;

import android.os.Parcel;

import java.util.List;

public class ProdChatConversation implements ChatConversation{

    String name;
    List<ChatUser> users;

    public ProdChatConversation(String name, List<ChatUser> users) {
        this.name = name;
        this.users = users;
    }

    public ProdChatConversation(Parcel parcel) {
        name = parcel.readString();
        users = parcel.readList();
    }

    @Override
    public ChatUser getConversationOwner() {
        return null;
    }

    @Override
    public void setConversationOwner(ChatUser chatUser) {

    }

    @Override
    public List<ChatMessage> getMessages() {
        return null;
    }

    @Override
    public void setChatMessages(List<ChatMessage> chatMessages) {

    }

    @Override
    public List<ChatUser> getChatUsers() {
        return null;
    }

    @Override
    public void setChatUsers(List<ChatUser> chatUsers) {

    }

    @Override
    public String getConversationName() {
        return null;
    }

    @Override
    public void setConversationName(String conversationName) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(users);
    }
}
