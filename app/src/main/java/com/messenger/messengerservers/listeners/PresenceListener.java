package com.messenger.messengerservers.listeners;

public interface PresenceListener {

   void onUserPresenceChanged(String userId, boolean isOnline);

}
