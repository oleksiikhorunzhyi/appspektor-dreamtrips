package com.messenger.model;

import android.os.Parcelable;

import java.util.List;

public interface ChatContacts extends Parcelable {
    List<ChatUser> getUsers();
}
