package com.messenger.model;

import android.os.Parcel;

import com.messenger.messengerservers.entities.Message;

import java.util.Date;

public class ChatMessageWrapper implements ChatMessage {
    private final Message message;

    public ChatMessageWrapper(Message message) {
        this.message = message;
    }

    @Override
    public CharSequence getMessage() {
        return message.getText();
    }

    @Override
    public void setMessage(String message) {
    }

    @Override
    public ChatUser getUser() {
        return new ChatUserWrapper(message.getFrom());
    }

    @Override
    public void setUser(ChatUser chatUser) {

    }

    @Override
    public Date getDate() {
        return message.getDate();
    }

    @Override
    public void setDate(Date date) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
