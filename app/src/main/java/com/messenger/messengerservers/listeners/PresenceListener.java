package com.messenger.messengerservers.listeners;


import com.messenger.messengerservers.entities.User;

public interface PresenceListener {

    void onUserPresenceChanged(User user);

}
