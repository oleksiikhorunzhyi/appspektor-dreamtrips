package com.messenger.model;

import android.os.Parcelable;

import java.util.Date;

public interface ChatMessage extends Parcelable {
    CharSequence getMessage();
    void setMessage(String message);
    ChatUser getUser();
    void setUser(ChatUser chatUser);
    Date getDate();
    void setDate(Date date);
}
