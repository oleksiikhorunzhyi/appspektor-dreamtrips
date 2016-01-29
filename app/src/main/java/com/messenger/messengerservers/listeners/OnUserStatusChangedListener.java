package com.messenger.messengerservers.listeners;

public interface OnUserStatusChangedListener {

    void onUserStatusChanged(String userId, boolean online);
}
